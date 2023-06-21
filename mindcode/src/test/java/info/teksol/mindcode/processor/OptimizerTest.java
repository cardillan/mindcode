package info.teksol.mindcode.processor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptimizerTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/optimizer";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, OptimizerTest.class.getSimpleName());
    }

    @TestFactory
    List<DynamicTest> runScripts() {
        final List<DynamicTest> result = new ArrayList<>();
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");
        Arrays.sort(files);

        for (final File file : files) {
            String fileName = file.getName();
            result.add(DynamicTest.dynamicTest(fileName, null, () -> compileAndOutputFile(fileName)));
        }

        return result;
    }
}
