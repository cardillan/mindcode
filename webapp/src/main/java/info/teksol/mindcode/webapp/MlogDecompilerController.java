package info.teksol.mindcode.webapp;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.decompiler.MlogDecompiler;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value = "/mlog-decompiler")
public class MlogDecompilerController {
    private static final Logger logger = LoggerFactory.getLogger(MlogDecompilerController.class);

    @Autowired
    private SourceRepository sourceRepository;

    @PostMapping("/decompile-mlog")
    public String postCompile(@RequestParam(required = false) String id,
                              @RequestParam String source) {
        Source textDto;
        if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            final Optional<Source> dto = sourceRepository.findById(UUID.fromString(id));
            final Source newSource = dto
                    .map(sdto -> sdto.withSource(source))
                    .orElseGet(() -> new Source(source, Instant.now()));
            textDto = sourceRepository.save(newSource);
        } else {
            textDto = sourceRepository.save(new Source(source, Instant.now()));
        }

        return "redirect:/mlog-decompiler?s=" + textDto.getId().toString();
    }

    @GetMapping
    public ModelAndView getHomePage(@RequestParam(name = "s", defaultValue = "") String id) {
        final String mlog;
        if (id != null && id.equals("clean")) {
            mlog = "";
        } else if (id != null && id.matches("\\A[a-f0-9]{8}(?:-[a-f0-9]{4}){3}-[a-f0-9]{12}\\z")) {
            final Optional<Source> source = sourceRepository.findById(UUID.fromString(id));
            if (source.isPresent()) {
                mlog = source.get().getSource();
            } else {
                mlog = "// 404 Not Found";
            }
        } else {
            mlog = "";
        }

        final long start = System.nanoTime();
        String result;
        try {
            result = MlogDecompiler.decompile(mlog);
        } catch (Exception e) {
            logger.error("Error decompiling mlog code", e);
            result = "Internal error";
        }

        final long end = System.nanoTime();
        logger.info("performance mlog_decompiled_in={}ms", TimeUnit.NANOSECONDS.toMillis(end - start));

        return new ModelAndView(
                "mlog-decompiler",
                "model",
                new HomePageData(
                        id,
                        "",
                        mlog,
                        (int) mlog.chars().filter(ch -> ch == '\n').count(),
                        result,
                        (int) result.chars().filter(ch -> ch == '\n').count(),
                        List.of(),
                        List.of(),
                        List.of(),
                        OptimizationLevel.EXPERIMENTAL.name(),
                        null,
                        0)
        );
    }

}
