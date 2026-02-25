package thejavalistener.mtr;

import java.awt.Color;

import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyScript;
import thejavalistener.mtr.json.MyJsonScriptImple;

public class MyTaskRunner
{
    public static void main(String[] args)
    {   
    	MyAwt.setWindowsLookAndFeel();
    	
        try
        {
	    	// usage
	        if (args == null || args.length == 0)
	        {
	            System.out.println("Usage:");
	            System.out.println(" java thejavalistener.mtr.MyTaskRunner <script.json>");
	            System.out.println(" java thejavalistener.mtr.MyTaskRunner <ScriptClass>");
	            System.exit(MyAction.ERROR);
	        }
	        
	        // qué script voy a ejecutar
	        String targetScript = args[0];

	        MyConsole console = MyConsoles.getOnWindow("MyTaskRunner: "+targetScript);
	        console.fg(Color.GRAY);
	        console.banner("MyTaskRunner");
	        console.x();
	        
	        // si es json lo levanto dinámicamente, si es Java lo hago por reflection
            MyScript script = loadScript(targetScript);
            
            
            // ejecuto el script
            int returnValue  = script.run();
        
            System.exit(returnValue);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(MyAction.ERROR);
        }
    }

    /* =====================
       CARGA UNIFICADA
       ===================== */

    private static MyScript loadScript(String target) throws Exception
    {
    	MyScript script;
    	
        // JSON
        if (target.toLowerCase().endsWith(".json"))
        {

            script = new MyJsonScriptImple(target);
        }
        else
        {
	        // Script Java normal
	        Class<?> clazz = Class.forName(target);
	        script = (MyScript)clazz.getDeclaredConstructor().newInstance();
        }	
        
        return script;
    }
}
