package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.schema.Schematics;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractSchematicsTest {

    /**
     * Creates a message listener that throws error at the moment error message is generated. Used in unit tests where
     * no errors or warnings are expected. Allows placing breakpoint here to break at the moment the error occurs.
     *
     * @param methodName name of the method being tested
     * @return message listener which throws on errors
     */
    private Consumer<CompilerMessage> messageListener(String methodName) {
        return message -> {
            if (message.isError() || message.isWarning()) {
                throw new RuntimeException("Unexpected error returned from " + methodName + ": " + message.message());
            }
        };
    }

    protected DefinitionsContext parseSchematics(String definition) {
        return SchemacodeCompiler.parseSchematics(definition, messageListener("parseSchematics"));
    }

    protected void parseSchematicsExpectingMessage(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        SchemacodeCompiler.parseSchematics(definition, messages::add);
        assertRegex(regex, messages);
    }

    protected AstDefinitions createDefinitions(String definition) {
        DefinitionsContext parseTree = parseSchematics(definition);
        return SchemacodeCompiler.createDefinitions(parseTree, messageListener("createDefinitions"));
    }

    protected Schematics buildSchematics(String definition) {
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations();
        return SchemacodeCompiler.buildSchematics(definitions, compilerProfile, messageListener("buildSchematics"), null);
    }

    protected void buildSchematicsExpectingMessage(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations();
        SchemacodeCompiler.buildSchematics(definitions, compilerProfile, messages::add, null);
        assertRegex(regex, messages);
    }

    private void assertRegex(String expectedRegex, List<CompilerMessage> messages) {
        List<String> list = messages.stream().filter(CompilerMessage::isError).map(CompilerMessage::message).toList();
        if (list.stream().anyMatch(s -> s.matches(expectedRegex))) {
            assertTrue(true);
        } else {
            fail("No message matched expected expression.\nExpected expression: %s, found messages: %s"
                    .formatted(expectedRegex, String.join("\n", list)));
        }
    }
}
