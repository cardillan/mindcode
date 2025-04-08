package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class FunctionInlinerTest extends AbstractOptimizerTest<FunctionInliner> {

    @Override
    protected Class<FunctionInliner> getTestedClass() {
        return FunctionInliner.class;
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
    void inlinesFunction() {
        assertCompilesTo("""
                        def foo(n)
                            print(2 * n);
                        end;

                        foo(1);
                        foo(2);
                        """,
                createInstruction(PRINT, q("24"))
        );
    }

    @Test
    void inlinesFunctionWithOutputParameter() {
        assertCompilesTo("""
                        param c = 10;
                        void foo(a, out b)
                            b = c * a;
                        end;
                        
                        foo(10, out x);
                        foo(20, out y);
                        
                        print(x, y);
                        """,
                createInstruction(SET, "c", "10"),
                createInstruction(OP, "mul", ":x", "c", "10"),
                createInstruction(OP, "mul", ":fn0:b", "c", "20"),
                createInstruction(PRINT, ":x"),
                createInstruction(PRINT, ":fn0:b")
        );
    }

    @Test
    void inlinesTwoFunction() {
        assertCompilesTo("""
                        def foo(n)
                            print(2 * n);
                        end;

                        def bar(n)
                            foo(n);
                            foo(n + 1);
                        end;

                        bar(1);
                        bar(3);
                        """,
                createInstruction(PRINT, q("2468"))
        );
    }

    @Test
    void inlinesFunctionInsideLoop() {
        assertCompilesTo("""
                        def foo(n)
                            print(n / 2);
                            sum = 0;
                            for i in 0 .. n do
                                sum += i;
                            end;
                            return sum;
                        end;

                        for i in 0 ... 10 do
                            foo(2 * i);
                        end;
                        foo(0);
                        """,
                createInstruction(PRINT, q("01234567890"))
        );
    }

    @Test
    void inlinesTwoFunctionCallsInsideLoop() {
        assertCompilesTo("""
                        #set optimization = basic;
                        while true do
                            a = foo();
                            b = foo();
                            print(a, b);
                        end;
                        
                        def foo()
                            rand(10);
                        end;
                        """,
                createInstruction(OP, "rand", ":foo*retval", "10"),
                createInstruction(SET, ":a", ":foo*retval"),
                createInstruction(OP, "rand", ":foo*retval", "10"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":foo*retval")
        );
    }

    @Test
    void inlinesNestedFunctionCalls() {
        assertCompilesTo("""
                        def foo(n)
                            print(n + 1);
                        end;

                        foo(foo(1));
                        """,
                createInstruction(PRINT, q("23"))
        );
    }

    @Test
    void inlinesFunctionCallsInExpressions() {
        assertCompilesTo("""
                        def foo()
                            rand(10);
                        end;
                        print(foo() + foo());
                        """,
                createInstruction(OP, "rand", tmp(0), "10"),
                createInstruction(OP, "rand", ":foo*retval", "10"),
                createInstruction(OP, "add", tmp(2), tmp(0), ":foo*retval"),
                createInstruction(PRINT, tmp(2))
        );
    }


    @Test
    void inlinesFunctionCallsWithParametersInExpressions() {
        assertCompilesTo("""
                        def foo(n)
                            t = rand(n);
                            return t;
                        end;
                        print(foo(10) + foo(20));
                        """,
                createInstruction(OP, "rand", tmp(0), "10"),
                createInstruction(OP, "rand", ":fn0:t", "20"),
                createInstruction(OP, "add", tmp(2), tmp(0), ":fn0:t"),
                createInstruction(PRINT, tmp(2))
        );
    }

    @Test
    void handlesReturnsCorrectly() {
        assertCompilesTo("""
                        def foo()
                            t = rand(10);
                            return t;
                        end;
                        print(foo());
                        print(foo());
                        """,
                createInstruction(OP, "rand", ":fn0:t", "10"),
                createInstruction(PRINT, ":fn0:t"),
                createInstruction(OP, "rand", ":fn0:t", "10"),
                createInstruction(PRINT, ":fn0:t")
        );
    }

    @Test
    void respectsNoinlineFunctions() {
        assertCompilesTo("""
                        noinline def foo(n)
                            print(2 * n);
                        end;

                        foo(1);
                        foo(2);
                        """,
                createInstruction(SET, ":foo:n", "1"),
                createInstruction(SETADDR, ":foo*retaddr", label(1)),
                createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(1)),
                createInstruction(SET, ":foo:n", "2"),
                createInstruction(SETADDR, ":foo*retaddr", label(2)),
                createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(OP, "mul", tmp(2), "2", ":foo:n"),
                createInstruction(PRINT, tmp(2)),
                createInstruction(RETURN, ":foo*retaddr")
        );
    }

}