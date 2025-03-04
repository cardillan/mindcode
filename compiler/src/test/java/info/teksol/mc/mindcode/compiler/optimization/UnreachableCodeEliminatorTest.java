package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.*;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class UnreachableCodeEliminatorTest extends AbstractOptimizerTest<UnreachableCodeEliminator> {

    @Override
    protected Class<UnreachableCodeEliminator> getTestedClass() {
        return UnreachableCodeEliminator.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                JUMP_NORMALIZATION,
                DEAD_CODE_ELIMINATION,
                SINGLE_STEP_ELIMINATION,
                JUMP_THREADING,
                UNREACHABLE_CODE_ELIMINATION
        );
    }

    @Test
    void removesOrphanedJump() {
        assertCompilesTo("""
                        while a do
                            while b do
                                print(b);
                            end;
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, "__start__", "equal", "a", "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1000), "equal", "b", "false"),
                createInstruction(PRINT, "b"),
                createInstruction(JUMP, var(1003), "always")
        );
    }

    @Test
    void eliminateDeadBranch() {
        assertCompilesTo("""
                        print(a);
                        while false do
                            print(b);
                        end;
                        print(c);
                        """,
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "c")
        );
    }

    @Test
    void eliminateUnusedFunction() {
        assertCompilesTo("""
                        def a()
                            print("here");
                        end;
                        while false do
                            a();
                            a();
                        end;
                        print("Done");
                        """,
                createInstruction(PRINT, q("Done"))
        );
    }

    @Test
    void keepsUsedFunctions() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'testa.n' is not used.")
                        .add("Variable 'testb.n' is not used.")
                        .add("Variable 'testc.n' is not used."),
                """
                        allocate stack in cell1[0 .. 63];
                        def testb(n)
                            print("Middle");
                        end;
                        def testc(n)
                            print("End");
                        end;
                        def testa(n)
                            print("Start");
                        end;
                        testa(0);
                        testa(0);
                        while false do
                            testb(1);
                            testb(1);
                        end;
                        testc(2);
                        testc(2);
                        printflush(message1);
                        """,
                createInstruction(LABEL, label(3)),
                createInstruction(JUMP, label(3), "equal", "cell1", "null"),
                createInstruction(SETADDR, ":testa.0*retaddr", label(4)),
                createInstruction(CALL, label(2), ":testa.0*retval"),
                createInstruction(LABEL, label(4)),
                createInstruction(SETADDR, ":testa.0*retaddr", label(5)),
                createInstruction(CALL, label(2), ":testa.0*retval"),
                createInstruction(LABEL, label(5)),
                createInstruction(SETADDR, ":testc.0*retaddr", label(11)),
                createInstruction(CALL, label(1), ":testc.0*retval"),
                createInstruction(LABEL, label(11)),
                createInstruction(SETADDR, ":testc.0*retaddr", label(12)),
                createInstruction(CALL, label(1), ":testc.0*retval"),
                createInstruction(LABEL, label(12)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, q("End")),
                createInstruction(RETURN, ":testc.0*retaddr"),
                createInstruction(LABEL, label(2)),
                createInstruction(PRINT, q("Start")),
                createInstruction(RETURN, ":testa.0*retaddr")
        );
    }

    @Test
    void eliminatesSelfReferencedJumps() {
        assertCompilesTo("""
                        while true do
                           print("foo");
                           printflush(message1);
                        end;
                        print("WooHoo!");
                        """,
                createInstruction(PRINT, q("foo")),
                createInstruction(PRINTFLUSH, "message1")
        );
    }
}
