package info.teksol.schemacode;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.Schematic;
import info.teksol.util.ExpectedMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static info.teksol.mindcode.InputPosition.EMPTY;

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
        return new Block(EMPTY, index++, labels, BlockType.forName(blockType), position, direction, configuration);
    }

    public Block block(String blockType, Position position, Direction direction, Configuration configuration) {
        return new Block(EMPTY, index++, List.of(), BlockType.forName(blockType), position, direction, configuration);
    }

    /**
     * Creates a message listener that throws error at the moment error message is generated. Used in unit tests where
     * no errors or warnings are expected. Allows placing breakpoint here to break at the moment the error occurs.
     *
     * @param methodName name of the method being tested
     * @return message listener which throws on errors
     */
    private Consumer<MindcodeMessage> messageListener(String methodName) {
        return message -> {
            if (message.isError() || message.isWarning()) {
                throw new RuntimeException("Unexpected error returned from " + methodName + ": " + message.message());
            }
        };
    }

    protected DefinitionsContext parseSchematics(String definition) {
        return SchemacodeCompiler.parseSchematics(InputFile.createSourceFile(definition),
                messageListener("parseSchematics"));
    }

    protected void parseSchematicsExpectingMessages(ExpectedMessages expectedMessages, String definition) {
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        SchemacodeCompiler.parseSchematics(InputFile.createSourceFile(definition), messages::add);
        expectedMessages.validate(messages);
    }

    protected AstDefinitions createDefinitions(String definition) {
        DefinitionsContext parseTree = parseSchematics(definition);
        return SchemacodeCompiler.createDefinitions(InputFile.EMPTY, parseTree, messageListener("createDefinitions"));
    }

    protected Schematic buildSchematics(String definition) {
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        return SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messageListener("buildSchematics"),
                InputFile.EMPTY, null);
    }

    protected void assertGeneratesErrors(ExpectedMessages expectedMessages, String definition) {
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messages::add, InputFile.EMPTY, null);
        expectedMessages.validate(messages);
    }

    protected void assertGeneratesWarnings(ExpectedMessages expectedMessages, String definition) {
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(definition);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false);
        SchemacodeCompiler.buildSchematic(definitions, compilerProfile, messages::add, InputFile.EMPTY, null);
        expectedMessages.validate(messages);
    }
}
