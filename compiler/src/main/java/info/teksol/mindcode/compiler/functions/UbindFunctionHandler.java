package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

import java.util.List;
import java.util.function.Consumer;

class UbindFunctionHandler extends AbstractFunctionHandler {
    private final BaseFunctionMapper functionMapper;

    UbindFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1);
        this.functionMapper = functionMapper;
    }

    @Override
    public LogicValue handleFunction(FunctionCall call, Consumer<LogicInstruction> program, List<LogicFunctionArgument> arguments) {
        checkArguments(call, arguments);
        program.accept(functionMapper.createInstruction(Opcode.UBIND, arguments.get(0).value()));
        return LogicBuiltIn.UNIT;
    }

    @Override
    public String generateSampleCall() {
        return "unit = " + super.generateSampleCall();
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments, boolean markOptional) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
