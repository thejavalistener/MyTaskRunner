package thejavalistener.mtr.json.expr.ns;

import java.util.Arrays;
import java.util.Map;

import thejavalistener.mtr.doc.DocNamespace;
import thejavalistener.mtr.json.expr.NamespaceHandler;
import thejavalistener.mtr.json.expr.NamespaceOperation;
import thejavalistener.mtr.json.expr.ns.doc.VarNamespaceHandlerDoc;

public class VarNamespaceHandler extends NamespaceHandler
{
	private Map<String,String> vars=null;

	public VarNamespaceHandler setVars(Map<String,String> vars)
	{
		this.vars=vars;
		return this;
	}

	@Override
	public String getNamespace()
	{
		return "var";
	}
	
    @Override
    public DocNamespace getNamespaceDoc()
    {
    	return new VarNamespaceHandlerDoc();
    }

	@Override
	public String resolve(String payload) throws Exception
	{
		if(payload==null||payload.isBlank()) throw new IllegalArgumentException("var namespace requires arguments");

		String[] parts=payload.split(":");

		// soporte compatibilidad: var:directorio => get implícito
		if(parts.length==1) return getOperation("get").resolve(new String[] {parts[0]});

		String opName=parts[0];

		NamespaceOperation op=getOperation(opName);

		if(op==null) throw new IllegalArgumentException("Unknown var operation: "+opName);

		String[] args=Arrays.copyOfRange(parts,1,parts.length);

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
			if(args.length!=1) throw new IllegalArgumentException("var:get requires 1 argument");

			String key=args[0].trim();

			String value=vars.get(key);

			if(value==null) throw new IllegalArgumentException("Unknown var: "+key);

			return value;
		}
	}
}