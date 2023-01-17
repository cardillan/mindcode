package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

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
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "while a while b print(b) end end"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(JUMP, var(1000), "equal", "a", "false"),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(JUMP, var(1001), "equal", "b", "false"),
                        new LogicInstruction(PRINT, "b"),
                        new LogicInstruction(JUMP, var(1003), "always"),
                        new LogicInstruction(LABEL, var(1004)),
                        new LogicInstruction(LABEL, var(1002))
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void eliminateDeadBranch() {
        LogicInstructionGenerator.generateInto(
                sut,
                (Seq) translateToAst(
                        "print(a) if false print(b) end print(c)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(PRINT, "c"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void eliminateUnusedFunction() {
        LogicInstructionGenerator.generateInto(
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
                        new LogicInstruction(SET, var(0), "63"),
                        new LogicInstruction(SET, var(1), "63"),
                        new LogicInstruction(WRITE, var(0), "cell1", var(1)),
                        new LogicInstruction(SET, var(2), "\"Start\""),
                        new LogicInstruction(PRINT, var(2)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(3), "\"End\""),
                        new LogicInstruction(PRINT, var(3)),
                        new LogicInstruction(PRINTFLUSH, "message1"),
                        new LogicInstruction(END),
                        new LogicInstruction(LABEL, var(1010))
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void keepsUsedFunctions() {
        LogicInstructionGenerator.generateInto(
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
                        new LogicInstruction(SET, var(0), "63"),
                        new LogicInstruction(SET, var(1), "63"),
                        new LogicInstruction(WRITE, var(0), "cell1", var(1)),
                        new LogicInstruction(SET, var(2), "0"),
                        new LogicInstruction(SET, var(4), "63"),
                        new LogicInstruction(READ, var(5), "cell1", var(4)),
                        new LogicInstruction(SET, var(3), var(5)),
                        new LogicInstruction(SET, var(6), "1"),
                        new LogicInstruction(OP, "sub", var(7), var(3), var(6)),
                        new LogicInstruction(SET, var(3), var(7)),
                        new LogicInstruction(WRITE, var(1003), "cell1", var(3)),
                        new LogicInstruction(SET, var(8), "63"),
                        new LogicInstruction(WRITE, var(3), "cell1", var(8)),
                        new LogicInstruction(SET, var(10), "63"),
                        new LogicInstruction(READ, var(11), "cell1", var(10)),
                        new LogicInstruction(SET, var(9), var(11)),
                        new LogicInstruction(SET, var(12), "1"),
                        new LogicInstruction(OP, "sub", var(13), var(9), var(12)),
                        new LogicInstruction(SET, var(9), var(13)),
                        new LogicInstruction(WRITE, var(2), "cell1", var(9)),
                        new LogicInstruction(SET, var(14), "63"),
                        new LogicInstruction(WRITE, var(9), "cell1", var(14)),
                        new LogicInstruction(SET, "@counter", var(1000)),
                        new LogicInstruction(LABEL, var(1003)),
                        new LogicInstruction(SET, var(17), "63"),
                        new LogicInstruction(READ, var(18), "cell1", var(17)),
                        new LogicInstruction(SET, var(16), var(18)),
                        new LogicInstruction(SET, var(20), "1"),
                        new LogicInstruction(OP, "add", var(21), var(16), var(20)),
                        new LogicInstruction(SET, var(16), var(21)),
                        new LogicInstruction(SET, var(22), "63"),
                        new LogicInstruction(WRITE, var(16), "cell1", var(22)),
                        new LogicInstruction(LABEL, var(1006)),
                        new LogicInstruction(LABEL, var(1004)),
                        new LogicInstruction(LABEL, var(1005)),
                        new LogicInstruction(SET, var(45), "2"),
                        new LogicInstruction(SET, var(47), "63"),
                        new LogicInstruction(READ, var(48), "cell1", var(47)),
                        new LogicInstruction(SET, var(46), var(48)),
                        new LogicInstruction(SET, var(49), "1"),
                        new LogicInstruction(OP, "sub", var(50), var(46), var(49)),
                        new LogicInstruction(SET, var(46), var(50)),
                        new LogicInstruction(WRITE, var(1007), "cell1", var(46)),
                        new LogicInstruction(SET, var(51), "63"),
                        new LogicInstruction(WRITE, var(46), "cell1", var(51)),
                        new LogicInstruction(SET, var(53), "63"),
                        new LogicInstruction(READ, var(54), "cell1", var(53)),
                        new LogicInstruction(SET, var(52), var(54)),
                        new LogicInstruction(SET, var(55), "1"),
                        new LogicInstruction(OP, "sub", var(56), var(52), var(55)),
                        new LogicInstruction(SET, var(52), var(56)),
                        new LogicInstruction(WRITE, var(45), "cell1", var(52)),
                        new LogicInstruction(SET, var(57), "63"),
                        new LogicInstruction(WRITE, var(52), "cell1", var(57)),
                        new LogicInstruction(SET, "@counter", var(1002)),
                        new LogicInstruction(LABEL, var(1007)),
                        new LogicInstruction(SET, var(60), "63"),
                        new LogicInstruction(READ, var(61), "cell1", var(60)),
                        new LogicInstruction(SET, var(59), var(61)),
                        new LogicInstruction(SET, var(63), "1"),
                        new LogicInstruction(OP, "add", var(64), var(59), var(63)),
                        new LogicInstruction(SET, var(59), var(64)),
                        new LogicInstruction(SET, var(65), "63"),
                        new LogicInstruction(WRITE, var(59), "cell1", var(65)),
                        new LogicInstruction(PRINTFLUSH, "message1"),
                        new LogicInstruction(END),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(LABEL, var(1002)),
                        new LogicInstruction(SET, var(91), "63"),
                        new LogicInstruction(READ, var(92), "cell1", var(91)),
                        new LogicInstruction(SET, var(90), var(92)),
                        new LogicInstruction(SET, var(94), "1"),
                        new LogicInstruction(OP, "add", var(95), var(90), var(94)),
                        new LogicInstruction(SET, var(90), var(95)),
                        new LogicInstruction(SET, var(96), "63"),
                        new LogicInstruction(WRITE, var(90), "cell1", var(96)),
                        new LogicInstruction(SET, var(97), "\"End\""),
                        new LogicInstruction(PRINT, var(97)),
                        new LogicInstruction(SET, var(100), "63"),
                        new LogicInstruction(READ, var(101), "cell1", var(100)),
                        new LogicInstruction(SET, var(99), var(101)),
                        new LogicInstruction(READ, var(102), "cell1", var(99)),
                        new LogicInstruction(SET, var(98), var(102)),
                        new LogicInstruction(SET, var(103), "1"),
                        new LogicInstruction(OP, "add", var(104), var(99), var(103)),
                        new LogicInstruction(SET, var(99), var(104)),
                        new LogicInstruction(SET, var(105), "63"),
                        new LogicInstruction(WRITE, var(99), "cell1", var(105)),
                        new LogicInstruction(SET, var(107), "63"),
                        new LogicInstruction(READ, var(108), "cell1", var(107)),
                        new LogicInstruction(SET, var(106), var(108)),
                        new LogicInstruction(SET, var(109), "1"),
                        new LogicInstruction(OP, "sub", var(110), var(106), var(109)),
                        new LogicInstruction(SET, var(106), var(110)),
                        new LogicInstruction(WRITE, var(97), "cell1", var(106)),
                        new LogicInstruction(SET, var(111), "63"),
                        new LogicInstruction(WRITE, var(106), "cell1", var(111)),
                        new LogicInstruction(SET, "@counter", var(98)),
                        new LogicInstruction(END),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(114), "63"),
                        new LogicInstruction(READ, var(115), "cell1", var(114)),
                        new LogicInstruction(SET, var(113), var(115)),
                        new LogicInstruction(SET, var(117), "1"),
                        new LogicInstruction(OP, "add", var(118), var(113), var(117)),
                        new LogicInstruction(SET, var(113), var(118)),
                        new LogicInstruction(SET, var(119), "63"),
                        new LogicInstruction(WRITE, var(113), "cell1", var(119)),
                        new LogicInstruction(SET, var(120), "\"Start\""),
                        new LogicInstruction(PRINT, var(120)),
                        new LogicInstruction(SET, var(123), "63"),
                        new LogicInstruction(READ, var(124), "cell1", var(123)),
                        new LogicInstruction(SET, var(122), var(124)),
                        new LogicInstruction(READ, var(125), "cell1", var(122)),
                        new LogicInstruction(SET, var(121), var(125)),
                        new LogicInstruction(SET, var(126), "1"),
                        new LogicInstruction(OP, "add", var(127), var(122), var(126)),
                        new LogicInstruction(SET, var(122), var(127)),
                        new LogicInstruction(SET, var(128), "63"),
                        new LogicInstruction(WRITE, var(122), "cell1", var(128)),
                        new LogicInstruction(SET, var(130), "63"),
                        new LogicInstruction(READ, var(131), "cell1", var(130)),
                        new LogicInstruction(SET, var(129), var(131)),
                        new LogicInstruction(SET, var(132), "1"),
                        new LogicInstruction(OP, "sub", var(133), var(129), var(132)),
                        new LogicInstruction(SET, var(129), var(133)),
                        new LogicInstruction(WRITE, var(120), "cell1", var(129)),
                        new LogicInstruction(SET, var(134), "63"),
                        new LogicInstruction(WRITE, var(129), "cell1", var(134)),
                        new LogicInstruction(SET, "@counter", var(121)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
