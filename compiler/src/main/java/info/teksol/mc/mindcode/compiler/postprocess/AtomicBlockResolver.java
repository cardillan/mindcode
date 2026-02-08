package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.postprocess.cfg.*;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.WorldAction;
import info.teksol.mc.profile.options.CompilerOptions;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@NullMarked
public class AtomicBlockResolver extends CompilerMessageEmitter {
    public static final boolean DEBUG_PRINT = false;

    private final InstructionProcessor processor;
    private final List<LogicInstruction> program;
    private ControlFlowGraph graph = ControlFlowGraph.EMPTY;
    private Map<Integer, Set<ControlFlowNode>> domTree = Map.of();

    private AtomicBlockResolver(InstructionProcessor processor, List<LogicInstruction> instructions) {
        super(processor.messageConsumer());
        this.processor = processor;
        this.program = new ArrayList<>(instructions);

        computeCfg(instructions);
    }

    public static List<LogicInstruction> resolve(InstructionProcessor processor, List<LogicInstruction> instructions) {
        int start = CollectionUtils.indexOf(instructions, LogicInstruction::isAtomicWait);
        return start < 0 ? instructions : new AtomicBlockResolver(processor, instructions).resolve(start);
    }

    public static void debugPrint(Supplier<String> text) {
        if (DEBUG_PRINT) System.out.println(text.get());
    }

    private void computeCfg(List<LogicInstruction> program) {
        graph = new ControlFlowBuilder().buildControlFlowGraph(program);
        DominatorTreeBuilder builder = new DominatorTreeBuilder();
        domTree = new HashMap<>();

        for (ControlFlowNode entryPoint : graph.entryPoints()) {
            DominatorTree dominatorTree = builder.build(entryPoint);

            dominatorTree.tree().forEach((index, nodes) -> {
                if (domTree.put(index, nodes) != null) throw new IllegalStateException("Duplicate dominator tree node: " + index);
            });
        }

        if (DEBUG_PRINT) {
            System.out.println("Dominator tree:");
            domTree.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .forEach(e -> System.out.println("#" + e.getKey() + " -> " + e.getValue()));
        }
    }

    // Returns true if the atomic block at the first instruction dominates the atomic block at the second instruction
    private boolean dominates(int first, int second) {
        Set<ControlFlowNode> children = domTree.getOrDefault(first, Set.of());
        for (ControlFlowNode child : children) {
            if (child.index == second || dominates(child.index, second)) return true;
        }
        return false;
    }

    // Returns true if the given control node block dominates any other atomic block
    private boolean dominatesAny(int block) {
        Set<ControlFlowNode> children = domTree.getOrDefault(block, Set.of());
        for (ControlFlowNode child : children) {
            if (child.isAtomicBlock() || dominatesAny(child.index)) return true;
        }
        return false;
    }

    private final Map<Integer, BitSet> predecessorsCache = new HashMap<>();

    /// Returns a bitset identifying CFG nodes the target is reachable from
    private BitSet allPredecessors(int target) {
        return predecessorsCache.computeIfAbsent(target, this::computeAllPredecessors);
    }

    private BitSet computeAllPredecessors(int target) {
        BitSet predecessors = new BitSet(program.size());
        predecessors.set(target);

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(target);
        while (!queue.isEmpty()) {
            int node = queue.remove();
            for (ControlFlowNode predecessor : graph.nodeMap().get(node).predecessors) {
                if (!predecessors.get(predecessor.index)) {
                    predecessors.set(predecessor.index);
                    queue.add(predecessor.index);
                }
            }
        }
        return predecessors;
    }

