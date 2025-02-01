package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
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
                        param A = 10;
                        i = 0;
                        while i < 1000 do
                            i = i + 1;
                            print(2 * A);
                        end;
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
                        param A = 10;
                        i = 0;
                        while i < A + 10 do
                            i = i + 1;
                            print(i);
                        end;
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
                        param A = 10;
                        i = 0;
                        while i < A do
                            j = i + 1;
                            print(j);
                            i = j + 1;
                            k = j + 20;
                            print(i, A + 10, k);
                        end;
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
                        a = 1;
                        for i in a, a = 2 do
                            k = 2 * a;
                            print(i, k);
                        end;
                        """,
                createInstruction(SET, ":a", "1"),
                createInstruction(SETADDR, var(0), var(1003)),
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(MULTILABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, ":a", "2"),
                createInstruction(SET, ":i", "2"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "mul", ":k", "2", ":a"),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, ":k"),
                createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                createInstruction(MULTILABEL, var(1004), "marker0")
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

                        noinline def bar(x)
                            A = x - B;
                        end;

                        noinline def foo(n)
                            print(n);
                            bar(n);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(OP, "rand", ".A", "10"),
                createInstruction(OP, "rand", ".B", "20"),
                createInstruction(SET, ":i", "0"),
                createInstruction(OP, "mul", ":y", "2", ".B"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", ".A"),
                createInstruction(LABEL, var(1009)),
                createInstruction(OP, "mul", ":x", "2", ".A"),
                createInstruction(SET, ":fn1:n", "10"),
                createInstruction(SETADDR, ":fn1*retaddr", var(1005)),
                createInstruction(CALL, var(1001), ":fn1*retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, ":x"),
                createInstruction(PRINT, ":y"),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", ":i", ".A"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "sub", ".A", ":fn0:x", ".B"),
                createInstruction(RETURN, ":fn0*retaddr"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, ":fn1:n"),
                createInstruction(SET, ":fn0:x", ":fn1:n"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1008)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1008)),
                createInstruction(RETURN, ":fn1*retaddr")
        );
    }

    @Test
    void handlesAssignmentsInConditions() {
        assertCompilesTo("""
                        param A = 100;
                        i = 0;
                        while (k = i + 10) < A do
                            i += 1;
                            print(i, k);
                        end;
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
                        while true do
                            a = foo();
                            b = foo();
                            print(a, b);
                        end;

                        def foo()
                            rand(10);
                        end;
                        """,
                createInstruction(OP, "rand", ":fn0*retval", "10"),
                createInstruction(SET, ":a", ":fn0*retval"),
                createInstruction(OP, "rand", ":fn0*retval", "10"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":fn0*retval")
        );
    }

    @Test
    void recognizesInlineFunctionOutputParameters() {
        assertCompilesTo("""
                        param count = 10;
                        
                        for i in 1 .. count do
                            foo(out a, out b);
                            print(a + b);
                        end;

                        def foo(out x, out y)
                            x = rand(10);
                            y = rand(10);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "count", "10"),
                createInstruction(SET, "i", "1"),
                createInstruction(JUMP, "__start__", "greaterThan", "1", "count"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "rand", "__fn0_x", "10"),
                createInstruction(OP, "rand", "__fn0_y", "10"),
                createInstruction(OP, "add", var(4), "__fn0_x", "__fn0_y"),
                createInstruction(PRINT, var(4)),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1005), "lessThanEq", "i", "count")
        );
    }

    @Test
    void recognizesFunctionOutputParameters() {
        assertCompilesTo("""
                        param count = 10;

                        foo(out a, out b);
                        print(a + b);
                        
                        for i in 1 .. count do
                            foo(out a, out b);
                            print(a + b);
                        end;

                        noinline def foo(out x, out y)
                            x = rand(10);
                            y = rand(10);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "count", "10"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(OP, "add", var(1), ":fn0:x", ":fn0:y"),
                createInstruction(PRINT, var(1)),
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, "__start__", "greaterThan", "1", "count"),
                createInstruction(LABEL, var(1007)),
                createInstruction(SETADDR, ":fn0*retaddr", var(1005)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "add", var(4), ":fn0:x", ":fn0:y"),
                createInstruction(PRINT, var(4)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, var(1007), "lessThanEq", ":i", "count"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "rand", ":fn0:x", "10"),
                createInstruction(OP, "rand", ":fn0:y", "10"),
                createInstruction(RETURN, ":fn0*retaddr")
        );
    }

    @Test
    void handlesNestedLoops() {
        assertCompilesTo("""
                        param A = 10;
                        for j in 0 ... A do
                            i = 0;
                            while i < A + 10 do
                                i = i + 1;
                                print(i);
                            end;
                        end;
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
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(MULTILABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(SET, ":i", "A"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, var(1)),
                createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                createInstruction(MULTILABEL, var(1004), "marker0")
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
                createInstruction(SET, ":i", "1"),
                createInstruction(SET, ":j", "2"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(MULTILABEL, var(1003), "marker0"),
                createInstruction(SETADDR, var(0), var(1004)),
                createInstruction(OP, "mul", ":i", "2", "A"),
                createInstruction(OP, "mul", ":j", "4", "A"),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "mul", var(3), "2", ":i"),
                createInstruction(OP, "mul", var(4), "2", ":j"),
                createInstruction(PRINT, var(3)),
                createInstruction(PRINT, var(4)),
                createInstruction(PRINT, var(5)),
                createInstruction(MULTIJUMP, var(0), "0", "0", "marker0"),
                createInstruction(MULTILABEL, var(1004), "marker0")
        );
    }

    @Test
    void handlesIfInsideLoop() {
        // At this moment, invariant ifs aren't handled. Special handling of an entire IF context needs to be added.
        assertCompilesTo("""
                        param A = 10;
                        i = 0;
                        while i < 1000 do
                            i = i + 1;
                            print(A ? "1" : "2");
                        end;
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
                        for i in 1 ... 1000 do
                            for j in 1 ... 1000 do
                                for k in 1 ... 1000 do
                                    print(sqrt(i * i + j * j) / (k * k));
                                end;
                            end;
                        end;
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
                        param A = 10;
                        for i in 1 ... A do
                            a = A < 0 ? 3 : 4;
                            print(a);
                        end;
                        print("finish");
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
                        param A = 10;
                        for i in 1 ... A do
                            a = 10 * (A < 0 ? 3 : 4);
                            print(a);
                        end;
                        print("finish");
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
                        noinline def bar(s)
                            foo(10 + s);
                        end;

                        noinline def foo(n)
                            sum = 0;
                            r = rand(10);        // Prevents compile-time evaluation
                            for i in 0 ... 50 do
                                sum += n + r;
                            end;
                            print(floor(sum - 50 * r + 0.5));
                        end;
                        
                        bar(1);
                        """,
                createInstruction(SET, ":fn0:s", "1"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "add", ":fn1:n", "10", ":fn0:s"),
                createInstruction(SETADDR, ":fn1*retaddr", var(1004)),
                createInstruction(CALL, var(1001), ":fn1*retval"),
                createInstruction(LABEL, var(1004)),
                createInstruction(RETURN, ":fn0*retaddr"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, ":fn1:sum", "0"),
                createInstruction(OP, "rand", ":fn1:r", "10"),
                createInstruction(SET, ":fn1:i", "0"),
                createInstruction(OP, "add", var(2), ":fn1:n", ":fn1:r"),
                createInstruction(LABEL, var(1009)),
                createInstruction(OP, "add", ":fn1:sum", ":fn1:sum", var(2)),
                createInstruction(OP, "add", ":fn1:i", ":fn1:i", "1"),
                createInstruction(JUMP, var(1009), "lessThan", ":fn1:i", "50"),
                createInstruction(OP, "mul", var(3), "50", ":fn1:r"),
                createInstruction(OP, "sub", var(4), ":fn1:sum", var(3)),
                createInstruction(OP, "add", var(5), var(4), "0.5"),
                createInstruction(OP, "floor", var(6), var(5)),
                createInstruction(PRINT, var(6)),
                createInstruction(RETURN, ":fn1*retaddr")

        );
    }
}