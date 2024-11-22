package info.teksol.mindcode.exttest.threadpool;

import info.teksol.mindcode.exttest.ExecutionFramework;
import info.teksol.mindcode.exttest.TestConfiguration;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ThreadPoolFramework implements ExecutionFramework {
    private final ExecutorService executorService;
    private final TestProgress progress;
    private final TestCaseExecutor caseExecutor;

    private final List<Callable<Integer>> runners;
    private List<Future<Integer>> futures;

    public ThreadPoolFramework(TestConfiguration configuration, TestProgress progress) {
        this.executorService = Executors.newFixedThreadPool(configuration.getParallelism());
        this.progress = progress;
        this.caseExecutor = new TestCaseExecutor(configuration, progress);

        this.runners = IntStream.range(0, configuration.getParallelism())
                .mapToObj(i -> (Callable<Integer>) new ThreadPoolRunner(progress, configuration.getTestCaseSelector(), caseExecutor))
                .toList();
    }

    public void process(PrintWriter writer) {
        try {
            futures = executorService.invokeAll(runners, 5, TimeUnit.SECONDS);
            executorService.shutdown();
            do {
                progress.printProgress();
                progress.processResults(writer);
            } while (!executorService.awaitTermination(5, TimeUnit.SECONDS));

            progress.printProgress();
        } catch (InterruptedException ignored) {
        } finally {
            futures.forEach(future -> future.cancel(true));
            executorService.shutdownNow();
            progress.processResults(writer);
        }
    }
}
