package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.compiler.instructions.MlogInstruction;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface FunctionMapper {

    @Nullable
    ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);

    @Nullable
    ValueStore handleProperty(AstFunctionCall astNode, ValueStore target, List<FunctionArgument> arguments);

    @Nullable String decompile(MlogInstruction instruction);

    List<FunctionSample> generateSamples();

    record FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                          ProcessorEdition edition, String note) {
    }
}
