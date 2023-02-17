package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.functions.FunctionMapperFactory;
import info.teksol.mindcode.mindustry.generator.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import info.teksol.mindcode.mindustry.optimisation.DebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.DiffDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.NullDebugPrinter;
import info.teksol.mindcode.mindustry.optimisation.OptimisationPipeline;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ANTLRErrorListener;

public class MindcodeCompiler implements Compiler {
    private final CompilerProfile profile;
    private final InstructionProcessor instructionProcessor;

    private final List<String> errors = new ArrayList<>();
    private final List<String> messages = new ArrayList<>();
    private final ANTLRErrorListener errorListener = new ErrorListener(errors);

    MindcodeCompiler(CompilerProfile profile, InstructionProcessor instructionProcessor) {
        this.profile = profile;
        this.instructionProcessor = instructionProcessor;
    }

    @Override
    public CompilerOutput compile(String sourceCode) {
        String instructions = "";

        try {
            // 1: parse
            final Seq prog = parse(sourceCode);
            printParseTree(prog);

            // 2: Compile to code
            List<LogicInstruction> result = generateCode(prog);

            // 3: Optimize code
            if (!profile.getOptimisations().isEmpty()) {
                result = optimize(result);
            }

            // 4: Resolve symbolic labels
            result = LogicInstructionLabelResolver.resolve(instructionProcessor, result);

            // 5: Convert to final output
            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new CompilerOutput(instructions, errors, messages);
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(String sourceCode) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        return AstNodeBuilder.generate(context);
    }

    /**
     * Prints the parse tree according to level
     */
    private void printParseTree(Seq prog) {
        if (profile.getParseTreeLevel() > 0) {
            messages.add("Parse tree:");
            messages.add(AstIndentedPrinter.printIndented(prog, profile.getParseTreeLevel()));
            messages.add("");
        }
    }

    private List<LogicInstruction> generateCode(Seq program) {
        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        final LogicInstructionGenerator generator = new LogicInstructionGenerator(instructionProcessor,
                FunctionMapperFactory.getFunctionMapper(instructionProcessor, messages::add), terminus);
        generator.start(program);
        terminus.flush();
        return terminus.getResult();
    }

    private List<LogicInstruction> optimize(List<LogicInstruction> program) {
        messages.add(profile.getOptimisations().stream()
                .sorted()
                .map(Object::toString)
                .collect(Collectors.joining(",\n    ", "Active optimisations:\n    ", "\n")));

        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        final DebugPrinter debugPrinter = profile.getDebugLevel() == 0 || profile.getOptimisations().isEmpty()
                ? new NullDebugPrinter() : new DiffDebugPrinter(profile.getDebugLevel());
        final LogicInstructionPipeline pipeline = OptimisationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, debugPrinter, messages::add);
        program.forEach(pipeline::emit);
        pipeline.flush();

        debugPrinter.print(messages::add);
        return terminus.getResult();
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
