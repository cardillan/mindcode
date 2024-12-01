package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicFunctionArgument;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.List;
import java.util.function.Consumer;

/**
 * Handles multiple arguments to the function, creating one instruction per passed argument.
 */
class VariableArityFunctionHandler extends AbstractFunctionHandler {
    private final BaseFunctionMapper functionMapper;

    VariableArityFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1);
        this.functionMapper = functionMapper;
    }

    @Override
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicFunctionArgument> arguments) {
        arguments.forEach(arg -> program.accept(functionMapper.createInstruction(opcodeVariant.opcode(), arg.value())));
        return arguments.getLast().value();
    }

    @Override
    protected boolean validateArguments(FunctionCall call, List<LogicFunctionArgument> arguments) {
        return super.validateArguments(call, arguments) && validateStandardArgumentModifiers(arguments);
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
