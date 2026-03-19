package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
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
	public List<String> getExamples()
	{
		String examples = """
				{"action":"Zip", "from":"c:/tmp/origen", "to":"c:/tmp/out.zip"}
				{"action":"Zip", "from":"c:/tmp/a.txt" , "to":"c:/tmp/a.zip"  }
				{"action":"Zip", "from":"${sys:prop:user.home}/bkp_${time:now:yyyyMMdd-HHmm}.zip"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
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