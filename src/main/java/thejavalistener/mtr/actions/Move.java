package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.nio.file.*;

public class Move extends MyAction
{
    private final Path from;
    private final Path to;

    public Move(String from, String to)
    {
        this.from = Paths.get(from);
        this.to   = Paths.get(to);
    }

    @Override
    public String getVerb()
    {
        return "Moving";
    }

    @Override
    public String getDescription()
    {
        return from + " to " + to;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (pl != null) pl.onStart();

        if (!Files.exists(from))
            throw new java.io.IOException("Source does not exist: " + from);

        if (to.getParent() != null)
            Files.createDirectories(to.getParent());

        Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
