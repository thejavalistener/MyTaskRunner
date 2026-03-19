package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class ExecDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "Exec";
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"Exec", "command":"cmd /c echo hola"                }
				{"action":"Exec", "command":"cmd /c dir c:/tmp", "opts":"WAIT"}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("command","Comando a ejecutar (incluye argumentos)","string",null));
		ret.add(new DocParam("opts","Opciones de ejecución: DETACHED (default) o WAIT","string","DETACHED"));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		return List.of(); // no define propios
	}
}