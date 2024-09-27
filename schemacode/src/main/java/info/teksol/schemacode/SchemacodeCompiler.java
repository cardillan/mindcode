package info.teksol.schemacode;

import info.teksol.mindcode.compiler.CompilerMessage;
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
    static DefinitionsContext parseSchematics(String definition, Consumer<CompilerMessage> messageListener) {
        ErrorListener errorListener = new ErrorListener(messageListener);

        final SchemacodeLexer lexer = new SchemacodeLexer(CharStreams.fromString(definition));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final SchemacodeParser parser = new SchemacodeParser(new BufferedTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        return parser.definitions();
    }

    static AstDefinitions createDefinitions(DefinitionsContext parseTree, Consumer<CompilerMessage> messageListener) {
        return AstSchematicsBuilder.generate(parseTree, messageListener);
    }

    static Schematic buildSchematic(AstDefinitions astDefinitions, CompilerProfile compilerProfile,
            Consumer<CompilerMessage> messageListener, Path basePath) {
        SchematicsBuilder builder = SchematicsBuilder.create(compilerProfile, astDefinitions, messageListener, basePath);
        return builder.buildSchematics();
    }

    public static CompilerOutput<byte[]> compile(String definition, CompilerProfile compilerProfile, Path basePath) {
        if (definition.isBlank()) {
            return new CompilerOutput<>(new byte[0], List.of());
        }

        List<CompilerMessage> messages = new ArrayList<>();
        DefinitionsContext parseTree = parseSchematics(definition, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        AstDefinitions astDefinitions = createDefinitions(parseTree, messages::add);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        Schematic schematic = buildSchematic(astDefinitions, compilerProfile, messages::add, basePath);
        if (hasErrors(messages)) return new CompilerOutput<>(null, messages);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            SchematicsIO.write(schematic, output);
            return new CompilerOutput<>(output.toByteArray(), messages);
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error converting schematics to binary representation.");
        }
    }

    public static CompilerOutput<String> compileAndEncode(String definition, CompilerProfile compilerProfile, Path basePath) {
        CompilerOutput<byte[]> binaryOutput = compile(definition, compilerProfile, basePath);

        String encoded = binaryOutput.output() != null
                ? Base64.getEncoder().encodeToString(binaryOutput.output()) : "";
        return binaryOutput.withOutput(encoded);
    }

    private static boolean hasErrors(List<CompilerMessage> messages) {
        return messages.stream().anyMatch(CompilerMessage::isError);
    }


    private static class ErrorListener extends BaseErrorListener {
        private final Consumer<CompilerMessage> messageListener;

        public ErrorListener(Consumer<CompilerMessage> messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                messageListener.accept(SchemacodeMessage.error("Syntax error on line " + line + ":" + charPositionInLine + ": " + msg));
            } else {
                messageListener.accept(SchemacodeMessage.error("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg));
            }
        }
    }
}
