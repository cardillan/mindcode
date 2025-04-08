package info.teksol.mindcode.samples;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import info.teksol.schemacode.SchemacodeCompiler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
class SamplesTest {

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode loadMindcodeSamples() {
        Map<String, Sample> samples = Samples.loadMindcodeSamples();
        assertFalse(samples.isEmpty(), "Expected to find at least one sample; found none");

        return DynamicContainer.dynamicContainer("Mindcode Samples",
                samples.values().stream()
                        .map(sample -> DynamicTest.dynamicTest(sample.name(), null,
                                () -> compileMindcode(sample)))
        );
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode loadSchemacodeSamples() {
        Map<String, Sample> samples = Samples.loadSchemacodeSamples();
        assertFalse(samples.isEmpty(), "Expected to find at least one sample; found none");

        return DynamicContainer.dynamicContainer("Schemacode Samples",
                samples.values().stream()
                        .map(sample -> DynamicTest.dynamicTest(sample.name(), null,
                                () -> buildSchematic(sample)))
        );
    }

    private void compileMindcode(Sample sample) {
        InputFiles inputFiles = InputFiles.fromSource(sample.source());
        CompilerProfile profile = new CompilerProfile(true, OptimizationLevel.BASIC)
                .setSyntacticMode(sample.relaxed() ? SyntacticMode.RELAXED : SyntacticMode.STRICT);

        MindcodeCompiler compiler = new MindcodeCompiler(s -> {}, profile, inputFiles);
        compiler.compile();
        evaluateOutput(sample, compiler.getMessages());
    }

    private void buildSchematic(Sample sample) {
        evaluateOutput(sample, SchemacodeCompiler.compile(
                InputFiles.fromSource(sample.source()),
                CompilerProfile.fullOptimizations(true)).messages());
    }

    private void evaluateOutput(Sample sample, List<MindcodeMessage> output) {
        List<String> unexpectedMessages = output.stream().filter(MindcodeMessage::isErrorOrWarning)
                .filter(message -> !message.message().matches("Optimization passes limit \\(\\d+\\) reached\\."))
                .map(MindcodeMessage::formatMessage)
                .toList();

        unexpectedMessages.forEach(System.out::println);
        assertTrue(unexpectedMessages.isEmpty(), "Sample " + sample.name() + " generated warnings or errors.");
    }
}