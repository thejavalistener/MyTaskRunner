package thejavalistener.mtr.json.expr.ns;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import thejavalistener.mtr.json.expr.NamespaceHandler;
import thejavalistener.mtr.json.expr.NamespaceOperation;

public class TimeNamespaceHandler extends NamespaceHandler
{
    @Override
    public String getNamespace()
    {
        return "time";
    }

    @Override
    public String resolve(String payload) throws Exception
    {
        if (payload == null || payload.isBlank())
            throw new IllegalArgumentException("time namespace requires arguments");

        String[] parts = payload.split(":");
        String opName = parts[0];

        NamespaceOperation op = getOperation(opName);
        if (op == null)
            throw new IllegalArgumentException("Unknown time operation: " + opName);

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        return op.resolve(args);
    }

    private static String format(LocalDateTime dt, String[] args)
    {
        if (args.length == 0)
            return dt.toString();

        // patrón puede contener ':' => reconstruyo
        String pattern = String.join(":", args);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        return dt.format(fmt);
    }
    
	@Override
	public String getDocumentation(String opName)
	{
		// TODO Auto-generated method stub
		return null;
	}

    class EpochMillis implements NamespaceOperation
    {
        @Override public String getName() { return "epochMillis"; }

        @Override
        public String resolve(String[] args)
        {
            if (args.length != 0)
                throw new IllegalArgumentException("time:epochMillis takes no arguments");

            return String.valueOf(System.currentTimeMillis());
        }
    }

    class Now implements NamespaceOperation
    {
        @Override public String getName() { return "now"; }

        @Override
        public String resolve(String[] args)
        {
            return format(LocalDateTime.now(), args);
        }
    }

    class Today implements NamespaceOperation
    {
        @Override public String getName() { return "today"; }

        @Override
        public String resolve(String[] args)
        {
            return format(LocalDate.now().atStartOfDay(), args);
        }
    }

    class Tomorrow implements NamespaceOperation
    {
        @Override public String getName() { return "tomorrow"; }

        @Override
        public String resolve(String[] args)
        {
            return format(LocalDate.now().plusDays(1).atStartOfDay(), args);
        }
    }

    class Yesterday implements NamespaceOperation
    {
        @Override public String getName() { return "yesterday"; }

        @Override
        public String resolve(String[] args)
        {
            return format(LocalDate.now().minusDays(1).atStartOfDay(), args);
        }
    }
}