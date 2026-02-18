package thejavalistener.mtr;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyJsonScriptImple;
import thejavalistener.mtr.core.MyScript;

public class MyTaskRunner
{
    public static void main(String[] args)
    {   
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
	        
	        // si es json lo levanto dinámicamente, si es Java lo hago por reflection
            MyScript script = loadScript(targetScript);

            
            MyConsole console = MyConsole.singleton();
        	console.openWindow("Demo");

            
            
            // ejecuto el script
            int returnValue  = script.run();
            
            console.print("Toque una tecla para finalizar").pressAnyKey();
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
