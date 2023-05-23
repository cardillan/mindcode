package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class StackUsageOptimizerTest extends AbstractOptimizerTest<StackUsageOptimizer> {

    @Override
    protected Class<StackUsageOptimizer> getTestedClass() {
        return StackUsageOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.STACK_USAGE_OPTIMIZATION
        );
    }

    @Test
    void removesUnusedParameter() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            foo(x - 1)
                        end
                        print(foo(5))
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(SET, "__fn0_x", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003)),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(2), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(2)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void keepsUsedParameter() {
        assertCompilesTo("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            foo(x - 1)
                            x
                        end
                        print(foo(5))
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0_x", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003)),
                createInstruction(LABEL, var(1003)),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0retval", "__fn0_x"),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void keepsVariablesInLoop() {
        // The loop prevents elimination of y from stack due it not being read after function call.
        // z isn't read in the loop and can be eliminated
        assertCompilesTo("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            z = x
                            print(z)
                            while true
                                y = x
                                print(y)
                                foo(x)
                            end
                        end
                        foo(5)
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_z", "__fn0_x"),
                createInstruction(PRINT, "__fn0_z"),
                createInstruction(LABEL, var(1003)),
                createInstruction(JUMP, var(1005), "equal", "true", "false"),
                createInstruction(SET, "__fn0_y", "__fn0_x"),
                createInstruction(PRINT, "__fn0_y"),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(PUSH, "bank1", "__fn0_y"),
                createInstruction(SET, "__fn0_x", "__fn0_x"),
                createInstruction(CALLREC, "bank1", var(1000), var(1006)),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_y"),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void removesVariablesNotInLoop() {
        // For the first call, y isn't read in the loop, but is read after the loop
        assertCompilesTo("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            while true
                                y = x - 1
                                foo(2)
                            end
                            print(y)
                            foo(1)
                        end
                        foo(5)
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
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
                createInstruction(CALLREC, "bank1", var(1000), var(1006)),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_y"),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "__fn0_y"),
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(CALLREC, "bank1", var(1000), var(1007)),
                createInstruction(LABEL, var(1007)),
                createInstruction(SET, var(3), "__fn0retval"),
                createInstruction(SET, "__fn0retval", var(3)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }

    @Test
    void removesUnreadVariables() {
        // For the first call, y isn't read in the loop, but is read after the loop
        assertCompilesTo("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            y = x
                            print(y)
                            while true
                                y = x - 1
                                foo(2)
                            end
                        end
                        foo(5)
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "5"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
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
                createInstruction(CALLREC, "bank1", var(1000), var(1006)),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1005)),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }
}
