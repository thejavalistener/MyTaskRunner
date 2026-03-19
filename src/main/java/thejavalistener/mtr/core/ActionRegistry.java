package thejavalistener.mtr.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import thejavalistener.mtr.actions.DirCopy;
import thejavalistener.mtr.actions.Download;
import thejavalistener.mtr.actions.Exec;
import thejavalistener.mtr.actions.FileCopy;
import thejavalistener.mtr.actions.GoogleDriveDownload;
import thejavalistener.mtr.actions.MkDir;
import thejavalistener.mtr.actions.Move;
import thejavalistener.mtr.actions.Remove;
import thejavalistener.mtr.actions.Unzip;
import thejavalistener.mtr.actions.Zip;

public final class ActionRegistry
{
    private static final Map<String,Class<? extends MyAction>> actions=new LinkedHashMap<>();

    static
    {
        register(DirCopy.class);
        register(Download.class);
        register(Exec.class);
        register(FileCopy.class);
        register(GoogleDriveDownload.class);
        register(MkDir.class);
        register(Move.class);
        register(Remove.class);
        register(Unzip.class);
        register(Zip.class);
    }

    private ActionRegistry() {}

    private static void register(Class<? extends MyAction> clazz)
    {
        actions.put(clazz.getSimpleName(), clazz);
    }

    public static MyAction create(String name) throws Exception
    {
        Class<? extends MyAction> clazz=actions.get(name);

        if(clazz==null)
            throw new IllegalArgumentException("Unknown action: "+name);

        return clazz.getDeclaredConstructor().newInstance();
    }

    public static Set<String> getNames()
    {
        return actions.keySet();
    }
}