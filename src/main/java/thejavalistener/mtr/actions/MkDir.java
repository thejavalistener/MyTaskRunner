package thejavalistener.mtr.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class MkDir extends MyAction
{
    private String to;

    public void setPath(String to)
    {
        this.to = to;
    }

    @Override
    public String getVerb()
    {
        return "Creating directory";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        if (to == null || to.isBlank())
            throw new IllegalArgumentException("Path is null");

        Path dir = Paths.get(to);

        Files.createDirectories(dir);

        if (p != null) p.setPercent(100,"");
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if (to == null || to.isBlank())
            return "'to' es obligatorio";

        Path dir;

        try
        {
            dir = Paths.get(to).normalize();
        }
        catch (Exception e)
        {
            return "path inválido: " + to + " (" + e.getMessage() + ")";
        }

        if (ctx != null && ctx.exists(dir))
        {
            if (!ctx.isDirectory(dir))
                return "ya existe pero no es un directorio: " + to;
        }

        if (ctx != null)
            ctx.addDirectory(dir);

        return null;
    }}
