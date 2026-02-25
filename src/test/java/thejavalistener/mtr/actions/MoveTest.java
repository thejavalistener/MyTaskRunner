package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.mtr.core.ValidationContext;

class MoveTest {

    @TempDir
    Path tmp;

    @Test
    void move_file_ok() throws Exception {
        Path src = tmp.resolve("a.txt");
        Files.writeString(src, "hola");

        Path dst = tmp.resolve("out/b.txt");

        Move m = new Move();
        m.setFrom(src.toString());
        m.setTo(dst.toString());

        m.doAction(null);

        assertFalse(Files.exists(src));
        assertTrue(Files.exists(dst));
        assertEquals("hola", Files.readString(dst));
    }

    @Test
    void move_creates_parent_directories() throws Exception {
        Path src = tmp.resolve("x.txt");
        Files.writeString(src, "x");

        Path dst = tmp.resolve("a/b/c/x.txt");

        Move m = new Move();
        m.setFrom(src.toString());
        m.setTo(dst.toString());

        m.doAction(null);

        assertTrue(Files.exists(dst));
    }

    @Test
    void move_fails_if_source_missing() {
        Path src = tmp.resolve("nope.txt");
        Path dst = tmp.resolve("x.txt");

        Move m = new Move();
        m.setFrom(src.toString());
        m.setTo(dst.toString());

        assertThrows(Exception.class, () -> m.doAction(null));
    }

    @Test
    void validate_requires_from() {
        Move m = new Move();
        m.setTo("algo");
        assertNotNull(m.validate(new ValidationContext()));
    }

    @Test
    void validate_requires_to() {
        Move m = new Move();
        m.setFrom("algo");
        assertNotNull(m.validate(new ValidationContext()));
    }
    
    @Test
    void move_directory_ok() throws Exception {
        Path srcDir = tmp.resolve("src");
        Files.createDirectories(srcDir.resolve("sub"));
        Files.writeString(srcDir.resolve("sub").resolve("x.txt"), "x");

        Path dstDir = tmp.resolve("dst");

        Move m = new Move();
        m.setFrom(srcDir.toString());
        m.setTo(dstDir.toString());

        m.doAction(null);

        assertFalse(Files.exists(srcDir));
        assertTrue(Files.exists(dstDir.resolve("sub").resolve("x.txt")));
        assertEquals("x", Files.readString(dstDir.resolve("sub").resolve("x.txt")));
    }
}