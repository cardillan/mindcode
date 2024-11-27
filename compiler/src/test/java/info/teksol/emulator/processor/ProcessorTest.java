package info.teksol.emulator.processor;

import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Some Mindcode code tested in this class can be quite complex and hard to maintain as a string constant.
// Such code can be saved as a file in the src/test/resources/scripts directory.
@Order(4)
public class ProcessorTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/processor";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProcessorTest.class.getSimpleName());
    }

    @Test
    void testSuiteRecognizesUnexpectedWarnings() {
        TestCompiler testCompiler = createTestCompiler();
        testAndEvaluateCode(
                testCompiler,
                null,
                """
                        a = 10;
                        print("hi");
                        """,
                Map.of(),
                ExpectedMessages.create().add("List of unused variables: a."),
                outputEvaluator(testCompiler, List.of("hi")),
                null
        );
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode processesExternalScriptsTest() {
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");

        return DynamicContainer.dynamicContainer("Processor tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(name -> DynamicTest.dynamicTest(name, null,
                                () -> testAndEvaluateFile(name))
        ));
    }
}
