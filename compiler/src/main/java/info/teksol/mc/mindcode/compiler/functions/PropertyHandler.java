package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface PropertyHandler extends SampleGenerator {
    LogicValue handleProperty(AstFunctionCall node, ValueStore target, List<FunctionArgument> arguments);
}
