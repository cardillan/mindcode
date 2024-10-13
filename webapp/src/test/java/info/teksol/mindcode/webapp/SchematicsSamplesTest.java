package info.teksol.mindcode.webapp;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.SchemacodeCompiler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SchematicsSamplesTest {

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode validateSamples() {
        final String dirname = "src/main/resources/samples/schematics";
        final File[] files = new File(dirname).listFiles((d, f) -> f.toLowerCase().endsWith(".sdf"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one sample; found none");

        return DynamicContainer.dynamicContainer("Optimization tests",
                Stream.of(files)
                        .map(file -> DynamicTest.dynamicTest(file.getName(), null,
                                () -> evaluateSample(file)))
        );
    }

    private void evaluateSample(File file) throws IOException {
        String sample = Files.readString(file.toPath());
        buildSchematic(sample, file);
    }

    private void buildSchematic(String sample, File file) {
        CompilerOutput<byte[]> output = SchemacodeCompiler.compile(sample, CompilerProfile.fullOptimizations(true), null);
        output.errors(MindcodeMessage::message).forEach(Assertions::fail);
        assertFalse(output.hasErrors(), "Failed to compile sample " + file.getPath());
    }
}
