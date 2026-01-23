package info.teksol.mc.emulator;

import java.util.List;

public interface Emulator {

    // Running

    void run(int stepLimit);

    // Providing outputs

    boolean isError();

    int getExecutionSteps();
    int getNoopSteps();

    List<Assertion> getAllAssertions();

    int getExecutorCount();
    List<? extends ExecutorResults> getExecutorResults();
    ExecutorResults getExecutorResults(int executor);
}
