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
		// Creo un directorio temporal
		doAction(new MkDir("D:\\TEMPPP"));

		// Copio VSCode
		doAction(new Copy("D:\\Soft\\IDE\\VSCode\\VSCode_v1.9.9.zip","D:\\TEMPPP\\VSCode.zip"),true);

		// Copio el proyecto FULL
		doAction(new Copy("D:\\Soft\\IDE\\VSCode\\AlgoritmosAFondo_FULL_v2.7.27.zip","D:\\TEMPPP\\FULL.zip"),true);

		// Remuevo la instalación vieja
		doAction(new Remove("D:\\vscode"));

		// Instalo VSCode
		doAction(new Unzip("D:\\TEMPPP\\VSCode.zip","D:\\"),true);

		// Instalo el proyecto FULL
		doAction(new Unzip("D:\\TEMPPP\\FULL.zip","D:\\vscode\\Workspace"),true);
		
		// ejecuto la IDE
		doAction(new Exec("D:\\vscode\\RunVSCode.bat"));

		return MyAction.SUCCESS;
	}
}
