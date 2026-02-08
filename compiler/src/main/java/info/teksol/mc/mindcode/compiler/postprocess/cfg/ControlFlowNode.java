package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.WaitInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class ControlFlowNode {
    // First instruction index
    public final int index;
    public final List<LogicInstruction> instructions = new ArrayList<>();
    public final Set<String> entryLabels = new HashSet<>();
    public final Set<String> exitLabels = new HashSet<>();
    public final Set<ControlFlowNode> successors = new HashSet<>();
    public final Set<ControlFlowNode> predecessors = new HashSet<>();

    @Nullable WaitInstruction atomicWait = null;
    public int size = 0;

    /// Indicates the node is compatible with an atomic section
    public boolean compatible = true;

    public ControlFlowNode(int index) {
        this.index = index;
    }

    private static final Set<Opcode> INCOMPATIBLE_OPCODES = Set.of(WAIT, CALL, CALLREC, MULTICALL, RETURN, RETURNREC, SETRATE);

    public void addInstruction(LogicInstruction instruction) {
        instructions.add(instruction);
        size += instruction.getRealSize();
        if (INCOMPATIBLE_OPCODES.contains(instruction.getOpcode()) && !instruction.isAtomicWait()) {
            compatible = false;
        }
    }

    public int getIndex() {
        return index;
    }

    public boolean isAtomicBlock() {
        return atomicWait != null;
    }

    public boolean empty() {
        return size == 0;
    }

    public void addEntryLabel(String label) {
        entryLabels.add(label);
    }

    public void addExitLabel(String label) {
        exitLabels.add(label);
    }

    public void addEdgeTo(ControlFlowNode target) {
        successors.add(target);
        target.predecessors.add(this);
    }

    // Completely removes an unreachable node from the graph.
    public void remove() {
        if (!predecessors.isEmpty()) {
            throw new IllegalStateException("Cannot remove a node that has predecessors");
        }
        successors.forEach(node -> node.predecessors.remove(this));
        successors.clear();
    }

    public ControlFlowNode next(int index) {
        ControlFlowNode next = new ControlFlowNode(index);
        addEdgeTo(next);
        return next;
    }

    @Override
    public String toString() {
        return "#" + index;
    }
}
