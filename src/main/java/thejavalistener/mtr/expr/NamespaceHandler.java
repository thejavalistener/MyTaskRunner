package thejavalistener.mtr.expr;

import java.util.HashMap;
import java.util.Map;

import thejavalistener.fwkutils.various.MyReflection;
import thejavalistener.mtr.expr.ns.NamespaceOperation;

public abstract class NamespaceHandler
{
	public abstract String getNamespace();
	public abstract String resolve(String payload) throws Exception;
	public abstract String getDocumentation(String opName);

	private Map<String,NamespaceOperation> operations=new HashMap<>();

	public NamespaceHandler()
	{
		operations=new HashMap<>();

		for(Class<?> inner:MyReflection.clasz.getInnerClasses(getClass()))
		{
			NamespaceOperation nso=(NamespaceOperation)MyReflection.clasz.innerClassNewInstance(this,inner);
			operations.put(nso.getName(),nso);
		}
	}
	
	public NamespaceOperation getOperation(String opName)
	{
		return operations.get(opName);
	}

}