package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.LogicInstructionPipeline;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

public class OutputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = OptimizationPipeline.createPipelineOf(getInstructionProcessor(),
            terminus,
            getCompilerProfile(),
            Optimization.OUTPUT_TEMPS_ELIMINATION);


    @Test
    void optimizesBasicCase() {
        pipeline.emit(createInstruction(SENSOR, tmp0, vault1, coal));
        pipeline.emit(createInstruction(SET, var, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var, vault1, coal),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(createInstruction(SENSOR, var, vault1, coal));
        pipeline.emit(createInstruction(SET, result, var));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, var, vault1, coal),
                        createInstruction(SET, result, var),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(createInstruction(SENSOR, tmp0, vault1, coal));
        pipeline.emit(createInstruction(SET, result, tmp0));
        pipeline.emit(createInstruction(SET, another, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SET, another, tmp0),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(createInstruction(PRINT, K1));
        pipeline.emit(createInstruction(SET, result, tmp0));
        pipeline.emit(createInstruction(SENSOR, tmp0, vault1, coal));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(PRINT, K1),
                        createInstruction(SET, result, tmp0),
                        createInstruction(SENSOR, tmp0, vault1, coal),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesTwoSets() {
        pipeline.emit(createInstruction(SET, tmp0, a));
        pipeline.emit(createInstruction(SET, b, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, b, a),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresIncompatibleSets() {
        pipeline.emit(createInstruction(SET, tmp0, a));
        pipeline.emit(createInstruction(SET, tmp0, b));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SET, tmp0, a),
                        createInstruction(SET, tmp0, b),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresWrongArgumentType() {
        pipeline.emit(createInstruction(SENSOR, tmp1, vault1, tmp0));
        pipeline.emit(createInstruction(SET, result, tmp0));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, tmp1, vault1, tmp0),
                        createInstruction(SET, result, tmp0),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeGetlinkThenSetTest
    
    @Test
    void optimizesGetlinkFollowedBySet() {
        generateInto(pipeline, (Seq) translateToAst("target = getlink(0)"));

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
        pipeline.emit(createInstruction(GETLINK, a, K0));
        pipeline.emit(createInstruction(SET, b, K1));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, a, K0),
                        createInstruction(SET, b, K1)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        pipeline.emit(createInstruction(GETLINK, a, K0));
        pipeline.emit(createInstruction(GETLINK, b, K1));
        pipeline.emit(createInstruction(READ, c, cell1, K1));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, a, K0),
                        createInstruction(GETLINK, b, K1),
                        createInstruction(READ, c, cell1, K1)
                ),
                terminus.getResult()
        );
    }

    @Test
    void terminalGetlinkIsEmitted() {
        pipeline.emit(createInstruction(GETLINK, a, K0));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(GETLINK, a, K0)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeOpThenSetTest
    
    @Test
    void optimizesOpThenSet() {
        generateInto(pipeline, (Seq) translateToAst("r = rand(100)"));

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
        generateInto(pipeline, (Seq) translateToAst("state = min(max(state, MIN), MAX)"));

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
        generateInto(pipeline, (Seq) translateToAst("remaining = capacity - current"));

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
        pipeline.emit(createInstruction(READ, tmp0, cell1, K1));
        pipeline.emit(createInstruction(SET, tmp1, K1));
        pipeline.emit(createInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(READ, tmp0, cell1, K1),
                        createInstruction(SET, tmp1, K1),
                        createInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSensorThenSetTest
    
    @Test
    void optimizesSensorThenSet() {
        generateInto(pipeline, (Seq) translateToAst("numsilicon = STORAGE.silicon"));

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
                (Seq) translateToAst("""
                        numgraphite = container1.graphite
                        numcoal = container1.coal
                        """
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
        pipeline.emit(createInstruction(SENSOR, a, vault1, coal));
        pipeline.emit(createInstruction(SET, b, K1));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, a, vault1, coal),
                        createInstruction(SET, b, K1)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        pipeline.emit(createInstruction(SENSOR, a, vault1, coal));
        pipeline.emit(createInstruction(PRINT, a));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, a, vault1, coal),
                        createInstruction(PRINT, a)
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSensorsOptimizeCorrectly() {
        pipeline.emit(createInstruction(SENSOR, a, vault1, coal));
        pipeline.emit(createInstruction(SENSOR, b, conveyor1, enabled));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, a, vault1, coal),
                        createInstruction(SENSOR, b, conveyor1, enabled)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        pipeline.emit(createInstruction(SENSOR, a, vault1, coal));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        createInstruction(SENSOR, a, vault1, coal)
                ),
                terminus.getResult()
        );
    }
    
}
