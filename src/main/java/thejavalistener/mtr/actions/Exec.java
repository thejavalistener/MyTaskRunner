package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;
import thejavalistener.mtr.core.ValidationContext;

public class Exec extends MyAction
{
    public enum ExecOpt { DETACHED, WAIT }

    private String command;   // línea completa
    private String opts;      // "DETACHED", "WAIT" o null

    public void setCommand(String command)
    {
        this.command = command;
    }

    public void setOpts(String opts)
    {
        this.opts = opts;
    }

    @Override
    public String getVerb()
    {
        return "Executing";
    }

    @Override
    public String getDescription()
    {
        return command;
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (command == null || command.isBlank())
            throw new IllegalArgumentException("Empty command");

        EnumSet<ExecOpt> options = parseOpts(opts);

        boolean detached = options.contains(ExecOpt.DETACHED);
        boolean wait     = options.contains(ExecOpt.WAIT);

        if (pl != null) pl.onStart();

        List<String> cmd = build(detached);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process p = pb.start();

        if (!wait)
        {
            if (pl != null) pl.onProgress(100);
            if (pl != null) pl.onFinish();
            return;
        }

        int exitCode = p.waitFor();
        if (exitCode != 0)
            throw new RuntimeException("Process exited with code: " + exitCode);

        if (pl != null) pl.onProgress(100);
        if (pl != null) pl.onFinish();
    }

    private EnumSet<ExecOpt> parseOpts(String s)
    {
        if (s == null || s.isBlank())
            return EnumSet.of(ExecOpt.DETACHED);

        EnumSet<ExecOpt> set = EnumSet.noneOf(ExecOpt.class);

        for (String part : s.split(","))
        {
            part = part.trim().toUpperCase();
            if (!part.isEmpty())
                set.add(ExecOpt.valueOf(part));
        }

        if (set.contains(ExecOpt.WAIT))
        {
            set.remove(ExecOpt.DETACHED);
            return set;
        }

        if (!set.contains(ExecOpt.DETACHED))
            set.add(ExecOpt.DETACHED);

        return set;
    }

    private List<String> build(boolean detached)
    {
        boolean isWin = System.getProperty("os.name")
                .toLowerCase()
                .contains("win");

        List<String> raw = Arrays.asList(command.split(" "));
        List<String> out = new ArrayList<>();

        if (isWin && detached)
        {
            out.addAll(raw);
            return out;
        }

        String first = raw.get(0).toLowerCase();

        if (isWin && (first.endsWith(".bat") || first.endsWith(".cmd")))
        {
            out.add("cmd");
            out.add("/c");
        }

        out.addAll(raw);
        return out;
    }
    
    @Override
    public String validate(ValidationContext ctx)
    {
        if(command == null || command.isBlank())
        {
            return "'command' es obligatorio";
        }

        try
        {
            parseOpts(opts);
        }
        catch(Exception e)
        {
            return "opts inválido: " + opts + " (" + e.getMessage() + ")";
        }

        return null;
    }
}
