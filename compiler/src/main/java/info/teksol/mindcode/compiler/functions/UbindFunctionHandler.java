package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
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
    public LogicValue handleFunction(AstNode node, Consumer<LogicInstruction> program, List<LogicValue> arguments) {
        checkArguments(node, arguments);
        program.accept(functionMapper.createInstruction(Opcode.UBIND, arguments.get(0)));
        return LogicBuiltIn.UNIT;
    }

    @Override
    public String generateSampleCall() {
        return "unit = " + super.generateSampleCall();
    }

    @Override
    public String generateCall(List<NamedParameter> arguments) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
