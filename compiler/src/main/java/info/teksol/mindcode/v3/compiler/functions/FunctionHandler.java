package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface FunctionHandler extends SampleGenerator {
    NodeValue handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);
}
