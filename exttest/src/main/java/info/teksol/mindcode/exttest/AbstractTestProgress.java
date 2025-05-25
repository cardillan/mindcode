package info.teksol.mindcode.exttest;

import org.jspecify.annotations.NullMarked;

import java.io.PrintWriter;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

@NullMarked
public abstract class AbstractTestProgress implements TestProgress {
    protected final AtomicInteger nextTestRun = new AtomicInteger(0);
    protected final AtomicInteger finishedCount = new AtomicInteger(0);
    protected final AtomicInteger errorCount = new AtomicInteger(0);
    protected final Deque<ErrorResult> errors = new ConcurrentLinkedDeque<>();

    @Override
    public int nextTestRun() {
        return nextTestRun.getAndIncrement();
    }

    @Override
    public int getSuccessCount() {
        return finishedCount.get() - errorCount.get();
    }

    @Override
    public int getFailureCount() {
        return errorCount.get();
    }

    @Override
    public void reportError(ErrorResult errorResult) {
        errors.offer(errorResult);
        errorCount.incrementAndGet();
        finishedCount.incrementAndGet();
    }

    @Override
    public void reportSuccess() {
        finishedCount.incrementAndGet();
    }

    @Override
    public void processResults(PrintWriter writer) {
        ErrorResult errorResult;
        while ((errorResult = errors.poll()) != null) {
            writer.println(errorResult);
            updateStatistics(errorResult);
        }
        writer.flush();
    }
}
