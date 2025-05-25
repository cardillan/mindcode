package info.teksol.mindcode.exttest.forkjoin;

import info.teksol.mindcode.exttest.TestProgress;
import info.teksol.mindcode.exttest.cases.TestCaseCreator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.RecursiveTask;

@NullMarked
public class ForkJoinTestRunner extends RecursiveTask<Integer> {
    private final TestProgress progress;
    private final TestCaseCreator caseCreator;
    private final int start;
    private final int end;

    public ForkJoinTestRunner(TestProgress progress, TestCaseCreator caseCreator, int start, int end) {
        this.progress = progress;
        this.caseCreator = caseCreator;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (start > end) {
            return 0;
        } else if (start == end) {
            TestCaseExecutor executor = caseCreator.createExecutor(start);
            executor.runTest(progress);
            return 1;
        } else {
            int middle = (start + end) / 2;
            ForkJoinTestRunner fork1 = new ForkJoinTestRunner(progress, caseCreator, start, middle);
            ForkJoinTestRunner fork2 = new ForkJoinTestRunner(progress, caseCreator, middle + 1, end);

            fork2.fork();
            return fork1.compute() + fork2.join();
        }
    }
}
