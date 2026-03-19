package thejavalistener.mtr.core;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.doc.DocAction;

public abstract class MyAction 
{
	private String executeIf;
	private boolean showProgress=false;
	private boolean stopScriptOnError=true;
	protected final String[] args;

	private String ifdef;
	private String ifndef;

	private boolean mustSkipped = false;
	
	public abstract String getVerb();
	public abstract String[] getDescription();
	public abstract String validate(ValidationContext ctx); // null = OK
	protected abstract void doAction(Progress p) throws Exception;
	public abstract DocAction getActionDoc();
	
	
	public void execute() throws Exception
	{
		MyConsole console=MyConsoles.get();

		Progress p=null;
		if(isShowProgress())
		{
			p=console.progressMeter(100);
		}

		// ejecuto
		doAction(p);

		if(p!=null) p.finish();

		// agrego un espacio si hubo progress
		String space=p!=null?" ":"";
		console.print(space);
	}

	protected boolean checkExecuteIf()
	{
		if(executeIf==null) return true;

		throw new RuntimeException(getClass().getSimpleName()+" no soporta 'conditional'");
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
	
	public String getExecuteIf()
	{
		return executeIf;
	}
	public void setExecuteIf(String executeIf)
	{
		this.executeIf=executeIf;
	}
	protected MyAction(String... args)
	{
		this.args=(args==null?new String[0]:args);
	}

	protected final String arg(int i)
	{
		return args[i];
	}

	protected final boolean hasArg(int i)
	{
		return i>=0&&i<args.length;
	}

	public String getIfdef()
	{
		return ifdef;
	}

	public void setIfdef(String ifdef)
	{
		this.ifdef=ifdef;
	}

	public String getIfndef()
	{
		return ifndef;
	}

	public void setIfndef(String ifndef)
	{
		this.ifndef=ifndef;
	}
	public boolean isMustSkipped()
	{
		return mustSkipped;
	}
	public void setMustSkipped(boolean mustSkipped)
	{
		this.mustSkipped=mustSkipped;
	}
	
	// ---- DOC ----
	
	
	
}