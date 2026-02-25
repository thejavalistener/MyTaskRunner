package thejavalistener.mtr.actions;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class DirCopy extends MyAction
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
        return "Copying dir";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{from, "to " + to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path srcRoot = Paths.get(from).normalize();
        Path dstBase = Paths.get(to).normalize();

        if (!Files.exists(srcRoot) || !Files.isDirectory(srcRoot))
            throw new IOException("Source directory does not exist: " + from);

        // Siempre replica la carpeta: dstBase / srcRoot.getFileName()
        Path dstRoot = dstBase.resolve(srcRoot.getFileName()).normalize();

        // si dstBase existe como archivo => error
        if (Files.exists(dstBase) && !Files.isDirectory(dstBase))
            throw new IllegalArgumentException("'to' debe ser directorio: " + to);

        Files.createDirectories(dstRoot);

        final long[] copied = new long[]{0};
        final long total = calculateTotalBytes(srcRoot);

        Files.walkFileTree(srcRoot, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                Path rel = srcRoot.relativize(dir);
                Path targetDir = dstRoot.resolve(rel).normalize();
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Path rel = srcRoot.relativize(file);
                Path targetFile = dstRoot.resolve(rel).normalize();

                if (targetFile.getParent() != null)
                    Files.createDirectories(targetFile.getParent());

                Files.copy(file, targetFile,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);

                copied[0] += Files.size(file);

                if (p != null && total > 0)
                {
                    int pct = (int)Math.min(100, (copied[0] * 100) / total);
                    p.setPercent(pct, "");
                }

                return FileVisitResult.CONTINUE;
            }
        });

        if (p != null) p.setPercent(100, "");
    }

    private long calculateTotalBytes(Path source) throws IOException
    {
        final long[] sum = new long[]{0};

        Files.walkFileTree(source, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (attrs.isRegularFile())
                    sum[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });

        return sum[0];
    }

    @Override
    public String validate(ValidationContext ctx)
    {
        if (from == null || from.isBlank())
            return "'from' es obligatorio";

        if (to == null || to.isBlank())
            return "'to' es obligatorio";

        Path srcRoot;
        Path dstBase;

        try
        {
            srcRoot = Paths.get(from).normalize();
        }
        catch(Exception e)
        {
            return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
        }

        try
        {
            dstBase = Paths.get(to).normalize();
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        if (!ctx.exists(srcRoot) && !Files.exists(srcRoot))
            return "no existe el directorio origen (según script): " + from;

        if (!ctx.isDirectory(srcRoot, true) && !Files.isDirectory(srcRoot))
            return "el origen no es un directorio: " + from;

        if (ctx.exists(dstBase) && !ctx.isDirectory(dstBase, true))
            return "el destino existe y no es un directorio: " + to;

        // Simulación: se crea dstBase y luego dstBase/srcName
        ctx.addDirectory(dstBase);

        Path dstRoot = dstBase.resolve(srcRoot.getFileName()).normalize();
        ctx.addDirectory(dstRoot);

        return null;
    }
}