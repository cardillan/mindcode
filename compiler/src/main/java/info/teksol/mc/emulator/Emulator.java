package info.teksol.mc.emulator;

import java.util.List;

public interface Emulator {

    // Running

    void run(int stepLimit);

    // Providing outputs

    boolean isError();

    int executionSteps();
    int noopSteps();

    List<Assertion> getAllAssertions();

    int getExecutorCount();
    ExecutorResults getExecutorResults(int executor);
}
