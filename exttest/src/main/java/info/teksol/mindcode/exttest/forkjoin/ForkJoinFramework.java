package info.teksol.mindcode.exttest.forkjoin;

import info.teksol.mindcode.exttest.Configuration;
import info.teksol.mindcode.exttest.ExecutionFramework;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;

import java.io.PrintWriter;
import java.util.concurrent.*;

public class ForkJoinFramework implements ExecutionFramework {
    private final ForkJoinPool forkJoinPool;
    private final Configuration.TestConfiguration configuration;
    private final TestProgress progress;
    private final TestCaseExecutor caseExecutor;

    ForkJoinTask<Integer> task;

    public ForkJoinFramework(Configuration.TestConfiguration configuration, TestProgress progress) {
        this.forkJoinPool = new ForkJoinPool(configuration.global().threads());
        this.configuration = configuration;
        this.progress = progress;
        this.caseExecutor = new TestCaseExecutor(configuration, progress);
    }

    @Override
    public void process(PrintWriter writer) {
        try {
            task = forkJoinPool.submit(new ForkJoinTestRunner(progress, configuration.getTestCaseSelector(), caseExecutor,
                    0, configuration.getSampleCount() - 1));
            Thread.sleep(5_000);

            while (!progress.finished()) {
                try {
                    progress.processResults(writer);
                    progress.printProgress(false);

                    task.get(5, TimeUnit.SECONDS);
                    break;
                } catch (TimeoutException ignored) {
                }
            }
        } catch (ExecutionException e) {
            System.out.println("Error executing tests.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Test execution interrupted.");
            e.printStackTrace();
        } finally {
            task.cancel(true);
            progress.processResults(writer);
            progress.printProgress(true);
        }
    }
}
