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
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1001), "equal", "cell1", "null"),
                createInstruction(SET, "*sp", "0"),
                createInstruction(SET, ":fn0:n", "10"),
                createInstruction(CALLREC, "cell1", var(1000), var(1002), ":fn0*retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, ":fn0*retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1004), "greaterThanEq", ":fn0:n", "2"),
                createInstruction(SET, ":fn0*retval", ":fn0:n"),
                createInstruction(RETURNREC, "cell1"),
                createInstruction(LABEL, var(1004)),
                createInstruction(PUSH, "cell1", ":fn0:n"),
                createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "1"),
                createInstruction(CALLREC, "cell1", var(1000), var(1006), ":fn0*retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "cell1", ":fn0:n"),
                createInstruction(SET, var(4), ":fn0*retval"),
                createInstruction(PUSH, "cell1", var(4)),
                createInstruction(OP, "sub", ":fn0:n", ":fn0:n", "2"),
                createInstruction(CALLREC, "cell1", var(1000), var(1007), ":fn0*retval"),
                createInstruction(LABEL, var(1007)),
                createInstruction(POP, "cell1", var(4)),
                createInstruction(OP, "add", ":fn0*retval", var(4), ":fn0*retval"),
                createInstruction(RETURNREC, "cell1")
        );
    }
}
