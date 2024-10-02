package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.InstructionParameterType;
import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public class DrawflushInstruction extends BaseInstruction {

    DrawflushInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.DRAWFLUSH, args, params);
    }

    protected DrawflushInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public DrawflushInstruction copy() {
        return new DrawflushInstruction(this, astContext);
    }

    @Override
    public DrawflushInstruction withContext(AstContext astContext) {
        return new DrawflushInstruction(this, astContext);
    }

    public final LogicVariable getDisplay() {
        return (LogicVariable) getArg(0);
    }
}
