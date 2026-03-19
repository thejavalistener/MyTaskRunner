package thejavalistener.mtr.actions;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.core.MyActionDoc;
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
	public String getActionDescription()
	{
		return "Mueve un archivo o directorio desde una ubicación a otra";
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