package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.logic.arguments.LogicArray;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface ArrayAccessInstruction extends LogicInstruction{

    default LogicArray getArray() {
        return (LogicArray) getArg(1);
    }

    default LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }

    String getJumpTableId();

    @Override
    default int getRealSize(@Nullable Map<String, Integer> sharedStructures) {
        if (sharedStructures != null) {
            LogicArray array = getArray();
            int size = array.getSize() * 2;
            String key = array.getArrayName() + getJumpTableId();
            if (!sharedStructures.containsKey(key) || sharedStructures.get(key) < size) {
                sharedStructures.put(key, size);
            }
        }

        int checkSize = MindcodeCompiler.getContext().compilerProfile().getBoundaryChecks().getSize();
        return getOpcode().getSize() + checkSize;
    }
}
