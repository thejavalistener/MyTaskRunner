package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ProgressListener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Exec extends MyAction
{
    public enum ExecOpt { DETACHED, WAIT }

    private final String[] command;
    private final EnumSet<ExecOpt> opts;

    public Exec(String... command)
    {
        this(EnumSet.of(ExecOpt.DETACHED), command); // DEFAULT
    }

    public Exec(EnumSet<ExecOpt> opts, String... command)
    {
        this.command = (command == null ? new String[0] : command);
        this.opts = normalize(opts);
    }

    @Override
    public String getVerb() { return "Executing"; }

    @Override
    public String getDescription()
    {
        return command.length == 0 ? "" : String.join(" ", command);
    }

    @Override
    public void execute(ProgressListener pl) throws Exception
    {
        if (command.length == 0)
            throw new IllegalArgumentException("Empty command");

        boolean detached = opts.contains(ExecOpt.DETACHED);
        boolean wait     = opts.contains(ExecOpt.WAIT);

        if (pl != null) pl.onStart();

        List<String> cmd = build(detached);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process p = pb.start();

        if (!wait) // detached (default)
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

    private EnumSet<ExecOpt> normalize(EnumSet<ExecOpt> in)
    {
        if (in == null || in.isEmpty())
            return EnumSet.of(ExecOpt.DETACHED);

        EnumSet<ExecOpt> out = EnumSet.copyOf(in);

        // If both are present, WAIT wins (explicit)
        if (out.contains(ExecOpt.WAIT))
        {
            out.remove(ExecOpt.DETACHED);
            return out;
        }

        if (!out.contains(ExecOpt.DETACHED))
            out.add(ExecOpt.DETACHED);

        return out;
    }

    private List<String> build(boolean detached)
    {
        boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

        List<String> raw = new ArrayList<>();
        for (String s : command) raw.add(s);

        List<String> out = new ArrayList<>();

        if (isWin && detached)
        {
            // real detach
//            out.add("cmd");
//            out.add("/c");
//            out.add("start");
//            out.add("");
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
}
