package info.teksol.mindcode.compiler;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.graphics.LogicDisplay;
import info.teksol.emulator.processor.ExecutionException;
import info.teksol.emulator.processor.Processor;
import info.teksol.emulator.processor.ProcessorFlag;
import info.teksol.mindcode.*;
import info.teksol.mindcode.ast.AstIndentedPrinter;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Requirement;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.*;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.mindcode.v3.InputFiles;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler<String> {
    // Global cache
    private static final Map<String, InputFiles.InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();
    private static final Map<String, Seq> LIBRARY_PARSES = new ConcurrentHashMap<>();

    private final CompilerProfile profile;
    private InstructionProcessor instructionProcessor;

    private final List<MindcodeMessage> messages = new ArrayList<>();
    private final TranslatingMessageConsumer messageConsumer;

    public MindcodeCompiler(CompilerProfile profile) {
        this.profile = profile;
        this.messageConsumer = new TranslatingMessageConsumer(messages::add, profile.getPositionTranslator());
    }

    @Override
    public CompilerOutput<String> compile(InputFiles inputFiles) {
        return compile(inputFiles, inputFiles.getInputFiles());
    }

    private static String readInput(Path inputFile) {
        try {
            return Files.readString(inputFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MindcodeInternalError(e, "Error reading file %s.", inputFile);
        }
    }

    private InputFiles.InputFile processRequirement(Path relativePath, Requirement requirement) {
        if (requirement.isSystem()) {
            return loadLibrary(requirement.getFile());
        } else if (profile.isWebApplication()) {
            messageConsumer.accept(CompilerMessage.error(requirement.inputPosition(), "Loading code from external file not supported in web application."));
            return InputFile.createSourceFile("");
        } else {
            Path file = relativePath.resolve(requirement.getFile());
            return new InputFile(requirement.getFile(), file.toAbsolutePath().normalize().toString(), readInput(file));
        }
    }

    @Override
    public CompilerOutput<String> compile(InputFiles inputFiles, List<InputFiles.InputFile> filesToCompile) {
        String instructions = "";
        RunResults runResults = new RunResults(null,0);
        Deque<InputFiles.InputFile> queue = new ArrayDeque<>(filesToCompile);
        Set<InputFiles.InputFile> processedFiles = new HashSet<>();

        try {
            long parseStart = System.nanoTime();
            Seq program = null;
            while (!queue.isEmpty()) {
                InputFiles.InputFile inputFile = queue.pop();
                Path relativePath = Path.of(inputFile.absolutePath()).toAbsolutePath().normalize().getParent();
                if (processedFiles.add(inputFile)) {
                    // Additional source files are put in front of the others
                    List<Requirement> requirements = new ArrayList<>();
                    final Seq other = inputFile.fileName().startsWith("*")
                            ? parseLibrary(inputFile.fileName().substring(1), requirements)
                            : parse(inputFile, requirements);
                    program = Seq.append(other, program);
                    if (messages.stream().anyMatch(MindcodeMessage::isError)) {
                        return new CompilerOutput<>("", messages, null, 0);
                    }
                    requirements.stream().map(r -> processRequirement(relativePath, r)).forEach(queue::addLast);
                }
            }

            DirectiveProcessor.processDirectives(program, profile, messageConsumer);

            printParseTree(program);
            long parseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

            long compileStart = System.nanoTime();
            instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);
            GeneratorOutput generated = generateCode(program);
            if (messages.stream().anyMatch(MindcodeMessage::isError)) {
                return new CompilerOutput<>("", messages, null, 0);
            }
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
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

            messageConsumer.accept(ToolMessage.error("Internal error: %s", e.getMessage()));
        }

        return new CompilerOutput<>(instructions, messages, runResults.textBuffer(), runResults.steps());
    }

    private static InputFiles.InputFile loadLibrary(InputFiles inputFiles, String filename) {
        return LIBRARY_SOURCES.computeIfAbsent(filename, f -> loadLibraryFromResource(inputFiles, f));
    }

    static InputFiles.InputFile loadLibraryFromResource(InputFiles inputFiles, String filename) {
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(MindcodeCompiler.class.getResourceAsStream("/library/" + filename + ".mnd")))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return inputFiles.registerVirtualFile(Path.of(filename), out.toString());
        } catch (IOException e) {
            throw new MindcodeInternalError(e, "Error loading library: " + filename);
        }
    }

    private Seq parseLibrary(InputFiles inputFiles, String filename, List<Requirement> requirements) {
        if (LIBRARY_PARSES.containsKey(filename)) {
            return LIBRARY_PARSES.get(filename);
        }

        InputFiles.InputFile inputFile = loadLibrary(inputFiles, filename);

        long before = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();
        Seq parsed = parse(loadLibrary(filename), requirements);
        long after = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();

        if (before == after) {
            LIBRARY_PARSES.put(filename, parsed);
        }

        return parsed;
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(Consumer<MindcodeMessage> messageConsumer, InputFiles.InputFile inputFile, List<Requirement> requirements) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        messageConsumer.accept(ToolMessage.info("Number of reported ambiguities: %d", errorListener.getAmbiguities()));
        return AstNodeBuilder.generate(inputFile, messageConsumer, context, requirements);
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
                messageConsumer);
        return generator.generate(program);
    }

    private List<LogicInstruction> optimize(GeneratorOutput generatorOutput) {
        messageConsumer.accept(
                OptimizerMessage.debug("%s", profile.getOptimizationLevels().entrySet().stream()
                        .sorted(Comparator.comparing(e -> e.getKey().getOptionName()))
                        .map(e -> e.getKey().getOptionName() + " = " + e.getValue().name().toLowerCase())
                        .collect(Collectors.joining(",\n    ", "Active optimizations:\n    ", "\n"))
                )
        );

        final DebugPrinter debugPrinter = profile.getDebugLevel() > 0 && profile.optimizationsActive()
                ? new DiffDebugPrinter(profile.getDebugLevel()) : new NullDebugPrinter();

        OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, profile, messageConsumer);
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
            return new RunResults(textBuffer == null || textBuffer.isEmpty() ? e.getMessage()
                    : textBuffer + "\n" + e.getMessage(), processor.getSteps());
        }
    }

    private void info(String message) {
        messageConsumer.accept(ToolMessage.info(message));
    }

    private void debug(String message) {
        messageConsumer.accept(ToolMessage.debug(message));
    }

}
