package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicValue;

import java.util.List;
import java.util.function.Consumer;

interface FunctionHandler extends SampleGenerator {
    LogicValue handleFunction(AstNode node, Consumer<LogicInstruction> program, List<LogicValue> arguments);
}
