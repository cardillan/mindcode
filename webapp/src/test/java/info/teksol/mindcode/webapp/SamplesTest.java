package info.teksol.mindcode.webapp;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionLabelResolver;
import info.teksol.mindcode.mindustry.LogicInstructionPrinter;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SamplesTest {
    private static final boolean onWindowsPlatform = System.getProperty("os.name").toLowerCase().contains("win");

    @TestFactory
    List<DynamicTest> validateSamples() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/main/resources/samples";
        final File[] files = new File(dirname).listFiles();
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one template; found none");
        Arrays.sort(files);

        for (final File sample : files) {
            result.add(DynamicTest.dynamicTest(sample.getName(), null, () -> evaluateSample(sample)));
        }

        return result;
    }

    private void evaluateSample(File sample) throws IOException {
        final StringWriter sw = new StringWriter();
        try (final FileReader reader = new FileReader(sample)) {
            reader.transferTo(sw);
        }
        compile(sw.toString(), sample);
    }

    private void compile(String program, File source) throws IOException {
        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(program));
        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        final List<String> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        });

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq prog = AstNodeBuilder.generate(context);
        List<LogicInstruction> unoptimized = LogicInstructionGenerator.generateUnoptimized(prog);
        List<LogicInstruction> optimized = LogicInstructionGenerator.generateAndOptimize(prog);

        final File tmp = new File("tmp", "samples");
        if (!tmp.exists()) assertTrue(tmp.mkdirs());

        final File unoptimizedTarget = new File(tmp, source.getName() + "-unoptimized.txt");
        final File optimizedTarget = new File(tmp, source.getName() + "-optimized.txt");
        final File diffTarget = new File(tmp, source.getName() + ".patch");

        try (final Writer w = new FileWriter(unoptimizedTarget)) {
            w.write(LogicInstructionPrinter.toString(unoptimized));
        }

        try (final Writer w = new FileWriter(optimizedTarget)) {
            w.write(LogicInstructionPrinter.toString(optimized));
        }

        // Async run a diff process between the unoptimized and optimized versions
        // This helps developers determine what optimizations were actually applied
        // We don't need to wait for this process: the files can be diffed in the background, and we can continue on our merry way
        new ProcessBuilder()
                .command(onWindowsPlatform ? "fc.exe" : "diff", "-u", unoptimizedTarget.getAbsolutePath(), optimizedTarget.getAbsolutePath())
                .redirectOutput(diffTarget)
                .start();

        final List<LogicInstruction> result = LogicInstructionLabelResolver.resolve(
                optimized
        );

        final String opcodes = LogicInstructionPrinter.toString(result);
        assertFalse(opcodes.isEmpty(), "Failed to generateUnoptimized a Logic program out of:\n" + program);
        assertTrue(errors.isEmpty(), errors.toString());
    }
}
