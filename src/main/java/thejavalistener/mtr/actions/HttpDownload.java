package thejavalistener.mtr.actions;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.doc.DocAction;

public class HttpDownload extends MyAction
{
    private String from;
    private String to;

    public HttpDownload()
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
        return new String[]{from, "to " + to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        Path dest = Paths.get(to);

        if(dest.getParent() != null)
        {
            Files.createDirectories(dest.getParent());
        }

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest req = HttpRequest.newBuilder(URI.create(from))
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());

        if(resp.statusCode() < 200 || resp.statusCode() >= 300)
        {
            throw new RuntimeException("HTTP error: " + resp.statusCode());
        }

        long len = resp.headers().firstValue("content-length").map(Long::parseLong).orElse(-1L);

        try(InputStream in = resp.body())
        {
            if(p == null || len <= 0)
            {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                if(p != null) p.setPercent(100, "");
                return;
            }

            byte[] buf = new byte[64 * 1024];
            long read = 0;
            int lastPct = -1;

            try(var out = Files.newOutputStream(dest))
            {
                int n;
                while((n = in.read(buf)) >= 0)
                {
                    if(n == 0) continue;

                    out.write(buf, 0, n);
                    read += n;

                    int pct = (int)((read * 100L) / len);
                    if(pct != lastPct)
                    {
                        lastPct = pct;
                        p.setPercent(pct, "");
                    }
                }
            }

            p.setPercent(100, "");
        }
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

	@Override
	public DocAction getActionDoc()
	{
		return null;
	}
}