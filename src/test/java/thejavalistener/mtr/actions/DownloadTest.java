package thejavalistener.mtr.actions;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

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
        d.setUrl(url);                 // <-- tu setter real
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
        d.setUrl(url);                 // <-- tu setter real
        d.setTo(dest.toString());      // <-- tu setter real

        assertThrows(Exception.class, () -> d.doAction(null));
        // si tu implementación NO lanza excepción y sólo retorna error,
        // cambiamos este assert al comportamiento real.
    }
}