package thejavalistener.mtr.actions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.OutputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class UnzipTest {

    @TempDir
    Path tmp;

    @Test
    void doAction_unzips_file() throws Exception {
        Path zip = tmp.resolve("test.zip");
        Path dest = tmp.resolve("out");

        createZip(zip);

        Unzip u = new Unzip();
        u.setFrom(zip.toString());
        u.setTo(dest.toString());

        u.doAction(null);

        assertTrue(Files.exists(dest.resolve("a.txt")));
        assertTrue(Files.exists(dest.resolve("dir/b.txt")));
        assertEquals("hola", Files.readString(dest.resolve("a.txt")));
        assertEquals("mundo", Files.readString(dest.resolve("dir/b.txt")));
    }

    @Test
    void validate_requires_from() {
        Unzip u = new Unzip();
        u.setTo("algo");
        assertNotNull(u.validate(null));
    }

    @Test
    void validate_requires_to() {
        Unzip u = new Unzip();
        u.setFrom("algo");
        assertNotNull(u.validate(null));
    }

    @Test
    void doAction_fails_if_zip_missing() {
        Unzip u = new Unzip();
        u.setFrom("no-existe.zip");
        u.setTo(tmp.resolve("x").toString());

        assertThrows(Exception.class, () -> u.doAction(null));
    }

    private void createZip(Path zipPath) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(
                Files.newOutputStream(zipPath))) {

            // archivo raíz
            zos.putNextEntry(new ZipEntry("a.txt"));
            zos.write("hola".getBytes());
            zos.closeEntry();

            // subdirectorio + archivo
            zos.putNextEntry(new ZipEntry("dir/"));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry("dir/b.txt"));
            zos.write("mundo".getBytes());
            zos.closeEntry();
        }
    }
    
    @Test
    void doAction_unzips_single_file() throws Exception {

        Path zip = tmp.resolve("single.zip");
        Path dest = tmp.resolve("out");

        try (ZipOutputStream zos =
                     new ZipOutputStream(Files.newOutputStream(zip))) {

            zos.putNextEntry(new ZipEntry("archivo.bin"));
            zos.write("contenido".getBytes());
            zos.closeEntry();
        }

        Unzip u = new Unzip();
        u.setFrom(zip.toString());
        u.setTo(dest.toString());

        u.doAction(null);

        Path extracted = dest.resolve("archivo.bin");

        assertTrue(Files.exists(extracted));
        assertEquals("contenido", Files.readString(extracted));
    }
}