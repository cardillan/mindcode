package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

public class OutputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            Optimization.OUTPUT_TEMPS_ELIMINATION);

    @Test
    void optimizesBasicCase() {
        pipeline.emit(createInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(createInstruction(SET, "result", "__tmp0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "result", "vault1", "@silicon"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(createInstruction(SENSOR, "var", "vault1", "@silicon"));
        pipeline.emit(createInstruction(SET, "result", "var"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "var", "vault1", "@silicon"),
                        createInstruction(SET, "result", "var"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(createInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(createInstruction(SET, "result", "__tmp0"));
        pipeline.emit(createInstruction(SET, "another", "__tmp0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "__tmp0", "vault1", "@silicon"),
                        createInstruction(SET, "result", "__tmp0"),
                        createInstruction(SET, "another", "__tmp0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(createInstruction(PRINT, "1"));
        pipeline.emit(createInstruction(SET, "result", "__tmp0"));
        pipeline.emit(createInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, "1"),
                        createInstruction(SET, "result", "__tmp0"),
                        createInstruction(SENSOR, "__tmp0", "vault1", "@silicon"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesTwoSets() {
        pipeline.emit(createInstruction(SET, "__tmp0", "x"));
        pipeline.emit(createInstruction(SET, "y", "__tmp0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "y", "x"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresIncompatibleSets() {
        pipeline.emit(createInstruction(SET, "__tmp0", "x"));
        pipeline.emit(createInstruction(SET, "__tmp0", "y"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, "__tmp0", "x"),
                        createInstruction(SET, "__tmp0", "y"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        pipeline.emit(createInstruction(SENSOR, "__tmp1", "vault1", "__tmp0"));
        pipeline.emit(createInstruction(SET, "result", "__tmp0"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "__tmp1", "vault1", "__tmp0"),
                        createInstruction(SET, "result", "__tmp0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeGetlinkThenSetTest
    
    @Test
    void optimizesGetlinkFollowedBySet() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "target = getlink(0)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, "target", "0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedBySetForDifferentStuffAlone() {
        pipeline.emit(createInstruction(GETLINK, "a", "0"));
        pipeline.emit(createInstruction(SET, "b", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, "a", "0"),
                        createInstruction(SET, "b", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        pipeline.emit(createInstruction(GETLINK, "a", "0"));
        pipeline.emit(createInstruction(GETLINK, "b", "1"));
        pipeline.emit(createInstruction(READ, "c", "cell1", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, "a", "0"),
                        createInstruction(GETLINK, "b", "1"),
                        createInstruction(READ, "c", "cell1", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void terminalGetlinkIsEmitted() {
        pipeline.emit(createInstruction(GETLINK, "a", "0"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, "a", "0")
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeOpThenSetTest
    
    @Test
    void optimizesOpThenSet() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "r = rand(100)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "rand", "r", "100"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void optimizesOpOpThenSet() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "state = min(max(state, MIN), MAX)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "max", var(0), "state", "MIN"),
                        createInstruction(OP, "min", "state", var(0), "MAX"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesOpThenSetFromBinaryOp() {
        generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "remaining = capacity - current"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(OP, "sub", "remaining", "capacity", "current"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeReadThenSetTest
    
    @Test
    void improvesReadThenSet() {
        generateInto(pipeline, (Seq) translateToAst("boo = cell1[0]"));

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, "boo", "cell1", "0"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesReadThenSetButOtherValueAlone() {
        pipeline.emit(createInstruction(READ, "__tmp0", "cell1", "14"));
        pipeline.emit(createInstruction(SET, "__tmp2", "14"));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, "__tmp0", "cell1", "14"),
                        createInstruction(SET, "__tmp2", "14"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSensorThenSetTest
    
    @Test
    void optimizesSensorThenSet() {
        generateInto(pipeline,
                (Seq) translateToAst(
                        "numsilicon = STORAGE.silicon"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "numsilicon", "STORAGE", "@silicon"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void supportsConsecutiveSensors() {
        generateInto(pipeline,
                (Seq) translateToAst(
                        "numgraphite = container1.graphite\nnumcoal = container1.coal"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "numgraphite", "container1", "@graphite"),
                        createInstruction(SENSOR, "numcoal", "container1", "@coal"),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorThenSetWithUnrelatedAlone() {
        pipeline.emit(createInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.emit(createInstruction(SET, "n", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "numsil", "vault1", "@silicon"),
                        createInstruction(SET, "n", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        pipeline.emit(createInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.emit(createInstruction(PRINT, "numsil"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "numsil", "vault1", "@silicon"),
                        createInstruction(PRINT, "numsil")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSensorsOptimizeCorrectly() {
        pipeline.emit(createInstruction(SENSOR, "a", "vault1", "@silicon"));
        pipeline.emit(createInstruction(SENSOR, "b", "conveyor1", "@enabled"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "a", "vault1", "@silicon"),
                        createInstruction(SENSOR, "b", "conveyor1", "@enabled")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        pipeline.emit(createInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, "numsil", "vault1", "@silicon")
                ),
                terminus.getResult()
        );
    }
    
}
