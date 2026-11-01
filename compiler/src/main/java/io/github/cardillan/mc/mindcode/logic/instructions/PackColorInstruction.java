package io.github.cardillan.mc.mindcode.logic.instructions;

import io.github.cardillan.mc.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicArgument;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicValue;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mc.mindcode.logic.opcodes.InstructionParameterType;
import io.github.cardillan.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class PackColorInstruction extends BaseResultInstruction {
    PackColorInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.PACKCOLOR, args, params);
    }

    protected PackColorInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public PackColorInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new PackColorInstruction(this, astContext);
    }

    @Override
    public PackColorInstruction withResult(LogicVariable result) {
        assert getArgumentTypes() != null;
        return new PackColorInstruction(astContext, List.of(result, getR(), getG(), getB(), getA()), getArgumentTypes()).copyInfo(this);
    }

    public final LogicValue getR() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getG() {
        return (LogicValue) getArg(2);
    }

    public final LogicValue getB() {
        return (LogicValue) getArg(3);
    }

    public final LogicValue getA() {
        return (LogicValue) getArg(4);
    }
}
