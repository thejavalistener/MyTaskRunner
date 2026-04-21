package thejavalistener.mtr.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyException;
import thejavalistener.mtr.json.expr.ExpressionEngine;

public class MyScriptBase
{
	protected ScriptJson sj;
	protected ExpressionEngine engine;
	protected ScriptOptions options = new ScriptOptions();	
	protected Map<String,String> vars;
	
	/*
	 * ===================== Reflection helper =====================
	 */

	protected static java.lang.reflect.Method findSetter(Class<?> clazz, String name, Object value)
	{
		for(var m:clazz.getMethods())
		{
			if(!m.getName().equals(name)) continue;
			if(m.getParameterCount()!=1) continue;

			Class<?> pt=m.getParameterTypes()[0];

			if(value==null) return m;
			if(pt.isAssignableFrom(value.getClass())) return m;

			if(pt==boolean.class&&value instanceof Boolean) return m;
			if(pt==int.class&&value instanceof Integer) return m;
			if(pt==long.class&&value instanceof Long) return m;
		}
		return null;
	}

	protected void _validateSyntax() throws Exception
	{
		if(sj==null) return;

		// Validar vars
		if(sj.vars!=null)
		{
			for(var e:sj.vars.entrySet())
			{
				String value=e.getValue();
				if(value!=null) 
				{
					engine.resolve(value);
				}
			}
		}

		// Validar steps
		if(sj.steps!=null)
		{
			for(Map<String,Object> step:sj.steps)
			{
				for(var entry:step.entrySet())
				{
					Object raw=entry.getValue();

					if(raw instanceof String s)
					{
						engine.resolve(s);
					}
				}
			}
		}
	}

	protected boolean _evalIfVar(String expr)
	{
		// --- NOT IN ---
		if(expr.matches(".*\\s+not\\s+in\\s*\\(.*\\).*"))
		{
		    String[] p = expr.split("\\s+not\\s+in\\s*",2);

		    String left = p[0].trim();
		    String right = p[1].trim();

		    if(!right.startsWith("(") || !right.endsWith(")"))
		        throw new RuntimeException("Invalid 'not in' syntax: "+expr);

		    String values = right.substring(1, right.length()-1);

		    for(String item : values.split(","))
		    {
		        if(left.equals(item.trim()))
		            return false;
		    }
		    return true;
		}

		// --- IN ---
		if(expr.matches(".*\\s+in\\s*\\(.*\\).*"))
		{
		    String[] p = expr.split("\\s+in\\s*\\(",2);

		    String left = p[0].trim();
		    String right = "(" + p[1].trim(); // recupero '('

		    if(!right.startsWith("(") || !right.endsWith(")"))
		        throw new RuntimeException("Invalid 'in' syntax: "+expr);

		    String values = right.substring(1, right.length()-1);

		    for(String item : values.split(","))
		    {
		        if(left.equals(item.trim()))
		            return true;
		    }
		    return false;
		}
		
		if(expr.contains("=="))
		{
			String[] p = expr.split("==",2);
			return p[0].trim().equals(p[1].trim());
		}

		if(expr.contains("!="))
		{
			String[] p = expr.split("!=",2);
			return !p[0].trim().equals(p[1].trim());
		}

		if(expr.contains(">="))
		{
			String[] p = expr.split(">=",2);
			return Long.parseLong(p[0].trim()) >= Long.parseLong(p[1].trim());
		}

		if(expr.contains("<="))
		{
			String[] p = expr.split("<=",2);
			return Long.parseLong(p[0].trim()) <= Long.parseLong(p[1].trim());
		}

		if(expr.contains(">"))
		{
			String[] p = expr.split(">",2);
			return Long.parseLong(p[0].trim()) > Long.parseLong(p[1].trim());
		}

		if(expr.contains("<"))
		{
			String[] p = expr.split("<",2);
			return Long.parseLong(p[0].trim()) < Long.parseLong(p[1].trim());
		}

		throw new RuntimeException("Invalid ifvar expression: "+expr);
	}
	
	static class ScriptJson
	{
		Map<String,String> vars;
		List<Map<String,Object>> steps;
		ScriptOptions options;
	}

	
	protected void _executeAction(MyAction a) throws Exception
	{
		MyConsole console=MyConsoles.get();

		try
		{
			// presentación: Copiando D:/temp/equis a C:/unDir/zeta
			_log(a);
			
			a.checkExecuteIf();

			
			if( a.isMustSkipped() || !a.checkExecuteIf() )
			{
	            console.println("[b][fg(GREEN)]Skiped[x][x] ");
	            return;				
			}
			
			// ejecuto la acción

			if( !options.isSimulationMode() )
			{
				a.execute();
			}
			
			// exito
			console.println("[b][fg(GREEN)]OK[x][x] ");
		}
		catch(Exception e)
		{
			// error (fatal o recuperable)
			console.println("[fg(RED)][b]FAILED:[x] "+e.getMessage()+"[x] ");

			// stacktrace
			String stackTrace=MyException.stackTraceToString(e);
			console.println("[fg(RED)]"+stackTrace+"[x]");

			if(a.isStopScriptOnError())
			{
				throw new IllegalStateException(e);
			}
		}
	}

	protected void _log(MyAction a)
	{
		MyConsole console=MyConsoles.get();

		String mssg="";

		int maxLength=(int)(console.getTextPane().cols()*0.9);
		String vervo="[fg(GREEN)]"+a.getVerb()+"[x]";
		String mssgs[]=a.getDescription();
		if(mssgs.length>1)
		{
			mssg=vervo+"\n";
			for(int i=0; i<mssgs.length; i++)
			{
				mssg+="\t"+MyString.trimMiddle(mssgs[i],maxLength);
				mssg+=i<mssgs.length-1?"\n":" ";
			}
		}
		else
		{
			mssg=MyString.trimMiddle(vervo+" "+mssgs[0],maxLength)+" ";
		}

		console.print(mssg);
	}
	
}
