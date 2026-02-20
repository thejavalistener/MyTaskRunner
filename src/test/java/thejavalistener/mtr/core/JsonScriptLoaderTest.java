package thejavalistener.mtr.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonScriptLoaderTest {

    @TempDir
    Path tmp;

    @Test
    void load_simple_script_creates_action() throws Exception {

        String json = """
        {
          "steps": [
            {
              "action": "MkDir",
              "path": "miDir"
            }
          ]
        }
        """;

        Path file = tmp.resolve("test.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        assertEquals("test.json", script.getScriptName());

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertEquals("MkDir", actions.get(0).getClass().getSimpleName());
    }
    
    @Test
    void load_resolves_variables() throws Exception {

        String json = """
        {
          "vars": {
            "base": "carpeta",
            "nombre": "final"
          },
          "steps": [
            {
              "action": "MkDir",
              "path": "${base}/${nombre}"
            }
          ]
        }
        """;

        Path file = tmp.resolve("vars.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());

        MyAction action = actions.get(0);

        assertEquals("MkDir", action.getClass().getSimpleName());

        // inspeccionamos el campo path vía reflection
        var field = action.getClass().getDeclaredField("path");
        field.setAccessible(true);
        String value = (String) field.get(action);

        assertEquals("carpeta/final", value);
    }    
    
    @Test
    void load_resolves_nested_variables() throws Exception {

        String json = """
        {
          "vars": {
            "a": "uno",
            "b": "${a}/dos",
            "c": "${b}/tres"
          },
          "steps": [
            {
              "action": "MkDir",
              "path": "${c}"
            }
          ]
        }
        """;

        Path file = tmp.resolve("nested.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();
        assertEquals(1, actions.size());

        MyAction action = actions.get(0);

        var field = action.getClass().getDeclaredField("path");
        field.setAccessible(true);
        String value = (String) field.get(action);

        assertEquals("uno/dos/tres", value);
    }
    
    @Test
    void load_fails_if_action_class_not_found() throws Exception {

        String json = """
        {
          "steps": [
            {
              "action": "NoExiste",
              "path": "algo"
            }
          ]
        }
        """;

        Path file = tmp.resolve("bad.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                script::getScriptActions
        );

        assertTrue(ex.getMessage().contains("NoExiste"));
    }
    
    @Test
    void load_does_not_fail_when_json_has_unknown_property() throws Exception {
        String json = """
        {
          "steps": [
            {
              "action": "MkDir",
              "path": "algo",
              "propiedadInexistente": "valor"
            }
          ]
        }
        """;

        Path file = tmp.resolve("unknown-prop.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertEquals("MkDir", actions.get(0).getClass().getSimpleName());
    }
    
    @Test
    void load_ignores_unknown_property_if_setter_not_found() throws Exception {

        String json = """
        {
          "steps": [
            {
              "action": "MkDir",
              "path": "algo",
              "propiedadInexistente": "valor"
            }
          ]
        }
        """;

        Path file = tmp.resolve("unknown-prop.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertEquals("MkDir", actions.get(0).getClass().getSimpleName());
    }
    
    @Test
    void run_returns_error_if_validation_fails() throws Exception {

        String json = """
        {
          "steps": [
            {
              "action": "Remove",
              "path": "no-existe",
              "stopScriptOnError": true
            }
          ]
        }
        """;

        Path file = tmp.resolve("invalid.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        int result;

        try {
            result = script.run();
        } catch (Throwable t) {
            result = MyScript.ERROR;
        }

        assertEquals(MyScript.ERROR, result);

        assertEquals(MyScript.ERROR, result);
    }}