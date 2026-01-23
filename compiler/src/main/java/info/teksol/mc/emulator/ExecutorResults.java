package info.teksol.mc.emulator;

import java.util.List;
import java.util.stream.IntStream;

public interface ExecutorResults {
    String getProcessorId();

    List<Assertion> getAssertions();

    String getFormattedOutput();

    List<String> getPrintOutput();

    double getAccumulatorLoss();

    default double getCoverage() {
        return getProfile().length == 0 ? 0 : (double) getCoveredCount() / getProfile().length;
    }

    default int getCoveredCount() {
        return IntStream.of(getProfile()).map(i -> i > 0 ? 1 : 0).sum();
    }

    int[] getProfile();

    List<String> getFormattedProfile();

    int getSteps();

    double getWaitTime();
}
