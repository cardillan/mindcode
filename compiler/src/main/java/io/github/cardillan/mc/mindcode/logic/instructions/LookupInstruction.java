package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicKeyword;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class LookupInstruction extends BaseResultInstruction {

    LookupInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.LOOKUP, args, params);
    }

    protected LookupInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public LookupInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new LookupInstruction(this, astContext);
    }

    @Override
    public LookupInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new LookupInstruction(astContext, List.of(getType(), result, getIndex()), getArgumentTypes()).copyInfo(this);
    }

    public final LogicKeyword getType() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getIndex() {
        return (LogicValue) getArg(2);
    }

    public LookupInstruction withIndex(LogicValue index) {
        assert getArgumentTypes() != null;
        return new LookupInstruction(astContext, List.of(getType(), getResult(), index), getArgumentTypes()).copyInfo(this);
    }
}
