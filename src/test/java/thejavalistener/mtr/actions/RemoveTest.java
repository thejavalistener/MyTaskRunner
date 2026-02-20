package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.mtr.core.ValidationContext;

class RemoveTest {

    @TempDir
    Path tmp;

    @Test
    void doAction_removes_file() throws Exception {
        Path file = tmp.resolve("a.txt");
        Files.writeString(file, "hola");

        Remove r = new Remove();
        r.setPath(file.toString());

        r.doAction(null);

        assertFalse(Files.exists(file));
    }

    @Test
    void doAction_removes_directory_recursively() throws Exception {
        Path dir = tmp.resolve("dir");
        Files.createDirectories(dir.resolve("sub"));
        Files.writeString(dir.resolve("sub").resolve("x.txt"), "x");

        Remove r = new Remove();
        r.setPath(dir.toString());

        r.doAction(null);

        assertFalse(Files.exists(dir));
    }

    @Test
    void doAction_does_not_fail_if_missing() throws Exception {
        Path missing = tmp.resolve("nope");

        Remove r = new Remove();
        r.setPath(missing.toString());

        assertDoesNotThrow(() -> r.doAction(null));
    }

    @Test
    void validate_requires_path() {
        Remove r = new Remove();
        assertEquals("'path' es obligatorio", r.validate(null));
    }

    @Test
    void validate_fails_if_missing_and_stop_true() {
        Remove r = new Remove();
        r.setPath("no-existe-123");
        r.setStopScriptOnError(true);

        String result = r.validate(new ValidationContext());

        assertTrue(result.startsWith("no existe"));
    }
}