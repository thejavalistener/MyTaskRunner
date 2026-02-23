package thejavalistener.mtr.expr.ns;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import thejavalistener.mtr.expr.NamespaceHandler;

public class SysNamespaceHandler implements NamespaceHandler
{
    private final Map<String, NamespaceOperation> operations = new HashMap<>();

    public SysNamespaceHandler()
    {
        register(new Prop());
        register(new Env());
    }

    private void register(NamespaceOperation op)
    {
        operations.put(op.getName(), op);
    }

    @Override
    public String getNamespace()
    {
        return "sys";
    }

    @Override
    public String resolve(String payload) throws Exception
    {
        if (payload == null || payload.isBlank())
            throw new IllegalArgumentException("sys namespace requires arguments");

        String[] parts = payload.split(":");

        String opName = parts[0];

        NamespaceOperation op = operations.get(opName);

        if (op == null)
            throw new IllegalArgumentException("Unknown sys operation: " + opName);

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        return op.resolve(args);
    }

    class Prop implements NamespaceOperation
    {
        @Override
        public String getName() { return "prop"; }

        @Override
        public String resolve(String[] args)
        {
            if (args.length != 1)
                throw new IllegalArgumentException("prop requires 1 argument");

            String key = args[0];
            String val = System.getProperty(key);

            if (val == null)
                throw new IllegalArgumentException("System property not found: " + key);

            return val.replace("\\", "/");
        }
    }

    class Env implements NamespaceOperation
    {
        @Override
        public String getName() { return "env"; }

        @Override
        public String resolve(String[] args)
        {
            if (args.length != 1)
                throw new IllegalArgumentException("env requires 1 argument");

            String var = args[0];
            String val = System.getenv(var);

            if (val == null)
                throw new IllegalArgumentException("Environment variable not found: " + var);

            return val.replace("\\", "/");
        }
    }
}