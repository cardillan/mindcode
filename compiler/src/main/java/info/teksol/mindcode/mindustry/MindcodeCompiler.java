package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;

public class MindcodeCompiler implements Compiler {
    private final CompilerProfile profile;
    private final InstructionProcessor instructionProcessor;

    MindcodeCompiler(CompilerProfile profile, InstructionProcessor instructionProcessor) {
        this.profile = profile;
        this.instructionProcessor = instructionProcessor;
    }

    @Override
    public CompilerOutput compile(String sourceCode) {
        String instructions = "";
        final List<String> errors = new ArrayList<>();
        final List<String> messages = new ArrayList<>();

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        parser.addErrorListener(new ErrorListener(errors));

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
                result = LogicInstructionGenerator.generateUnoptimized(instructionProcessor, prog);
            } else {
                messages.add("Active optimisations: "
                        + profile.getOptimisations().stream().map(Object::toString).collect(Collectors.joining(", ")));
                result = LogicInstructionGenerator.generateAndOptimize(instructionProcessor, prog, profile, messages::add);
            }
            result = LogicInstructionLabelResolver.resolve(instructionProcessor, result);
            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new CompilerOutput(instructions, errors, messages);
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<String> errors;

        public ErrorListener(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
        }
    }
}
