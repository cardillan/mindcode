package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class MultiLabelInstruction extends BaseInstruction implements LabeledInstruction {

    MultiLabelInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTILABEL, args, params);
    }

    protected MultiLabelInstruction(BaseInstruction other, AstContext astContext, SideEffects sideEffects) {
        super(other, astContext, sideEffects);
    }

    @Override
    public MultiLabelInstruction copy() {
        return new MultiLabelInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiLabelInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new MultiLabelInstruction(this, astContext, sideEffects);
    }

    @Override
    public MultiLabelInstruction withSideEffects(SideEffects sideEffects) {
        return this.sideEffects == sideEffects ? this : new MultiLabelInstruction(this, astContext, sideEffects);
    }

    public boolean matches(LogicInstruction instruction) {
        return getMarker().equals(instruction.getMarker());
    }

    @Override
    public final LogicLabel getLabel() {
        return (LogicLabel) getArg(0);
    }

    public final LogicLabel getMarker() {
        return (LogicLabel) getArg(1);
    }
}
