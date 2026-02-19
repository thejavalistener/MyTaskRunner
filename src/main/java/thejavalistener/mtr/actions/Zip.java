package thejavalistener.mtr.actions;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

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
    public String[] getDescription()
    {
    	return new String[]{from , "to " + to};
    }

    @Override
    public void execute(Progress pl) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path sourcePath = Paths.get(from);
        Path zipPath = Paths.get(to);

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
                                        pl.setPercent(pct,"");
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

        if (pl != null) pl.setPercent(100,"");
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

        Path sourcePath;
        Path zipPath;

        try
        {
            sourcePath = Paths.get(from);
        }
        catch(Exception e)
        {
            return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
        }

        try
        {
            zipPath = Paths.get(to);
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        if(!ctx.exists(sourcePath))
        {
            return "no existe el origen (según script): " + from;
        }

        if(!ctx.isDirectory(sourcePath))
        {
            return "el origen no es un directorio: " + from;
        }

        if(ctx.exists(zipPath))
        {
            return "el destino ya fue creado previamente en el script: " + to;
        }

        ctx.addFile(zipPath);

        return null;
    }
}
