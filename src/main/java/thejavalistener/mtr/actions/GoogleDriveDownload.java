package thejavalistener.mtr.actions;

import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class GoogleDriveDownload extends MyAction
{
    private String from;
    private String to;

    // si tu loader instancia por reflexión y luego setea campos, dejá también constructor vacío
    public GoogleDriveDownload()
    {
        super();
    }

    @Override
    public String getVerb()
    {
        return "Downloading";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{ from, "to " + to };
    }

    @Override
    public String validate(ValidationContext ctx)
    {
        if (from == null || from.isBlank())
            return "'url' es obligatorio";

        if (to == null || to.isBlank())
            return "'to' es obligatorio";

        if (!from.contains("drive.google.com"))
            return "no es un link válido de Google Drive";

        String fileId = extractFileId(from);
        if (fileId == null || fileId.isBlank())
            return "no se pudo extraer el fileId del link";

        try
        {
            Paths.get(to);
        }
        catch (Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        // si querés: validar que el parent exista o sea creable (yo prefiero hacerlo en ejecución)
        return null;
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        String fileId = extractFileId(from);
        if (fileId == null)
            throw new RuntimeException("No se pudo extraer el fileId del link");

        Path dest = Paths.get(to);
        if (dest.getParent() != null)
            Files.createDirectories(dest.getParent());

        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        String baseUrl = "https://drive.google.com/uc?export=download&id=" + fileId;

        // 1) primer request
        HttpRequest r1 = HttpRequest.newBuilder(URI.create(baseUrl))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        HttpResponse<byte[]> resp1 = client.send(r1, HttpResponse.BodyHandlers.ofByteArray());

        String ctype1 = resp1.headers().firstValue("content-type").orElse("").toLowerCase();

        // Si no es HTML, ya era el archivo
        if (!ctype1.contains("text/html"))
        {
            writeBytesWithProgress(resp1.body(), dest, p);
            return;
        }

        // 2) si es HTML, extraer confirm token y hacer segundo request
        String html = new String(resp1.body(), StandardCharsets.UTF_8);

        Optional<String> url2opt = extractDownloadUrl(html);

        if (url2opt.isEmpty())
        {
        	Path dump = Paths.get(to + ".html");
        	System.out.println("DUMP => " + dump.toAbsolutePath());

        	try
        	{
        	    Files.createDirectories(dump.getParent());
        	    Files.writeString(dump, html, StandardCharsets.UTF_8,
        	            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        	    System.out.println("DUMP WRITTEN? " + Files.exists(dump) + " size=" + Files.size(dump));
        	}
        	catch(Exception ex)
        	{
        	    ex.printStackTrace();
        	}

        	throw new RuntimeException("Drive devolvió HTML sin URL de descarga. Dump: " + dump.toAbsolutePath());

        //    throw new RuntimeException("Drive devolvió HTML pero no encontré URL/form de descarga (¿no público o requiere login?)");
        }

        String url2 = url2opt.get();
        HttpRequest r2 = HttpRequest.newBuilder(URI.create(url2))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        HttpResponse<InputStream> resp2 = client.send(r2, HttpResponse.BodyHandlers.ofInputStream());
        String ctype2 = resp2.headers().firstValue("content-type").orElse("").toLowerCase();

        if (ctype2.contains("text/html"))
            throw new RuntimeException("El segundo request devolvió HTML (no el archivo). Probablemente no está accesible.");

        long len = resp2.headers().firstValue("content-length").map(Long::parseLong).orElse(-1L);
        copyStreamWithProgress(resp2.body(), dest, len, p);
    }

    private static String extractFileId(String url)
    {
        // formato: https://drive.google.com/file/d/<ID>/view?...
        Matcher m = Pattern.compile("/file/d/([a-zA-Z0-9_-]+)").matcher(url);
        if (m.find()) return m.group(1);

        // formato: ...?id=<ID>
        Matcher m2 = Pattern.compile("[?&]id=([a-zA-Z0-9_-]+)").matcher(url);
        if (m2.find()) return m2.group(1);

        return null;
    }

    private static Optional<String> extractConfirmToken(String html)
    {
        // patrón común
        Matcher m = Pattern.compile("confirm=([0-9A-Za-z_\\-]+)").matcher(html);
        if (m.find()) return Optional.of(m.group(1));

        // a veces viene url-encoded
        Matcher m2 = Pattern.compile("confirm%3D([0-9A-Za-z_\\-]+)").matcher(html);
        if (m2.find()) return Optional.of(m2.group(1));

        return Optional.empty();
    }

    private static void writeBytesWithProgress(byte[] data, Path dest, Progress p) throws Exception
    {
        if (p != null)
        {
            // no tenemos "len real" de descarga por chunks en este caso, pero al menos marcamos 100%
            // si querés progreso real, hacelo siempre por stream (más abajo)
            Files.write(dest, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            p.setPercent(100, "");
        }
        else
        {
            Files.write(dest, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static void copyStreamWithProgress(InputStream in, Path dest, long contentLength, Progress p) throws Exception
    {
        // Descarga en stream con progreso (si content-length está disponible)
        try (InputStream src = in)
        {
            if (p == null || contentLength <= 0)
            {
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                return;
            }

            // progreso basado en bytes
            final int bufSize = 64 * 1024;
            byte[] buf = new byte[bufSize];

            long total = contentLength;
            long read = 0;
            int lastPct = -1;

            try (var out = Files.newOutputStream(dest, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
            {
                int n;
                while ((n = src.read(buf)) >= 0)
                {
                    out.write(buf, 0, n);
                    read += n;

                    int pct = (int)((read * 100L) / total);
                    if (pct != lastPct)
                    {
                        lastPct = pct;
                        p.setPercent(pct, "");
                    }
                }
            }

            p.setPercent(100, "");
        }
    }
    
    private static Optional<String> extractDownloadUrl(String html)
    {
        if (html == null) return Optional.empty();

        // 1) Capturar action del form
        Matcher action = Pattern.compile(
            "<form[^>]*action=\"([^\"]+)\"[^>]*>").matcher(html);

        if (!action.find()) return Optional.empty();

        String base = action.group(1);

        // 2) Capturar todos los inputs hidden
        Matcher inputs = Pattern.compile(
            "<input[^>]*name=\"([^\"]+)\"[^>]*value=\"([^\"]*)\"").matcher(html);

        StringBuilder qs = new StringBuilder();
        boolean first = true;

        while (inputs.find())
        {
            if (!first) qs.append("&");
            first = false;

            qs.append(java.net.URLEncoder.encode(inputs.group(1),
                    java.nio.charset.StandardCharsets.UTF_8));
            qs.append("=");
            qs.append(java.net.URLEncoder.encode(inputs.group(2),
                    java.nio.charset.StandardCharsets.UTF_8));
        }

        if (qs.length() == 0) return Optional.empty();

        return Optional.of(base + "?" + qs.toString());
    }

	public void setFrom(String from)
	{
		this.from=from;
	}

	public void setTo(String to)
	{
		this.to=to;
	}
}