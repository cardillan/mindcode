package info.teksol.mindcode.webapp;

import info.teksol.mindcode.ast.AstNodeBuilder;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.*;
import info.teksol.mindcode.compiler.generator.GeneratorOutput;
import info.teksol.mindcode.compiler.generator.LogicInstructionGenerator;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.InstructionProcessorFactory;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.grammar.MindcodeLexer;
import info.teksol.mindcode.grammar.MindcodeParser;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SamplesTest {
    private static final boolean onWindowsPlatform = System.getProperty("os.name").toLowerCase().contains("win");

    private static final List<CompilerMessage> messages = new ArrayList<>();

    private final InstructionProcessor instructionProcessor =
            InstructionProcessorFactory.getInstructionProcessor(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);

    private static List<LogicInstruction> generateAndOptimize(InstructionProcessor instructionProcessor, Seq program, CompilerProfile profile) {
        messages.clear();
        LogicInstructionGenerator generator = new LogicInstructionGenerator(profile, instructionProcessor, messages::add);
        GeneratorOutput generatorOutput  = generator.generate(program);
        OptimizationCoordinator optimizer = new OptimizationCoordinator(instructionProcessor, profile, messages::add);
        return optimizer.optimize(generatorOutput);
    }

    private  List<LogicInstruction> generateAndOptimize(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.standardOptimizations(false));
    }

    private  List<LogicInstruction> generateUnoptimized(Seq program) {
        return generateAndOptimize(instructionProcessor, program, CompilerProfile.noOptimizations(false));
    }

    @TestFactory
    List<DynamicTest> validateSamples() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/main/resources/samples/mindcode";
        final File[] files = new File(dirname).listFiles((d, f) -> f.toLowerCase().endsWith(".mnd"));
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

        final MindcodeParser parser = new MindcodeParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        final MindcodeParser.ProgramContext context = parser.program();
        final Seq program = AstNodeBuilder.generate(new SourceFile(file.getPath(), file.getAbsolutePath(), source),context);
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

        final List<LogicInstruction> result = LogicInstructionLabelResolver.resolve(instructionProcessor,
                CompilerProfile.standardOptimizations(false), optimized);

        final String opcodes = LogicInstructionPrinter.toString(instructionProcessor, result);
        assertFalse(opcodes.isEmpty(), "Failed to generateUnoptimized a Logic program out of:\n" + source);
        assertTrue(errors.isEmpty(), errors.toString());

        assertTrue(messages.stream().noneMatch(CompilerMessage::isError),
                "Unexpected error messages:\n" + messages.stream().filter(CompilerMessage::isError).map(CompilerMessage::message)
                        .collect(Collectors.joining("\n")));

        assertTrue(messages.stream().noneMatch(CompilerMessage::isWarning),
                "Unexpected warning messages:\n" + messages.stream().filter(CompilerMessage::isWarning).map(CompilerMessage::message)
                        .collect(Collectors.joining("\n")));
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
