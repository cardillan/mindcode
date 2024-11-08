package info.teksol.mindcode.samples;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerFacade;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.schemacode.SchemacodeCompiler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
        evaluateOutput(sample, CompilerFacade.compile(true, sample.source(),
                OptimizationLevel.BASIC, false));
    }

    private void buildSchematic(Sample sample) {
        evaluateOutput(sample, SchemacodeCompiler.compile(
                InputFiles.fromSource(sample.source()),
                CompilerProfile.fullOptimizations(true)));
    }

    private void evaluateOutput(Sample sample, CompilerOutput<?> output) {
        output.messages().stream().filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::formatMessage)
                .forEach(System.out::println);

        assertFalse(output.hasErrors() || output.hasWarnings(), "Sample " + sample.name() + " generated warnings or errors.");
    }
}