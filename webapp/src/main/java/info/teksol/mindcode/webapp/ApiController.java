package info.teksol.mindcode.webapp;

import info.teksol.mc.common.CompilerOutput;
import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.EmulatorMessage;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import info.teksol.mindcode.samples.Sample;
import info.teksol.mindcode.samples.Samples;
import info.teksol.schemacode.SchemacodeCompiler;
import info.teksol.schemacode.SchematicsDecompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private static final Map<String, Sample> samples = Samples.loadMindcodeSamples();
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

    @PostMapping("/source")
    public Source saveSource(@RequestBody String source) {
        return sourceRepository.save(new Source(source, Instant.now()));
    }

    @PutMapping("/source/{id}")
    public Source updateSource(@PathVariable String id, @RequestBody String source) {
        try {
            UUID uuid = UUID.fromString(id);
            return sourceRepository.findById(uuid)
                    .map(s -> sourceRepository.save(s.withSource(source)))
                    .orElseGet(() -> sourceRepository.save(new Source(uuid, source, Instant.now())));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID format");
        }
    }

    @GetMapping("/samples")
    public List<SampleDTO> getSamples() {
        return samples.entrySet().stream()
                .map(e -> new SampleDTO(e.getKey(), e.getValue().name(), e.getValue().source()))
                .toList();
    }

    @PostMapping("/compile")
    public CompileResponse compile(@RequestBody CompileRequest request) {
        Target target = new Target(request.target());
        boolean run = request.run();
        String sourceCode = request.source();

        ListMessageLogger messageLogger = new ListMessageLogger();
        MindcodeCompiler compiler = new MindcodeCompiler(
                messageLogger,
                CompilerProfile.fullOptimizations(true).setTarget(target).setRun(run),
                InputFiles.fromSource(sourceCode));

        final long start = System.nanoTime();
        compiler.safeCompile();
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        String compiledCode = getCompilerCode(sourceCode, compiler);

        return new CompileResponse(
                compiledCode,
                errors(compiler),
                warnings(compiler),
                messages(compiler),
                processRunOutput(compiler),
                compiler.getSteps()
        );
    }

    @GetMapping("/schemacode/samples")
    public List<SampleDTO> getSchemacodeSamples() {
        return schemacodeSamples.entrySet().stream()
                .map(e -> new SampleDTO(e.getKey(), e.getValue().name(), e.getValue().source()))
                .toList();
    }

    @PostMapping("/schemacode/compile")
    public SchemacodeCompileResponse compileSchemacode(@RequestBody SchemacodeCompileRequest request) {
        Target target = new Target(request.target());
        String sourceCode = request.source().trim();

        final CompilerOutput<byte[]> compilerOutput = SchemacodeCompiler.compile(InputFiles.fromSource(sourceCode), CompilerProfile.fullOptimizations(true).setTarget(target));

        String compiledCode = compilerOutput.hasCompilerErrors() ? "" : Base64.getEncoder().encodeToString(compilerOutput.output());

        return new SchemacodeCompileResponse(
                compiledCode,
                compilerOutput.errors(CompileResponseMessage::transform),
                compilerOutput.warnings(CompileResponseMessage::transform),
                compilerOutput.infos(CompileResponseMessage::transform)
        );
    }

    @PostMapping("/decompile/schematic")
    public DecompileResponse decompileSchematic(@RequestBody String source) {
        final CompilerOutput<String> compilerOutput = SchematicsDecompiler.decompile(source);

        return new DecompileResponse(
                compilerOutput.output(),
                compilerOutput.errors(CompileResponseMessage::transform),
                compilerOutput.warnings(CompileResponseMessage::transform),
                compilerOutput.infos(CompileResponseMessage::transform)
        );
    }

    @PostMapping("/decompile/mlog")
    public DecompileResponse decompileMlog(@RequestBody String source) {
        final String decompiled = MlogDecompiler.decompile(source);
        return new DecompileResponse(decompiled, List.of(), List.of(), List.of());
    }

    private List<WebappMessage> transformMessages(List<MindcodeMessage> messages) {
        return messages.stream()
                .map(WebappMessage::transform)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String getCompilerCode(String sourceCode, MindcodeCompiler compiler) {
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
        } else {
            return compiler.getOutput();
        }
    }

    private boolean isEmpty(List<LogicInstruction> program) {
        return program.isEmpty() || program.size() <= 2 && program.getFirst().getOpcode() == Opcode.END;
    }

    public List<CompileResponseMessage> errors(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isError);
    }

    public List<CompileResponseMessage> warnings(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isWarning);
    }

    public List<CompileResponseMessage> messages(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isInfo);
    }

    public List<CompileResponseMessage> formatMessages(MindcodeCompiler compiler, Predicate<MindcodeMessage> filter) {
        return compiler.getMessages().stream().filter(filter.and(m -> !(m instanceof EmulatorMessage)))
                .map(CompileResponseMessage::transform)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String processRunOutput(MindcodeCompiler compiler) {
        if (compiler.hasCompilerErrors()) {
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

            return text;
        }
    }

    public record SampleDTO(String name, String title, String source) {}
    public record CompileRequest(String source, String target, boolean run) {}
    public record CompileResponse(String compiledCode, List<CompileResponseMessage> errors, List<CompileResponseMessage> warnings, List<CompileResponseMessage> info, String runOutput, int runSteps) {}
    public record SchemacodeCompileRequest(String source, String target) {}
    public record SchemacodeCompileResponse(String compiledCode, List<CompileResponseMessage> errors, List<CompileResponseMessage> warnings, List<CompileResponseMessage> info) {}

    public record DecompileResponse(String source, List<CompileResponseMessage> errors, List<CompileResponseMessage> warnings, List<CompileResponseMessage> info) {}

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
