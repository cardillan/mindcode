package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.logic.*;

public class MessageFunctionHandler extends StandardFunctionHandler {

    public MessageFunctionHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant) {
        super(functionMapper, name, opcodeVariant, 2, 3, false);
    }

    protected LogicValue requireOutModifier(NamedParameter parameter, LogicFunctionArgument argument) {
        if (parameter.type().isOutput() && argument.value() instanceof LogicBuiltIn builtIn && "@wait".equals(builtIn.getName())) {
            if (argument.inModifier()) {
                error(argument.pos(), "Parameter '%s' isn't input, 'in' modifier not allowed.", parameter.name());
            }
            if (argument.outModifier()) {
                error(argument.pos(), "'out' modifier not allowed with special value '%s'.", builtIn.toMlog());
            }
            return argument.value();
        } else {
            return super.requireOutModifier(parameter, argument);
        }
    }
}
