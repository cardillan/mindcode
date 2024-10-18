package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.LogicValue;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.List;
import java.util.function.Consumer;

// Handles the print function
class PrintFunctionHandler extends AbstractFunctionHandler {
    private final BaseFunctionMapper functionMapper;

    PrintFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 1);
        this.functionMapper = functionMapper;
    }

    @Override
    public LogicValue handleFunction(AstNode node, Consumer<LogicInstruction> program, List<LogicValue> arguments) {
        arguments.forEach(arg -> program.accept(functionMapper.createInstruction(Opcode.PRINT, arg)));
        return arguments.get(arguments.size() - 1);
    }

    @Override
    public String generateCall(List<NamedParameter> arguments) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
