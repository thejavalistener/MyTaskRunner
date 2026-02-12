package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Copy extends MyAction
{
    private String from;
    private String to;

    @Override
    public String getVerb()
    {
        return "Copying";
    }

    @Override
    public String getDescription()
    {
        return from + " to " + to;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
    	Path pFrom = Paths.get(from);
    	Path pTo = Paths.get(to);
    	
        if (!Files.exists(pFrom))
            throw new java.io.IOException("Source does not exist: " + from);

        if (pl != null) pl.onStart();

        if (Files.isDirectory(pFrom))
        {
            long totalBytes = calculateTotalBytes(pFrom);
            AtomicLong copiedBytes = new AtomicLong(0);

            try (Stream<Path> stream = Files.walk(pFrom))
            {
                stream.forEach(path ->
                {
                    try
                    {
                        Path dest = pTo.resolve(pFrom.relativize(path));

                        if (Files.isDirectory(path))
                        {
                            Files.createDirectories(dest);
                        }
                        else
                        {
                            if (dest.getParent() != null)
                                Files.createDirectories(dest.getParent());

                            copyFileWithProgress(path, dest, totalBytes, copiedBytes, pl);
                        }
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        else
        {
            long totalBytes = Files.size(pFrom);
            AtomicLong copiedBytes = new AtomicLong(0);

            if (pTo.getParent() != null)
                Files.createDirectories(pTo.getParent());

            copyFileWithProgress(pFrom, pTo, totalBytes, copiedBytes, pl);
        }

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }

    private void copyFileWithProgress(Path source,
                                      Path dest,
                                      long totalBytes,
                                      AtomicLong copiedBytes,
                                      ProgressListener pl) throws Exception
    {
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(dest,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE))
        {
            byte[] buffer = new byte[64 * 1024];
            int n;
            int lastPct = -1;

            while ((n = in.read(buffer)) >= 0)
            {
                if (n == 0) continue;

                out.write(buffer, 0, n);

                long current = copiedBytes.addAndGet(n);

                if (pl != null && totalBytes > 0)
                {
                    int pct = (int)Math.min(100, (current * 100) / totalBytes);

                    if (pct != lastPct)
                    {
                        pl.onProgress(pct);
                        lastPct = pct;
                    }
                }
            }
        }
    }

    private long calculateTotalBytes(Path source) throws Exception
    {
        try (Stream<Path> stream = Files.walk(source))
        {
            return stream
                    .filter(Files::isRegularFile)
                    .mapToLong(path ->
                    {
                        try
                        {
                            return Files.size(path);
                        }
                        catch (Exception e)
                        {
                            return 0L;
                        }
                    })
                    .sum();
        }
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
