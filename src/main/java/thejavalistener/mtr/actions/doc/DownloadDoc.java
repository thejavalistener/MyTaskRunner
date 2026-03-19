package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
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
	public List<String> getExamples()
	{
		String examples="""
				{"action":"Download", "from":"https://example.com/a.zip"  , "to":"c:/tmp/a.zip"     }
				{"action":"Download", "from":"https://example.com/img.png", "to":"c:/tmp/imagen.png"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
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