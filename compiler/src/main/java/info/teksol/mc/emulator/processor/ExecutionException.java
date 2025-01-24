package info.teksol.mc.emulator.processor;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ExecutionException extends RuntimeException {
    private final ExecutionFlag flag;

    private int instructionIndex = -1;
    private @Nullable LogicInstruction instruction;

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

    public void setInstruction(@Nullable LogicInstruction instruction) {
        this.instruction = instruction;
    }

    private String formatInstruction() {
        return instruction == null ? "[instruction not available]" : instruction.toMlog();
    }

    @Override
    public String getMessage() {
        if (instructionIndex >= 0) {
            return "Execution exception at instruction %d: %s:\n%s\n(Use the '#set %s = false;' directive or the '--%s false' command line option to ignore this exception.)"
                    .formatted(instructionIndex, formatInstruction(), super.getMessage(), flag.getOptionName(), flag.getOptionName());
        } else {
            return super.getMessage();
        }
    }

    public String getWebAppMessage() {
        if (instructionIndex >= 0) {
            return "Execution exception at instruction %d: %s:\n%s\n(Use the '#set %s = false;' directive to ignore this exception.)"
                    .formatted(instructionIndex, formatInstruction(), super.getMessage(), flag.getOptionName());
        } else {
            return super.getMessage();
        }
    }
}
