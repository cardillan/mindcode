package info.teksol.mindcode.webapp;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.*;
import info.teksol.mc.emulator.blocks.BlockPosition;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.messages.*;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.generation.variables.StandardNameCreator;
import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessorFactory;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import info.teksol.mindcode.samples.Sample;
import info.teksol.mindcode.samples.Samples;
import info.teksol.schemacode.SchemacodeCompiler;
import info.teksol.schemacode.SchematicsDecompiler;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.mindustry.SchematicsIO;
import info.teksol.schemacode.schematics.Schematic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private static final Map<String, Sample> mindcodeSamples = Samples.loadMindcodeSamples();
    private static final Map<String, Sample> schemacodeSamples = Samples.loadSchemacodeSamples();

    @Autowired
    private SourceRepository sourceRepository;

    @GetMapping("/source/{id}")
    public Source getSource(@PathVariable String id) {
        try {
            return sourceRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format");
        }
    }

    @PostMapping("/compile")
    public CompileResponse compile(@RequestBody CompileRequest request) {
        Target target = new Target(request.target);
        boolean run = request.run();
        ApiSource apiSource = getApiSource(request.sourceId, request.source, GetSourceMode.compileMindcode);

        ListMessageLogger messageLogger = new ListMessageLogger();
        MindcodeCompiler compiler = new MindcodeCompiler(
                messageLogger,
                CompilerProfile.fullOptimizations(false, true)
                        .setTarget(target)
                        .setRun(run),
                InputFiles.fromSource(apiSource.content));

        final long start = System.nanoTime();
        compiler.safeCompile();
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        String compiled = getCompilationMessage(apiSource.content, compiler);
        boolean isText = compiled != null;
        if(compiled == null) {
            compiled = compiler.getOutput();
        }

        return new CompileResponse(
                apiSource.id,
                compiled,
                errors(messageLogger),
                warnings(messageLogger),
                infos(messageLogger),
                processRunOutput(compiler),
                isText
        );
    }

    @PostMapping("/decompile")
    public DecompileResponse decompileMlog(@RequestBody DecompileRequest request) {
        Target target = new Target(request.target);
        boolean run = request.run();
        ApiSource apiSource = getApiSource(request.sourceId, request.source, GetSourceMode.other);
        String mlog = apiSource.content;
        final String decompiled = MlogDecompiler.decompile(mlog);

        ListMessageLogger messageLogger = new ListMessageLogger();
        List<RunResult> runResults = List.of();

        if(run) {
            CompilerProfile profile = CompilerProfile.fullOptimizations(false, true)
                    .setTarget(target)
                    .setRun(true);
            InstructionProcessor instructionProcessor = InstructionProcessorFactory.getInstructionProcessor(
                    messageLogger,
                    new StandardNameCreator(),
                    profile);
            MindustryMetadata metadata = instructionProcessor.getMetadata();

            LogicBlock logicBlock = LogicBlock.createProcessor(metadata, profile.getEmulatorTarget().type(),
                    BlockPosition.ZERO_POSITION, mlog);
            logicBlock.createDefaultBlocks(metadata);

            EmulatorSchematic schematic = new EmulatorSchematic(List.of(logicBlock));
            Emulator emulator = new BasicEmulator(messageLogger, profile, schematic);

            emulator.run(profile.getStepLimit());
            runResults = processEmulatorResults(emulator);
        }

        return new DecompileResponse(
                apiSource.id,
                decompiled,
                errors(messageLogger),
                warnings(messageLogger),
                infos(messageLogger),
                runResults
        );
    }

    @PostMapping("/schemacode/compile")
    public SchemacodeCompileResponse compileSchemacode(@RequestBody SchemacodeCompileRequest request) {
        Target target = new Target(request.target());
        ApiSource apiSource = getApiSource(request.sourceId, request.source, GetSourceMode.other);

        ListMessageLogger messageLogger = new ListMessageLogger();
        final CompilerOutput<String> result = SchemacodeCompiler.compileAndEncode(
                messageLogger,
                InputFiles.fromSource(apiSource.content),
                CompilerProfile.fullOptimizations(true, true)
                        .setTarget(target)
                        .setRun(request.run));

        String compiledCode = result.getStringOutput();
        List<RunResult> runResults = result.emulator() instanceof Emulator emulator ? processEmulatorResults(emulator) : List.of();

        return new SchemacodeCompileResponse(
                apiSource.id,
                compiledCode,
                errors(messageLogger),
                warnings(messageLogger),
                infos(messageLogger),
                runResults
        );
    }

    @PostMapping("/schemacode/decompile")
    public DecompileResponse decompileSchematic(@RequestBody DecompileRequest request) {
        ApiSource apiSource = getApiSource(request.sourceId, request.source, GetSourceMode.other);
        ListMessageLogger messageLogger = new ListMessageLogger();
        final CompilerOutput<String> compilerOutput = SchematicsDecompiler.decompile(messageLogger, apiSource.content);
        List<RunResult> runResults = List.of();

        run: if (request.run) {
            Target target = new Target(request.target);
            CompilerProfile profile = CompilerProfile.fullOptimizations(false, true)
                    .setTarget(target)
                    .setRun(true);
            Schematic schematic = parseSchematic(apiSource.content, messageLogger);
            if(schematic == null) break run;

            EmulatorSchematic emulatorSchematic = schematic.toEmulatorSchematic(SchematicsMetadata.getMetadata());
            Emulator emulator = new BasicEmulator(messageLogger, profile, emulatorSchematic);
            emulator.run(profile.getStepLimit());

            runResults = processEmulatorResults(emulator);
        }

        return new DecompileResponse(
                apiSource.id,
                compilerOutput.output(),
                errors(messageLogger),
                warnings(messageLogger),
                infos(messageLogger),
                runResults
        );
    }

    private ApiSource getApiSource(String id, String source, GetSourceMode mode) {
        ApiSource apiSource;
        if (mode == GetSourceMode.compileMindcode && mindcodeSamples.containsKey(id)) {
            apiSource = new ApiSource(id, source);
        } else if (mode == GetSourceMode.compileSchemacode && schemacodeSamples.containsKey(id)) {
            apiSource = new ApiSource(id, source);
        } else if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            final Optional<Source> dto = sourceRepository.findById(UUID.fromString(id));
            final Source newSource = dto
                    .map(sdto -> sdto.withSource(source))
                    .orElseGet(() -> new Source(source, Instant.now()));

            final Source sourceDto = sourceRepository.save(newSource);
            apiSource = new ApiSource(sourceDto.getId().toString(), source);
        } else {
            Source sourceDto = sourceRepository.save(new Source(source, Instant.now()));
            apiSource = new ApiSource(sourceDto.getId().toString(), source);
        }

        return apiSource;
    }

    private String getCompilationMessage(String sourceCode, MindcodeCompiler compiler) {
        if (compiler.hasInternalError()) {
            return """
                    Oh no! Mindcode crashed.
                    
                    Please report the error, either by creating an issue on GitHub,
                    or in one of the Discord servers - I monitor Mindustry, Mindustry Logic,
                    and, of course, Mindcode.
                    
                    It is quite possible that a workaround will be found for this problem,
                    in which case you'll be able to continue developing your program
                    even before a fix is available.
                    """;
        } else if (sourceCode.isBlank() || compiler.hasCompilerErrors()) {
            return "";
        } else if (isEmpty(compiler.getUnoptimized())) {
            if (compiler.getCallGraph().getFunctions().stream().filter(f -> !f.getDeclaration().sourcePosition().isLibrary()).count() > 1) {
                return """
                        Oops! Your program didn't generate any code.
                        
                        However, it looks like you defined some functions.
                        Maybe you just forgot to call them?
                        """;
            } else {
                return """
                        Oops! Your program didn't generate any code.
                        
                        It appears you haven't entered any statements
                        or expressions that actually do something.
                        Maybe you only have comments in your program,
                        or declarations that don't produce actual code,
                        such as constant declarations.
                        """;
            }
        } else if (isEmpty(compiler.getInstructions())) {
            return """
                    Whoa! Your program generated some code,
                    but it was all removed by the optimizer.
                    
                    Mindcode removes all unused parts of the program,
                    and those statements that to not have an effect
                    on the Mindustry world.
                    
                    If your program computes some values, just add
                    a print() function to output the results of your
                    computations. For example:
                    
                    a = 3;
                    b = 4;
                    c = a * a + b * b;
                    println(c);    // <-- prints the result of the computation
                    """;
        }

        return null;
    }

    private boolean isEmpty(List<LogicInstruction> program) {
        return program.isEmpty() || program.size() <= 2 && program.getFirst().getOpcode() == Opcode.END;
    }

    public List<CompileResponseMessage> errors(ListMessageLogger logger) {
        return formatMessages(logger.getMessages(), MindcodeMessage::isError);
    }

    public List<CompileResponseMessage> warnings(ListMessageLogger logger) {
        return formatMessages(logger.getMessages(), MindcodeMessage::isWarning);
    }

    public List<CompileResponseMessage> infos(ListMessageLogger logger) {
        return formatMessages(logger.getMessages(), MindcodeMessage::isInfo);
    }

    public List<CompileResponseMessage> formatMessages(List<MindcodeMessage> messages, Predicate<MindcodeMessage> filter) {
        return messages.stream().filter(filter.and(m -> !(m instanceof EmulatorMessage)))
                .map(CompileResponseMessage::transform)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private RunResult processRunOutput(MindcodeCompiler compiler) {
        if (compiler.hasCompilerErrors()) {
            return null;
        } else if(!compiler.compilerProfile().isRun()) {
            return null;
        } else {
            String output = compiler.getTextBufferOutput();
            String text = output.isEmpty() ? "The program produced no output." : output;

            Optional<EmulatorMessage> emulatorError = compiler.getMessages().stream()
                    .filter(m -> m.level() == MessageLevel.ERROR && m instanceof EmulatorMessage)
                    .map(EmulatorMessage.class::cast)
                    .findFirst();

            if (emulatorError.isPresent()) {
                text = text + "\n" + emulatorError.get().message();
            }

            String processorId = compiler.getEmulator().getExecutorResults(0).getProcessorId();
            int steps = compiler.getSteps();

            return new RunResult(processorId, text, steps);
        }
    }

    private List<RunResult> processEmulatorResults(Emulator emulator) {
        ArrayList<RunResult> results = new ArrayList<>();

        for(ExecutorResults executorResult : emulator.getExecutorResults()) {
            String id =  executorResult.getProcessorId();
            int steps = executorResult.getSteps();
            String output = executorResult.getFormattedOutput();
            if(output.isEmpty()) {
                output = "The program didn't generate any output.";
            }
            results.add(new RunResult(id, output, steps));
        }

        return results;
    }

    private Schematic parseSchematic(String encodedSchematic, MessageConsumer messageConsumer) {
        if (encodedSchematic.isBlank()) {
            return null;
        }

        final byte[] binary;
        try {
            binary = Base64.getDecoder().decode(encodedSchematic.trim());
        } catch (IllegalArgumentException e) {
            messageConsumer.addMessage(ToolMessage.error("Error decoding schematics string: " + e.getMessage()));
            return null;
        }

        try (InputStream is = new ByteArrayInputStream(binary)) {
            return SchematicsIO.read("", is);
        } catch (Exception e) {
            messageConsumer.addMessage(ToolMessage.error("Error decoding schematics: " + e.getMessage()));
            return null;
        }
    }


    private enum GetSourceMode {
        compileMindcode,
        compileSchemacode,
        other,
    }

    /**
     * <p>
     *     Can represent either a {@link Source} object loaded from the server (saved on compile requests)
     *     or a sample received for compilation (not saved on compile requests).
     * </p>
     * <p>
     *     This class had to be created because {@link Source} only supports {@link UUID} ids, which
     *     are not compatible with the sample ids.
     * </p>
     */
    private record ApiSource(String id, String content) {}

    public record CompileRequest(String sourceId, String source, String target, boolean run) {}
    public record CompileResponse(
            String sourceId,
            String compiled,
            List<CompileResponseMessage> errors,
            List<CompileResponseMessage> warnings,
            List<CompileResponseMessage> infos,
            RunResult runResult,
            boolean isPlainText) {}
    public record SchemacodeCompileRequest(String sourceId, String source, String target, boolean run) {}
    public record SchemacodeCompileResponse(
            String sourceId,
            String compiled,
            List<CompileResponseMessage> errors,
            List<CompileResponseMessage> warnings,
            List<CompileResponseMessage> infos,
            List<RunResult> runResults) {}


    public record RunResult(String processorId, String output, int steps) {}

    public record DecompileRequest(String sourceId, String source, String target, boolean run) {}
    public record DecompileResponse(
        String sourceId,
        String source,
        List<CompileResponseMessage> errors,
        List<CompileResponseMessage> warnings,
        List<CompileResponseMessage> infos,
        List<RunResult> runResults) {}

    public record SourceRange(String path, int startLine, int startColumn, int endLine, int endColumn) {}
    public record CompileResponseMessage(String prefix, String message, SourceRange range) {
        static CompileResponseMessage transform(MindcodeMessage msg) {
            if (msg.sourcePosition().isEmpty()) {
                return new CompileResponseMessage("", msg.message(), null);
            } else if (msg.sourcePosition().isLibrary()) {
                var sourcePosition =  msg.sourcePosition();
                return new CompileResponseMessage("", msg.level().getTitle() + msg.message(), new SourceRange(
                        sourcePosition.getDistinctPath(),
                        sourcePosition.start().line(),
                        sourcePosition.start().column(),
                        sourcePosition.end().line(),
                        sourcePosition.end().column()
                ));
            } else {
                var sourcePosition = msg.sourcePosition();
                return new CompileResponseMessage(msg.level().getTitle(), msg.message(), new SourceRange(
                        sourcePosition.getDistinctPath(),
                        sourcePosition.start().line(),
                        sourcePosition.start().column(),
                        sourcePosition.end().line(),
                        sourcePosition.end().column()
                ));
            }
        }
    }
}
