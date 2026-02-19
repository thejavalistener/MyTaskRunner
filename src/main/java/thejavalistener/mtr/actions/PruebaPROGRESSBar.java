package thejavalistener.mtr.actions;

import thejavalistener.fwkutils.console.MyConsole;
import thejavalistener.fwkutils.console.MyConsoles;
import thejavalistener.fwkutils.console.Progress;

public class PruebaPROGRESSBar
{
	public static void main(String[] args)
	{
		MyConsole c = MyConsoles.getOnWindow("Vas a ver, LA CONCHA DE TU MADRE!!!");
		
		long vueltas = 100000000;
		Progress p = c.progressBar2(100,vueltas);
		for(long i=0; i<vueltas; i++)
		{
			p.increase(" "+i);
			
			for(long j=0; j<10000000000L; j++);
		}
		
	}
}
