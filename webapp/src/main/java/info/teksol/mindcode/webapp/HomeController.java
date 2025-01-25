package info.teksol.mindcode.webapp;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
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

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Map<String, Sample> samples = Samples.loadMindcodeSamples();
    private static final List<Sample> quickSamples = samples.values().stream().filter(s -> !s.slow()).toList();

    private final Random random = new Random();
    @Autowired
    private SourceRepository sourceRepository;

    @PostMapping("/compile")
    public String postCompile(@RequestParam(required = false) String id,
                              @RequestParam String source,
                              @RequestParam(required = false) String optimizationLevel) {
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

        return "redirect:/?optimizationLevel=" + optimizationLevel + "&s=" + sourceDto.getId().toString();
    }

    @PostMapping("/compileandrun")
    public String postCompileAndRun(@RequestParam(required = false) String id,
                              @RequestParam String source,
                              @RequestParam(required = false) String optimizationLevel) {
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

        return "redirect:/?optimizationLevel=" + optimizationLevel + "&s=" + sourceDto.getId().toString() + "&run=true";
    }

    @GetMapping
    public ModelAndView getHomePage(
        @RequestParam(name = "s", defaultValue = "") String id,
        @RequestParam(name = "mindcode", defaultValue = "") String src,
        @RequestParam(name = "optimizationLevel", defaultValue = "ADVANCED") String optimizationLevel,
        @RequestParam(name = "run", defaultValue = "false") String compileAndRun
    ) {
        OptimizationLevel level = OptimizationLevel.byName(optimizationLevel, OptimizationLevel.ADVANCED);
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
                new CompilerProfile(true, level).setRun(run),
                InputFiles.fromSource(sourceCode));

        final long start = System.nanoTime();
        compiler.compile();
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        final String compiledCode = compiler.getOutput();
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
                        optimizationLevel,
                        processRunOutput(compiler),
                        compiler.getSteps())
        );
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
        return compiler.getMessages().stream().filter(filter)
                .map(WebappMessage::transform)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String processRunOutput(MindcodeCompiler compiler) {
        if (compiler.hasErrors()) {
            return null;
        } else {
            String output = compiler.getTextBuffer().getFormattedOutput();
            String text = output.isEmpty() ? "The program produced no output." : output;

            if (compiler.getExecutionException() != null) {
                text = text + "\n" + compiler.getExecutionException().getWebAppMessage();
            }

            return text;
        }
    }
}
