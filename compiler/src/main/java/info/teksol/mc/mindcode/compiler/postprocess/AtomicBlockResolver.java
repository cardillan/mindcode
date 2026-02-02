package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.logic.arguments.LogicBuiltIn;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.instructions.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.mindcode.logic.opcodes.WorldAction;
import info.teksol.mc.profile.options.CompilerOptions;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class AtomicBlockResolver extends CompilerMessageEmitter {
    private final InstructionProcessor processor;
    private final List<LogicInstruction> program;
    private int start;

    private AtomicBlockResolver(InstructionProcessor processor, List<LogicInstruction> instructions, int start) {
        super(processor.messageConsumer());
        this.processor = processor;
        this.program = new ArrayList<>(instructions);
        this.start = start;
    }

    public static List<LogicInstruction> resolve(InstructionProcessor processor, List<LogicInstruction> instructions) {
        int start = CollectionUtils.indexOf(instructions, LogicInstruction::isAtomicWait);
        return start < 0 ? instructions : new AtomicBlockResolver(processor, instructions, start).resolve();
    }

    private @Nullable AstContext context;
    private @Nullable AtomicError error;
    private boolean allAtomic;

    private List<LogicInstruction> resolve() {
        do {
            // Skip comments and labels
            while (program.get(start).getRealSize() == 0) start++;

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


            int end = CollectionUtils.indexOf(program, start, ix -> !ix.belongsTo(context));
            if (end < 0) end = program.size();

            // This already includes the initial wait
            int steps = computeNumberOfSteps(program, IntRange.of(start, end - 1), maxAllowedSteps + 25);
            if (error != null) {
                switch (error) {
                    case LOOP -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_LOOPS);
                    case NESTED -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_NESTED);
                    case RECURSIVE -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_RECURSIVE);
                    case WAIT -> error(context.sourcePosition(), ERR.ATOMIC_BLOCK_WAIT);
                }
            } else if (steps > maxAllowedSteps) {
                error(context.sourcePosition(), ERR.ATOMIC_BLOCK_TOO_LONG, steps, maxAllowedSteps);
            }

            double sec = (double) ((1000000 * steps + (ips - 1)) / ips) / 1000000.0;
            Optional<LogicLiteral> waitValue = processor.createLiteral(context.sourcePosition(), sec, false);

            if (waitValue.isEmpty()) {
                throw new MindcodeInternalError("Cannot encode wait time (%s) to a numeric literal.", sec);
            }

            if (program.get(start) instanceof WaitInstruction waitIx) {
                int milliTicks = (int) Math.round(60000 * sec);
                program.set(start, waitIx.withTime(waitValue.get()).setComment(
                        String.format("# %d.%03d ticks for atomic execution of %d steps at %d ipt", milliTicks / 1000, milliTicks % 1000, steps, ipt)));

                int last = end - 1;
                while (program.get(last).getRealSize() == 0) last--;
                program.get(last).setComment("# The last atomic block instruction");
            } else {
                // This is probably a function with the atomic wait removed.
                //throw new MindcodeInternalError("No 'wait' instruction found in atomic block.");
            }

            start = CollectionUtils.indexOf(program, end, ix -> ix.getAstContext().matches(AstContextType.ATOMIC));
        } while (start >= 0);

        return program;
    }

    private int computeNumberOfSteps(List<LogicInstruction> program, IntRange range, int maxAllowedSteps) {
        error = null;
        int maxSteps = 0;
        Queue<Head> heads = new ArrayDeque<>();
        heads.offer(new Head(range.min()));

        MainLoop:
        while (!heads.isEmpty()) {
            Head head = heads.poll();

            while (!head.stack.isEmpty() || range.contains(head.next)) {
                LogicInstruction ix = program.get(head.next);
                if (!head.step(ix)) return -1;
                if (head.step > maxAllowedSteps) return maxAllowedSteps;
                if (head.maxSteps > maxSteps) maxSteps = head.maxSteps;

                switch (ix.getOpcode()) {
                    case END -> {
                        // We've exited the atomic block
                        continue MainLoop;
                    }
                    case RETURN -> {
                        head.returnFromCall();
                    }
                    case JUMP -> {
                        JumpInstruction jump = (JumpInstruction) ix;
                        int index = findLabelIndex(jump.getTarget());
                        if (!head.stack.isEmpty() || range.contains(index)) {
                            if (jump.isUnconditional()) {
                                head.next = index;
                            } else {
                                heads.offer(head.branch(index));
                            }
                        } else if (jump.isUnconditional()) {
                            // We've exited the atomic block
                            continue MainLoop;
                        }
                    }
                    case CALL -> {
                        CallInstruction call = (CallInstruction) ix;
                        head.call(findLabelIndex(call.getCallAddr()));
                    }
                    case CALLREC, RETURNREC -> {
                        error = AtomicError.RECURSIVE;
                        return -1;
                    }
                    case MULTIJUMP, MULTICALL -> {
                        for (int i = 0; i < program.size(); i++) {
                            if (program.get(i) instanceof MultiLabelInstruction gli && gli.getMarker().equals(ix.getMarker())) {
                                if (ix.getOpcode() == Opcode.MULTICALL) {
                                    heads.offer(head.branchCall(i));
                                } else {
                                    heads.offer(head.branch(i));
                                }
                            }
                        }
                        // A separate head was created for each target, discontinue this head
                        continue MainLoop;
                    }
                    case CUSTOM -> {
                        CustomInstruction custIx = (CustomInstruction) ix;
                        if (custIx.getMlogOpcode().equals("jump")) {
                            boolean unconditional = custIx.getArg(1).toMlog().equals("always");
                            int index = findMlogLabelIndex(custIx.getArg(0).toMlog());
                            if (!head.stack.isEmpty() || range.contains(index)) {
                                if (unconditional) {
                                    head.next = index;
                                } else {
                                    heads.offer(head.branch(index));
                                }
                            } else if (unconditional) {
                                // We've exited the atomic block
                                continue MainLoop;
                            }
                        }
                    }
                }
            }
        }

        return maxSteps;
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
                ix -> ix instanceof CustomInstruction l &&l.isLabel() && l.getMlogOpcode().equals(label));
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

    private int counter;

    private class Head {
        final int id = counter++;
        int next;
        int step;
        int maxSteps;
        List<Integer> stack;
        BitSet visited;

        Head(int next) {
            this.next = next;
            this.step = 0;
            stack = new ArrayList<>();
            visited = new BitSet(program.size());
        }

        Head(Head other, int next) {
            this.next = next;
            this.step = other.step;
            this.stack = new ArrayList<>(other.stack);
            this.visited = (BitSet) other.visited.clone();
            //System.out.printf("Branching head %d to %d%n", other.id, id);
        }

        boolean step(LogicInstruction ix) {
            //System.out.printf("Head %d, address %d, step %d: %s%n", id, next, step, ix);
            if (next != start && ix.isAtomicWait()) {
                error = AtomicError.NESTED;
                return false;
            }

            if (next != start && ix.getOpcode() == Opcode.WAIT) {
                error = AtomicError.WAIT;
                return false;
            }

            if (visited.get(next)) {
                error = AtomicError.LOOP;
                return false;
            }

            visited.set(next++);
            step += ix.getRealSize();

            // If the instruction isn't safe, extend protection
            if (isProtected(ix)) {
                maxSteps = step;
            }

            return true;
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

        void returnFromCall() {
            next = stack.removeLast();
        }
    }

    private enum AtomicError { LOOP, NESTED, RECURSIVE, WAIT }
}
