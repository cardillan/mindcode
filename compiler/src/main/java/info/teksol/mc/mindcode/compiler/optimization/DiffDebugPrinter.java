package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.InstructionCounter;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.NoOpInstruction;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/// Formats and prints a simple diff between various versions of the program produced by individual optimizers.
@NullMarked
public class DiffDebugPrinter implements DebugPrinter {
    protected static final String ADD_PREFIX          = "+";
    protected static final String DELETE_PREFIX       = "-";
    protected static final String NO_CHANGE_PREFIX    = " ";

    private int diffMargin = 3;
    private final List<ProgramVersion> versions = new ArrayList<>();
    private final int level;

    /**
     * Creates an instance producing desired level of detail in its output. Possible levels are:
     * <ul>
     * <li>1: provides a single diff between initial and optimized state of the program</li>
     * <li>2: provides a diff for each optimizer which modified the program</li>
     * <li>3: provides a diff for each optimizer and iteration which modified the program</li>
     * </ul>
     * @param level level of detail or provided information.
     */
    public DiffDebugPrinter(int level) {
        this.level = level;
    }

    @Override
    public void registerIteration(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
        versions.add(new ProgramVersion(optimizer, title, program));
    }

    public int getDiffMargin() {
        return diffMargin;
    }

    public void setDiffMargin(int diffMargin) {
        this.diffMargin = diffMargin;
    }

    @Override
    public void print(Consumer<String> messageConsumer) {
        List<ProgramVersion> sel = selectProgramVersions();
        for (int i = 1; i < sel.size(); i++) {
            printDiff(messageConsumer, sel.get(i).getTitle(), sel.get(i - 1).getProgram(), sel.get(i).getProgram());
        }
    }

    /*
     * Selects a subset of recorded versions of the program according to the level of detail.
     */
    protected List<ProgramVersion> selectProgramVersions() {
        return switch (level) {
            case 1 -> diffLevel1();
            case 2 -> diffLevel2();
            default -> diffLevel3();
        };
    }

    protected List<ProgramVersion> diffLevel1() {
        ProgramVersion first = versions.getFirst();
        ProgramVersion last = versions.getLast();
        last.setTitle("all optimizers");
        return List.of(first, last);
    }

    protected List<ProgramVersion> diffLevel2() {
        // Select the last iteration of each optimizer
        List<ProgramVersion> result = new ArrayList<>();
        for (int i = 0; i < versions.size(); i++) {
            if (i == versions.size() - 1 || versions.get(i).getOptimizerClass() != versions.get(i+1).getOptimizerClass()) {
                result.add(versions.get(i));
            }
        }

        return result;
    }

    protected List<ProgramVersion> diffLevel3() {
        return versions;
    }

    protected boolean printAll() {
        return false;
    }

    // Prints diff between the "before" and "after" versions. Instructions in the output are numbered
    // according to the after list.
    protected void printDiff(Consumer<String> messageConsumer, String title, List<LogicInstruction> before0,
            List<LogicInstruction> after0) {

        List<LogicInstruction> before = before0.stream().filter(ix -> !(ix instanceof NoOpInstruction)).toList();
        List<LogicInstruction> after  =  after0.stream().filter(ix -> !(ix instanceof NoOpInstruction)).toList();

        // Do not print steps that didn't change anything
        if (before.equals(after) && !printAll()) {
            return;
        }

        int sizeBefore = InstructionCounter.globalSize(before);
        int sizeAfter = InstructionCounter.globalSize(after);
        String sizeChange = sizeBefore == sizeAfter ? "" : " (%+d instructions)".formatted(sizeAfter - sizeBefore);

        List<String> output = new ArrayList<>();
        messageConsumer.accept("");
        messageConsumer.accept("Modifications by " + title + sizeChange + ":");

        int index1 = 0;
        int index2 = 0;
        while (index1 < before.size() && index2 < after.size()) {
            LogicInstruction ix1 = before.get(index1);
            LogicInstruction ix2 = after.get(index2);

            // A primitive diff; relies on the fact that instructions which were not touched by the optimizer
            // are represented by the same instance in both lists
            if (ix1 == ix2) {
                output.add(printInstruction(NO_CHANGE_PREFIX, index2, ix2));
                index1++;
                index2++;
            } else if (findInstructionIndex(after, index2, ix1) < 0) {
                output.add(printInstruction(DELETE_PREFIX, -1, ix1));
                index1++;
            } else {
                output.add(printInstruction(ADD_PREFIX, index2, ix2));
                index2++;
            }
        }

        // Add the rest
        before.subList(index1, before.size()).forEach(ix -> output.add(printInstruction(DELETE_PREFIX, -1, ix)));
        for (int i = index2; i < after.size(); i++) {
            output.add(printInstruction(ADD_PREFIX, i, after.get(i)));
        }

        // Print lines around difference clusters
        boolean active = printAll();
        int countdown = active ? 2 * diffMargin + 1 : 0;
        int skipped = 0;
        for (int i = -diffMargin; i < output.size(); i++) {
            if (i + diffMargin < output.size() && !output.get(i + diffMargin).startsWith(NO_CHANGE_PREFIX)) {
                if (!active && skipped > 0) {
                    messageConsumer.accept(NO_CHANGE_PREFIX);
                }
                active = true;
                countdown = 2 * diffMargin + 1;
            } else {
                countdown--;
                if (countdown <= 0) {
                    active = false;
                }
            }

            if (i < 0) continue;

            if (active) {
                messageConsumer.accept(output.get(i));
                skipped = 0;
            } else {
                skipped++;
            }
        }
    }

    protected int findInstructionIndex(List<LogicInstruction> program, int index, LogicInstruction instruction) {
        return CollectionUtils.indexOf(program, index, ix -> ix == instruction);
    }

    protected String printInstruction(String prefix, int index, LogicInstruction instruction) {
        StringBuilder str = new StringBuilder(50);
        str.append(prefix);
        if (index >= 0) {
            String formatted = "     ".concat(String.valueOf(index));
            str.append(formatted.substring(formatted.length() - 5)).append(' ');
            //str.append(String.format("%5d ", index));
        } else {
            str.append("    * ");       // Deleted line -- no number
        }
        str.append(instruction.getMlogOpcode());
        instruction.getArgs().forEach(arg -> str.append(" ").append(arg.toMlog()));
        return str.toString();
    }

    // Class holding program version and information about the optimizer and iteration which produced it.
    protected static class ProgramVersion {
        private final @Nullable Class<? extends Optimizer> optimizerClass;
        private final List<LogicInstruction> program;
        private String title;

        public ProgramVersion(@Nullable Optimizer optimizer, String title, List<LogicInstruction> program) {
            this.optimizerClass = optimizer == null ? null : optimizer.getClass();
            this.title = title;
            this.program = List.copyOf(program);
        }

        public @Nullable Class<? extends Optimizer> getOptimizerClass() {
            return optimizerClass;
        }

        public List<LogicInstruction> getProgram() {
            return program;
        }

        void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
