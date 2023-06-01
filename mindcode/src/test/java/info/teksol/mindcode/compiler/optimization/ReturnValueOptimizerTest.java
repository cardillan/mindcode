package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.logic.Condition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class ReturnValueOptimizerTest extends AbstractOptimizerTest<ReturnValueOptimizer> {

    @Override
    protected Class<ReturnValueOptimizer> getTestedClass() {
        return ReturnValueOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.RETURN_VALUE_OPTIMIZATION
        );
    }


    @Test
    void optimizesFnRetVal() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, retval0, fn0retval),
                        createInstruction(PRINT, retval0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(PRINT, fn0retval),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesOtherVariable() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, retval0, tmp1),
                        createInstruction(PRINT, retval0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(PRINT, tmp1),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesWithLocalJumps() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, retval0, tmp1),
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(LABEL, label0),
                        createInstruction(PRINT, retval0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(LABEL, label0),
                        createInstruction(PRINT, tmp1),
                        createInstruction(END)
                )
        );
    }

    @Test
    void optimizesFunctionCallsWithoutFnRetVal() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SET, retval0, unit),
                        createInstruction(CALLREC, bank1, label0, label1),
                        createInstruction(LABEL, label1),
                        createInstruction(PRINT, retval0),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(CALLREC, bank1, label0, label1),
                        createInstruction(LABEL, label1),
                        createInstruction(PRINT, unit),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresMultipleUses() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, tmp1),
                createInstruction(SET, a, retval0),
                createInstruction(SET, b, retval0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(SET, a, retval0),
                createInstruction(SET, retval0, tmp1),
                createInstruction(END)
        );
    }

    @Test
    void ignoresNonlinearCodeJumps() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, tmp1),
                createInstruction(LABEL, label0),
                createInstruction(SET, a, retval0),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(END)
        );
    }

    @Test
    void ignoresNonlinearCodeGoto() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, tmp0),
                createInstruction(GOTOLABEL, label0, marker),
                createInstruction(PRINT, retval0),
                createInstruction(GOTO, tmp1, marker),
                createInstruction(END)
        );
    }

    @Test
    void ignoresModifiedVariables() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, a),
                createInstruction(SET, a, K1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresFunctionCallsWithFnRetVal() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, fn0retval),
                createInstruction(CALLREC, bank1, label0, label1),
                createInstruction(LABEL, label1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresFunctionCallsWithGlobals() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, C),
                createInstruction(CALLREC, bank1, label0, label1),
                createInstruction(LABEL, label1),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresVolatiles() {
        assertDoesNotOptimize(
                createInstruction(SET, retval0, time),
                createInstruction(PRINT, retval0),
                createInstruction(END)
        );
    }

    @Test
    void optimizesInlineFunction() {
        assertCompilesTo("""
                        def foo(x)
                            x
                        end
                        print(foo(1))
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "__fn0_x"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesStacklessFunction() {
        assertCompilesTo("""
                        def foo(x)
                            x
                        end
                        print(foo(1), foo(2))
                        """,
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000)),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(SET, "__fn0_x", "2"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000)),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(PRINT, var(0)),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0retval", "__fn0_x"),
                createInstruction(LABEL, var(1003)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }

    @Test
    void optimizesRecursiveFunction() {
        assertCompilesTo("""
                        allocate stack in bank1
                        def foo(x)
                            foo(x - 1)
                        end
                        print(foo(1))
                        """,
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(CALLREC, "bank1", var(1000), var(1001)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                createInstruction(PUSH, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0_x", var(1)),
                createInstruction(CALLREC, "bank1", var(1000), var(1003)),
                createInstruction(LABEL, var(1003)),
                createInstruction(POP, "bank1", "__fn0_x"),
                createInstruction(SET, "__fn0retval", "__fn0retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(RETURN, "bank1"),
                createInstruction(END)
        );
    }


    @Test
    void ignoresVolatileInScript() {
        assertCompilesTo("""
                        def foo(x)
                            @time
                        end
                        print(foo(1))
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_x", "1"),
                createInstruction(SET, var(0), "@time"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, var(0)),
                createInstruction(END)
        );
    }
}