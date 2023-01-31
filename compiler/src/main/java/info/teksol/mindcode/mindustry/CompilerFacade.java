package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.*;

public class CompilerFacade {
    public static CompilerOutput compile(String sourceCode, boolean enableOptimization) {
        return compile(sourceCode, enableOptimization ? CompileProfile.fullOptimizations() : CompileProfile.noOptimizations());
    }
    
    public static CompilerOutput compile(String sourceCode, CompileProfile profile) {
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
            if (profile.getParseTreeLevel() > 0) {
                messages.add("Parse tree:");
                messages.add(AstIndentedPrinter.printIndented(prog, profile.getParseTreeLevel()));
                messages.add("");
            }

            List<LogicInstruction> result;
            if (profile.getOptimisations().isEmpty()) {
                result = LogicInstructionGenerator.generateUnoptimized(prog);
            } else {
                messages.add("Active optimisations: "
                        + profile.getOptimisations().stream().map(Object::toString).collect(Collectors.joining(", ")));
                result = LogicInstructionGenerator.generateAndOptimize(prog, profile, messages::add);
            }
            result = LogicInstructionLabelResolver.resolve(result);
            instructions = LogicInstructionPrinter.toString(result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new CompilerOutput(instructions, errors, messages);
    }
}
