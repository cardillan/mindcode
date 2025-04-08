package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class StackOptimizerTest extends AbstractOptimizerTest<StackOptimizer> {

    @Override
    protected Class<StackOptimizer> getTestedClass() {
        return StackOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.EXPRESSION_OPTIMIZATION,
                Optimization.STACK_OPTIMIZATION
        );
    }

    @Test
    void removesUnusedParameter() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(x)
                            foo(x - 1);
                        end;
                        print(foo(5));
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:x", "5"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(SET, tmp(0), ":foo*retval"),
                createInstruction(PRINT, tmp(0)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "sub", tmp(1), ":foo:x", "1"),
                createInstruction(SET, ":foo:x", tmp(1)),
                createInstruction(CALLREC, "bank1", label(0), label(4), ":foo*retval"),
                createInstruction(LABEL, label(4)),
                createInstruction(SET, tmp(2), ":foo*retval"),
                createInstruction(SET, ":foo*retval", tmp(2)),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void keepsUsedParameter() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(x)
                            foo(x - 1);
                            x;
                        end;
                        print(foo(5));
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:x", "5"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(SET, tmp(0), ":foo*retval"),
                createInstruction(PRINT, tmp(0)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "sub", tmp(1), ":foo:x", "1"),
                createInstruction(PUSH, "bank1", ":foo:x"),
                createInstruction(SET, ":foo:x", tmp(1)),
                createInstruction(CALLREC, "bank1", label(0), label(4), ":foo*retval"),
                createInstruction(LABEL, label(4)),
                createInstruction(POP, "bank1", ":foo:x"),
                createInstruction(SET, ":foo*retval", ":foo:x"),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void keepsVariablesInLoop() {
        // The loop prevents elimination of y from stack due it not being read after function call.
        // z isn't read in the loop and can be eliminated
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(x)
                            z = x;
                            print(z);
                            while true do
                                y = x;
                                print(y);
                                foo(x);
                            end;
                        end;
                        foo(5);
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:x", "5"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, ":foo:z", ":foo:x"),
                createInstruction(PRINT, ":foo:z"),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(6), "equal", "true", "false"),
                createInstruction(SET, ":foo:y", ":foo:x"),
                createInstruction(PRINT, ":foo:y"),
                createInstruction(PUSH, "bank1", ":foo:y"),
                createInstruction(CALLREC, "bank1", label(0), label(7), ":foo*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank1", ":foo:y"),
                createInstruction(JUMP, label(4), "always"),
                createInstruction(LABEL, label(6)),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void removesVariablesNotInLoop() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(x)
                            while true do
                                y = x - 1;
                                foo(2);
                            end;
                            print(y);
                            foo(1);
                        end;
                        foo(5);
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:x", "5"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(6), "equal", "true", "false"),
                createInstruction(OP, "sub", tmp(1), ":foo:x", "1"),
                createInstruction(SET, ":foo:y", tmp(1)),
                createInstruction(PUSH, "bank1", ":foo:x"),
                createInstruction(PUSH, "bank1", ":foo:y"),
                createInstruction(SET, ":foo:x", "2"),
                createInstruction(CALLREC, "bank1", label(0), label(7), ":foo*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank1", ":foo:y"),
                createInstruction(POP, "bank1", ":foo:x"),
                createInstruction(JUMP, label(4), "always"),
                createInstruction(LABEL, label(6)),
                createInstruction(PRINT, ":foo:y"),
                createInstruction(SET, ":foo:x", "1"),
                createInstruction(CALLREC, "bank1", label(0), label(8), ":foo*retval"),
                createInstruction(LABEL, label(8)),
                createInstruction(SET, tmp(3), ":foo*retval"),
                createInstruction(SET, ":foo*retval", tmp(3)),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void removesUnreadVariables() {
        // For the first call, y isn't read in the loop, but is read after the loop
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(x)
                            y = x;
                            print(y);
                            while true do
                                y = x - 1;
                                foo(2);
                            end;
                        end;
                        foo(5);
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:x", "5"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, ":foo:y", ":foo:x"),
                createInstruction(PRINT, ":foo:y"),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(6), "equal", "true", "false"),
                createInstruction(OP, "sub", tmp(1), ":foo:x", "1"),
                createInstruction(SET, ":foo:y", tmp(1)),
                createInstruction(PUSH, "bank1", ":foo:x"),
                createInstruction(SET, ":foo:x", "2"),
                createInstruction(CALLREC, "bank1", label(0), label(7), ":foo*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank1", ":foo:x"),
                createInstruction(JUMP, label(4), "always"),
                createInstruction(LABEL, label(6)),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void removesUnmodifiedVariables() {
        // For the first call, y isn't read in the loop, but is read after the loop
        assertCompilesTo("""
                        allocate stack in bank1[0...512];
                        def foo(m, n)
                            for i in 1 .. n do
                                print(n);
                                printflush(m);
                                foo(m, n - 1);
                            end;
                        end;
                        foo(message1, 10);
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":foo:m", "message1"),
                createInstruction(SET, ":foo:n", "10"),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, tmp(1), ":foo:n"),
                createInstruction(SET, ":foo:i", "1"),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(6), "greaterThan", ":foo:i", tmp(1)),
                createInstruction(PRINT, ":foo:n"),
                createInstruction(PRINTFLUSH, ":foo:m"),
                createInstruction(OP, "sub", tmp(2), ":foo:n", "1"),
                createInstruction(PUSH, "bank1", ":foo:n"),
                createInstruction(PUSH, "bank1", ":foo:i"),
                createInstruction(PUSH, "bank1", tmp(1)),
                createInstruction(SET, ":foo:n", tmp(2)),
                createInstruction(CALLREC, "bank1", label(0), label(7), ":foo*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank1", tmp(1)),
                createInstruction(POP, "bank1", ":foo:i"),
                createInstruction(POP, "bank1", ":foo:n"),
                createInstruction(OP, "add", ":foo:i", ":foo:i", "1"),
                createInstruction(JUMP, label(4), "always"),
                createInstruction(LABEL, label(6)),
                createInstruction(RETURNREC, "bank1")
        );
    }

    @Test
    void correctlyOptimizesQuicksort() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'partition.left' is not used.")
                        .add("Variable 'partition.right' is not used.")
                        .add("Variable 'SIZE' is not initialized."),
                """
                        allocate stack in bank1[0...512];
                        
                        def quicksort(left, right)
                            if right > left then
                                pivot_index = left + floor(rand(right - left + 1));
                                new_pivot_index = partition(left, right, pivot_index);
                                quicksort(left, new_pivot_index - 1);
                                quicksort(new_pivot_index + 1, right);
                            end;
                        end;
                        
                        inline def partition(left, right, pivot_index)
                            pivot_index;
                        end;
                        
                        quicksort(0, SIZE - 1);
                        """,
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "bank1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(OP, "sub", tmp(0), ".SIZE", "1"),
                createInstruction(SET, ":quicksort.0:left", "0"),
                createInstruction(SET, ":quicksort.0:right", tmp(0)),
                createInstruction(CALLREC, "bank1", label(0), label(2), ":quicksort*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "greaterThan", tmp(2), ":quicksort.0:right", ":quicksort.0:left"),
                createInstruction(JUMP, label(4), "equal", tmp(2), "false"),
                createInstruction(OP, "sub", tmp(4), ":quicksort.0:right", ":quicksort.0:left"),
                createInstruction(OP, "add", tmp(5), tmp(4), "1"),
                createInstruction(OP, "rand", tmp(6), tmp(5)),
                createInstruction(OP, "floor", tmp(7), tmp(6)),
                createInstruction(OP, "add", tmp(8), ":quicksort.0:left", tmp(7)),
                createInstruction(SET, ":quicksort.0:pivot_index", tmp(8)),
                createInstruction(SET, ":partition.0:pivot_index", ":quicksort.0:pivot_index"),
                createInstruction(SET, tmp(9), ":partition.0:pivot_index"),
                createInstruction(SET, ":quicksort.0:new_pivot_index", tmp(9)),
                createInstruction(OP, "sub", tmp(10), ":quicksort.0:new_pivot_index", "1"),
                createInstruction(PUSH, "bank1", ":quicksort.0:right"),
                createInstruction(PUSH, "bank1", ":quicksort.0:new_pivot_index"),
                createInstruction(SET, ":quicksort.0:right", tmp(10)),
                createInstruction(CALLREC, "bank1", label(0), label(7), ":quicksort*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank1", ":quicksort.0:new_pivot_index"),
                createInstruction(POP, "bank1", ":quicksort.0:right"),
                createInstruction(OP, "add", tmp(12), ":quicksort.0:new_pivot_index", "1"),
                createInstruction(SET, ":quicksort.0:left", tmp(12)),
                createInstruction(CALLREC, "bank1", label(0), label(8), ":quicksort*retval"),
                createInstruction(LABEL, label(8)),
                createInstruction(SET, tmp(13), ":quicksort*retval"),
                createInstruction(SET, tmp(3), tmp(13)),
                createInstruction(JUMP, label(5), "always"),
                createInstruction(LABEL, label(4)),
                createInstruction(SET, tmp(3), "null"),
                createInstruction(LABEL, label(5)),
                createInstruction(SET, ":quicksort*retval", tmp(3)),
                createInstruction(RETURNREC, "bank1")
        );
    }
}
