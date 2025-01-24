package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.InputFunctionArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.opcodes.NamedParameter;
import info.teksol.mc.mindcode.logic.opcodes.OpcodeVariant;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageFunctionHandler extends StandardFunctionHandler {

    public MessageFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 2, 3, false);
    }

    @Override
    protected FunctionArgument validateOutput(NamedParameter parameter, FunctionArgument argument) {
        if (parameter.type().isOutput() && argument.getArgumentValue() instanceof LogicBuiltIn builtIn && LogicBuiltIn.WAIT.equals(builtIn)) {
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
