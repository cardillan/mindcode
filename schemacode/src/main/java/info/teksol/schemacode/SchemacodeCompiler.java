package info.teksol.schemacode;

import info.teksol.mindcode.MindcodeErrorListener;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.ast.AstSchematicsBuilder;
import info.teksol.schemacode.grammar.SchemacodeLexer;
import info.teksol.schemacode.grammar.SchemacodeParser;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.Schematic;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

public class SchemacodeCompiler {

    /**
     * Parses schemacode source into AST tree.
     *
     * @param definition source code
     * @param messageConsumer message consumer
     * @return Top node of parsed AST tree
     */
    static DefinitionsContext parseSchematics(Consumer<MindcodeMessage> messageConsumer, InputFiles inputFiles) {
        InputFile inputFile = inputFiles.getMainInputFile();
        final MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);

        final SchemacodeLexer lexer = new SchemacodeLexer(CharStreams.fromString(inputFile.getCode()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final SchemacodeParser parser = new SchemacodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return parser.definitions();
    }

    static AstDefinitions createDefinitions(InputFile inputFile, DefinitionsContext parseTree, Consumer<MindcodeMessage> messageListener) {
        return AstSchematicsBuilder.generate(inputFile, parseTree, messageListener);
    }

    static Schematic buildSchematic(InputFiles inputFiles, AstDefinitions astDefinitions, CompilerProfile compilerProfile,
            Consumer<MindcodeMessage> messageListener) {
        SchematicsBuilder builder = SchematicsBuilder.create(inputFiles, compilerProfile, astDefinitions, messageListener);
        return builder.buildSchematics();
    }

    public static CompilerOutput<byte[]> compile(InputFiles inputFiles, CompilerProfile compilerProfile) {
        InputFile inputFile = inputFiles.getMainInputFile();
        if (inputFile.getCode().isBlank()) {
            return new CompilerOutput<>(new byte[0], List.of());
        }

        List<MindcodeMessage> messages = new ArrayList<>();
        DefinitionsContext parseTree = parseSchematics(messages::add, inputFiles);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        AstDefinitions astDefinitions = createDefinitions(inputFile, parseTree, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        Schematic schematic = buildSchematic(inputFiles, astDefinitions, compilerProfile, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematic, output);
            return new CompilerOutput<>(output.toByteArray(), messages);
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error converting schematics to binary representation.");
        }
    }

    public static CompilerOutput<String> compileAndEncode(InputFiles inputFiles, CompilerProfile compilerProfile) {
        CompilerOutput<byte[]> binaryOutput = compile(inputFiles, compilerProfile);

        String encoded = binaryOutput.output() != null
                ? Base64.getEncoder().encodeToString(binaryOutput.output()) : "";
        return binaryOutput.withOutput(encoded);
    }

    private static boolean hasErrors(List<MindcodeMessage> messages) {
        return messages.stream().anyMatch(MindcodeMessage::isError);
    }
}
