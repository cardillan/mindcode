package info.teksol.mindcode.compiler;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.graphics.LogicDisplay;
import info.teksol.emulator.processor.ExecutionException;
import info.teksol.emulator.processor.Processor;
import info.teksol.emulator.processor.ProcessorFlag;
import info.teksol.mindcode.*;
import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.*;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.grammar.MissingSemicolonException;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.antlr.v4.runtime.*;
import org.intellij.lang.annotations.PrintFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler<String> {
    // Global cache
    private static final Map<String, InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();
    private static final Map<String, Seq> LIBRARY_PARSES = new ConcurrentHashMap<>();

    private final CompilerProfile profile;
    private InstructionProcessor instructionProcessor;

    private final List<MindcodeMessage> messages = new ArrayList<>();
    private final ErrorListener errorListener = new ErrorListener(messages);

    public MindcodeCompiler(CompilerProfile profile) {
        this.profile = profile;
    }

    @Override
    public CompilerOutput<String> compile(List<InputFile> inputFiles) {
        String instructions = "";
        RunResults runResults = new RunResults(null,0);

        try {
            long parseStart = System.nanoTime();
            Seq program = null;
            for (InputFile inputFile : inputFiles) {
                // Additional source files are put in front of the others
                final Seq other = parse(inputFile);
                program = Seq.append(other, program);
                if (messages.stream().anyMatch(MindcodeMessage::isError)) {
                    return new CompilerOutput<>("", messages, null, 0);
                }
            }

            DirectiveProcessor.processDirectives(program, profile, messages::add);

            if (profile.getProcessorVersion().matches(ProcessorVersion.V8A, ProcessorVersion.MAX)) {
                Seq sys = parseLibrary("sys");
                program = Seq.append(sys, program);
            }

            printParseTree(program);
            long parseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

            long compileStart = System.nanoTime();
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
                info("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.".formatted(parseTime, compileTime, optimizeTime, runTime));
            } else {
                info("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.".formatted(parseTime, compileTime, optimizeTime));
            }

            instructions = LogicInstructionPrinter.toString(instructionProcessor, result);
        } catch (ParserAbort ignored) {
            // Do nothing
        } catch (Exception e) {
            if (profile.isPrintStackTrace()) {
                e.printStackTrace();
            }
            if (e instanceof MindcodeException ex) {
                if (!ex.isReported()) {
                    messages.add(MindcodeCompilerMessage.error("Error while compiling source code: %s", e.getMessage()));
                }
            } else {
                messages.add(MindcodeCompilerMessage.error("Internal error: %s", e.getMessage()));
            }
        }

        return new CompilerOutput<>(instructions, messages, runResults.textBuffer(), runResults.steps());
    }

    private static InputFile loadLibrary(String filename) {
        return LIBRARY_SOURCES.computeIfAbsent(filename, MindcodeCompiler::loadLibraryFromResource);
    }

    static InputFile loadLibraryFromResource(String filename) {
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(MindcodeCompiler.class.getResourceAsStream("/library/" + filename + ".mnd")))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return new InputFile("*" + filename, filename + ".mnd", out.toString());
        } catch (IOException e) {
            throw new MindcodeInternalError(e, "Error loading library: " + filename);
        }
    }

    private Seq parseLibrary(String filename) {
        if (LIBRARY_PARSES.containsKey(filename)) {
            return LIBRARY_PARSES.get(filename);
        }

        long before = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();
        Seq parsed = parse(loadLibrary(filename));
        long after = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();

        if (before == after) {
            LIBRARY_PARSES.put(filename, parsed);
        }

        return parsed;
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(InputFile inputFile) {
        errorListener.setInputFile(inputFile);
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.code()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        return AstNodeBuilder.generate(inputFile, context);
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
        try {
            return generator.generate(program);
        } catch (MindcodeException ex) {
            if (ex.getToken() != null) {
                errorListener.syntaxError(null, null,
                        ex.getToken().getLine(), ex.getToken().getCharPositionInLine(), ex.getMessage(), null);
                ex.setReported(true);
            }
            throw ex;
        }
    }

    private List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        messages.add(
                MindcodeOptimizerMessage.debug("%s", profile.getOptimizationLevels().entrySet().stream()
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
            processor.addBlock("cell" + i, Memory.createMemoryCell());
            processor.addBlock("bank" + i, Memory.createMemoryBank());
            processor.addBlock("display" + i, LogicDisplay.createLogicDisplay(i < 5));
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
        messages.add(ToolMessage.info(message));
    }

    private void debug(String message) {
        messages.add(ToolMessage.debug(message));
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<MindcodeMessage> errors;
        private InputFile inputFile;

        public ErrorListener(List<MindcodeMessage> errors) {
            this.errors = errors;
        }

        public void setInputFile(InputFile inputFile) {
            this.inputFile = inputFile;
        }

        private final Set<MindcodeCompilerMessage> reportedMessages = new HashSet<>();

        private void reportError(int line, int charPositionInLine, @PrintFormat String format, Object... args) {
            MindcodeCompilerMessage message = MindcodeCompilerMessage.error(
                    new InputPosition(inputFile, line, charPositionInLine + 1), format, args);
            if (reportedMessages.add(message)) {
                errors.add(message);
            }
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException exception) {
            if (exception instanceof MissingSemicolonException ex && offendingSymbol instanceof Token token) {
                String nextToken = ex.getNextToken().getText();
                // Reporting missing semicolon before "do" and "then" is probably a consequence of do/then being
                // driven by syntactic predicate. In any case it doesn't make any sense.
                if (!"do".equals(nextToken) && !"then".equals(nextToken)) {
                    int length = token.getStopIndex() - token.getStartIndex();
                    reportError(line, charPositionInLine + length + 1, "Parse error: %s%s",
                            msg, ex.getNextToken() != null ? " before '" + nextToken + "'": "");

                }
            } else if (exception instanceof NoViableAltException) {
                String offendingTokenText = getOffendingTokenText(exception);
                if (offendingTokenText == null) {
                    reportError(line, charPositionInLine, "Parse error: unrecoverable parse error");
                } else {
                    reportError(line, charPositionInLine, "Parse error: unexpected '%s'", offendingTokenText);
                }
            } else {
                String offendingTokenText = getOffendingTokenText(exception);
                if (offendingTokenText == null) {
                    reportError(line, charPositionInLine, "Parse error: %s", msg);
                } else {
                    reportError(line, charPositionInLine, "Parse error: '%s': %s", offendingTokenText, msg);
                }
            }
        }

        private String getOffendingTokenText(RecognitionException ex) {
            return ex != null && ex.getOffendingToken() != null ? ex.getOffendingToken().getText() : null;
        }
    }
}
