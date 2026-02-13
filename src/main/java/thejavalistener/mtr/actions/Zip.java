package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Stream;

public class Zip extends MyAction
{
    private String from;
    private String to;

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    @Override
    public String getVerb()
    {
        return "Zipping";
    }

    @Override
    public String getDescription()
    {
        return from + " to " + to;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path sourcePath = Paths.get(from);
        Path zipPath = Paths.get(to);

        if (pl != null) pl.onStart();

        long totalBytes;
        try (Stream<Path> s = Files.walk(sourcePath))
        {
            totalBytes = s
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try { return Files.size(p); }
                        catch (Exception e) { return 0L; }
                    })
                    .sum();
        }

        AtomicLong processed = new AtomicLong(0);
        int[] lastPct = { -1 };

        if (zipPath.getParent() != null)
            Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(
                Files.newOutputStream(zipPath,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)))
        {
            try (Stream<Path> paths = Files.walk(sourcePath))
            {
                paths.forEach(path -> {
                    try
                    {
                        Path rel = sourcePath.relativize(path);

                        if (Files.isDirectory(path))
                        {
                            if (!rel.toString().isEmpty())
                            {
                                zos.putNextEntry(new ZipEntry(rel.toString() + "/"));
                                zos.closeEntry();
                            }
                            return;
                        }

                        zos.putNextEntry(new ZipEntry(rel.toString()));

                        try (InputStream in = Files.newInputStream(path))
                        {
                            byte[] buffer = new byte[64 * 1024];
                            int n;

                            while ((n = in.read(buffer)) >= 0)
                            {
                                if (n == 0) continue;

                                zos.write(buffer, 0, n);

                                long current = processed.addAndGet(n);

                                if (pl != null && totalBytes > 0)
                                {
                                    int pct = (int)Math.min(100, (current * 100) / totalBytes);

                                    if (pct != lastPct[0])
                                    {
                                        pl.onProgress(pct);
                                        lastPct[0] = pct;
                                    }
                                }
                            }
                        }

                        zos.closeEntry();
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
