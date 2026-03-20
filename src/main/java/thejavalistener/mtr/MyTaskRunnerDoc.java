package thejavalistener.mtr;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
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

//public class MyTaskRunnerDoc
//{
//	private static MyConsole c = MyConsoles.getOnWindow("Help");
//	
//	public static final Color C_USAGE      = Color.WHITE;
//	public static final Color C_HEADER     = Color.CYAN;
//	public static final Color C_NAMESPACE  = Color.YELLOW;
//	public static final Color C_ACTION     = Color.GREEN;
//	public static final Color C_PARAMS     = Color.WHITE;
//	public static final Color C_PARAM_NAME = Color.MAGENTA;
//	public static final Color C_DESC       = Color.LIGHT_GRAY;
//	public static final Color C_EXAMPLE    = Color.LIGHT_GRAY;
//	public static final Color C_OK         = Color.GREEN;
//	public static final Color C_ERROR      = Color.RED;
//	public static final Color C_WARNING    = Color.ORANGE;
//	
//	public static void doc() throws Exception
//	{
//		c.println(C_USAGE,"Usage:");
//		c.println(C_USAGE," java -jar MyTaskRunner.jar <script.json>");
//		c.println(C_USAGE," java -jar MyTaskRunner.jar <ScriptClass>");
//		c.println(C_USAGE," java -jar MyTaskRunner.jar -?");
//		c.println();
//		_printNamespaces();
//		c.println();
//		_printActions();
//		c.print("Press any key to exit...").pressAnyKey();
//	}
//
//	// ================= NAMESPACES =================
//
//	private static void _printNamespaces()
//	{
//		c.println(C_HEADER,"=== NAMESPACES ===");
//
//		List<NamespaceHandler> handlers=new ArrayList<>();
//		handlers.add(new SysNamespaceHandler());
//		handlers.add(new TimeNamespaceHandler());
//		handlers.add(new VarNamespaceHandler());
//
//		handlers.sort(Comparator.comparing(NamespaceHandler::getNamespace));
//
//		for(NamespaceHandler h:handlers)
//		{
//			c.println("[" + h.getNamespace() + "]");
//
//			DocNamespace doc=h.getNamespaceDoc();
//
//			if(doc!=null)
//			{
//				List<NamespaceDocOperation> ops=new ArrayList<>(doc.getOperations());
//				ops.sort(Comparator.comparing(o->o.name));
//
//				for(NamespaceDocOperation op:ops)
//				{
//					c.println("  - " + op.name);
//
//					if(op.params!=null && !op.params.isEmpty())
//					{
//						c.println("      params:");
//						for(DocParam p:op.params)
//						{
//							_printParam(p,"        ");
//						}
//					}
//				}
//			}
//			else
//			{
//				List<NamespaceOperation> ops=new ArrayList<>(h.getOperations().values());
//				ops.sort(Comparator.comparing(NamespaceOperation::getName));
//
//				for(NamespaceOperation op:ops)
//				{
//					c.println("  - " + op.getName());
//				}
//			}
//
//			c.println();
//		}
//	}
//
//	// ================= ACTIONS =================
//
//	private static void _printActions() throws Exception
//	{
//		c.println(C_HEADER,"=== ACTIONS ===");
//
//		List<String> names=new ArrayList<>(ActionRegistry.getNames());
//		names.sort(String::compareToIgnoreCase);
//
//		for(String name:names)
//		{
//			MyAction action=ActionRegistry.create(name);
//
//			c.println("[" + name + "]");
//			
//			DocAction doc=action.getActionDoc();
//
//			if(doc!=null)
//			{
//				List<DocParam> params=doc.getParams();
//				if(params!=null && !params.isEmpty())
//				{
//					c.println("  params:");
//					for(DocParam p:params)
//					{
//						_printParam(p,"    ");
//					}
//				}
//
//				List<DocOperator> ops=doc.getExecuteIfOperators();
//				if(ops!=null && !ops.isEmpty())
//				{
//					c.println("  executeIf:");
//					for(DocOperator op:ops)
//					{
//						c.println("    - " + op.name);
//						_printText(op.description,"        ");
//					}
//				}
//
//				List<String> examples=doc.getExamples();
//				if(examples!=null && !examples.isEmpty())
//				{
//					c.println("  examples:");
//					for(String ex:examples)
//					{
//						if(ex!=null && !ex.isBlank())
//						{
//							c.println("    " + ex.trim());
//						}
//					}
//				}
//			}
//			else
//			{
//				for(var m:action.getClass().getMethods())
//				{
//					if(!m.getName().startsWith("set")) continue;
//					if(m.getParameterCount()!=1) continue;
//
//					String p=m.getName().substring(3);
//					p=Character.toLowerCase(p.charAt(0))+p.substring(1);
//
//					String type=m.getParameterTypes()[0].getSimpleName();
//
//					c.println("  - " + p + " : " + type);
//				}
//			}
//
//			c.println();
//		}
//	}
//
//	// ================= HELPERS =================
//
//	private static void _printParam(DocParam p,String indent)
//	{
//		String line=indent + "- " + p.name + " : " + p.type;
//
//		if(p.defaultValue!=null && !p.defaultValue.isBlank())
//			line+=" (default=" + p.defaultValue + ")";
//
//		c.println(line);
//		_printText(p.description,indent+"    ");
//	}
//
//	private static void _printText(String text,String indent)
//	{
//		if(text==null||text.isBlank()) return;
//
//		for(String line:text.split("\\R"))
//		{
//			c.println(indent+line);
//		}
//	}
//}

