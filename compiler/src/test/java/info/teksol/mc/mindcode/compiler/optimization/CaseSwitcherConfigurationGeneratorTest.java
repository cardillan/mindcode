package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.optimization.cases.CaseSwitcherConfigurations;
import info.teksol.mc.mindcode.compiler.optimization.cases.ConvertCaseOptimizationAction;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.util.StringUtils;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
@Order(5)
public class CaseSwitcherConfigurationGeneratorTest extends AbstractOptimizerTest<CaseSwitcher> {
    public static final String SCRIPTS_DIRECTORY = SCRIPTS_BASE_DIRECTORY + "/caseswitcher";

    private static final int MAX_STRENGTH = 6;
    private record TestResult(int configurationCount, int[] steps) {}
    private record StrengthStatistic(int configurations, double improvement) {}

    private static final Map<String, List<StrengthStatistic>> results = new ConcurrentHashMap<>();

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setUseTextJumpTables(false).setUseTextTranslations(false);
    }

    @AfterAll
    static void done() throws IOException {
        StringBuilder text = new StringBuilder();
        TreeMap<String, List<StrengthStatistic>> sorted = new TreeMap<>(results);

        text.append("Strength;Improvement");
        sorted.keySet().forEach(key -> text.append(";").append(key));
        text.append("\n");

        for (int i = 1; i <= MAX_STRENGTH; i++) {
            text.append(i);
            final int strength = i;
            double improvement = sorted.values().stream().map(l -> l.get(strength)).mapToDouble(StrengthStatistic::improvement).average().orElse(0);
            text.append(";").append(str(improvement));
            sorted.values().forEach(l -> text.append(";").append(str(l.get(strength).improvement)));
            text.append("\n");
        }

        Path path = Path.of(SCRIPTS_DIRECTORY, CaseSwitcherConfigurationGeneratorTest.class.getSimpleName() + ".txt");
        Files.writeString(path, StringUtils.normalizeLineEndings(text.toString()));
    }

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

    private static String str(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static double d(int i) {
        return i;
    }

    TestResult executeCaseSwitchingTest(String fileContent, int strength) {
        List<CaseSwitcherConfigurations> configurations = new ArrayList<>();
        List<ConvertCaseOptimizationAction> actions = new ArrayList<>();
        String source = "#set case-optimization-strength = " + strength + ";\n" + fileContent;
        compile(expectedMessages(), source, compiler -> {
            configurations.addAll(compiler.getDiagnosticData(CaseSwitcherConfigurations.class));
            actions.addAll(compiler.getDiagnosticData(ConvertCaseOptimizationAction.class));
            if (configurations.isEmpty() || actions.isEmpty()) {
                throw new IllegalStateException("No diagnostic data found.");
            }
        });

        actions.sort(Comparator.comparingInt(ConvertCaseOptimizationAction::rawCost));
        int max = actions.getLast().rawCost() + 1;

        int[] result = new int[max];
        Arrays.fill(result, actions.getLast().originalSteps());
        for (ConvertCaseOptimizationAction d : actions) {
            Arrays.fill(result, d.rawCost(), max, d.executionSteps());
        }

        return new TestResult(configurations.getFirst().configurationsCount(), result);
    }

    void executeCaseSwitchingTest(String fileName) throws IOException {
        String fileContent = readFile(fileName);
        StringBuilder text = new StringBuilder();
        StringBuilder text2 = new StringBuilder();
        List<int[]> fullData = new ArrayList<>();
        List<String> regressions = new ArrayList<>();
        List<StrengthStatistic> statistics = new ArrayList<>();
        int[] last = null;
        int lastSum = 0;
        text.append("Strength;Configurations;Total steps;Avg steps;Avg improvement;Regression\n");
        for (int strength = 0; strength <= MAX_STRENGTH; strength++) {
            TestResult testResult = executeCaseSwitchingTest(fileContent, strength);
            double avgDifference;
            if (last != null) {
                if (last.length != testResult.steps.length) {
                    throw new IllegalStateException("Steps length mismatch.");
                }

                int sum = 0;
                boolean regression = false;
                for (int i = 0; i < testResult.steps.length; i++) {
                    if (testResult.steps[i] > last[i] && last[i] > 0) {
                        regression = true;
                    }
                    sum += testResult.steps[i];
                }

                avgDifference = d(lastSum - sum) / testResult.steps.length;
                text.append(strength).append(";")
                        .append(testResult.configurationCount).append(";")
                        .append(sum).append(";")
                        .append(str(d(sum) / testResult.steps.length)).append(";")
                        .append(lastSum > 0 ? str(avgDifference) : "").append(";")
                        .append(regression ? "yes" : "").append("\n");

                lastSum = sum;
                if (regression) regressions.add(String.valueOf(strength));
            } else {
                for (int i = 0; i < testResult.steps.length; i++) {
                    lastSum += testResult.steps[i];
                }
                avgDifference = 0;
            }
            statistics.add(new StrengthStatistic(testResult.configurationCount, avgDifference));
            fullData.add(testResult.steps);
            last = testResult.steps;
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
        Files.writeString(path, StringUtils.normalizeLineEndings(text.toString()));

        results.put(fileName.replace(".mnd", ""), statistics);
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
