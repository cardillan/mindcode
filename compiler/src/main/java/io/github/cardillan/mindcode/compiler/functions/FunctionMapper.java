package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.logic.instructions.LogicInstruction;
import io.github.cardillan.mindcode.logic.instructions.MlogInstruction;
import io.github.cardillan.mindcode.logic.opcodes.ProcessorType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public interface FunctionMapper {

    @Nullable ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments);

    @Nullable ValueStore handleProperty(AstFunctionCall astNode, ValueStore target, List<FunctionArgument> arguments);

    @Nullable String decompile(MlogInstruction instruction);

    List<FunctionSample> generateSamples();

    record FunctionSample(int order, String name, String functionCall, LogicInstruction instruction,
                          ProcessorType type, String note) {
    }
}
