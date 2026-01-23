package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class LocalContextfulInstructionsCreator implements ContextfulInstructionCreator {
    private final InstructionProcessor processor;
    private final Consumer<LogicInstruction> consumer;
    private AstContext astContext;
    private boolean subcontextTypeSet = false;

    public LocalContextfulInstructionsCreator(InstructionProcessor processor, AstContext astContext, Consumer<LogicInstruction> consumer) {
        this.processor = processor;
        this.astContext = astContext;
        this.consumer = consumer;
    }

    public void pushContext(AstContextType contextType, AstSubcontextType subcontextType) {
        assert astContext.node() != null;
        astContext = astContext.createChild(astContext.node(), contextType, subcontextType);
        subcontextTypeSet = false;
    }

    public void popContext() {
        assert astContext.parent() != null;
        astContext = astContext.parent();
        subcontextTypeSet = true;
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
    public LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments) {
        LogicInstruction instruction = processor.createInstruction(astContext, opcode, arguments);
        consumer.accept(instruction);
        return instruction;
    }
}
