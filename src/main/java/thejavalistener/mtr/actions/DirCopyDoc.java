package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
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
	public String getActionDescription()
	{
		return "Crea una copia de un directorio";
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
