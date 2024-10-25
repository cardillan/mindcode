package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.logic.LogicFunctionArgument;
import info.teksol.mindcode.logic.OpcodeVariant;

import java.util.List;

abstract class AbstractPropertyHandler extends AbstractHandler implements PropertyHandler {
    protected final String name;
    protected final int numArgs;

    AbstractPropertyHandler(BaseFunctionMapper functionMapper, String name, OpcodeVariant opcodeVariant, int numArgs) {
        super(functionMapper, opcodeVariant);
        this.name = name;
        this.numArgs = numArgs;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OpcodeVariant getOpcodeVariant() {
        return opcodeVariant;
    }

    protected boolean checkArguments(AstNode node, List<LogicFunctionArgument> arguments) {
        if (arguments.size() != numArgs) {
            error(node, "Function '%s': wrong number of arguments (expected %d, found %d)", name, numArgs, arguments.size());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + "opcodeVariant=" + opcodeVariant + ", name=" + name + ", numArgs=" + numArgs + '}';
    }
}
