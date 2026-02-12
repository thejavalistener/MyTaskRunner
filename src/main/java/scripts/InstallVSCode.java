package scripts;

import thejavalistener.mtr.actions.Copy;
import thejavalistener.mtr.actions.Exec;
import thejavalistener.mtr.actions.MkDir;
import thejavalistener.mtr.actions.Remove;
import thejavalistener.mtr.actions.Unzip;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyScript;

public class InstallVSCode extends MyScript
{
	@Override
	public int script()
	{
	    MkDir mk = new MkDir();
	    mk.setPath("D:\\TEMPPP");
	    doAction(mk);

	    Copy c1 = new Copy();
	    c1.setFrom("D:\\Soft\\IDE\\VSCode\\VSCode_v1.9.9.zip");
	    c1.setTo("D:\\TEMPPP\\VSCode.zip");
	    c1.setProgress(true);
	    doAction(c1);

	    Copy c2 = new Copy();
	    c2.setFrom("D:\\Soft\\IDE\\VSCode\\AlgoritmosAFondo_FULL_v2.7.27.zip");
	    c2.setTo("D:\\TEMPPP\\FULL.zip");
	    c2.setProgress(true);
	    doAction(c2);

	    Remove rm = new Remove();
	    rm.setPath("D:\\vscode");
	    doAction(rm);

	    Unzip u1 = new Unzip();
	    u1.setFrom("D:\\TEMPPP\\VSCode.zip");
	    u1.setTo("D:\\");
	    u1.setProgress(true);
	    doAction(u1);

	    Unzip u2 = new Unzip();
	    u2.setFrom("D:\\TEMPPP\\FULL.zip");
	    u2.setTo("D:\\vscode\\Workspace");
	    u2.setProgress(true);
	    doAction(u2);

	    Exec ex = new Exec();
	    ex.setCommand("D:\\vscode\\RunVSCode.bat");
	    doAction(ex);

	    return MyAction.SUCCESS;
	}
}
