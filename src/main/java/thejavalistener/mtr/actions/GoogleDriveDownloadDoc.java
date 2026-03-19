package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class GoogleDriveDownloadDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "GoogleDriveDownload";
	}

	@Override
	public String getActionDescription()
	{
		return "Descarga un archivo desde Google Drive a partir de un link público";
	}

	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","URL pública de Google Drive (formato /file/d/... o ?id=...)","url",null));
		ret.add(new DocParam("to","Ruta del archivo destino donde se guardará la descarga","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}