package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface PropertyHandler extends SampleGenerator {
    LogicValue handleProperty(AstFunctionCall node, ValueStore target, List<FunctionArgument> arguments);
}
