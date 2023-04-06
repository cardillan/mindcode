package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Formats and prints a simple diff between various versions of the program produced by individual optimizers.
 */
public class DiffDebugPrinter implements DebugPrinter {
    private static final int DIFF_MARGIN = 3;
    private static final String ADD_PREFIX          = "+";
    private static final String DELETE_PREFIX       = "-";
    private static final String NO_CHANGE_PREFIX    = " ";

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
    public void instructionEmitted(Optimizer optimizer, LogicInstruction instruction) {
        findProgramVersion(optimizer).getProgram().add(instruction);
    }

    @Override
    public void iterationFinished(Optimizer optimizer, int iteration, List<LogicInstruction> program) {
        versions.add(new ProgramVersion(optimizer, iteration, program));
    }

    private ProgramVersion findProgramVersion(Optimizer optimizer) {
        for (ProgramVersion version : versions) {
            if (version.getOptimizer() == optimizer) {
                return version;
            }
        }

        ProgramVersion version = new ProgramVersion(optimizer);
        versions.add(version);
        return version;
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
    private List<ProgramVersion> selectProgramVersions() {
        switch (level) {
            case 1:     return diffLevel1();
            case 2:     return diffLevel2();
            default:    return versions;
        }
    }

    private List<ProgramVersion> diffLevel1() {
        ProgramVersion first = versions.get(0);
        ProgramVersion last = versions.get(versions.size() - 1);
        last.setTitle("all optimizers");
        return List.of(first, last);
    }

    private List<ProgramVersion> diffLevel2() {
        // Select the last iteration of each optimizer
        List<ProgramVersion> result = new ArrayList<>();
        for (int i = 0; i < versions.size(); i++) {
            if (i == versions.size() - 1 || versions.get(i).getOptimizer() != versions.get(i+1).getOptimizer()) {
                result.add(versions.get(i));
            }
        }

        return result;
    }

    // Prints diff between the "before" and "after" versions. Instructions in the output are numbered
    // according to the after list.
    private void printDiff(Consumer<String> messageConsumer, String title, List<LogicInstruction> before,
            List<LogicInstruction> after) {
        // Do not print steps that didn't change anything
        if (before.equals(after)) {
            return;
        }

        List<String> output = new ArrayList<>();
        messageConsumer.accept("");
        messageConsumer.accept("Modifications by " + title + ":");

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
        };

        // Print lines around difference clusters
        boolean active = false;
        int countdown = 0;
        int skipped = 0;
        for (int i = -DIFF_MARGIN; i < output.size(); i++) {
            if (i + DIFF_MARGIN < output.size() && !output.get(i + DIFF_MARGIN).startsWith(NO_CHANGE_PREFIX)) {
                if (!active && skipped > 0) {
                    messageConsumer.accept(NO_CHANGE_PREFIX + "");
                }
                active = true;
                countdown = 2 * DIFF_MARGIN + 1;
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

    protected int findInstructionIndex(List<LogicInstruction> program, int index, LogicInstruction ix) {
        for (int i = index; i < program.size(); i++) {
            if (program.get(i) == ix) {
                return i;
            }
        }

        return -1;
    }

    private String printInstruction(String prefix, int index, LogicInstruction instruction) {
        StringBuilder str = new StringBuilder(50);
        str.append(prefix);
        if (index >= 0) {
            str.append(String.format("%5d ", index));
        } else {
            str.append("    * ");       // Deleted line -- no number
        }
        str.append(instruction.getOpcode());
        instruction.getArgs().forEach(arg -> str.append(" ").append(arg));
        return str.toString();
    }

    // Class holding program version and information about the optimizer and iteration which produced it.
    static class ProgramVersion {
        private final Optimizer optimizer;
        private final List<LogicInstruction> program;
        private String title;

        ProgramVersion(Optimizer optimizer) {
            this.optimizer = optimizer;
            this.program = new ArrayList<>();
            this.title = optimizer.getName();
        }

        ProgramVersion(Optimizer optimizer, int iteration, List<LogicInstruction> program) {
            this.optimizer = optimizer;
            this.program = List.copyOf(program);
            this.title = optimizer.getName() + ", iteration " + iteration;
        }

        Optimizer getOptimizer() {
            return optimizer;
        }

        List<LogicInstruction> getProgram() {
            return program;
        }

        void setTitle(String title) {
            this.title = title;
        }

        String getTitle() {
            return title;
        }
    }
}
