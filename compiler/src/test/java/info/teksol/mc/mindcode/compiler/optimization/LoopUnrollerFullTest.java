package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.PRINT;

@NullMarked
public class LoopUnrollerFullTest extends LoopUnrollerTestBase {
    public LoopUnrollerFullTest() {
        super(OptimizationLevel.EXPERIMENTAL);
    }

    @Test
    void unrollsNonIntegerLoops() {
        assertCompilesTo("""
                        for i = 0; i <= 1.0; i += 0.1 do
                            print(floor(i * 10 + 0.5), ":");
                        end;
                        """,
                createInstruction(PRINT, q("0:1:2:3:4:5:6:7:8:9:10:"))
        );
    }

    @Test
    void unrollsShiftingLoop() {
        assertCompilesTo("""
                        for k = 1; k < 100000; k <<= 1 do
                            print($" $k");
                        end;
                        """,
                excludeLabelInstructions(),
                createInstruction(PRINT, q(" 1 2 4 8 16 32 64 128 256 512 1024 2048 4096 8192 16384 32768 65536"))
        );
    }
}
