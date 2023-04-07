package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffDebugPrinterTest extends AbstractGeneratorTest {
    private final DiffDebugPrinter printer = new DiffDebugPrinter(1);
    private final List<String> messages = new ArrayList<>();

    private final Optimizer optimizer = new Optimizer() {
        @Override
        public String getName() {
            return "Dummy";
        }

        @Override
        public void setLevel(OptimizationLevel level) {
        }

        @Override public void setDebugPrinter(DebugPrinter debugPrinter) { }
        @Override public void setMessagesRecipient(Consumer<CompilerMessage> messagesRecipient) { }
        @Override public void emit(LogicInstruction instruction) { }
        @Override public void flush() { }
    };

    @Test
    void skipsEmptyIterations() {
        List<LogicInstruction> program = new ArrayList<>();
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(PRINT, "c"));
        program.add(createInstruction(END));

        printer.iterationFinished(optimizer, 1, program);
        printer.iterationFinished(optimizer, 2, program);
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

        printer.iterationFinished(optimizer, 1, program);
        program.remove(3);
        printer.iterationFinished(optimizer, 2, program);
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
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(END));

        printer.iterationFinished(optimizer, 1, program);
        program.add(3, createInstruction(PRINT, "c"));
        printer.iterationFinished(optimizer, 2, program);
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
        program.add(createInstruction(SET, "a", "1"));
        program.add(createInstruction(SET, "b", "2"));
        program.add(createInstruction(OP, "add", "c", "a", "b"));
        program.add(createInstruction(PRINT, "c"));
        program.add(createInstruction(END));

        printer.iterationFinished(optimizer, 1, program);
        Collections.swap(program, 0, 1);
        printer.iterationFinished(optimizer, 2, program);
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
