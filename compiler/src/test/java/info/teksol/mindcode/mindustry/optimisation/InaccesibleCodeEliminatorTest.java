package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

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
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("jump", var(1000), "notEqual", "a", "true"),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("jump", var(1001), "notEqual", "b", "true"),
                        new LogicInstruction("print", "b"),
                        new LogicInstruction("jump", var(1003), "always"),
                        new LogicInstruction("label", var(1004)),
                        new LogicInstruction("label", var(1002))
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
                        new LogicInstruction("print", "a"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("print", "c"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "63"),
                        new LogicInstruction("set", var(1), "63"),
                        new LogicInstruction("write", var(0), "cell1", var(1)),
                        new LogicInstruction("set", var(2), "\"Start\""),
                        new LogicInstruction("print", var(2)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(3), "\"End\""),
                        new LogicInstruction("print", var(3)),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),
                        new LogicInstruction("label", var(1010))
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
                        new LogicInstruction("set", var(0), "63"),
                        new LogicInstruction("set", var(1), "63"),
                        new LogicInstruction("write", var(0), "cell1", var(1)),
                        new LogicInstruction("set", var(2), "0"),
                        new LogicInstruction("set", var(4), "63"),
                        new LogicInstruction("read", var(5), "cell1", var(4)),
                        new LogicInstruction("set", var(3), var(5)),
                        new LogicInstruction("set", var(6), "1"),
                        new LogicInstruction("op", "sub", var(7), var(3), var(6)),
                        new LogicInstruction("set", var(3), var(7)),
                        new LogicInstruction("write", var(1003), "cell1", var(3)),
                        new LogicInstruction("set", var(8), "63"),
                        new LogicInstruction("write", var(3), "cell1", var(8)),
                        new LogicInstruction("set", var(10), "63"),
                        new LogicInstruction("read", var(11), "cell1", var(10)),
                        new LogicInstruction("set", var(9), var(11)),
                        new LogicInstruction("set", var(12), "1"),
                        new LogicInstruction("op", "sub", var(13), var(9), var(12)),
                        new LogicInstruction("set", var(9), var(13)),
                        new LogicInstruction("write", var(2), "cell1", var(9)),
                        new LogicInstruction("set", var(14), "63"),
                        new LogicInstruction("write", var(9), "cell1", var(14)),
                        new LogicInstruction("set", "@counter", var(1000)),
                        new LogicInstruction("label", var(1003)),
                        new LogicInstruction("set", var(17), "63"),
                        new LogicInstruction("read", var(18), "cell1", var(17)),
                        new LogicInstruction("set", var(16), var(18)),
                        new LogicInstruction("set", var(20), "1"),
                        new LogicInstruction("op", "add", var(21), var(16), var(20)),
                        new LogicInstruction("set", var(16), var(21)),
                        new LogicInstruction("set", var(22), "63"),
                        new LogicInstruction("write", var(16), "cell1", var(22)),
                        new LogicInstruction("label", var(1006)),
                        new LogicInstruction("label", var(1004)),
                        new LogicInstruction("label", var(1005)),
                        new LogicInstruction("set", var(45), "2"),
                        new LogicInstruction("set", var(47), "63"),
                        new LogicInstruction("read", var(48), "cell1", var(47)),
                        new LogicInstruction("set", var(46), var(48)),
                        new LogicInstruction("set", var(49), "1"),
                        new LogicInstruction("op", "sub", var(50), var(46), var(49)),
                        new LogicInstruction("set", var(46), var(50)),
                        new LogicInstruction("write", var(1007), "cell1", var(46)),
                        new LogicInstruction("set", var(51), "63"),
                        new LogicInstruction("write", var(46), "cell1", var(51)),
                        new LogicInstruction("set", var(53), "63"),
                        new LogicInstruction("read", var(54), "cell1", var(53)),
                        new LogicInstruction("set", var(52), var(54)),
                        new LogicInstruction("set", var(55), "1"),
                        new LogicInstruction("op", "sub", var(56), var(52), var(55)),
                        new LogicInstruction("set", var(52), var(56)),
                        new LogicInstruction("write", var(45), "cell1", var(52)),
                        new LogicInstruction("set", var(57), "63"),
                        new LogicInstruction("write", var(52), "cell1", var(57)),
                        new LogicInstruction("set", "@counter", var(1002)),
                        new LogicInstruction("label", var(1007)),
                        new LogicInstruction("set", var(60), "63"),
                        new LogicInstruction("read", var(61), "cell1", var(60)),
                        new LogicInstruction("set", var(59), var(61)),
                        new LogicInstruction("set", var(63), "1"),
                        new LogicInstruction("op", "add", var(64), var(59), var(63)),
                        new LogicInstruction("set", var(59), var(64)),
                        new LogicInstruction("set", var(65), "63"),
                        new LogicInstruction("write", var(59), "cell1", var(65)),
                        new LogicInstruction("printflush", "message1"),
                        new LogicInstruction("end"),
                        new LogicInstruction("label", var(1001)),
                        new LogicInstruction("label", var(1002)),
                        new LogicInstruction("set", var(91), "63"),
                        new LogicInstruction("read", var(92), "cell1", var(91)),
                        new LogicInstruction("set", var(90), var(92)),
                        new LogicInstruction("set", var(94), "1"),
                        new LogicInstruction("op", "add", var(95), var(90), var(94)),
                        new LogicInstruction("set", var(90), var(95)),
                        new LogicInstruction("set", var(96), "63"),
                        new LogicInstruction("write", var(90), "cell1", var(96)),
                        new LogicInstruction("set", var(97), "\"End\""),
                        new LogicInstruction("print", var(97)),
                        new LogicInstruction("set", var(100), "63"),
                        new LogicInstruction("read", var(101), "cell1", var(100)),
                        new LogicInstruction("set", var(99), var(101)),
                        new LogicInstruction("read", var(102), "cell1", var(99)),
                        new LogicInstruction("set", var(98), var(102)),
                        new LogicInstruction("set", var(103), "1"),
                        new LogicInstruction("op", "add", var(104), var(99), var(103)),
                        new LogicInstruction("set", var(99), var(104)),
                        new LogicInstruction("set", var(105), "63"),
                        new LogicInstruction("write", var(99), "cell1", var(105)),
                        new LogicInstruction("set", var(107), "63"),
                        new LogicInstruction("read", var(108), "cell1", var(107)),
                        new LogicInstruction("set", var(106), var(108)),
                        new LogicInstruction("set", var(109), "1"),
                        new LogicInstruction("op", "sub", var(110), var(106), var(109)),
                        new LogicInstruction("set", var(106), var(110)),
                        new LogicInstruction("write", var(97), "cell1", var(106)),
                        new LogicInstruction("set", var(111), "63"),
                        new LogicInstruction("write", var(106), "cell1", var(111)),
                        new LogicInstruction("set", "@counter", var(98)),
                        new LogicInstruction("end"),
                        new LogicInstruction("label", var(1000)),
                        new LogicInstruction("set", var(114), "63"),
                        new LogicInstruction("read", var(115), "cell1", var(114)),
                        new LogicInstruction("set", var(113), var(115)),
                        new LogicInstruction("set", var(117), "1"),
                        new LogicInstruction("op", "add", var(118), var(113), var(117)),
                        new LogicInstruction("set", var(113), var(118)),
                        new LogicInstruction("set", var(119), "63"),
                        new LogicInstruction("write", var(113), "cell1", var(119)),
                        new LogicInstruction("set", var(120), "\"Start\""),
                        new LogicInstruction("print", var(120)),
                        new LogicInstruction("set", var(123), "63"),
                        new LogicInstruction("read", var(124), "cell1", var(123)),
                        new LogicInstruction("set", var(122), var(124)),
                        new LogicInstruction("read", var(125), "cell1", var(122)),
                        new LogicInstruction("set", var(121), var(125)),
                        new LogicInstruction("set", var(126), "1"),
                        new LogicInstruction("op", "add", var(127), var(122), var(126)),
                        new LogicInstruction("set", var(122), var(127)),
                        new LogicInstruction("set", var(128), "63"),
                        new LogicInstruction("write", var(122), "cell1", var(128)),
                        new LogicInstruction("set", var(130), "63"),
                        new LogicInstruction("read", var(131), "cell1", var(130)),
                        new LogicInstruction("set", var(129), var(131)),
                        new LogicInstruction("set", var(132), "1"),
                        new LogicInstruction("op", "sub", var(133), var(129), var(132)),
                        new LogicInstruction("set", var(129), var(133)),
                        new LogicInstruction("write", var(120), "cell1", var(129)),
                        new LogicInstruction("set", var(134), "63"),
                        new LogicInstruction("write", var(129), "cell1", var(134)),
                        new LogicInstruction("set", "@counter", var(121)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }
}
