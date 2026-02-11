package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;

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
    public int run()
    {
        try
        {
            if (!Files.exists(from)) return IO_ERROR;

            if (to.getParent() != null)
                Files.createDirectories(to.getParent());

            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
            return SUCCESS;
        }
        catch (Exception e)
        {
            return IO_ERROR;
        }
    }
}
