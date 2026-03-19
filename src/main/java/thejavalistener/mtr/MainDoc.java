package thejavalistener.mtr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import thejavalistener.mtr.core.ActionRegistry;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.doc.DocAction;
import thejavalistener.mtr.doc.DocNamespace;
import thejavalistener.mtr.doc.DocOperator;
import thejavalistener.mtr.doc.DocParam;
import thejavalistener.mtr.doc.NamespaceDocOperation;
import thejavalistener.mtr.json.expr.NamespaceHandler;
import thejavalistener.mtr.json.expr.NamespaceOperation;
import thejavalistener.mtr.json.expr.ns.SysNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.TimeNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.VarNamespaceHandler;

public class MainDoc
{
	public static void main(String[] args) throws Exception
	{
		printNamespaces();
		System.out.println();
		printActions();
	}

	// ================= NAMESPACES =================

	private static void printNamespaces()
	{
		System.out.println("=== NAMESPACES ===");

		List<NamespaceHandler> handlers=new ArrayList<>();
		handlers.add(new SysNamespaceHandler());
		handlers.add(new TimeNamespaceHandler());
		handlers.add(new VarNamespaceHandler());

		handlers.sort(Comparator.comparing(NamespaceHandler::getNamespace));

		for(NamespaceHandler h:handlers)
		{
			System.out.println("[" + h.getNamespace() + "]");

			DocNamespace doc = resolveNamespaceDoc(h);

			if(doc!=null)
			{
				printText(doc.getDescription(),"  ");

				List<NamespaceDocOperation> ops=new ArrayList<>(doc.getOperations());
				ops.sort(Comparator.comparing(o->o.name));

				for(NamespaceDocOperation op:ops)
				{
					System.out.println("  - " + op.name);
					printText(op.description,"      ");

					if(op.params!=null && !op.params.isEmpty())
					{
						for(DocParam p:op.params)
						{
							printParam(p,"        ");
						}
					}
				}
			}
			else
			{
				List<NamespaceOperation> ops=new ArrayList<>(h.getOperations().values());
				ops.sort(Comparator.comparing(NamespaceOperation::getName));

				for(NamespaceOperation op:ops)
				{
					System.out.println("  - " + op.getName());
				}
			}

			System.out.println();
		}
	}

	// ================= ACTIONS =================

	private static void printActions() throws Exception
	{
		System.out.println("=== ACTIONS ===");

		List<String> names=new ArrayList<>(ActionRegistry.getNames());
		names.sort(String::compareToIgnoreCase);

		for(String name:names)
		{
			MyAction action=ActionRegistry.create(name);

			System.out.println("[" + name + "]");

			DocAction doc = resolveActionDoc(action);

			if(doc!=null)
			{
				printText(doc.getActionDescription(),"  ");

				List<DocParam> params=doc.getParams();
				if(params!=null && !params.isEmpty())
				{
					System.out.println("  params:");
					for(DocParam p:params)
					{
						printParam(p,"    ");
					}
				}

				List<DocOperator> ops=doc.getExecuteIfOperators();
				if(ops!=null && !ops.isEmpty())
				{
					System.out.println("  executeIf:");
					for(DocOperator op:ops)
					{
						System.out.println("    - " + op.name);
						printText(op.description,"        ");
					}
				}
			}
			else
			{
				// fallback reflection
				for(var m:action.getClass().getMethods())
				{
					if(!m.getName().startsWith("set")) continue;
					if(m.getParameterCount()!=1) continue;

					String p=m.getName().substring(3);
					p=Character.toLowerCase(p.charAt(0))+p.substring(1);

					String type=m.getParameterTypes()[0].getSimpleName();

					System.out.println("  - " + p + " : " + type);
				}
			}

			System.out.println();
		}
	}

	// ================= DOC RESOLUTION =================

	private static DocAction resolveActionDoc(MyAction action)
	{
		try
		{
			String docClassName = action.getClass().getName() + "Doc";
			Class<?> docClass = Class.forName(docClassName);
			return (DocAction) docClass.getDeclaredConstructor().newInstance();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	private static DocNamespace resolveNamespaceDoc(NamespaceHandler h)
	{
		try
		{
			String docClassName = h.getClass().getName() + "Doc";
			Class<?> docClass = Class.forName(docClassName);
			return (DocNamespace) docClass.getDeclaredConstructor().newInstance();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	// ================= HELPERS =================

	private static void printParam(DocParam p,String indent)
	{
		String line=indent + "- " + p.name + " : " + p.type;

		if(p.defaultValue!=null && !p.defaultValue.isBlank())
			line+=" (default=" + p.defaultValue + ")";

		System.out.println(line);
		printText(p.description,indent+"    ");
	}

	private static void printText(String text,String indent)
	{
		if(text==null||text.isBlank()) return;

		for(String line:text.split("\\R"))
		{
			System.out.println(indent+line);
		}
	}
}