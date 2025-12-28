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
                createInstruction(SET, ":i", "0"),
                createInstruction(OP, "mul", tmp(2), "2", "A"),
                createInstruction(LABEL, label(3)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(PRINT, tmp(2)),
                createInstruction(JUMP, label(3), "lessThan", ":i", "1000")
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
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "__start__", "lessThanEq", "A", "-10"),
                createInstruction(OP, "add", tmp(0), "A", "10"),
                createInstruction(LABEL, label(3)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(PRINT, ":i"),
                createInstruction(JUMP, label(3), "lessThan", ":i", tmp(0))
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
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(OP, "add", tmp(4), "A", "10"),
                createInstruction(LABEL, label(3)),
                createInstruction(OP, "add", ":j", ":i", "1"),
                createInstruction(PRINT, ":j"),
                createInstruction(OP, "add", ":i", ":j", "1"),
                createInstruction(OP, "add", ":k", ":j", "20"),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, tmp(4)),
                createInstruction(PRINT, ":k"),
                createInstruction(JUMP, label(3), "lessThan", ":i", "A")
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
                createInstruction(SET, ":i", ":a"),
                createInstruction(SETADDR, tmp(0), label(3)),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(3)),
                createInstruction(SET, ":a", "2"),
                createInstruction(SET, ":i", "2"),
                createInstruction(SETADDR, tmp(0), label(4)),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "mul", ":k", "2", ":a"),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, ":k"),
                createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                createInstruction(MULTILABEL, label(4))
        );
    }

    @Test
    void recognizesGlobalVariablesModifiedByFunctions() {
        assertCompilesTo("""
                        A = rand(10);
                        B = rand(20);
                        i = 0;
                        do
                            x = 2 * A;
                            y = 2 * B;
                            foo(10);
                            print(x, y);
                        while ++i < A;

                        noinline def bar(x)
                            A = x - B;
                        end;

                        noinline def foo(n)
                            print(n);
                            bar(n);
                        end;
                        """,
                createInstruction(OP, "rand", ".A", "10"),
                createInstruction(OP, "rand", ".B", "20"),
                createInstruction(SET, ":i", "0"),
                createInstruction(OP, "mul", ":y", "2", ".B"),
                createInstruction(SET, ":foo:n", "10"),
                createInstruction(SETADDR, ":foo*retaddr", label(5)),
                createInstruction(LABEL, label(2)),
                createInstruction(OP, "mul", ":x", "2", ".A"),
                createInstruction(CALL, label(1), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(5)),
                createInstruction(PRINT, ":x"),
                createInstruction(PRINT, ":y"),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(2), "lessThan", ":i", ".A"),
                createInstruction(END),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, ":foo:n"),
                createInstruction(SET, ":bar:x", ":foo:n"),
                createInstruction(SETADDR, ":bar*retaddr", label(7)),
                createInstruction(CALL, label(0), "*invalid", ":bar*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(RETURN, ":foo*retaddr"),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "sub", ".A", ":bar:x", ".B"),
                createInstruction(RETURN, ":bar*retaddr")
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
                createInstruction(SET, ":i", "0"),
                createInstruction(SET, ":k", "10"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "10", "A"),
                createInstruction(LABEL, label(3)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, ":k"),
                createInstruction(OP, "add", ":k", ":i", "10"),
                createInstruction(JUMP, label(3), "lessThan", ":k", "A")
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
                createInstruction(OP, "rand", ":a", "10"),
                createInstruction(OP, "rand", ":foo*retval", "10"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":foo*retval")
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
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, "__start__", "greaterThan", "1", "count"),
                createInstruction(LABEL, label(4)),
                createInstruction(OP, "rand", ":foo:x", "10"),
                createInstruction(OP, "rand", ":foo:y", "10"),
                createInstruction(OP, "add", tmp(3), ":foo:x", ":foo:y"),
                createInstruction(PRINT, tmp(3)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(4), "lessThanEq", ":i", "count")
        );
    }

    @Test
    void recognizesFunctionOutputParameters() {
        assertCompilesTo("""
                        const count = 10;

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
                createInstruction(SETADDR, ":foo*retaddr", label(1)),
                createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(1)),
                createInstruction(OP, "add", tmp(1), ":foo:x", ":foo:y"),
                createInstruction(PRINT, tmp(1)),
                createInstruction(SET, ":i", "1"),
                createInstruction(SETADDR, ":foo*retaddr", label(5)),
                createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(5)),
                createInstruction(OP, "add", tmp(3), ":foo:x", ":foo:y"),
                createInstruction(PRINT, tmp(3)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(0), "lessThanEq", ":i", "10"),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "rand", ":foo:x", "10"),
                createInstruction(OP, "rand", ":foo:y", "10"),
                createInstruction(RETURN, ":foo*retaddr")
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
                createInstruction(SET, ":j", "0"),
                createInstruction(JUMP, "__start__", "greaterThanEq", "0", "A"),
                createInstruction(OP, "add", tmp(0), "A", "10"),
                createInstruction(LABEL, label(6)),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, label(5), "lessThanEq", "A", "-10"),
                createInstruction(LABEL, label(7)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(PRINT, ":i"),
                createInstruction(JUMP, label(7), "lessThan", ":i", tmp(0)),
                createInstruction(LABEL, label(5)),
                createInstruction(OP, "add", ":j", ":j", "1"),
                createInstruction(JUMP, label(6), "lessThan", ":j", "A")
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
                createInstruction(OP, "mul", tmp(1), "2", "A"),
                createInstruction(SET, ":i", "1"),
                createInstruction(SETADDR, tmp(0), label(3)),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(3)),
                createInstruction(SET, ":i", "A"),
                createInstruction(SETADDR, tmp(0), label(4)),
                createInstruction(LABEL, label(0)),
                createInstruction(PRINT, ":i"),
                createInstruction(PRINT, tmp(1)),
                createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                createInstruction(MULTILABEL, label(4))
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
                createInstruction(OP, "mul", tmp(5), "2", "A"),
                createInstruction(SET, ":i", "1"),
                createInstruction(SET, ":j", "2"),
                createInstruction(SETADDR, tmp(0), label(3)),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(MULTILABEL, label(3)),
                createInstruction(OP, "mul", ":i", "2", "A"),
                createInstruction(OP, "mul", ":j", "4", "A"),
                createInstruction(SETADDR, tmp(0), label(4)),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "mul", tmp(3), "2", ":i"),
                createInstruction(OP, "mul", tmp(4), "2", ":j"),
                createInstruction(PRINT, tmp(3)),
                createInstruction(PRINT, tmp(4)),
                createInstruction(PRINT, tmp(5)),
                createInstruction(MULTIJUMP, tmp(0), "0", "0"),
                createInstruction(MULTILABEL, label(4))
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
                            if A then print("1"); else print("2"); end;
                        end;
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, ":i", "0"),
                createInstruction(LABEL, label(5)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(3), "equal", "A", "false"),
                createInstruction(PRINT, q("1")),
                createInstruction(JUMP, label(4), "always"),
                createInstruction(LABEL, label(3)),
                createInstruction(PRINT, q("2")),
                createInstruction(LABEL, label(4)),
                createInstruction(JUMP, label(5), "lessThan", ":i", "1000")
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
                createInstruction(SET, ":i", "1"),
                createInstruction(LABEL, label(9)),
                createInstruction(SET, ":j", "1"),
                createInstruction(OP, "mul", tmp(0), ":i", ":i"),
                createInstruction(LABEL, label(10)),
                createInstruction(SET, ":k", "1"),
                createInstruction(OP, "mul", tmp(1), ":j", ":j"),
                createInstruction(OP, "add", tmp(2), tmp(0), tmp(1)),
                createInstruction(OP, "sqrt", tmp(3), tmp(2)),
                createInstruction(LABEL, label(11)),
                createInstruction(OP, "mul", tmp(4), ":k", ":k"),
                createInstruction(OP, "div", tmp(5), tmp(3), tmp(4)),
                createInstruction(PRINT, tmp(5)),
                createInstruction(OP, "add", ":k", ":k", "1"),
                createInstruction(JUMP, label(11), "lessThan", ":k", "1000"),
                createInstruction(OP, "add", ":j", ":j", "1"),
                createInstruction(JUMP, label(10), "lessThan", ":j", "1000"),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(9), "lessThan", ":i", "1000")
        );
    }

    @Test
    void handlesSelectInExpression() {
        assertCompilesTo("""
                        param A = 10;
                        for i in 1 ... A do
                            print(A < 0 ? 3 : 4);
                        end;
                        print("finish");
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, label(2), "greaterThanEq", "1", "A"),
                createInstruction(SELECT, tmp(1), "lessThan", "A", "0", "3", "4"),
                createInstruction(LABEL, label(5)),
                createInstruction(PRINT, tmp(1)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(5), "lessThan", ":i", "A"),
                createInstruction(LABEL, label(2)),
                createInstruction(PRINT, q("finish"))
        );
    }

    @Test
    void handlesSelectsInExpression() {
        assertCompilesTo("""
                        param A = 10;
                        for i in 1 ... A do
                            print(10 * (A < 0 ? 3 : 4));
                        end;
                        print("finish");
                        """,
                createInstruction(SET, "A", "10"),
                createInstruction(SET, ":i", "1"),
                createInstruction(JUMP, label(2), "greaterThanEq", "1", "A"),
                createInstruction(SELECT, tmp(1), "lessThan", "A", "0", "3", "4"),
                createInstruction(OP, "mul", tmp(2), "10", tmp(1)),
                createInstruction(LABEL, label(5)),
                createInstruction(PRINT, tmp(2)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(5), "lessThan", ":i", "A"),
                createInstruction(LABEL, label(2)),
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
                createInstruction(SET, ":bar:s", "1"),
                createInstruction(SETADDR, ":bar*retaddr", label(2)),
                createInstruction(CALL, label(0), "*invalid", ":bar*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "add", ":foo:n", "10", ":bar:s"),
                createInstruction(SETADDR, ":foo*retaddr", label(4)),
                createInstruction(CALL, label(1), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(4)),
                createInstruction(RETURN, ":bar*retaddr"),
                createInstruction(LABEL, label(1)),
                createInstruction(SET, ":foo:sum", "0"),
                createInstruction(OP, "rand", ":foo:r", "10"),
                createInstruction(SET, ":foo:i", "0"),
                createInstruction(OP, "add", tmp(2), ":foo:n", ":foo:r"),
                createInstruction(LABEL, label(9)),
                createInstruction(OP, "add", ":foo:sum", ":foo:sum", tmp(2)),
                createInstruction(OP, "add", ":foo:i", ":foo:i", "1"),
                createInstruction(JUMP, label(9), "lessThan", ":foo:i", "50"),
                createInstruction(OP, "mul", tmp(3), "50", ":foo:r"),
                createInstruction(OP, "sub", tmp(4), ":foo:sum", tmp(3)),
                createInstruction(OP, "add", tmp(5), tmp(4), "0.5"),
                createInstruction(OP, "floor", tmp(6), tmp(5)),
                createInstruction(PRINT, tmp(6)),
                createInstruction(RETURN, ":foo*retaddr")
        );
    }
}
