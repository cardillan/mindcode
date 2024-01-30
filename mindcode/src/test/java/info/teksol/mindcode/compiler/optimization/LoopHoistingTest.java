package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

class LoopHoistingTest extends AbstractOptimizerTest<LoopHoisting> {

    @Override
    protected Class<LoopHoisting> getTestedClass() {
        return LoopHoisting.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Test
    void movesInvariantCodeOutOfLoop() {
        assertCompilesTo("""
                        A = 10
                        i = 0
                        while i < 1000
                            i = i + 1
                            print(2 * A)
                        end
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "0"),
                createInstruction(OP, "mul", var(2), "2", "A"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(PRINT, var(2)),
                createInstruction(JUMP, var(1003), "lessThan", "i", "1000"),
                createInstruction(END)
        );
    }

    @Test
    void movesInvariantCodeOutOfCondition() {
        assertCompilesTo("""
                        A = 10
                        i = 0
                        while i < A + 10
                            i = i + 1
                            print(i)
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "0"),
                createInstruction(OP, "add", var(0), "A", "10"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", var(0)),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1003), "lessThan", "i", var(0)),
                createInstruction(END)
        );
    }

    @Test
    void recognizesAllLoopVariables() {
        assertCompilesTo("""
                        A = 10
                        i = 0
                        while i < A
                            j = i + 1
                            print(j)
                            i = j + 1
                            k = j + 20
                            print(i, A + 10, k)
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "0"),
                createInstruction(OP, "add", var(4), "A", "10"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "j", "i", "1"),
                createInstruction(PRINT, "j"),
                createInstruction(OP, "add", "i", "j", "1"),
                createInstruction(OP, "add", "k", "j", "20"),
                createInstruction(PRINT, "i"),
                createInstruction(PRINT, var(4)),
                createInstruction(PRINT, "k"),
                createInstruction(JUMP, var(1003), "lessThan", "i", "A"),
                createInstruction(END)
        );
    }

    @Test
    void handlesAssignmentsInConditions() {
        assertCompilesTo("""
                        A = 100
                        i = 0
                        while (k = i + 10) < A
                            i += 1
                            print(i, k)
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "A", "100"),
                createInstruction(SET, "i", "0"),
                createInstruction(SET, "k", "10"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "10", "A"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(PRINT, "i"),
                createInstruction(PRINT, "k"),
                createInstruction(OP, "add", "k", "i", "10"),
                createInstruction(JUMP, var(1003), "lessThan", "k", "A"),
                createInstruction(END)
        );
    }

    @Test
    void handlesNestedLoops() {
        assertCompilesTo("""
                        A = 10
                        for j in 0 ... A
                            i = 0
                            while i < A + 10
                                i = i + 1
                                print(i)
                            end
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "j", "0"),
                createInstruction(OP, "add", var(1), "A", "10"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(LABEL, var(1006)),
                createInstruction(SET, "i", "0"),
                createInstruction(JUMP, var(1005), "greaterThanEq", "0", var(1)),
                createInstruction(LABEL, var(1007)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, var(1007), "lessThan", "i", var(1)),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "add", "j", "j", "1"),
                createInstruction(JUMP, var(1006), "lessThan", "j", "A"),
                createInstruction(END)
        );
    }

    @Test
    void handlesIfInsideLoop() {
        // At this moment, invariant ifs aren't handled. Special handling of an entire IF context needs to be added.
        assertCompilesTo("""
                        A = 10
                        i = 0
                        while i < 1000
                            i = i + 1
                            print(A ? "1" : "2")
                        end
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(SET, var(2), q("2")),
                createInstruction(JUMP, var(1004), "equal", "A", "false"),
                createInstruction(SET, var(2), q("1")),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, var(2)),
                createInstruction(JUMP, var(1005), "lessThan", "i", "1000"),
                createInstruction(END)
        );
    }


    @Test
    void handlesExpressions() {
        // At this moment, invariant ifs aren't handled. Special handling of an entire IF context needs to be added.
        assertCompilesTo("""
                        for i in 1 ... 1000
                            for j in 1 ... 1000
                                for k in 1 ... 1000
                                    print(sqrt(i * i + j * j) / (k * k))
                                end
                            end
                        end
                        """,
                createInstruction(SET, "i", "1"),
                createInstruction(LABEL, var(1009)),
                createInstruction(SET, "j", "1"),
                createInstruction(OP, "mul", var(0), "i", "i"),
                createInstruction(LABEL, var(1010)),
                createInstruction(SET, "k", "1"),
                createInstruction(OP, "mul", var(1), "j", "j"),
                createInstruction(OP, "add", var(2), var(0), var(1)),
                createInstruction(OP, "sqrt", var(3), var(2)),
                createInstruction(LABEL, var(1011)),
                createInstruction(OP, "mul", var(4), "k", "k"),
                createInstruction(OP, "div", var(5), var(3), var(4)),
                createInstruction(PRINT, var(5)),
                createInstruction(OP, "add", "k", "k", "1"),
                createInstruction(JUMP, var(1011), "lessThan", "k", "1000"),
                createInstruction(OP, "add", "j", "j", "1"),
                createInstruction(JUMP, var(1010), "lessThan", "j", "1000"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", "i", "1000"),
                createInstruction(END)
        );
    }
}