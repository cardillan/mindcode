package io.github.cardillan.schemacode;

import io.github.cardillan.mc.common.CompilerOutput;
import io.github.cardillan.mc.common.InputFile;
import io.github.cardillan.mc.common.InputFiles;
import io.github.cardillan.mc.common.Statistics;
import io.github.cardillan.mc.emulator.Emulator;
import io.github.cardillan.mc.emulator.EmulatorSchematic;
import io.github.cardillan.mc.emulator.mimex.BasicEmulator;
import io.github.cardillan.mc.messages.ERR;
import io.github.cardillan.mc.messages.MessageConsumer;
import io.github.cardillan.mc.messages.MessageLogger;
import io.github.cardillan.mc.messages.ToolMessage;
import io.github.cardillan.mc.profile.CompilerProfile;
import io.github.cardillan.schemacode.ast.AstDefinitions;
import io.github.cardillan.schemacode.ast.AstSchematicsBuilder;
import io.github.cardillan.schemacode.grammar.SchemacodeLexer;
import io.github.cardillan.schemacode.grammar.SchemacodeParser;
import io.github.cardillan.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import io.github.cardillan.schemacode.mindustry.SchematicsIO;
import io.github.cardillan.schemacode.schematics.Schematic;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
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
