package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
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
    void propagatesReturn() {
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
                createInstruction(SET, ":fn0:n", "2"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(SET, var(0), ":fn0*retval"),
                createInstruction(PRINT, var(0)),
                createInstruction(SET, ":fn0:n", "3"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1002)),
                createInstruction(SET, var(1), ":fn0*retval"),
                createInstruction(PRINT, var(1)),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1004), "lessThanEq", ":fn0:n", "10"),
                createInstruction(SET, ":fn0*retval", "2"),
                createInstruction(RETURN, ":fn0*retaddr"),
                createInstruction(SET, var(3), "null"),
                createInstruction(JUMP, var(1005), "always"),
                createInstruction(LABEL, var(1004)),
                createInstruction(SET, ":fn0*retval", "0"),
                createInstruction(RETURN, ":fn0*retaddr"),
                createInstruction(SET, var(3), "null"),
                createInstruction(LABEL, var(1005)),
                createInstruction(SET, ":fn0*retval", var(3)),
                createInstruction(RETURN, ":fn0*retaddr")
        );
    }

    @Test
    void propagatesReturnOnlyToUnconditionalJump() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        noinline def foo(x2, x3, x4, x5)
                            if x3 > x2 then
                                x2 < x4 ? min(x3, x4) : min(x2, x5);
                            else
                                x3 > x4 ? min(x3, x5) : min(x2, x4);
                            end;
                        end;
                        print(foo(2, 3, 4, 5));
                        """,
                createInstruction(SET, ":fn0:x2", "2"),
                createInstruction(SET, ":fn0:x3", "3"),
                createInstruction(SET, ":fn0:x4", "4"),
                createInstruction(SET, ":fn0:x5", "5"),
                createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                createInstruction(CALL, var(1000), ":fn0*retval"),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, ":fn0*retval"),
                createInstruction(END),
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1003), "lessThanEq", ":fn0:x3", ":fn0:x2"),
                createInstruction(OP, "min", ":fn0*retval", ":fn0:x2", ":fn0:x5"),
                createInstruction(JUMP, var(1004), "greaterThanEq", ":fn0:x2", ":fn0:x4"),
                createInstruction(OP, "min", ":fn0*retval", ":fn0:x3", ":fn0:x4"),
                createInstruction(RETURN, ":fn0*retaddr"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "min", ":fn0*retval", ":fn0:x2", ":fn0:x4"),
                createInstruction(JUMP, var(1008), "lessThanEq", ":fn0:x3", ":fn0:x4"),
                createInstruction(OP, "min", ":fn0*retval", ":fn0:x3", ":fn0:x5"),
                createInstruction(LABEL, var(1008)),
                createInstruction(LABEL, var(1004)),
                createInstruction(RETURN, ":fn0*retaddr")
        );
    }
}
