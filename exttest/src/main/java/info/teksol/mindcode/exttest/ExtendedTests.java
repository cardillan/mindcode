package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;
import info.teksol.mindcode.exttest.threadpool.ThreadPoolFramework;

import java.io.IOException;
import java.io.PrintWriter;
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

        List<Configuration.TestConfiguration> testConfigurations = configuration.configurations();

        int test = 1;
        for (Configuration.TestConfiguration testConfiguration : testConfigurations) {
            System.out.println();
            System.out.printf("TEST %d/%d%n", test++, testConfigurations.size());
            System.out.printf("Source: %s (%s), parallel tests: %d%n", testConfiguration.getSourceFileName(),
                    testConfiguration.isRun() ? "compile and run" : "compile only",
                    configuration.threads());
            TestCaseSelector testCaseSelector = testConfiguration.getTestCaseSelector();
            System.out.printf("Sampling %,d configurations out of %,d total configurations (coverage %.3g%%).%n",
                    testCaseSelector.getSampleCount(), testCaseSelector.getTotalCases(),
                    100d * testCaseSelector.getSampleCount() / testCaseSelector.getTotalCases());

            TestProgress progress = new TestProgress(testConfiguration);
            TestCaseExecutor testCaseExecutor = new TestCaseExecutor(testConfiguration, progress);

            try (PrintWriter writer = new PrintWriter(testConfiguration.getResultPath().toFile())) {
                ExecutionFramework executionFramework = new ThreadPoolFramework(testConfiguration, progress);
                executionFramework.process(writer);
                progress.printFinalMessage(writer);
            } catch (IOException e) {
                System.out.println("Error writing results to file");
                e.printStackTrace();
            }
        }
    }
}
