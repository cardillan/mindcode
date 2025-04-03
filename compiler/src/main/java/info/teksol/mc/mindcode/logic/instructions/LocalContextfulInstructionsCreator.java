package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class LocalContextfulInstructionsCreator extends AbstractMessageEmitter implements ContextfulInstructionCreator {
    private final InstructionProcessor processor;
    private final Consumer<LogicInstruction> consumer;
    private AstContext astContext;
    private boolean subcontextTypeSet = false;

    public LocalContextfulInstructionsCreator(InstructionProcessor processor, AstContext astContext, Consumer<LogicInstruction> consumer) {
        super(processor.messageConsumer());
        this.processor = processor;
        this.astContext = astContext;
        this.consumer = consumer;
    }

    public void pushContext(AstContextType contextType, AstSubcontextType subcontextType) {
        assert astContext.node() != null;
        astContext = astContext.createChild(astContext.getProfile(), astContext.node(), contextType, subcontextType);
    }

    public void popContext() {
        assert astContext.parent() != null;
        astContext = astContext.parent();
    }

    public void setSubcontextType(AstSubcontextType subcontextType, double multiplier) {
        if (subcontextTypeSet) {
            assert astContext.parent() != null;
            astContext = astContext.parent();
        }
        astContext = astContext.createSubcontext(subcontextType, multiplier);
        subcontextTypeSet = true;
    }

    public void clearSubcontextType() {
        if (subcontextTypeSet) {
            assert astContext.parent() != null;
            astContext = astContext.parent();
            subcontextTypeSet = false;
        }
    }

    @Override
    public InstructionProcessor getProcessor() {
        return processor;
    }

    @Override
    public LogicVariable nextTemp() {
        return processor.nextTemp();
    }

    @Override
    public LogicValue defensiveCopy(ValueStore valueStore, ArgumentType argumentType) {
        if (valueStore instanceof LogicValue value && value.isImmutable()) {
            return value;
        } else {
            LogicVariable tmp = processor.nextTemp().withType(argumentType);
            createSet(tmp, valueStore.getValue(this));
            return tmp;
        }
    }

    @Override
    public void setInternalError() {
        throw new MindcodeInternalError("Internal error occurred.");
    }

    @Override
    public LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments) {
        LogicInstruction instruction = processor.createInstruction(astContext, opcode, arguments);
        consumer.accept(instruction);
        return instruction;
    }
}
