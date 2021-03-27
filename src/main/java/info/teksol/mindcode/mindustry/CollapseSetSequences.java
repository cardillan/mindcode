package info.teksol.mindcode.mindustry;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CollapseSetSequences implements LogicInstructionPipeline {
    private final LogicInstructionPipeline next;
    private final Map<String, String> replacements = new LinkedHashMap<>();
    private final Set<String> used = new HashSet<>();

    CollapseSetSequences(LogicInstructionPipeline next) {
        this.next = next;
    }

    @Override
    public void emit(LogicInstruction instruction) {
        if (instruction.isSet()) {
            if (replacements.containsKey(instruction.getArgs().get(1))) {
                used.add(instruction.getArgs().get(1));
                next.emit(
                        new LogicInstruction(
                                instruction.getOpcode(),
                                instruction.getArgs().get(0),
                                replacements.get(instruction.getArgs().get(1))
                        )
                );
            } else {
                replacements.put(instruction.getArgs().get(0), instruction.getArgs().get(1));
            }
        } else if (instruction.isWrite()) {
            if (replacements.containsKey(instruction.getArgs().get(0))) used.add(instruction.getArgs().get(0));
            if (replacements.containsKey(instruction.getArgs().get(2))) used.add(instruction.getArgs().get(2));
            flushPendingAndContinueWith(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            replacements.getOrDefault(instruction.getArgs().get(0), instruction.getArgs().get(0)),
                            instruction.getArgs().get(1),
                            replacements.getOrDefault(instruction.getArgs().get(2), instruction.getArgs().get(2))
                    )
            );
        } else if (instruction.isRead()) {
            if (replacements.containsKey(instruction.getArgs().get(1))) used.add(instruction.getArgs().get(1));
            if (replacements.containsKey(instruction.getArgs().get(2))) used.add(instruction.getArgs().get(2));
            flushPendingAndContinueWith(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            instruction.getArgs().get(0),
                            replacements.getOrDefault(instruction.getArgs().get(1), instruction.getArgs().get(1)),
                            replacements.getOrDefault(instruction.getArgs().get(2), instruction.getArgs().get(2))
                    )
            );
        } else if (instruction.isPrint()) {
            if (replacements.containsKey(instruction.getArgs().get(0))) {
                used.add(instruction.getArgs().get(0));
                next.emit(new LogicInstruction(instruction.getOpcode(), replacements.get(instruction.getArgs().get(0))));
            } else {
                next.emit(instruction);
            }
        } else if (instruction.isOp() && instruction.getArgs().size() == 4) {
            if (replacements.containsKey(instruction.getArgs().get(2))) used.add(instruction.getArgs().get(2));
            if (replacements.containsKey(instruction.getArgs().get(3))) used.add(instruction.getArgs().get(3));
            flushPendingAndContinueWith(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            instruction.getArgs().get(0),
                            instruction.getArgs().get(1),
                            replacements.getOrDefault(instruction.getArgs().get(2), instruction.getArgs().get(2)),
                            replacements.getOrDefault(instruction.getArgs().get(3), instruction.getArgs().get(3))
                    )
            );
        } else if (instruction.isUControl() && instruction.getArgs().size() == 4) {
            if (replacements.containsKey(instruction.getArgs().get(1))) used.add(instruction.getArgs().get(1));
            if (replacements.containsKey(instruction.getArgs().get(2))) used.add(instruction.getArgs().get(2));
            if (replacements.containsKey(instruction.getArgs().get(3))) used.add(instruction.getArgs().get(3));
            flushPendingAndContinueWith(
                    new LogicInstruction(
                            instruction.getOpcode(),
                            instruction.getArgs().get(0),
                            replacements.getOrDefault(instruction.getArgs().get(1), instruction.getArgs().get(1)),
                            replacements.getOrDefault(instruction.getArgs().get(2), instruction.getArgs().get(2)),
                            replacements.getOrDefault(instruction.getArgs().get(3), instruction.getArgs().get(3))
                    )
            );
        } else {
            flushPendingAndContinueWith(instruction);
        }
    }

    @Override
    public void flush() {

    }

    private void flushPendingAndContinueWith(LogicInstruction instruction) {
        final Set<String> missingSets = new HashSet<>(replacements.keySet());
        missingSets.removeAll(used);
        missingSets.forEach((k) -> next.emit(new LogicInstruction("set", k, replacements.get(k))));
        replacements.clear();
        used.clear();
        next.emit(instruction);
    }
}
