package info.teksol.schemacode;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.Schematic;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSchematicsTest {
    protected InputFiles inputFiles = InputFiles.create();

    public static final Position P0_0 = Position.ORIGIN;
    public static final Position P1_0 = new Position(1, 0);
    public static final Position P2_0 = new Position(2, 0);
    public static final Position P3_0 = new Position(3, 0);

    public static Position p(int x, int y) {
        return new Position(x, y);
    }

    protected static void assertAstEquals(AstDefinitions expected, AstDefinitions actual) {
        Assertions.assertEquals(expected.withEmptyPosition(), actual.withEmptyPosition());
    }

    protected static void assertAstEquals(Schematic expected, Schematic actual) {
        Assertions.assertEquals(expected, actual);
    }

    public static PositionArray pa(Position... positions) {
        return positions.length == 0 ? PositionArray.EMPTY : new PositionArray(positions);
    }

    protected SourcePosition pos(int line, int column) {
        //return new SourcePosition(inputFiles.getMainInputFile(), line, column);
        return SourcePosition.EMPTY;
    }

    // Block index generator. Class is re-instantiated for each test, index always starts from zero.
    private int index = 0;

    public Block block(SourcePosition pos, List<String> labels, String blockType, Position position, Direction direction, Configuration configuration) {
        return new Block(pos, index++, labels, SchematicsMetadata.getMetadata().getBlockByName(blockType), position, direction, configuration);
    }

    public Block block(SourcePosition pos, String blockType, Position position, Direction direction, Configuration configuration) {
        return new Block(pos, index++, List.of(), SchematicsMetadata.getMetadata().getBlockByName(blockType), position, direction, configuration);
    }

    /**
     * Creates a message listener that throws error at the moment error message is generated. Used in unit tests where
     * no errors or warnings are expected. Allows placing breakpoint here to break at the moment the error occurs.
     *
     * @param methodName name of the method being tested
     * @return message listener which throws on errors
     */
    private MessageConsumer messageListener(String methodName) {
        return message -> {
            if (message.isError() || message.isWarning()) {
                throw new RuntimeException("Unexpected error returned from " + methodName + ": " + message.message());
            }
        };
    }

    protected DefinitionsContext parseSchematics(InputFiles inputFiles) {
        return SchemacodeCompiler.parseSchematics(messageListener("parseSchematics"), inputFiles);
    }

    protected void parseSchematicsExpectingMessages(ExpectedMessages expectedMessages, String source) {
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        SchemacodeCompiler.parseSchematics(messages::add, InputFiles.fromSource(source));
        expectedMessages.validate(messages);
    }

    protected AstDefinitions createDefinitions(InputFiles inputFiles) {
        DefinitionsContext parseTree = parseSchematics(inputFiles);
        return SchemacodeCompiler.createDefinitions(inputFiles.getMainInputFile(), parseTree, messageListener("createDefinitions"));
    }

    protected AstDefinitions createDefinitions(String source) {
        inputFiles.registerSource(source);
        return createDefinitions(inputFiles);
    }

    protected Schematic buildSchematics(String source) {
        inputFiles.registerSource(source);
        AstDefinitions definitions = createDefinitions(source).withEmptyPosition();
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false, false);
        return SchemacodeCompiler.buildSchematic(inputFiles, definitions, compilerProfile, messageListener("buildSchematics"));
    }

    protected void assertGeneratesErrors(ExpectedMessages expectedMessages, String source) {
        InputFiles inputFiles = InputFiles.fromSource(source);
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(inputFiles);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false, false);
        SchemacodeCompiler.buildSchematic(inputFiles, definitions, compilerProfile, messages::add);
        expectedMessages.validate(messages);
    }

    protected void assertGeneratesWarnings(ExpectedMessages expectedMessages, String source) {
        InputFiles inputFiles = InputFiles.fromSource(source);
        expectedMessages.addRegex("Created schematic '.*' with dimensions .*").ignored();
        List<MindcodeMessage> messages = new ArrayList<>();
        AstDefinitions definitions = createDefinitions(inputFiles);
        CompilerProfile compilerProfile = CompilerProfile.fullOptimizations(false, false);
        SchemacodeCompiler.buildSchematic(inputFiles, definitions, compilerProfile, messages::add);
        expectedMessages.validate(messages);
    }
}
