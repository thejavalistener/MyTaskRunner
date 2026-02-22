package thejavalistener.mtr.expr.ns;

import thejavalistener.mtr.expr.NamespaceHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SysNamespaceHandler implements NamespaceHandler
{
    private final Map<String, Function<String[], String>> operations = new HashMap<>();

    public SysNamespaceHandler()
    {
        // sys:prop:user.home[:default]
        operations.put("prop", args ->
        {
            if (args.length < 1)
                throw new RuntimeException("sys:prop requires a key");

            String key = args[0];
            String def = args.length >= 2 ? args[1] : "";

            String val = System.getProperty(key, def);
            if (val == null) val = "";

            return val.replace("\\", "/");
        });

        // sys:env:PATH[:default]
        operations.put("env", args ->
        {
            if (args.length < 1)
                throw new RuntimeException("sys:env requires a variable name");

            String key = args[0];
            String def = args.length >= 2 ? args[1] : "";

            String val = System.getenv(key);
            if (val == null) val = def;

            if (val == null) val = "";

            return val.replace("\\", "/");
        });

        // atajo opcional
        operations.put("home", args ->
                System.getProperty("user.home", "").replace("\\", "/")
        );
    }

    @Override
    public String getNamespace()
    {
        return "sys";
    }

    @Override
    public String resolve(String payload)
    {
        if (payload == null || payload.isBlank())
            throw new RuntimeException("sys namespace requires arguments");

        String[] parts = payload.split(":");
        String op = parts[0];

        Function<String[], String> fn = operations.get(op);

        if (fn == null)
            throw new RuntimeException("Unknown sys operation: " + op);

        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        return fn.apply(args);
    }
}