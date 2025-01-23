package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class SetAddressInstruction extends BaseInstruction {

    SetAddressInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
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
