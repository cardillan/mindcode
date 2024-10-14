package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.LogicVariable;

public interface LogicResultInstruction extends LogicInstruction {

    LogicVariable getResult();

    LogicResultInstruction withResult(LogicVariable result);
}
