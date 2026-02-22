package thejavalistener.mtr.core;

import java.util.List;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.console.Progress;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyException;

public abstract class MyScript
{
	public static final int SUCCESS = 0;
	public static final int ERROR = 1;
	public abstract List<MyAction> getScriptActions();
	
	public String getScriptName()
	{
		return getClass().getSimpleName();
	}
	
	public int run()
	{
        MyConsole console = MyConsoles.get();
		
		try
		{
			// comienza script
	        console.println("[fg(YELLOW)]Running: [x][b]"+getScriptName()+"[x]");

			// obtengo la lista de acciones del script
			List<MyAction> actions = getScriptActions();

			// creo un FS ficticio para validar los parámetros
			ValidationContext ctx = new ValidationContext();
			
			// valido cada acción del script
			for(int i=0; i<actions.size(); i++)
			{
				MyAction action = actions.get(i);
				
				// cada acción se valida a sí misma
				String err = action.validate(ctx);
				if( err!=null )
				{
					int nroPaso = i+1;
					
					// Paso 4. Remove: No existe el archivo o carpeta a remover 
					String mssg = "Paso "+nroPaso+". "+action.getClass().getSimpleName()+": "+err;
					throw new RuntimeException(mssg);
				}
			}
				        
	        int step = 1;
	        
			// ejecuto cada acción del script
			for(MyAction action:actions)
			{
				console.print("[fg(CYAN)]Step: "+step+".[x] ");
				_executeAction(action);
				step++;
			}
			
			
			
			// finaliza script OK
            console.print("[fg(YELLOW)]Returned value: [x][b]SUCCESS[x]. ");
            System.out.println(console.getTextPane().getText());

            console.print("Closing in ").countdown(10);
			return SUCCESS;
		}
		catch(Exception e)
		{
	        // finaliza script ERROR
			String err = MyException.stackTraceToString(e);
            console.println("[fg(RED)]"+err+"[x]");
            console.print("[fg(YELLOW)]Returned value: [x][fg(RED)][b]ERROR[x][x]. ");
            System.out.println(console.getTextPane().getText());

            console.print("Closing in ").countdown(10);
			return ERROR;
		}
	}
	
	private void _executeAction(MyAction a) throws Exception
	{
		MyConsole console = MyConsoles.get();

		try
		{
			// presentación: Copiando D:/temp/equis a C:/unDir/zeta    	
	    	_log(a);
	    	
			// ejecuto la acción
			a.execute();

			// exito
			console.println("[b][fg(GREEN)]OK[x][x] ");
			
		}
		catch(Exception e)
		{				
			// error (fatal o recuperable)
			console.println("[fg(RED)][b]FAILED:[x] "+e.getMessage()+"[x] ");
			
			// stacktrace
			String stackTrace = MyException.stackTraceToString(e);
			console.println("[fg(RED)]"+stackTrace+"[x]");

			if( a.isStopScriptOnError() )
			{
				 throw new IllegalStateException(e);
			}
		}	
	}
	
	private void _log(MyAction a)
	{
		MyConsole console = MyConsoles.get();
		
		String mssg = "";

		int maxLength = (int)(console.getTextPane().cols()*0.9);
		String vervo = "[fg(GREEN)]"+a.getVerb()+"[x]";
		String mssgs[] = a.getDescription();
		if( mssgs.length>1 )
		{
			mssg = vervo+"\n";
			for(int i=0; i<mssgs.length; i++)
			{
				mssg+="\t"+MyString.trimMiddle(mssgs[i],maxLength);
				mssg+=i<mssgs.length-1?"\n":" ";
			}			
		}
		else
		{
			mssg = MyString.trimMiddle(vervo+" "+mssgs[0],maxLength)+" ";			
		}

		console.print(mssg);
	}
}
