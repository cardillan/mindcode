package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class InaccesibleCodeEliminatorTest extends AbstractGeneratorTest {

    // Sequences of jumps are not generated without dead code elimination
    private final LogicInstructionPipeline sut = OptimisationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimisation.CONDITIONAL_JUMPS_NORMALIZATION,
            Optimisation.DEAD_CODE_ELIMINATION,
            Optimisation.SINGLE_STEP_JUMP_ELIMINATION,
            Optimisation.JUMP_TARGET_PROPAGATION,
            Optimisation.INACCESSIBLE_CODE_ELIMINATION
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
                        "print(a) if false print(b) end print(c)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "a"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(LABEL, var(1001)),
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
                        + "if false "
                        + "  a() "
                        + "  a() "
                        + "end "
                        + "print(\"Done\")"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(3), "\"Done\""),
                        createInstruction(PRINT, var(3)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000))
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
                        + "if false "
                        + "  testb(1) "
                        + "end "
                        + "testc(2) "
                        + "printflush(message1)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(1), "\"Start\""),
                        createInstruction(PRINT, var(1)),
                        createInstruction(LABEL, "__label0"),
                        createInstruction(LABEL, "__label1"),
                        createInstruction(SET, var(2), "\"End\""),
                        createInstruction(PRINT, var(2)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
