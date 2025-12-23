package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class AssignmentsBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class ArrayAssignments {

        @Test
        void compilesInternalToInternalArrayAssignment() {
            assertCompilesTo("""
                            var a[3], b[3];
                            a = b;
                            """,
                    createInstruction(SET, ".a*0", ".b*0"),
                    createInstruction(SET, ".a*1", ".b*1"),
                    createInstruction(SET, ".a*2", ".b*2")
            );
        }

        @Test
        void compilesInternalToExternalArrayAssignment() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            external a[3];
                            var b[3];
                            a = b;
                            """,
                    createInstruction(WRITE, ".b*0", "bank1", "0"),
                    createInstruction(WRITE, ".b*1", "bank1", "1"),
                    createInstruction(WRITE, ".b*2", "bank1", "2")
            );
        }

        @Test
        void compilesExternalToInternalArrayAssignment() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            var a[3];
                            external b[3];
                            a = b;
                            """,
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(SET, ".a*0", tmp(0)),
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(SET, ".a*1", tmp(1)),
                    createInstruction(READ, tmp(2), "bank1", "2"),
                    createInstruction(SET, ".a*2", tmp(2))
            );
        }

        @Test
        void compilesExternalToExternalArrayAssignment() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            external a[3], b[3], c[3];
                            b = c;
                            """,
                    createInstruction(SET, tmp(9), "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(10), tmp(9), "3"),
                    createInstruction(OP, "add", tmp(12), tmp(9), "6"),
                    createInstruction(READARR, tmp(13), ".c[]", tmp(12)),
                    createInstruction(WRITEARR, tmp(13), ".b[]", tmp(10)),
                    createInstruction(OP, "add", tmp(9), tmp(9), "1"),
                    createInstruction(JUMP, label(0), "lessThan", tmp(9), "3"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesChainArrayAssignment() {
            assertCompilesTo("""
                            var a[3], b[3], c[3];
                            a = b = c;
                            """,
                    createInstruction(SET, ".b*0", ".c*0"),
                    createInstruction(SET, ".b*1", ".c*1"),
                    createInstruction(SET, ".b*2", ".c*2"),
                    createInstruction(SET, ".a*0", ".b*0"),
                    createInstruction(SET, ".a*1", ".b*1"),
                    createInstruction(SET, ".a*2", ".b*2")
            );
        }

        @Test
        void refusesCompoundArrayAssignments() {
            assertGeneratesMessage(
                    "Unsupported array expression.",
                    "var a[10]; a = 5;"
            );
        }

        @Test
        void refusesPostfixArrayOperators() {
            assertGeneratesMessage(
                    "Unsupported array expression.",
                    "var a[10]; a++;"
            );
        }

        @Test
        void refusesPrefixArrayOperators() {
            assertGeneratesMessage(
                    "Unsupported array expression.",
                    "var a[10]; --a;"
            );
        }

        @Test
        void refusesAssignmentsToConstantArrayElement() {
            assertGeneratesMessage(
                    "Assignment to an element of a constant array not allowed.",
                    "const a[] = (1); a[0]++;"
            );
        }
    }

    @Nested
    class CompoundAssignments {
        @Test
        void compilesPowerAssignment() {
            assertCompilesTo("""
                            c = a **= b;
                            """,
                    createInstruction(OP, "pow", ":a", ":a", ":b"),
                    createInstruction(SET, ":c", ":a")
            );
        }

        @Test
        void compilesMultiplications() {
            assertCompilesTo("""
                            a *= b;
                            c /= d;
                            e \\= f;
                            g %= h;
                            i %%= j;
                            """,
                    createInstruction(OP, "mul", ":a", ":a", ":b"),
                    createInstruction(OP, "div", ":c", ":c", ":d"),
                    createInstruction(OP, "idiv", ":e", ":e", ":f"),
                    createInstruction(OP, "mod", ":g", ":g", ":h"),
                    createInstruction(OP, "emod", ":i", ":i", ":j")
            );
        }

        @Test
        void compilesAdditions() {
            assertCompilesTo("""
                            a += b;
                            c -= d;
                            """,
                    createInstruction(OP, "add", ":a", ":a", ":b"),
                    createInstruction(OP, "sub", ":c", ":c", ":d")
            );
        }

        @Test
        void compilesShiftOperations() {
            assertCompilesTo("""
                            a <<= b;
                            c >>= d;
                            e >>>= f;
                            """,
                    createInstruction(OP, "shl", ":a", ":a", ":b"),
                    createInstruction(OP, "shr", ":c", ":c", ":d"),
                    createInstruction(OP, "ushr", ":e", ":e", ":f")
            );
        }

        @Test
        void compilesAndOperations() {
            assertCompilesTo("""
                            a &= b;
                            c &&= d;
                            """,
                    createInstruction(OP, "and", ":a", ":a", ":b"),
                    createInstruction(OP, "land", ":c", ":c", ":d")
            );
        }

        @Test
        void compilesOrXorOperations() {
            assertCompilesTo("""
                            a |= b;
                            c ||= d;
                            g ^= h;
                            """,
                    createInstruction(OP, "or", ":a", ":a", ":b"),
                    createInstruction(OP, "lor", ":c", ":c", ":d"),
                    createInstruction(OP, "xor", ":g", ":g", ":h")
            );
        }

        @Test
        void compilesExternalVariablesWithCompoundAssignments() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A += $B;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(READ, tmp(0), "bank1", "0"),
                    createInstruction(OP, "add", tmp(2), tmp(0), tmp(1)),
                    createInstruction(WRITE, tmp(2), "bank1", "0")
            );
        }

        @Test
        void emulatesEmodInTarget7() {
            assertCompilesTo("""
                            #set target = 7;
                            e %%= f;
                            """,
                    createInstruction(OP, "mod", ":e", ":e", ":f"),
                    createInstruction(OP, "add", ":e", ":e", ":f"),
                    createInstruction(OP, "mod", ":e", ":e", ":f")
            );
        }

        @Test
        void emulatesUshrInTarget7() {
            assertCompilesTo("""
                            #set target = 7;
                            a >>>= b;
                            """,
                    createInstruction(SET, ":a", ":a"),
                    createInstruction(OP, "and", tmp(0), ":b", "63"),
                    createInstruction(JUMP, label(0), "lessThanEq", tmp(0), "0"),
                    createInstruction(OP, "shr", ":a", ":a", "1"),
                    createInstruction(OP, "and", ":a", ":a", "0x7FFFFFFFFFFFFFFF"),
                    createInstruction(OP, "sub", tmp(0), tmp(0), "1"),
                    createInstruction(OP, "shr", ":a", ":a", tmp(0)),
                    createInstruction(LABEL, label(0))
            );
        }
    }

    @Nested
    class IncrementDecrementOperators {

        @Test
        void compilesBasicPrefixOperators() {
            assertCompilesTo("""
                            a = ++b;
                            c = --d;
                            """,
                    createInstruction(OP, "add", ":b", ":b", "1"),
                    createInstruction(SET, ":a", ":b"),
                    createInstruction(OP, "sub", ":d", ":d", "1"),
                    createInstruction(SET, ":c", ":d")
            );
        }

        @Test
        void compilesBasicPostfixOperators() {
            assertCompilesTo("""
                            a = b++;
                            c = d--;
                            """,
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(OP, "add", ":b", ":b", "1"),
                    createInstruction(SET, ":a", tmp(0)),
                    createInstruction(SET, tmp(1), ":d"),
                    createInstruction(OP, "sub", ":d", ":d", "1"),
                    createInstruction(SET, ":c", tmp(1))
            );
        }

        @Test
        void compilesComplexPrefixPostfixExpressions() {
            assertCompilesTo("""
                            a = b++ * --c;
                            """,
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(OP, "add", ":b", ":b", "1"),
                    createInstruction(OP, "sub", ":c", ":c", "1"),
                    createInstruction(OP, "mul", tmp(1), tmp(0), ":c"),
                    createInstruction(SET, ":a", tmp(1))
            );
        }

        @Test
        void compilesExternalVariablePrefixPostfixExpressions() {
            assertCompilesTo("""
                            allocate heap in bank1;
                            $A = $B++ * --$C;
                            """,
                    createInstruction(READ, tmp(1), "bank1", "1"),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(OP, "add", tmp(1), tmp(1), "1"),
                    createInstruction(WRITE, tmp(1), "bank1", "1"),
                    createInstruction(READ, tmp(3), "bank1", "2"),
                    createInstruction(OP, "sub", tmp(4), tmp(3), "1"),
                    createInstruction(WRITE, tmp(4), "bank1", "2"),
                    createInstruction(OP, "mul", tmp(5), tmp(2), tmp(4)),
                    createInstruction(WRITE, tmp(5), "bank1", "0")
            );
        }

        @Test
        void compilesArrayAccessPrefixPostfixExpressions() {
            assertCompilesTo("""
                            cell1[a] = cell2[b]++ * --cell3[c];
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(READ, tmp(3), "cell2", tmp(2)),
                    createInstruction(SET, tmp(4), tmp(3)),
                    createInstruction(OP, "add", tmp(3), tmp(3), "1"),
                    createInstruction(WRITE, tmp(3), "cell2", tmp(2)),
                    createInstruction(SET, tmp(5), ":c"),
                    createInstruction(READ, tmp(6), "cell3", tmp(5)),
                    createInstruction(OP, "sub", tmp(7), tmp(6), "1"),
                    createInstruction(WRITE, tmp(7), "cell3", tmp(5)),
                    createInstruction(OP, "mul", tmp(8), tmp(4), tmp(7)),
                    createInstruction(WRITE, tmp(8), "cell1", tmp(0))
            );
        }

        @Test
        void compilesArrayAccessPrefixPostfixIndexExpressions() {
            assertCompilesTo("""
                            cell1[a++]++;
                            cell2[--a]--;
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(OP, "add", ":a", ":a", "1"),
                    createInstruction(SET, tmp(1), tmp(0)),
                    createInstruction(READ, tmp(2), "cell1", tmp(1)),
                    createInstruction(SET, tmp(3), tmp(2)),
                    createInstruction(OP, "add", tmp(2), tmp(2), "1"),
                    createInstruction(WRITE, tmp(2), "cell1", tmp(1)),
                    createInstruction(OP, "sub", ":a", ":a", "1"),
                    createInstruction(SET, tmp(4), ":a"),
                    createInstruction(READ, tmp(5), "cell2", tmp(4)),
                    createInstruction(SET, tmp(6), tmp(5)),
                    createInstruction(OP, "sub", tmp(5), tmp(5), "1"),
                    createInstruction(WRITE, tmp(5), "cell2", tmp(4))
            );
        }

        @Test
        void refusesAssignmentToLiteral() {
            assertGeneratesMessage(
                    "Variable expected.",
                    "10 = a;");
        }
    }

    @Nested
    class InvalidAssignments {
        @Test
        void refusesAssignmentToLiteral() {
            assertGeneratesMessage(
                    "Variable expected.",
                    "10 = a;");
        }

        @Test
        void refusesAssignmentToConstants() {
            assertGeneratesMessage(
                    "Assignment to constant or parameter 'a' not allowed.",
                    """
                            const a = 10;
                            a = 20;
                            """);
        }

        @Test
        void refusesAssignmentToParameters() {
            assertGeneratesMessage(
                    "Assignment to constant or parameter 'a' not allowed.",
                    """
                            param a = 10;
                            a = 20;
                            """);
        }

        @Test
        void refusesAssignmentToExpression() {
            assertGeneratesMessage(
                    "Cannot assign a value to this expression.",
                    """
                            a + b = c;
                            """);
        }

        @Test
        void refusesAssigningFormattables() {
            assertGeneratesMessageRegex(
                    "A formattable string literal can only be used as a first argument to the .* functions\\.",
                    """
                            a = $"Hello";
                            """);
        }

        @Test
        void generatesNoValueWarnings() {
            assertGeneratesMessages(
                    expectedMessages()
                            .add("Expression doesn't have any value. Using value-less expressions in assignments is deprecated.")
                            .add("Expression doesn't have any value. Using value-less expressions in function calls is deprecated.")
                            .add("Expression doesn't have any value. Using value-less expressions in return statements is deprecated."),
                    """
                            void foo()
                                null;
                            end;
                            
                            def bar()
                                x = foo();
                                print(foo());
                                return foo();
                            end;
                            
                            bar();
                            """
            );
        }
    }

    @Nested
    class SimpleAssignments {
        @Test
        void compilesLiteralAssignments() {
            assertCompilesTo("a = 0b101;", createInstruction(SET, ":a", "0b101"));
            assertCompilesTo("a = true;", createInstruction(SET, ":a", "true"));
            assertCompilesTo("a = false;", createInstruction(SET, ":a", "false"));
            assertCompilesTo("a = 10;", createInstruction(SET, ":a", "10"));
            assertCompilesTo("a = 1.05;", createInstruction(SET, ":a", "1.05"));
            assertCompilesTo("a = 1.50e10;", createInstruction(SET, ":a", "15000000000"));
            assertCompilesTo("a = 1.50e80;", createInstruction(SET, ":a", "15E79"));
            assertCompilesTo("a = 0x10;", createInstruction(SET, ":a", "0x10"));
            assertCompilesTo("a = null;", createInstruction(SET, ":a", "null"));
        }

        @Test
        void compilesNegativeLiteralAssignment() {
            assertCompilesTo("""
                            a = -10;
                            """,
                    createInstruction(SET, ":a", "-10"));
        }

        @Test
        void compilesNegativeBinaryLiteralAssignment() {
            assertCompilesTo("""
                            a = -0b1111;
                            """,
                    createInstruction(SET, ":a", "-0b1111")
            );
        }

        @Test
        void compilesNegativeHexadecimalLiteralAssignment() {
            assertCompilesTo("""
                            a = -0xFF;
                            """,
                    createInstruction(SET, ":a", "-0xff")
            );
        }

        @Test
        void compilesStringLiteralAssignment() {
            assertCompilesTo("""
                            a = "Hello";
                            """,
                    createInstruction(SET, ":a", AbstractCodeGeneratorTest.q("Hello")));
        }

        @Test
        void compilesChainedConstantAssignments() {
            assertCompilesTo("""
                            a = b = 10;
                            """,
                    createInstruction(SET, ":b", "10"),
                    createInstruction(SET, ":a", ":b")
            );
        }

        @Test
        void compilesChainedExpressionAssignments() {
            assertCompilesTo("""
                            a = b = c + d;
                            """,
                    createInstruction(OP, "add", tmp(0), ":c", ":d"),
                    createInstruction(SET, ":b", tmp(0)),
                    createInstruction(SET, ":a", ":b")
            );
        }

        @Test
        void refusesVoidAssignments() {
            assertGeneratesMessageRegex(1, 5,
                    "Parse error: extraneous input 'const' expecting.*",
                    "a = const b = 0;");
        }

        @Test
        void refusesInvalidAssignments() {
            assertGeneratesMessage(
                    "Cannot assign a value to this expression.",
                    "a + b = 10;");
        }
    }

    @Nested
    class Subarrays {
        @Test
        void compilesInternalSubarrayAssignmentForward() {
            assertCompilesTo("""
                            var a[3];
                            a[0 .. 1] = a[1 .. 2];
                            """,
                    createInstruction(SET, ".a*0", ".a*1"),
                    createInstruction(SET, ".a*1", ".a*2")
            );
        }

        @Test
        void compilesInternalSubarrayAssignmentReverse() {
            assertCompilesTo("""
                            var a[3];
                            a[1 .. 2] = a[0 .. 1];
                            """,
                    createInstruction(SET, ".a*2", ".a*1"),
                    createInstruction(SET, ".a*1", ".a*0")
            );
        }

        @Test
        void compilesExternalSubarrayAssignmentForward() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[3];
                            a[0 .. 1] = a[1 .. 2];
                            """,
                    createInstruction(SET, tmp(3), "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(4), tmp(3)),
                    createInstruction(OP, "add", tmp(6), tmp(3), "1"),
                    createInstruction(READARR, tmp(7), ".a[]", tmp(6)),
                    createInstruction(WRITEARR, tmp(7), ".a[]", tmp(4)),
                    createInstruction(OP, "add", tmp(3), tmp(3), "1"),
                    createInstruction(JUMP, label(0), "lessThan", tmp(3), "2"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesExternalSubarrayAssignmentReverse() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[3];
                            a[1 .. 2] = a[0 .. 1];
                            """,
                    createInstruction(SET, tmp(3), "1"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(4), tmp(3), "1"),
                    createInstruction(SET, tmp(6), tmp(3)),
                    createInstruction(READARR, tmp(7), ".a[]", tmp(6)),
                    createInstruction(WRITEARR, tmp(7), ".a[]", tmp(4)),
                    createInstruction(OP, "sub", tmp(3), tmp(3), "1"),
                    createInstruction(JUMP, label(0), "greaterThanEq", tmp(3), "0"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesMemorySubarrayAssignmentForward() {
            assertCompilesTo("""
                            cell1[0 .. 1] = cell1[1 .. 2];
                            """,
                    createInstruction(SET, tmp(4), "0"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(5), tmp(4)),
                    createInstruction(OP, "add", tmp(7), tmp(4), "1"),
                    createInstruction(READARR, tmp(8), "cell1[]", tmp(7)),
                    createInstruction(WRITEARR, tmp(8), "cell1[]", tmp(5)),
                    createInstruction(OP, "add", tmp(4), tmp(4), "1"),
                    createInstruction(JUMP, label(0), "lessThan", tmp(4), "2"),
                    createInstruction(LABEL, label(1))
            );
        }

        @Test
        void compilesMemorySubarrayAssignmentReverse() {
            assertCompilesTo("""
                            cell1[1 .. 2] = cell1[0 .. 1];
                            """,
                    createInstruction(SET, tmp(4), "1"),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "add", tmp(5), tmp(4), "1"),
                    createInstruction(SET, tmp(7), tmp(4)),
                    createInstruction(READARR, tmp(8), "cell1[]", tmp(7)),
                    createInstruction(WRITEARR, tmp(8), "cell1[]", tmp(5)),
                    createInstruction(OP, "sub", tmp(4), tmp(4), "1"),
                    createInstruction(JUMP, label(0), "greaterThanEq", tmp(4), "0"),
                    createInstruction(LABEL, label(1))
            );
        }
    }
}
