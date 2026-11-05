package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mindcode.compiler.generation.CodeAssembler;
import io.github.cardillan.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.logic.arguments.LogicVoid;
import io.github.cardillan.mindcode.logic.opcodes.NamedParameter;
import io.github.cardillan.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mindcode.messages.ERR;
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
        if (arguments.isEmpty()) {
            error(call, ERR.FUNCTION_CALL_NOT_ENOUGH_ARGS, call.getFunctionName(), 1, 0);
            return LogicVoid.VOID;
        }

        CodeAssembler assembler = assembler();
        arguments.forEach(arg -> assembler.createInstruction(opcodeVariant.opcode(),
                validateInput(opcodeVariant.namedParameters().getFirst(), arg).getValue(assembler)));
        return arguments.getLast();
    }

    @Override
    protected String generateCall(List<NamedParameter> arguments, boolean useKeywordPrefixes) {
        return getName() + "(" + BaseFunctionMapper.joinNamedArguments(arguments) + ")";
    }
}
