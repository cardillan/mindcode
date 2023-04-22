package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.MessageLevel;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static info.teksol.mindcode.logic.Opcode.*;
import static junit.framework.Assert.assertEquals;

class DeadCodeEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.DEAD_CODE_ELIMINATION);

    @Test
    void removesDeadSetsInIfExpression() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        if x == 3
                            1
                        else
                            end()
                        end
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "equal", var(0), "x", "3"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsUsefulIfAssignments() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        n = if x == 3
                            1
                        else
                            41
                        end
                        move(73, n)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "equal", var(0), "x", "3"),
                        createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                        createInstruction(SET, var(1), "1"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(1), "41"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, "n", var(1)),
                        createInstruction(UCONTROL, "move", "73", "n"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preventsEliminationOfUradarUsages() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        target = uradar(enemy, ground, any, health, MIN_TO_MAX)
                        if target != null
                            approach(target.x, target.y, 10)
                            if within(target.x, target.y, 10)
                                target(target.x, target.y, SHOOT)
                            end
                        end
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(URADAR, "enemy", "ground", "any", "health", "0", "MIN_TO_MAX", var(0)),
                        createInstruction(SET, "target", var(0)),
                        createInstruction(OP, "notEqual", var(1), "target", "null"),
                        createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                        createInstruction(SENSOR, var(3), "target", "@x"),
                        createInstruction(SENSOR, var(4), "target", "@y"),
                        createInstruction(UCONTROL, "approach", var(3), var(4), "10"),
                        createInstruction(SENSOR, var(5), "target", "@x"),
                        createInstruction(SENSOR, var(6), "target", "@y"),
                        createInstruction(UCONTROL, "within", var(5), var(6), "10", var(7)),
                        createInstruction(JUMP, var(1002), "equal", var(7), "false"),
                        createInstruction(SENSOR, var(9), "target", "@x"),
                        createInstruction(SENSOR, var(10), "target", "@y"),
                        createInstruction(UCONTROL, "target", var(9), var(10), "SHOOT"),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preventsEliminationOfUlocateUsages() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        ulocate(ore, @surge-alloy, outx, outy)
                        approach(outx, outy, 4)
                        ulocate(building, core, ENEMY, outx, outy, outbuilding)
                        approach(outx, outy, 4)
                        ulocate(spawn, outx, outy, outbuilding)
                        approach(outx, outy, 4)
                        ulocate(damaged, outx, outy, outbuilding)
                        approach(outx, outy, 4)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                        createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(2), "outbuilding"),
                        createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                        createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", var(3), "outbuilding"),
                        createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                        createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", var(4), "outbuilding"),
                        createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void completelyRemovesDeadcode() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        n = 1
                        n = 1
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void removesUnusedUlocate() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        ulocate(ore, @surge-alloy, outx, outy)
                        ulocate(ore, @surge-alloy, x, y)
                        approach(outx, outy, 4)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                        createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void preventsEliminationOfPartiallyUsedUlocate() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        found = ulocate(building, core, ENEMY, outx, outy, outbuilding)
                        print(found)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", var(0), "outbuilding"),
                        createInstruction(SET, "found", var(0)),
                        createInstruction(PRINT, "found"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    private String extractWarnings(List<CompilerMessage> messages) {
        return messages.stream()
                .filter(CompilerMessage::isWarning)
                .map(CompilerMessage::message)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    @Test
    void generatesUnusedWarning() {
        List<CompilerMessage> messages = new ArrayList<>();
        pipeline.setMessagesRecipient(messages::add);
        generateInto(pipeline,
                (Seq) translateToAst("""
                        X = 10
                        """
                )
        );

        assertEquals(
                "List of unused variables: X.",
                extractWarnings(messages)
        );
    }

    @Test
    void generatesUninitializedWarning() {
        List<CompilerMessage> messages = new ArrayList<>();
        pipeline.setMessagesRecipient(messages::add);
        generateInto(pipeline,
                (Seq) translateToAst("""
                        print(X, Y)
                        """
                )
        );

        assertEquals(
                "List of uninitialized variables: X, Y.",
                extractWarnings(messages)
        );
    }

    @Test
    void generatesNoUnexpectedWarnings() {
        List<CompilerMessage> messages = new ArrayList<>();
        pipeline.setMessagesRecipient(messages::add);
        generateInto(pipeline,
                (Seq) translateToAst("""
                        def foo(n)
                            n = n + 1
                        end
                        z = foo(5)
                        print(z)
                        """
                )
        );

        assertEquals(
                "",
                extractWarnings(messages)
        );
    }

    @Test
    void generatesBothWarnings() {
        List<CompilerMessage> messages = new ArrayList<>();
        pipeline.setMessagesRecipient(messages::add);
        generateInto(pipeline,
                (Seq) translateToAst("""
                        def foo(n)
                            n = n + 1
                        end
                        z = foo(5)
                        print(Z)
                        """
                )
        );

        assertEquals(
                """
                        List of unused variables: z.
                        List of uninitialized variables: Z.""",
                extractWarnings(messages)
        );
    }
}
