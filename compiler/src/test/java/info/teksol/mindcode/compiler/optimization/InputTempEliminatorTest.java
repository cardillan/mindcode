package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.Condition;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.Operation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class InputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.INPUT_TEMPS_ELIMINATION);


    @Test
    void optimizesBasicCase() {
        pipeline.emit(createInstruction(SET, tmp0, K0));
        pipeline.emit(createInstruction(DRAW, color, tmp0, tmp0, tmp0, K255));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, color, K0, K0, K0, K255),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(createInstruction(SET, C, K0));
        pipeline.emit(createInstruction(DRAW, color, C, C, C, K255));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, C, K0),
                        createInstruction(DRAW, color, C, C, C, K255),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(createInstruction(SET, tmp0, K0));
        pipeline.emit(createInstruction(SET, tmp0, K1));
        pipeline.emit(createInstruction(DRAW, color, tmp0, tmp0, tmp0, K255));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, tmp0, K0),
                        createInstruction(SET, tmp0, K1),
                        createInstruction(DRAW, color, tmp0, tmp0, tmp0, K255),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(createInstruction(DRAW, color, tmp0, tmp0, tmp0, K255));
        pipeline.emit(createInstruction(SET, tmp0, K0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(DRAW, color, tmp0, tmp0, tmp0, K255),
                        createInstruction(SET, tmp0, K0),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        pipeline.emit(createInstruction(SET, tmp0, K0));
        pipeline.emit(createInstruction(GETLINK, tmp0, K1));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, tmp0, K0),
                        createInstruction(GETLINK, tmp0, K1),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesDrawingCode() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        color(255,80,80,255)
                        rect(x, y3, 14, 8)
                        color(0,0,0,255)
                        rect(x + 2, y + 5, 8, 4)
                        rect(x12, y3, 2, 2)
                        rect(x12, y + 9, 2, 2)
                        color(255,80,80,255)
                        rect(x + 4, y + 6, 2, 2)
                        """
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
        generateInto(pipeline, (Seq) translateToAst("cell1[42] = 36"));

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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        RAM = cell14
                        RAM[21] = 17
                        """
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
        generateInto(pipeline, (Seq) translateToAst("foo = cell1[14]"));

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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        allocate heap in cell2[0 ... 4]
                        targetx = @thisx + $offsetx
                        targety = @thisy + $offsety
                        """
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
        generateInto(pipeline, (Seq) translateToAst("bar = foo"));

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "bar", "foo"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeSetThenOpTest
    
    @Test
    void optimizesSetThenOp() {
        generateInto(pipeline, (Seq) translateToAst("FLAG = floor(rand(10000))\n"));

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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        r = 2
                        p = rand(500)
                        """
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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        x = 41
                        pos = 70 + x
                        """
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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        x = 41
                        pos = x + 70
                        """
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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        a = 1
                        b = a
                        """
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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        a = 1
                        b = 2
                        """
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
        List<LogicInstruction> sequence = List.of(
                createInstruction(SET, a, K0),
                createInstruction(SET, b, K1)
        );

        sequence.forEach(pipeline::emit);
        pipeline.flush();

        assertLogicInstructionsMatch(sequence, terminus.getResult());
    }
    
    // Taken from OptimizeSetThenPrintTest
    
    @Test
    void optimizesSetThenPrint() {
        generateInto(pipeline,
                (Seq) translateToAst("""
                        print("a: ", a)
                        """
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
        generateInto(pipeline, (Seq) translateToAst("a = cell4[10]"));

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
        generateInto(pipeline, (Seq) translateToAst("x = 1\nprint(y)"));

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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        print("damaged at ", dmgx, ", ", dmgy)
                        """
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
        generateInto(pipeline,
                (Seq) translateToAst("""
                        x = 1
                        print("damaged at ", dmgx)
                        x = 2
                        print(", ", dmgy)
                        """
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
        generateInto(pipeline,
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
