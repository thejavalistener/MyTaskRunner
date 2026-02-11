package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.zip.*;

public class Unzip extends MyAction
{
    private final Path zip;
    private final Path dest;
    private final ProgressListener progress;

    public Unzip(String zipPath, String destDir)
    {
        this(zipPath, destDir, null);
    }

    public Unzip(String zipPath, String destDir, ProgressListener progress)
    {
        this.zip = Paths.get(zipPath);
        this.dest = Paths.get(destDir);
        this.progress = progress;
    }

    @Override
    public int run()
    {
        try
        {
            if (!Files.exists(zip)) return IO_ERROR;
            Files.createDirectories(dest);

            long total = computeTotalUncompressedSize(zip); // puede ser -1
            long done = 0;

            try (InputStream fis = Files.newInputStream(zip);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 ZipInputStream zis = new ZipInputStream(bis))
            {
                ZipEntry e;
                byte[] buf = new byte[64 * 1024];

                while ((e = zis.getNextEntry()) != null)
                {
                    Path outPath = safeResolve(dest, e.getName());

                    if (e.isDirectory())
                    {
                        Files.createDirectories(outPath);
                        zis.closeEntry();
                        continue;
                    }

                    if (outPath.getParent() != null)
                        Files.createDirectories(outPath.getParent());

                    try (OutputStream os = new BufferedOutputStream(
                            Files.newOutputStream(outPath,
                                    StandardOpenOption.CREATE,
                                    StandardOpenOption.TRUNCATE_EXISTING,
                                    StandardOpenOption.WRITE)))
                    {
                        int n;
                        while ((n = zis.read(buf)) > 0)
                        {
                            os.write(buf, 0, n);
                            done += n;
                            if (progress != null) progress.onProgress(done, total);
                        }
                    }

                    zis.closeEntry();
                }
            }

            if (progress != null) progress.onProgress(total < 0 ? done : total, total);
            return MyAction.SUCCESS;
        }
        catch (Exception e)
        {
            return IO_ERROR;
        }
    }

    private static long computeTotalUncompressedSize(Path zipPath)
    {
        try (ZipFile zf = new ZipFile(zipPath.toFile()))
        {
            long total = 0;
            Enumeration<? extends ZipEntry> en = zf.entries();
            while (en.hasMoreElements())
            {
                ZipEntry e = en.nextElement();
                if (e.isDirectory()) continue;
                long s = e.getSize();
                if (s < 0) return -1;
                total += s;
            }
            return total;
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    private static Path safeResolve(Path destDir, String entryName) throws IOException
    {
        Path resolved = destDir.resolve(entryName).normalize();
        Path destNorm = destDir.toAbsolutePath().normalize();
        Path resNorm  = resolved.toAbsolutePath().normalize();

        if (!resNorm.startsWith(destNorm))
            throw new IOException("Zip Slip detectado: " + entryName);

        return resolved;
    }
}
