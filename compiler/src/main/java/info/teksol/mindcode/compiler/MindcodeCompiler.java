package info.teksol.mindcode.compiler;

import info.teksol.emulator.blocks.Memory;
import info.teksol.emulator.blocks.MessageBlock;
import info.teksol.emulator.blocks.MindustryBlock;
import info.teksol.emulator.blocks.graphics.LogicDisplay;
import info.teksol.emulator.processor.Assertion;
import info.teksol.emulator.processor.ExecutionException;
import info.teksol.emulator.processor.Processor;
import info.teksol.emulator.processor.TextBuffer;
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
import info.teksol.mindcode.v3.InputFile;
import info.teksol.mindcode.v3.InputFiles;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.intellij.lang.annotations.PrintFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MindcodeCompiler implements Compiler<String> {
    // Global cache
    private static final Map<String, InputFile> LIBRARY_SOURCES = new ConcurrentHashMap<>();
    private static final Map<InputFile, ParsedLibrary> LIBRARY_PARSES = new ConcurrentHashMap<>();

    private final CompilerProfile profile;
    private final InputFiles inputFiles;
    private InstructionProcessor instructionProcessor;

    private final List<MindcodeMessage> messages = new ArrayList<>();
    private final TranslatingMessageConsumer messageConsumer;

    public MindcodeCompiler(CompilerProfile profile, InputFiles inputFiles) {
        this.profile = profile;
        this.inputFiles = inputFiles;
        this.messageConsumer = new TranslatingMessageConsumer(messages::add, profile.getPositionTranslator());
    }

    private void error(AstElement element, @PrintFormat String format, Object... args) {
        messageConsumer.accept(CompilerMessage.error(element.inputPosition(), format, args));
    }

    private void error(@PrintFormat String format, Object... args) {
        messageConsumer.accept(ToolMessage.error(format, args));
    }

    private void info(@PrintFormat String format, Object... args) {
        messageConsumer.accept(ToolMessage.info(format, args));
    }

    private void timing(@PrintFormat String format, Object... args) {
        messageConsumer.accept(TimingMessage.info(format, args));
    }

    private void debug(String message) {
        messageConsumer.accept(ToolMessage.debug(message));
    }

    @Override
    public CompilerOutput<String> compile() {
        return compile(inputFiles.getInputFiles());
    }

    @Override
    public CompilerOutput<String> compile(List<InputFile> filesToCompile) {
        try {
            return compileInternal(filesToCompile);
        } catch (RuntimeException e) {
            if (profile.isPrintStackTrace()) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }

            error("Internal error: %s", e.getMessage());
            return new CompilerOutput<>("", messages, List.of(), null, 0);
        }
    }

    private InputFile loadFile(Requirement requirement, Path path) {
        try {
            String code = Files.readString(path, StandardCharsets.UTF_8);
            return inputFiles.registerFile(path, code);
        } catch (IOException e) {
            error(requirement, "Error reading file %s.", path);
            return null;
        }
    }

    private InputFile loadLibrary(Requirement requirement) {
        return LIBRARY_SOURCES.computeIfAbsent(requirement.getFile(), s -> loadLibraryFromResource(requirement));
    }

    private InputFile loadLibraryFromResource(Requirement requirement) {
        String libraryName = requirement.getFile();
        try {
            InputFile library = loadSystemLibrary(libraryName);
            if (library == null) {
                error(requirement, "Unknown system library '%s'.", libraryName);
            }
            return library;
        } catch (IOException e) {
            error(requirement, "Error reading system library file '%s'.", libraryName);
            throw new MindcodeInternalError(e, "Error reading system library file '%s'.", libraryName);
        }
    }

    InputFile loadSystemLibrary(String libraryName) throws IOException {
        try (InputStream resource = MindcodeCompiler.class.getResourceAsStream("/library/" + libraryName + ".mnd")) {
            if (resource == null) {
                return null;
            }
            try (final InputStreamReader reader = new InputStreamReader(resource)) {
                final StringWriter out = new StringWriter();
                reader.transferTo(out);
                return inputFiles.registerLibraryFile(Path.of(libraryName), out.toString());
            }
        }
    }

    private InputFile processRequirement(Path relativePath, Requirement requirement) {
        if (requirement.isLibrary()) {
            return loadLibrary(requirement);
        } else if (profile.isWebApplication()) {
            error(requirement, "Loading code from external file not supported in web application.");
            return null;
        } else {
            return loadFile(requirement, relativePath.resolve(requirement.getFile()));
        }
    }

    /**
     * Parses all input files, loading and parsing additional files referenced by the 'require' keyword.
     * Processes directives found in source files and updates the compiler profile.
     */
    private Seq parseFiles(List<InputFile> filesToCompile) {
        Deque<InputFile> queue = new ArrayDeque<>(filesToCompile);
        Set<InputFile> processedFiles = new HashSet<>();
        Seq program = null;

        try {
            while (!queue.isEmpty()) {
                InputFile inputFile = queue.pop();
                if (processedFiles.add(inputFile)) {
                    List<Requirement> requirements = new ArrayList<>();
                    Seq other = inputFile.isLibrary() ? parseLibrary(inputFile, requirements) : parse(inputFile, requirements);

                    // Additional source files are put in front of the others
                    program = Seq.append(other, program);

                    requirements.stream()
                            .map(r -> processRequirement(inputFiles.getBasePath(), r))
                            .filter(Objects::nonNull)
                            .forEach(queue::addLast);
                }
            }

            DirectiveProcessor.processDirectives(program, profile, messageConsumer);

            printParseTree(program);
        } catch (ParserAbort ignored) {
            // Do nothing
        }
        return program;
    }

    /**
     * Parses the source code using ANTLR generated parser.
     */
    private Seq parse(InputFile inputFile, List<Requirement> requirements) {
        MindcodeErrorListener errorListener = new MindcodeErrorListener(messageConsumer, inputFile);
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(inputFile.getCode()));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        final MindcodeParser.ProgramContext context = parser.program();
        if (!inputFile.isLibrary()) {
            info("%s: number of reported ambiguities: %d", inputFile.getDistinctTitle(), errorListener.getAmbiguities());
        }
        return AstNodeBuilder.generate(inputFile, messageConsumer, context, requirements);
    }

    private Seq parseLibrary(InputFile inputFile, List<Requirement> requirements) {
        if (LIBRARY_PARSES.containsKey(inputFile)) {
            ParsedLibrary parsedLibrary = LIBRARY_PARSES.get(inputFile);
            requirements.addAll(parsedLibrary.requirements);
            return parsedLibrary.program;
        }

        long before = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();
        Seq parsed = parse(inputFile, requirements);
        long after = messages.stream().filter(MindcodeMessage::isErrorOrWarning).count();

        if (before == after) {
            // Do not pollute cache with wrong parses
            LIBRARY_PARSES.put(inputFile, new ParsedLibrary(parsed, List.copyOf(requirements)));
        }

        return parsed;
    }

    private CompilerOutput<String> compileInternal(List<InputFile> filesToCompile) {
        // PARSE
        long parseStart = System.nanoTime();
        Seq program = parseFiles(filesToCompile);
        if (messages.stream().anyMatch(MindcodeMessage::isError)) {
            return new CompilerOutput<>("", messages);
        }
        long parseTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart);

        // GENERATE CODE
        long compileStart = System.nanoTime();
        instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(messageConsumer, profile);
        GeneratorOutput generated = generateCode(program);
        if (messages.stream().anyMatch(MindcodeMessage::isError)) {
            return new CompilerOutput<>("", messages);
        }
        long compileTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - compileStart);

        // OPTIMIZE
        long optimizeStart = System.nanoTime();
        List<LogicInstruction> optimized = optimize(generated);
        if (messages.stream().anyMatch(MindcodeMessage::isError)) {
            return new CompilerOutput<>("", messages);
        }
        long optimizeTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - optimizeStart);

        // Sort variables
        LogicInstructionLabelResolver resolver = new LogicInstructionLabelResolver(instructionProcessor, profile);
        List<LogicInstruction> instructions = resolver.sortVariables(optimized);

        // Print unresolved code
        if (profile.getFinalCodeOutput() != null) {
            debug("\nFinal code before resolving virtual instructions:\n");
            debug(LogicInstructionPrinter.toString(profile.getFinalCodeOutput(), instructionProcessor, instructions));
        }

        // Label resolving
        List<LogicInstruction> result = resolver.resolveLabels(instructions);

        // RUN if requested
        // Timing output
        final RunResults runResults;
        if (profile.isRun()) {
            long runStart = System.nanoTime();
            runResults = run(result);
            long runTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStart);
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms, run in %,d ms.",
                    parseTime, compileTime, optimizeTime, runTime);
        } else {
            runResults = new RunResults(List.of(), null,0);
            timing("\nPerformance: parsed in %,d ms, compiled in %,d ms, optimized in %,d ms.",
                    parseTime, compileTime, optimizeTime);
        }

        String output = LogicInstructionPrinter.toString(instructionProcessor, result);

        return new CompilerOutput<>(output, messages, runResults.assertions(), runResults.textBuffer(), runResults.steps());
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

    private List<LogicInstruction> optimize(GeneratorOutput generated) {
        if (!profile.optimizationsActive() || generated.instructions().size() <= 1) {
            return generated.instructions();
        }

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
        List<LogicInstruction> result = optimizer.optimize(generated);
        debugPrinter.print(this::debug);
        return result;
    }

    private record RunResults(List<Assertion> assertions, TextBuffer textBuffer, int steps) { }

    private RunResults run(List<LogicInstruction> instructions) {
        List<LogicInstruction> program = instructions.stream().map(instructionProcessor::normalizeInstruction).toList();

        // All flags are already set as we want them to be
        Processor processor = new Processor(messageConsumer, profile.getExecutionFlags(), profile.getTraceLimit());
        addBlocks(processor, "cell", i -> Memory.createMemoryCell());
        addBlocks(processor, "bank", i -> Memory.createMemoryBank());
        addBlocks(processor, "display", i -> LogicDisplay.createLogicDisplay(i < 5));
        addBlocks(processor, "message", i -> MessageBlock.createMessage());

        try {
            processor.run(program, profile.getStepLimit());
            return new RunResults(processor.getAssertions(), processor.getTextBuffer(), processor.getSteps());
        } catch (ExecutionException e) {
            return new RunResults(processor.getAssertions(),
                    processor.getTextBuffer().append("\n" + e.getMessage()), processor.getSteps());
        }
    }

    private void addBlocks(Processor processor, String name, Function<Integer, MindustryBlock> creator) {
        for (int i = 1; i < 10; i++) {
            processor.addBlock(name + i, creator.apply(i));
        }
    }

    private record ParsedLibrary(Seq program, List<Requirement> requirements) {
    }
}
