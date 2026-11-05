package io.github.cardillan.mindcode.compiler;

import io.github.cardillan.mindcode.logic.arguments.LogicVariable;

import java.util.Set;

public interface ForcedVariableContext extends CompilerContext {
    void addForcedVariable(LogicVariable variable);
    Set<LogicVariable> getForcedVariables();
}
