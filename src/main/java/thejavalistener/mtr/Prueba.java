package thejavalistener.mtr;

import java.util.List;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyJsonScriptImple;
import thejavalistener.mtr.core.MyScript;

public class Prueba
{
	public static void main(String[] args) throws Exception
	{
	    MyScript s = new MyJsonScriptImple("test_ifvar.json");

	    List<MyAction> actions = s.getScriptActions();

	    for(MyAction a: actions)
	    {
	        System.out.println(
	            a.getClass().getSimpleName()
	            +"  skipped="+a.isMustSkipped()
	        );
	    }
	}
}
