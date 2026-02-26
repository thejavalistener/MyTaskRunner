package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.mtr.core.ValidationContext;

class MkDirTest {

    @TempDir
    Path tmp;

    @Test
    void validate_requires_path() {
        MkDir m = new MkDir();
        assertNotNull(m.validate(null));
    }

    @Test
    void doAction_creates_directory() throws Exception {
        Path dir = tmp.resolve("nuevo");

        MkDir m = new MkDir();
        m.setTo(dir.toString());

        m.doAction(null);

        assertTrue(Files.exists(dir));
        assertTrue(Files.isDirectory(dir));
    }

    @Test
    void doAction_creates_directories_recursively() throws Exception {
        Path dir = tmp.resolve("a/b/c");

        MkDir m = new MkDir();
        m.setTo(dir.toString());

        m.doAction(null);

        assertTrue(Files.exists(dir));
    }

    @Test
    void validate_fails_if_path_exists_and_is_file() throws Exception {
        Path file = tmp.resolve("x.txt");
        Files.writeString(file, "hola");

        ValidationContext ctx = new ValidationContext();
        ctx.addFile(file); // ajustá si tu API es distinta

        MkDir m = new MkDir();
        m.setTo(file.toString());

        String result = m.validate(ctx);
        assertNotNull(result);
    }
}