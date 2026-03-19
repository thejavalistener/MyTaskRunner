package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.actions.DirCopy;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class DirCopyDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return DirCopy.class.getSimpleName();
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"DirCopy", "from":"c:/tmp/origen"  , "to":"c:/tmp/destino"}
				{"action":"DirCopy", "from":"c:/tmp/proyecto", "to":"d:/backup"     }
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret = super.getParams();
		ret.add(new DocParam("from","Directorio a copiar","String"));
		ret.add(new DocParam("to","Directorio base dentro del que se replicará ${from}","String"));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		return new ArrayList<>();
	}
}
