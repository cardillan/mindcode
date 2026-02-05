package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.instructions.*;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

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
    private final Set<String> entryPoints = new HashSet<>();

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
                    // We need separate nodes for atomic waits, starting exactly at the wait, no labels before
                    if (!current.instructions.isEmpty()) next();
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
                        // It will be unreachable, but that's ok - we don't need it for our analysis
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

        // Set of entrypoints
        Set<ControlFlowNode> entryNodeSet = entryPoints.stream().map(nodes::get).flatMap(Collection::stream).collect(Collectors.toSet());

        // Remove unreachables
        while (true) {
            Optional<ControlFlowNode> unreachable = allNodes.stream().filter(n -> n.predecessors.isEmpty() && !entryNodeSet.contains(n)).findAny();
            unreachable.ifPresent(ControlFlowNode::remove);
            unreachable.ifPresent(allNodes::remove);
            if (unreachable.isEmpty()) break;
        }

//        System.out.println("Total nodes: " + allNodes.size());
//        for (ControlFlowNode node : allNodes) {
//            System.out.println(node + " -> " + node.successors + (node.atomicWait != null ? " (atomic wait)" : ""));
//            node.instructions.forEach(System.out::println);
//        }

        return new ControlFlowGraph(allNodes, List.copyOf(entryNodeSet));
    }

    private void next() {
        current = current.next(index);
        allNodes.add(current);
    }

    // If the current node is not empty, creates a new one
    private void ensureEmpty() {
        if (!current.empty()) next();
    }

    // Marks this node as jumping to a new node
    // Creates a jump
    private void jump(String label, boolean unconditional) {
        current.addExitLabel(label);
        // Create a node (unconnected for unconditional jump) starting at the next instruction
        current = unconditional ? new ControlFlowNode(index + 1) : current.next(index + 1);
        allNodes.add(current);
    }

    private void terminate() {
        // Create an unconnected node starting at the next instruction
        current = new ControlFlowNode(index + 1);
        allNodes.add(current);
    }


    private void addEntryLabel(LogicLabel label) {
        if (label != LogicLabel.EMPTY) addEntryLabel(label.getLabel());
    }

    private void addEntryLabel(String labelName) {
        current.addEntryLabel(labelName);
        nodes.computeIfAbsent(labelName, _ -> new ArrayList<>()).add(current);
    }
}
