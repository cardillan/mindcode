package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicFunctionArgument;
import info.teksol.mindcode.logic.LogicValue;

import java.util.List;
import java.util.function.Consumer;

interface FunctionHandler extends SampleGenerator {
    LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicFunctionArgument> arguments);
}
