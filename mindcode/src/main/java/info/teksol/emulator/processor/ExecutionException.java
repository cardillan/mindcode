package info.teksol.emulator.processor;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.intellij.lang.annotations.PrintFormat;

public class ExecutionException extends RuntimeException {
    private final ProcessorFlag flag;

    private int instructionIndex = -1;
    private LogicInstruction instruction;

    public ExecutionException(ProcessorFlag flag, String message) {
        super(message);
        this.flag = flag;
    }

    public ExecutionException(ProcessorFlag flag, @PrintFormat String message, Object... args) {
        this(flag, String.format(message, args));
    }

    public ProcessorFlag getFlag() {
        return flag;
    }

    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }

    public void setInstruction(LogicInstruction instruction) {
        this.instruction = instruction;
    }

    @Override
    public String getMessage() {
        if (instructionIndex >= 0) {
            return "Execution exception at instruction %d: %s:\n%s".formatted(instructionIndex, instruction.toMlog(), super.getMessage());
        } else {
            return super.getMessage();
        }
    }
}
