package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class MkDirDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "MkDir";
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"MkDir", "to":"c:/tmp/nuevaCarpeta" }
				{"action":"MkDir", "to":"c:/tmp/base/sub/sub2"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("to","Ruta del directorio a crear","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		return ret;
	}
}