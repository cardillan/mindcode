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

    default ArrayOrganization getArrayOrganization() {
        return (ArrayOrganization) getInfo(InstructionInfo.ARRAY_ORGANIZATION);
    }

    default ArrayAccessInstruction setArrayOrganization(ArrayOrganization arrayOrganization) {
        return (ArrayAccessInstruction) setInfo(InstructionInfo.ARRAY_ORGANIZATION, arrayOrganization);
    }

    default ArrayConstruction getArrayConstruction() {
        return (ArrayConstruction) getInfo(InstructionInfo.ARRAY_CONSTRUCTION);
    }

    default ArrayAccessInstruction setArrayConstruction(ArrayConstruction arrayConstruction) {
        return (ArrayAccessInstruction) setInfo(InstructionInfo.ARRAY_CONSTRUCTION, arrayConstruction);
    }

    default boolean isCompactAccessSource() {
        return (boolean) getInfo(InstructionInfo.COMPACT_ACCESS_SOURCE);
    }

    default ArrayAccessInstruction setCompactAccessSource() {
        return (ArrayAccessInstruction) setInfo(InstructionInfo.COMPACT_ACCESS_SOURCE, true);
    }

    default boolean isCompactAccessTarget() {
        return (boolean) getInfo(InstructionInfo.COMPACT_ACCESS_TARGET);
    }

    default ArrayAccessInstruction setCompactAccessTarget() {
        return (ArrayAccessInstruction) setInfo(InstructionInfo.COMPACT_ACCESS_TARGET, true);
    }

    default LogicInstruction resetCompactAccess() {
        return resetInfo(InstructionInfo.COMPACT_ACCESS_SOURCE).resetInfo(InstructionInfo.COMPACT_ACCESS_TARGET);
    }
}
