package info.teksol.mc.emulator;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;

import java.util.List;

public interface Emulator {

    // Setting up

    void addBlock(String name, MindustryBuilding block);

    // Running

    void run(List<LogicInstruction> program, int stepLimit);

    // Providing outputs

    boolean isError();

    int executionSteps();
    int noopSteps();

    int getExecutorCount();
    int instructionCount(int executor);
    int coveredCount(int executor);
    int[] getProfile(int executor);

    List<Assertion> getAssertions(int executor);

    TextBuffer getTextBuffer(int executor);
}
