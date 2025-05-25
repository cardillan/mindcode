package info.teksol.mindcode.exttest;

import org.jspecify.annotations.NullMarked;

import java.io.PrintWriter;

@NullMarked
public interface TestProgress {

    boolean finished();

    int nextTestRun();

    int getSuccessCount();

    int getFailureCount();

    void printFinalMessage(PrintWriter writer);

    void printProgress(boolean finished);

    void printStatistics(PrintWriter writer);

    void processResults(PrintWriter writer);

    void reportError(ErrorResult errorResult);

    void reportSuccess();

    void updateStatistics(ErrorResult errorResult);
}
