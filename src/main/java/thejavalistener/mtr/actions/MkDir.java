package thejavalistener.mtr.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class MkDir extends MyAction
{
    private String path;

    public void setPath(String path)
    {
        this.path = path;
    }

    @Override
    public String getVerb()
    {
        return "Creating directory";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{path};
    }

    @Override
    public void execute(Progress pl) throws Exception
    {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path is null");

        Path dir = Paths.get(path);

        if (pl != null) pl.begin();

        Files.createDirectories(dir);

        if (pl != null) pl.setPercent(100,"");
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(path == null || path.isBlank())
        {
            return "'path' es obligatorio";
        }

        Path dir;
        try
        {
            dir = Paths.get(path);
        }
        catch(Exception e)
        {
            return "path inválido: " + path + " (" + e + ")";
        }

        if(ctx.exists(dir))
        {
            if(!ctx.isDirectory(dir))
            {
                return "ya existe pero no es un directorio: " + path;
            }
        }

        ctx.addDirectory(dir);

        return null;
    }
}
