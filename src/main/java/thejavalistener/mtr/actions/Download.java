package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;

import java.io.InputStream;
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
    public int run()
    {
        try
        {
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
                return NETWORK_ERROR;

            try (InputStream in = response.body())
            {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }

            return SUCCESS;
        }
        catch (Exception e)
        {
            return NETWORK_ERROR;
        }
    }
}
