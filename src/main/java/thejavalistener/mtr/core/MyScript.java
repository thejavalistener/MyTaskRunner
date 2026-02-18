
package thejavalistener.mtr.core;

import java.util.List;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.Progress;

public abstract class MyScript
{
	public static final int SUCCESS = 0;
	public static final int ERROR = 1;
	public abstract List<MyAction> getScriptActions();
	
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

			// ejecuto cada acción del script
			for(MyAction action:actions)
			{
				_executeAction(action);
			}
			
			return SUCCESS;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return ERROR;
		}
	}

	private void _executeAction(MyAction a) throws Exception
	{
		MyConsole console = MyConsole.singleton();

		try
		{
//			if( a instanceof Zip)
//			{
//				System.out.println("XX");
//			}
			
			
			// Copiando D:/temp/equis a C:/unDir/zeta
//			System.out.print(a.getVerb() + ": " + a.getDescription()+" ");

			console.print(a.getVerb()+": "+a.getDescription()+" ");
			
			
			// si hay progress => [#####       ]
			Progress p = null;
			if(a.isShowProgressBar())
			{
				p = console.progressBar(20,100);
				p.setUsingThread(false);
			}
			
			// ejecuto
			a.execute(p);
			
//			System.out.print("OK ");
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
}
