package thejavalistener.mtr.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.actions.doc.FileCopyDoc;
import thejavalistener.mtr.actions.doc.MyActionDoc;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.ValidationContext;

public class FileCopy extends MyAction
{
	private String from;
	private String to;

	public void setFrom(String from)
	{
		this.from=from;
	}

	public void setTo(String to)
	{
		this.to=to;
	}

	@Override
	public String getVerb()
	{
		return "Copying file";
	}

	@Override
	public String[] getDescription()
	{
		return new String[] {from, "to "+to};
	}
	
    @Override
    public MyActionDoc getActionDoc()
    {
    	return new FileCopyDoc();
    }

	@Override
	protected void doAction(Progress p) throws Exception
	{
		Path pFrom=Paths.get(from).normalize();
		Path pTo=Paths.get(to).normalize();
		
		if(!Files.exists(pFrom)||!Files.isRegularFile(pFrom)) throw new java.io.IOException("Source file does not exist: "+from);

		Path finalDest=resolveDestination(pFrom,pTo,to);

		if(finalDest.getParent()!=null) Files.createDirectories(finalDest.getParent());

		long totalBytes=Files.size(pFrom);
		AtomicLong copiedBytes=new AtomicLong(0);
		int lastPct=-1;

		try (InputStream in=Files.newInputStream(pFrom); OutputStream out=Files.newOutputStream(finalDest,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE))
		{
			byte[] buffer=new byte[64*1024];
			int n;

			while((n=in.read(buffer))>=0)
			{
				if(n==0) continue;

				out.write(buffer,0,n);

				long current=copiedBytes.addAndGet(n);

				if(p!=null&&totalBytes>0)
				{
					int pct=(int)Math.min(100,(current*100)/totalBytes);
					if(pct!=lastPct)
					{
						p.setPercent(pct,"");
						lastPct=pct;
					}
				}
			}
		}

		if(p!=null) p.setPercent(100,"");
	}

	private static boolean endsWithSep(String s)
	{
		if(s==null||s.isEmpty()) return false;
		char c=s.charAt(s.length()-1);
		return c=='/'||c=='\\';
	}

	private Path resolveDestination(Path source, Path target, String rawTo) throws Exception
	{
		boolean forceDir=endsWithSep(rawTo);

		if(forceDir)
		{
			// si hay un archivo con ese nombre => error
			if(Files.exists(target)&&!Files.isDirectory(target)) throw new IllegalArgumentException("'to' es un directorio pero existe como archivo: "+rawTo);

			Files.createDirectories(target);
			return target.resolve(source.getFileName()).normalize();
		}

		// no forzado: si existe y es dir => adentro
		if(Files.exists(target)&&Files.isDirectory(target)) return target.resolve(source.getFileName()).normalize();

		// si no existe => se interpreta como archivo destino
		return target.normalize();
	}

	@Override
	protected boolean checkExecuteIf()
	{
		String c=getExecuteIf();
		if(c==null||c.isBlank()) return true;

		Path src = Paths.get(from).normalize();
		Path pTo = Paths.get(to).normalize();

		Path dest;
		try
		{
		    dest = resolveDestination(src, pTo, to);
		}
		catch(Exception e)
		{
		    throw new RuntimeException(e);
		}		
		
		
		ExecuteIf opt = ExecuteIf.valueOf(c);
		switch(opt)
		{
			case exists:
				return Files.exists(dest);
			case notExists:
				return !Files.exists(dest);

			case notExistsOrIsNewer:
				if(!Files.exists(dest)) return true;

				if(!Files.exists(src)) throw new RuntimeException("Source no existe: "+from);

				try
				{
					return Files.getLastModifiedTime(src).toMillis()>Files.getLastModifiedTime(dest).toMillis();
				}
				catch(IOException e)
				{
					throw new RuntimeException(e);
				}

			default:
                throw new RuntimeException("Invalid executeIf option: "+opt+". Allowed values are: "+Arrays.toString(ExecuteIf.values()));
		}
	}

	@Override
	public String validate(ValidationContext ctx)
	{
	    String c = getExecuteIf();
	    if(c != null && !c.isBlank())
	    {
	        switch(c)
	        {
	            case "exists":
	            case "notExists":
	            case "notExistsOrIsNewer":
	                break;
	            default:
	                return "executeIf inválido: " + c;
	        }
	    }

	    if(from == null || from.isBlank())
	        return "'from' es obligatorio";

	    if(to == null || to.isBlank())
	        return "'to' es obligatorio";

	    Path pFrom;
	    Path pTo;

	    try
	    {
	        pFrom = Paths.get(from).normalize();
	    }
	    catch(Exception e)
	    {
	        return "path 'from' inválido: " + from + " (" + e.getMessage() + ")";
	    }

	    try
	    {
	        pTo = Paths.get(to).normalize();
	    }
	    catch(Exception e)
	    {
	        return "path 'to' inválido: " + to + " (" + e.getMessage() + ")";
	    }

	    // tracking liviano
	    boolean forceDir = endsWithSep(to);
	    Path finalDest;

	    if(forceDir)
	    {
	        finalDest = pTo.resolve(pFrom.getFileName()).normalize();
	        ctx.addDirectory(pTo);
	    }
	    else
	    {
	        finalDest = pTo.normalize();
	    }

	    Path parent = finalDest.getParent();
	    if(parent != null) ctx.addDirectory(parent);

	    ctx.addFile(finalDest);

	    return null;
	}
	
	private enum ExecuteIf
	{
		exists, notExists, notExistsOrIsNewer
	}
	
}