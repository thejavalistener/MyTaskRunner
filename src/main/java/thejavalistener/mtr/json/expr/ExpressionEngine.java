package thejavalistener.mtr.json.expr;

import java.util.HashMap;
import java.util.Map;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyReflection;

public class ExpressionEngine
{
    private final Map<String, NamespaceHandler> registry = new HashMap<>();

    public ExpressionEngine register(NamespaceHandler handler)
    {
        registry.put(handler.getNamespace(), handler);
        return this;
    }

    public String resolve(String input) throws Exception
    {
        if (input == null) return null;

        String result = input;

        for (int pass = 0; pass < 10; pass++)
        {
            String before = result;
            result = resolveOnce(result);
            if (result.equals(before)) break;
        }

        return result;
    }

    private String resolveOnce(String s) throws Exception
    {
        int start = s.indexOf("${");
        if (start < 0) return s;

        StringBuilder out = new StringBuilder();
        int cursor = 0;

        while (start >= 0)
        {
            int end = s.indexOf("}", start);
            if (end < 0) break;

            out.append(s, cursor, start);

            String expr = s.substring(start + 2, end);
            out.append(evaluate(expr));

            cursor = end + 1;
            start = s.indexOf("${", cursor);
        }

        out.append(s.substring(cursor));
        return out.toString();
    }

    private String evaluate(String expr) throws Exception
    {
        int k = expr.indexOf(':');
        if (k < 0)
            throw new RuntimeException("Invalid expression (missing namespace): ${" + expr + "}");

        String namespace = expr.substring(0, k);
        String payload   = expr.substring(k + 1); // puede tener ':'

        NamespaceHandler h = registry.get(namespace);
          
        if (h == null)
            throw new RuntimeException("Unknown namespace: " + namespace);

        String r = h.resolve(payload);
        return r != null ? r : "";
    }
}