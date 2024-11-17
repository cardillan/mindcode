package info.teksol.emulator.processor;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import org.intellij.lang.annotations.PrintFormat;

public class ExecutionException extends RuntimeException {
    private final ExecutionFlag flag;

    private int instructionIndex = -1;
    private LogicInstruction instruction;

    public ExecutionException(ExecutionFlag flag, String message) {
        super(message);
        this.flag = flag;
    }

    public ExecutionException(ExecutionFlag flag, @PrintFormat String message, Object... args) {
        super(String.format(message, args));
        this.flag = flag;
    }

    public ExecutionFlag getFlag() {
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
            return "Execution exception at instruction %d: %s:\n%s\n(Use '#set %s = false;' directive or '--%s false' command line option to ignore this exception.)"
                    .formatted(instructionIndex, instruction.toMlog(), super.getMessage(), flag.getOptionName(), flag.getOptionName());
        } else {
            return super.getMessage();
        }
    }
}
