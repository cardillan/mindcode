package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class FunctionParameterOptimizerTest extends AbstractGeneratorTest {

    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            getCompilerProfile(),
            Optimization.DEAD_CODE_ELIMINATION,
            Optimization.FUNCTION_PARAM_OPTIMIZATION);

    @Test
    public void handlesSimpleParameters() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(5)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "5"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void handlesNestedParameters() {
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        inline def foo(n)
                            print(n)
                        end
                        inline def bar(n)
                            foo(n)
                        end
                        bar(5)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "5"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void handlesGlobalVariables() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            print(n)
                        end
                        X = 5
                        bar(X)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "X", "5"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "X"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void handlesBlockNames() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(switch1)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "switch1"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void handlesChainedVariables() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            a = n
                            print(a)
                        end
                        bar(5)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(PRINT, "5"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void handlesVariablesInExpressions() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            print(n + 1)
                        end
                        bar(5)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "add", var(1), "5", "1"),
                        createInstruction(PRINT, var(1)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void preservesVolatileVariables() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            print(n)
                        end
                        bar(@time)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_n", "@time"),
                        createInstruction(PRINT, "__fn0_n"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void preservesModifiedVariables() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        inline def bar(n)
                            n += 1
                            print(n)
                        end
                        bar(0)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, "__fn0_n", "0"),
                        createInstruction(OP, "add", var(1), "__fn0_n", "1"),
                        createInstruction(SET, "__fn0_n", var(1)),
                        createInstruction(PRINT, "__fn0_n"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    public void preservesGlobalVariablesWithFunctionCalls() {
        generateInto(pipeline,
                (Seq) translateToAst("""
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
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
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
                ),
                terminus.getResult()
        );
    }

    @Test
    void passesParameterToFunctionRegressionTest() {
        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(OP, "lessThan", var(1), "1", "2"),
                        createInstruction(PRINT, var(1)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                generateAndOptimize(
                        (Seq) translateToAst("""
                                inline def d(n)
                                    n
                                end
                                print(1 < d(2))
                                printflush(message1)
                                """
                        )
                )
        );
    }
}
