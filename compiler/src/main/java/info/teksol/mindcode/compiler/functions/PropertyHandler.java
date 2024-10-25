package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicFunctionArgument;
import info.teksol.mindcode.logic.LogicValue;

import java.util.List;
import java.util.function.Consumer;

interface PropertyHandler extends SampleGenerator {
    LogicValue handleProperty(AstNode node, Consumer<LogicInstruction> program, LogicValue target, List<LogicFunctionArgument> arguments);
}
