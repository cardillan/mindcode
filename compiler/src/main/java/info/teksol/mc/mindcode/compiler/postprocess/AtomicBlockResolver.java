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

@NullMarked
public class AtomicBlockResolver extends CompilerMessageEmitter {
    private final InstructionProcessor processor;
    private final List<LogicInstruction> program;
    private Map<Integer, Set<ControlFlowNode>> domTree = Map.of();

    private AtomicBlockResolver(InstructionProcessor processor, List<LogicInstruction> instructions) {
        super(processor.messageConsumer());
        this.processor = processor;
        this.program = new ArrayList<>(instructions);

        computeDominators(instructions);
    }

    public static List<LogicInstruction> resolve(InstructionProcessor processor, List<LogicInstruction> instructions) {
        int start = CollectionUtils.indexOf(instructions, LogicInstruction::isAtomicWait);
        return start < 0 ? instructions : new AtomicBlockResolver(processor, instructions).resolve(start);
    }

    private void computeDominators(List<LogicInstruction> program) {
        ControlFlowGraph graph = new ControlFlowBuilder().buildControlFlowGraph(program);
        DominatorTreeBuilder builder = new DominatorTreeBuilder();
        domTree = new HashMap<>();

        for (ControlFlowNode entryPoint : graph.entryPoints()) {
            DominatorTree dominatorTree = builder.build(entryPoint);

            dominatorTree.tree().forEach((index, nodes) -> {
                if (domTree.put(index, nodes) != null) throw new IllegalStateException("Duplicate dominator tree node: " + index);
            });
        }

//        System.out.println("Dominator tree:");
//        domTree.entrySet().stream()
//                .sorted(Comparator.comparingInt(Map.Entry::getKey))
//                .forEach(e -> System.out.println("#" + e.getKey() + " -> " + e.getValue()));
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

    private @Nullable AstContext context;
    private @Nullable AtomicError error;
    private boolean allAtomic;

    private List<LogicInstruction> resolve(int start) {
        do {
            if (!(program.get(start) instanceof WaitInstruction waitIx)) {
                // Can't happen
                throw new MindcodeInternalError("No 'wait' instruction found in atomic block.");
            }

            context = program.get(start).getAstContext();
            allAtomic = !context.getLocalProfile().isVolatileAtomic();
            int ipt;
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


            // This already includes the initial wait
            AtomicBlock lastBlock = findLastBlockToMerge(start, ipt, maxAllowedSteps);
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

                    int milliTicks = (int) Math.round(60000 * sec);
                    program.set(start, waitIx.withTime(waitValue.get()).setComment(
                            String.format("# %d.%03d ticks for atomic execution of %d steps at %d ipt", milliTicks / 1000, milliTicks % 1000, lastBlock.steps, ipt)));

                    int last = lastBlock.end - 1;
                    while (program.get(last).getRealSize() == 0) last--;
                    program.get(last).setComment("# The last atomic section instruction");
                }
            }

            start = CollectionUtils.indexOf(program, lastBlock.end, LogicInstruction::isAtomicWait);
        } while (start >= 0);

        return program;
    }

    private int findEndOfContext(AstContext context) {
        return CollectionUtils.lastIndexOf(program, ix -> ix.belongsTo(context)) + 1;
    }

    private final Queue<Head> heads = new ArrayDeque<>();
    private final List<AtomicBlock> activeBlocks = new ArrayList<>();

    private AtomicBlock findLastBlockToMerge(int start, int ipt, int maxAllowedSteps) {
        assert context != null;
        AtomicBlock firstBlock = new AtomicBlock(start, findEndOfContext(context));
        activeBlocks.clear();
        activeBlocks.add(firstBlock);

        // If this block doesn't dominate anything else, no need to look ahead
        firstBlock.last = !dominatesAny(firstBlock.start);

        heads.clear();
        heads.offer(new Head(start));

        while (firstBlock.valid() && !heads.isEmpty()) {
            Head head = heads.poll();
            if (head.valid()) {
                while (head.step(maxAllowedSteps)) ;
            }
        }

        if (firstBlock.error != null) {
            return firstBlock;
        } else {
            // Find the last block to process
            int blockToMerge = 0;
            for (int i = 0; i < activeBlocks.size() && activeBlocks.get(i).error == null; i++) {
                // If the shortest code path is greater than a tick, stop
                if (activeBlocks.get(i).shortestPath > ipt) break;
                blockToMerge = i;
                if (activeBlocks.get(i).last) break;
            }

            // Deactivate all merged instructions
            for (int i = 1; i <= blockToMerge; i++) {
                int index = activeBlocks.get(i).start;
                program.set(index, processor.createEmpty(program.get(index).getAstContext()));
            }

            return activeBlocks.get(blockToMerge);
        }
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

        return allAtomic || ix instanceof CustomInstruction || !noOutsideEffects
                || ix.getAllArguments().anyMatch(a -> a.isVolatile() && a != LogicBuiltIn.COUNTER);
    }

    private static class AtomicBlock {
        int start;                      // First instruction index
        int end;                        // Last instruction index + 1
        int steps = 0;                  // Max number of steps to cross from the very beginning
        int shortestPath = 0;           // Shortest code path reaching this block
        boolean last = false;           // This is the last block
        @Nullable AtomicError error;    // This block's error, if any

        public AtomicBlock(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean valid() {
            return error == null;
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

        Head(int start) {
            this.block = activeBlocks.getFirst();
            this.next = start;
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
            //System.out.printf("Branching head %d to %d%n", other.id, id);
        }

        // Sets the error on the current block (if any) and ends the current code path
        boolean error(AtomicError error) {
            if (block != null) {
                block.error = error;
            } else {
                activeBlocks.get(blockIndex).last = true;
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
            //System.out.printf("Head %d, address %d, step %d: %s%n", id, next, step, ix);

            if (block != null && next != block.start && ix.getOpcode() == Opcode.WAIT) {
                return error(ix.isAtomicWait() ? AtomicError.NESTED : AtomicError.WAIT);
            } else if (visited.get(next)) {
                return error(AtomicError.LOOP);
            }

            step += ix.getRealSize();
            // Add 25 steps in case we're still resolving the first block
            // to be able to report the number of steps needed to complete the block better
            if (step > maxAllowedSteps + (blockIndex == 0 && block != null ? 25 : 0)) return error(AtomicError.TOO_LONG);

            // If the instruction isn't safe, extend protection
            if (isProtected(ix) && block != null && step > block.steps) {
                block.steps = step;
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
                            if (!dominates(lastBlock.start, current)) {
                                // The last block doesn't dominate this one, can't merge
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
                                lastBlock.last = true;
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
