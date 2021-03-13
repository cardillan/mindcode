package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.*;
import org.antlr.v4.runtime.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        final StringWriter sw = new StringWriter();
        try (final FileReader reader = new FileReader(args[0])) {
            reader.transferTo(sw);
        }

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sw.toString()));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq program = AstNodeBuilder.generate(context);
        final List<MOpcode> result = MOpcodeLabelResolver.resolve(
                MOpcodePeepholeOptimizer.optimize(
                        MOpcodeGenerator.generateFrom(program)
                )
        );

        final String opcodes = MOpcodePrinter.toString(result);
        System.out.println(opcodes);
        errors.forEach(System.err::println);
    }
}
