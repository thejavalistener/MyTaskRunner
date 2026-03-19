package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
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
	public String getActionDescription()
	{
		return "Crea un directorio y, si hace falta, también sus directorios padres";
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