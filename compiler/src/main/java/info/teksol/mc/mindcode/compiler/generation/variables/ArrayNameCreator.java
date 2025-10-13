package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;

public interface ArrayNameCreator {

    String arrayBase(String processorName, String arrayName);
    String arrayElement(String arrayName, int index);
    String remoteArrayElement(String arrayName, int index);

    default LogicKeyword arrayLookupType() {
        return LogicKeyword.INVALID;
    }
}
