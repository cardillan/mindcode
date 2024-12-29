package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.v3.compiler.generation.AbstractCodeGeneratorTest;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.SET;

class LiteralsBuilderTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesSimpleArrayAccess() {
        assertCompiles("""
                            a = %ABCDEF;
                            b = %ABCDEF00;
                            c = %abcdefff;
                            """,
                createInstruction(SET, "a", "%ABCDEF"),
                createInstruction(SET, "b", "%ABCDEF00"),
                createInstruction(SET, "c", "%abcdefff")
        );
    }

}