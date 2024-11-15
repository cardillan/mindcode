package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerFacade;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
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
        @RequestParam(name = "optimizationLevel", defaultValue = "BASIC") String optimizationLevel,
        @RequestParam(name = "run", defaultValue = "false") String compileAndRun
    ) {
        OptimizationLevel level = OptimizationLevel.byName(optimizationLevel, OptimizationLevel.BASIC);
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

        final long start = System.nanoTime();
        final CompilerOutput<String> result = CompilerFacade.compile(true, sourceCode, level, run);
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        final String compiledCode = result.output();
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
                        result.errors(WebappMessage::transform),
                        result.warnings(WebappMessage::transform),
                        result.infos(WebappMessage::transform),
                        optimizationLevel,
                        run && !result.hasErrors() ? processRunOutput(result.getProgramOutput()) : null,
                        result.steps())
        );
    }

    private String processRunOutput(String output) {
        return output == null || output.isEmpty() ? "The program produced no output." : output;
    }
}
