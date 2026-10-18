package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.instructions.ArrayAccessInstruction;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.OpInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static info.teksol.mc.mindcode.logic.arguments.Operation.NOT_EQUAL;

/// Expands virtual instructions into full code:
/// - array access instructions (READARR, WRITEARR)
/// - Boolean OR ('||')
@NullMarked
public class VirtualInstructionResolver {
    private final InstructionProcessor processor;

    private boolean expanded = false;
    private final Map<String, JumpTable> jumpTables = new TreeMap<>();

    public VirtualInstructionResolver(InstructionProcessor processor) {
        this.processor = processor;
    }

    public boolean expanded() {
        return expanded;
    }

    private int index;

    public boolean resolveVirtualInstructions(List<LogicInstruction> program) {
        // Already expanded, do not repeat
        if (!expanded && analyze(program)) {
            for (index = 0; index < program.size(); index++) {
                LogicInstruction ix = program.get(index);
                if (ix.getArrayInjectedInto() != null) {
                    program.remove(index--);
                } else if (ix instanceof ArrayAccessInstruction || ix instanceof OpInstruction op && op.getOperation() == Operation.BOOLEAN_OR) {
                    program.remove(index);
                    expandInstruction(ix, newIx -> program.add(index++, newIx));
                    index--;
                }
            }

            int skip = program.getLast().getOpcode() == Opcode.END ? 1 : 0;
            jumpTables.values().stream()
                    .map(JumpTable::instructions)
                    .flatMap(Collection::stream)
                    .skip(skip)
                    .forEach(program::add);

            return true;
        } else {
            return false;
        }
    }

    private boolean analyze(List<LogicInstruction> program) {
        if (expanded) return false;
        expanded = true;

        boolean found = false;
        for (LogicInstruction instruction : program) {
            if (instruction instanceof ArrayAccessInstruction ix && ix.getArrayInjectedInto() == null) {
                ix.getArrayConstructor().generateJumpTable(jumpTables);
                found = true;
            } else if (instruction instanceof OpInstruction op && op.getOperation() == Operation.BOOLEAN_OR) {
                found = true;
            }
        }

        return found;
    }

    private void expandInstruction(LogicInstruction instruction, Consumer<LogicInstruction> consumer) {
        if (instruction instanceof ArrayAccessInstruction ix) {
            ix.getArrayConstructor().expandInstruction(consumer, jumpTables);
        } else if (instruction instanceof OpInstruction op && op.getOperation() == Operation.BOOLEAN_OR) {
            LogicVariable tmp = processor.nextTemp();
            consumer.accept(op.withOperationAndResult(Operation.BITWISE_OR, tmp));
            consumer.accept(op.withOperands(NOT_EQUAL, tmp, LogicBoolean.FALSE));
        } else {
            consumer.accept(instruction);
        }
    }
}
