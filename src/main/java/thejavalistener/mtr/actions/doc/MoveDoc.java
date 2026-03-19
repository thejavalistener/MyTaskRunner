package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class MoveDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Move";
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"Move", "from":"c:/tmp/a.txt"  , "to":"c:/tmp/b.txt"   }
				{"action":"Move", "from":"c:/tmp/a.txt"  , "to":"c:/tmp/"        }
				{"action":"Move", "from":"c:/tmp/carpeta", "to":"c:/tmp/carpeta2"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","Ruta del archivo o directorio origen","path",null));
		ret.add(new DocParam("to","Ruta destino (si existe será reemplazado)","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}