    // We're using recursion, as it is much simpler than maintaining a queue.
    // Realistically, code longer than 1000 instructions shouldn't have atomic sections, so we're not afraid
    // of a stack overflow. If it happens, switch to a heap-based BFS.
    // Using the visited bitset to detect loops
    private boolean allPathsCompatible(ControlFlowNode node, int target, int stepLimit, BitSet predecessors, BitSet visited) {
        if (visited.get(node.index) || !node.compatible) return false;
        visited.set(node.index);

        for (ControlFlowNode successor : node.successors) {
            // This node doesn't lie on a path from node to target, ignore it
            if (!predecessors.get(successor.index)) continue;

            if (successor.index == target) {
                if (stepLimit <= 0) {
                    visited.clear(node.index);
                    return false;
                }
            } else if (!allPathsCompatible(successor, target, stepLimit - successor.size, predecessors, visited)) {
                visited.clear(node.index);
                return false;
            }
        }

        visited.clear(node.index);

        // Reached the end of a path which doesn't visit the target - that's ok
        return true;
    }

    // Returns true if all code paths from start to target are compatible with an atomic section,
    // including the max path length
    private boolean allPathsCompatible(int start, int target, int stepLimit) {
        BitSet visited = new BitSet(program.size());
        ControlFlowNode node = graph.nodeMap().get(start);
        return allPathsCompatible(node, target, stepLimit - node.size, allPredecessors(target), visited);
    }

    private @Nullable AstContext context;
    private int start;          // Current start node
    private int ipt;

