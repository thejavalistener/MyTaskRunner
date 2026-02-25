package thejavalistener.mtr.actions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecTest {

    @Test
    void validate_requires_command() {
        Exec e = new Exec();
        String result = e.validate(null);

        assertNotNull(result);
    }

    @Test
    void validate_rejects_invalid_opts() {
        Exec e = new Exec();
        e.setCommand("echo hola");
        e.setOpts("NO_EXISTE");

        String result = e.validate(null);

        assertNotNull(result);
    }

    @Test
    void wait_option_executes_and_waits_ok() throws Exception {
        Exec e = new Exec();

        if (isWindows())
            e.setCommand("cmd /c exit 0");
        else
            e.setCommand("sh -c exit 0");

        e.setOpts("WAIT");

        assertDoesNotThrow(() -> e.doAction(null));
    }

    @Test
    void wait_option_throws_if_exit_code_not_zero() {
        Exec e = new Exec();

        if (isWindows())
            e.setCommand("cmd /c exit 1");
        else
            e.setCommand("sh -c exit 1");

        e.setOpts("WAIT");

        assertThrows(RuntimeException.class, () -> e.doAction(null));
    }

    @Test
    void default_is_detached_and_does_not_wait() throws Exception {
        Exec e = new Exec();

        if (isWindows())
            e.setCommand("cmd /c timeout 1");
        else
            e.setCommand("sleep 1");

        // sin opts → DETACHED por default
        assertDoesNotThrow(() -> e.doAction(null));
    }

    private boolean isWindows() {
        return System.getProperty("os.name")
                .toLowerCase()
                .contains("win");
    }
}