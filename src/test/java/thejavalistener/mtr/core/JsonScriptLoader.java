package thejavalistener.mtr.core;

import java.nio.file.Path;

public class JsonScriptLoader
{
    public static MyScript load(String jsonFile) throws Exception
    {
        return new MyScript(Path.of(jsonFile));
    }

    public static MyScript load(Path jsonPath) throws Exception
    {
        return new MyScript(jsonPath);
    }
}
