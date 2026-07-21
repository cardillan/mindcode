package info.teksol.schemacode;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.common.Statistics;
import info.teksol.mc.emulator.Emulator;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.messages.MessageLogger;
import info.teksol.mc.messages.ToolMessage;
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

public class SchemacodeCompiler {
    private final MessageLogger messageConsumer;
    private Statistics statistics;

    public SchemacodeCompiler(MessageConsumer messageConsumer) {
        this.messageConsumer = new MessageLogger(messageConsumer);
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

    public CompilerOutput<byte[]> compile(InputFiles inputFiles, CompilerProfile compilerProfile) {

        InputFile inputFile = inputFiles.getMainInputFile();
        if (inputFile.getCode().isBlank()) {
            return new CompilerOutput<>(new byte[0]);
        }

        DefinitionsContext parseTree = parseSchematics(messageConsumer, inputFiles);
        if (messageConsumer.hasErrors()) return CompilerOutput.empty();

        AstDefinitions astDefinitions = createDefinitions(inputFile, parseTree, messageConsumer);
        if (messageConsumer.hasErrors()) return CompilerOutput.empty();

        SchematicsBuilder builder = SchematicsBuilder.create(inputFiles, compilerProfile, astDefinitions, messageConsumer);
        Schematic schematic = builder.buildSchematics();
        statistics = builder.getStatistics();
        if (messageConsumer.hasErrors()) return CompilerOutput.empty();

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematic, output);

            Emulator emulator = null;
            if (compilerProfile.isRun()) {
                EmulatorSchematic emulatorSchematic = schematic.toEmulatorSchematic(SchematicsMetadata.getMetadata());
                emulator = new BasicEmulator(messageConsumer, compilerProfile, emulatorSchematic);
                emulator.run(compilerProfile.getStepLimit());
            }

            return new CompilerOutput<>(output.toByteArray(), schematic.filename(), emulator);
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error converting schematics to binary representation.");
        }
    }

    public CompilerOutput<String> compileAndEncode(MessageConsumer messageConsumer, InputFiles inputFiles,
            CompilerProfile compilerProfile) {
        try {
            CompilerOutput<byte[]> binaryOutput = compile(inputFiles, compilerProfile);

            String encoded = binaryOutput.output() != null
                    ? Base64.getEncoder().encodeToString(binaryOutput.output()) : "";
            return binaryOutput.withOutput(encoded);
        } catch (Exception e) {
            messageConsumer.addMessage(ToolMessage.error(ERR.INTERNAL_ERROR));
            return CompilerOutput.empty();
        }
    }

    public Statistics getStatistics() {
        Statistics stats = statistics == null ? new Statistics() : statistics;
        return stats.add(messageConsumer.getErrorCount(), messageConsumer.getWarningCount());
    }
}
