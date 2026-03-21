package thejavalistener.mtr.core;

import java.util.List;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyException;

public abstract class MyScript
{
	public static final int SUCCESS=0;
	public static final int ERROR=1;

	protected ScriptOptions options = new ScriptOptions();
	protected abstract List<MyAction> getScriptActions();

	public void validateSyntax() throws Exception {};

	public String getScriptName()
	{
		return getClass().getSimpleName();
	}
	
	protected void beforeRun() {}
	protected void afterRun() {}

	public int run()
	{
		MyConsole console=MyConsoles.get();

		int delay = options.getCloseDelaySeconds();

		Runnable rDelay = delay==0?()->console.print("\nPress any key to exit...").pressAnyKey():()->console.print("\nClosing in ").countdown(delay);
		
		
		try
		{
			// comienza script
			console.println("[fg(YELLOW)]Running: [x][b]"+getScriptName()+"[x]");

			// valido la sintaxis del script
			validateSyntax();

			
			// obtengo la lista de acciones del script
			List<MyAction> actions=getScriptActions();

			// hook
			beforeRun();
			
			// valido las acciones del script
			validateActions(actions);

			int step=1;

			
			// ejecuto cada acción del script
			for(MyAction action:actions)
			{
				console.println("[fg(CYAN)]Step: "+step+".[x] ");
				
				_executeAction(action);
				
				step++;
			}
			

			// finaliza script OK
			console.print("[fg(YELLOW)]Returned value: [x][b]SUCCESS[x]. ");
			System.out.println(console.getTextPane().getText());

			// hook
			afterRun();
			
//			console.print("Closing in ").countdown(delay);
			rDelay.run();
			return SUCCESS;
		}
		catch(Exception e)
		{
			// finaliza script ERROR
			String err=MyException.stackTraceToString(e);
			console.println("[fg(RED)]"+err+"[x]");
			console.print("[fg(YELLOW)]Returned value: [x][fg(RED)][b]ERROR[x][x]. ");
			System.out.println(console.getTextPane().getText());

//			console.print("Closing in ").countdown(delay);
			rDelay.run();
			return ERROR;
		}
	}

	private void _executeAction(MyAction a) throws Exception
	{
		MyConsole console=MyConsoles.get();

		try
		{
			// presentación: Copiando D:/temp/equis a C:/unDir/zeta
			_log(a);
			
			a.checkExecuteIf();

			
			if( a.isMustSkipped() || !a.checkExecuteIf() )
			{
	            console.println("[b][fg(GREEN)]Skiped[x][x] ");
	            return;				
			}
			
			// ejecuto la acción

			if( !options.isSimulationMode() )
			{
				a.execute();
			}
			
			// exito
			console.println("[b][fg(GREEN)]OK[x][x] ");
		}
		catch(Exception e)
		{
			// error (fatal o recuperable)
			console.println("[fg(RED)][b]FAILED:[x] "+e.getMessage()+"[x] ");

			// stacktrace
			String stackTrace=MyException.stackTraceToString(e);
			console.println("[fg(RED)]"+stackTrace+"[x]");

			if(a.isStopScriptOnError())
			{
				throw new IllegalStateException(e);
			}
		}
	}

	private void _log(MyAction a)
	{
		MyConsole console=MyConsoles.get();

		String mssg="";

		int maxLength=(int)(console.getTextPane().cols()*0.9);
		String vervo="[fg(GREEN)]"+a.getVerb()+"[x]";
		String mssgs[]=a.getDescription();
		if(mssgs.length>1)
		{
			mssg=vervo+"\n";
			for(int i=0; i<mssgs.length; i++)
			{
				mssg+="\t"+MyString.trimMiddle(mssgs[i],maxLength);
				mssg+=i<mssgs.length-1?"\n":" ";
			}
		}
		else
		{
			mssg=MyString.trimMiddle(vervo+" "+mssgs[0],maxLength)+" ";
		}

		console.print(mssg);
	}
	
	private boolean _checkDefines(MyAction a)
	{
		
	    String ifdef=a.getIfdef();
	    String ifndef=a.getIfndef();

	    if(ifdef!=null && System.getProperty(ifdef)==null)
	        return false;

	    if(ifndef!=null && System.getProperty(ifndef)!=null)
	        return false;

	    return true;
	}

	public void validateActions(List<MyAction> actions) throws Exception
	{
		// creo un FS ficticio para validar los parámetros
		ValidationContext ctx=new ValidationContext();

		for(int i=0; i<actions.size(); i++)
		{
			MyAction action=actions.get(i);

			// cada acción se valida a sí misma
			String err=action.validate(ctx);
			if(err!=null&&action.isStopScriptOnError())
			{
				int nroPaso=i+1;

				// Paso 4. Remove: No existe el archivo o carpeta a remover
				String mssg="Step "+nroPaso+". "+action.getClass().getSimpleName()+": "+err;
				throw new RuntimeException(mssg);
			}
		}

	}
}
