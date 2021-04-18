package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionLabelResolver;
import info.teksol.mindcode.mindustry.LogicInstructionPrinter;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class CompilerFacade {
    static Tuple2<String, List<String>> compile(String sourceCode) {
        String instructions = "";

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        try {
            final MindcodeParser.ProgramContext context = parser.program();
            final Seq prog = AstNodeBuilder.generate(context);

            List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(prog);
            result = LogicInstructionLabelResolver.resolve(result);
            instructions = LogicInstructionPrinter.toString(result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new Tuple2<>(instructions, errors);
    }
}
