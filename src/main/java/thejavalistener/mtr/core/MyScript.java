package thejavalistener.mtr.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyException;
import thejavalistener.fwkutils.various.Pair;
import thejavalistener.mtr.json.expr.ExpressionEngine;
import thejavalistener.mtr.json.expr.ns.SysNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.TimeNamespaceHandler;
import thejavalistener.mtr.json.expr.ns.VarNamespaceHandler;

public class MyScript extends MyScriptBase
{
	public static final int SUCCESS=0;
	public static final int ERROR=1;
	
	private final String jsonFile;

	public MyScript(String jsonFile) throws Exception
	{
		this(Path.of(jsonFile));
	}

	public MyScript(Path jsonPath) throws Exception
	{
	    this.jsonFile = jsonPath.getFileName().toString();

	    String raw = Files.readString(jsonPath);
	    raw = MyString.removeLinesWithPrefix(raw, new String[]{"--","//","#"}, true);

	    Gson gson = new Gson();
	    this.sj = gson.fromJson(raw, ScriptJson.class);

	    this.options = sj != null && sj.options != null ? sj.options : new ScriptOptions();

	    this.vars = new HashMap<>();
	    if(sj != null && sj.vars != null)
	    {
	        this.vars.putAll(sj.vars);
	    }
	    
	    engine = new ExpressionEngine();
	    engine.register(new SysNamespaceHandler());
	    engine.register(new TimeNamespaceHandler());
	    engine.register(new VarNamespaceHandler().setVars(vars));
	}
	
	public String getScriptName()
	{
		return jsonFile;
	}
	
	public List<String[]> getVars(boolean expandedValues) 
	{
		List<String[]> ret = new ArrayList<>();
		vars.forEach((k,v)->{
			try
			{
				v = expandedValues?   engine.resolve(v):v;				
				ret.add(new String[]{k,v});
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});

		return ret;
	}

	public List<MyAction> getScriptActions()
	{
		List<MyAction> ret=new ArrayList<>();

		if(sj==null||sj.steps==null) return ret;

		for(Map<String,Object> st:sj.steps)
		{
			try
			{
				String actionName=engine.resolve((String)st.get("action"));

				if(!ActionRegistry.exists(actionName))
				    throw new RuntimeException("Unknown action: "+actionName);
				
				MyAction action=ActionRegistry.create(actionName);
				
				boolean mustSkipped=false;

				// evalúo ifdef
				Object ifdefRaw=st.get("ifdef");
				if(!mustSkipped && ifdefRaw instanceof String s)
				{
					String ifdef=engine.resolve(s);
					if(!vars.containsKey(ifdef)) mustSkipped=true;
				}

				// evalúo ifndef
				Object ifndefRaw=st.get("ifndef");
				if(!mustSkipped && ifndefRaw instanceof String s)
				{
					String ifndef=engine.resolve(s);
					if(vars.containsKey(ifndef)) mustSkipped=true;
				}
				
				// evalúo ifvar
				Object ifVarRaw = st.get("ifvar");
				if(!mustSkipped && ifVarRaw instanceof String s)
				{
					String expr = engine.resolve(s).trim();

					if(!_evalIfVar(expr))
					{
						mustSkipped = true;						
					}
				}
				
				action.setMustSkipped(mustSkipped);

				for(var entry:st.entrySet())
				{
					String name=entry.getKey();
					if("action".equals(name)) continue;
					if("ifdef".equals(name)) continue;
					if("ifndef".equals(name)) continue;
					//if("executeIf".equals(name)) continue;
				    if("ifvar".equals(name)) continue;   

					Object raw=entry.getValue();
					if(raw==null) continue;

					Object value=raw;

					if(raw instanceof String s) value=engine.resolve(s);

					String setter="set"+Character.toUpperCase(name.charAt(0))+name.substring(1);

					var m=findSetter(action.getClass(),setter,value);

					if(m!=null) m.invoke(action,value);
				}

				ret.add(action);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Error ejecutando acción: "+st.get("action"),e);
			}
		}

		return ret;
	}
	
	protected void afterRun() {}

	protected void beforeRun()
	{

		if( !options.isShowVarValues() ) return;

		MyConsole c=MyConsoles.get();
		

		c.println("[fg(CYAN)]Variables:[x]");

		vars.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(e -> {
				try
				{
					String val = engine.resolve(e.getValue());
					c.println("  "+e.getKey()+" = "+val);						
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
					throw new RuntimeException(e2);
				}
			});
			
	}
	
	public int run()
	{
		MyConsole console=MyConsoles.get();

		int delay = options.getCloseDelaySeconds();

		Runnable rDelay = delay==0?()->console.print("\nPress any key to exit...").pressAnyKey():()->console.print("\nClosing in ").countdown(delay);		
		
		try
		{
			// comienza script
			console.println("[fg(YELLOW)]Running: [x][b]"+getScriptName()+"[x]");

			// valido la sintaxis del script
			_validateSyntax();

			
			// obtengo la lista de acciones del script
			List<MyAction> actions=getScriptActions();

			beforeRun();

			int step=1;
			
			// ejecuto cada acción del script
			for(MyAction action:actions)
			{
				console.println("[fg(CYAN)]Step: "+step+".[x] ");
				
				_executeAction(action);
				
				step++;
			}
			

			// finaliza script OK
			console.print("[fg(YELLOW)]Returned value: [x][b]SUCCESS[x]. ");
			System.out.println(console.getTextPane().getText());
			
			afterRun();

			rDelay.run();
			return SUCCESS;
		}
		catch(Exception e)
		{
			// finaliza script ERROR
			String err=MyException.stackTraceToString(e);
			console.println("[fg(RED)]"+err+"[x]");
			console.print("[fg(YELLOW)]Returned value: [x][fg(RED)][b]ERROR[x][x]. ");
			System.out.println(console.getTextPane().getText());

			rDelay.run();
			return ERROR;
		}
	}


}