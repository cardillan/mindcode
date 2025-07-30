package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
public class LoopUnrollerBasicTest extends LoopUnrollerTestBase {

    @Override
    protected OptimizationLevel getLoopUnrollingLevel() {
        return OptimizationLevel.BASIC;
    }

    @Test
    void preservesNonIntegerLoops() {
        assertCompilesTo("""
                        for i = 0; i <= 1.0; i += 0.1 do
                            print(i);
                        end;
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(OP, "add", "i", "i", "0.1"),
                createInstruction(JUMP, var(1003), "lessThanEq", "i", "1")
        );
    }

    @Test
    void preservesShiftingLoop() {
        assertCompilesTo("""
                        for k = 1; k < 100000; k <<= 1 do
                            print($" $k");
                        end;
                        """,
                createInstruction(SET, "k", "1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q(" ")),
                createInstruction(PRINT, "k"),
                createInstruction(OP, "shl", "k", "k", "1"),
                createInstruction(JUMP, var(1003), "lessThan", "k", "100000")
        );
    }
}
