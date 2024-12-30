package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.SET;

@NullMarked
class LiteralsBuilderTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesColorLiterals() {
        assertCompilesTo("""
                        a = %ABCDEF;
                        a = %ABCDEF00;
                        a = %abcdefff;
                        """,
                createInstruction(SET, "a", "%ABCDEF"),
                createInstruction(SET, "a", "%ABCDEF00"),
                createInstruction(SET, "a", "%abcdefff")
        );
    }

    @Test
    void compilesStringLiterals() {
        assertCompilesTo("""
                        a = "";
                        a = "A string literal";
                        """,
                createInstruction(SET, "a", q("")),
                createInstruction(SET, "a", q("A string literal"))
        );
    }

    @Test
    void compilesFloatLiterals() {
        assertCompilesTo("""
                        a = 1.0;
                        a = 0.0;
                        a = .5;
                        a = 1e5;
                        a = 1e+5;
                        a = 1e-5;
                        a = 1E10;
                        a = 1E+10;
                        a = 1E-10;
                        a = 2.5E10;
                        a = 2.5E+10;
                        a = 2.5E-10;
                        a = .5E10;
                        a = .5E+10;
                        a = .5E-10;
                        a = 1.5E+30;
                        a = 1.5E-30;
                        """,
                createInstruction(SET, "a", "1"),
                createInstruction(SET, "a", "0"),
                createInstruction(SET, "a", "0.5"),
                createInstruction(SET, "a", "100000"),
                createInstruction(SET, "a", "100000"),
                createInstruction(SET, "a", "0.00001"),
                createInstruction(SET, "a", "10000000000"),
                createInstruction(SET, "a", "10000000000"),
                createInstruction(SET, "a", "0.0000000001"),
                createInstruction(SET, "a", "25000000000"),
                createInstruction(SET, "a", "25000000000"),
                createInstruction(SET, "a", "0.00000000025"),
                createInstruction(SET, "a", "5000000000"),
                createInstruction(SET, "a", "5000000000"),
                createInstruction(SET, "a", "0.00000000005"),
                createInstruction(SET, "a", "15E29"),
                createInstruction(SET, "a", "15E-31")
        );
    }

    @Test
    void compilesNegativeFloatLiterals() {
        assertCompilesTo("""
                        a = -1.0;
                        a = -0.0;
                        a = -.5;
                        a = -1e5;
                        a = -1e+5;
                        a = -1e-5;
                        a = -1E10;
                        a = -1E+10;
                        a = -1E-10;
                        a = -2.5E10;
                        a = -2.5E+10;
                        a = -2.5E-10;
                        a = -.5E10;
                        a = -.5E+10;
                        a = -.5E-10;
                        a = -1.5E+30;
                        a = -1.5E-30;
                        """,
                createInstruction(SET, "a", "-1"),
                createInstruction(SET, "a", "0"),
                createInstruction(SET, "a", "-0.5"),
                createInstruction(SET, "a", "-100000"),
                createInstruction(SET, "a", "-100000"),
                createInstruction(SET, "a", "-0.00001"),
                createInstruction(SET, "a", "-10000000000"),
                createInstruction(SET, "a", "-10000000000"),
                createInstruction(SET, "a", "-0.0000000001"),
                createInstruction(SET, "a", "-25000000000"),
                createInstruction(SET, "a", "-25000000000"),
                createInstruction(SET, "a", "-0.00000000025"),
                createInstruction(SET, "a", "-5000000000"),
                createInstruction(SET, "a", "-5000000000"),
                createInstruction(SET, "a", "-0.00000000005"),
                createInstruction(SET, "a", "-15E29"),
                createInstruction(SET, "a", "-15E-31")
        );
    }

    @Test
    void compilesNumericLiterals() {
        assertCompilesTo("""
                        a = 0b0011;
                        a = 0x0123456789ABCDEF;
                        a = 0xfedcba987654321;
                        a = 0;
                        a = 01;
                        a = 123;
                        """,
                createInstruction(SET, "a", "0b0011"),
                createInstruction(SET, "a", "0x0123456789ABCDEF"),
                createInstruction(SET, "a", "0xfedcba987654321"),
                createInstruction(SET, "a", "0"),
                createInstruction(SET, "a", "1"),
                createInstruction(SET, "a", "123")
        );
    }

    @Test
    void compilesNegativeNumericLiterals() {
        assertCompilesTo("""
                        a = -0b0011;
                        a = -0x0123456789ABCDEF;
                        a = -0xfedcba987654321;
                        a = -0;
                        a = -01;
                        a = -123;
                        """,
                createInstruction(SET, "a", "-3"),
                createInstruction(SET, "a", "-81985529216486900"),
                createInstruction(SET, "a", "-1147797409030816500"),
                createInstruction(SET, "a", "0"),
                createInstruction(SET, "a", "-1"),
                createInstruction(SET, "a", "-123")
        );
    }

    @Test
    void compilesOtherLiterals() {
        assertCompilesTo("""
                        a = null;
                        a = true;
                        a = false;
                        """,
                createInstruction(SET, "a", "null"),
                createInstruction(SET, "a", "true"),
                createInstruction(SET, "a", "false")
        );
    }
}