public class MyTaskRunnerDoc
{
	private static MyConsole c = MyConsoles.getOnWindow("Help");
	
	public static final Color C_USAGE      = Color.WHITE;
	public static final Color C_HEADER     = Color.CYAN;
	public static final Color C_NAMESPACE  = Color.YELLOW;
	public static final Color C_ACTION     = Color.GREEN;
	public static final Color C_PARAMS     = Color.WHITE;
	public static final Color C_PARAM_NAME = Color.MAGENTA;
	public static final Color C_DESC       = Color.LIGHT_GRAY;
	public static final Color C_EXAMPLE    = Color.LIGHT_GRAY;
	public static final Color C_OK         = Color.GREEN;
	public static final Color C_ERROR      = Color.RED;
	public static final Color C_WARNING    = Color.ORANGE;
	
	public static void doc() throws Exception
	{
		c.println(C_USAGE,"Usage:");
		c.println(C_USAGE," java -jar MyTaskRunner.jar <script.json>");
		c.println(C_USAGE," java -jar MyTaskRunner.jar <ScriptClass>");
		c.println(C_USAGE," java -jar MyTaskRunner.jar -?");
		c.println();
		_printNamespaces();
		c.println();
		_printActions();
		c.print(C_USAGE,"Press any key to exit...").pressAnyKey();
	}

	// ================= NAMESPACES =================

	private static void _printNamespaces()
	{
		c.println(C_HEADER,"=== NAMESPACES ===");

		List<NamespaceHandler> handlers=new ArrayList<>();
		handlers.add(new SysNamespaceHandler());
		handlers.add(new TimeNamespaceHandler());
		handlers.add(new VarNamespaceHandler());

		handlers.sort(Comparator.comparing(NamespaceHandler::getNamespace));

		for(NamespaceHandler h:handlers)
		{
			c.println(C_NAMESPACE,"[" + h.getNamespace() + "]");

			DocNamespace doc=h.getNamespaceDoc();

			if(doc!=null)
			{
				List<NamespaceDocOperation> ops=new ArrayList<>(doc.getOperations());
				ops.sort(Comparator.comparing(o->o.name));

				for(NamespaceDocOperation op:ops)
				{
					c.println(C_ACTION,"  - " + op.name);

					if(op.params!=null && !op.params.isEmpty())
					{
						c.println(C_PARAMS,"      params:");
						for(DocParam p:op.params)
						{
							_printParam(p,"        ");
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
					c.println(C_ACTION,"  - " + op.getName());
				}
			}

			c.println();
		}
	}

	// ================= ACTIONS =================

	private static void _printActions() throws Exception
	{
		c.println(C_HEADER,"=== ACTIONS ===");

		List<String> names=new ArrayList<>(ActionRegistry.getNames());
		names.sort(String::compareToIgnoreCase);

		for(String name:names)
		{
			MyAction action=ActionRegistry.create(name);

			c.println(C_NAMESPACE,"[" + name + "]");
			
			DocAction doc=action.getActionDoc();

			if(doc!=null)
			{
				List<DocParam> params=doc.getParams();
				if(params!=null && !params.isEmpty())
				{
					c.println(C_PARAMS,"  params:");
					for(DocParam p:params)
					{
						_printParam(p,"    ");
					}
				}

				List<DocOperator> ops=doc.getExecuteIfOperators();
				if(ops!=null && !ops.isEmpty())
				{
					c.println(C_PARAMS,"  executeIf:");
					for(DocOperator op:ops)
					{
						c.println(C_ACTION,"    - " + op.name);
						_printText(op.description,"        ");
					}
				}

				List<String> examples=doc.getExamples();
				if(examples!=null && !examples.isEmpty())
				{
					c.println(C_PARAMS,"  examples:");
					for(String ex:examples)
					{
						if(ex!=null && !ex.isBlank())
						{
							c.println(C_EXAMPLE,"    " + ex.trim());
						}
					}
				}
			}
			else
			{
				for(var m:action.getClass().getMethods())
				{
					if(!m.getName().startsWith("set")) continue;
					if(m.getParameterCount()!=1) continue;

					String p=m.getName().substring(3);
					p=Character.toLowerCase(p.charAt(0))+p.substring(1);

					String type=m.getParameterTypes()[0].getSimpleName();

					c.println(C_PARAM_NAME,"  - " + p + " : " + type);
				}
			}

			c.println();
		}
	}

	// ================= HELPERS =================

	private static void _printParam(DocParam p,String indent)
	{
		String line=indent + "- " + p.name + " : " + p.type;

		if(p.defaultValue!=null && !p.defaultValue.isBlank())
			line+=" (default=" + p.defaultValue + ")";

		c.println(C_PARAM_NAME,line);
		_printText(p.description,indent+"    ");
	}

	private static void _printText(String text,String indent)
	{
		if(text==null||text.isBlank()) return;

		for(String line:text.split("\\R"))
		{
			c.println(C_DESC,indent+line);
		}
	}
}