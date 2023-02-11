package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class InaccesibleCodeEliminatorTest extends AbstractGeneratorTest {
    // Sequences of jumps are not generated without dead code elimination
    private final LogicInstructionPipeline sut = Optimisation.createPipelineOf(terminus, 
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
                (Seq) translateToAst("" +
                        "allocate stack in cell1[0 .. 63]\n" +
                        "def testfunc(n)\n" +
                        "  print(\"Here!\", n)\n" +
                        "end\n" +
                        "print(\"Start\")\n" +
                        "if false\n" +
                        "  testfunc(1)\n" +
                        "end\n" +
                        "print(\"End\")\n" +
                        "printflush(message1)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "63"),
                        createInstruction(SET, var(1), "63"),
                        createInstruction(WRITE, var(0), "cell1", var(1)),
                        createInstruction(SET, var(2), "\"Start\""),
                        createInstruction(PRINT, var(2)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(3), "\"End\""),
                        createInstruction(PRINT, var(3)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END),
                        createInstruction(LABEL, var(1010))
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void keepsUsedFunctions() {
        generateInto(
                sut,
                (Seq) translateToAst("" +
                        "allocate stack in cell1[0 .. 63]\n" +
                        "def testa(n)\n" +
                        "  print(\"Start\")\n" +
                        "end\n" +
                        "def testb(n)\n" +
                        "  print(\"Middle\")\n" +
                        "end\n" +
                        "def testc(n)\n" +
                        "  print(\"End\")\n" +
                        "end\n" +
                        "testa(0)\n" +
                        "if false\n" +
                        "  testb(1)\n" +
                        "end\n" +
                        "testc(2)\n" +
                        "printflush(message1)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, var(0), "63"),
                        createInstruction(SET, var(1), "63"),
                        createInstruction(WRITE, var(0), "cell1", var(1)),
                        createInstruction(SET, var(2), "0"),
                        createInstruction(SET, var(4), "63"),
                        createInstruction(READ, var(5), "cell1", var(4)),
                        createInstruction(SET, var(3), var(5)),
                        createInstruction(SET, var(6), "1"),
                        createInstruction(OP, "sub", var(7), var(3), var(6)),
                        createInstruction(SET, var(3), var(7)),
                        createInstruction(WRITE, var(1003), "cell1", var(3)),
                        createInstruction(SET, var(8), "63"),
                        createInstruction(WRITE, var(3), "cell1", var(8)),
                        createInstruction(SET, var(10), "63"),
                        createInstruction(READ, var(11), "cell1", var(10)),
                        createInstruction(SET, var(9), var(11)),
                        createInstruction(SET, var(12), "1"),
                        createInstruction(OP, "sub", var(13), var(9), var(12)),
                        createInstruction(SET, var(9), var(13)),
                        createInstruction(WRITE, var(2), "cell1", var(9)),
                        createInstruction(SET, var(14), "63"),
                        createInstruction(WRITE, var(9), "cell1", var(14)),
                        createInstruction(SET, "@counter", var(1000)),
                        createInstruction(LABEL, var(1003)),
                        createInstruction(SET, var(17), "63"),
                        createInstruction(READ, var(18), "cell1", var(17)),
                        createInstruction(SET, var(16), var(18)),
                        createInstruction(SET, var(20), "1"),
                        createInstruction(OP, "add", var(21), var(16), var(20)),
                        createInstruction(SET, var(16), var(21)),
                        createInstruction(SET, var(22), "63"),
                        createInstruction(WRITE, var(16), "cell1", var(22)),
                        createInstruction(LABEL, var(1006)),
                        createInstruction(LABEL, var(1004)),
                        createInstruction(LABEL, var(1005)),
                        createInstruction(SET, var(45), "2"),
                        createInstruction(SET, var(47), "63"),
                        createInstruction(READ, var(48), "cell1", var(47)),
                        createInstruction(SET, var(46), var(48)),
                        createInstruction(SET, var(49), "1"),
                        createInstruction(OP, "sub", var(50), var(46), var(49)),
                        createInstruction(SET, var(46), var(50)),
                        createInstruction(WRITE, var(1007), "cell1", var(46)),
                        createInstruction(SET, var(51), "63"),
                        createInstruction(WRITE, var(46), "cell1", var(51)),
                        createInstruction(SET, var(53), "63"),
                        createInstruction(READ, var(54), "cell1", var(53)),
                        createInstruction(SET, var(52), var(54)),
                        createInstruction(SET, var(55), "1"),
                        createInstruction(OP, "sub", var(56), var(52), var(55)),
                        createInstruction(SET, var(52), var(56)),
                        createInstruction(WRITE, var(45), "cell1", var(52)),
                        createInstruction(SET, var(57), "63"),
                        createInstruction(WRITE, var(52), "cell1", var(57)),
                        createInstruction(SET, "@counter", var(1002)),
                        createInstruction(LABEL, var(1007)),
                        createInstruction(SET, var(60), "63"),
                        createInstruction(READ, var(61), "cell1", var(60)),
                        createInstruction(SET, var(59), var(61)),
                        createInstruction(SET, var(63), "1"),
                        createInstruction(OP, "add", var(64), var(59), var(63)),
                        createInstruction(SET, var(59), var(64)),
                        createInstruction(SET, var(65), "63"),
                        createInstruction(WRITE, var(59), "cell1", var(65)),
                        createInstruction(PRINTFLUSH, "message1"),
                        createInstruction(END),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(LABEL, var(1002)),
                        createInstruction(SET, var(91), "63"),
                        createInstruction(READ, var(92), "cell1", var(91)),
                        createInstruction(SET, var(90), var(92)),
                        createInstruction(SET, var(94), "1"),
                        createInstruction(OP, "add", var(95), var(90), var(94)),
                        createInstruction(SET, var(90), var(95)),
                        createInstruction(SET, var(96), "63"),
                        createInstruction(WRITE, var(90), "cell1", var(96)),
                        createInstruction(SET, var(97), "\"End\""),
                        createInstruction(PRINT, var(97)),
                        createInstruction(SET, var(100), "63"),
                        createInstruction(READ, var(101), "cell1", var(100)),
                        createInstruction(SET, var(99), var(101)),
                        createInstruction(READ, var(102), "cell1", var(99)),
                        createInstruction(SET, var(98), var(102)),
                        createInstruction(SET, var(103), "1"),
                        createInstruction(OP, "add", var(104), var(99), var(103)),
                        createInstruction(SET, var(99), var(104)),
                        createInstruction(SET, var(105), "63"),
                        createInstruction(WRITE, var(99), "cell1", var(105)),
                        createInstruction(SET, var(107), "63"),
                        createInstruction(READ, var(108), "cell1", var(107)),
                        createInstruction(SET, var(106), var(108)),
                        createInstruction(SET, var(109), "1"),
                        createInstruction(OP, "sub", var(110), var(106), var(109)),
                        createInstruction(SET, var(106), var(110)),
                        createInstruction(WRITE, var(97), "cell1", var(106)),
                        createInstruction(SET, var(111), "63"),
                        createInstruction(WRITE, var(106), "cell1", var(111)),
                        createInstruction(SET, "@counter", var(98)),
                        createInstruction(END),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(114), "63"),
                        createInstruction(READ, var(115), "cell1", var(114)),
                        createInstruction(SET, var(113), var(115)),
                        createInstruction(SET, var(117), "1"),
                        createInstruction(OP, "add", var(118), var(113), var(117)),
                        createInstruction(SET, var(113), var(118)),
                        createInstruction(SET, var(119), "63"),
                        createInstruction(WRITE, var(113), "cell1", var(119)),
                        createInstruction(SET, var(120), "\"Start\""),
                        createInstruction(PRINT, var(120)),
                        createInstruction(SET, var(123), "63"),
                        createInstruction(READ, var(124), "cell1", var(123)),
                        createInstruction(SET, var(122), var(124)),
                        createInstruction(READ, var(125), "cell1", var(122)),
                        createInstruction(SET, var(121), var(125)),
                        createInstruction(SET, var(126), "1"),
                        createInstruction(OP, "add", var(127), var(122), var(126)),
                        createInstruction(SET, var(122), var(127)),
                        createInstruction(SET, var(128), "63"),
                        createInstruction(WRITE, var(122), "cell1", var(128)),
                        createInstruction(SET, var(130), "63"),
                        createInstruction(READ, var(131), "cell1", var(130)),
                        createInstruction(SET, var(129), var(131)),
                        createInstruction(SET, var(132), "1"),
                        createInstruction(OP, "sub", var(133), var(129), var(132)),
                        createInstruction(SET, var(129), var(133)),
                        createInstruction(WRITE, var(120), "cell1", var(129)),
                        createInstruction(SET, var(134), "63"),
                        createInstruction(WRITE, var(129), "cell1", var(134)),
                        createInstruction(SET, "@counter", var(121)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
