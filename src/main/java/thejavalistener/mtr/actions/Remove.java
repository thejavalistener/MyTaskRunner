package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Remove extends MyAction
{
    private final Path target;

    public Remove(String path)
    {
        this.target = Paths.get(path);
    }

    @Override
    public int run()
    {
        try
        {
            if (!Files.exists(target))
                return SUCCESS;

            if (Files.isDirectory(target))
                deleteDir(target);
            else
                Files.deleteIfExists(target);

            return SUCCESS;
        }
        catch (Exception e)
        {
            return IO_ERROR;
        }
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
}
