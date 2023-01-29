package info.teksol.mindcode.mindustry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopStackTest {
    private final LoopStack loopStack = new LoopStack();

    @Test
    void remembersLabels() {
        loopStack.enterLoop(null, "break", "continue");

        assertEquals("break", loopStack.getBreakLabel(null));
        assertEquals("continue", loopStack.getContinueLabel(null));
    }

    @Test
    void remembersMultipleLabels() {
        loopStack.enterLoop(null, "break1", "continue1");
        loopStack.enterLoop(null, "break2", "continue2");
        loopStack.exitLoop(null);

        assertEquals("break1", loopStack.getBreakLabel(null));
        assertEquals("continue1", loopStack.getContinueLabel(null));
    }

    @Test
    void findsLabeledLabels() {
        loopStack.enterLoop("label1", "break1", "continue1");
        loopStack.enterLoop("label2", "break2", "continue2");

        assertEquals("break1", loopStack.getBreakLabel("label1"));
        assertEquals("continue1", loopStack.getContinueLabel("label1"));
        assertEquals("break2", loopStack.getBreakLabel("label2"));
        assertEquals("continue2", loopStack.getContinueLabel("label2"));
    }

    @Test
    void rejectsDuplicateLabels() {
        loopStack.enterLoop("label1", "break1", "continue1");
        Assertions.assertThrows(GenerationException.class, () -> loopStack.enterLoop("label1", "break2", "continue2"));
    }

    @Test
    void rejectsProvidingLabelsOnEmptyStack() {
        Assertions.assertThrows(GenerationException.class, () -> loopStack.getBreakLabel(null));
        Assertions.assertThrows(GenerationException.class, () -> loopStack.getContinueLabel(null));
    }

    @Test
    void rejectsOutOfOrderLoopExits() {
        loopStack.enterLoop("label1", "break1", "continue1");
        loopStack.enterLoop("label2", "break2", "continue2");
        Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
    }

    @Test
    void rejectsExitLoopOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
    }
}
