package thejavalistener.mtr.json.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import thejavalistener.fwkutils.various.MyReflection;
import thejavalistener.mtr.doc.DocNamespace;

public abstract class NamespaceHandler
{
	public abstract String getNamespace();
	public abstract String resolve(String payload) throws Exception;
	public abstract DocNamespace getNamespaceDoc();
	
	
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
	
	public Map<String,NamespaceOperation> getOperations()
	{
		return operations;
	}
}