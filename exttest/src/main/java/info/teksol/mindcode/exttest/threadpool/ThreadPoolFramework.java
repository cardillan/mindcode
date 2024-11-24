package info.teksol.mindcode.exttest.threadpool;

import info.teksol.mindcode.exttest.Configuration;
import info.teksol.mindcode.exttest.ExecutionFramework;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ThreadPoolFramework implements ExecutionFramework {
    private final ExecutorService executorService;
    private final Configuration configuration;
    private final TestCaseCreator caseCreator;
    private final TestProgress progress;
    private final TestCaseExecutor caseExecutor;

    private final List<Callable<Integer>> runners;
    private List<Future<Integer>> futures;

    public ThreadPoolFramework(Configuration configuration, TestCaseCreator caseCreator, TestProgress progress) {
        this.executorService = Executors.newFixedThreadPool(configuration.threads());
        this.configuration = configuration;
        this.caseCreator = caseCreator;
        this.progress = progress;
        this.caseExecutor = new TestCaseExecutor(progress);

        this.runners = IntStream.range(0, configuration.threads())
                .mapToObj(this::createRunner)
                .toList();
    }

    private Callable<Integer> createRunner(int number) {
        return new ThreadPoolRunner(progress, caseCreator, caseExecutor);
    }

    public void process(PrintWriter writer) {
        try {
            futures = executorService.invokeAll(runners, 5, TimeUnit.SECONDS);
            executorService.shutdown();
            do {
                progress.processResults(writer);
                progress.printProgress(false);
            } while (!executorService.awaitTermination(5, TimeUnit.SECONDS));

        } catch (InterruptedException ignored) {
        } finally {
            futures.forEach(future -> future.cancel(true));
            executorService.shutdownNow();
            progress.processResults(writer);
            progress.printProgress(true);
        }
    }
}
