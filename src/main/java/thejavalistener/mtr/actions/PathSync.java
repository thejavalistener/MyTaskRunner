package thejavalistener.mtr.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.MyAction;
import thejavalistener.mtr.doc.DocAction;

public class PathSync extends MyAction
{
    private String from;
    private String to;

    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }

    @Override
    public String getVerb()
    {
        return "Syncing path";
    }

    @Override
    public String[] getDescription()
    {
        return new String[]{from, "to " + to};
    }

    @Override
    protected void doAction(Progress p) throws Exception
    {
        Path src = Paths.get(from).normalize();
        Path dst = Paths.get(to).normalize();

        if (!Files.exists(src) || !Files.isDirectory(src))
            throw new IOException("Source directory does not exist: " + from);

        Files.createDirectories(dst);

        // --- INDEX SOURCE ---
        Map<Path, Path> srcFiles = Files.walk(src)
                .filter(Files::isRegularFile)
                .collect(Collectors.toMap(
                        f -> src.relativize(f),
                        f -> f
                ));

        Set<Path> srcDirs = Files.walk(src)
                .filter(Files::isDirectory)
                .map(d -> src.relativize(d))
                .collect(Collectors.toSet());

        // --- CREATE DIRS ---
        for (Path rel : srcDirs)
        {
            Path targetDir = dst.resolve(rel);
            if (!Files.exists(targetDir))
            {
                Files.createDirectories(targetDir);
            }
        }

        // --- COPY / UPDATE ---
        for (Map.Entry<Path, Path> e : srcFiles.entrySet())
        {
            Path rel = e.getKey();
            Path fSrc = e.getValue();
            Path fDst = dst.resolve(rel);

            boolean copy = true;
            String action = "agregado";

            if (Files.exists(fDst))
            {
                action = "recuperado";

                if (Files.size(fSrc) == Files.size(fDst))
                {
                    if (Files.getLastModifiedTime(fSrc).toMillis() ==
                        Files.getLastModifiedTime(fDst).toMillis())
                    {
                        copy = false;
                    }
                    else
                    {
                        if (hashEquals(fSrc, fDst))
                        {
                            copy = false;
                        }
                    }
                }
            }

            if (copy)
            {
                Files.createDirectories(fDst.getParent());
                copyFile(fSrc, fDst);

                System.out.println("  -> " + rel + " " + action + ".");
            }
        }

        // --- DELETE EXTRA FILES ---
        List<Path> dstFiles = Files.walk(dst)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        for (Path fDst : dstFiles)
        {
            Path rel = dst.relativize(fDst);
            Path fSrc = src.resolve(rel);

            if (!Files.exists(fSrc))
            {
                Files.delete(fDst);
                System.out.println("  -> " + rel + " eliminado.");
            }
        }

        // --- DELETE EXTRA DIRS (reverse order) ---
        List<Path> dstDirs = Files.walk(dst)
                .filter(Files::isDirectory)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        for (Path dDst : dstDirs)
        {
            if (dDst.equals(dst)) continue;

            Path rel = dst.relativize(dDst);
            Path dSrc = src.resolve(rel);

            if (!Files.exists(dSrc))
            {
                Files.delete(dDst);
                System.out.println("  -> " + rel + " eliminado.");
            }
        }
    }

    private static void copyFile(Path from, Path to) throws IOException
    {
        try (InputStream in = Files.newInputStream(from);
             OutputStream out = Files.newOutputStream(to,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE))
        {
            byte[] buffer = new byte[64 * 1024];
            int n;
            while ((n = in.read(buffer)) >= 0)
            {
                if (n == 0) continue;
                out.write(buffer, 0, n);
            }
        }
    }

    private static boolean hashEquals(Path a, Path b) throws Exception
    {
        return Arrays.equals(hash(a), hash(b));
    }

    private static byte[] hash(Path p) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try (InputStream in = Files.newInputStream(p))
        {
            byte[] buffer = new byte[64 * 1024];
            int n;

            while ((n = in.read(buffer)) >= 0)
            {
                if (n == 0) continue;
                md.update(buffer, 0, n);
            }
        }

        return md.digest();
    }
	@Override
	public DocAction getActionDoc()
	{
		return null;
	}
}