package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.mindustry.LogicInstruction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffDebugPrinterTest {
    private final DiffDebugPrinter printer = new DiffDebugPrinter(1);
    private final List<String> messages = new ArrayList<>();

    private final Optimizer optimiser = new Optimizer() {
        @Override
        public String getName() {
            return "Dummy";
        }

        @Override public void setDebugPrinter(DebugPrinter debugPrinter) { }
        @Override public void emit(LogicInstruction instruction) { }
        @Override public void flush() { }
    };

    @Test
    void skipsEmptyIterations() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(new LogicInstruction(SET, "a", "1"));
        program.add(new LogicInstruction(SET, "b", "2"));
        program.add(new LogicInstruction(OP, "add", "c", "a", "b"));
        program.add(new LogicInstruction(PRINT, "c"));
        program.add(new LogicInstruction(END));

        printer.iterationFinished(optimiser, 1, program);
        printer.iterationFinished(optimiser, 2, program);
        printer.print(messages::add);

        Assertions.assertTrue(messages.isEmpty());
    }

    @Test
    void identifiesRemovedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(new LogicInstruction(SET, "a", "1"));
        program.add(new LogicInstruction(SET, "b", "2"));
        program.add(new LogicInstruction(OP, "add", "c", "a", "b"));
        program.add(new LogicInstruction(PRINT, "c"));
        program.add(new LogicInstruction(END));

        printer.iterationFinished(optimiser, 1, program);
        program.remove(3);
        printer.iterationFinished(optimiser, 2, program);
        printer.print(messages::add);

        assertEquals("\n" +
                "Modifications by all optimizers:\n" +
                "     0 set a 1\n" +
                "     1 set b 2\n" +
                "     2 op add c a b\n" +
                "-    * print c\n" +
                "     3 end",
                String.join("\n", messages));
    }

    @Test
    void identifiesAddedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(new LogicInstruction(SET, "a", "1"));
        program.add(new LogicInstruction(SET, "b", "2"));
        program.add(new LogicInstruction(OP, "add", "c", "a", "b"));
        program.add(new LogicInstruction(END));

        printer.iterationFinished(optimiser, 1, program);
        program.add(3, new LogicInstruction(PRINT, "c"));
        printer.iterationFinished(optimiser, 2, program);
        printer.print(messages::add);

        assertEquals("\n" +
                "Modifications by all optimizers:\n" +
                "     0 set a 1\n" +
                "     1 set b 2\n" +
                "     2 op add c a b\n" +
                "+    3 print c\n" +
                "     4 end",
                String.join("\n", messages));
    }

    @Test
    void identifiesSwappedInstructions() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(new LogicInstruction(SET, "a", "1"));
        program.add(new LogicInstruction(SET, "b", "2"));
        program.add(new LogicInstruction(OP, "add", "c", "a", "b"));
        program.add(new LogicInstruction(PRINT, "c"));
        program.add(new LogicInstruction(END));

        printer.iterationFinished(optimiser, 1, program);
        Collections.swap(program, 0, 1);
        printer.iterationFinished(optimiser, 2, program);
        printer.print(messages::add);

        assertEquals("\n" +
                "Modifications by all optimizers:\n" +
                "+    0 set b 2\n" +
                "     1 set a 1\n" +
                "-    * set b 2\n" +
                "     2 op add c a b\n" +
                "     3 print c\n" +
                "     4 end",
                String.join("\n", messages));
    }
}
