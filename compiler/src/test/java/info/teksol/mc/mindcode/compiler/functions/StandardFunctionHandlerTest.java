package info.teksol.mc.mindcode.compiler.functions;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.UCONTROL;

class StandardFunctionHandlerTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesMissingFunctionArgument() {
        assertCompilesTo("""
                        getBlock(x, y, , out floor);
                        """,
                createInstruction(UCONTROL, "getBlock", ":x", ":y", tmp(1), tmp(0), ":floor")
        );
    }
}