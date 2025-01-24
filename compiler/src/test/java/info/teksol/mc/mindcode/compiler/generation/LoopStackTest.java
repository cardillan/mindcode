package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.messages.ExpectedMessages;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstBreakStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstContinueStatement;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.common.SourcePosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public class LoopStackTest {
    private final LoopStack loopStack = new LoopStack(ExpectedMessages.none());
    private final LogicLabel
            break1    = LogicLabel.symbolic("break1"),
            break2    = LogicLabel.symbolic("break2"),
            continue1 = LogicLabel.symbolic("continue1"),
            continue2 = LogicLabel.symbolic("continue2");

    private final AstIdentifier
            label1 = new AstIdentifier(EMPTY, "label1"),
            label2 = new AstIdentifier(EMPTY, "label2");

    private final AstBreakStatement breakNode = new AstBreakStatement(EMPTY, null);
    private final AstBreakStatement breakNode1 = new AstBreakStatement(EMPTY, label1);
    private final AstBreakStatement breakNode2 = new AstBreakStatement(EMPTY, label2);
    private final AstContinueStatement continueNode = new AstContinueStatement(EMPTY, null);
    private final AstContinueStatement continueNode1 = new AstContinueStatement(EMPTY, label1);
    private final AstContinueStatement continueNode2 = new AstContinueStatement(EMPTY, label2);

    @Nested
    class EmptyLabelsTest {
        @Test
        void remembersLabels() {
            loopStack.enterLoop(null, break1, continue1);

            assertEquals(break1, loopStack.getBreakLabel(breakNode));
            assertEquals(continue1, loopStack.getContinueLabel(continueNode));
        }

        @Test
        void remembersMultipleLabels() {
            loopStack.enterLoop(null, break1, continue1);
            LoopStack.LoopLabels loopLabels = loopStack.enterLoop(null, break2, continue2);
            loopStack.exitLoop(loopLabels);

            assertEquals(break1, loopStack.getBreakLabel(breakNode));
            assertEquals(continue1, loopStack.getContinueLabel(continueNode));
        }

        @Test
        void rejectsProvidingLabelsOnEmptyStack() {
            ExpectedMessages.create()
                    .add("'break' statement outside of a do/while/for loop.")
                    .add("'continue' statement outside of a do/while/for loop.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.getBreakLabel(breakNode);
                        loopStack.getContinueLabel(continueNode);
                    });
        }

        @Test
        void rejectsOutOfOrderLoopExits() {
            LoopStack.LoopLabels loopLabels1 = loopStack.enterLoop(label1, break1, continue1);
            LoopStack.LoopLabels loopLabels2 = loopStack.enterLoop(label2, break2, continue2);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop(loopLabels1));
        }

        @Test
        void rejectsExitLoopOnEmptyStack() {
            LoopStack.LoopLabels loopLabels1 = loopStack.enterLoop(label1, break1, continue1);
            loopStack.exitLoop(loopLabels1);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop(loopLabels1));
        }
    }


    @Nested
    class NonEmptyLabelsTest {
        @Test
        void findsLabeledLabels() {
            loopStack.enterLoop(label1, break1, continue1);
            loopStack.enterLoop(label2, break2, continue2);

            assertEquals(break1, loopStack.getBreakLabel(breakNode1));
            assertEquals(continue1, loopStack.getContinueLabel(continueNode1));
            assertEquals(break2, loopStack.getBreakLabel(breakNode2));
            assertEquals(continue2, loopStack.getContinueLabel(continueNode2));
        }

        @Test
        void rejectsDuplicateLabels() {
            ExpectedMessages.create()
                    .add("Loop label 'label1' already in use.")
                    .validate(consumer -> {
                        LoopStack loopStack = new LoopStack(consumer);
                        loopStack.enterLoop(label1, break1, continue1);
                        loopStack.enterLoop(label1, break2, continue2);
                    });
        }

        @Test
        void rejectsOutOfOrderLoopExits() {
            LoopStack.LoopLabels loopLabels1 = loopStack.enterLoop(label1, break1, continue1);
            LoopStack.LoopLabels loopLabels2 = loopStack.enterLoop(label2, break2, continue2);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop(loopLabels1));
        }

        @Test
        void rejectsExitLoopOnEmptyStack() {
            LoopStack.LoopLabels loopLabels1 = loopStack.enterLoop(label1, break1, continue1);
            loopStack.exitLoop(loopLabels1);
            Assertions.assertThrows(IllegalStateException.class, () -> loopStack.exitLoop(loopLabels1));
        }
    }
}
