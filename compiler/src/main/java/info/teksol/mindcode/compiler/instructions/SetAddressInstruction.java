package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class SetAddressInstruction extends BaseInstruction {

    SetAddressInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.SETADDR, args, params);
    }

    protected SetAddressInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SetAddressInstruction copy() {
        return new SetAddressInstruction(this, astContext);
    }

    @Override
    public SetAddressInstruction withContext(AstContext astContext) {
        return new SetAddressInstruction(this, astContext);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(1);
    }
}
