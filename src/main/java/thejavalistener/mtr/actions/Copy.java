package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Copy extends MyAction
{
    private final Path from;
    private final Path to;

    public Copy(String from, String to)
    {
        this.from = Paths.get(from);
        this.to   = Paths.get(to);
    }

    @Override
    public int run()
    {
        try
        {
            if (!Files.exists(from))
                return IO_ERROR;

            if (Files.isDirectory(from))
            {
                copyDirectory(from, to);
            }
            else
            {
                if (to.getParent() != null)
                    Files.createDirectories(to.getParent());

                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            }

            return SUCCESS;
        }
        catch (Exception e)
        {
            return IO_ERROR;
        }
    }

    private void copyDirectory(Path source, Path target) throws Exception
    {
        Files.walk(source).forEach(path ->
        {
            try
            {
                Path dest = target.resolve(source.relativize(path));

                if (Files.isDirectory(path))
                {
                    Files.createDirectories(dest);
                }
                else
                {
                    Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        });
    }
}
