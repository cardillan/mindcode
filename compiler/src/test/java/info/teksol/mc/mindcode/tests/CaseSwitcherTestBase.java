package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public abstract class CaseSwitcherTestBase extends AbstractProcessorTest {
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/caseswitcher";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    void executeCaseSwitchingTest(String fileName, int codeSize) throws IOException {
        testAndEvaluateCode(
                getTestName(fileName, codeSize),
                expectedMessages(),
                decorateCode(fileName, codeSize),
                Map.of(),
                assertEvaluator(),
                Path.of(getScriptsDirectory(),
                        fileName.replace(".mnd", "") + "-limit-" + codeSize + logSuffix)
        );
    }

    private String decorateCode(String fileName, int codeSize) throws IOException {
        return (codeSize == 0
                ? "#set case-switching = none;"
                : "#set instruction-limit = " + codeSize + ";\n"
        ) + readFile(fileName);
    }

    private String getTestName(String fileName, int codeSize) {
        return fileName
                .replace(".mnd", "")
                .replace("case-switching-", "Case switcher ")
                + String.format(" (limit %4d)", codeSize);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode optimizesCaseExpression() {
        List<Integer> codeSizes = List.of(0, 50, 100, 500);
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");

        return DynamicContainer.dynamicContainer("Case Switcher tests",
                Stream.of(files)
                        .map(File::getName)
                        .flatMap(name -> codeSizes.stream().map(codeSize -> DynamicTest.dynamicTest(
                                getTestName(name, codeSize), null,
                                () -> executeCaseSwitchingTest(name, codeSize))
                        )));
    }
}
