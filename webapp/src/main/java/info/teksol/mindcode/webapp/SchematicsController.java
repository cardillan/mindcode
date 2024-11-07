package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.schemacode.SchemacodeCompiler;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/schematics")
public class SchematicsController {
    private static final Logger logger = LoggerFactory.getLogger(SchematicsController.class);
    private static final Map<String, String> samples;
    private static final List<String> quickSamples;

    static {
        final List<String> sampleNames = List.of(
                "detector",
                "healing-center",
                "on-off-switch",
                "regulator",
                "slow:overdrive-dome-supply",
                "worker-recall-station",
                "scrap-to-metaglass-2",
                "payload-source",
                "slow:mandelbrot-generator"
        );

        samples = sampleNames.stream()
                .map(s -> s.replace("slow:", ""))
                .collect(Collectors.toMap(s -> s, SchematicsController::loadSample));
        quickSamples = sampleNames.stream().filter(s -> !s.startsWith("slow:")).toList();
    }

    private static String loadSample(String sampleName) {
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(SchematicsController.class.getClassLoader().getResourceAsStream("samples/schematics/" + sampleName + ".sdf")))) {
            final StringWriter out = new StringWriter();
            reader.transferTo(out);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read sample: " + sampleName);
        }
    }

    private final Random random = new Random();
    @Autowired
    private SourceRepository sourceRepository;

    @PostMapping("/compile")
    public String postCompile(@RequestParam(required = false) String id,
                              @RequestParam String source,
                              @RequestParam(required = false) String optimizationLevel) {
        Source schematicDto;
        if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            final Optional<Source> dto = sourceRepository.findById(UUID.fromString(id));
            final Source newSource = dto
                    .map(sdto -> sdto.withSource(source))
                    .orElseGet(() -> new Source(source, Instant.now()));
            schematicDto = sourceRepository.save(newSource);
        } else {
            schematicDto = sourceRepository.save(new Source(source, Instant.now()));
        }

        return "redirect:/schematics?optimizationLevel=" + optimizationLevel + "&s=" + schematicDto.getId().toString();
    }

    @GetMapping
    public ModelAndView getHomePage(@RequestParam(name = "s", defaultValue = "") String id,
                                    @RequestParam(name = "optimizationLevel", defaultValue = "ADVANCED") String optimizationLevel) {
        OptimizationLevel level = OptimizationLevel.byName(optimizationLevel, OptimizationLevel.ADVANCED);
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
            final int skipCount = random.nextInt(quickSamples.size());
            sampleName = quickSamples.get(skipCount);
            sourceCode = samples.get(sampleName);
        }

        final long start = System.nanoTime();
        final CompilerOutput<String> result = SchemacodeCompiler.compileAndEncode(
                InputFiles.fromSource(sourceCode),
                new CompilerProfile(true, level));
        final long end = System.nanoTime();
        logger.info("performance built_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        final String compiledCode = result.output();
        return new ModelAndView(
                "schematic",
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
                        null,
                        0)
        );
    }

}
