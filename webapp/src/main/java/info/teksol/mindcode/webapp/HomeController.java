package info.teksol.mindcode.webapp;

import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionLabelResolver;
import info.teksol.mindcode.mindustry.LogicInstructionPrinter;
import org.antlr.v4.runtime.*;
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

        final List<String> sources = List.of(
                "1-bind-poly-move-to-core.mnd",
                "2-thorium-reactor-stopper.mnd",
                "8-heal-damaged-building.mnd",
                "3-multi-thorium-reactor.mnd",
                "5-mining-drone.mnd",
                "6-upgrade-copper-conveyors-to-titanium.mnd")
                .stream()
                .map((filename) -> {
                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(HomeController.class.getClassLoader().getResourceAsStream("samples/" + filename)))) {
                        final StringWriter out = new StringWriter();
                        reader.transferTo(out);
                        return out.toString();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read sample: " + filename);
                    }
                })
                .collect(Collectors.toList());

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
                              @RequestParam String source) {
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

        return "redirect:/?s=" + sourceDto.getId().toString();
    }

    @GetMapping
    public ModelAndView getHomePage(@RequestParam(name = "s", defaultValue = "") String id) {
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
        final Tuple2<String, List<String>> result = compile(sourceCode);
        final long end = System.nanoTime();
        logger.info("performance compiled_in={}µs", TimeUnit.NANOSECONDS.toMicros(end - start));

        final String compiledCode = result._1;
        final List<String> syntaxErrors = result._2;
        return new ModelAndView(
                "home",
                "model",
                new HomePageData(
                        id,
                        sampleName,
                        sourceCode,
                        sourceCode.split("\n").length,
                        compiledCode,
                        compiledCode.split("\n").length,
                        syntaxErrors)
        );
    }

    private Tuple2<String, List<String>> compile(String sourceCode) {
        String instructions = "";

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(sourceCode));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        try {
            final MindcodeParser.ProgramContext context = parser.program();
            final Seq prog = AstNodeBuilder.generate(context);

            List<LogicInstruction> result = LogicInstructionGenerator.generateAndOptimize(prog);
            result = LogicInstructionLabelResolver.resolve(result);
            instructions = LogicInstructionPrinter.toString(result);
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
        }

        return new Tuple2<>(instructions, errors);
    }
}
