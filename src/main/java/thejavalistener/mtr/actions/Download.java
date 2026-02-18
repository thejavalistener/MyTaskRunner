package thejavalistener.mtr.actions;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class Download extends MyAction
{
    private String url;
    private String to;   // antes era Path dest

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    @Override
    public String getVerb()
    {
        return "Downloading";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{url, "to " + to};
    }

    @Override
    public void execute(Progress pl) throws Exception
    {
        Path dest = Paths.get(to);

        if (dest.getParent() != null)
            Files.createDirectories(dest.getParent());


        
        
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response =
                client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int sc = response.statusCode();
        if (sc < 200 || sc >= 300)
            throw new RuntimeException("HTTP status: " + sc);

        long total = response.headers()
                .firstValue("Content-Length")
                .map(Long::parseLong)
                .orElse(-1L);

        if( pl!=null ) pl.begin();

        try (InputStream in = response.body();
             OutputStream out = Files.newOutputStream(dest,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE))
        {
            byte[] buf = new byte[64 * 1024];
            long done = 0;
            int lastPct = -1;

            int n;
            while ((n = in.read(buf)) >= 0)
            {
                if (n == 0) continue;

                out.write(buf, 0, n);
                done += n;

                if (pl != null && total > 0)
                {
                    int pct = (int)Math.min(100, (done * 100) / total);

                    if (pct != lastPct)
                    {
                    	pl.setPercent(pct,"");
                        lastPct = pct;
                    }
                }
            }
        }

//        pl.setPercent(100,"");
//        if (pl != null) pl.onProgress(100);
//        if (pl != null) pl.onFinish();
        
        System.out.println(".,.,");
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(url == null || url.isBlank())
        {
            return "'url' es obligatorio";
        }

        if(to == null || to.isBlank())
        {
            return "'to' es obligatorio";
        }

        try
        {
            URI.create(url);
        }
        catch(Exception e)
        {
            return "url inválida: " + url + " (" + e.getMessage() + ")";
        }

        Path dest;

        try
        {
            dest = Paths.get(to);
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        if(ctx.exists(dest) && ctx.isDirectory(dest))
        {
            return "el destino existe y es un directorio: " + to;
        }

        ctx.addFile(dest);

        return null;
    }

}
