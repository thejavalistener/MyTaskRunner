package thejavalistener.mtr.core;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import thejavalistener.mtr.expr.ExpressionEngine;
import thejavalistener.mtr.expr.ns.SysNamespaceHandler;
import thejavalistener.mtr.expr.ns.TimeNamespaceHandler;
import thejavalistener.mtr.expr.ns.VarNamespaceHandler;

public class MyJsonScriptImple extends MyScript
{
    private final ScriptJson sj;
    private final Map<String,String> vars;
    private final String jsonFile;

//    private final ExpressionEngine engine;
    private final ExpressionEngine engine;

    public MyJsonScriptImple(String jsonFile) throws Exception
    {
        this(Path.of(jsonFile));
    }

    @Override
    public String getScriptName()
    {
        return jsonFile;
    }

    public MyJsonScriptImple(Path jsonPath) throws Exception
    {
        this.jsonFile = jsonPath.getFileName().toString();

        Gson gson = new Gson();

        try (Reader r = new FileReader(jsonPath.toFile()))
        {
            this.sj = gson.fromJson(r, ScriptJson.class);
        }

        this.vars = new HashMap<>();
        if (sj != null && sj.vars != null)
            this.vars.putAll(sj.vars);

	      this.engine = new ExpressionEngine()
		      .register(new SysNamespaceHandler())
		      .register(new TimeNamespaceHandler())
		      .register(new VarNamespaceHandler().setVars(vars));
    }

    @Override
    public List<MyAction> getScriptActions()
    {
        List<MyAction> ret = new ArrayList<>();

        if (sj == null || sj.steps == null)
            return ret;

        for (Map<String,Object> st : sj.steps)
        {
            try
            {
                String actionName = engine.resolve((String) st.get("action"));

                Class<?> clazz =
                        Class.forName("thejavalistener.mtr.actions." + actionName);

                MyAction action =
                        (MyAction) clazz.getDeclaredConstructor().newInstance();

                for (var entry : st.entrySet())
                {
                    String name = entry.getKey();
                    if ("action".equals(name)) continue;

                    Object raw = entry.getValue();
                    if (raw == null) continue;

                    Object value = raw;

                    if (raw instanceof String s)
                        value = engine.resolve(s);

                    String setter =
                            "set" + Character.toUpperCase(name.charAt(0))
                                    + name.substring(1);

                    var m = findSetter(action.getClass(), setter, value);

                    if (m != null)
                        m.invoke(action, value);
                }

                ret.add(action);
            }
            catch (Exception e)
            {
                throw new RuntimeException(
                        "Error ejecutando acción: " + st.get("action"), e);
            }
        }

        return ret;
    }

    /*
     * ===================== Reflection helper =====================
     */

    private static java.lang.reflect.Method findSetter(
            Class<?> clazz, String name, Object value)
    {
        for (var m : clazz.getMethods())
        {
            if (!m.getName().equals(name)) continue;
            if (m.getParameterCount() != 1) continue;

            Class<?> pt = m.getParameterTypes()[0];

            if (value == null) return m;
            if (pt.isAssignableFrom(value.getClass())) return m;

            if (pt == boolean.class && value instanceof Boolean) return m;
            if (pt == int.class && value instanceof Integer) return m;
            if (pt == long.class && value instanceof Long) return m;
        }
        return null;
    }
    
    @Override
    public void validateSyntax() throws Exception
    {
        if (sj == null)
            return;

        // Validar vars
        if (sj.vars != null)
        {
            for (var e : sj.vars.entrySet())
            {
                String value = e.getValue();
                if (value != null)
                    engine.resolve(value);
            }
        }

        // Validar steps
        if (sj.steps != null)
        {
            for (Map<String,Object> step : sj.steps)
            {
                for (var entry : step.entrySet())
                {
                    Object raw = entry.getValue();

                    if (raw instanceof String s)
                    {
                        engine.resolve(s);
                    }
                }
            }
        }
    }    

    static class ScriptJson
    {
        Map<String,String> vars;
        List<Map<String,Object>> steps;
    }
}