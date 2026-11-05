package io.github.cardillan.mindcode.logic.instructions;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mindcode.logic.arguments.LogicBuiltIn;
import io.github.cardillan.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class SetInstruction extends BaseResultInstruction {

    SetInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.SET, args, params);
    }

    protected SetInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public boolean endsCodePath() {
        return getResult() == LogicBuiltIn.COUNTER;
    }

    @Override
    public SetInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new SetInstruction(this, astContext);
    }

    @Override
    public SetInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new SetInstruction(astContext, List.of(result, getValue()), getArgumentTypes()).copyInfo(this);
    }

    public SetInstruction withValue(LogicValue value) {
        assert getArgumentTypes() != null;
        return new SetInstruction(astContext, List.of(getResult(), value), getArgumentTypes()).copyInfo(this);
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
