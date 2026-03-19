package thejavalistener.mtr.actions.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.mtr.doc.DocAction;
import thejavalistener.mtr.doc.DocParam;

public abstract class MyActionDoc implements DocAction
{
	@Override
	public List<DocParam> getParams()
    {
		List<DocParam> ret = new ArrayList<>();
        ret.add(new DocParam("showProgress", "Muestra progreso", "boolean","false"));
        ret.add(new DocParam("stopScriptOnError", "Detiene el script ante error", "boolean", "true"));
        ret.add(new DocParam("ifdef", "Ejecuta sólo si la variable existe", "string", null));
        ret.add(new DocParam("ifndef", "Ejecuta sólo si la variable no existe", "string", null));
        ret.add(new DocParam("executeIf", "Condición propia de la action", "string", null));
        return ret;
    }
}
