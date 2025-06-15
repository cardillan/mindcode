package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.CaseSwitcher.ConvertCaseExpressionAction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
@Order(5)
public class CaseSwitcherConfigurationGeneratorTest extends AbstractOptimizerTest<CaseSwitcher> {
    private static final int MAX_STRENGTH = 10;

    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/caseswitcher";

    protected String getScriptsDirectory() {
        return SCRIPTS_DIRECTORY;
    }

    @Override
    protected @Nullable Class<CaseSwitcher> getTestedClass() {
        return CaseSwitcher.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    int[] executeCaseSwitchingTest(String fileContent, int strength) {
        List<ConvertCaseExpressionAction> diagnosticData = new ArrayList<>();
        String source = "#set case-optimization-strength = " + strength + ";\n" + fileContent;
        compile(expectedMessages(), source, compiler -> {
            diagnosticData.addAll(compiler.getDiagnosticData(ConvertCaseExpressionAction.class));
            if (diagnosticData.isEmpty()) {
                throw new IllegalStateException("No diagnostic data found.");
            }
        });

        diagnosticData.sort(Comparator.comparingInt(ConvertCaseExpressionAction::rawCost));
        int max = diagnosticData.getLast().rawCost() + 1;

        int[] result = new int[max];
        Arrays.fill(result, diagnosticData.getLast().originalSteps());
        for (ConvertCaseExpressionAction d : diagnosticData) {
            Arrays.fill(result, d.rawCost(), max, d.executionSteps());
        }

        return result;
    }

    void executeCaseSwitchingTest(String fileName) throws IOException {
        String fileContent = readFile(fileName);
        StringBuilder text = new StringBuilder();
        StringBuilder text2 = new StringBuilder();
        List<int[]> fullData = new ArrayList<>();
        List<String> regressions = new ArrayList<>();
        int[] last = null;
        int lastSum = 0;
        text.append("Strength;Total steps;Avg steps;Avg difference;Regression\n");
        for (int strength = 0; strength <= MAX_STRENGTH; strength++) {
            int[] steps = executeCaseSwitchingTest(fileContent, strength);
            if (last != null) {
                if (last.length != steps.length) {
                    throw new IllegalStateException("Steps length mismatch.");
                }

                int sum = 0;
                boolean regression = false;
                for (int i = 0; i < steps.length; i++) {
                    if (steps[i] > last[i] && last[i] > 0) {
                        regression = true;
                    }
                    sum += steps[i];
                }

                text.append(strength).append(";")
                        .append(sum).append(";")
                        .append(String.format(Locale.US, "%.2f", ((double)sum) / steps.length)).append(";")
                        .append(lastSum > 0 ? String.format(Locale.US, "%.2f", ((double)sum - lastSum) / steps.length) : "").append(";")
                        .append(regression ? "yes" : "").append("\n");

                lastSum = sum;
                if (regression) regressions.add(String.valueOf(strength));
            }
            fullData.add(steps);
            last = steps;
        }

        addStrengthHeader(text);
        addStrengthHeader(text2);
        int[] lastRecord = new int[fullData.size()];
        int maxSize = fullData.getFirst().length;
        for (int codeSize = 0; codeSize < maxSize; codeSize++) {
            boolean diff = false;
            for (int j = 0; j < fullData.size(); j++) {
                if (lastRecord[j] != fullData.get(j)[codeSize]) {
                    lastRecord[j] = fullData.get(j)[codeSize];
                    diff = true;
                }
            }

            if (diff) {
                text.append("Size ").append(codeSize);
                text2.append("Size ").append(codeSize);
                for (int j = 0; j < fullData.size(); j++) {
                    int[] curr = fullData.get(j);
                    text.append(";").append(curr[codeSize]);
                    if (j > 0) {
                        int[] prev =  fullData.get(j - 1);
                        text2.append(";");
                        int change = curr[codeSize] - prev[codeSize];
                        if (change != 0) text2.append(change);
                    } else {
                        text2.append(";");
                    }
                }
                text.append("\n");
                text2.append("\n");
            }
        }

        text.append(text2);

        Path path = Path.of(getScriptsDirectory(), fileName.replace(".mnd", ".txt"));
        Files.writeString(path, text);

        assertTrue(regressions.isEmpty(), "Regressions at strength: " + String.join(", ", regressions));
    }

    private void addStrengthHeader(StringBuilder text) {
        text.append("\nStrength");
        for (int i = 0; i <= MAX_STRENGTH; i++) text.append(";").append(i);
        text.append("\n");
    }

    private String getTestName(String fileName) {
        return fileName.replace(".mnd", "");
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode optimizesCaseExpression() {
        final File[] files = new File(getScriptsDirectory()).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + getScriptsDirectory() + "; found none");

        return DynamicContainer.dynamicContainer("Case Switcher Configuration generator tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(name -> DynamicTest.dynamicTest(
                                getTestName(name), null,
                                () -> executeCaseSwitchingTest(name))
                        ));
    }
}