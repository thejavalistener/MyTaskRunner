package thejavalistener.mtr.core;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class MyJsonScriptImple extends MyScript
{
	private final ScriptJson sj;
	private final Map<String,String> vars;

	public MyJsonScriptImple(String jsonFile) throws Exception
	{
		this(Path.of(jsonFile));
	}

	public MyJsonScriptImple(Path jsonPath) throws Exception
	{
		Gson gson=new Gson();

		try (Reader r=new FileReader(jsonPath.toFile()))
		{
			this.sj=gson.fromJson(r,ScriptJson.class);
		}

		this.vars=new HashMap<>();
		if(sj!=null&&sj.vars!=null) this.vars.putAll(sj.vars);
	}

	@Override
	public List<MyAction> script()
	{
		List<MyAction> ret = new ArrayList<>();
		
		if(sj==null||sj.steps==null) return ret;

		
		// for(Step st:sj.steps)
		for(Map<String,Object> st:sj.steps)
		{
			try
			{
				// String actionName=resolve(st.action,vars);
				String actionName=resolve((String)st.get("action"),vars);

				Class<?> clazz=Class.forName("thejavalistener.mtr.actions."+actionName);

				MyAction action=(MyAction)clazz.getDeclaredConstructor().newInstance();

				// ====== Mapeo automático de propiedades ======
//				for(var f:st.getClass().getDeclaredFields())
//				{
//					f.setAccessible(true);
//
//					String name=f.getName();
//					if("action".equals(name)) continue;
//
//					Object raw=f.get(st);
//					if(raw==null) continue;
//
//					Object value=raw;
//
//					if(raw instanceof String s) value=resolve(s,vars);
//
//					String setter="set"+Character.toUpperCase(name.charAt(0))+name.substring(1);
//
//					var m=findSetter(action.getClass(),setter,value);
//					if(m!=null) m.invoke(action,value);
//				}

				for (var entry : st.entrySet())
				{
				    String name = entry.getKey();
				    if ("action".equals(name)) continue;

				    Object raw = entry.getValue();
				    if (raw == null) continue;

				    Object value = raw;

				    if (raw instanceof String s)
				        value = resolve(s, vars);

				    String setter = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);

				    var m = findSetter(action.getClass(), setter, value);
				    if (m != null)
				        m.invoke(action, value);
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

	/*
	 * ===================== Variable resolution =====================
	 */

	private static String resolve(String s, Map<String,String> vars)
	{
		if(s==null) return null;

		for(int i=0; i<10; i++)
		{
			String before=s;
			for(var e:vars.entrySet())
				s=s.replace("${"+e.getKey()+"}",e.getValue());
			if(s.equals(before)) break;
		}
		return s;
	}

	/*
	 * ===================== POJOs JSON =====================
	 */

	static class ScriptJson
	{
		Map<String,String> vars;
		List<Map<String,Object>> steps;
	}

	// static class Step
	// {
	// String action;
	// String path;
	// String from;
	// String to;
	// String command;
	// Boolean progress;
	// }
}
