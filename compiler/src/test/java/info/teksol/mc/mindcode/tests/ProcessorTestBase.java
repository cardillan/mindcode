package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public abstract class ProcessorTestBase extends AbstractProcessorTest {

    // Some Mindcode code tested in subclasses of this class can be quite complex and hard to maintain
    // as a string constant. Such code can be saved as a file in subdirectories of this directory.
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/processor";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @Test
    void processesCaseExpressions() {
        testCode("""
                        #set use-text-translations = false;
                        param x = 1;
                        a = case x
                            when 0 then 0;
                        end;
                        print(a);
                        """,
                "null"
        );
    }

    @Test
    void processesTranslatedCaseExpressions() {
        testCode("""
                        param x = 1;
                        a = case x
                            when 0 then 0;
                        end;
                        print(a);
                        """,
                "null"
        );
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
    void processesAssertBounds() {
        // Array optimization will redirect all array writes to the single element
        // Therefore, the program will output 10.
        testCode(expectedMessages()
                        .add("Failed runtime check: 'position 4:1: index out of bounds (0 to 0)'."),
                """
                        #set error-reporting = assert;
                        #set err-runtime-check-failed = false;
                        var a[1];
                        a[1 + rand(0)] = 10;
                        print(a);
                        stopProcessor();
                        """,
                "10");
    }

    @Test
    void processesGlobalVariableDataFlow() {
        testCode("""
                        var a;
                        var b[4];
                        const c[] = (1, 2, 3, 4);
                        const d[] = (2, 4, 6, 8);
                        param p = 0;
                        
                        begin
                            a = "A"; b = c;
                            foo();
                            a = "B"; b = d;
                            print(a, b[p]);
                        end;
                        
                        noinline def foo()
                            print(a, b[p]);
                        end;
                        """,
                "A", "1", "B", "2");
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
                        .map(name -> DynamicTest.dynamicTest(name,
                                Path.of(getScriptsDirectory(), name).toUri(),
                                () -> testAndEvaluateFile(name))
                        ));
    }
}
