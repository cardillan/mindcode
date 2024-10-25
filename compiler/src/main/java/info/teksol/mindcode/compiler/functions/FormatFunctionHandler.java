package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.function.Consumer;

// Handles the format function
class FormatFunctionHandler extends AbstractFunctionHandler {
    private final BaseFunctionMapper functionMapper;

    FormatFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1);
        this.functionMapper = functionMapper;
    }

    @Override
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicFunctionArgument> arguments) {
        arguments.forEach(arg -> program.accept(functionMapper.createInstruction(Opcode.FORMAT, arg.value())));
        return arguments.get(arguments.size() - 1).value();
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments, boolean markOptional) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
