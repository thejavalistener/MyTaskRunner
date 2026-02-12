package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.nio.file.*;

public class Move extends MyAction
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
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path pFrom = Paths.get(from);
        Path pTo   = Paths.get(to);

        if (pl != null) pl.onStart();

        if (!Files.exists(pFrom))
            throw new java.io.IOException("Source does not exist: " + from);

        if (pTo.getParent() != null)
            Files.createDirectories(pTo.getParent());

        Files.move(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }
}
