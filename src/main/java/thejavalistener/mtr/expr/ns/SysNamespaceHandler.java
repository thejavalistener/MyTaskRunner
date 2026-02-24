package thejavalistener.mtr.expr.ns;
import java.util.Arrays;

import thejavalistener.mtr.expr.NamespaceHandler;
import thejavalistener.mtr.expr.NamespaceOperation;

public class SysNamespaceHandler extends NamespaceHandler
{
    @Override
    public String getNamespace()
    {
        return "sys";
    }
    
    @Override
    public String getDocumentation(String opName)
    {
    	NamespaceOperation op = getOperation(opName);
    	
    	return null;
    }

    @Override
    public String resolve(String payload) throws Exception
    {
        if (payload == null || payload.isBlank())
            throw new IllegalArgumentException("sys namespace requires arguments");

        String[] parts = payload.split(":");

        String opName = parts[0];

        NamespaceOperation op = getOperation(opName);

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