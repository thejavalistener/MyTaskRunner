package thejavalistener.mtr.expr.ns;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import thejavalistener.mtr.expr.NamespaceHandler;

public class VarNamespaceHandler implements NamespaceHandler
{
    private final Map<String, String> vars;
    private final Map<String, NamespaceOperation> operations = new HashMap<>();

    public VarNamespaceHandler(Map<String, String> vars)
    {
        if (vars == null)
            throw new IllegalArgumentException("vars map cannot be null");

        this.vars = vars;

        register(new Get());
    }

    private void register(NamespaceOperation op)
    {
        operations.put(op.getName(), op);
    }

    @Override
    public String getNamespace()
    {
        return "var";
    }

    @Override
    public String resolve(String payload) throws Exception
    {
        if (payload == null || payload.isBlank())
            throw new IllegalArgumentException("var namespace requires arguments");

        String[] parts = payload.split(":");

        // soporte compatibilidad: var:directorio  => get implícito
        if (parts.length == 1)
            return operations.get("get").resolve(new String[]{parts[0]});

        String opName = parts[0];

        NamespaceOperation op = operations.get(opName);

        if (op == null)
            throw new IllegalArgumentException("Unknown var operation: " + opName);

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        return op.resolve(args);
    }

    class Get implements NamespaceOperation
    {
        @Override
        public String getName()
        {
            return "get";
        }

        @Override
        public String resolve(String[] args)
        {
            if (args.length != 1)
                throw new IllegalArgumentException("var:get requires 1 argument");

            String key = args[0].trim();

            String value = vars.get(key);

            if (value == null)
                throw new IllegalArgumentException("Unknown var: " + key);

            return value;
        }
    }

    public interface NamespaceOperation
    {
        String getName();
        String resolve(String[] args) throws Exception;
    }
}