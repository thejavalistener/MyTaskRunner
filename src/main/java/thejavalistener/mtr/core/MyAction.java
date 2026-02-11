package thejavalistener.mtr.core;

public abstract class MyAction
{
    public static final int SUCCESS            = 0;
    public static final int ERROR         = 1;
    public static final int IO_ERROR      = 2;
    public static final int NETWORK_ERROR = 3;

    protected final String[] args;

    protected MyAction(String... args)
    {
        this.args = (args == null ? new String[0] : args);
    }

    public abstract int run();

    protected final String arg(int i)
    {
        return args[i];
    }

    protected final boolean hasArg(int i)
    {
        return i >= 0 && i < args.length;
    }
}
