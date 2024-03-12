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
        // Loop unrolling might interfere with this optimizer
        return Optimization.LIST.stream().filter(o -> o != Optimization.LOOP_UNROLLING).toList();
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
    void recognizesIteratorVariables() {
        assertCompilesTo("""
                        a = 1
                        for i in (a, a = 2)
                            k = 2 * a
                            print(i, k)
                        end
                        """,
                createInstruction(SET, "a", "1"),
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "i", "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "a", "2"),
                createInstruction(SET, "i", "2"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "mul", "k", "2", "a"),
                createInstruction(PRINT, "i"),
                createInstruction(PRINT, "k"),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(END)
        );
    }

    @Test
    void ignoresGlobalVariablesOnFunctionCall() {
        assertCompilesTo("""
                        allocate stack in cell1
                                                
                        A = 10
                        for i = 0; i < A; i += 1
                            x = 2 * A
                            foo(10)
                            print(x)
                        end
                                                
                        def foo(n)
                            print(n)
                            A = 20
                            if n > 0
                                foo(n - 1)
                            end
                        end
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "0"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(LABEL, var(1009)),
                createInstruction(OP, "mul", "x", "2", "A"),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(CALLREC, "cell1", var(1000), var(1004), "__fn0retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PRINT, "x"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", "i", "A"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "__fn0_n"),
                createInstruction(SET, "A", "20"),
                createInstruction(JUMP, var(1007), "lessThanEq", "__fn0_n", "0"),
                createInstruction(OP, "sub", "__fn0_n", "__fn0_n", "1"),
                createInstruction(CALLREC, "cell1", var(1000), var(1008), "__fn0retval"),
                createInstruction(LABEL, var(1008)),
                createInstruction(LABEL, var(1007)),
                createInstruction(RETURN, "cell1")
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
    void recognizesFunctionReturnVariables() {
        assertCompilesTo("""
                        while true
                            a = foo()
                            b = foo()
                            print(a, b)
                        end

                        def foo()
                            rand(10)
                        end
                        """,
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(SET, "a", "__fn0retval"),
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(JUMP, var(1001), "always")
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
    void handlesListIteratorLoops() {
        assertCompilesTo("""
                        A = 10
                        i = 0
                        for i in (1, A)
                            print(i, 2 * A)
                        end
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(OP, "mul", var(1), "2", "A"),
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "i", "1"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, "i", "A"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "i"),
                createInstruction(PRINT, var(1)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1004), "marker0"),
                createInstruction(END)
        );
    }

    @Test
    void handlesIfInsideLoop() {
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

    @Test
    void handlesInvariantIf() {
        assertCompilesTo("""
                        A = 10
                        for i in 1 ... A
                            a = A < 0 ? 3 : 4
                            print(a)
                        end
                        print("finish")
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, "a", "4"),
                createInstruction(JUMP, var(1005), "greaterThanEq", "A", "0"),
                createInstruction(SET, "a", "3"),
                createInstruction(LABEL, var(1005)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "1", "A"),
                createInstruction(LABEL, var(1006)),
                createInstruction(PRINT, "a"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1006), "lessThan", "i", "A"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("finish")),
                createInstruction(END)
        );
    }

    @Test
    void handlesInvariantIfInExpression() {
        assertCompilesTo("""
                        A = 10
                        for i in 1 ... A
                            a = 10 * (A < 0 ? 3 : 4)
                            print(a)
                        end
                        print("finish")
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, var(2), "4"),
                createInstruction(JUMP, var(1005), "greaterThanEq", "A", "0"),
                createInstruction(SET, var(2), "3"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "mul", "a", "10", var(2)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "1", "A"),
                createInstruction(LABEL, var(1006)),
                createInstruction(PRINT, "a"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1006), "lessThan", "i", "A"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("finish")),
                createInstruction(END)
        );
    }
}