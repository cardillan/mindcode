package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.compiler.callgraph.MindcodeFunction;
import io.github.cardillan.mindcode.logic.arguments.LogicKeyword;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ArrayNameCreator {

    String arrayBase(@Nullable MindcodeFunction function, String processorName, String arrayName, int variableIndex);
    String arrayElement(@Nullable MindcodeFunction function, String arrayName, int variableIndex, int elementIndex);
    String remoteArrayElement(String arrayName, int index);
    String arrayOffset(MindcodeFunction function, String arrayName, int variableIndex);


    default LogicKeyword arrayLookupType() {
        return LogicKeyword.INVALID;
    }
}
