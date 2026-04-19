package thejavalistener.mtr.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.actions.doc.FileCopyDoc;
import thejavalistener.mtr.actions.doc.MyActionDoc;
import thejavalistener.mtr.core.MyAction;

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
			if(Files.exists(target)&&!Files.isDirectory(target)) throw new IllegalArgumentException("'to' es un directorio pero existe como archivo: "+rawTo);

			Files.createDirectories(target);
			return target.resolve(source.getFileName()).normalize();
		}

		if(Files.exists(target)&&Files.isDirectory(target)) return target.resolve(source.getFileName()).normalize();

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

		String[] conditions = c.split("\\|\\|");

		for(String cond : conditions)
		{
			String op = cond.trim();

			switch(op)
			{
				case "exists":
					if(Files.exists(dest)) return true;
					break;

				case "notExists":
					if(!Files.exists(dest)) return true;
					break;

				case "isNewer":
					if(!Files.exists(dest)) return true;

					try
					{
						if(Files.getLastModifiedTime(src).toMillis() > Files.getLastModifiedTime(dest).toMillis())
							return true;
					}
					catch(IOException e)
					{
						throw new RuntimeException(e);
					}
					break;

				case "isDifferentSize":
					if(!Files.exists(dest)) return true;

					try
					{
						if(Files.size(src) != Files.size(dest))
							return true;
					}
					catch(IOException e)
					{
						throw new RuntimeException(e);
					}
					break;

				default:
					throw new RuntimeException("Invalid executeIf condition: "+op);
			}
		}

		return false;
	}
}