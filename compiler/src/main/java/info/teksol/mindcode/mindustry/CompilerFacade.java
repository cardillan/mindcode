package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class CompilerFacade {
    public static CompilerOutput compile(String sourceCode, boolean enableOptimization) {
        String instructions = "";

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        final List<String> messages = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        try {
            final MindcodeParser.ProgramContext context = parser.program();
            final Seq prog = AstNodeBuilder.generate(context);

            List<LogicInstruction> result;
            if (enableOptimization) {
                result = LogicInstructionGenerator.generateAndOptimize(prog, messages::add);
            } else {
                result = LogicInstructionGenerator.generateUnoptimized(prog);
            }
            result = LogicInstructionLabelResolver.resolve(result);
            instructions = LogicInstructionPrinter.toString(result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new CompilerOutput(instructions, errors, messages);
    }
}
