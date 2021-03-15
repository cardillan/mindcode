package info.teksol.mindcode;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.*;
import org.antlr.v4.runtime.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        for (final String dirname : args) {
            final File[] files = new File(dirname).listFiles();
            Arrays.sort(files);
            for (final File file : files) {
                final StringWriter sw = new StringWriter();
                try (final FileReader reader = new FileReader(file)) {
                    reader.transferTo(sw);
                }
                System.out.println("=====> " + file);
                compile(sw.toString());
                System.out.println(file + " <=====");
            }
        }
    }

    private static void compile(String program) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq prog = AstNodeBuilder.generate(context);
        final List<MOpcode> result = MOpcodeLabelResolver.resolve(
                MOpcodePeepholeOptimizer.optimize(
                        MOpcodeGenerator.generateFrom(prog)
                )
        );

        final String opcodes = MOpcodePrinter.toString(result);
        System.out.println(opcodes);
        errors.forEach(System.err::println);
    }
}
