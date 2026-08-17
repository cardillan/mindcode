package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicKeyword;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public class AssertBoundsInstruction extends BaseInstruction {
    AssertBoundsInstruction(AstContext astContext, List<LogicArgument> args, @Nullable List<InstructionParameterType> params) {
        super(astContext, Opcode.ASSERT_BOUNDS, args, params);
    }

    protected AssertBoundsInstruction(BaseInstruction other, AstContext astContext) {
        super(other, astContext);
    }

    @Override
    public AssertBoundsInstruction withContext(AstContext astContext) {
        return this.astContext == astContext ? this : new AssertBoundsInstruction(this, astContext);
    }

    public boolean hasLowerBound() {
        return !isStackOverflowCheck();
    }

    public boolean hasUpperBound() {
        return true;
    }

    public final LogicKeyword getType() {
        return (LogicKeyword) getArg(0);
    }

    public final LogicValue getMultiple() {
        return (LogicValue) getArg(1);
    }

    public final LogicValue getLowerBound() {
        return (LogicValue) getArg(2);
    }

    public final Condition getLowerCondition() {
        return (Condition) getArg(3);
    }

    public final LogicValue getValue() {
        return (LogicValue) getArg(4);
    }

    public final Condition getUpperCondition() {
        return (Condition) getArg(5);
    }

    public final LogicValue getUpperBound() {
        return (LogicValue) getArg(6);
    }

    public final LogicValue getMessage() {
        return (LogicValue) getArg(7);
    }

    @Override
    public int getSharedSize(@Nullable Map<String, Integer> sharedStructures) {
        return switch (getLocalProfile().getErrorReporting()) {
            case NONE -> 0;
            case ASSERT -> 1;
            case MINIMAL -> conditions();
            case SIMPLE -> conditions() + 1;
            case DESCRIBED -> conditions() + 2;
        };
    }

    private int conditions() {
        return (getLowerBound() == getValue() ? 0 : 1) + (getUpperBound() == getValue() ? 0 : 1);
    }
}
