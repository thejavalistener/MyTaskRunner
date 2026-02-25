package thejavalistener.mtr.actions;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class Remove extends MyAction
{
    private String from;

    public void setFrom(String from)
    {
        this.from = from;
    }

    @Override
    public String getVerb()
    {
        return "Removing";
    }

    @Override
    public String[] getDescription()
    {
    	return new String[]{from};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        if (from == null || from.isBlank())
            throw new IllegalArgumentException("From not set");

        Path target = Paths.get(from);

        if (!Files.exists(target))
        {
            if (p != null) p.setPercent(100,"");
            return;
        }

        if (Files.isDirectory(target))
            deleteDir(target);
        else
            Files.deleteIfExists(target);

        if (p != null) p.setPercent(100,"");
    }

    private void deleteDir(Path dir) throws IOException
    {
        Files.walkFileTree(dir, new SimpleFileVisitor<>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path d, IOException exc) throws IOException
            {
                Files.deleteIfExists(d);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(from == null || from.isBlank())
        {
            return "'from' es obligatorio";
        }

        Path p;

        try
        {
            p = Paths.get(from);
        }
        catch(Exception e)
        {
            return "from inválido: " + from + " (" + e.getMessage() + ")";
        }

        if(ctx.exists(p))
        {
            ctx.remove(p);
            return null;
        }

        if(Files.exists(p))
        {
            return null;
        }

        if(isStopScriptOnError())
        {
            return "no existe: " + from;
        }

        return null;
    }
}
