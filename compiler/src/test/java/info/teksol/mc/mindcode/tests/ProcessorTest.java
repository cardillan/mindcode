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
@Order(4)
public class ProcessorTest extends AbstractProcessorTest {

    // Some Mindcode code tested in subclasses of this class can be quite complex and hard to maintain
    // as a string constant. Such code can be saved as a file in subdirectories of this directory.
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/processor";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProcessorTest.class.getSimpleName());
    }

    @Test
    void processesBasicCode() {
        testCode("""
                allocate heap in bank1[0...512];
                $A = 10;
                assertEquals(10, $A, "value from memory");
                stopProcessor();
                """);
    }

    @Test
    void processesBasicCode2() {
        testCode("""
                param a = 25;
                print(sqrt(a));
                """,
                "5");
    }

    @Test
    void testSuiteRecognizesUnexpectedWarnings() {
        testCode(
                expectedMessages().add("Variable 'a' is not used."),
                """
                        a = 10;
                        print("hi");
                        """,
                "hi");
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
