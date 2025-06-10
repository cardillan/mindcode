package info.teksol.mindcode.exttest;

import info.teksol.mc.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorScreening;
import info.teksol.mindcode.exttest.forkjoin.ForkJoinFramework;
import info.teksol.mindcode.exttest.threadpool.ThreadPoolFramework;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@NullMarked
public class ExtendedTests {

    public static void main(String[] args) {
        if (OptimizationCoordinator.isDebugOn()) {
            System.err.println("Error: debug active.");
            return;
        }

        Configuration configuration = ConfigurationReader.loadConfiguration("settings.yaml");

        if (configuration == null) {
            System.err.println("Error: cannot parse settings.");
            return;
        }


        try (PrintWriter master = new PrintWriter("results.txt")) {
            List<TestConfiguration> testConfigurations = new CopyOnWriteArrayList<>();

            if (!runScreeningTests(master, configuration, testConfigurations)) {
                return;
            }

            List<String> failedSources = new ArrayList<>();

            int test = 1;
            for (TestConfiguration testConfiguration : testConfigurations) {
                printf(master, "%n");
                printf(master,"TEST %d/%d%n", test++, testConfigurations.size());
                printf(master,"    Source: %s (%s), parallel tests: %d%n", testConfiguration.getSourceFileName(),
                        testConfiguration.isRun() ? "compile and run" : "compile only",
                        configuration.threads());
                TestCaseCreator testCaseCreator = testConfiguration.getTestCaseCreator();
                printf(master,"    Sampling %,d configurations out of %,d total configurations (coverage %.3g%%).%n",
                        testCaseCreator.getSampleCount(), testCaseCreator.getTotalCases(),
                        100d * testCaseCreator.getSampleCount() / testCaseCreator.getTotalCases());
                master.flush();

                TestProgress progress = new BasicTestProgress(testConfiguration);

                try (PrintWriter writer = new PrintWriter(testConfiguration.getResultPath().toFile())) {
                    ExecutionFramework executionFramework = new ForkJoinFramework(testConfiguration, progress);
                    executionFramework.process(writer);
                    progress.printFinalMessage(writer);
                    progress.printStatistics(writer);
                    if (progress.getFailureCount() > 0) {
                        master.printf("*** %d FAILURES ENCOUNTERED!%n", progress.getFailureCount());
                        failedSources.add(testConfiguration.getSourceFileName());
                    } else {
                        master.println("    No errors.");
                    }
                    master.flush();
                }
            }

            printf(master, "%n%n");
            if (failedSources.isEmpty()) {
                printf(master, "All tests succeeded.%n");
            } else {
                printf(master, "%d out of %d test runs failed.%n", failedSources.size(), testConfigurations.size());
                printf(master, "Failed source files:%n");
                failedSources.forEach(fileName -> printf(master, "    %s%n",fileName));
            }

        } catch (IOException e) {
            System.out.println("Error writing results to file");
            e.printStackTrace();
        }
    }

    private static void printf(@Nullable PrintWriter master, @PrintFormat String format, Object... args) {
        System.out.printf(format, args);
        if (master != null) {
            master.printf(format, args);
        }
    }

    private static boolean runScreeningTests(PrintWriter writer, Configuration configuration,
            List<TestConfiguration> updatedConfigurations) {
        TestCaseCreatorScreening testCaseCreator = new TestCaseCreatorScreening(configuration.configurations(), updatedConfigurations);

        System.out.printf("Running %d screening tests:%n", testCaseCreator.getTotalCases());

        TestProgress progress = new ScreeningTestProgress(configuration);

        ExecutionFramework executionFramework = new ThreadPoolFramework(configuration, testCaseCreator, progress);
        executionFramework.process(writer);
        progress.printFinalMessage(writer);

        if (progress.getSuccessCount() < testCaseCreator.getTotalCases()) {
            int failures = testCaseCreator.getTotalCases() - progress.getSuccessCount();
            System.out.printf("%n%d out of %d screening tests failed.%n", failures, testCaseCreator.getTotalCases());
            return false;
        } else {
            System.out.printf("%nAll screening tests successful.%n");
            return true;
        }
    }
}
