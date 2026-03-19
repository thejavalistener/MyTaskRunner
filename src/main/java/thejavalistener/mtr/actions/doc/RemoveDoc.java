package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class RemoveDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Remove";
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"Remove", "from":"c:/tmp/a.txt"  }
				{"action":"Remove", "from":"c:/tmp/carpeta"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","Ruta del archivo o directorio a eliminar","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}