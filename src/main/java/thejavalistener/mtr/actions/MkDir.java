package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class MkDir extends MyAction
{
    private final Path dir;

    public MkDir(String path)
    {
        this.dir = Paths.get(path);
    }

    @Override
    public int run()
    {
        try
        {
            Files.createDirectories(dir);
            return SUCCESS;
        }
        catch (Exception e)
        {
            return IO_ERROR;
        }
    }
}
