package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.AbstractTestBase;
import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
class DiffDebugPrinterTest extends AbstractTestBase {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.COMPILER;
    }

    private final DiffDebugPrinter printer = new DiffDebugPrinter(1);
    private final List<String> messages = new ArrayList<>();

    private final Optimizer optimizer = new Optimizer() {
        @Override
        public String getName() {
            return "Dummy";
        }

        @Override
        public Optimization getOptimization() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLevel(OptimizationLevel level) {
        }

        @Override
        public void setGoal(GenerationGoal goal) {
        }

        @Override
        public void setDebugPrinter(DebugPrinter debugPrinter) {
        }

        @Override
        public boolean optimize(OptimizationPhase phase, int pass) {
            return false;
        }

        @Override
        public List<OptimizationAction> getPossibleOptimizations(int costLimit) {
            return List.of();
        }

        @Override
        public void generateFinalMessages() {
        }
    };

    @Test
    void skipsEmptyIterations() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(PRINT, "c"));
        program.add(createInstruction(END));

        printer.registerIteration(optimizer, "Iteration 1", program);
        printer.registerIteration(optimizer, "Iteration 2", program);
        printer.print(messages::add);

        Assertions.assertTrue(messages.isEmpty());
    }

    @Test
    void identifiesRemovedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(PRINT, "c"));
        program.add(createInstruction(END));

        printer.registerIteration(optimizer, "Iteration 1", program);
        program.remove(3);
        printer.registerIteration(optimizer, "Iteration 2", program);
        printer.print(messages::add);

        assertEquals("""

                        Modifications by all optimizers (-1 instructions):
                             0 set a 1
                             1 set b 2
                             2 op add c a b
                        -    * print c
                             3 end""",
                String.join("\n", messages));
    }

    @Test
    void identifiesAddedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(END));

        printer.registerIteration(optimizer, "Iteration 1", program);
        program.add(3, createInstruction(PRINT, "c"));
        printer.registerIteration(optimizer, "Iteration 2", program);
        printer.print(messages::add);

        assertEquals("""

                        Modifications by all optimizers (+1 instructions):
                             0 set a 1
                             1 set b 2
                             2 op add c a b
                        +    3 print c
                             4 end""",
                String.join("\n", messages));
    }

    @Test
    void identifiesSwappedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(PRINT, "c"));
        program.add(createInstruction(END));

        printer.registerIteration(optimizer, "Iteration 1", program);
        Collections.swap(program, 0, 1);
        printer.registerIteration(optimizer, "Iteration 2", program);
        printer.print(messages::add);

        assertEquals("""

                        Modifications by all optimizers:
                        +    0 set b 2
                             1 set a 1
                        -    * set b 2
                             2 op add c a b
                             3 print c
                             4 end""",
                String.join("\n", messages));
    }
}
