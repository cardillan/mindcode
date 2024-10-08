package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.logic.LogicLabel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopStackTest {
    private final LoopStack loopStack = new LoopStack();
    private final LogicLabel break1 = LogicLabel.symbolic("break1");
    private final LogicLabel break2 = LogicLabel.symbolic("break2");
    private final LogicLabel continue1 = LogicLabel.symbolic("continue1");
    private final LogicLabel continue2 = LogicLabel.symbolic("continue2");

    @Test
    void remembersLabels() {
        loopStack.enterLoop(null, null, break1, continue1);

        assertEquals(break1, loopStack.getBreakLabel(null, null));
        assertEquals(continue1, loopStack.getContinueLabel(null, null));
    }

    @Test
    void remembersMultipleLabels() {
        loopStack.enterLoop(null, null, break1, continue1);
        loopStack.enterLoop(null, null, break2, continue2);
        loopStack.exitLoop(null);

        assertEquals(break1, loopStack.getBreakLabel(null, null));
        assertEquals(continue1, loopStack.getContinueLabel(null, null));
    }

    @Test
    void findsLabeledLabels() {
        loopStack.enterLoop(null, "label1", break1, continue1);
        loopStack.enterLoop(null, "label2", break2, continue2);

        assertEquals(break1, loopStack.getBreakLabel(null, "label1"));
        assertEquals(continue1, loopStack.getContinueLabel(null, "label1"));
        assertEquals(break2, loopStack.getBreakLabel(null, "label2"));
        assertEquals(continue2, loopStack.getContinueLabel(null, "label2"));
    }

    @Test
    void rejectsDuplicateLabels() {
        loopStack.enterLoop(null, "label1", break1, continue1);
        Assertions.assertThrows(MindcodeException.class, () -> loopStack.enterLoop(null, "label1", break2, continue2));
    }

    @Test
    void rejectsProvidingLabelsOnEmptyStack() {
        Assertions.assertThrows(MindcodeException.class, () -> loopStack.getBreakLabel(null, null));
        Assertions.assertThrows(MindcodeException.class, () -> loopStack.getContinueLabel(null, null));
    }

    @Test
    void rejectsOutOfOrderLoopExits() {
        loopStack.enterLoop(null, "label1", break1, continue1);
        loopStack.enterLoop(null, "label2", break2, continue2);
        Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
    }

    @Test
    void rejectsExitLoopOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
    }
}
