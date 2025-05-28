package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class ReturnOptimizerTest extends AbstractOptimizerTest<ReturnOptimizer> {

    @Override
    protected Class<ReturnOptimizer> getTestedClass() {
        return ReturnOptimizer.class;
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
    void optimizesRecursiveReturn() {
        assertCompilesTo("""
                        allocate stack in cell1;
                        def fib(n)
                            if n < 2 then
                                return n;
                            end;
                        
                            fib(n - 1) + fib(n - 2);
                        end;
                        print(fib(10));
                        """,
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":fib:n", "10"),
                createInstruction(CALLREC, "cell1", label(0), label(1), ":fib*retval"),
                createInstruction(LABEL, label(1)),
                createInstruction(PRINT, ":fib*retval"),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(3), "greaterThanEq", ":fib:n", "2"),
                createInstruction(SET, ":fib*retval", ":fib:n"),
                createInstruction(RETURNREC, "cell1"),
                createInstruction(LABEL, label(3)),
                createInstruction(PUSH, "cell1", ":fib:n"),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "1"),
                createInstruction(CALLREC, "cell1", label(0), label(5), ":fib*retval"),
                createInstruction(LABEL, label(5)),
                createInstruction(POP, "cell1", ":fib:n"),
                createInstruction(SET, tmp(4), ":fib*retval"),
                createInstruction(PUSH, "cell1", tmp(4)),
                createInstruction(OP, "sub", ":fib:n", ":fib:n", "2"),
                createInstruction(CALLREC, "cell1", label(0), label(6), ":fib*retval"),
                createInstruction(LABEL, label(6)),
                createInstruction(POP, "cell1", tmp(4)),
                createInstruction(OP, "add", ":fib*retval", tmp(4), ":fib*retval"),
                createInstruction(RETURNREC, "cell1")
        );
    }
}
