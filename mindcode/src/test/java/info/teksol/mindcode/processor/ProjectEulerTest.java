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

import static org.junit.jupiter.api.Assertions.*;

public class ProjectEulerTest extends AbstractProcessorTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/mindcode/processor/euler";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @BeforeAll
    static void init() {
        AbstractProcessorTest.init();
    }

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProjectEulerTest.class.getSimpleName());
    }

    @TestFactory
    List<DynamicTest> runScripts() {
        final List<DynamicTest> result = new ArrayList<>();
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");
        Arrays.sort(files);

        for (final File file : files) {
            processFile(result, file);
        }

        return result;
    }

    private void processFile(List<DynamicTest> result, File file) {
        String fileName = file.getName();
        result.add(DynamicTest.dynamicTest(fileName, null, () -> testAndEvaluateFile(
                createTestCompiler(),
                fileName,
                s -> s + "\ndef expect(v) print(v) end def actual(v) print(v) end",
                List.of(MindustryMemory.createMemoryBank("bank2")),
                (useAsserts, actualOutput) -> {
                    if (useAsserts) {
                        assertEquals(2, actualOutput.size(), "Expected the script to generate two output values: expected and actual.");
                        assertEquals(actualOutput.get(0), actualOutput.get(1));
                    }
                    return actualOutput.size() == 2 && actualOutput.get(0).equals(actualOutput.get(1));
                })));
    }
}
