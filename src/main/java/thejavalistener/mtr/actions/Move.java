package thejavalistener.mtr.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.actions.doc.MoveDoc;
import thejavalistener.mtr.actions.doc.MyActionDoc;
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
    public String[] getDescription()
    {
    	return new String[]{from , "to " + to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        if (from == null || to == null)
            throw new IllegalArgumentException("From/To not set");

        Path pFrom = Paths.get(from);
        Path pTo   = Paths.get(to);

        if (!Files.exists(pFrom))
            throw new java.io.IOException("Source does not exist: " + from);

        if (pTo.getParent() != null)
            Files.createDirectories(pTo.getParent());

        Files.move(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);

        if (p != null) p.setPercent(100,"");
    }

    @Override
    public MyActionDoc getActionDoc()
    {
    	return new MoveDoc();
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
            pFrom = Paths.get(from).normalize();
        }
        catch(Exception e)
        {
            return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
        }

        try
        {
            pTo = Paths.get(to).normalize();
        }
        catch(Exception e)
        {
            return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
        }

        // tracking liviano
        if(ctx != null)
        {
            Path parent = pTo.getParent();
            if(parent != null) ctx.addDirectory(parent);

            // no sabemos si es file o dir → asumimos file por defecto
            ctx.addFile(pTo);
        }

        return null;
    }
}
