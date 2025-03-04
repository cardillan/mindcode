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
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(1), "equal", "cell1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":fib.0:n", "10"),
                createInstruction(CALLREC, "cell1", label(0), label(2), ":fib.0*retval"),
                createInstruction(LABEL, label(2)),
                createInstruction(PRINT, ":fib.0*retval"),
                createInstruction(END),
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(4), "greaterThanEq", ":fib.0:n", "2"),
                createInstruction(SET, ":fib.0*retval", ":fib.0:n"),
                createInstruction(RETURNREC, "cell1"),
                createInstruction(LABEL, label(4)),
                createInstruction(PUSH, "cell1", ":fib.0:n"),
                createInstruction(OP, "sub", ":fib.0:n", ":fib.0:n", "1"),
                createInstruction(CALLREC, "cell1", label(0), label(6), ":fib.0*retval"),
                createInstruction(LABEL, label(6)),
                createInstruction(POP, "cell1", ":fib.0:n"),
                createInstruction(SET, tmp(4), ":fib.0*retval"),
                createInstruction(PUSH, "cell1", tmp(4)),
                createInstruction(OP, "sub", ":fib.0:n", ":fib.0:n", "2"),
                createInstruction(CALLREC, "cell1", label(0), label(7), ":fib.0*retval"),
                createInstruction(LABEL, label(7)),
                createInstruction(POP, "cell1", tmp(4)),
                createInstruction(OP, "add", ":fib.0*retval", tmp(4), ":fib.0*retval"),
                createInstruction(RETURNREC, "cell1")
        );
    }
}
