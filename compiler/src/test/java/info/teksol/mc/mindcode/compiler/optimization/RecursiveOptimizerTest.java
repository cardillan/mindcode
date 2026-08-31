package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class RecursiveOptimizerTest extends AbstractOptimizerTest<RecursiveOptimizer> {

    @Override
    protected Class<RecursiveOptimizer> getTestedClass() {
        return RecursiveOptimizer.class;
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
    void optimizesRecursiveReturnExt() {
        assertCompilesTo("""
                        allocate stack in cell1;
                        noinline def fib(n)
                            if n < 2 then
                                return n;
                            end;
                        
                            fib(n - 1) + fib(n - 2);
                        end;
                        print(fib(10));
                        """,
                createInstruction(INITSTACK, label(1), label(2)),
                createInstruction(SET, ":fib:n", "10"),
                createInstruction(CALLREC, "cell1", label(0), label(3), ":fib*retval"),
                createInstruction(LABEL, label(3)),
                createInstruction(PRINT, ":fib*retval"),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(ASSERT_BOUNDS, "decimal", "1", "0", "lessThanEq", "*sp", "lessThan", "64", q("position 2:1: stack overflow error")),
                createInstruction(INITREC, "true"),
                createInstruction(JUMP, label(5), "greaterThanEq", ":fib:n", "2"),
                createInstruction(SET, ":fib*retval", ":fib:n"),
                createInstruction(RETURNREC, "cell1"),
                createInstruction(LABEL, label(5)),
                createInstruction(PUSH, "cell1", ":fib:n"),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "1"),
                createInstruction(CALLREC, "cell1", label(0), label(7), ":fib*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "cell1", ":fib:n"),
                createInstruction(SET, tmp(4), ":fib*retval"),
                createInstruction(PUSH, "cell1", tmp(4)),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "2"),
                createInstruction(CALLREC, "cell1", label(0), label(8), ":fib*retval"),
                createInstruction(LABEL, label(8)),
                createInstruction(POP, "cell1", tmp(4)),
                createInstruction(OP, "add", ":fib*retval", tmp(4), ":fib*retval"),
                createInstruction(RETURNREC, "cell1")
        );
    }

    @Test
    void optimizesRecursiveReturnInt() {
        assertCompilesTo("""
                        noinline def fib(n)
                            if n < 2 then
                                return n;
                            end;
                        
                            fib(n - 1) + fib(n - 2);
                        end;
                        print(fib(10));
                        """,
                ix -> !ix.getAstContext().matches(AstContextType.STACK, AstSubcontextType.BASIC),

                createInstruction(INITSTACK, label(1), label(2)),
                createInstruction(SET, ":fib:n", "10"),
                createInstruction(CALLREC, "bank0", label(0), label(3), ":fib*retval"),
                createInstruction(LABEL, label(3)),
                createInstruction(PRINT, ":fib*retval"),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(INITREC, "false"),
                createInstruction(JUMP, label(5), "greaterThanEq", ":fib:n", "2"),
                createInstruction(SET, ":fib*retval", ":fib:n"),
                createInstruction(RETURNREC, "bank0"),
                createInstruction(LABEL, label(5)),
                createInstruction(PUSH, "bank0", ":fib:n"),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "1"),
                createInstruction(CALLREC, "bank0", label(0), label(7), ":fib*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "bank0", ":fib:n"),
                createInstruction(SET, tmp(4), ":fib*retval"),
                createInstruction(PUSH, "bank0", tmp(4)),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "2"),
                createInstruction(CALLREC, "bank0", label(0), label(8), ":fib*retval"),
                createInstruction(LABEL, label(8)),
                createInstruction(POP, "bank0", tmp(4)),
                createInstruction(OP, "add", ":fib*retval", tmp(4), ":fib*retval"),
                createInstruction(RETURNREC, "bank0")
        );
    }
}
