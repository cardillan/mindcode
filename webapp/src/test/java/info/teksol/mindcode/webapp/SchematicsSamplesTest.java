package info.teksol.mindcode.webapp;

import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.schemacode.SchemacodeCompiler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchematicsSamplesTest {

    @TestFactory
    List<DynamicTest> validateSamples() {
        final List<DynamicTest> result = new ArrayList<>();
        final String dirname = "src/main/resources/samples/schematics";
        final File[] files = new File(dirname).listFiles((d, f) -> f.toLowerCase().endsWith(".sdf"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one sample; found none");
        Arrays.sort(files);

        for (final File sample : files) {
            result.add(DynamicTest.dynamicTest(sample.getName(), null, () -> evaluateSample(sample)));
        }

        return result;
    }

    private void evaluateSample(File file) throws IOException {
        String sample = Files.readString(file.toPath());
        buildSchematic(sample, file);
    }

    private void buildSchematic(String sample, File file) {
        CompilerOutput<byte[]> output = SchemacodeCompiler.compile(sample, CompilerProfile.fullOptimizations(true), null);
        output.errors().forEach(Assertions::fail);
        assertFalse(output.hasErrors(), "Failed to compile sample " + file.getPath());
    }
}
