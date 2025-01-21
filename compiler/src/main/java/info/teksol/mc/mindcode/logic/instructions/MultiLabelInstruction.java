package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class MultiLabelInstruction extends BaseInstruction implements LabeledInstruction {

    MultiLabelInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.MULTILABEL, args, params);
    }

    protected MultiLabelInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public MultiLabelInstruction copy() {
        return new MultiLabelInstruction(this, astContext);
    }

    @Override
    public MultiLabelInstruction withContext(AstContext astContext) {
        return new MultiLabelInstruction(this, astContext);
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
