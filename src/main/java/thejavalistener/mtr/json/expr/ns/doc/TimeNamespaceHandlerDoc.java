package thejavalistener.mtr.json.expr.ns.doc;

import java.util.ArrayList;
import java.util.List;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.mtr.doc.DocNamespace;
import thejavalistener.mtr.doc.DocParam;
import thejavalistener.mtr.doc.NamespaceDocOperation;

public class TimeNamespaceHandlerDoc implements DocNamespace
{
	@Override
	public String getNamespace()
	{
		return "time";
	}

	@Override
	public String getDescription()
	{
		return "Funciones relacionadas con fecha y hora (actual, relativa o epoch)";
	}

	@Override
	public List<NamespaceDocOperation> getOperations()
	{
		List<NamespaceDocOperation> ret=new ArrayList<>();

		// epochMillis
		{
			List<DocParam> params=new ArrayList<>();
			ret.add(new NamespaceDocOperation("epochMillis","Devuelve el tiempo actual en milisegundos desde epoch (no recibe parámetros)",params));
		}

		// now
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("pattern","Patrón opcional de formato (DateTimeFormatter)","string",""));
			ret.add(new NamespaceDocOperation("now","Devuelve la fecha y hora actual. Si se especifica patrón, formatea la salida",params));
		}

		// today
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("pattern","Patrón opcional de formato (DateTimeFormatter)","string",""));
			ret.add(new NamespaceDocOperation("today","Devuelve la fecha de hoy (00:00). Puede formatearse",params));
		}

		// tomorrow
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("pattern","Patrón opcional de formato (DateTimeFormatter)","string",""));
			ret.add(new NamespaceDocOperation("tomorrow","Devuelve la fecha de mañana (00:00). Puede formatearse",params));
		}

		// yesterday
		{
			List<DocParam> params=new ArrayList<>();
			params.add(new DocParam("pattern","Patrón opcional de formato (DateTimeFormatter)","string",""));
			ret.add(new NamespaceDocOperation("yesterday","Devuelve la fecha de ayer (00:00). Puede formatearse",params));
		}

		return ret;
	}

	@Override
	public List<String> getExamples()
	{
		String examples="""
				${time:epochMillis}
				${time:now}
				${time:now:yyyyMMdd_HHmm}
				${time:today:yyyy-MM-dd}
				${time:tomorrow}
				${time:yesterday:yyyy-MM}
				""";
		return new ArrayList<>(MyString.split(examples,"\n"));
	}
	
}