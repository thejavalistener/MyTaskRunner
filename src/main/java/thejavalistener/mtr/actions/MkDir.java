package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

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
    public String getVerb()
    {
        return "Creating directory";
    }

    @Override
    public String getDescription()
    {
        return dir.toString();
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (pl != null) pl.onStart();

        Files.createDirectories(dir);

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
