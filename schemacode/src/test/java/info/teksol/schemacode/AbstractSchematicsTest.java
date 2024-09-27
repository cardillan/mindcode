package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.MessageLevel;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.Schematic;
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

    public static PositionArray pa(Position... positions) {
        return positions.length == 0 ? PositionArray.EMPTY : new PositionArray(positions);
    }

    // Block index generator. Class is re-instantiated for each test, index always starts from zero.
    private int index = 0;

    public Block block(List<String> labels, String blockType, Position position, Direction direction, Configuration configuration) {
        return new Block(index++, labels, BlockType.forName(blockType), position, direction, configuration);
    }

    public Block block(String blockType, Position position, Direction direction, Configuration configuration) {
        return new Block(index++, List.of(), BlockType.forName(blockType), position, direction, configuration);
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

    protected Schematic buildSchematics(String definition) {
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        return SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messageListener("buildSchematics"), null);
    }

    protected void buildSchematicsExpectingError(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messages::add, null);
        assertRegex(MessageLevel.ERROR, regex, messages);
    }

    protected void buildSchematicsExpectingWarning(String definition, @Language("RegExp") String regex) {
        List<CompilerMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messages::add, null);
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
