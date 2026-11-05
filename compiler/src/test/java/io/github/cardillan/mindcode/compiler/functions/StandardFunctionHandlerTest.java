package io.github.cardillan.mindcode.compiler.functions;

import io.github.cardillan.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Test;

import static io.github.cardillan.mindcode.logic.opcodes.Opcode.UCONTROL;

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
