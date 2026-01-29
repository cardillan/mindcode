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

    @PostMapping("/compile")
    public CompileResponse compile(@RequestBody CompileRequest request) {
        Target target = new Target(request.target);
        boolean run = request.run();
        Source sourceDto = getSourceDto(request.sourceId, request.source);

        ListMessageLogger messageLogger = new ListMessageLogger();
        MindcodeCompiler compiler = new MindcodeCompiler(
                messageLogger,
                CompilerProfile.fullOptimizations(true).setTarget(target).setRun(run),
                InputFiles.fromSource(sourceDto.getSource()));

        final long start = System.nanoTime();
        compiler.safeCompile();
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        String compiled = getCompilationMessage(sourceDto.getSource(), compiler);
        boolean isText = compiled != null;
        if(compiled == null) {
            compiled = compiler.getOutput();
        }

        return new CompileResponse(
                sourceDto.getId().toString(),
                compiled,
                errors(compiler),
                warnings(compiler),
                messages(compiler),
                processRunOutput(compiler),
                compiler.getSteps(),
                isText
        );
    }

    @PostMapping("/decompile")
    public DecompileResponse decompileMlog(@RequestBody DecompileRequest request) {
        Source sourceDto =  getSourceDto(request.sourceId, request.source);
        final String decompiled = MlogDecompiler.decompile(sourceDto.getSource());
        return new DecompileResponse(sourceDto.getId().toString(),decompiled, List.of(), List.of(), List.of());
    }

    @PostMapping("/schemacode/compile")
    public SchemacodeCompileResponse compileSchemacode(@RequestBody SchemacodeCompileRequest request) {
        Target target = new Target(request.target());
        Source sourceDto = getSourceDto(request.sourceId, request.source);

        final CompilerOutput<String> result = SchemacodeCompiler.compileAndEncode(
                InputFiles.fromSource(sourceDto.getSource()),
                CompilerProfile.fullOptimizations(true).setTarget(target));

        String compiledCode = result.getStringOutput();

        return new SchemacodeCompileResponse(
                sourceDto.getId().toString(),
                compiledCode,
                result.errors(CompileResponseMessage::transform),
                result.warnings(CompileResponseMessage::transform),
                result.infos(CompileResponseMessage::transform)
        );
    }

    @PostMapping("/schemacode/decompile")
    public DecompileResponse decompileSchematic(@RequestBody DecompileRequest request) {
        Source sourceDto =  getSourceDto(request.sourceId, request.source);
        final CompilerOutput<String> compilerOutput = SchematicsDecompiler.decompile(sourceDto.getSource());

        return new DecompileResponse(
                sourceDto.getId().toString(),
                compilerOutput.output(),
                compilerOutput.errors(CompileResponseMessage::transform),
                compilerOutput.warnings(CompileResponseMessage::transform),
                compilerOutput.infos(CompileResponseMessage::transform)
        );
    }

    private Source getSourceDto(String id, String source) {
        Source sourceDto;
        if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            final Optional<Source> dto = sourceRepository.findById(UUID.fromString(id));
            final Source newSource = dto
                    .map(sdto -> sdto.withSource(source))
                    .orElseGet(() -> new Source(source, Instant.now()));
            sourceDto = sourceRepository.save(newSource);
        } else {
            sourceDto = sourceRepository.save(new Source(source, Instant.now()));
        }

        return sourceDto;
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

    public record CompileRequest(String sourceId, String source, String target, boolean run) {}
    public record CompileResponse(
            String sourceId,
            String compiled,
            List<CompileResponseMessage> errors,
            List<CompileResponseMessage> warnings,
            List<CompileResponseMessage> infos,
            String runOutput,
            int runSteps,
            boolean isPlainText) {}
    public record SchemacodeCompileRequest(String sourceId, String source, String target) {}
    public record SchemacodeCompileResponse(String sourceId, String compiled, List<CompileResponseMessage> errors, List<CompileResponseMessage> warnings, List<CompileResponseMessage> infos) {}


    public record DecompileRequest(String sourceId, String source) {}
    public record DecompileResponse(String sourceId, String source, List<CompileResponseMessage> errors, List<CompileResponseMessage> warnings, List<CompileResponseMessage> infos) {}

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
