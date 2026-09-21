package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.profile.LocalCompilerProfile;
import info.teksol.mc.profile.RuntimeErrorReporting;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
        return getSize(getLocalProfile(), conditions());
    }

    private int conditions() {
        return (hasLowerBound() ? 1 : 0) + (hasUpperBound() ? 1 : 0);
    }

    @Override
    public void resolve(InstructionProcessor processor, Consumer<LogicInstruction> consumer) {
        LocalContextfulInstructionsCreator creator = creator(processor, consumer);

        switch (getLocalProfile().getErrorReporting()) {
            case NONE -> {}
            case ASSERT -> consumer.accept(this);
            case MINIMAL -> {
                if (hasLowerBound()) {
                    LogicLabel label = processor.nextLabel();
                    creator.createLabel(label);
                    creator.createJump(label, getLowerCondition().inverse(false), getLowerBound(), getValue());
                }
                if (hasUpperBound()) {
                    LogicLabel label = processor.nextLabel();
                    creator.createLabel(label);
                    creator.createJump(label, getUpperCondition().inverse(false), getValue(), getUpperBound());
                }
            }
            case SIMPLE, DESCRIBED -> {
                LogicLabel logicLabelStop = processor.nextLabel();
                LogicLabel logicLabelRun = processor.nextLabel();

                if (!hasUpperBound()) {
                    creator.createJump(logicLabelRun, getLowerCondition(), getLowerBound(), getValue());
                } else {
                    if (hasLowerBound()) {
                        creator.createJump(logicLabelStop, getLowerCondition().inverse(false), getLowerBound(), getValue());
                    }
                    creator.createJump(logicLabelRun, getUpperCondition(), getValue(), getUpperBound());
                }

                creator.createLabel(logicLabelStop);
                if (getLocalProfile().getErrorReporting() == RuntimeErrorReporting.DESCRIBED) {
                    creator.createPrint(getMessage());
                }
                creator.createStop();
                creator.createLabel(logicLabelRun);
            }
        }
    }


    public static int getSize(LocalCompilerProfile profile, int conditions) {
        return switch (profile.getErrorReporting()) {
            case NONE -> 0;
            case ASSERT -> 1;
            case MINIMAL -> conditions;
            case SIMPLE -> conditions + 1;
            case DESCRIBED -> conditions + 2;
        };
    }
}
