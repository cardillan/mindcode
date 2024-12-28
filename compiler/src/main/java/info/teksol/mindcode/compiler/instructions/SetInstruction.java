package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.logic.*;

import java.util.List;

public class SetInstruction extends BaseResultInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, List<InstructionParameterType> params) {
        super(astContext, Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public SetInstruction copy() {
        return new SetInstruction(this, astContext);
    }

    @Override
    public SetInstruction withContext(AstContext astContext) {
        return new SetInstruction(this, astContext);
    }

    @Override
    public SetInstruction withResult(LogicVariable result) {
        return new SetInstruction(astContext, List.of(result, getValue()), getArgumentTypes());
    }

    public SetInstruction withValue(LogicValue value) {
        return new SetInstruction(astContext, List.of(getResult(), value), getArgumentTypes());
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(1);
    }

    @Override
    public boolean isUpdating() {
        // SET instruction is not self-modifying, even when it assigns the same value to self.
        return false;
    }
}
