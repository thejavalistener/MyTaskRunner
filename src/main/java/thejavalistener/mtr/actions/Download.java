package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;

public class Download extends MyAction
{
    private final String url;
    private final Path dest;

    public Download(String url, String destFile)
    {
        this.url = url;
        this.dest = Paths.get(destFile);
    }

    @Override
    public String getVerb()
    {
        return "Downloading";
    }

    @Override
    public String getDescription()
    {
        return url + " to " + dest;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (dest.getParent() != null)
            Files.createDirectories(dest.getParent());

        if (pl != null) pl.onStart();

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

                if (pl != null)
                {
                    int pct;
                    if (total > 0)
                        pct = (int)Math.min(100, (done * 100) / total);
                    else
                        pct = 0; // unknown size

                    if (pct != lastPct)
                    {
                        pl.onProgress(pct);
                        lastPct = pct;
                    }
                }
            }
        }

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
