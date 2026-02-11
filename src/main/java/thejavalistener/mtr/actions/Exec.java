package thejavalistener.mtr.actions;

import thejavalistener.mtr.core.MyAction;

import java.util.ArrayList;
import java.util.List;

public class Exec extends MyAction
{
    private final String[] command;

    public Exec(String... command)
    {
        this.command = (command == null ? new String[0] : command);
    }

    @Override
    public int run()
    {
        if (command.length == 0) return ERROR;

        boolean detached = has("DETACHED");
        boolean max      = has("MAX");

        try
        {
            List<String> cmd = build(detached, max);

            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);

            Process p = pb.start();

            if (detached || max)
                return MyAction.SUCCESS;

            int exitCode = p.waitFor();
            return exitCode == 0 ? MyAction.SUCCESS : exitCode;
        }
        catch (Exception e)
        {
            return ERROR;
        }
    }

    private List<String> build(boolean detached, boolean max)
    {
        List<String> out = new ArrayList<>();

        boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

        // Filtramos opciones
        List<String> raw = new ArrayList<>();
        for (String s : command)
            if (!"DETACHED".equalsIgnoreCase(s) && !"MAX".equalsIgnoreCase(s))
                raw.add(s);

        if (isWin && max)
        {
            // cmd /c start "" /MAX <raw...>
            out.add("cmd");
            out.add("/c");
            out.add("start");
            out.add("");
            out.add("/MAX");
            out.addAll(raw);
            return out;
        }

        // Caso normal: si es .bat/.cmd => cmd /c
        String first = raw.get(0).toLowerCase();
        if (isWin && (first.endsWith(".bat") || first.endsWith(".cmd")))
        {
            out.add("cmd");
            out.add("/c");
        }

        out.addAll(raw);
        return out;
    }

    private boolean has(String opt)
    {
        for (String s : command)
            if (opt.equalsIgnoreCase(s)) return true;
        return false;
    }
}
