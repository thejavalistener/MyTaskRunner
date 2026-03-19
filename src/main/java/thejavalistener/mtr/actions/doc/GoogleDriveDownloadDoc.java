package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
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
	public List<String> getExamples()
	{
		String examples="""
				{"action":"GoogleDriveDownload", "from":"https://drive.google.com/file/d/1AbYz/view?usp=sharing", "to":"c:/tmp/archivo.zip" }
				{"action":"GoogleDriveDownload", "from":"https://drive.google.com/open?id=1AvWxYz"              , "to":"c:/tmp/descarga.bin"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
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