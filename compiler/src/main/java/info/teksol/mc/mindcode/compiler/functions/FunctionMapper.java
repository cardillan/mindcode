package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.MlogInstruction;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
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
