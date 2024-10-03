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
                        param A = 10
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
                createInstruction(JUMP, var(1003), "lessThan", "i", "1000")
        );
    }

    @Test
    void movesInvariantCodeOutOfCondition() {
        assertCompilesTo("""
                        param A = 10
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
                createInstruction(JUMP, var(1003), "lessThan", "i", var(0))
        );
    }

    @Test
    void recognizesAllLoopVariables() {
        assertCompilesTo("""
                        param A = 10
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
                createInstruction(JUMP, var(1003), "lessThan", "i", "A")
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
                createInstruction(GOTOLABEL, var(1004), "marker0")
        );
    }

    @Test
    void recognizesGlobalVariablesModifiedByFunctions() {
        assertCompilesTo("""
                        A = rand(10);
                        B = rand(20);
                        for i = 0; i < A; i += 1 do
                            x = 2 * A;
                            y = 2 * B;
                            foo(10);
                            print(x, y);
                        end;

                        noinline def foo(n)
                            print(n);
                            bar(n);
                        end
                        
                        noinline def bar(x)
                            A = x - B;
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(OP, "rand", "A", "10"),
                createInstruction(OP, "rand", "B", "20"),
                createInstruction(SET, "i", "0"),
                createInstruction(OP, "mul", "y", "2", "B"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(LABEL, var(1009)),
                createInstruction(OP, "mul", "x", "2", "A"),
                createInstruction(SET, "__fn1_n", "10"),
                createInstruction(SETADDR, "__fn1retaddr", var(1005)),
                createInstruction(CALL, var(1001), "__fn1retval"),
                createInstruction(GOTOLABEL, var(1005), "__fn1"),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "y"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", "i", "A"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", "A", "__fn0_x", "B"),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "__fn1_n"),
                createInstruction(SET, "__fn0_x", "__fn1_n"),
                createInstruction(SETADDR, "__fn0retaddr", var(1008)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1008), "__fn0"),
                createInstruction(GOTO, "__fn1retaddr", "__fn1")
        );
    }

    @Test
    void handlesAssignmentsInConditions() {
        assertCompilesTo("""
                        param A = 100
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
                createInstruction(JUMP, var(1003), "lessThan", "k", "A")
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
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(SET, "a", "__fn0retval"),
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(PRINT, "a"),
                createInstruction(PRINT, "__fn0retval")
        );
    }

    @Test
    void handlesNestedLoops() {
        assertCompilesTo("""
                        param A = 10
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
                createInstruction(JUMP, var(1006), "lessThan", "j", "A")
        );
    }

    @Test
    void handlesListIteratorLoops() {
        assertCompilesTo("""
                        param A = 10;
                        for i in 1, A do
                            print(i, 2 * A);
                        end;
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
                createInstruction(GOTOLABEL, var(1004), "marker0")
        );
    }

    @Test
    void handlesListIteratorLoopsWithMultipleIterators() {
        assertCompilesTo("""
                        param A = 10;
                        for i, j in 1, 2, 2*A, 4*A do
                            print(2 * i, 2 * j, 2 * A);
                        end;
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(OP, "mul", var(5), "2", "A"),
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, "j", "2"),
                createInstruction(JUMP, var(1001), "always"),
                createInstruction(GOTOLABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(OP, "mul", "i", "2", "A"),
                createInstruction(OP, "mul", "j", "4", "A"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "mul", var(3), "2", "i"),
                createInstruction(OP, "mul", var(4), "2", "j"),
                createInstruction(PRINT, var(3)),
                createInstruction(PRINT, var(4)),
                createInstruction(PRINT, var(5)),
                createInstruction(GOTO, var(0), "marker0"),
                createInstruction(GOTOLABEL, var(1004), "marker0")
        );
    }

    @Test
    void handlesIfInsideLoop() {
        // At this moment, invariant ifs aren't handled. Special handling of an entire IF context needs to be added.
        assertCompilesTo("""
                        param A = 10
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
                createInstruction(JUMP, var(1003), "equal", "A", "false"),
                createInstruction(PRINT, q("1")),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q("2")),
                createInstruction(LABEL, var(1004)),
                createInstruction(JUMP, var(1005), "lessThan", "i", "1000")
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
                createInstruction(JUMP, var(1009), "lessThan", "i", "1000")
        );
    }

    @Test
    void handlesInvariantIf() {
        assertCompilesTo("""
                        param A = 10
                        for i in 1 ... A
                            a = A < 0 ? 3 : 4
                            print(a)
                        end
                        print("finish")
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "1"),
                createInstruction(SET, var(2), "4"),
                createInstruction(JUMP, var(1006), "greaterThanEq", "A", "0"),
                createInstruction(SET, var(2), "3"),
                createInstruction(LABEL, var(1006)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "1", "A"),
                createInstruction(LABEL, var(1007)),
                createInstruction(PRINT, var(2)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1007), "lessThan", "i", "A"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("finish"))
        );
    }

    @Test
    void handlesInvariantIfInExpression() {
        assertCompilesTo("""
                        param A = 10
                        for i in 1 ... A
                            a = 10 * (A < 0 ? 3 : 4)
                            print(a)
                        end
                        print("finish")
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, "i", "1"),
                createInstruction(OP, "mul", "a", "10", "4"),
                createInstruction(JUMP, var(1006), "greaterThanEq", "A", "0"),
                createInstruction(OP, "mul", "a", "10", "3"),
                createInstruction(LABEL, var(1006)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "1", "A"),
                createInstruction(LABEL, var(1007)),
                createInstruction(PRINT, "a"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1007), "lessThan", "i", "A"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("finish"))
        );
    }

    @Test
    void doesNotBlockVariablesModifiedByOwnFunction() {
        assertCompilesTo("""
                        noinline def foo(n)
                            sum = 0;
                            r = rand(10);        // Prevents compile-time evaluation
                            for i in 0 ... 50
                                sum += n + r;
                            end;
                            print(floor(sum - 50 * r + 0.5));
                        end;
                        
                        noinline def bar(s)
                            foo(10 + s);
                        end;
                        
                        bar(1);
                        """,
                createInstruction(SET, "__fn0_s", "1"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "add", "__fn1_n", "10", "__fn0_s"),
                createInstruction(SETADDR, "__fn1retaddr", var(1004)),
                createInstruction(CALL, var(1001), "__fn1retval"),
                createInstruction(GOTOLABEL, var(1004), "__fn1"),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, "__fn1_sum", "0"),
                createInstruction(OP, "rand", "__fn1_r", "10"),
                createInstruction(SET, "__fn1_i", "0"),
                createInstruction(OP, "add", var(2), "__fn1_n", "__fn1_r"),
                createInstruction(LABEL, var(1009)),
                createInstruction(OP, "add", "__fn1_sum", "__fn1_sum", var(2)),
                createInstruction(OP, "add", "__fn1_i", "__fn1_i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", "__fn1_i", "50"),
                createInstruction(OP, "mul", var(4), "50", "__fn1_r"),
                createInstruction(OP, "sub", var(5), "__fn1_sum", var(4)),
                createInstruction(OP, "add", var(6), var(5), "0.5"),
                createInstruction(OP, "floor", var(7), var(6)),
                createInstruction(PRINT, var(7)),
                createInstruction(GOTO, "__fn1retaddr", "__fn1")

        );
    }
}