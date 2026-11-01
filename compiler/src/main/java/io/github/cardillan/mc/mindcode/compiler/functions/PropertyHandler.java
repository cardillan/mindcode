package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface PropertyHandler extends SampleGenerator {
    LogicValue handleProperty(AstFunctionCall node, ValueStore target, List<FunctionArgument> arguments);
}
