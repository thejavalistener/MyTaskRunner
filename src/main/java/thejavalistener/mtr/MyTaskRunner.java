package thejavalistener.mtr;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyJsonScriptImple;
import thejavalistener.mtr.core.MyScript;

public class MyTaskRunner
{
    public static void main(String[] args)
    {
        int rc = run(args);
        System.exit(rc);
    }

    public static int run(String[] args)
    {
        if (args == null || args.length == 0)
        {
            System.out.println("Uso:");
            System.out.println("  java thejavalistener.mtr.MyTaskRunner <ClaseScript>");
            System.out.println("  java thejavalistener.mtr.MyTaskRunner <script.json>");
            return MyAction.ERROR;
        }

        String target = args[0];

        try
        {
            MyScript script = loadScript(target);
            return script.run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return MyAction.ERROR;
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
