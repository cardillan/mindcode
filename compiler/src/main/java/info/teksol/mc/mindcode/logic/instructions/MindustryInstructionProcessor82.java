package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.util.UtfUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MindustryInstructionProcessor82 extends MindustryInstructionProcessor8 {

    MindustryInstructionProcessor82(InstructionProcessorParameters parameters) {
        super(parameters);
    }

    @Override
    public boolean canEncode(int character) {
        return UtfUtils.canEncode(character);
    }

    @Override
    public String toMlog(LogicArgument argument) {
        return argument.toMlog();
    }
}
