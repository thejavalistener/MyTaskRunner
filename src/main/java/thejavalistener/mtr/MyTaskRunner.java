package thejavalistener.mtr;

import thejavalistener.mtr.core.MyScript;
import thejavalistener.mtr.core.MyAction;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
            System.out.println("  java thejavalistener.mtr.MyTaskRunner <script.json> [--scriptsDir=DIR]");
            return MyAction.ERROR;
        }

        String target = args[0];

        try
        {
            if (target.toLowerCase().endsWith(".json"))
            {
                System.out.println("JSON aún no implementado: " + target);
                return MyAction.ERROR;
            }

            // Si está en el classpath, basta con Class.forName
            Class<?> clazz = Class.forName(target);
            Object o = clazz.getDeclaredConstructor().newInstance();

            if (!(o instanceof MyScript))
            {
                System.out.println("La clase no extiende MyScript: " + target);
                return MyAction.ERROR;
            }

            MyScript script = (MyScript) o;
            return script.run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return MyAction.ERROR;
        }
    }

    /* =====================
       (Opcional) cargar scripts externos
       ===================== */

    public static MyScript loadFromDir(String scriptsDir, String fqcn) throws Exception
    {
        File dir = new File(scriptsDir);
        URLClassLoader loader = new URLClassLoader(
                new URL[]{dir.toURI().toURL()},
                MyTaskRunner.class.getClassLoader()
        );

        Class<?> clazz = loader.loadClass(fqcn);
        return (MyScript) clazz.getDeclaredConstructor().newInstance();
    }
}
