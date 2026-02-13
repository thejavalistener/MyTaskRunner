package thejavalistener.mtr.core;

public abstract class MyAction
{
    public static final int SUCCESS            = 0;
    public static final int ERROR         = 1;
    public static final int IO_ERROR      = 2;
    public static final int NETWORK_ERROR = 3;

    protected final String[] args;

    public abstract String getVerb();
    public abstract String getDescription();
    public abstract String validate(ValidationContext ctx); // null = OK

	private boolean showProgresBar;
	private boolean stopScriptOnError;
	
	
    public boolean isShowProgresBar()
	{
		return showProgresBar;
	}
	public void setShowProgresBar(boolean showProgresBar)
	{
		this.showProgresBar=showProgresBar;
	}
	public boolean isStopScriptOnError()
	{
		return stopScriptOnError;
	}
	public void setStopScriptOnError(boolean stopScriptOnError)
	{
		this.stopScriptOnError=stopScriptOnError;
	}
	protected MyAction(String... args)
    {
        this.args = (args == null ? new String[0] : args);
    }

    public abstract void execute(ProgressListener pl) throws Exception;

    protected final String arg(int i)
    {
        return args[i];
    }

    protected final boolean hasArg(int i)
    {
        return i >= 0 && i < args.length;
    }
}
