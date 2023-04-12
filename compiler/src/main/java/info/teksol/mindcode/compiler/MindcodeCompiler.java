package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mindcode.compiler.optimization.OptimizationPipeline;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler {
    private final CompilerProfile profile;
    private InstructionProcessor instructionProcessor;

    private final List<CompilerMessage> messages = new ArrayList<>();
    private final ANTLRErrorListener errorListener = new ErrorListener(messages);

    MindcodeCompiler(CompilerProfile profile) {
        this.profile = profile;
    }

    @Override
    public CompilerOutput compile(String sourceCode) {
        String instructions = "";

        try {
            final Seq prog = parse(sourceCode);
            printParseTree(prog);

            DirectiveProcessor.processDirectives(prog, profile);
            instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(profile);

            List<LogicInstruction> result = generateCode(prog);

            if (profile.optimizationsActive() && result.size() > 1) {
                result = optimize(result);
            }

            if (profile.isPrintFinalCode()) {
                debug("\nFinal code before resolving virtual instructions:\n");
                debug(LogicInstructionPrinter.toString(instructionProcessor, result));
            }

            result = LogicInstructionLabelResolver.resolve(instructionProcessor, result);

            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (RuntimeException e) {
            if (profile.getDebugLevel() > 0) {
                // TODO: use specific command line argument to obtain stack trace
                e.printStackTrace();
            }
            messages.add(CompilerMessage.error(e.getMessage()));
        }

        return new CompilerOutput(instructions, messages);
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

    /** Prints the parse tree according to level */
    private void printParseTree(Seq prog) {
        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(AstIndentedPrinter.printIndented(prog, profile.getParseTreeLevel()));
            debug("");
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
        messages.add(
                CompilerMessage.debug(profile.getOptimizationLevels().entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getKey().getName()))
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining(",\n    ", "Active optimizations:\n    ", "\n"))
                )
        );

        final AccumulatingLogicInstructionPipeline terminus = new AccumulatingLogicInstructionPipeline();
        final DebugPrinter debugPrinter = profile.getDebugLevel() == 0 || profile.optimizationsActive()
                ? new DiffDebugPrinter(profile.getDebugLevel()) : new NullDebugPrinter();
        final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineForProfile(instructionProcessor,
                terminus, profile, debugPrinter, messages::add);
        program.forEach(pipeline::emit);
        pipeline.flush();

        debugPrinter.print(this::debug);
        return terminus.getResult();
    }

    private void debug(String message) {
        messages.add(CompilerMessage.debug(message));
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<CompilerMessage> errors;

        public ErrorListener(List<CompilerMessage> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            errors.add(CompilerMessage.error("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg));
        }
    }
}
