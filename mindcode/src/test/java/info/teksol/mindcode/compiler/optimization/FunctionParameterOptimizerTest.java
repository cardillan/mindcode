package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class FunctionParameterOptimizerTest extends AbstractOptimizerTest<FunctionParameterOptimizer> {

    @Override
    protected Class<FunctionParameterOptimizer> getTestedClass() {
        return FunctionParameterOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.FUNCTION_PARAM_OPTIMIZATION
        );
    }

    @Test
    public void handlesSimpleParameters() {
        assertCompilesTo("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(5)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "5"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void handlesNestedParameters() {
        assertCompilesTo("""
                        inline def foo(n)
                            print(n)
                        end
                        inline def bar(n)
                            foo(n)
                        end
                        bar(5)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, "5"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void handlesGlobalVariables() {
        assertCompilesTo("""
                        inline def bar(n)
                            print(n)
                        end
                        X = 5
                        bar(X)
                        """,
                createInstruction(SET, "X", "5"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "X"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void handlesBlockNames() {
        assertCompilesTo("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(switch1)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "switch1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void handlesChainedVariables() {
        assertCompilesTo("""
                        inline def bar(n)
                            a = n
                            print(a)
                        end
                        bar(5)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "5"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void handlesVariablesInExpressions() {
        assertCompilesTo("""
                        inline def bar(n)
                            print(n + 1)
                        end
                        bar(5)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "add", var(1), "5", "1"),
                createInstruction(PRINT, var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void preservesVolatileVariables() {
        assertCompilesTo("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(@time)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "@time"),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void preservesModifiedVariables() {
        assertCompilesTo("""
                        inline def bar(n)
                            n += 1
                            print(n)
                        end
                        bar(0)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "__fn0_n", "0"),
                createInstruction(OP, "add", var(1), "__fn0_n", "1"),
                createInstruction(SET, "__fn0_n", var(1)),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(LABEL, var(1001)),
                createInstruction(END)
        );
    }

    @Test
    public void preservesGlobalVariablesWithFunctionCalls() {
        assertCompilesTo("""
                        inline def bar(n)
                            foo(n)
                            print(n)
                        end
                        def foo(n)
                            print(n)
                        end
                        X = 5
                        foo(X)
                        bar(X)
                        """,
                createInstruction(SET, "X", "5"),
                createInstruction(SET, "__fn0_n", "X"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000)),
                createInstruction(LABEL, var(1001)),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, "__fn1_n", "X"),
                createInstruction(SET, "__fn0_n", "__fn1_n"),
                createInstruction(SETADDR, "__fn0retaddr", var(1004)),
                createInstruction(CALL, var(1000)),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "__fn1_n"),
                createInstruction(LABEL, var(1003)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(LABEL, var(1005)),
                createInstruction(GOTO, "__fn0retaddr"),
                createInstruction(END)
        );
    }

    @Test
    void passesParameterToFunctionRegressionTest() {
        assertCompilesTo(createCompilerProfile().setAllOptimizationLevels(OptimizationLevel.AGGRESSIVE),
                        """
                        X = 5
                        inline def d(n)
                            n
                        end
                        print(1 < d(2))
                        printflush(message1)
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "lessThan", var(1), "1", "2"),
                createInstruction(PRINT, var(1)),
                createInstruction(PRINTFLUSH, "message1"),
                createInstruction(END)
        );
    }
}
