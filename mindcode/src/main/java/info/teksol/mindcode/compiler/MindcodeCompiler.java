package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.DebugPrinter;
import info.teksol.mindcode.compiler.optimization.DiffDebugPrinter;
import info.teksol.mindcode.compiler.optimization.MindcodeOptimizer;
import info.teksol.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler<String> {
    private final CompilerProfile profile;
    private InstructionProcessor instructionProcessor;

    private final List<CompilerMessage> messages = new ArrayList<>();
    private final ANTLRErrorListener errorListener = new ErrorListener(messages);
    public MindcodeCompiler(CompilerProfile profile) {
        this.profile = profile;
    }

    @Override
    public CompilerOutput<String> compile(String sourceCode) {
        String instructions = "";

        try {
            long parseStart = System.nanoTime();
            final Seq program = parse(sourceCode);
            if (messages.stream().anyMatch(CompilerMessage::isError)) {
                return new CompilerOutput<>("", messages);
            }
            printParseTree(program);
            long parseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

            long compileStart = System.nanoTime();
            DirectiveProcessor.processDirectives(program, profile, messages::add);
            instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messages::add, profile);
            GeneratorOutput generated = generateCode(program);
            long compileTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - compileStart);

            long optimizeStart = System.nanoTime();
            List<LogicInstruction> result = List.of();
            if (profile.optimizationsActive() && generated.instructions().size() > 1) {
                result = optimize(generated);
            }
            long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

            if (profile.getFinalCodeOutput() != null) {
                debug("\nFinal code before resolving virtual instructions:\n");
                String output = switch (profile.getFinalCodeOutput()) {
                    case PLAIN      -> LogicInstructionPrinter.toString(instructionProcessor, result);
                    case FLAT_AST   -> LogicInstructionPrinter.toStringWithContextsShort(instructionProcessor, result);
                    case DEEP_AST   -> LogicInstructionPrinter.toStringWithContextsFull(instructionProcessor, result);
                    case SOURCE     -> LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, result, sourceCode);
                };
                debug(output);
            }

            info("Performance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.".formatted(parseTime, compileTime, optimizeTime));

            result = LogicInstructionLabelResolver.resolve(instructionProcessor, result);

            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (Exception e) {
            if (profile.isPrintStackTrace()) {
                e.printStackTrace();
            }
            messages.add(MindcodeMessage.error("Error while compiling source code."));
        }

        return new CompilerOutput<>(instructions, messages);
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(String sourceCode) {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        return AstNodeBuilder.generate(context);
    }

    /** Prints the parse tree according to level */
    private void printParseTree(Seq program) {
        if (profile.getParseTreeLevel() > 0) {
            debug("Parse tree:");
            debug(AstIndentedPrinter.printIndented(program, profile.getParseTreeLevel()));
            debug("");
        }
    }

    private GeneratorOutput generateCode(Seq program) {
        final LogicInstructionGenerator generator = new LogicInstructionGenerator(profile, instructionProcessor,
                messages::add);
        return generator.generate(program);
    }

    private List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        messages.add(
                MindcodeMessage.debug(profile.getOptimizationLevels().entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getKey().getOptionName()))
                        .map(e -> e.getKey().getOptionName() + " = " + e.getValue().name().toLowerCase())
                        .collect(Collectors.joining(",\n    ", "Active optimizations:\n    ", "\n"))
                )
        );

        final DebugPrinter debugPrinter = profile.getDebugLevel() > 0 && profile.optimizationsActive()
                ? new DiffDebugPrinter(profile.getDebugLevel()) : new NullDebugPrinter();

        MindcodeOptimizer optimizer = new MindcodeOptimizer(instructionProcessor, profile, messages::add);
        optimizer.setDebugPrinter(debugPrinter);
        List<LogicInstruction> result = optimizer.optimize(generatorOutput);
        debugPrinter.print(this::debug);
        return result;
    }

    private void info(String message) {
        messages.add(MindcodeMessage.info(message));
    }

    private void debug(String message) {
        messages.add(MindcodeMessage.debug(message));
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<CompilerMessage> errors;

        public ErrorListener(List<CompilerMessage> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                errors.add(MindcodeMessage.error("Syntax error on line " + line + ":" + charPositionInLine + ": " + msg));
            } else {
                errors.add(MindcodeMessage.error("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg));
            }
        }
    }
}
