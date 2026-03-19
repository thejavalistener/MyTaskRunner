package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class ZipDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Zip";
	}

	@Override
	public String getActionDescription()
	{
		return "Comprime un archivo o directorio en un archivo ZIP. Si es un directorio, incluye todo su contenido recursivamente";
	}

	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","Ruta del archivo o directorio a comprimir","path",null));
		ret.add(new DocParam("to","Ruta del archivo ZIP destino a generar","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}