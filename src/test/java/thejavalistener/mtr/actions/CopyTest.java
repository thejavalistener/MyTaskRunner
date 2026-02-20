package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CopyTest {

  @TempDir Path tmp;

  @Test
  void copy_file_ok() throws Exception {
    Path src = tmp.resolve("a.txt");
    Path dst = tmp.resolve("out").resolve("a.txt");
    Files.writeString(src, "hola");

    Copy c = new Copy();
    c.setFrom(src.toString());
    c.setTo(dst.toString());

    c.doAction(null);

    assertTrue(Files.exists(dst));
    assertEquals("hola", Files.readString(dst));
  }

  @Test
  void copy_dir_recursive_ok() throws Exception {
    Path srcDir = tmp.resolve("src");
    Files.createDirectories(srcDir.resolve("d1"));
    Files.writeString(srcDir.resolve("d1").resolve("x.txt"), "x");
    Files.writeString(srcDir.resolve("y.txt"), "y");

    Path dstDir = tmp.resolve("dst");

    Copy c = new Copy();
    c.setFrom(srcDir.toString());
    c.setTo(dstDir.toString());

    c.doAction(null);

    assertEquals("x", Files.readString(dstDir.resolve("d1").resolve("x.txt")));
    assertEquals("y", Files.readString(dstDir.resolve("y.txt")));
  }

  @Test
  void copy_missing_source_throws() {
    Path missing = tmp.resolve("nope");
    Copy c = new Copy();
    c.setFrom(missing.toString());
    c.setTo(tmp.resolve("dst").toString());

    assertThrows(Exception.class, () -> c.doAction(null));
  }
}