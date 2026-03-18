package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.fwkutils.console.Progress;
import thejavalistener.mtr.core.ValidationContext;

class DirCopyTest
{
    @TempDir
    Path tmp;

    @Test
    void copy_directory_into_new_base_directory() throws Exception
    {
        Path src = Files.createDirectory(tmp.resolve("dir1"));
        Files.writeString(src.resolve("a.txt"), "hola");

        Path dstBase = tmp.resolve("dir2"); // no existe

        DirCopy dc = new DirCopy();
        dc.setFrom(src.toString());
        dc.setTo(dstBase.toString());

        ValidationContext ctx = new ValidationContext();
        assertNull(dc.validate(ctx));

        dc.doAction((Progress)null);

        assertTrue(Files.exists(dstBase.resolve("dir1").resolve("a.txt")));
    }

    @Test
    void overwrite_existing_files() throws Exception
    {
        Path src = Files.createDirectory(tmp.resolve("dir1"));
        Files.writeString(src.resolve("a.txt"), "nuevo");

        Path dstBase = Files.createDirectory(tmp.resolve("dir2"));
        Path existingDir = Files.createDirectory(dstBase.resolve("dir1"));
        Files.writeString(existingDir.resolve("a.txt"), "viejo");

        DirCopy dc = new DirCopy();
        dc.setFrom(src.toString());
        dc.setTo(dstBase.toString());

        ValidationContext ctx = new ValidationContext();
        assertNull(dc.validate(ctx));

        dc.doAction((Progress)null);

        assertEquals("nuevo", Files.readString(existingDir.resolve("a.txt")));
    }

//    @Test
//    void error_if_source_is_not_directory() throws Exception
//    {
//        Path file = Files.writeString(tmp.resolve("file.txt"), "hola");
//
//        DirCopy dc = new DirCopy();
//        dc.setFrom(file.toString());
//        dc.setTo(tmp.resolve("dest").toString());
//
//        ValidationContext ctx = new ValidationContext();
//
//        String err = dc.validate(ctx);
//        assertNotNull(err);
//    }
    
    @Test
    void error_if_source_is_not_directory_in_execution() throws Exception
    {
        Path file = Files.writeString(tmp.resolve("file.txt"), "hola");

        DirCopy dc = new DirCopy();
        dc.setFrom(file.toString());
        dc.setTo(tmp.resolve("dest").toString());

        assertThrows(IOException.class, () -> dc.doAction(null));
    }
}