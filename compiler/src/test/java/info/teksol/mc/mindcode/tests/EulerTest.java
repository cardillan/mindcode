package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
@Order(6)
public class EulerTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/euler";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, EulerTest.class.getSimpleName());
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode runScripts() {
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");

        return DynamicContainer.dynamicContainer("Project Euler tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(name -> DynamicTest.dynamicTest(name, null, () -> testAndEvaluateFile(name)))
        );
    }
}
