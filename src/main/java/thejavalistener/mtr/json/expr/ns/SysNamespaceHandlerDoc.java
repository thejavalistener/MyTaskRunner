package thejavalistener.mtr.json.expr.ns;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.doc.DocNamespace;
import thejavalistener.mtr.doc.DocParam;
import thejavalistener.mtr.doc.NamespaceDocOperation;

public class SysNamespaceHandlerDoc implements DocNamespace
{
	@Override
	public String getNamespace()
	{
		return "sys";
	}

	@Override
	public String getDescription()
	{
		return "Acceso a propiedades del sistema y variables de entorno";
	}

	@Override
	public List<NamespaceDocOperation> getOperations()
	{
		List<NamespaceDocOperation> ret=new ArrayList<>();

		// prop
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("key","Nombre de la system property","string",null));

			ret.add(new NamespaceDocOperation("prop","Obtiene el valor de una system property (ej: java.io.tmpdir)",params));
		}

		// env
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("var","Nombre de la variable de entorno","string",null));

			ret.add(new NamespaceDocOperation("env","Obtiene el valor de una variable de entorno",params));
		}

		return ret;
	}
}