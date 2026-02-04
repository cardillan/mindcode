package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.WaitInstruction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public class ControlFlowNode {
    final int index;
    final List<LogicInstruction> instructions = new ArrayList<>();
    final Set<String> entryLabels = new HashSet<>();
    final Set<String> exitLabels = new HashSet<>();
    final Set<ControlFlowNode> successors = new HashSet<>();
    final Set<ControlFlowNode> predecessors = new HashSet<>();

    public @Nullable WaitInstruction atomicWait = null;
    int size = 0;

    public ControlFlowNode(int index) {
        this.index = index;
    }

    public void addInstruction(LogicInstruction instruction) {
        instructions.add(instruction);
        size += instruction.getRealSize();
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
