package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.InputPosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopStackTest {
    private final LoopStack loopStack = new LoopStack(ExpectedMessages.none());
    private final LogicLabel break1 = LogicLabel.symbolic("break1");
    private final LogicLabel break2 = LogicLabel.symbolic("break2");
    private final LogicLabel continue1 = LogicLabel.symbolic("continue1");
    private final LogicLabel continue2 = LogicLabel.symbolic("continue2");

    @Nested
    class NullLabelsTest {
        @Test
        void remembersLabels() {
            loopStack.enterLoop(EMPTY, null, break1, continue1);

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, null));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, null));
        }

        @Test
        void remembersMultipleLabels() {
            loopStack.enterLoop(EMPTY, null, break1, continue1);
            loopStack.enterLoop(EMPTY, null, break2, continue2);
            loopStack.exitLoop(null);

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, null));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, null));
        }

        @Test
        void findsLabeledLabels() {
            loopStack.enterLoop(EMPTY, "label1", break1, continue1);
            loopStack.enterLoop(EMPTY, "label2", break2, continue2);

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, "label1"));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, "label1"));
            assertEquals(break2, loopStack.getBreakLabel(EMPTY, "label2"));
            assertEquals(continue2, loopStack.getContinueLabel(EMPTY, "label2"));
        }

        @Test
        void rejectsDuplicateLabels() {
            ExpectedMessages.create()
                    .add("Loop label 'label1' already in use.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.enterLoop(EMPTY, "label1", break1, continue1);
                        loopStack.enterLoop(EMPTY, "label1", break2, continue2);
                    });
        }

        @Test
        void rejectsProvidingLabelsOnEmptyStack() {
            ExpectedMessages.create()
                    .add("'break' statement outside of a do/while/for loop.")
                    .add("'continue' statement outside of a do/while/for loop.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.getBreakLabel(EMPTY, null);
                        loopStack.getContinueLabel(EMPTY, null);
                    });
        }

        @Test
        void rejectsOutOfOrderLoopExits() {
            loopStack.enterLoop(EMPTY, "label1", break1, continue1);
            loopStack.enterLoop(EMPTY, "label2", break2, continue2);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
        }

        @Test
        void rejectsExitLoopOnEmptyStack() {
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
        }
    }


    @Nested
    class NonNullLabelsTest {
        @Test
        void remembersLabels() {
            loopStack.enterLoop(EMPTY, "", break1, continue1);

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, ""));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, ""));
        }

        @Test
        void remembersMultipleLabels() {
            loopStack.enterLoop(EMPTY, "", break1, continue1);
            loopStack.enterLoop(EMPTY, "", break2, continue2);
            loopStack.exitLoop("");

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, ""));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, ""));
        }

        @Test
        void findsLabeledLabels() {
            loopStack.enterLoop(EMPTY, "label1", break1, continue1);
            loopStack.enterLoop(EMPTY, "label2", break2, continue2);

            assertEquals(break1, loopStack.getBreakLabel(EMPTY, "label1"));
            assertEquals(continue1, loopStack.getContinueLabel(EMPTY, "label1"));
            assertEquals(break2, loopStack.getBreakLabel(EMPTY, "label2"));
            assertEquals(continue2, loopStack.getContinueLabel(EMPTY, "label2"));
        }

        @Test
        void rejectsDuplicateLabels() {
            ExpectedMessages.create()
                    .add("Loop label 'label1' already in use.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.enterLoop(EMPTY, "label1", break1, continue1);
                        loopStack.enterLoop(EMPTY, "label1", break2, continue2);
                    });
        }

        @Test
        void rejectsProvidingLabelsOnEmptyStack() {
            ExpectedMessages.create()
                    .add("'break' statement outside of a do/while/for loop.")
                    .add("'continue' statement outside of a do/while/for loop.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.getBreakLabel(EMPTY, "");
                        loopStack.getContinueLabel(EMPTY, "");
                    });
        }

        @Test
        void rejectsOutOfOrderLoopExits() {
            loopStack.enterLoop(EMPTY, "label1", break1, continue1);
            loopStack.enterLoop(EMPTY, "label2", break2, continue2);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
        }

        @Test
        void rejectsExitLoopOnEmptyStack() {
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop("label1"));
        }
    }
}