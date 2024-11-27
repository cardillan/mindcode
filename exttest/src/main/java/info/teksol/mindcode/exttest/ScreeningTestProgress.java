package info.teksol.mindcode.exttest;

import java.io.PrintWriter;

public class ScreeningTestProgress extends AbstractTestProgress {
    private final int tests;

    public ScreeningTestProgress(Configuration configuration) {
        tests = configuration.configurations().size();
    }

    @Override
    public void reportSuccess() {
        super.reportSuccess();
        System.out.print(".");
    }

    @Override
    public void reportError(ErrorResult errorResult) {
        super.reportError(errorResult);
        System.out.print(".");
    }

    @Override
    public boolean finished() {
        return finishedCount.get() >= tests;
    }

    @Override
    public void updateStatistics(ErrorResult errorResult) {
    }

    @Override
    public void printProgress(boolean finished) {
    }

    @Override
    public void printFinalMessage(PrintWriter writer) {
    }

    @Override
    public void printStatistics(PrintWriter writer) {
    }
}
