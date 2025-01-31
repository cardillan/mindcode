package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NullMarked
public class DrawflushInstruction extends BaseInstruction {

    DrawflushInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAWFLUSH, args, params);
    }

    protected DrawflushInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public DrawflushInstruction copy() {
        return new DrawflushInstruction(this, astContext, sideEffects);
    }

    @Override
    public DrawflushInstruction withContext(AstContext astContext) {
        return Objects.equals(this.astContext, astContext) ? this : new DrawflushInstruction(this, astContext, sideEffects);
    }

    @Override
    public DrawflushInstruction withSideEffects(SideEffects sideEffects) {
        return Objects.equals(this.sideEffects, sideEffects) ? this : new DrawflushInstruction(this, astContext, sideEffects);
    }


    public final LogicValue getDisplay() {
        return (LogicValue) getArg(0);
    }
}
