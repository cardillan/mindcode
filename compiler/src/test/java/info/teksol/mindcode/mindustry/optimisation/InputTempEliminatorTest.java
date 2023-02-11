package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.logic.Opcode.*;

public class InputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.INPUT_TEMPS_ELIMINATION);

    @Test
    void optimizesBasicCase() {
        pipeline.emit(createInstruction(SET, "__tmp0", "0"));
        pipeline.emit(createInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, "color", "0", "0", "0", "255"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(createInstruction(SET, "C", "0"));
        pipeline.emit(createInstruction(DRAW, "color", "C", "C", "C", "255"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "C", "0"),
                        createInstruction(DRAW, "color", "C", "C", "C", "255"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(createInstruction(SET, "__tmp0", "0"));
        pipeline.emit(createInstruction(SET, "__tmp0", "1"));
        pipeline.emit(createInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__tmp0", "0"),
                        createInstruction(SET, "__tmp0", "1"),
                        createInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(createInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"));
        pipeline.emit(createInstruction(SET, "__tmp0", "0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, "color", "__tmp0", "__tmp0", "__tmp0", "255"),
                        createInstruction(SET, "__tmp0", "0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        pipeline.emit(createInstruction(SET, "__tmp0", "0"));
        pipeline.emit(createInstruction(GETLINK, "__tmp0", "3"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__tmp0", "0"),
                        createInstruction(GETLINK, "__tmp0", "3"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesDrawingCode() {
        generateInto(
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
                        createInstruction(DRAW, "color", "255", "80", "80", "255"),
                        createInstruction(DRAW, "rect", "x", "y3", "14", "8"),
                        createInstruction(DRAW, "color", "0", "0", "0", "255"),
                        createInstruction(OP, "add", var(7), "x", "2"),
                        createInstruction(OP, "add", var(9), "y", "5"),
                        createInstruction(DRAW, "rect", var(7), var(9), "8", "4"),
                        createInstruction(DRAW, "rect", "x12", "y3", "2", "2"),
                        createInstruction(OP, "add", var(11), "y", "9"),
                        createInstruction(DRAW, "rect", "x12", var(11), "2", "2"),
                        createInstruction(DRAW, "color", "255", "80", "80", "255"),
                        createInstruction(OP, "add", var(13), "x", "4"),
                        createInstruction(OP, "add", var(15), "y", "6"),
                        createInstruction(DRAW, "rect", var(13), var(15), "2", "2"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeSetThenWriteTest

    @Test
    void optimizesSetThenWriteValueOut() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "cell1[42] = 36"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(WRITE, "36", "cell1", "42"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void allowsUsingVariableForCell() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "RAM = cell14\nRAM[21] = 17"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "RAM", "cell14"),
                        createInstruction(WRITE, "17", "RAM", "21"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeSetThenReadTest

    @Test
    void correctlyOptimizesReadAtFixedAddress() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "foo = cell1[14]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(0), "cell1", "14"),
                        createInstruction(SET, "foo", var(0)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyOptimizesHeapAccess() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "allocate heap in cell2[0 ... 4]\ntargetx = @thisx + $offsetx\ntargety = @thisy + $offsety\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(0), "cell2", "0"),
                        createInstruction(OP, "add", var(1), "@thisx", var(0)),
                        createInstruction(SET, "targetx", var(1)),
                        createInstruction(READ, var(2), "cell2", "1"),
                        createInstruction(OP, "add", var(3), "@thisy", var(2)),
                        createInstruction(SET, "targety", var(3)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void keepsTerminalSet() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "bar = foo"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "bar", "foo"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyHandlesSetThenReadWithDifferingValues() {
        pipeline.emit(createInstruction(SET, "x", "1"));
        pipeline.emit(createInstruction(READ, "__tmp0", "cell2", "14"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "1"),
                        createInstruction(READ, var(0), "cell2", "14")
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenOpTest
    
    @Test
    void optimizesSetThenOp() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "FLAG = floor(rand(10000))\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "rand", var(1), "10000"),
                        createInstruction(OP, "floor", var(2), var(1)),
                        createInstruction(SET, "FLAG", var(2)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenOpWhenOpDoesNotUseSetAlone() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "r = 2\np = rand(500)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "r", "2"),
                        createInstruction(OP, "rand", var(1), "500"),
                        createInstruction(SET, "p", var(1)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpFirst() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 41\npos = 70 + x"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "41"),
                        createInstruction(OP, "add", var(2), "70", "x"),
                        createInstruction(SET, "pos", var(2)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizeSetThenOpWithBinaryOpLast() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 41\npos = x + 70"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "41"),
                        createInstruction(OP, "add", var(2), "x", "70"),
                        createInstruction(SET, "pos", var(2)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenSetTest

    @Test
    void consecutiveSetsThatRelateToEachOtherCollapse() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = 1\nb = a\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", "1"),
                        createInstruction(SET, "b", "a"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsThatDoNotRelateAreLeftAlone() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = 1\nb = 2\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "a", "1"),
                        createInstruction(SET, "b", "2"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSetsWithNoRelationshipAreLeftAlone() {
        pipeline.emit(createInstruction(SET, "x", "10"));
        pipeline.emit(createInstruction(SET, "y", "4"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "10"),
                        createInstruction(SET, "y", "4")
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSetThenPrintTest
    
    @Test
    void optimizesSetThenPrint() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"a: \", a)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"a: \""),
                        createInstruction(PRINT, "a"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesSetThenRead() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "a = cell4[10]"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, var(1), "cell4", "10"),
                        createInstruction(SET, "a", var(1)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSetThenPrintUnrelatedUndisturbed() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 1\nprint(y)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "1"),
                        createInstruction(PRINT, "y"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyOptimizesPrintPipelines() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "print(\"damaged at \", dmgx, \", \", dmgy)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "\"damaged at \""),
                        createInstruction(PRINT, "dmgx"),
                        createInstruction(PRINT, "\", \""),
                        createInstruction(PRINT, "dmgy"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyLeavesMultipleInstructionsAlone() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "x = 1\nprint(\"damaged at \", dmgx)\nx = 2\nprint(\", \", dmgy)\n"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "x", "1"),
                        createInstruction(PRINT, "\"damaged at \""),
                        createInstruction(PRINT, "dmgx"),
                        createInstruction(SET, "x", "2"),
                        createInstruction(PRINT, "\", \""),
                        createInstruction(PRINT, "dmgy"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void correctlyLeavesTwoSetsOnSameTargetAlone() {
        generateInto(
                pipeline,
                (Seq) translateToAst("if x print(x) end")
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(JUMP, var(1000), "equal", "x", "false"),
                        createInstruction(PRINT, "x"),
                        createInstruction(SET, var(0), "x"),
                        createInstruction(JUMP, var(1001), "always"),
                        createInstruction(LABEL, var(1000)),
                        createInstruction(SET, var(0), "null"),
                        createInstruction(LABEL, var(1001)),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
}
