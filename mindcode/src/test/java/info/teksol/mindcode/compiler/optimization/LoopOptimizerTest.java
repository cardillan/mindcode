package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class LoopOptimizerTest extends AbstractOptimizerTest<LoopOptimizer> {

    @Override
    protected Class<LoopOptimizer> getTestedClass() {
        return LoopOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.CONDITIONAL_JUMPS_IMPROVEMENT,
                Optimization.SINGLE_STEP_JUMP_ELIMINATION,
                Optimization.JUMP_OVER_JUMP_ELIMINATION,
                Optimization.UNREACHABLE_CODE_ELIMINATION,
                Optimization.LOOP_OPTIMIZATION
        );
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Test
    void optimizesRangedForLoops() {
        assertCompilesTo("""
                        for i in 0 ... 10
                            cell1[i] = 1
                        end
                        print("Done.")
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1003)),
                createInstruction(WRITE, "1", "cell1", "i"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1003), "lessThan", "i", "10"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Done.")),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoop() {
        assertCompilesTo("""
                        i = 10
                        while i > 0
                            print(i)
                            i -= 1
                        end
                        """,
                createInstruction(SET, "i", "10"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(OP, "sub", var(1), "i", "1"),
                createInstruction(SET, "i", var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1003), "greaterThan", "i", "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoopComparingToNull() {
        assertCompilesTo("""
                        block = null
                        while block == null
                            block = getlink(1)
                        end
                        """,
                createInstruction(SET, "block", "null"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1003)),
                createInstruction(GETLINK, var(1), "1"),
                createInstruction(SET, "block", var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1003), "equal", "block", "null"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoopStrictEqual() {
        assertCompilesTo("""
                        while state === 0
                            print(i)
                            state = @unit.dead
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "strictEqual", var(0), "state", "0"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(SENSOR, var(1), "@unit", "@dead"),
                createInstruction(SET, "state", var(1)),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1003), "strictEqual", "state", "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoopWithInitialization() {
        assertCompilesTo("""
                        count = 0
                        while switch1.enabled
                            print(count += 1)
                        end
                        """,
                createInstruction(SET, "count", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                createInstruction(JUMP, var(1002), "equal", var(0), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", var(1), "count", "1"),
                createInstruction(SET, "count", var(1)),
                createInstruction(PRINT, "count"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                createInstruction(JUMP, var(1003), "notEqual", var(0), "false"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoopWithInitializationAndStrictEqual() {
        assertCompilesTo("""
                        while @unit.dead === 0
                            print("Got unit!")
                        end
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", var(1), var(0), "0"),
                createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q("Got unit!")),
                createInstruction(LABEL, var(1001)),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1003), "strictEqual", var(0), "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesRangedForLoopsWithBreak() {
        assertCompilesTo("""
                        for i in 1 .. 10
                            print(i)
                            if i > 5
                                break
                            end
                        end
                        """,
                createInstruction(SET, "i", "1"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1002), "greaterThan", "i", "5"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1005), "lessThanEq", "i", "10"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesWhileLoopWithContinue() {
        assertCompilesTo("""
                        i = 10
                        while i > 0
                            i -= 1
                            if i == 4
                                continue
                            end
                            print(i)
                        end
                        """,
                createInstruction(SET, "i", "10"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "sub", var(1), "i", "1"),
                createInstruction(SET, "i", var(1)),
                createInstruction(JUMP, var(1001), "equal", "i", "4"),
                createInstruction(LABEL, var(1003)),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "i"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1005), "greaterThan", "i", "0"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }

    @Test
    void optimizesBitReadTest() {
        assertCompilesTo(createTestCompiler(CompilerProfile.fullOptimizations()),
                        """
                        def getBit(bitIndex)
                          bitIndex % 2
                        end
                        
                        for i in 0 ... 16
                            print(getBit(i) ? 1 : 0)
                        end
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(LABEL, var(1007)),
                createInstruction(SET, var(2), "0"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "mod", var(0), "i", "2"),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1006), "equal", var(0), "false"),
                createInstruction(SET, var(2), "1"),
                createInstruction(LABEL, var(1006)),
                createInstruction(PRINT, var(2)),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1007), "lessThan", "i", "16"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END)
        );
    }
}