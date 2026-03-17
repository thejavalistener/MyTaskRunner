package thejavalistener.mtr.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thejavalistener.mtr.actions.FileCopy;

public class JsonScriptConditionsTest 
{
    @TempDir
    Path tmp;

    @Test
    void ifdef_executes_when_variable_exists() throws Exception {

        String json = """
        {
          "vars": { "x": "1" },
          "steps": [
            { "action": "MkDir", "to": "a", "ifdef": "x" }
          ]
        }
        """;

        Path file = tmp.resolve("ifdef.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertFalse(actions.get(0).isMustSkipped());
    }

    @Test
    void ifndef_skips_when_variable_exists() throws Exception {

        String json = """
        {
          "vars": { "x": "1" },
          "steps": [
            { "action": "MkDir", "to": "a", "ifndef": "x" }
          ]
        }
        """;

        Path file = tmp.resolve("ifndef.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertTrue(actions.get(0).isMustSkipped());
    }

    @Test
    void ifvar_evaluates_true_condition() throws Exception {

        String json = """
        {
          "vars": { "build": "10" },
          "steps": [
            { "action": "MkDir", "to": "a", "ifvar": "${build} >= 5" }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar1.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertFalse(actions.get(0).isMustSkipped());
    }

    @Test
    void ifvar_evaluates_false_condition() throws Exception {

        String json = """
        {
          "vars": { "build": "3" },
          "steps": [
            { "action": "MkDir", "to": "a", "ifvar": "${build} >= 5" }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar2.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertTrue(actions.get(0).isMustSkipped());
    }

    @Test
    void invalid_ifvar_expression_should_throw() throws Exception {

        String json = """
        {
          "vars": { "build": "10" },
          "steps": [
            { "action": "MkDir", "to": "a", "ifvar": "${build} >>> 5" }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_bad.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        assertThrows(RuntimeException.class, script::getScriptActions);
    }
    
    @Test
    void ifdef_false_should_not_evaluate_ifvar() throws Exception {

        String json = """
        {
          "steps": [
            {
              "action": "MkDir",
              "to": "a",
              "ifdef": "noExiste",
              "ifvar": "${otra} >= 5"
            }
          ]
        }
        """;

        Path file = tmp.resolve("combo3.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertTrue(actions.get(0).isMustSkipped());
    }
    
    @Test
    void load_filecopy_with_executeIf() throws Exception {

        Path src = Files.writeString(tmp.resolve("a.txt"), "hola");
        Path dest = tmp.resolve("b.txt");

        String json = """
        {
          "steps": [
            {
              "action": "FileCopy",
              "from": "%s",
              "to": "%s",
              "executeIf": "notExists"
            }
          ]
        }
        """.formatted(
                src.toString().replace("\\", "\\\\"),
                dest.toString().replace("\\", "\\\\")
        );

        Path file = tmp.resolve("executeIf.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);

        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertFalse(actions.get(0).isMustSkipped());

        FileCopy fc = (FileCopy) actions.get(0);
        assertEquals("notExists", fc.getExecuteIf());
    }
    
    @Test
    void load_ifvar_in_true() throws Exception
    {
        String json = """
        {
          "vars": {
            "installType":"1"
          },
          "steps": [
            {
              "action":"FileCopy",
              "from":"D:/tmp/a.txt",
              "to":"D:/tmp/b.txt",
              "ifvar":"${installType} in (1,2,3)"
            }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_in_true.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertFalse(actions.get(0).isMustSkipped());
    }
    
    @Test
    void load_ifvar_in_false() throws Exception
    {
        String json = """
        {
          "vars": {
            "installType":"4"
          },
          "steps": [
            {
              "action":"FileCopy",
              "from":"D:/tmp/a.txt",
              "to":"D:/tmp/b.txt",
              "ifvar":"${installType} in (1,2,3)"
            }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_in_false.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertTrue(actions.get(0).isMustSkipped());
    }
    
    @Test
    void load_ifvar_not_in_true() throws Exception
    {
        String json = """
        {
          "vars": {
            "installType":"4"
          },
          "steps": [
            {
              "action":"FileCopy",
              "from":"D:/tmp/a.txt",
              "to":"D:/tmp/b.txt",
              "ifvar":"${installType} not in (1,2,3)"
            }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_not_in_true.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertFalse(actions.get(0).isMustSkipped());
    }
    
    @Test
    void load_ifvar_not_in_false() throws Exception
    {
        String json = """
        {
          "vars": {
            "installType":"2"
          },
          "steps": [
            {
              "action":"FileCopy",
              "from":"D:/tmp/a.txt",
              "to":"D:/tmp/b.txt",
              "ifvar":"${installType} not in (1,2,3)"
            }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_not_in_false.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertTrue(actions.get(0).isMustSkipped());
    }
    
    @Test
    void load_ifvar_in_with_spaces_and_strings() throws Exception
    {
        String json = """
        {
          "vars": {
            "curso":"Proyecto_01"
          },
          "steps": [
            {
              "action":"FileCopy",
              "from":"D:/tmp/a.txt",
              "to":"D:/tmp/b.txt",
              "ifvar":"${curso} in ( Proyecto_01 , Proyecto_02 )"
            }
          ]
        }
        """;

        Path file = tmp.resolve("ifvar_in_spaces_strings.json");
        Files.writeString(file, json);

        MyScript script = JsonScriptLoader.load(file);
        List<MyAction> actions = script.getScriptActions();

        assertEquals(1, actions.size());
        assertFalse(actions.get(0).isMustSkipped());
    }
}