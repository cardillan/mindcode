package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;

import java.util.List;

public class ControlInstruction extends BaseInstruction {

    ControlInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.CONTROL, args, params);
    }

    protected ControlInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public ControlInstruction copy() {
        return new ControlInstruction(this, astContext);
    }

    @Override
    public ControlInstruction withContext(AstContext astContext) {
        return new ControlInstruction(this, astContext);
    }

    public final LogicKeyword getProperty() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getObject() {
        return (LogicValue) getArg(1);
    }

    // Property types

    public final LogicValue getValue() {
        return (LogicValue) getArg(2);
    }
}
