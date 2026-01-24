package info.teksol.schemacode;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.messages.*;
import info.teksol.mc.profile.CompilerProfile;
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
import java.util.Base64;
import java.util.List;

public class SchemacodeCompiler {

    private static final ThreadLocal<CompilerProfile> compilerProfile = new ThreadLocal<>();

    static CompilerProfile getCompilerProfile() {
        return compilerProfile.get();
    }

    /**
     * Parses schemacode source into AST tree.
     *
     * @param messageConsumer message consumer
     * @return Top node of parsed AST tree
     */
    static DefinitionsContext parseSchematics(MessageConsumer messageConsumer, InputFiles inputFiles) {
        InputFile inputFile = inputFiles.getMainInputFile();
        final SchemacodeErrorListener errorListener = new SchemacodeErrorListener(messageConsumer, inputFile);

        final SchemacodeLexer lexer = new SchemacodeLexer(CharStreams.fromString(inputFile.getCode()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final SchemacodeParser parser = new SchemacodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return parser.definitions();
    }

    static AstDefinitions createDefinitions(InputFile inputFile, DefinitionsContext parseTree, MessageConsumer messageConsumer) {
        return AstSchematicsBuilder.generate(inputFile, parseTree, messageConsumer);
    }

    static Schematic buildSchematic(InputFiles inputFiles, AstDefinitions astDefinitions, CompilerProfile compilerProfile,
            MessageConsumer messageConsumer) {
        SchematicsBuilder builder = SchematicsBuilder.create(inputFiles, compilerProfile, astDefinitions, messageConsumer);
        return builder.buildSchematics();
    }

    public static CompilerOutput<byte[]> compile(MessageConsumer messageConsumer, InputFiles inputFiles,
            CompilerProfile compilerProfile) {
        SchemacodeCompiler.compilerProfile.set(compilerProfile);
        MessageLogger messageLogger = new MessageLogger(messageConsumer);

        InputFile inputFile = inputFiles.getMainInputFile();
        if (inputFile.getCode().isBlank()) {
            return new CompilerOutput<>(new byte[0]);
        }

        DefinitionsContext parseTree = parseSchematics(messageLogger, inputFiles);
        if (messageLogger.hasErrors()) return CompilerOutput.empty();

        AstDefinitions astDefinitions = createDefinitions(inputFile, parseTree, messageLogger);
        if (messageLogger.hasErrors()) return CompilerOutput.empty();

        Schematic schematic = buildSchematic(inputFiles, astDefinitions, compilerProfile, messageLogger);
        if (messageLogger.hasErrors()) return CompilerOutput.empty();

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematic, output);

            Emulator emulator = null;
            if (compilerProfile.isRun()) {
                EmulatorSchematic emulatorSchematic = schematic.toEmulatorSchematic(SchematicsMetadata.getMetadata());
                emulator = new BasicEmulator(messageLogger, compilerProfile, emulatorSchematic);
                emulator.run(compilerProfile.getStepLimit());
            }

            return new CompilerOutput<>(output.toByteArray(), schematic.filename(), emulator);
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error converting schematics to binary representation.");
        }
    }

    public static CompilerOutput<String> compileAndEncode(MessageConsumer messageConsumer, InputFiles inputFiles,
            CompilerProfile compilerProfile) {
        try {
            CompilerOutput<byte[]> binaryOutput = compile(messageConsumer, inputFiles, compilerProfile);

            String encoded = binaryOutput.output() != null
                    ? Base64.getEncoder().encodeToString(binaryOutput.output()) : "";
            return binaryOutput.withOutput(encoded);
        } catch (Exception e) {
            messageConsumer.addMessage(ToolMessage.error(ERR.INTERNAL_ERROR));
            return CompilerOutput.empty();
        }
    }

    private static boolean hasErrors(List<MindcodeMessage> messages) {
        return messages.stream().anyMatch(MindcodeMessage::isError);
    }
}
