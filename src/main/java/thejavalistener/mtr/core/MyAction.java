package thejavalistener.mtr.core;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.console.Progress;

public abstract class MyAction
{
    public static final int SUCCESS            = 0;
    public static final int ERROR         = 1;
    public static final int IO_ERROR      = 2;
    public static final int NETWORK_ERROR = 3;

    private boolean showProgress;
	private boolean stopScriptOnError;
    protected final String[] args;

    public abstract String getVerb();
    public abstract String[] getDescription();
    public abstract String validate(ValidationContext ctx); // null = OK
    protected abstract void doAction(Progress p) throws Exception;

    public void execute() throws Exception
    {
    	MyConsole console = MyConsoles.get();
    	
		Progress p = null;
		if(isShowProgress())
		{
			p = console.progressMeter(100);
		}
		
		// ejecuto
		doAction(p);
		
		if( p!=null ) p.finish();

		// agrego un espacio si hubo progress
		String space = p!=null?" ":"";
		console.print(space);
    }
    
    
	
    public boolean isShowProgress()
	{
		return showProgress;
	}
	public void setShowProgress(boolean showProgress)
	{
		this.showProgress=showProgress;
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


    protected final String arg(int i)
    {
        return args[i];
    }

    protected final boolean hasArg(int i)
    {
        return i >= 0 && i < args.length;
    }
}