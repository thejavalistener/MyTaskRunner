package thejavalistener.mtr.core;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ValidationContextBKP
{
    public enum Type
    {
        FILE,
        DIRECTORY
    }

    public static class Entry
    {
        public final Path path;
        public final Type type;

        public Entry(Path path, Type type)
        {
            this.path = path;
            this.type = type;
        }
    }

    private final Map<Path, Entry> entries = new HashMap<>();

    public boolean exists(Path p)
    {
        return entries.containsKey(p);
    }

    public boolean isDirectory(Path p)
    {
        Entry e = entries.get(p);
        if(e == null)
        {
            return false;
        }
        return e.type == Type.DIRECTORY;
    }

    public boolean isFile(Path p)
    {
        Entry e = entries.get(p);
        if(e == null)
        {
            return false;
        }
        return e.type == Type.FILE;
    }

    public void addDirectory(Path p)
    {
        entries.put(p, new Entry(p, Type.DIRECTORY));
    }

    public void addFile(Path p)
    {
        entries.put(p, new Entry(p, Type.FILE));
    }
    
    public void remove(Path p)
    {
        entries.remove(p);
    }

}
