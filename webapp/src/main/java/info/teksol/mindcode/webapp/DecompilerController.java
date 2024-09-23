package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.schemacode.SchematicsDecompiler;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value = "/decompiler")
public class DecompilerController {
    private static final Logger logger = LoggerFactory.getLogger(DecompilerController.class);

    @Autowired
    private SourceRepository sourceRepository;

    @PostMapping("/decompile")
    public String postCompile(@RequestParam(required = false) String id,
                              @RequestParam String source) {
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

        return "redirect:/decompiler?s=" + schematicDto.getId().toString();
    }

    @GetMapping
    public ModelAndView getHomePage(@RequestParam(name = "s", defaultValue = "") String id) {
        final String sampleName;
        final String sourceCode;
        if (id != null && id.equals("clean")) {
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
            sampleName = "";
            sourceCode = "";
        }

        final long start = System.nanoTime();
        final CompilerOutput<String> result = SchematicsDecompiler.decompile(sourceCode);
        final long end = System.nanoTime();
        logger.info("performance decompiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        final String compiledCode = result.output();
        return new ModelAndView(
                "decompiler",
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
                        OptimizationLevel.BASIC.name(),
                        null)
        );
    }

}
