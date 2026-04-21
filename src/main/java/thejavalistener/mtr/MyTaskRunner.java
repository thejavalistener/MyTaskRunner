package thejavalistener.mtr;

import java.awt.Color;
import java.util.List;

import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.mtr.core.MyScript;

public class MyTaskRunner
{
	public static void main(String[] args)
	{
		MyAwt.setWindowsLookAndFeel();

		try
		{
			if( _isHelp(args) )
			{
				MyTaskRunnerDoc.doc();
				System.exit(0);
			}

			// qué script voy a ejecutar
			String jsonTargetScript=args[0];

			MyConsole console=MyConsoles.getOnWindow("MyTaskRunner: "+jsonTargetScript);
			console.fg(Color.GRAY);
			console.banner("MyTaskRunner");
			console.x();

			
			MyScript script=new MyScript(jsonTargetScript);

			List<String[]> varss = script.getVars(true);
			for(String[]x:varss)
			{
				System.out.println(x[0]+"="+x[1]);
			}
			
			// ejecuto el script
			int returnValue=script.run();

			System.exit(returnValue);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			System.exit(MyScript.ERROR);
		}
	}

	/*
	 * ===================== CARGA UNIFICADA =====================
	 */

//	private static MyScript loadScript(String target) throws Exception
//	{
//		MyScript script;
//
//		// JSON
//		if(target.toLowerCase().endsWith(".json"))
//		{
//
//			script=new MyJsonScriptImple(target);
//		}
//		else
//		{
//			// Script Java normal
//			Class<?> clazz=Class.forName(target);
//			script=(MyScript)clazz.getDeclaredConstructor().newInstance();
//		}
//
//		return script;
//	}
	
	private static boolean _isHelp(String[] args)
	{
	    String a = args.length==0?"-?":args[0].toLowerCase();

	    return a.equals("-?")
	        || a.equals("--?")
	        || a.equals("/?")
	        || a.equals("-help")
	        || a.equals("/help");
	}	
}
