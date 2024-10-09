package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.GenerationGoal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

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
                createInstruction(SET, "__sp", "0"),
                createInstruction(SET, "__fn0_n", "10"),
                createInstruction(CALLREC, "cell1", var(1000), var(1001), "__fn0retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "__fn0retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1003), "greaterThanEq", "__fn0_n", "2"),
                createInstruction(SET, "__fn0retval", "__fn0_n"),
                createInstruction(RETURN, "cell1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PUSH, "cell1", "__fn0_n"),
                createInstruction(OP, "sub", "__fn0_n", "__fn0_n", "1"),
                createInstruction(CALLREC, "cell1", var(1000), var(1005), "__fn0retval"),
                createInstruction(LABEL, var(1005)),
                createInstruction(POP, "cell1", "__fn0_n"),
                createInstruction(SET, var(4), "__fn0retval"),
                createInstruction(PUSH, "cell1", var(4)),
                createInstruction(OP, "sub", "__fn0_n", "__fn0_n", "2"),
                createInstruction(CALLREC, "cell1", var(1000), var(1006), "__fn0retval"),
                createInstruction(LABEL, var(1006)),
                createInstruction(POP, "cell1", var(4)),
                createInstruction(OP, "add", "__fn0retval", var(4), "__fn0retval"),
                createInstruction(RETURN, "cell1")
        );
    }
}
