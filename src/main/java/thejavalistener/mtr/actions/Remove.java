package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

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
    public String getVerb()
    {
        return "Removing";
    }

    @Override
    public String getDescription()
    {
        return target.toString();
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        // Remove is usually fast; still supports progress, but it might be unused.
        if (pl != null) pl.onStart();

        if (!Files.exists(target))
        {
            if (pl != null) pl.onProgress(100);
            if (pl != null) pl.onFinish();
            return;
        }

        if (Files.isDirectory(target))
            deleteDir(target);
        else
            Files.deleteIfExists(target);

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
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
