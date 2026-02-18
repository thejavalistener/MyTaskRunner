
package thejavalistener.mtr.core;

import java.util.List;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.console.Progress;
import thejavalistener.fwkutils.string.MyString;

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
		try
		{
			
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
			
	        MyConsole console = MyConsoles.get();
	        console.println("[fg(YELLOW)]Running: [x][b]"+getScriptName()+"[x]");
	        

			// ejecuto cada acción del script
			for(MyAction action:actions)
			{
				_executeAction(action);
			}
			
			
            console.print("[fg(YELLOW)]Returned value: [x][b]SUCCESS[x]. Closing in ").countdown(9);
			return SUCCESS;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
	        MyConsole console = MyConsoles.get();
            console.print("[fg(YELLOW)]Returned value: [x][b]ERROR[x]. Closing in ").countdown(9);
			return ERROR;
		}
	}
	
	private void _executeAction(MyAction a) throws Exception
	{
		MyConsole console = MyConsoles.get();

		try
		{
			// Copiando D:/temp/equis a C:/unDir/zeta

			_log(a);
			
			// si hay progress => [#####       ]
			Progress p = null;
			if(a.isShowProgressBar())
			{
				p = console.progressBar(20,100);
//				p = console.progressMeter(100);
				p.setUsingThread(false);
			}
			
			// ejecuto
			a.execute(p);
			
			console.println("OK ");
		}
		catch(Exception e)
		{
			System.out.print("ERROR");
			if( a.isStopScriptOnError() )
			{
				throw new RuntimeException("Failed "+a.getClass().getSimpleName(),e);
			}
			else
			{
				System.out.println("Failed "+a.getClass().getSimpleName()+": "+e.getMessage());
				e.printStackTrace();
			}
		}	
		
		System.out.println();
		System.out.flush();

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
