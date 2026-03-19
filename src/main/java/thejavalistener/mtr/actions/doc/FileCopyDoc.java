package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;

public class FileCopyDoc extends MyActionDoc
{
	@Override
	public String getActionName()
	{
		return "FileCopy";
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				{"action":"FileCopy", "from":"c:/tmp/a.txt" , "to":"c:/tmp/b.txt"    }
				{"action":"FileCopy", "from":"c:/tmp/a.txt" , "to":"c:/tmp/destino/" }
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	
	@Override
	public List<DocParam> getParams()
	{
		List<DocParam> ret=new ArrayList<>(super.getParams());
		ret.add(new DocParam("from","Ruta del archivo origen a copiar","path",null));
		ret.add(new DocParam("to","Destino. Si termina en / o se interpreta como directorio; si no, como archivo destino","path",null));
		return ret;
	}

	@Override
	public List<DocOperator> getExecuteIfOperators()
	{
		List<DocOperator> ret=new ArrayList<>();
		ret.add(new DocOperator("exists","Ejecuta si el archivo destino existe"));
		ret.add(new DocOperator("notExists","Ejecuta si el archivo destino no existe"));
		ret.add(new DocOperator("notExistsOrIsNewer","Ejecuta si el destino no existe o el origen es más nuevo"));
		return ret;
	}
}