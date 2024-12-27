package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
interface FunctionHandler extends SampleGenerator {
    ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);
}
