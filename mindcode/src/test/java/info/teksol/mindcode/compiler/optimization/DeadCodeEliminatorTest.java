package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class DeadCodeEliminatorTest extends AbstractOptimizerTest<DeadCodeEliminator> {

    @Override
    protected Class<DeadCodeEliminator> getTestedClass() {
        return DeadCodeEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.DEAD_CODE_ELIMINATION);
    }

    @Test
    void removesDeadSetsInIfExpression() {
        assertCompilesTo("""
                        if x == 3
                            1
                        else
                            end()
                        end
                        """,
                createInstruction(OP, "equal", var(0), "x", "3"),
                createInstruction(JUMP, var(1000), "equal", var(0), "false"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(END),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    void keepsUsefulIfAssignments() {
        assertCompilesTo("""
                        n = if x == 3
                            1
                        else
                            41
                        end
                        move(73, n)
                        """,
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
        );
    }

    @Test
    void preventsEliminationOfUradarUsages() {
        assertCompilesToWithMessages(ignore("List of uninitialized variables: MIN_TO_MAX, SHOOT."),
                """
                        target = uradar(enemy, ground, any, health, MIN_TO_MAX)
                        if target != null
                            approach(target.x, target.y, 10)
                            if within(target.x, target.y, 10)
                                target(target.x, target.y, SHOOT)
                            end
                        end
                        """,
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
        );
    }

    @Test
    void preventsEliminationOfUlocateUsages() {
        assertCompilesToWithMessages(ignore(
                        "List of unused variables: found, outbuilding.",
                        "List of uninitialized variables: ENEMY."),
                """
                        ulocate(ore, @surge-alloy, outx, outy)
                        approach(outx, outy, 4)
                        outbuilding = ulocate(building, core, ENEMY, outx, outy, found)
                        approach(outx, outy, 4)
                        outbuilding = ulocate(spawn, outx, outy, found)
                        approach(outx, outy, 4)
                        outbuilding = ulocate(damaged, outx, outy, found)
                        approach(outx, outy, 4)
                        """,
                createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", "found", var(2)),
                createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "outx", "outy", "found", var(3)),
                createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "outx", "outy", "found", var(4)),
                createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                createInstruction(END)
        );
    }

    @Test
    void completelyRemovesDeadCode() {
        assertCompilesToWithMessages(ignore("List of unused variables: n."),
                """
                        n = 1
                        n = 1
                        """,
                createInstruction(END)
        );
    }

    @Test
    void removesUnusedUlocate() {
        assertCompilesToWithMessages(ignore("List of unused variables: x, y."),
                """
                        ulocate(ore, @surge-alloy, outx, outy)
                        ulocate(ore, @surge-alloy, x, y)
                        approach(outx, outy, 4)
                        """,
                createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "outx", "outy", var(0), var(1)),
                createInstruction(UCONTROL, "approach", "outx", "outy", "4"),
                createInstruction(END)
        );
    }

    @Test
    void preventsEliminationOfPartiallyUsedUlocate() {
        assertCompilesToWithMessages(ignore(
                        "List of unused variables: found, outx, outy.",
                        "List of uninitialized variables: ENEMY."),
                """
                        outbuilding = ulocate(building, core, ENEMY, outx, outy, found)
                        print(outbuilding)
                        """,
                createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "outx", "outy", "found", var(0)),
                createInstruction(SET, "outbuilding", var(0)),
                createInstruction(PRINT, "outbuilding"),
                createInstruction(END)
        );
    }

    @Test
    void generatesUnusedWarning() {
        assertGeneratesWarnings("""
                        X = 10
                        """,
                "List of unused variables: X.");
    }

    @Test
    void generatesUninitializedWarning() {
        assertGeneratesWarnings("""
                        print(X, Y)
                        """,
                "List of uninitialized variables: X, Y.");
    }

    @Test
    void generatesNoUnexpectedWarnings() {
        assertGeneratesWarnings("""
                        def foo(n)
                            n = n + 1
                        end
                        z = foo(5)
                        print(z)
                        """,
                "");
    }

    @Test
    void generatesBothWarnings() {
        assertGeneratesWarnings(
                """
                        def foo(n)
                            n = n + 1
                        end
                        z = foo(5)
                        print(Z)
                        """,
                """
                        List of unused variables: z.
                        List of uninitialized variables: Z."""
        );
    }


    @Test
    void eliminatesUnusedReturnValues() {
        assertCompilesTo("""
                        def foo(n)
                            print(n)
                            n * 2
                        end
                        foo(2)
                        foo(4)
                        """,
                createInstruction(SET, "__fn0_n", "2"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, "__fn0_n", "4"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }
}
