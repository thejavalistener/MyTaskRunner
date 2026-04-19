package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PathSyncTest
{
    @TempDir
    Path tmp;

    @Test
    void doAction_copies_all_files_and_dirs() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertTrue(Files.exists(dst.resolve("a.txt")));
        assertTrue(Files.exists(dst.resolve("dir/b.txt")));
        assertEquals("hola", Files.readString(dst.resolve("a.txt")));
        assertEquals("mundo", Files.readString(dst.resolve("dir/b.txt")));
    }

    @Test
    void doAction_updates_modified_file() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);
        createStructure(dst);

        // modificar destino
        Files.writeString(dst.resolve("a.txt"), "cambiado");

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertEquals("hola", Files.readString(dst.resolve("a.txt")));
    }

    @Test
    void doAction_adds_missing_file() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);
        Files.createDirectories(dst);

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertTrue(Files.exists(dst.resolve("a.txt")));
    }

    @Test
    void doAction_removes_extra_file() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);
        createStructure(dst);

        // agregar archivo extra en destino
        Files.writeString(dst.resolve("extra.txt"), "x");

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertFalse(Files.exists(dst.resolve("extra.txt")));
    }

    @Test
    void doAction_removes_extra_directory() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);
        createStructure(dst);

        Path extraDir = dst.resolve("extraDir");
        Files.createDirectories(extraDir);

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertFalse(Files.exists(extraDir));
    }

    @Test
    void doAction_keeps_identical_file() throws Exception
    {
        Path src = tmp.resolve("src");
        Path dst = tmp.resolve("dst");

        createStructure(src);
        createStructure(dst);

        PathSync s = new PathSync();
        s.setFrom(src.toString());
        s.setTo(dst.toString());

        s.doAction(null);

        assertEquals("hola", Files.readString(dst.resolve("a.txt")));
    }

    @Test
    void doAction_fails_if_source_missing()
    {
        Path dst = tmp.resolve("dst");

        PathSync s = new PathSync();
        s.setFrom("no-existe");
        s.setTo(dst.toString());

        assertThrows(Exception.class, () -> s.doAction(null));
    }

    // ================= helpers =================

    private void createStructure(Path root) throws Exception
    {
        Files.createDirectories(root);
        Files.writeString(root.resolve("a.txt"), "hola");

        Path dir = root.resolve("dir");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("b.txt"), "mundo");
    }

    @SuppressWarnings("unused")
    private void deleteRecursively(Path p) throws Exception
    {
        if (!Files.exists(p)) return;

        Files.walk(p)
             .sorted(Comparator.reverseOrder())
             .forEach(x -> {
                 try { Files.delete(x); } catch (Exception ignored) {}
             });
    }
}