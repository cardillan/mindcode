package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor;
import info.teksol.mc.mindcode.logic.arguments.arrays.ArrayConstructor.AccessType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ArrayAccessInstruction extends LogicInstruction {

    default LogicArray getArray() {
        return (LogicArray) getArg(1);
    }

    default LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }

    ArrayConstructor getArrayConstructor();

    AccessType getAccessType();

    default String getJumpTableId() {
        return getArrayConstructor().getJumpTableId(getAccessType());
    }
}
