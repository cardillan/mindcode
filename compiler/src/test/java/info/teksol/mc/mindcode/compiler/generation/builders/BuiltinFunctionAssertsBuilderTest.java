package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class BuiltinFunctionAssertsBuilderTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesAssertEquals() {
        assertCompilesTo("""
                        allocate heap in bank1;
                        $A = 10;
                        assertEquals(10, $A, "value from memory");
                        """,
                createInstruction(WRITE, "10", "bank1", "0"),
                createInstruction(READ, tmp(0), "bank1", "0"),
                createInstruction(ASSERT_EQUALS, "10", tmp(0), q("value from memory"))
        );
    }

    @Test
    void compilesAssertPrints() {
        assertCompilesTo("""
                        assertPrints("10", print(1, 0), "print test");
                        """,
                createInstruction(ASSERT_FLUSH),
                createInstruction(PRINT, "1"),
                createInstruction(PRINT, "0"),
                createInstruction(ASSERT_PRINTS, q("10"), q("print test"))
        );
    }
}