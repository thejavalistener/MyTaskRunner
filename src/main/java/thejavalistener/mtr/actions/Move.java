package thejavalistener.mtr.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

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
    public void execute(Progress pl) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path pFrom = Paths.get(from);
        Path pTo   = Paths.get(to);

        if (pl != null) pl.begin();

        if (!Files.exists(pFrom))
            throw new java.io.IOException("Source does not exist: " + from);

        if (pTo.getParent() != null)
            Files.createDirectories(pTo.getParent());

        Files.move(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);

        if (pl != null) pl.setPercent(100,"");
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(from == null || from.isBlank())
        {
            return "'from' es obligatorio";
        }

        if(to == null || to.isBlank())
        {
            return "'to' es obligatorio";
        }

        Path pFrom;
        Path pTo;

        try
        {
            pFrom = Paths.get(from);
        }
        catch(Exception e)
        {
            return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
        }

        try
        {
            pTo = Paths.get(to);
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        if(!ctx.exists(pFrom))
        {
            return "no existe el origen (según script): " + from;
        }

        if(ctx.exists(pTo))
        {
            return "el destino ya existe (según script): " + to;
        }

        if(ctx.isDirectory(pFrom))
        {
            ctx.addDirectory(pTo);
        }
        else
        {
            ctx.addFile(pTo);
        }

        return null;
    }

}
