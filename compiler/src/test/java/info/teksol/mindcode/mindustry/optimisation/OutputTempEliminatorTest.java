package info.teksol.mindcode.mindustry.optimisation;

import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.mindustry.AbstractGeneratorTest;
import info.teksol.mindcode.mindustry.LogicInstruction;
import info.teksol.mindcode.mindustry.LogicInstructionGenerator;
import info.teksol.mindcode.mindustry.LogicInstructionPipeline;
import info.teksol.mindcode.mindustry.Optimisation;
import java.util.List;
import org.junit.jupiter.api.Test;

public class OutputTempEliminatorTest extends AbstractGeneratorTest {
    private final LogicInstructionPipeline pipeline = Optimisation.createPipelineOf(terminus,
            Optimisation.OUTPUT_TEMPS_ELIMINATION);

    @Test
    void optimizesBasicCase() {
        pipeline.emit(new LogicInstruction("sensor", "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("set", "result", "__tmp0"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "result", "vault1", "@silicon"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresNontemporaryVariables() {
        pipeline.emit(new LogicInstruction("sensor", "var", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("set", "result", "var"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "var", "vault1", "@silicon"),
                        new LogicInstruction("set", "result", "var"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresVariablesWithMultipleUsage() {
        pipeline.emit(new LogicInstruction("sensor", "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("set", "result", "__tmp0"));
        pipeline.emit(new LogicInstruction("set", "another", "__tmp0"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "__tmp0", "vault1", "@silicon"),
                        new LogicInstruction("set", "result", "__tmp0"),
                        new LogicInstruction("set", "another", "__tmp0"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresInstructionsInWrongOrder() {
        pipeline.emit(new LogicInstruction("print", "1"));
        pipeline.emit(new LogicInstruction("set", "result", "__tmp0"));
        pipeline.emit(new LogicInstruction("sensor", "__tmp0", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("print", "1"),
                        new LogicInstruction("set", "result", "__tmp0"),
                        new LogicInstruction("sensor", "__tmp0", "vault1", "@silicon"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void mergesTwoSets() {
        pipeline.emit(new LogicInstruction("set", "__tmp0", "x"));
        pipeline.emit(new LogicInstruction("set", "y", "__tmp0"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "y", "x"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void ignoresIncompatibleSets() {
        pipeline.emit(new LogicInstruction("set", "__tmp0", "x"));
        pipeline.emit(new LogicInstruction("set", "__tmp0", "y"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("set", "__tmp0", "x"),
                        new LogicInstruction("set", "__tmp0", "y"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("getlink", "target", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedBySetForDifferentStuffAlone() {
        pipeline.emit(new LogicInstruction("getlink", "a", "0"));
        pipeline.emit(new LogicInstruction("set", "b", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0"),
                        new LogicInstruction("set", "b", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesGetlinkFollowedByGetlinkAlone() {
        pipeline.emit(new LogicInstruction("getlink", "a", "0"));
        pipeline.emit(new LogicInstruction("getlink", "b", "1"));
        pipeline.emit(new LogicInstruction("read", "c", "cell1", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0"),
                        new LogicInstruction("getlink", "b", "1"),
                        new LogicInstruction("read", "c", "cell1", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void terminalGetlinkIsEmitted() {
        pipeline.emit(new LogicInstruction("getlink", "a", "0"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("getlink", "a", "0")
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
                        new LogicInstruction("set", var(0), "100"),
                        new LogicInstruction("op", "rand", "r", var(0)),
                        new LogicInstruction("end")
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
                        new LogicInstruction("op", "max", var(0), "state", "MIN"),
                        new LogicInstruction("op", "min", "state", var(0), "MAX"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("op", "sub", "remaining", "capacity", "current"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("set", var(0), "0"),
                        new LogicInstruction("read", "boo", "cell1", var(0)),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesReadThenSetButOtherValueAlone() {
        pipeline.emit(new LogicInstruction("read", "__tmp0", "cell1", "14"));
        pipeline.emit(new LogicInstruction("set", "__tmp2", "14"));
        pipeline.emit(new LogicInstruction("end"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("read", "__tmp0", "cell1", "14"),
                        new LogicInstruction("set", "__tmp2", "14"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", "numsilicon", "STORAGE", "@silicon"),
                        new LogicInstruction("end")
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
                        new LogicInstruction("sensor", "numgraphite", "container1", "@graphite"),
                        new LogicInstruction("sensor", "numcoal", "container1", "@coal"),
                        new LogicInstruction("end")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorThenSetWithUnrelatedAlone() {
        pipeline.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("set", "n", "1"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon"),
                        new LogicInstruction("set", "n", "1")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSensorNonSetAlone() {
        pipeline.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("print", "numsil"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon"),
                        new LogicInstruction("print", "numsil")
                ),
                terminus.getResult()
        );
    }

    @Test
    void consecutiveSensorsOptimizeCorrectly() {
        pipeline.emit(new LogicInstruction("sensor", "a", "vault1", "@silicon"));
        pipeline.emit(new LogicInstruction("sensor", "b", "conveyor1", "@enabled"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "a", "vault1", "@silicon"),
                        new LogicInstruction("sensor", "b", "conveyor1", "@enabled")
                ),
                terminus.getResult()
        );
    }

    @Test
    void leavesSingleSensorAlone() {
        pipeline.emit(new LogicInstruction("sensor", "numsil", "vault1", "@silicon"));
        pipeline.flush();

        assertLogicInstructionsMatch(
                List.of(
                        new LogicInstruction("sensor", "numsil", "vault1", "@silicon")
                ),
                terminus.getResult()
        );
    }
    
}
