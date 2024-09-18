package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private static final Map<String, String> samples;

    static {
        final Map<String, String> theSamples = new HashMap<>();
        final List<String> sampleNames = List.of(
                "bind-single-unit",
                "one-thorium",
                "heal-damaged-building",
                "many-thorium",
                "mine-coord",
                "upgrade-conveyors"
        );

        final List<String> sources = Stream.of(
                "1-bind-poly-move-to-core.mnd",
                "2-thorium-reactor-stopper.mnd",
                "8-heal-damaged-building.mnd",
                "3-multi-thorium-reactor.mnd",
                "5-mining-drone.mnd",
                "6-upgrade-copper-conveyors-to-titanium.mnd")
                .map((filename) -> {
                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(HomeController.class.getClassLoader().getResourceAsStream("samples/mindcode/" + filename)))) {
                        final StringWriter out = new StringWriter();
                        reader.transferTo(out);
                        return out.toString();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read sample: " + filename);
                    }
                })
                .toList();

        for (int i = 0; i < sampleNames.size(); i++) {
            theSamples.put(sampleNames.get(i), sources.get(i));
        }

        samples = theSamples;
    }

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

    @GetMapping
    public ModelAndView getHomePage(
        @RequestParam(name = "s", defaultValue = "") String id,
        @RequestParam(name = "optimizationLevel", defaultValue = "BASIC") String optimizationLevel
    ) {
        OptimizationLevel level = OptimizationLevel.byName(optimizationLevel, OptimizationLevel.BASIC);
        final boolean enableOptimization = level != OptimizationLevel.OFF;
        final String sampleName;
        final String sourceCode;
        if (samples.containsKey(id)) {
            sampleName = id;
            sourceCode = samples.get(sampleName);
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
        } else {
            final int skipCount = random.nextInt(samples.size());
            sampleName = samples.keySet().stream().skip(skipCount).findFirst().get();
            sourceCode = samples.get(sampleName);
        }

        final long start = System.nanoTime();
        final CompilerOutput<String> result = compile(true, sourceCode, level);
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
                        result.errors(),
                        result.warnings(),
                        result.infos(),
                        enableOptimization,
                        optimizationLevel)
        );
    }

}
