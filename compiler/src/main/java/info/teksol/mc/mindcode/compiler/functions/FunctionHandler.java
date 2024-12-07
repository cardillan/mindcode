package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface FunctionHandler extends SampleGenerator {
    ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);
}
