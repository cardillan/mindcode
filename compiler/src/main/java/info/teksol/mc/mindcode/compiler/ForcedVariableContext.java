package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.logic.arguments.LogicVariable;

import java.util.Set;

public interface ForcedVariableContext extends CompilerContext {
    void addForcedVariable(LogicVariable variable);
    Set<LogicVariable> getForcedVariables();
}