    private List<LogicInstruction> resolve(int startNode) {
        this.start = startNode;
        do {
            if (!(program.get(start) instanceof WaitInstruction waitIx)) {
                // Can't happen
                throw new MindcodeInternalError("No 'wait' instruction found in atomic block.");
            }

            context = waitIx.getAstContext();
            if (processor.getProcessorType() == ProcessorType.WORLD_PROCESSOR) {
                if (context.getLocalProfile().isDefault(CompilerOptions.IPT) && context.getGlobalProfile().isDefault(CompilerOptions.SETRATE)) {
                    error(context.sourcePosition(), ERR.ATOMIC_BLOCK_NO_IPT_SET);
                }
                ipt = context.getLocalProfile().getIpt();
            } else {
                ipt = processor.getProcessorType().ipt();
            }
            int ips = 60 * ipt;
            int maxAllowedSteps = 5 * ipt;
            int safetyMargin = (int) Math.ceil(context.getLocalProfile().getAtomicSafetyMargin() * ipt);

            // This already includes the initial wait
            AtomicBlock lastBlock = findLastBlockToMerge(maxAllowedSteps, safetyMargin);
            if (lastBlock.error != null) {
                switch (lastBlock.error) {
                    case LOOP -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_LOOPS);
                    case NESTED -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_NESTED);
                    case RECURSIVE -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_RECURSIVE);
                    case SETRATE -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_SETRATE);
                    case WAIT -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_WAIT);
                    case TOO_LONG -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_TOO_LONG, lastBlock.steps, maxAllowedSteps);
                }
            } else {
                if (lastBlock.steps > maxAllowedSteps) {
                    error(context.sourcePosition(), ERR.ATOMIC_BLOCK_TOO_LONG, lastBlock.steps, maxAllowedSteps);
                } else if (lastBlock.steps <= 1) {
                    program.set(start, processor.createEmpty(context));
                } else {
                    // Rounding up to 1/100,000 sec - less than one instruction at 1000 ipt
                    double sec = Math.ceil(100000d * lastBlock.steps / ips) / 100000;
                    Optional<LogicLiteral> waitValue = processor.createLiteral(context.sourcePosition(), sec, false);

                    if (waitValue.isEmpty()) {
                        throw new MindcodeInternalError("Cannot encode wait time (%s) to a numeric literal.", sec);
                    }

                    program.set(start, waitIx.withTime(waitValue.get()).setComment(formatComment(sec, safetyMargin, lastBlock.steps)));
                    int last = lastBlock.end - 1;
                    while (program.get(last).getRealSize() == 0) last--;
                    program.get(last).setComment("# The last atomic section instruction");
                }
            }

            start = CollectionUtils.indexOf(program, lastBlock.end, LogicInstruction::isAtomicWait);
        } while (start >= 0);

        return program;
    }

    private String formatComment(double sec, int safetyMargin, int steps) {
        int safetyMilliTicks = (1000 * safetyMargin / ipt);
        int milliTicks = (int) Math.round(60000 * sec) - safetyMilliTicks;
        return safetyMargin > 0
                ? String.format("# %d.%03d ticks of safety margin plus %d.%03d ticks for atomic execution of %d steps at %d ipt",
                        safetyMilliTicks/ 1000, safetyMilliTicks % 1000, milliTicks / 1000, milliTicks % 1000, steps, ipt)
                : String.format("# %d.%03d ticks for atomic execution of %d steps at %d ipt", milliTicks / 1000, milliTicks % 1000, steps, ipt);
    }

    private int findEndOfContext(AstContext context) {
        return CollectionUtils.lastIndexOf(program, ix -> ix.belongsTo(context)) + 1;
    }

    private final Queue<Head> heads = new ArrayDeque<>();
    private final List<AtomicBlock> activeBlocks = new ArrayList<>();

    private AtomicBlock findLastBlockToMerge(int maxAllowedSteps, int safetyMargin) {
        assert context != null;
        AtomicBlock firstBlock = new AtomicBlock(start, findEndOfContext(context));
        firstBlock.steps = safetyMargin;
        
        activeBlocks.clear();
        activeBlocks.add(firstBlock);

        heads.clear();
        heads.offer(new Head(start, safetyMargin));

        while (firstBlock.valid() && !heads.isEmpty()) {
            Head head = heads.poll();
            if (head.valid()) {
                while (head.step(maxAllowedSteps)) ;
            }
        }

        if (DEBUG_PRINT) activeBlocks.forEach(System.out::println);
        if (firstBlock.last) return firstBlock;

        // Find the last block to process
        int blockToMerge = 0;
        int bestUsage = firstBlock.tickUsage();
        int maxTicks = context.getLocalProfile().getAtomicMergeLevel();
        debugPrint(() -> "First block tick usage: " + firstBlock.tickUsage());

        for (int i = 1; i < activeBlocks.size(); i++) {
            AtomicBlock block = activeBlocks.get(i);
            if (block.error != null || block.tickCount() > maxTicks) break;
            int tickUsage = block.tickUsage();
            final int index = i;
            debugPrint(() -> "Considering merging in block " + index + ": tick usage: " + tickUsage + " steps, total " + block.steps + " steps");
            if (tickUsage > bestUsage) {
                bestUsage = tickUsage;
                blockToMerge = i;
            }
            if (block.last) break;
        }

        // Deactivate all wait instructions in merged blocks
        for (int i = 1; i <= blockToMerge; i++) {
            int index = activeBlocks.get(i).start;
            program.set(index, processor.createEmpty(program.get(index).getAstContext()));
        }

        return activeBlocks.get(blockToMerge);
    }

    private int findLabelIndex(LogicLabel label) {
        int index = CollectionUtils.indexOf(program, ix -> ix instanceof LabelInstruction l && l.getLabel().equals(label));
        if (index < 0) {
            throw new IllegalArgumentException("Illegal label: " + label);
        }
        return index;
    }

    private int findMlogLabelIndex(String labelToken) {
        String label = labelToken + ":";

        int index = CollectionUtils.indexOf(program,
                ix -> ix instanceof CustomInstruction l && l.isLabel() && l.getMlogOpcode().equals(label));
        if (index < 0) {
            throw new IllegalArgumentException("Illegal label: " + label);
        }
        return index;
    }

    private boolean isProtected(LogicInstruction ix) {
        WorldAction action = ix.getOpcode().getAction();
        boolean noOutsideEffects = action == WorldAction.NONE || action == WorldAction.THIS
                || (ix instanceof ReadInstruction r && r.getMemory() == LogicBuiltIn.THIS)
                || (ix instanceof WriteInstruction w && w.getMemory() == LogicBuiltIn.THIS);

        return ix.getLocalProfile().isAtomicFullProtection() || ix instanceof CustomInstruction || !noOutsideEffects
                || ix.getAllArguments().anyMatch(a -> a.isVolatile() && a != LogicBuiltIn.COUNTER);
    }

    private class AtomicBlock {
        int start;                      // First instruction index
        int end;                        // Last instruction index + 1
        int steps = 0;                  // Max number of steps to cross from the very beginning
        int shortestPath = 0;           // Shortest code path reaching this block
        int maxMergePath;               // Maximum path to allow merging
        boolean last;                   // This is the last block
        @Nullable AtomicError error;    // This block's error, if any

        public AtomicBlock(int start, int end) {
            this.start = start;
            this.end = end;

            assert context != null;
            maxMergePath = context.getLocalProfile().getAtomicMergeLevel() * ipt;

            // If this block doesn't dominate anything else, no need to look further ahead
            last = !dominatesAny(start);
        }

        private void updatePathLength(int step) {
            steps = Math.max(steps, step);
            if (steps > maxMergePath) last = true;
        }

        boolean valid() {
            return error == null;
        }

        /// The number of used instructions from the last tick
        int tickUsage() {
            return steps % ipt;
        }

        /// The number of ticks required to execute this block
        int tickCount() {
            return steps / ipt;
        }

        @Override
        public String toString() {
            return "AtomicBlock{" +
                    "start=" + start +
                    ", end=" + end +
                    ", steps=" + steps +
                    ", shortestPath=" + shortestPath +
                    ", maxMergePath=" + maxMergePath +
                    ", last=" + last +
                    ", error=" + error +
                    '}';
        }
    }

    private int counter = 0;

    private class Head {
        final int id = counter++;
        @Nullable AtomicBlock block;    // Current (or past) atomic block
        int blockIndex = 0;             // Current block index
        int next;                       // The next instruction to be processed (@counter)
        int step = 0;                   // The step number within the current code path
        List<Integer> stack;            // Call stack
        BitSet visited;                 // Visited instructions

        Head(int start, int initialSteps) {
            this.block = activeBlocks.getFirst();
            this.next = start;
            this.step = initialSteps;
            stack = new ArrayList<>();
            visited = new BitSet(program.size());
        }

        Head(Head other, int next) {
            this.block = other.block;
            this.blockIndex = other.blockIndex;
            this.next = next;
            this.step = other.step;
            this.stack = new ArrayList<>(other.stack);
            this.visited = (BitSet) other.visited.clone();
            debugPrint(() -> String.format("Branching head %d to %d (block index %d)%n", other.id, id, blockIndex));
        }

        // Sets the error on the current block (if any) and terminates the current code path analysis.
        // Merging a section is not possible when any path from the start section contains incompatible
        // instructions, and this is checked by the allPathsCompatible function
        boolean error(AtomicError error) {
            if (block != null) {
                block.error = error;
                block.last = true;
            }
            return false;
        }

        boolean valid() {
            // The first block isn't finished yet
            if (blockIndex == 0 && block != null && block.error == null) return true;

            // Find the last processed block: if it is still unresolved, we go on
            AtomicBlock lastBlock = activeBlocks.get(blockIndex);
            return lastBlock.error == null && (block != null || !lastBlock.last);
        }

        boolean step(int maxAllowedSteps) {
            LogicInstruction ix = program.get(next);
            debugPrint(() -> String.format("Head %d, address %d, step %d, block %d: %s", id, next, step, blockIndex, ix));

            if (block != null && next != block.start && ix.getOpcode() == Opcode.WAIT) {
                return error(ix.isAtomicWait() ? AtomicError.NESTED : AtomicError.WAIT);
            } else if (visited.get(next)) {
                return error(AtomicError.LOOP);
            }

            step += ix.getRealSize();
            // Add 25 steps in case we're still resolving the first block
            // to be able to report the number of steps needed to complete the block better
            if (step > maxAllowedSteps + (blockIndex == 0 && block != null ? 25 : 0)) return error(AtomicError.TOO_LONG);

            // If the instruction is protected, update the path length
            if (block != null && isProtected(ix)) {
                block.updatePathLength(step);
            }

            visited.set(next);
            int current = next++;

            switch (ix.getOpcode()) {
                case WAIT -> {
                    if (block == null && ix.isAtomicWait()) {
                        AtomicBlock lastBlock = activeBlocks.get(blockIndex);

                        step--;         // We'll avoid the merged block's wait
                        blockIndex++;   // We're entering a new atomic block

                        if (blockIndex >= activeBlocks.size()) {
                            // Entering a new block for the first time
                            if (!dominates(lastBlock.start, current) || !allPathsCompatible(start, current, maxAllowedSteps)) {
                                // The last block doesn't dominate this one or can't be reached in time: can't merge
                                lastBlock.last = true;
                                return false;
                            } else {
                                // Create a new block
                                block = new AtomicBlock(current, findEndOfContext(ix.getAstContext()));
                                block.shortestPath = step;
                                activeBlocks.add(block);
                            }
                        } else {
                            // There's already a next block visited by other code paths. Is it the same?
                            block = activeBlocks.get(blockIndex);
                            if (block.start != current) {
                                // Different block, can't merge
                                activeBlocks.get(blockIndex - 1).last = true;
                                return false;
                            }
                            block.shortestPath = Math.min(block.shortestPath, step);
                        }
                    } else {
                        // Can't happen. This is an error and is covered above.
                        if (block != null && block.start != current) throw new IllegalStateException("Unexpected wait.");
                    }
                }
                case END -> {
                    // Exiting block
                    // Can't merge more blocks
                    activeBlocks.get(blockIndex).last = true;
                    return false;
                }
                case RETURN -> {
                    return returnFromCall();
                }
                case JUMP -> {
                    JumpInstruction jump = (JumpInstruction) ix;
                    int index = findLabelIndex(jump.getTarget());
                    if (jump.isConditional()) {
                        // Detects loops faster if the branch is the normal flow and this head follows the jump
                        heads.offer(branch(next));
                    }
                    next = index;
                }
                case CALL -> {
                    CallInstruction call = (CallInstruction) ix;
                    call(findLabelIndex(call.getCallAddr()));
                }
                case MULTIJUMP, MULTICALL -> {
                    for (int i = 0; i < program.size(); i++) {
                        if (program.get(i) instanceof MultiLabelInstruction gli && gli.getMarker().equals(ix.getMarker())) {
                            if (ix.getOpcode() == Opcode.MULTICALL) {
                                heads.offer(branchCall(i));
                            } else {
                                heads.offer(branch(i));
                            }
                        }
                    }
                    // A separate head was created for each target, discontinue this head
                    return false;
                }
                case CUSTOM -> {
                    CustomInstruction custom = (CustomInstruction) ix;
                    if (custom.getMlogOpcode().equals("jump")) {
                        boolean conditional = !custom.getArg(1).toMlog().equals("always");
                        int index = findMlogLabelIndex(custom.getArg(0).toMlog());
                        if (conditional) {
                            // Detects loops faster if the branch is the normal flow and this head follows the jump
                            heads.offer(branch(next));
                        }
                        next = index;
                    }
                }
                case CALLREC, RETURNREC -> {
                    return error(AtomicError.RECURSIVE);
                }
                case SETRATE -> {
                    return error(AtomicError.SETRATE);
                }
            }

            // We've exited the current block
            if (stack.isEmpty() && block != null && (next < block.start || next >= block.end)) {
                // The active block can't be further merged, no need to go on
                if (block.last) return false;
                block = null;
            }

            return next < program.size();
        }

        Head branch(int address) {
            return new Head(this, address);
        }

        Head branchCall(int address) {
            return new Head(this, next).call(address);
        }

        Head call(int address) {
            stack.add(next);
            next = address;
            return this;
        }

        boolean returnFromCall() {
            if (stack.isEmpty()) {
                if (block != null) throw new IllegalStateException("Return from an atomic block.");
                return false;
            } else {
                next = stack.removeLast();
                return true;
            }
        }
    }

    private enum AtomicError { LOOP, NESTED, RECURSIVE, WAIT, SETRATE, TOO_LONG }
}
