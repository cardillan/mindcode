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
public class SetAddressInstruction extends BaseResultInstruction {

    SetAddressInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SETADDR, args, params);
    }

    protected SetAddressInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public SetAddressInstruction copy() {
        return new SetAddressInstruction(this, astContext, sideEffects);
    }

    @Override
    public SetAddressInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SetAddressInstruction(this, astContext, sideEffects);
    }

    @Override
    public SetAddressInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new SetAddressInstruction(this, astContext, sideEffects);
    }

    public final LogicVariable getResult() {
        return (LogicVariable) getArg(0);
    }

    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(1);
    }
}
