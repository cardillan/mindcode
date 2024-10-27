package info.teksol.mindcode.webapp;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static info.teksol.mindcode.compiler.CompilerFacade.compile;
import static org.junit.jupiter.api.Assertions.*;

class SamplesTest {

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode validateSamples() {
        final String dirname = "src/main/resources/samples/mindcode";
        final File[] files = new File(dirname).listFiles((d, f) -> f.toLowerCase().endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one sample; found none");

        return DynamicContainer.dynamicContainer("Optimization tests",
                Stream.of(files)
                        .map(file -> DynamicTest.dynamicTest(file.getName(), null,
                                () -> evaluateSample(file)))
        );
    }

    private void evaluateSample(File file) throws IOException {
        String sourceCode = Files.readString(file.toPath());
        final CompilerOutput<String> result = compile(true, sourceCode,
                OptimizationLevel.BASIC, false);

        result.messages().stream().filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::formatMessage)
                .forEach(System.out::println);

        assertFalse(result.hasErrors() || result.hasWarnings(), "Script produced errors or warnings.");
    }
}
