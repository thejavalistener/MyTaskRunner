package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileCopyTest2
{
    @TempDir
    Path tmp;

    @Test
    void copy_file_to_existing_directory() throws Exception
    {
        Path srcDir = Files.createDirectory(tmp.resolve("src"));
        Path dstDir = Files.createDirectory(tmp.resolve("dst"));

        Path source = Files.writeString(srcDir.resolve("a.txt"), "hola");

        FileCopy fc = new FileCopy();
        fc.setFrom(source.toString());
        fc.setTo(dstDir.toString());

        fc.doAction(null);

        assertTrue(Files.exists(dstDir.resolve("a.txt")));
    }

    @Test
    void copy_file_to_new_directory_with_trailing_slash() throws Exception
    {
        Path srcDir = Files.createDirectory(tmp.resolve("src"));
        Path source = Files.writeString(srcDir.resolve("a.txt"), "hola");

        Path newDir = tmp.resolve("nuevo");

        FileCopy fc = new FileCopy();
        fc.setFrom(source.toString());
        fc.setTo(newDir.toString() + "/");

        fc.doAction(null);

        assertTrue(Files.exists(newDir.resolve("a.txt")));
    }

    @Test
    void copy_file_with_new_name() throws Exception
    {
        Path srcDir = Files.createDirectory(tmp.resolve("src"));
        Path source = Files.writeString(srcDir.resolve("a.txt"), "hola");

        Path dest = tmp.resolve("b.txt");

        FileCopy fc = new FileCopy();
        fc.setFrom(source.toString());
        fc.setTo(dest.toString());

        fc.doAction(null);

        assertTrue(Files.exists(dest));
    }

    @Test
    void executeIf_notExists() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = tmp.resolve("b.txt");

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("notExists");

        assertTrue(fc.checkExecuteIf());
    }

    @Test
    void executeIf_exists() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = Files.writeString(tmp.resolve("b.txt"), "hola");

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("exists");

        assertTrue(fc.checkExecuteIf());
    }

    @Test
    void executeIf_isNewer() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Thread.sleep(10);
        Path dest = Files.writeString(tmp.resolve("b.txt"), "hola");

        // ahora hago src más nuevo
        Thread.sleep(10);
        Files.writeString(src, "hola2");

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("isNewer");

        assertTrue(fc.checkExecuteIf());
    }

    @Test
    void executeIf_isDifferentSize() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = Files.writeString(tmp.resolve("b.txt"), "hola mundo");

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("isDifferentSize");

        assertTrue(fc.checkExecuteIf());
    }

    @Test
    void executeIf_combined_conditions() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = tmp.resolve("b.txt"); // no existe

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("notExists || isNewer");

        assertTrue(fc.checkExecuteIf());
    }

    @Test
    void executeIf_false_when_no_condition_matches() throws Exception
    {
        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = Files.writeString(tmp.resolve("b.txt"), "hola");

        FileCopy fc = new FileCopy();
        fc.setFrom(src.toString());
        fc.setTo(dest.toString());
        fc.setExecuteIf("notExists || isDifferentSize");

        assertFalse(fc.checkExecuteIf());
    }

    @Test
    void error_if_source_not_exists_on_execute() 
    {
        FileCopy fc = new FileCopy();
        fc.setFrom(tmp.resolve("nope.txt").toString());
        fc.setTo(tmp.resolve("x.txt").toString());

        assertThrows(Exception.class, () -> fc.doAction(null));
    }
}