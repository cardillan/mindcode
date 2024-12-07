package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class BuiltinFunctionVarargsBuilderTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesLengthFunction() {
        assertCompilesTo("""
                            inline void foo(args...)
                                print(length(args));
                            end;
                            inline void bar(args...)
                                foo(args, args);
                            end;
                            foo();
                            bar(1, 2, 3);
                            print(length(10));
                            """,
                createInstruction(PRINT, "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(PRINT, "6"),
                createInstruction(LABEL, var(1002)),
                createInstruction(LABEL, var(1001)),
                createInstruction(PRINT, "1")
        );
    }

    @Test
    void compilesMinMaxFunctions() {
        assertCompilesTo(
                "r = min(x, max(y, 2));",
                createInstruction(OP, "max", var(0), "y", "2"),
                createInstruction(OP, "min", var(1), "x", var(0)),
                createInstruction(SET, "r", var(1))
        );
    }

    @Test
    void compilesMultiMinMaxFunctions() {
        assertCompilesTo("""
                        x = min(a, b, c, d);
                        y = max(a, b, c, d);
                        """,
                createInstruction(OP, "min", var(0), "a", "b"),
                createInstruction(OP, "min", var(0), var(0), "c"),
                createInstruction(OP, "min", var(0), var(0), "d"),
                createInstruction(SET, "x", var(0)),
                createInstruction(OP, "max", var(1), "a", "b"),
                createInstruction(OP, "max", var(1), var(1), "c"),
                createInstruction(OP, "max", var(1), var(1), "d"),
                createInstruction(Opcode.SET, "y", var(1))
        );
    }

    @Test
    void reportsWrongNumberOfMinMxParameters() {
        assertGeneratesMessages(expectedMessages()
                        .add("Not enough arguments to the 'min' function (expected 2 or more, found 0).")
                        .add("Not enough arguments to the 'max' function (expected 2 or more, found 0).")
                        .add("Not enough arguments to the 'min' function (expected 2 or more, found 1).")
                        .add("Not enough arguments to the 'max' function (expected 2 or more, found 1)."),
                """
                        a = min();
                        b = max();
                        c = min(a);
                        d = max(b);
                        """
        );
    }
}