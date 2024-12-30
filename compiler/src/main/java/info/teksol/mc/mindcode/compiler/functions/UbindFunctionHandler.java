package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
class UbindFunctionHandler extends AbstractHandler implements FunctionHandler {

    UbindFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1, false);
    }

    @Override
    public ValueStore handleFunction(AstFunctionCall call, List<FunctionArgument> arguments) {
        validateArguments(call, arguments);
        assembler().createInstruction(Opcode.UBIND, validateInput(
                        opcodeVariant.namedParameters().getFirst(),
                        arguments.getFirst()).getValue(assembler()));
        return LogicBuiltIn.UNIT;
    }

    @Override
    public String generateSampleCall() {
        return "unit = " + super.generateSampleCall();
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
