package info.teksol.mindcode.exttest.forkjoin;

import info.teksol.mindcode.exttest.ExecutionFramework;
import info.teksol.mindcode.exttest.TestConfiguration;
import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import org.jspecify.annotations.NullMarked;

import java.io.PrintWriter;
import java.util.concurrent.*;

@NullMarked
public class ForkJoinFramework implements ExecutionFramework {
    private final ForkJoinPool forkJoinPool;
    private final TestConfiguration configuration;
    private final TestProgress progress;

    public ForkJoinFramework(TestConfiguration configuration, TestProgress progress) {
        this.forkJoinPool = new ForkJoinPool(configuration.getThreads());
        this.configuration = configuration;
        this.progress = progress;
    }

    @Override
    public void process(PrintWriter writer) {
        ForkJoinTask<Integer> task = null;
        try {
            TestCaseCreator caseCreator = configuration.getTestCaseCreator();
            task = forkJoinPool.submit(new ForkJoinTestRunner(progress, caseCreator,
                    0, caseCreator.getTotalCases() - 1));
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
            if (task != null) {
                task.cancel(true);
            }
            progress.processResults(writer);
            progress.printProgress(true);
        }
    }
}
