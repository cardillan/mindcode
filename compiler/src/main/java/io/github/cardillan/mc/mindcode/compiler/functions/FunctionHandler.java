package io.github.cardillan.mc.mindcode.compiler.functions;

import io.github.cardillan.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface FunctionHandler extends SampleGenerator {
    ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);
}
