package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.mindustry.optimisation.PropagateJumpTargets;
import java.util.EnumSet;
import java.util.function.Function;

// The optimizations are applied in the declared order, ie. DeadCodeEliminator gets instructions from the 
// compiler, makes optimizations and passes them onto the next optimizer.
public enum Optimisation {
    DEAD_CODE_ELIMINATION               (next -> new DeadCodeEliminator(next)),
    SINGLE_STEP_JUMP_ELIMINATION        (next -> new SingleStepJumpEliminator(next)),
    SENSOR_THEN_SET_OPTIMIZATION        (next -> new OptimizeSensorThenSet(next)),
    OP_THEN_SET_OPTIMIZATION            (next -> new OptimizeOpThenSet(next)),
    SET_THEN_WRITE_OPTIMIZATION         (next -> new OptimizeSetThenWrite(next)),
    READ_THEN_SET_OPTIMIZATION          (next -> new OptimizeReadThenSet(next)),
    SET_THEN_READ_OPTIMIZATION          (next -> new OptimizeSetThenRead(next)),
    SET_THEN_OP_OPTIMIZATION            (next -> new OptimizeSetThenOp(next)),
    SET_THEN_SET_OPTIMIZATION           (next -> new OptimizeSetThenSet(next)),
    SET_THEN_PRINT_OPTIMIZATION         (next -> new OptimizeSetThenPrint(next)),
    GETLINK_THEN_SET_OPTIMIZATION       (next -> new OptimizeGetlinkThenSet(next)),
    CONDITIONAL_JUMPS_IMPROVEMENT       (next -> new ImproveConditionalJumps(next)),
    JUMP_TARGET_PROPAGATION             (next -> new PropagateJumpTargets(next)),
    ;
    
    private final Function<LogicInstructionPipeline, LogicInstructionPipeline> instanceCreator;

    private Optimisation(Function<LogicInstructionPipeline, LogicInstructionPipeline> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }
    
    private LogicInstructionPipeline createInstance(LogicInstructionPipeline next) {
        return instanceCreator.apply(next);
    }
    
    public static LogicInstructionPipeline createCompletePipeline(LogicInstructionPipeline terminus) {
        LogicInstructionPipeline pipeline = terminus;
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            pipeline = values[i].createInstance(pipeline);
            
        }
        return pipeline;
    }
    
    public static LogicInstructionPipeline createPipelineOf(LogicInstructionPipeline terminus, Optimisation... optimizers) {
        EnumSet<Optimisation> includes = EnumSet.of(optimizers[0], optimizers);
        LogicInstructionPipeline pipeline = terminus;
        Optimisation[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (includes.contains(values[i])) {
                pipeline = values[i].createInstance(pipeline);
            }
        }
        return pipeline;
    }
}
