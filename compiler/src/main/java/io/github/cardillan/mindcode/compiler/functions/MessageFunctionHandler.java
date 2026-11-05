package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.generation.variables.FunctionArgument;
import io.github.cardillan.mindcode.compiler.generation.variables.InputFunctionArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicBuiltIn;
import io.github.cardillan.mindcode.logic.opcodes.NamedParameter;
import io.github.cardillan.mindcode.logic.opcodes.OpcodeVariant;
import io.github.cardillan.mindcode.messages.ERR;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageFunctionHandler extends StandardFunctionHandler {

    public MessageFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 2, 3, false);
    }

    @Override
    protected FunctionArgument validateOutput(NamedParameter parameter, FunctionArgument argument) {
        if (parameter.type().isOutput() && argument.unwrap() instanceof LogicBuiltIn builtIn && LogicBuiltIn.WAIT.equals(builtIn)) {
            if (argument.hasInModifier()) {
                error(argument, ERR.ARGUMENT_IN_MODIFIER_NOT_ALLOWED, parameter.name());
            }
            if (argument.hasOutModifier()) {
                error(argument, ERR.ARGUMENT_WAIT_OUT_NOT_ALLOWED, builtIn.toMlog());
            }
            return argument instanceof InputFunctionArgument a ? a.toOutputFunctionArgument() : argument;
        } else {
            return super.validateOutput(parameter, argument);
        }
    }
}
