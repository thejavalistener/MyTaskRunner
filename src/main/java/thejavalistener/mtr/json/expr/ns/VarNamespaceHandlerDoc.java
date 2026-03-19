package thejavalistener.mtr.json.expr.ns;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.doc.DocNamespace;
import thejavalistener.mtr.doc.DocParam;
import thejavalistener.mtr.doc.NamespaceDocOperation;

public class VarNamespaceHandlerDoc implements DocNamespace
{
	@Override
	public String getNamespace()
	{
		return "var";
	}

	@Override
	public String getDescription()
	{
		return "Acceso a variables definidas en el script";
	}

	@Override
	public List<NamespaceDocOperation> getOperations()
	{
		List<NamespaceDocOperation> ret=new ArrayList<>();

		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("key","Nombre de la variable","string",null));

			ret.add(new NamespaceDocOperation("get","Obtiene el valor de una variable definida en 'vars'. También puede usarse en forma corta: ${var:nombre}",params));
		}

		return ret;
	}
}
