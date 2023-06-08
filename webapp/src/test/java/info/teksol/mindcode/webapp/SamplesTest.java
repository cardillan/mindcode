package info.teksol.mindcode.webapp;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.LogicInstructionLabelResolver;
import info.teksol.mindcode.compiler.LogicInstructionPrinter;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.MindcodeOptimizer;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SamplesTest {
    private static final boolean onWindowsPlatform = System.getProperty("os.name").toLowerCase().contains("win");

    private final InstructionProcessor instructionProcessor =
            InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);

    private static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program, CompilerProfile profile) {
        LogicInstructionGenerator generator = new LogicInstructionGenerator(profile, instructionProcessor, s -> {});
        GeneratorOutput generatorOutput  = generator.generate(program);
        MindcodeOptimizer optimizer = new MindcodeOptimizer(instructionProcessor, profile, s -> {});
        return optimizer.optimize(generatorOutput);
    }

    private  List<LogicInstruction> generateAndOptimize(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.fullOptimizations());
    }

    private  List<LogicInstruction> generateUnoptimized(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.noOptimizations());
    }

    @TestFactory
    List<DynamicTest> validateSamples() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/main/resources/samples/mindcode";
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

    private void compile(String source, File file) throws IOException {
        final List<String> errors = new ArrayList<>();
        ErrorListener errorListener = new ErrorListener(errors);

        final MindcodeLexer lexer = new MindcodeLexer(CharStreams.fromString(source));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        final MindcodeParser parser = new MindcodeParser(new BufferedTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq program = AstNodeBuilder.generate(context);
        List<LogicInstruction> unoptimized = generateUnoptimized(program);
        List<LogicInstruction> optimized = generateAndOptimize(program);

        final File tmp = new File("tmp", "samples");
        if (!tmp.exists()) assertTrue(tmp.mkdirs());

        final File unoptimizedTarget = new File(tmp, file.getName() + "-unoptimized.txt");
        final File optimizedTarget = new File(tmp, file.getName() + "-optimized.txt");
        final File diffTarget = new File(tmp, file.getName() + ".patch");

        try (final Writer w = new FileWriter(unoptimizedTarget)) {
            w.write(LogicInstructionPrinter.toString(instructionProcessor, unoptimized));
        }

        try (final Writer w = new FileWriter(optimizedTarget)) {
            w.write(LogicInstructionPrinter.toString(instructionProcessor, optimized));
        }

        // Async run a diff process between the unoptimized and optimized versions
        // This helps developers determine what optimizations were actually applied
        // We don't need to wait for this process: the files can be diffed in the background, and we can continue on our merry way
        new ProcessBuilder()
                .command(onWindowsPlatform ? "fc.exe" : "diff", "-u", unoptimizedTarget.getAbsolutePath(), optimizedTarget.getAbsolutePath())
                .redirectOutput(diffTarget)
                .start();

        final List<LogicInstruction> result = LogicInstructionLabelResolver.resolve(instructionProcessor, optimized);

        final String opcodes = LogicInstructionPrinter.toString(instructionProcessor, result);
        assertFalse(opcodes.isEmpty(), "Failed to generateUnoptimized a Logic program out of:\n" + source);
        assertTrue(errors.isEmpty(), errors.toString());
    }

    private static class ErrorListener extends BaseErrorListener {
        private final List<String> errors;

        public ErrorListener(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            if (offendingSymbol == null) {
                errors.add("Syntax error on line " + line + ":" + charPositionInLine + ": " + msg);
            } else {
                errors.add("Syntax error: " + offendingSymbol + " on line " + line + ":" + charPositionInLine + ": " + msg);
            }
        }
    }
}
