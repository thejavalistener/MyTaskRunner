package thejavalistener.mtr.json.expr;

import java.util.HashMap;
import java.util.Map;

import thejavalistener.fwkutils.various.MyReflection;

public abstract class NamespaceHandler
{
	public abstract String getNamespace();
	public abstract String resolve(String payload) throws Exception;

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
	
	public String getDocumentation(String ns)
	{
		return null;
	}
}