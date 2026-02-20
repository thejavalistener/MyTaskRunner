package thejavalistener.mtr.actions;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class Unzip extends MyAction
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
        return "Unzipping";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{from ,"to "+ to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path zipPath = Paths.get(from);
        Path destDir = Paths.get(to);

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

                        if (p != null && totalBytes > 0)
                        {
                            int pct = (int)Math.min(100, (current * 100) / totalBytes);

                            if (pct != lastPct)
                            {
                                p.setPercent(pct,"");
                                lastPct = pct;
                            }
                        }
                    }
                }
            }
        }

        if (p != null) p.setPercent(100,"");
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(from == null || from.isBlank())
        {
            return "'from' es obligatorio";
        }

        if(to == null || to.isBlank())
        {
            return "'to' es obligatorio";
        }

        Path zipPath;
        Path destDir;

        try
        {
            zipPath = Paths.get(from);
        }
        catch(Exception e)
        {
            return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
        }

        try
        {
            destDir = Paths.get(to);
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        if(!ctx.exists(zipPath))
        {
            return "no existe el archivo (según script): " + from;
        }

        if(!ctx.isFile(zipPath))
        {
            return "el origen no es un archivo: " + from;
        }

        if(ctx.exists(destDir) && !ctx.isDirectory(destDir))
        {
            return "el destino existe y no es un directorio: " + to;
        }

        ctx.addDirectory(destDir);

        return null;
    }

}
