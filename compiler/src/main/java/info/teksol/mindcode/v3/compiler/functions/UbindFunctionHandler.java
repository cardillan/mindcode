package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.logic.LogicBuiltIn;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
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
