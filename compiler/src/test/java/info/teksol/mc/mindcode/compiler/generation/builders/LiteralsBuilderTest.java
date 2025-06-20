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
    void compilesNamedColorLiterals() {
        assertCompilesTo("""
                        a = %[red];
                        a = %[salmon];
                        a = %[yellow];
                        """,
                createInstruction(SET, ":a", "%[red]"),
                createInstruction(SET, ":a", "%[salmon]"),
                createInstruction(SET, ":a", "%[yellow]")
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
                        a = 0x0123456789ABC;
                        a = 0xfedcba9876543;
                        a = 0;
                        a = 01;
                        a = 123;
                        a = 4.9e-324;
                        """,
                createInstruction(SET, ":a", "0b0011"),
                createInstruction(SET, ":a", "0x0123456789ABC"),
                createInstruction(SET, ":a", "0xfedcba9876543"),
                createInstruction(SET, ":a", "0"),
                createInstruction(SET, ":a", "01"),
                createInstruction(SET, ":a", "123"),
                createInstruction(SET, ":a", "49E-325")
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
                createInstruction(SET, ":a", "-0b11"),
                createInstruction(SET, ":a", "-0x123456789abcdf0"),
                createInstruction(SET, ":a", "-0xfedcba987654300"),
                createInstruction(SET, ":a", "0"),
                createInstruction(SET, ":a", "-1"),
                createInstruction(SET, ":a", "-123")
        );
    }

    @Test
    void compilesOtherLiterals() {
        assertCompilesTo("""
                        a = null;
                        a = true;
                        a = false;
                        a = 'A';
                        """,
                createInstruction(SET, "a", "null"),
                createInstruction(SET, "a", "true"),
                createInstruction(SET, "a", "false"),
                createInstruction(SET, "a", "65")
        );
    }

    @Test
    void generatesNoImprecisionWarningForFloatLiteralsVersion8() {
        assertCompiles("""
                #set target = 8;
                var a = 1e25;
                """
        );
    }

    @Test
    void generatesImprecisionWarningForFloatLiteralsVersion7() {
        assertGeneratesMessage(
                "Loss of precision while creating mlog literal (original value 1e25, encoded value 9.999999562023526E24).",
                """
                        #set target = 7;
                        var a = 1e25;
                        """
        );
    }

    @Test
    void generatesImprecisionWarningForDecimalLiterals() {
        assertGeneratesMessage(
                "Literal '9007199254740992' exceeds safe range for integer operations (0 ... 2**53).",
                """
                        var a = 9007199254740991;
                        var b = 9007199254740992;
                        """
        );
    }

    @Test
    void generatesImprecisionWarningForHexadecimalLiterals() {
        assertGeneratesMessage(
                "Literal '0x20000000000000' exceeds safe range for integer operations (0 ... 2**53).",
                """
                        var a = 0x1FFFFFFFFFFFFF;
                        var b = 0x20000000000000;
                        """
        );
    }

    @Test
    void generatesImprecisionWarningForBinaryLiterals() {
        assertGeneratesMessage(
                "Literal '0b100000000000000000000000000000000000000000000000000000' exceeds safe range for integer operations (0 ... 2**53).",
                """
                        var a = 0b011111111111111111111111111111111111111111111111111111;
                        var b = 0b100000000000000000000000000000000000000000000000000000;
                        """
        );
    }

    @Test
    void refusesTooBigDecimalLiterals() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Literal '9223372036854775807' exceeds safe range for integer operations (0 ... 2**53).")
                        .add("Literal '9223372036854775808' exceeds maximum possible value (9223372036854775807)."),
                """
                        var a = 9223372036854775807;
                        var b = 9223372036854775808;
                        """
        );
    }

    @Test
    void refusesTooBigHexadecimalLiterals() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Literal '0x7fffffffffffffff' exceeds safe range for integer operations (0 ... 2**53).")
                        .add("Value '-9223372036854775808' does not have a valid mlog representation."),
                """
                        var a = 0x7fffffffffffffff;
                        var b = 0x8000000000000000;
                        """
        );
    }

    @Test
    void refusesTooBigBinaryLiterals() {
        assertGeneratesMessages(
                expectedMessages()
                        .add("Literal '0b0111111111111111111111111111111111111111111111111111111111111111' exceeds safe range for integer operations (0 ... 2**53).")
                        .add("Value '-9223372036854775808' does not have a valid mlog representation."),
                """
                        var a = 0b0111111111111111111111111111111111111111111111111111111111111111;
                        var b = 0b1000000000000000000000000000000000000000000000000000000000000000;
                        """
        );
    }

    @Test
    void refusesColorLiteralsIn6() {
        assertGeneratesMessage(
                "Color literals require language target 7 or higher.",
                "#set target = 6; a = %123456;");
    }

    @Test
    void refusesNamedColorLiteralsIn7() {
        assertGeneratesMessage(
                "Named color literals require language target 8 or higher.",
                "#set target = 7; a = %[red];");
    }

    @Test
    void generatesWarningForUnrecognizedColors() {
        assertGeneratesMessage(
                "Named color 'fluffybunny' not recognized.",
                "a = %[fluffybunny];");
    }
}