package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class InaccessibleCodeEliminatorTest extends AbstractGeneratorTest {

    // Sequences of jumps are not generated without dead code elimination
    private final LogicInstructionPipeline sut = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.CONDITIONAL_JUMPS_NORMALIZATION,
            Optimization.DEAD_CODE_ELIMINATION,
            Optimization.SINGLE_STEP_JUMP_ELIMINATION,
            Optimization.JUMP_TARGET_PROPAGATION,
            Optimization.INACCESSIBLE_CODE_ELIMINATION
    );

    @Test
    void removesOrphanedJump() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "while a while b print(b) end end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(JUMP, var(1000), "equal", "a", "false"),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(JUMP, var(1001), "equal", "b", "false"),
                        createInstruction(PRINT, "b"),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(JUMP, var(1003), "always"),
                        createInstruction(LABEL, var(1011)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1002))
                ),
                terminus.getResult()
        );
    }

    @Test
    void eliminateDeadBranch() {
        generateInto(
                sut,
                (Seq) translateToAst(
                        "print(a) while false print(b) end print(c)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "c"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void eliminateUnusedFunction() {
        generateInto(
                sut,
                (Seq) translateToAst(""
                        + "def a "
                        + "  print(\"here\") "
                        + "end "
                        + "while false "
                        + "  a() "
                        + "  a() "
                        + "end "
                        + "print(\"Done\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1001)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(PRINT, "\"Done\""),
                        createInstruction(END),
                        // det a -- removed
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1006))
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsUsedFunctions() {
        generateInto(
                sut,
                (Seq) translateToAst(""
                        + "allocate stack in cell1[0 .. 63] "
                        + "def testa(n) "
                        + "  print(\"Start\") "
                        + "end "
                        + "def testb(n) "
                        + "  print(\"Middle\") "
                        + "end "
                        + "def testc(n) "
                        + "  print(\"End\") "
                        + "end "
                        + "testa(0) "
                        + "testa(0) "
                        + "while false "
                        + "  testb(1) "
                        + "  testb(1) "
                        + "end "
                        + "testc(2) "
                        + "testc(2) "
                        + "printflush(message1)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        // call testa (2x)
                        createInstruction(SET, "__fn2retaddr", var(1003)),
                        createInstruction(SET, "@counter", var(1002)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, "__fn2retaddr", var(1004)),
                        createInstruction(SET, "@counter", var(1002)),
                        createInstruction(LABEL, var(1004)),
                        // if false + call testb -- removed
                        createInstruction(LABEL, var(1005)),
                        createInstruction(LABEL, var(1008)),
                        createInstruction(LABEL, var(1009)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1007)),
                        // call testc (2)
                        createInstruction(SET, "__fn1retaddr", var(1010)),
                        createInstruction(SET, "@counter", var(1001)),
                        createInstruction(LABEL, var(1010)),
                        createInstruction(SET, "__fn1retaddr", var(1011)),
                        createInstruction(SET, "@counter", var(1001)),
                        createInstruction(LABEL, var(1011)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END),
                        // def testb -- removed
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1012)),
                        // def testc
                        createInstruction(LABEL, var(1001)),
                        createInstruction(PRINT, "\"End\""),
                        createInstruction(LABEL, var(1013)),
                        createInstruction(SET, "@counter", "__fn1retaddr"),
                        createInstruction(END),
                        // def testa
                        createInstruction(LABEL, var(1002)),
                        createInstruction(PRINT, "\"Start\""),
                        createInstruction(LABEL, var(1014)),
                        createInstruction(SET, "@counter", "__fn2retaddr"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
