package info.teksol.mindcode.v3.compiler.functions;

import info.teksol.mindcode.logic.LogicBuiltIn;
import info.teksol.mindcode.logic.NamedParameter;
import info.teksol.mindcode.logic.OpcodeVariant;
import info.teksol.mindcode.v3.compiler.generation.variables.FunctionArgument;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MessageFunctionHandler extends StandardFunctionHandler {

    public MessageFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 2, 3, false);
    }

    @Override
    protected FunctionArgument validateOutput(NamedParameter parameter, FunctionArgument argument) {
        if (parameter.type().isOutput() && argument.getArgumentValue() instanceof LogicBuiltIn builtIn && "@wait".equals(builtIn.getName())) {
            if (argument.hasInModifier()) {
                error(argument.inputPosition(), "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.name());
            }
            if (argument.hasOutModifier()) {
                error(argument.inputPosition(), "'out' modifier not allowed with special value '%s'.", builtIn.toMlog());
            }
            return argument;
        } else {
            return super.validateOutput(parameter, argument);
        }
    }
}
