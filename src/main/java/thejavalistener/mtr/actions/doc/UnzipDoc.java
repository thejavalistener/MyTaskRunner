package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
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
	public List<String> getExamples()
	{
		String examples="""
				{"action":"Unzip", "from":"c:/tmp/out.zip"   , "to":"c:/tmp/destino"   }
				{"action":"Unzip", "from":"c:/tmp/backup.zip", "to":"c:/tmp/restaurado"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
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