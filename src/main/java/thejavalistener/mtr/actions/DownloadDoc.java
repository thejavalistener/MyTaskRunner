package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class DownloadDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Download";
	}

	@Override
	public String getActionDescription()
	{
		return "Descarga un archivo desde una URL y lo guarda en el sistema de archivos";
	}

	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","URL desde donde se descargará el archivo","url",null));
		ret.add(new DocParam("to","Ruta del archivo destino (incluye nombre de archivo)","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		return List.of(); // no define operadores propios
	}
}