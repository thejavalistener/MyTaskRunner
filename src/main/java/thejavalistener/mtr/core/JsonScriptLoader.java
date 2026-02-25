package thejavalistener.mtr.core;

import java.nio.file.Path;

import thejavalistener.mtr.json.MyJsonScriptImple;

public class JsonScriptLoader
{
    public static MyScript load(String jsonFile) throws Exception
    {
        return new MyJsonScriptImple(Path.of(jsonFile));
    }

    public static MyScript load(Path jsonPath) throws Exception
    {
        return new MyJsonScriptImple(jsonPath);
    }
}
