package thejavalistener.mtr.actions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.mtr.core.ValidationContext;

public class FileCopyTest
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

        ValidationContext ctx = new ValidationContext();
        assertNull(fc.validate(ctx));

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

        ValidationContext ctx = new ValidationContext();
        assertNull(fc.validate(ctx));

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

        ValidationContext ctx = new ValidationContext();
        assertNull(fc.validate(ctx));

        fc.doAction(null);

        assertTrue(Files.exists(dest));
    }

    @Test
    void error_if_source_not_exists()
    {
        FileCopy fc = new FileCopy();
        fc.setFrom(tmp.resolve("nope.txt").toString());
        fc.setTo(tmp.resolve("x.txt").toString());

        ValidationContext ctx = new ValidationContext();
        assertNotNull(fc.validate(ctx));
    }
}