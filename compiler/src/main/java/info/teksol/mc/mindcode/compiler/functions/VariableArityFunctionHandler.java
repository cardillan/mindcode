package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Handles multiple arguments to the function, creating one instruction per passed argument.
@NullMarked
class VariableArityFunctionHandler extends AbstractHandler implements FunctionHandler {

    VariableArityFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1, false);
    }

    @Override
    public ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments) {
        CodeAssembler assembler = assembler();
        arguments.forEach(arg -> assembler.createInstruction(opcodeVariant.opcode(),
                validateInput(opcodeVariant.namedParameters().getFirst(), arg).getValue(assembler)));
        return arguments.getLast();
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}