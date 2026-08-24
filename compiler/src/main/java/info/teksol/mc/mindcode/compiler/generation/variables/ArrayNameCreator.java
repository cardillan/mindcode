package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface ArrayNameCreator {

    String arrayBase(@Nullable MindcodeFunction function, String processorName, String arrayName, int variableIndex);
    String arrayElement(@Nullable MindcodeFunction function, String arrayName, int variableIndex, int elementIndex);
    String remoteArrayElement(String arrayName, int index);

    default LogicKeyword arrayLookupType() {
        return LogicKeyword.INVALID;
    }
}
