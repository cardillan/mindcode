package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class StackUsageOptimizerTest extends AbstractGeneratorTest {

    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.DEAD_CODE_ELIMINATION,
            Optimization.STACK_USAGE_OPTIMIZATION);

    @Test
    void removesUnusedParameter() {
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            foo(x - 1)
                        end
                        print(foo(5))
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__sp", "0"),
                        createInstruction(SET, "__fn0_x", "5"),
                        createInstruction(CALL, "bank1", var(1000), var(1001)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "__fn0retval"),
                        createInstruction(PRINT, var(0)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                        createInstruction(SET, "__fn0_x", var(1)),
                        createInstruction(CALL, "bank1", var(1000), var(1003)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(2), "__fn0retval"),
                        createInstruction(SET, "__fn0retval", var(2)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(RETURN, "bank1"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsUsedParameter() {
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            foo(x - 1)
                            x
                        end
                        print(foo(5))
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__sp", "0"),
                        createInstruction(SET, "__fn0_x", "5"),
                        createInstruction(CALL, "bank1", var(1000), var(1001)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(SET, var(0), "__fn0retval"),
                        createInstruction(PRINT, var(0)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(OP, "sub", var(1), "__fn0_x", "1"),
                        createInstruction(PUSH, "bank1", "__fn0_x"),
                        createInstruction(SET, "__fn0_x", var(1)),
                        createInstruction(CALL, "bank1", var(1000), var(1003)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(POP, "bank1", "__fn0_x"),
                        createInstruction(SET, "__fn0retval", "__fn0_x"),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(RETURN, "bank1"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void handlesEliminatedVariables() {
        // Specifically tailored to trigger the eliminated variables code path:
        // the loop prevents elimination of y from stack due it not being read after function call.
        generateInto(
                pipeline,
                (Seq) translateToAst("""
                        allocate stack in bank1[0...512]
                        def foo(x)
                            while true
                                y = x
                                foo(x)
                            end
                        end
                        foo(5)
                        """
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__sp", "0"),
                        createInstruction(SET, "__fn0_x", "5"),
                        createInstruction(CALL, "bank1", var(1000), var(1001)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1005), "equal", "true", "false"),
                        createInstruction(PUSH, "bank1", "__fn0_x"),
                        createInstruction(SET, "__fn0_x", "__fn0_x"),
                        createInstruction(CALL, "bank1", var(1000), var(1006)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(POP, "bank1", "__fn0_x"),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(RETURN, "bank1"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
