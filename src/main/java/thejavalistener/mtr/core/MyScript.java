
package thejavalistener.mtr.core;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MyScript
{
	public abstract List<MyAction> script();

	public int run()
	{
		try
		{
			List<MyAction> actions = script();
			
			
			ValidationContext ctx = new ValidationContext();

			
			
			// valido
			for(int i=0; i<actions.size(); i++)
			{
				MyAction action = actions.get(i);
				
				String err = action.validate(ctx);
				if( err!=null )
				{
					int nroPaso = i+1;
					String mssg = "Paso "+nroPaso+". "+action.getClass().getSimpleName()+": "+err;
					throw new RuntimeException(mssg);
				}
			}

			// ejecuto
			for(MyAction action:actions)
			{
				doAction(action);
			}
			
			return 0; // VER ESTO...
			
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			return MyAction.ERROR;
		}
	}

	/*
	 * ===================== Helpers opcionales =====================
	 */

	protected void doAction(MyAction a)
	{
		try
		{
			ProgressListener pl=null;

			if(a.isShowProgresBar()) pl=new ConsoleProgressBar(a.getVerb(),a.getDescription());

			a.execute(pl);
		}
		catch(Exception e)
		{
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
	}
}
