package thejavalistener.mtr.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.json.expr.ExpressionEngine;
import thejavalistener.mtr.json.expr.ns.SysNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.TimeNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.VarNamespaceHandler;

public class MyJsonScriptImple extends MyScript
{
	private final ScriptJson sj;
	private final Map<String,String> vars;
	private final String jsonFile;
	
	private ExpressionEngine engine;

	public MyJsonScriptImple(String jsonFile) throws Exception
	{
		this(Path.of(jsonFile));
	}

	public MyJsonScriptImple(Path jsonPath) throws Exception
	{
	    this.jsonFile = jsonPath.getFileName().toString();

	    String raw = Files.readString(jsonPath);
	    raw = MyString.removeLinesWithPrefix(raw, new String[]{"--","//","#"}, true);

	    Gson gson = new Gson();
	    this.sj = gson.fromJson(raw, ScriptJson.class);

	    this.vars = new HashMap<>();
	    if (sj != null && sj.vars != null)
	        this.vars.putAll(sj.vars);

	    engine = new ExpressionEngine();
	    engine.register(new SysNamespaceHandler());
	    engine.register(new TimeNamespaceHandler());
	    engine.register(new VarNamespaceHandler().setVars(vars));
	}
	
	@Override
	public String getScriptName()
	{
		return jsonFile;
	}

	@Override
	public List<MyAction> getScriptActions()
	{
		List<MyAction> ret=new ArrayList<>();

		if(sj==null||sj.steps==null) return ret;

		for(Map<String,Object> st:sj.steps)
		{
			try
			{
				String actionName=engine.resolve((String)st.get("action"));

				Class<?> clazz=Class.forName("thejavalistener.mtr.actions."+actionName);

				MyAction action=(MyAction)clazz.getDeclaredConstructor().newInstance();

				boolean mustSkipped=false;

				// evalúo ifdef
				Object ifdefRaw=st.get("ifdef");
				if(!mustSkipped && ifdefRaw instanceof String s)
				{
					String ifdef=engine.resolve(s);
					if(!vars.containsKey(ifdef)) mustSkipped=true;
				}

				// evalúo ifndef
				Object ifndefRaw=st.get("ifndef");
				if(!mustSkipped && ifndefRaw instanceof String s)
				{
					String ifndef=engine.resolve(s);
					if(vars.containsKey(ifndef)) mustSkipped=true;
				}
				
				// evalúo ifvar
				Object ifVarRaw = st.get("ifvar");
				if(!mustSkipped && ifVarRaw instanceof String s)
				{
					String expr = engine.resolve(s).trim();

					if(!_evalIfVar(expr))
					{
						mustSkipped = true;						
					}
				}
				
//				// evalúo executeIf
//				Object doExecuteIfRaw=st.get("executeIf");
//				if(!mustSkipped && doExecuteIfRaw instanceof String s)
//				{
//					String executeIf=engine.resolve(s);
//					if(!Boolean.parseBoolean(executeIf)) mustSkipped=true;
//				}

				action.setMustSkipped(mustSkipped);

				for(var entry:st.entrySet())
				{
					String name=entry.getKey();
					if("action".equals(name)) continue;
					if("ifdef".equals(name)) continue;
					if("ifndef".equals(name)) continue;
					//if("executeIf".equals(name)) continue;
				    if("ifvar".equals(name)) continue;   // ← AGREGAR ESTO

					Object raw=entry.getValue();
					if(raw==null) continue;

					Object value=raw;

					if(raw instanceof String s) value=engine.resolve(s);

					String setter="set"+Character.toUpperCase(name.charAt(0))+name.substring(1);

					var m=findSetter(action.getClass(),setter,value);

					if(m!=null) m.invoke(action,value);
				}

				ret.add(action);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Error ejecutando acción: "+st.get("action"),e);
			}
		}

		return ret;
	}	
	
	
	/*
	 * ===================== Reflection helper =====================
	 */

	private static java.lang.reflect.Method findSetter(Class<?> clazz, String name, Object value)
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

	@Override
	public void validateSyntax() throws Exception
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

	private boolean _evalIfVar(String expr)
	{
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
	}
}