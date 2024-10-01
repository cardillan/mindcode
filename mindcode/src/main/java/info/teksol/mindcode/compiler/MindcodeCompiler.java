package info.teksol.mindcode.compiler;

import info.teksol.mindcode.MindcodeException;
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
import info.teksol.mindcode.compiler.optimization.NullDebugPrinter;
import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.processor.ExecutionException;
import info.teksol.mindcode.processor.MindustryMemory;
import info.teksol.mindcode.processor.Processor;
import info.teksol.mindcode.processor.ProcessorFlag;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler<String> {
    private final CompilerProfile profile;
    private InstructionProcessor instructionProcessor;

    private final List<CompilerMessage> messages = new ArrayList<>();
    private final ErrorListener errorListener = new ErrorListener(messages);
    public MindcodeCompiler(CompilerProfile profile) {
        this.profile = profile;
    }

    @Override
    public CompilerOutput<String> compile(List<SourceFile> sourceFiles) {
        String instructions = "";
        RunResults runResults = new RunResults(null,0);

        try {
            long parseStart = System.nanoTime();
            Seq program = null;
            for (SourceFile sourceFile : sourceFiles) {
                final Seq next = parse(sourceFile);
                program = Seq.append(program, next);
                if (messages.stream().anyMatch(CompilerMessage::isError)) {
                    return new CompilerOutput<>("", messages, null, 0);
                }
            }
            printParseTree(program);
            long parseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

            long compileStart = System.nanoTime();
            DirectiveProcessor.processDirectives(program, profile, messages::add);
            instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messages::add, profile);
            GeneratorOutput generated = generateCode(program);
            long compileTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - compileStart);

            long optimizeStart = System.nanoTime();
            List<LogicInstruction> result;
            if (profile.optimizationsActive() && generated.instructions().size() > 1) {
                result = optimize(generated);
            } else {
                result = generated.instructions();
            }
            long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

            // Sort variables before final code printout
            LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(instructionProcessor, profile);
            result = resolver.sortVariables(result);

            if (profile.getFinalCodeOutput() != null) {
                debug("\nFinal code before resolving virtual instructions:\n");
                String output = switch (profile.getFinalCodeOutput()) {
                    case PLAIN      -> LogicInstructionPrinter.toStringWithLineNumbers(instructionProcessor, result);
                    case FLAT_AST   -> LogicInstructionPrinter.toStringWithContextsShort(instructionProcessor, result);
                    case DEEP_AST   -> LogicInstructionPrinter.toStringWithContextsFull(instructionProcessor, result);
                    case SOURCE     -> LogicInstructionPrinter.toStringWithSourceCode(instructionProcessor, result);
                };
                debug(output);
            }

            result = resolver.resolveLabels(result);

            if (profile.isRun()) {
                long runStart = System.nanoTime();
                runResults = run(result);
                long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
                info("Performance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.".formatted(parseTime, compileTime, optimizeTime, runTime));
            } else {
                info("Performance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.".formatted(parseTime, compileTime, optimizeTime));
            }

            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (Exception e) {
            if (profile.isPrintStackTrace()) {
                e.printStackTrace();
            }
            if (e instanceof MindcodeException ex) {
                messages.add(MindcodeMessage.error("Error while compiling source code: " + e.getMessage()));
            } else {
                messages.add(MindcodeMessage.error("Internal error: " + e.getMessage()));
            }
        }

        return new CompilerOutput<>(instructions, messages, runResults.textBuffer(), runResults.steps());
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(SourceFile sourceFile) {
        errorListener.setFileName(sourceFile.fileName());
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceFile.code()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        return AstNodeBuilder.generate(sourceFile, context);
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

        OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, profile, messages::add);
        optimizer.setDebugPrinter(debugPrinter);
        List<LogicInstruction> result = optimizer.optimize(generatorOutput);
        debugPrinter.print(this::debug);
        return result;
    }

    private record RunResults(String textBuffer, int steps) { }

    private RunResults run(List<LogicInstruction> program) {
        // All flags are already set as we want them to be
        Processor processor = new Processor();
        processor.setFlag(ProcessorFlag.ERR_UNINITIALIZED_VAR, false);
        for (int i = 1; i < 10; i++) {
            processor.addBlock(MindustryMemory.createMemoryCell("cell" + i));
            processor.addBlock(MindustryMemory.createMemoryBank("bank" + i));
        }

        try {
            processor.run(program, profile.getStepLimit());
            return new RunResults(processor.getTextBuffer(), processor.getSteps());
        } catch (ExecutionException e) {
            String textBuffer = processor.getTextBuffer();
            return new RunResults(textBuffer.isEmpty() ? e.getMessage() : textBuffer + "\n" + e.getMessage(), processor.getSteps());
        }
    }

    private void info(String message) {
        messages.add(MindcodeMessage.info(message));
    }

    private void debug(String message) {
        messages.add(MindcodeMessage.debug(message));
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<CompilerMessage> errors;
        private String fileNameText;

        public ErrorListener(List<CompilerMessage> errors) {
            this.errors = errors;
        }

        public void setFileName(String fileName) {
            this.fileNameText = fileName.isEmpty() ? "" : " in " + fileName;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                errors.add(MindcodeMessage.error("Syntax error%s on line %d:%d: %s", fileNameText, line, charPositionInLine, msg));
            } else {
                errors.add(MindcodeMessage.error("Syntax error: %s%s on line %d:%d: %s", offendingSymbol, fileNameText, line, charPositionInLine, msg));
            }
        }
    }
}
