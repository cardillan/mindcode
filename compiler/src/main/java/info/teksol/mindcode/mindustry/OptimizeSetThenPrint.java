package info.teksol.mindcode.mindustry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class OptimizeSetThenPrint implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private State state;

    OptimizeSetThenPrint(LogicInstructionPipeline next) {
        this.next = next;
        this.state = new EmptyState();
    }

    @Override
    public void emit(LogicInstruction instruction) {
        state = state.emit(instruction);
    }

    @Override
    public void flush() {
        state = state.flush();
        next.flush();
    }

    private interface State {
        State emit(LogicInstruction instruction);

        State flush();
    }

    private final class EmptyState implements State {
        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                // Due to problems, the only set then print we will optimize are static strings
                // Anything else is left as-is
                if (instruction.getArgs().get(1).startsWith("\"")) {
                    return new ExpectPrint(instruction);
                } else {
                    next.emit(instruction);
                    return this;
                }
            } else {
                next.emit(instruction);
                return this;
            }
        }

        @Override
        public State flush() {
            return this;
        }
    }

    private final class ExpectPrint implements State {
        private final Map<String, String> sets = new LinkedHashMap<>();
        private final List<LogicInstruction> prints = new ArrayList<>();

        ExpectPrint(LogicInstruction set) {
            this.sets.put(set.getArgs().get(0), set.getArgs().get(1));
        }

        @Override
        public State emit(LogicInstruction instruction) {
            if (instruction.isSet()) {
                if (sets.containsKey(instruction.getArgs().get(0))) {
                    // we don't know what's going on: there is a reassignment of a variable that was previously set
                    // cancel this optimization, as it is probably something we can't do anything about
                    //
                    // This branch of the conditional skips code like this:
                    //
                    //   set tmp0 "dmg: "
                    //   print tmp0
                    //   print total_damage
                    //   set tmp0 "\n"
                    //   print tmp0
                    //
                    // The Mindcode compiler does not generate something like this at the moment (Apr 2021), but
                    // things may change and we need to be conservative.
                    sets.forEach((k, v) -> next.emit(new LogicInstruction("set", k, v)));
                    prints.forEach(next::emit);
                    next.emit(instruction);
                    return new EmptyState();
                } else if (instruction.getArgs().get(1).startsWith("\"")) {
                    sets.put(instruction.getArgs().get(0), instruction.getArgs().get(1));
                    return this;
                } else {
                    // this was a set, but not for a static string -- flush everything
                    sets.forEach((k, v) -> next.emit(new LogicInstruction("set", k, v)));
                    prints.forEach(next::emit);
                    next.emit(instruction);
                    return new EmptyState();
                }
            }

            if (!instruction.isPrint()) {
                sets.forEach((k, v) -> next.emit(new LogicInstruction("set", k, v)));
                prints.forEach(next::emit);
                next.emit(instruction);
                return new EmptyState();
            }

            if (!sets.containsKey(instruction.getArgs().get(0))) {
                prints.add(instruction);
                return this;
            }

            prints.add(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            sets.remove(instruction.getArgs().get(0))
                    )
            );

            return this;
        }

        @Override
        public State flush() {
            sets.forEach((k, v) -> next.emit(new LogicInstruction("set", k, v)));
            prints.forEach(next::emit);
            return new EmptyState();
        }
    }
}
