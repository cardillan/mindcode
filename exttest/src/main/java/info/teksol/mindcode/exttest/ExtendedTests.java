package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.threadpool.ThreadPoolFramework;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ExtendedTests {
    public static void main(String[] args) throws InterruptedException, IOException {
        TestConfiguration configuration = TestConfigurationFile.loadConfiguration("standard.properties");

        System.out.printf("Sampling %,d configurations out of %,d total configurations (coverage %.3g%%).%n",
                configuration.getSampleCount(), configuration.getTotalCases(),
                100d * configuration.getSampleCount() / configuration.getTotalCases());

        if (OptimizationCoordinator.isDebugOn()) {
            System.out.println("Error: debug active");
            return;
        }

        File file = new File("results.txt");

        TestProgress progress = new TestProgress(configuration);
        TestCaseExecutor testCaseExecutor = new TestCaseExecutor(configuration, progress);

        try (PrintWriter writer = new PrintWriter(file)) {
            ExecutionFramework executionFramework = new ThreadPoolFramework(configuration, progress);
            executionFramework.process(writer);
            progress.printFinalMessage(writer);
            progress.printStatistics(writer);
        } catch (IOException e) {
            System.out.println("Error writing results to file");
            e.printStackTrace();
        }
    }
}
