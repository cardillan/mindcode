package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class ControlFlowBuilder {
    /// A label to be used with the `end` instruction
    private static final String START = "#start";

    /// List of all nodes
    private final List<ControlFlowNode> allNodes = new ArrayList<>();

    /// Maps labels to nodes. Jump to a label means the control gets transferred to the node.
    /// Multiple nodes might be marked by the same label in the case of multijump/multicall instructions.
    private final Map<String, List<ControlFlowNode>> nodes = new HashMap<>();

    /// List of entry point label (from function calls)
    private final List<String> entryPoints = new ArrayList<>();

    /// The program entrypoint
    private final ControlFlowNode start = new ControlFlowNode(0);

    /// Currently processed node
    private ControlFlowNode current = start;
    private int index = -1;

    /// Builds the Control Flow Graph and returns the list of nodes.
    /// The first node is the entry points node.
    public ControlFlowGraph buildControlFlowGraph(List<LogicInstruction> program) {
        allNodes.add(start);
        nodes.put(START, new ArrayList<>(List.of(start)));
        entryPoints.add(START);

        for (LogicInstruction instruction : program) {
            index++;

            // Start a new node at this instruction
            switch (instruction) {
                case LabeledInstruction l -> {
                    ensureEmpty();
                    addEntryLabel(l.getLabel());
                    addEntryLabel(l.getMarker());
                }
                case CustomInstruction c when c.isLabel() -> {
                    ensureEmpty();
                    addEntryLabel(c.getMlogOpcode());
                }
                case WaitInstruction w when instruction.isAtomicWait() -> {
                    // We need separate nodes for atomic waits
                    ensureEmpty();
                    current.atomicWait = w;
                }
                default -> { }
            }

            current.addInstruction(instruction);

            // Handle control flow instructions
            // May end the node at this instruction
            switch (instruction) {
                case EndInstruction _ -> jump(START, true);
                case JumpInstruction ix -> {
                    if (ix.getAstContext().matches(AstContextType.JUMPS, AstSubcontextType.REMOTE_INIT)) {
                        // This jump leads to the remote function entrypoint.
                        // We don't need - or want - this node, so we'll terminate it.
                        // It is unreachable from any entrypoint, including start (because there's a jump in front of it)
                        entryPoints.add(ix.getTarget().getLabel());
                        terminate();
                    } else {
                        jump(ix.getTarget().getLabel(), ix.isUnconditional());
                    }
                }
                case MultiJumpInstruction _ -> jump(instruction.getMarker().getLabel(), true);
                case CallingInstruction ix -> entryPoints.add(ix.getCallAddr().getLabel());
                case ReturnInstruction _, ReturnRecInstruction _ -> terminate();
                case CustomInstruction ix when ix.getMlogOpcode().equals("jump") -> {
                    boolean unconditional = ix.getArg(1).toMlog().equals("always");
                    jump(ix.getArg(0).toMlog() + ":", unconditional);
                }
                default -> { }
            }
        }

        // Add edges for each exit label
        for (ControlFlowNode allNode : allNodes) {
            // Add edge for each exit label
            for (String exitLabel : allNode.exitLabels) {
                Objects.requireNonNull(nodes.get(exitLabel), () -> "Unknown label " + exitLabel)
                        .forEach(allNode::addEdgeTo);
            }
        }

//        for (ControlFlowNode node : allNodes) {
//            System.out.println(node + " -> " + node.successors + (node.atomicWait != null ? " (atomic wait)" : ""));
//            node.instructions.forEach(System.out::println);
//        }

        return new ControlFlowGraph(allNodes, entryPoints.stream().map(nodes::get).flatMap(Collection::stream).toList());
    }

    // If the current node is not empty, creates a new one
    private void ensureEmpty() {
        if (!current.empty()) {
            current = current.next(index);
            allNodes.add(current);
        }
    }

    // Marks this node as jumping to a new node
    // Creates a jump
    private void jump(String label, boolean unconditional) {
        current.addExitLabel(label);
        current = unconditional ? new ControlFlowNode(index) : current.next(index);
        allNodes.add(current);
    }

    private void terminate() {
        // Create an unconnected node starting at the next instruction
        current = new ControlFlowNode(index + 1);
    }


    private void addEntryLabel(LogicLabel label) {
        if (label != LogicLabel.EMPTY) addEntryLabel(label.getLabel());
    }

    private void addEntryLabel(String labelName) {
        current.addEntryLabel(labelName);
        nodes.computeIfAbsent(labelName, _ -> new ArrayList<>()).add(current);
    }
}
