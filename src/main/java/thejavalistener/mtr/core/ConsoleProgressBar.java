package thejavalistener.mtr.core;

public class ConsoleProgressBar implements ProgressListener
{
    private static final int WIDTH = 20;

    private final String prefix; // "Unzipping: a to b "

    private int lastPct = -1;
    private boolean started = false;

    public ConsoleProgressBar(String verb, String description)
    {
        this.prefix = verb + ": " + description + " ";
    }

    @Override
    public void onStart()
    {
        started = true;
        render(0, false);
    }

    @Override
    public void onProgress(int percent)
    {
        if (!started) onStart();

        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        if (percent == lastPct) return;
        lastPct = percent;

        render(percent, false);
    }

    @Override
    public void onFinish()
    {
        if (!started) onStart();
        render(100, true);
        System.out.println();
    }

    private void render(int percent, boolean ok)
    {
        int filled = (percent * WIDTH) / 100;

        StringBuilder sb = new StringBuilder();
        sb.append("\r").append(prefix).append("[");

        for (int i = 0; i < WIDTH; i++)
            sb.append(i < filled ? "#" : " ");

        sb.append("]");

        if (ok) sb.append(" OK");

        System.out.print(sb.toString());
        System.out.flush();
    }
}
