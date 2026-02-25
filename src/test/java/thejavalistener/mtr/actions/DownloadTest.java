package thejavalistener.mtr.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

import thejavalistener.mtr.core.ValidationContext;

class DownloadTest {

    @TempDir Path tmp;

    private HttpServer server;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        port = server.getAddress().getPort();

        server.createContext("/ok.txt", ex -> {
            byte[] data = "hola".getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(200, data.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(data); }
        });

        server.createContext("/404", ex -> {
            ex.sendResponseHeaders(404, -1);
            ex.close();
        });

        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void download_ok_writes_file_and_creates_parent_dirs() throws Exception {
        String url = URI.create("http://127.0.0.1:" + port + "/ok.txt").toString();
        Path dest = tmp.resolve("a/b/c.txt");

        Download d = new Download();
        d.setFrom(url);                 // <-- tu setter real
        d.setTo(dest.toString());      // <-- tu setter real

        d.doAction(null);

        assertTrue(Files.exists(dest));
        assertEquals("hola", Files.readString(dest));
    }

    @Test
    void download_404_should_fail() throws Exception {
        String url = URI.create("http://127.0.0.1:" + port + "/404").toString();
        Path dest = tmp.resolve("out.txt");

        Download d = new Download();
        d.setFrom(url);                 // <-- tu setter real
        d.setTo(dest.toString());      // <-- tu setter real

        assertThrows(Exception.class, () -> d.doAction(null));
        // si tu implementación NO lanza excepción y sólo retorna error,
        // cambiamos este assert al comportamiento real.
    }
    
    
    @Test
    void validate_requires_from_and_to() {
        Download d = new Download();
        ValidationContext ctx = new ValidationContext();

        assertNotNull(d.validate(ctx)); // sin from ni to

        d.setFrom("http://x");
        assertNotNull(d.validate(ctx)); // falta to

        d = new Download();
        d.setTo("x.txt");
        assertNotNull(d.validate(ctx)); // falta from
    }
}