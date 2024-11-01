package info.teksol.schemacode;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.ast.AstDefinitions;
import info.teksol.schemacode.ast.AstSchematicsBuilder;
import info.teksol.schemacode.grammar.SchemacodeLexer;
import info.teksol.schemacode.grammar.SchemacodeParser;
import info.teksol.schemacode.grammar.SchemacodeParser.DefinitionsContext;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.Schematic;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.antlr.v4.runtime.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Consumer;

public class SchemacodeCompiler {

    /**
     * Parses schemacode source into AST tree.
     *
     * @param definition source code
     * @param messageListener message listener
     * @return Top node of parsed AST tree
     */
    static DefinitionsContext parseSchematics(InputFile inputFile, Consumer<MindcodeMessage> messageListener) {
        ErrorListener errorListener = new ErrorListener(messageListener);

        final SchemacodeLexer lexer = new SchemacodeLexer(CharStreams.fromString(inputFile.code()));
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

    static Schematic buildSchematic(AstDefinitions astDefinitions, CompilerProfile compilerProfile,
            Consumer<MindcodeMessage> messageListener, InputFile inputFile, Path basePath) {
        SchematicsBuilder builder = SchematicsBuilder.create(compilerProfile, astDefinitions, messageListener, inputFile, basePath);
        return builder.buildSchematics();
    }

    public static CompilerOutput<byte[]> compile(InputFile inputFile, CompilerProfile compilerProfile, Path basePath) {
        if (inputFile.code().isBlank()) {
            return new CompilerOutput<>(new byte[0], List.of());
        }

        List<MindcodeMessage> messages = new ArrayList<>();
        DefinitionsContext parseTree = parseSchematics(inputFile, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        AstDefinitions astDefinitions = createDefinitions(inputFile, parseTree, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        Schematic schematic = buildSchematic(astDefinitions, compilerProfile, messages::add, inputFile, basePath);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematic, output);
            return new CompilerOutput<>(output.toByteArray(), messages);
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error converting schematics to binary representation.");
        }
    }

    public static CompilerOutput<String> compileAndEncode(InputFile inputFile, CompilerProfile compilerProfile, Path basePath) {
        CompilerOutput<byte[]> binaryOutput = compile(inputFile, compilerProfile, basePath);

        String encoded = binaryOutput.output() != null
                ? Base64.getEncoder().encodeToString(binaryOutput.output()) : "";
        return binaryOutput.withOutput(encoded);
    }

    private static boolean hasErrors(List<MindcodeMessage> messages) {
        return messages.stream().anyMatch(MindcodeMessage::isError);
    }


    private static class ErrorListener extends BaseErrorListener {
        private final Consumer<MindcodeMessage> messageListener;

        public ErrorListener(Consumer<MindcodeMessage> messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                messageListener.accept(SchemacodeCompilerMessage.error("Syntax error on line " + line + ":" + charPositionInLine + ": " + msg));
            } else {
                messageListener.accept(SchemacodeCompilerMessage.error("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg));
            }
        }
    }
}
