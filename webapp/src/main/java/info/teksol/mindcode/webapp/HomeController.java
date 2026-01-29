package info.teksol.mindcode.webapp;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.EmulatorMessage;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import info.teksol.mindcode.samples.Sample;
import info.teksol.mindcode.samples.Samples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/legacy")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Map<String, Sample> samples = Samples.loadMindcodeSamples();
    private static final List<Sample> quickSamples = samples.values().stream().filter(s -> !s.slow()).toList();

    private final Random random = new Random();
    @Autowired
    private SourceRepository sourceRepository;

    @PostMapping("/compile")
    public RedirectView postCompile(@RequestParam(required = false) String id,
            @RequestParam String source,
            @RequestParam(required = false) String compilerTarget) {
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

        String targetUrl = UriComponentsBuilder
                .fromPath("/legacy")
                .queryParam("compilerTarget", compilerTarget)
                .queryParam("s", sourceDto.getId().toString())
                .build()
                .toUriString();

        return new RedirectView(targetUrl);
    }

    @PostMapping("/compileandrun")
    public RedirectView postCompileAndRun(@RequestParam(required = false) String id,
            @RequestParam String source,
            @RequestParam(required = false) String compilerTarget) {
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

        String targetUrl = UriComponentsBuilder
                .fromPath("/legacy")
                .queryParam("compilerTarget", compilerTarget)
                .queryParam("s", sourceDto.getId().toString())
                .queryParam("run", "true")
                .build()
                .toUriString();

        return new RedirectView(targetUrl);
    }

    @GetMapping
    public ModelAndView getHomePage(
            @RequestParam(name = "s", defaultValue = "") String id,
            @RequestParam(name = "mindcode", defaultValue = "") String src,
            @RequestParam(name = "compilerTarget", defaultValue = "7s") String compilerTarget,
            @RequestParam(name = "run", defaultValue = "false") String compileAndRun
    ) {
        Target target = new Target(compilerTarget);
        boolean run = "true".equals(compileAndRun);
        final String sampleName;
        final String sourceCode;
        if (samples.containsKey(id)) {
            sampleName = id;
            sourceCode = samples.get(sampleName).source();
        } else if (id != null && id.equals("clean")) {
            sampleName = "";
            sourceCode = "";
        } else if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            sampleName = "";
            final Optional<Source> source = sourceRepository.findById(UUID.fromString(id));
            if (source.isPresent()) {
                sourceCode = source.get().getSource();
            } else {
                sourceCode = "// 404 Not Found";
            }
        } else if (!src.isEmpty()) {
            sourceCode = src;
            sampleName = "";
        } else {
            final int skipCount = random.nextInt(quickSamples.size());
            sampleName = quickSamples.get(skipCount).name();
            sourceCode = samples.get(sampleName).source();
        }

        if ("sum-of-primes".equals(sampleName)) {
            run = true;
        }

        ListMessageLogger messageLogger = new ListMessageLogger();
        MindcodeCompiler compiler = new MindcodeCompiler(
                messageLogger,
                CompilerProfile.fullOptimizations(false, true).setTarget(target).setRun(run),
                InputFiles.fromSource(sourceCode));

        final long start = System.nanoTime();
        compiler.safeCompile();
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        final String compiledCode = getCompilerCode(sourceCode, compiler);
        return new ModelAndView(
                "home",
                "model",
                new HomePageData(
                        id,
                        sampleName,
                        sourceCode,
                        (int) sourceCode.chars().filter(ch -> ch == '\n').count(),
                        compiledCode,
                        (int) compiledCode.chars().filter(ch -> ch == '\n').count(),
                        errors(compiler),
                        warnings(compiler),
                        messages(compiler),
                        target.webpageTargetName(),
                        processRunOutput(compiler),
                        compiler.getSteps())
        );
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

    public List<WebappMessage> errors(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isError);
    }

    public List<WebappMessage> warnings(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isWarning);
    }

    public List<WebappMessage> messages(MindcodeCompiler compiler) {
        return formatMessages(compiler, MindcodeMessage::isInfo);
    }

    public List<WebappMessage> formatMessages(MindcodeCompiler compiler, Predicate<MindcodeMessage> filter) {
        return compiler.getMessages().stream().filter(filter.and(m -> !(m instanceof EmulatorMessage)))
                .map(WebappMessage::transform)
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
}
