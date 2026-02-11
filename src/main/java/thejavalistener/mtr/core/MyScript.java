
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

    protected void doAction(MyAction a)
    {
    	doAction(a,false);
    }
    
    protected void doAction(MyAction a, boolean showProgress)
    {
        try
        {
            ProgressListener pl = null;

            if (showProgress)
                pl = new ConsoleProgressBar(a.getVerb(), a.getDescription());

            a.execute(pl);
        }
        catch (Exception e)
        {
            throw new RuntimeException(
                    "Failed " + a.getClass().getSimpleName(),
                    e
            );
        }
    }
}
