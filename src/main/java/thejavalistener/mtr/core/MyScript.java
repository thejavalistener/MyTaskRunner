
package thejavalistener.mtr.core;

public abstract class MyScript
{
    public abstract int script();

    public int run()
    {
        try
        {
            return script();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            return MyAction.ERROR;
        }
    }

    /* =====================
       Helpers opcionales
       ===================== */

    protected int doAction(MyAction a)
    {
        int r = a.run();
        if (r != MyAction.SUCCESS)
        {
            throw new RuntimeException("Falló " + a.getClass().getSimpleName() + " (code=" + r + ")");
        }
        return r;
    }
}
