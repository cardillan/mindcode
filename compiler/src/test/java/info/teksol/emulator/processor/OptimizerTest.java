package info.teksol.emulator.processor;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class OptimizerTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/optimizer";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, OptimizerTest.class.getSimpleName());
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode runScripts() {
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");

        return DynamicContainer.dynamicContainer("Optimization tests",
                Stream.of(files)
                .map(File::getName)
                .map(f -> DynamicTest.dynamicTest(f, null, () -> compileAndOutputFile(f)))
        );
    }
}
