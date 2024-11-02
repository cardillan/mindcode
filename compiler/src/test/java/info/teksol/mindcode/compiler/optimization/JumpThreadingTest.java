package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
class JumpThreadingTest extends AbstractOptimizerTest<JumpThreading> {

    @Override
    protected Class<JumpThreading> getTestedClass() {
        return JumpThreading.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.DEAD_CODE_ELIMINATION,
                Optimization.JUMP_OPTIMIZATION,
                Optimization.JUMP_THREADING,
                Optimization.TEMP_VARIABLES_ELIMINATION
        );
    }

    @Test
    void propagatesThroughUnconditionalTargets() {
        assertCompilesTo("""
                        if a then
                            if b then
                                print(b);
                            end;
                            print(a);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(JUMP, "__start__", "equal", "a", "false"),
                createInstruction(JUMP, var(1002), "equal", "b", "false"),
                createInstruction(PRINT, "b"),
                createInstruction(JUMP, var(1003), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "a"),
                createInstruction(JUMP, "__start__", "always"),
                createInstruction(END)
        );
    }

    @Test
    void propagatesThroughConditionalTargets() {
        assertCompilesTo("""
                        while c == null do
                            c = getlink(1);
                            if c == null then
                                print("Not found");
                            end;
                        end;
                        print("Done");
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "notEqual", "c", "null"),
                createInstruction(GETLINK, "c", "1"),
                createInstruction(JUMP, var(1002), "notEqual", "c", "null"),
                createInstruction(PRINT, q("Not found")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Done")),
                createInstruction(END)
        );
    }

    @Test
    void ignoresVolatileVariables() {
        assertCompilesTo("""
                        while @time < wait do
                            n += 1;
                            if @time < wait then
                                print("Waiting");
                            end;
                        end;
                        print("Done");
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "greaterThanEq", "@time", "wait"),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(JUMP, var(1000), "greaterThanEq", "@time", "wait"),
                createInstruction(PRINT, q("Waiting")),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Done")),
                createInstruction(END)
        );
    }

    @Test
    void propagatesGoto() {
        assertCompilesTo("""
                        def foo(n)
                            if n > 10 then
                                return 2;
                            else
                                return 0;
                            end;
                        end;
                        print(foo(2));
                        print(foo(3));
                        """,
                createInstruction(SET, "__fn0_n", "2"),
                createInstruction(SETADDR, "__fn0retaddr", var(1001)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1001), "__fn0"),
                createInstruction(SET, var(0), "__fn0retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(SET, "__fn0_n", "3"),
                createInstruction(SETADDR, "__fn0retaddr", var(1002)),
                createInstruction(CALL, var(1000), "__fn0retval"),
                createInstruction(GOTOLABEL, var(1002), "__fn0"),
                createInstruction(SET, var(1), "__fn0retval"),
                createInstruction(PRINT, var(1)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1004), "lessThanEq", "__fn0_n", "10"),
                createInstruction(SET, "__fn0retval", "2"),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(SET, var(3), "null"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, "__fn0retval", "0"),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(SET, var(3), "null"),
                createInstruction(LABEL, var(1005)),
                createInstruction(SET, "__fn0retval", var(3)),
                createInstruction(GOTO, "__fn0retaddr", "__fn0"),
                createInstruction(END)
        );
    }
}
