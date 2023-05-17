package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.Schematics;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractSchematicsTest {
    public static final Position P0_0 = Position.ORIGIN;
    public static final Position P1_0 = new Position(1, 0);
    public static final Position P2_0 = new Position(2, 0);
    public static final Position P3_0 = new Position(3, 0);

    public static Position p(int x, int y) {
        return new Position(x, y);
    }

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

    protected void parseSchematicsExpectingError(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        SchemacodeCompiler.parseSchematics(definition, messages::add);
        assertRegex(MessageLevel.ERROR, regex, messages);
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

    protected void buildSchematicsExpectingError(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations();
        SchemacodeCompiler.buildSchematics(definitions, compilerProfile, messages::add, null);
        assertRegex(MessageLevel.ERROR, regex, messages);
    }

    protected void buildSchematicsExpectingWarning(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations();
        SchemacodeCompiler.buildSchematics(definitions, compilerProfile, messages::add, null);
        assertRegex(MessageLevel.WARNING, regex, messages);
    }

    private void assertRegex(MessageLevel expectedLevel, String expectedRegex, List<CompilerMessage> messages) {
        List<String> list = messages.stream().filter(m -> m.level() == expectedLevel).map(CompilerMessage::message).toList();
        if (list.stream().anyMatch(s -> s.matches(expectedRegex))) {
            assertTrue(true);
        } else {
            fail("No message matched expected expression.\nExpected expression: %s, found messages: %s"
                    .formatted(expectedRegex, String.join("\n", list)));
        }
    }
}
