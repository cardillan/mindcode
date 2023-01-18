package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

public class InputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.INPUT_TEMPS_ELIMINATION);

    @Test
    void optimizesBasicCase() {
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "0"));
        pipeline.emit(new LogicInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(DRAW, "color", "0", "0", "0", "255"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(new LogicInstruction(SET, "C", "0"));
        pipeline.emit(new LogicInstruction(DRAW, "color", "C", "C", "C", "255"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "C", "0"),
                        new LogicInstruction(DRAW, "color", "C", "C", "C", "255"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "0"));
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "1"));
        pipeline.emit(new LogicInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "__tmp0", "0"),
                        new LogicInstruction(SET, "__tmp0", "1"),
                        new LogicInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(new LogicInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "0"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"),
                        new LogicInstruction(SET, "__tmp0", "0"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "0"));
        pipeline.emit(new LogicInstruction(GETLINK, "__tmp0", "3"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "__tmp0", "0"),
                        new LogicInstruction(GETLINK, "__tmp0", "3"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesDrawingCode() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "color(255,80,80,255)\n" +
                        "rect(x, y3, 14, 8)\n" +
                        "color(0,0,0,255)\n" +
                        "rect(x + 2, y + 5, 8, 4)\n" +
                        "rect(x12, y3, 2, 2)\n" +
                        "rect(x12, y + 9, 2, 2)\n" +
                        "color(255,80,80,255)\n" +
                        "rect(x + 4, y + 6, 2, 2)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(DRAW, "color", "255", "80", "80", "255"),
                        new LogicInstruction(DRAW, "rect", "x", "y3", "14", "8"),
                        new LogicInstruction(DRAW, "color", "0", "0", "0", "255"),
                        new LogicInstruction(OP, "add", var(7), "x", "2"),
                        new LogicInstruction(OP, "add", var(9), "y", "5"),
                        new LogicInstruction(DRAW, "rect", var(7), var(9), "8", "4"),
                        new LogicInstruction(DRAW, "rect", "x12", "y3", "2", "2"),
                        new LogicInstruction(OP, "add", var(11), "y", "9"),
                        new LogicInstruction(DRAW, "rect", "x12", var(11), "2", "2"),
                        new LogicInstruction(DRAW, "color", "255", "80", "80", "255"),
                        new LogicInstruction(OP, "add", var(13), "x", "4"),
                        new LogicInstruction(OP, "add", var(15), "y", "6"),
                        new LogicInstruction(DRAW, "rect", var(13), var(15), "2", "2"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeSetThenWriteTest

    @Test
    void optimizesSetThenWriteValueOut() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "cell1[42] = 36"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(WRITE, "36", "cell1", "42"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void allowsUsingVariableForCell() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "RAM = cell14\nRAM[21] = 17"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "RAM", "cell14"),
                        new LogicInstruction(WRITE, "17", "RAM", "21"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeSetThenReadTest

    @Test
    void correctlyOptimizesReadAtFixedAddress() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "foo = cell1[14]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(READ, var(0), "cell1", "14"),
                        new LogicInstruction(SET, "foo", var(0)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyOptimizesHeapAccess() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "allocate heap in cell2[0 ... 4]\ntargetx = @thisx + $offsetx\ntargety = @thisy + $offsety\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(READ, var(0), "cell2", "0"),
                        new LogicInstruction(OP, "add", var(1), "@thisx", var(0)),
                        new LogicInstruction(SET, "targetx", var(1)),
                        new LogicInstruction(READ, var(2), "cell2", "1"),
                        new LogicInstruction(OP, "add", var(3), "@thisy", var(2)),
                        new LogicInstruction(SET, "targety", var(3)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsTerminalSet() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "bar = foo"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "bar", "foo"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyHandlesSetThenReadWithDifferingValues() {
        pipeline.emit(new LogicInstruction(SET, "x", "1"));
        pipeline.emit(new LogicInstruction(READ, "__tmp0", "cell2", "14"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "1"),
                        new LogicInstruction(READ, var(0), "cell2", "14")
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenOpTest
    
    @Test
    void optimizesSetThenOp() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "FLAG = floor(rand(10000))\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "rand", var(1), "10000"),
                        new LogicInstruction(OP, "floor", var(2), var(1)),
                        new LogicInstruction(SET, "FLAG", var(2)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenOpWhenOpDoesNotUseSetAlone() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "r = 2\np = rand(500)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "r", "2"),
                        new LogicInstruction(OP, "rand", var(1), "500"),
                        new LogicInstruction(SET, "p", var(1)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpFirst() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 41\npos = 70 + x"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "41"),
                        new LogicInstruction(OP, "add", var(2), "70", "x"),
                        new LogicInstruction(SET, "pos", var(2)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpLast() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 41\npos = x + 70"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "41"),
                        new LogicInstruction(OP, "add", var(2), "x", "70"),
                        new LogicInstruction(SET, "pos", var(2)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenSetTest

    @Test
    void consecutiveSetsThatRelateToEachOtherCollapse() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = 1\nb = a\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "a", "1"),
                        new LogicInstruction(SET, "b", "a"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsThatDoNotRelateAreLeftAlone() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = 1\nb = 2\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "a", "1"),
                        new LogicInstruction(SET, "b", "2"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsWithNoRelationshipAreLeftAlone() {
        pipeline.emit(new LogicInstruction(SET, "x", "10"));
        pipeline.emit(new LogicInstruction(SET, "y", "4"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "10"),
                        new LogicInstruction(SET, "y", "4")
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenPrintTest
    
    @Test
    void optimizesSetThenPrint() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a: \", a)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"a: \""),
                        new LogicInstruction(PRINT, "a"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesSetThenRead() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = cell4[10]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(READ, var(1), "cell4", "10"),
                        new LogicInstruction(SET, "a", var(1)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenPrintUnrelatedUndisturbed() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 1\nprint(y)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "1"),
                        new LogicInstruction(PRINT, "y"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyOptimizesPrintPipelines() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"damaged at \", dmgx, \", \", dmgy)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "\"damaged at \""),
                        new LogicInstruction(PRINT, "dmgx"),
                        new LogicInstruction(PRINT, "\", \""),
                        new LogicInstruction(PRINT, "dmgy"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyLeavesMultipleInstructionsAlone() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 1\nprint(\"damaged at \", dmgx)\nx = 2\nprint(\", \", dmgy)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "x", "1"),
                        new LogicInstruction(PRINT, "\"damaged at \""),
                        new LogicInstruction(PRINT, "dmgx"),
                        new LogicInstruction(SET, "x", "2"),
                        new LogicInstruction(PRINT, "\", \""),
                        new LogicInstruction(PRINT, "dmgy"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyLeavesTwoSetsOnSameTargetAlone() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("if x print(x) end")
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(JUMP, var(1000), "equal", "x", "false"),
                        new LogicInstruction(PRINT, "x"),
                        new LogicInstruction(SET, var(0), "x"),
                        new LogicInstruction(JUMP, var(1001), "always"),
                        new LogicInstruction(LABEL, var(1000)),
                        new LogicInstruction(SET, var(0), "null"),
                        new LogicInstruction(LABEL, var(1001)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
