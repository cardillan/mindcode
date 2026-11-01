package io.github.cardillan.mc.mindcode.compiler;

import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;

import java.util.Set;

public interface ForcedVariableContext extends CompilerContext {
    void addForcedVariable(LogicVariable variable);
    Set<LogicVariable> getForcedVariables();
}
