package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import java.util.List;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.mindustry.Opcode.*;

public class OutputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.OUTPUT_TEMPS_ELIMINATION);

    @Test
    void optimizesBasicCase() {
        pipeline.emit(new LogicInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(SET, "result", "__tmp0"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "result", "vault1", "@silicon"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(new LogicInstruction(SENSOR, "var", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(SET, "result", "var"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "var", "vault1", "@silicon"),
                        new LogicInstruction(SET, "result", "var"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(new LogicInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(SET, "result", "__tmp0"));
        pipeline.emit(new LogicInstruction(SET, "another", "__tmp0"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "__tmp0", "vault1", "@silicon"),
                        new LogicInstruction(SET, "result", "__tmp0"),
                        new LogicInstruction(SET, "another", "__tmp0"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(new LogicInstruction(PRINT, "1"));
        pipeline.emit(new LogicInstruction(SET, "result", "__tmp0"));
        pipeline.emit(new LogicInstruction(SENSOR, "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(PRINT, "1"),
                        new LogicInstruction(SET, "result", "__tmp0"),
                        new LogicInstruction(SENSOR, "__tmp0", "vault1", "@silicon"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesTwoSets() {
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "x"));
        pipeline.emit(new LogicInstruction(SET, "y", "__tmp0"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "y", "x"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresIncompatibleSets() {
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "x"));
        pipeline.emit(new LogicInstruction(SET, "__tmp0", "y"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, "__tmp0", "x"),
                        new LogicInstruction(SET, "__tmp0", "y"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeGetlinkThenSetTest
    
    @Test
    void optimizesGetlinkFollowedBySet() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "target = getlink(0)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(GETLINK, "target", var(0)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedBySetForDifferentStuffAlone() {
        pipeline.emit(new LogicInstruction(GETLINK, "a", "0"));
        pipeline.emit(new LogicInstruction(SET, "b", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(GETLINK, "a", "0"),
                        new LogicInstruction(SET, "b", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        pipeline.emit(new LogicInstruction(GETLINK, "a", "0"));
        pipeline.emit(new LogicInstruction(GETLINK, "b", "1"));
        pipeline.emit(new LogicInstruction(READ, "c", "cell1", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(GETLINK, "a", "0"),
                        new LogicInstruction(GETLINK, "b", "1"),
                        new LogicInstruction(READ, "c", "cell1", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void terminalGetlinkIsEmitted() {
        pipeline.emit(new LogicInstruction(GETLINK, "a", "0"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(GETLINK, "a", "0")
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeOpThenSetTest
    
    @Test
    void optimizesOpThenSet() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "r = rand(100)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "100"),
                        new LogicInstruction(OP, "rand", "r", var(0)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    @Test
    void optimizesOpOpThenSet() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "state = min(max(state, MIN), MAX)"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "max", var(0), "state", "MIN"),
                        new LogicInstruction(OP, "min", "state", var(0), "MAX"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void optimizesOpThenSetFromBinaryOp() {
        LogicInstructionGenerator.generateInto(
                pipeline,
                (Seq) translateToAst("" +
                        "remaining = capacity - current"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(OP, "sub", "remaining", "capacity", "current"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    // Taken from OptimizeReadThenSetTest
    
    @Test
    void improvesReadThenSet() {
        LogicInstructionGenerator.generateInto(pipeline, (Seq) translateToAst("boo = cell1[0]"));

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SET, var(0), "0"),
                        new LogicInstruction(READ, "boo", "cell1", var(0)),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesReadThenSetButOtherValueAlone() {
        pipeline.emit(new LogicInstruction(READ, "__tmp0", "cell1", "14"));
        pipeline.emit(new LogicInstruction(SET, "__tmp2", "14"));
        pipeline.emit(new LogicInstruction(END));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(READ, "__tmp0", "cell1", "14"),
                        new LogicInstruction(SET, "__tmp2", "14"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }
    
    // Taken from OptimizeSensorThenSetTest
    
    @Test
    void optimizesSensorThenSet() {
        LogicInstructionGenerator.generateInto(pipeline,
                (Seq) translateToAst(
                        "numsilicon = STORAGE.silicon"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "numsilicon", "STORAGE", "@silicon"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void supportsConsecutiveSensors() {
        LogicInstructionGenerator.generateInto(pipeline,
                (Seq) translateToAst(
                        "numgraphite = container1.graphite\nnumcoal = container1.coal"
                )
        );

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "numgraphite", "container1", "@graphite"),
                        new LogicInstruction(SENSOR, "numcoal", "container1", "@coal"),
                        new LogicInstruction(END)
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorThenSetWithUnrelatedAlone() {
        pipeline.emit(new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(SET, "n", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon"),
                        new LogicInstruction(SET, "n", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        pipeline.emit(new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(PRINT, "numsil"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon"),
                        new LogicInstruction(PRINT, "numsil")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSensorsOptimizeCorrectly() {
        pipeline.emit(new LogicInstruction(SENSOR, "a", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction(SENSOR, "b", "conveyor1", "@enabled"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "a", "vault1", "@silicon"),
                        new LogicInstruction(SENSOR, "b", "conveyor1", "@enabled")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        pipeline.emit(new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction(SENSOR, "numsil", "vault1", "@silicon")
                ),
                terminus.getResult()
        );
    }
    
}
