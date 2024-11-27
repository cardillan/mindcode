package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.exttest.Configuration.SingleTestConfiguration;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseCreatorScreening;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.forkjoin.ForkJoinFramework;
import info.teksol.mindcode.exttest.threadpool.ThreadPoolFramework;
import org.intellij.lang.annotations.PrintFormat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ExtendedTests {

    public static void main(String[] args) throws IOException {
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
            if (!runScreeningTests(master, configuration)) {
                return;
            }

            List<String> failedSources = new ArrayList<>();
            List<SingleTestConfiguration> testConfigurations = configuration.configurations();

            int test = 1;
            for (SingleTestConfiguration testConfiguration : testConfigurations) {
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
                TestCaseExecutor testCaseExecutor = new TestCaseExecutor(progress);

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
                printf(master, "All test succeeded.%n");
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

    private static void printf(PrintWriter master, @PrintFormat String format, Object... args) {
        System.out.printf(format, args);
        if (master != null) {
            master.printf(format, args);
        }
    }

    private static boolean runScreeningTests(PrintWriter writer, Configuration configuration) {
        TestCaseCreatorScreening testCaseCreator = new TestCaseCreatorScreening(configuration.configurations());

        System.out.printf("Running %d screening tests:%n", testCaseCreator.getTotalCases());

        TestProgress progress = new ScreeningTestProgress(configuration);
        TestCaseExecutor testCaseExecutor = new TestCaseExecutor(progress);

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
