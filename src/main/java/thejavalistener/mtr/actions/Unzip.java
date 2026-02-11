package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip extends MyAction
{
    private final Path zipPath;
    private final Path destDir;

    public Unzip(String zipFilePath, String destDir)
    {
        this.zipPath = Paths.get(zipFilePath);
        this.destDir = Paths.get(destDir);
    }

    @Override
    public String getVerb()
    {
        return "Unzipping";
    }

    @Override
    public String getDescription()
    {
        return zipPath + " to " + destDir;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (pl != null) pl.onStart();

        Files.createDirectories(destDir);

        try (ZipFile zipFile = new ZipFile(zipPath.toFile()))
        {
            long totalBytes = zipFile.stream()
                    .filter(e -> !e.isDirectory())
                    .mapToLong(ZipEntry::getSize)
                    .filter(size -> size > 0)
                    .sum();

            AtomicLong extracted = new AtomicLong(0);
            int lastPct = -1;

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                Path outPath = destDir.resolve(entry.getName());

                if (entry.isDirectory())
                {
                    Files.createDirectories(outPath);
                    continue;
                }

                if (outPath.getParent() != null)
                    Files.createDirectories(outPath.getParent());

                try (InputStream in = zipFile.getInputStream(entry);
                     OutputStream out = Files.newOutputStream(outPath,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE))
                {
                    byte[] buffer = new byte[64 * 1024];
                    int n;

                    while ((n = in.read(buffer)) >= 0)
                    {
                        if (n == 0) continue;

                        out.write(buffer, 0, n);

                        long current = extracted.addAndGet(n);

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
        }

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
