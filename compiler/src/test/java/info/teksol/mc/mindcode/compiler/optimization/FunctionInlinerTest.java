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
                createInstruction(OP, "mul", "__fn0_b", "c", "10"),
                createInstruction(SET, "x", "__fn0_b"),
                createInstruction(OP, "mul", "__fn0_b", "c", "20"),
                createInstruction(PRINT, "x"),
                createInstruction(PRINT, "__fn0_b")
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
                createInstruction(OP, "rand", ":fn0*retval", "10"),
                createInstruction(SET, ":a", ":fn0*retval"),
                createInstruction(OP, "rand", ":fn0*retval", "10"),
                createInstruction(PRINT, ":a"),
                createInstruction(PRINT, ":fn0*retval")
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
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(OP, "rand", "__fn0retval", "10"),
                createInstruction(OP, "add", var(2), var(0), "__fn0retval"),
                createInstruction(PRINT, var(2))
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
                createInstruction(OP, "rand", "__fn0_t", "10"),
                createInstruction(SET, var(0), "__fn0_t"),
                createInstruction(OP, "rand", "__fn0_t", "20"),
                createInstruction(OP, "add", var(2), var(0), "__fn0_t"),
                createInstruction(PRINT, var(2))
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
                createInstruction(OP, "rand", "__fn0_t", "10"),
                createInstruction(PRINT, "__fn0_t"),
                createInstruction(OP, "rand", "__fn0_t", "10"),
                createInstruction(PRINT, "__fn0_t")
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
                createInstruction(SET, ":fn0:n", "1"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, ":fn0:n", "2"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(OP, "mul", var(2), "2", ":fn0:n"),
                createInstruction(PRINT, var(2)),
                createInstruction(RETURN, ":fn0*retaddr")
        );
    }

}