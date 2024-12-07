package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoopStackTest {
    private final LoopStack loopStack = new LoopStack(ExpectedMessages.none());
    private final LogicLabel
            break1    = LogicLabel.symbolic("break1"),
            break2    = LogicLabel.symbolic("break2"),
            continue1 = LogicLabel.symbolic("continue1"),
            continue2 = LogicLabel.symbolic("continue2");

    @Nested
    class EmptyLabelsTest {
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


    @Nested
    class NonEmptyLabelsTest {
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
