package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
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
                        if x == 3 then
                            1;
                        else
                            end();
                        end;
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
                        n = if x == 3 then
                            1;
                        else
                            41;
                        end;
                        move(73, n);
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
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'MIN_TO_MAX' is not initialized.")
                        .add("Variable 'SHOOT' is not initialized."),
                """
                        target = uradar(:enemy, :ground, :any, :health, MIN_TO_MAX);
                        if target != null then
                            approach(target.@x, target.@y, 10);
                            if within(target.@x, target.@y, 10) then
                                target(target.@x, target.@y, SHOOT);
                            end;
                        end;
                        """,
                createInstruction(URADAR, "enemy", "ground", "any", "health", "0", ".MIN_TO_MAX", var(0)),
                createInstruction(SET, ":target", var(0)),
                createInstruction(OP, "notEqual", var(1), ":target", "null"),
                createInstruction(JUMP, var(1000), "equal", var(1), "false"),
                createInstruction(SENSOR, var(3), ":target", "@x"),
                createInstruction(SENSOR, var(4), ":target", "@y"),
                createInstruction(UCONTROL, "approach", var(3), var(4), "10"),
                createInstruction(SENSOR, var(5), ":target", "@x"),
                createInstruction(SENSOR, var(6), ":target", "@y"),
                createInstruction(UCONTROL, "within", var(5), var(6), "10", var(7)),
                createInstruction(JUMP, var(1002), "equal", var(7), "false"),
                createInstruction(SENSOR, var(9), ":target", "@x"),
                createInstruction(SENSOR, var(10), ":target", "@y"),
                createInstruction(UCONTROL, "target", var(9), var(10), ".SHOOT"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1001))
        );
    }

    @Test
    void preventsEliminationOfUlocateUsages() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'outbuilding' is not used.")
                        .add("Variable 'found' is not used.")
                        .add("Variable 'ENEMY' is not initialized."),
                """
                        ulocate(:ore, @surge-alloy, out x, out y);
                        approach(x, y, 4);
                        outbuilding = ulocate(:building, :core, ENEMY, out x, out y, out found);
                        approach(x, y, 4);
                        outbuilding = ulocate(:spawn, out x, out y, out found);
                        approach(x, y, 4);
                        outbuilding = ulocate(:damaged, out x, out y, out found);
                        approach(x, y, 4);
                        """,
                createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "x", "y", var(0), var(1)),
                createInstruction(UCONTROL, "approach", "x", "y", "4"),
                createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "x", "y", "found", var(2)),
                createInstruction(UCONTROL, "approach", "x", "y", "4"),
                createInstruction(ULOCATE, "spawn", "core", "true", "@copper", "x", "y", "found", var(3)),
                createInstruction(UCONTROL, "approach", "x", "y", "4"),
                createInstruction(ULOCATE, "damaged", "core", "true", "@copper", "x", "y", "found", var(4)),
                createInstruction(UCONTROL, "approach", "x", "y", "4"),
                createInstruction(END)
        );
    }

    @Test
    void completelyRemovesDeadCode() {
        assertCompilesTo(
                expectedMessages().add("Variable 'n' is not used."),
                """
                        n = 1;
                        n = 1;
                        """,
                createInstruction(END)
        );
    }

    @Test
    void removesUnusedUlocate() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'a' is not used.")
                        .add("Variable 'b' is not used."),
                """
                        ulocate(:ore, @surge-alloy, out x, out y);
                        ulocate(:ore, @surge-alloy, out a, out b);
                        approach(x, y, 4);
                        """,
                createInstruction(ULOCATE, "ore", "core", "true", "@surge-alloy", "x", "y", var(0), var(1)),
                createInstruction(UCONTROL, "approach", "x", "y", "4"),
                createInstruction(END)
        );
    }

    @Test
    void preventsEliminationOfPartiallyUsedUlocate() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'x' is not used.")
                        .add("Variable 'y' is not used.")
                        .add("Variable 'found' is not used.")
                        .add("Variable 'ENEMY' is not initialized."),
                """
                        outbuilding = ulocate(:building, :core, ENEMY, out x, out y, out found);
                        print(outbuilding);
                        """,
                createInstruction(ULOCATE, "building", "core", "ENEMY", "@copper", "x", "y", "found", var(0)),
                createInstruction(SET, "outbuilding", var(0)),
                createInstruction(PRINT, "outbuilding"),
                createInstruction(END)
        );
    }

    @Test
    void generatesUnusedWarning() {
        assertGeneratesMessage(
                "Variable 'X' is not used.",
                """
                        X = 10;
                        """
        );
    }

    @Test
    void generatesUninitializedWarning() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Variable 'X' is not initialized.")
                        .add("Variable 'Y' is not initialized."),
                """
                        print(X, Y);
                        """
        );
    }

    @Test
    void generatesNoUnexpectedWarnings() {
        assertGeneratesMessages(
                expectedMessages(),
                """
                        def foo(n)
                            n = n + 1;
                        end;
                        z = foo(5);
                        print(z);
                        """
        );
    }

    @Test
    void generatesBothWarnings() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Variable 'z' is not used.")
                        .add("Variable 'Z' is not initialized."),
                """
                        def foo(n)
                            n = n + 1;
                        end;
                        z = foo(5);
                        print(Z);
                        """
        );
    }


    @Test
    void eliminatesUnusedReturnValues() {
        assertCompilesTo("""
                        def foo(n)
                            print(n);
                            n * 2;
                        end;
                        foo(2);
                        foo(4);
                        """,
                createInstruction(SET, ":foo.0:n", "2"),
                createInstruction(SETADDR, ":foo.0*retaddr", label(1)),
                createInstruction(CALL, label(0), "*invalid", ":foo.0*retval"),
                createInstruction(LABEL, label(1)),
                createInstruction(SET, ":foo.0:n", "4"),
                createInstruction(SETADDR, ":foo.0*retaddr", label(2)),
                createInstruction(CALL, label(0), "*invalid", ":foo.0*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(PRINT, ":foo.0:n"),
                createInstruction(RETURN, ":foo.0*retaddr")
        );
    }

    @Test
    void removesUnusedGlobalParameters() {
        assertCompilesTo(
                expectedMessages().add("Variable 'a' is not used."),
                """
                        param a = 1;
                        param b = 2;
                        
                        print(b);
                        """,
                createInstruction(SET, "b", "2"),
                createInstruction(PRINT, "b"),
                createInstruction(END)
        );
    }
}
