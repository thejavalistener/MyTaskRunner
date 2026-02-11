package scripts;

import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.core.MyScript;
import thejavalistener.mtr.actions.*;

public class InstallVSCode extends MyScript
{
    @Override
    public int script()
    {
        // Creo un directorio temporal
        doAction(new MkDir("D:\\TEMPPP"));

        // Copio VSCode
        doAction(new Copy(
                "D:\\Soft\\IDE\\VSCode\\VSCode_v1.9.9.zip",
                "D:\\TEMPPP\\VSCode.zip"
        ));

        // Copio el proyecto FULL
        doAction(new Copy(
                "D:\\Soft\\IDE\\VSCode\\AlgoritmosAFondo_FULL_v2.7.27.zip",
                "D:\\TEMPPP\\FULL.zip"
        ));

        // Remuevo la instalación vieja
        doAction(new Remove("D:\\vscode"));

//        // Instalo VSCode
//        doAction(new Unzip(
//                "D:\\TEMPPP\\VSCode.zip",
//                "D:\\"
//        ));

        doAction(new Unzip("D:\\TEMPPP\\VSCode.zip", "D:\\", (done,total) -> {
            if (total > 0) {
                int pct = (int)((done * 100) / total);
                System.out.print("\rUnzip: " + pct + "%");
            } else {
                System.out.print("\rUnzip: " + done + " bytes");
            }
        }));
        System.out.println();
        
        
        
        // Instalo el proyecto FULL
        doAction(new Unzip(
                "D:\\TEMPPP\\FULL.zip",
                "D:\\vscode\\Workspace"
        ));
    	
    	doAction(new Remove(System.getenv("APPDATA") + "\\Code"));

        // ejecuto la IDE
        doAction(new Exec(
                "D:\\vscode\\RunVSCode.bat"
        ));

        return MyAction.SUCCESS;
    }
}
