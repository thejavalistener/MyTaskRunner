package thejavalistener.mtr.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ValidationContext
{
    public enum Type { FILE, DIRECTORY }

    public static class Entry
    {
        public final Path path;
        public final Type type;

        public Entry(Path path, Type type)
        {
            this.path = path.normalize();
            this.type = type;
        }
    }

    private final Map<Path, Entry> entries = new HashMap<>();

    public boolean exists(Path p)
    {
    	return exists(p.normalize(),true);
    }

    public boolean exists(Path p, boolean alsoVerifyInFS)
    {
    	Path pn = p.normalize();
        if (entries.containsKey(pn)) return true;
        if (alsoVerifyInFS) return Files.exists(pn);
        return false;
    }

    public boolean isDirectory(Path p)
    {
    	return isDirectory(p.normalize(),true);
    }

    public boolean isDirectory(Path p, boolean alsoVerifyInFS)
    {
    	Path pn = p.normalize();
        Entry e = entries.get(pn);
        if (e != null) return e.type == Type.DIRECTORY;
        if (alsoVerifyInFS) return Files.isDirectory(pn);
        return false;
    }

    public boolean isFile(Path p)
    {
    	return isFile(p.normalize(),true);
    }
    
    public boolean isFile(Path p, boolean alsoVerifyInFS)
    {
    	Path pn = p.normalize();
        Entry e = entries.get(pn);
        if (e != null) return e.type == Type.FILE;
        if (alsoVerifyInFS) return Files.isRegularFile(pn);
        return false;
    }

    public void addDirectory(Path p)
    {
    	Path pn = p.normalize();
        entries.put(pn, new Entry(pn, Type.DIRECTORY));
    }

    public void addFile(Path p)
    {
    	Path pn = p.normalize();
        entries.put(pn, new Entry(pn, Type.FILE));
    }

    public void remove(Path p)
    {
        entries.remove(p.normalize());
    }
}