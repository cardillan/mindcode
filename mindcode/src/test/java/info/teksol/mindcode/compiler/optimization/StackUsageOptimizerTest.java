package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.ExpectedMessages;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class StackUsageOptimizerTest extends AbstractOptimizerTest<StackUsageOptimizer> {

    @Override
    protected Class<StackUsageOptimizer> getTestedClass() {
        return StackUsageOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.EXPRESSION_OPTIMIZATION,
                Optimization.STACK_USAGE_OPTIMIZATION
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(SET, "__fn0_x", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0_x", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003), "__fn0retval"),
                createInstruction(LABEL, var(1003)),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0retval", "__fn0_x"),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_z", "__fn0_x"),
                createInstruction(PRINT, "__fn0_z"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "true", "false"),
                createInstruction(SET, "__fn0_y", "__fn0_x"),
                createInstruction(PRINT, "__fn0_y"),
                createInstruction(PUSH, "bank1", "__fn0_y"),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_y"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void removesVariablesNotInLoop() {
        // For the first call, y isn't read in the loop, but is read after the loop
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "true", "false"),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(SET, "__fn0_y", var(1)),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(PUSH, "bank1", "__fn0_y"),
                createInstruction(SET, "__fn0_x", "2"),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_y"),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "__fn0_y"),
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(CALLREC, "bank1", var(1000), var(1007), "__fn0retval"),
                createInstruction(LABEL, var(1007)),
                createInstruction(SET, var(3), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(3)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_y", "__fn0_x"),
                createInstruction(PRINT, "__fn0_y"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "true", "false"),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(SET, "__fn0_y", var(1)),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0_x", "2"),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_m", "message1"),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, var(1), "__fn0_n"),
                createInstruction(SET, "__fn0_i", "1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "greaterThan", "__fn0_i", var(1)),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(PRINTFLUSH, "__fn0_m"),
                createInstruction(OP, "sub", var(2), "__fn0_n", "1"),
                createInstruction(PUSH, "bank1", "__fn0_n"),
                createInstruction(PUSH, "bank1", "__fn0_i"),
                createInstruction(PUSH, "bank1", var(1)),
                createInstruction(SET, "__fn0_n", var(2)),
                createInstruction(CALLREC, "bank1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", var(1)),
                createInstruction(POP, "bank1", "__fn0_i"),
                createInstruction(POP, "bank1", "__fn0_n"),
                createInstruction(OP, "add", "__fn0_i", "__fn0_i", "1"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void correctlyOptimizesQuicksort() {
        // For the first call, y isn't read in the loop, but is read after the loop
        assertCompilesTo(
                ExpectedMessages.create()
                        .add("List of unused variables: partition.left, partition.right.")
                        .add("List of uninitialized variables: SIZE."),
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
                createInstruction(SET, "__sp", "0"),
                createInstruction(OP, "sub", var(0), "SIZE", "1"),
                createInstruction(SET, "__fn0_left", "0"),
                createInstruction(SET, "__fn0_right", var(0)),
                createInstruction(CALLREC, "bank1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "greaterThan", var(2), "__fn0_right", "__fn0_left"),
                createInstruction(JUMP, var(1003), "equal", var(2), "false"),
                createInstruction(OP, "sub", var(4), "__fn0_right", "__fn0_left"),
                createInstruction(OP, "add", var(5), var(4), "1"),
                createInstruction(OP, "rand", var(6), var(5)),
                createInstruction(OP, "floor", var(7), var(6)),
                createInstruction(OP, "add", var(8), "__fn0_left", var(7)),
                createInstruction(SET, "__fn0_pivot_index", var(8)),
                createInstruction(SET, "__fn1_pivot_index", "__fn0_pivot_index"),
                createInstruction(SET, var(9), "__fn1_pivot_index"),
                createInstruction(SET, "__fn0_new_pivot_index", var(9)),
                createInstruction(OP, "sub", var(10), "__fn0_new_pivot_index", "1"),
                createInstruction(PUSH, "bank1", "__fn0_right"),
                createInstruction(PUSH, "bank1", "__fn0_new_pivot_index"),
                createInstruction(SET, "__fn0_right", var(10)),
                createInstruction(CALLREC, "bank1", var(1000), var(1007), "__fn0retval"),
                createInstruction(LABEL, var(1007)),
                createInstruction(POP, "bank1", "__fn0_new_pivot_index"),
                createInstruction(POP, "bank1", "__fn0_right"),
                createInstruction(OP, "add", var(12), "__fn0_new_pivot_index", "1"),
                createInstruction(SET, "__fn0_left", var(12)),
                createInstruction(CALLREC, "bank1", var(1000), var(1008), "__fn0retval"),
                createInstruction(LABEL, var(1008)),
                createInstruction(SET, var(13), "__fn0retval"),
                createInstruction(SET, var(3), var(13)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(3), "null"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0retval", var(3)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }
}
