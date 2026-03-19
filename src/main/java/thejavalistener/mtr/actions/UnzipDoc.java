package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class UnzipDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Unzip";
	}

	@Override
	public String getActionDescription()
	{
		return "Descomprime un archivo ZIP en un directorio destino, preservando la estructura interna";
	}

	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","Ruta del archivo ZIP a descomprimir","path",null));
		ret.add(new DocParam("to","Directorio destino donde se extraerán los archivos","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}