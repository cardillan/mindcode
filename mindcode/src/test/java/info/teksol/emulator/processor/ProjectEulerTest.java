package info.teksol.emulator.processor;

import info.teksol.emulator.blocks.Memory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Order(6)
public class ProjectEulerTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/euler";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProjectEulerTest.class.getSimpleName());
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
                        .map(name -> DynamicTest.dynamicTest(name, null, () -> processFile(name)))
        );
    }

    private void processFile(String fileName) throws IOException {
        testAndEvaluateFile(
                createTestCompiler(),
                fileName,
                s -> s + "\ndef expect(v) print(v); end; def actual(v) print(v); end;",
                Map.of("bank2", Memory.createMemoryBank()),
                (useAsserts, actualOutput) -> {
                    if (useAsserts) {
                        assertEquals(2, actualOutput.size(), "Expected the script to generate two output values: expected and actual.");
                        assertEquals(actualOutput.get(0), actualOutput.get(1));
                    }
                    return actualOutput.size() == 2 && actualOutput.get(0).equals(actualOutput.get(1));
                });
    }
}
