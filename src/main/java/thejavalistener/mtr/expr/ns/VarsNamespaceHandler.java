package thejavalistener.mtr.expr.ns;

import thejavalistener.mtr.expr.NamespaceHandler;

import java.util.Map;

public class VarsNamespaceHandler implements NamespaceHandler
{
    private final Map<String, String> vars;

    public VarsNamespaceHandler(Map<String, String> vars)
    {
        if (vars == null)
            throw new IllegalArgumentException("vars map cannot be null");

        this.vars = vars;
    }

    @Override
    public String getNamespace()
    {
        return "vars";
    }

    @Override
    public String resolve(String payload)
    {
        if (payload == null || payload.isBlank())
            throw new RuntimeException("vars namespace requires a key");

        String key = payload.trim();

        String value = vars.get(key);

        if (value == null)
            throw new RuntimeException("Unknown var: " + key);

        return value;
    }
}