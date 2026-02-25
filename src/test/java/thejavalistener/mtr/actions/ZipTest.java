package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipTest {

    @TempDir
    Path tmp;

    @Test
    void doAction_creates_zip_with_structure() throws Exception {

        Path src = tmp.resolve("src");
        Files.createDirectories(src.resolve("dir"));
        Files.writeString(src.resolve("a.txt"), "hola");
        Files.writeString(src.resolve("dir").resolve("b.txt"), "mundo");

        Path zip = tmp.resolve("out.zip");

        Zip z = new Zip();
        z.setFrom(src.toString());
        z.setTo(zip.toString());

        z.doAction(null);

        assertTrue(Files.exists(zip));

        boolean foundA = false;
        boolean foundB = false;

        try (java.util.zip.ZipInputStream zis =
                     new java.util.zip.ZipInputStream(
                             Files.newInputStream(zip))) {

            java.util.zip.ZipEntry e;

            while ((e = zis.getNextEntry()) != null) {

                if (e.isDirectory()) continue;

                String name = e.getName().replace("\\", "/");

                if (name.equals("a.txt"))
                    foundA = true;

                if (name.equals("dir/b.txt"))
                    foundB = true;
            }
        }

        assertTrue(foundA, "No encontró a.txt dentro del zip");
        assertTrue(foundB, "No encontró dir/b.txt dentro del zip");
    }
    @Test
    void validate_requires_from() {
        Zip z = new Zip();
        z.setTo("algo");
        assertNotNull(z.validate(null));
    }

    @Test
    void validate_requires_to() {
        Zip z = new Zip();
        z.setFrom("algo");
        assertNotNull(z.validate(null));
    }

    @Test
    void doAction_fails_if_source_missing() {
        Zip z = new Zip();
        z.setFrom("no-existe");
        z.setTo(tmp.resolve("x.zip").toString());

        assertThrows(Exception.class, () -> z.doAction(null));
    }
    
    @Test
    void doAction_zips_single_file_name_is_preserved() throws Exception {
        Path file = tmp.resolve("cualquierNombre.bin");
        Files.write(file, "contenido".getBytes());

        Path zip = tmp.resolve("out.zip");

        Zip z = new Zip();
        z.setFrom(file.toString());
        z.setTo(zip.toString());

        z.doAction(null);

        assertTrue(Files.exists(zip));

        int entries = 0;
        String onlyName = null;

        try (java.util.zip.ZipInputStream zis =
                     new java.util.zip.ZipInputStream(Files.newInputStream(zip))) {

            java.util.zip.ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if (e.isDirectory()) continue;
                entries++;
                onlyName = e.getName();
            }
        }

        assertEquals(1, entries);
        assertEquals(file.getFileName().toString(), onlyName);
    }
}