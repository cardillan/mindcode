package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class IdentifiersBuilderTest extends AbstractCodeGeneratorTest {

    @Nested
    class BuiltIns {
        @Test
        void compilesKnownBuiltIns() {
            assertCompiles("""
                            print(@coal);
                            print(@crux);
                            print(@time);
                            """
            );
        }

        @Test
        void warnsAtUnknownBuiltIns() {
            assertGeneratesMessages(expectedMessages()
                            .add("Built-in variable '@fluffy-bunny' not recognized."),
                    """
                            print(@fluffy-bunny);
                            """);
        }

    }

    @Nested
    class ExternalArrays {
        @Test
        void compilesConstantArrayAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10], b[10];
                            a[1] = b[2];
                            """,
                    createInstruction(READ, tmp(12), "cell1", "12"),
                    createInstruction(WRITE, tmp(12), "cell1", "1")
            );
        }

        @Test
        void compilesSimpleArrayAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10], b[10];
                            a[x] = b[y];
                            """,
                    createInstruction(SET, tmp(20), ":x"),
                    createInstruction(OP, "add", tmp(22), ":y", "10"),
                    createInstruction(READARR, tmp(23), ".b[]", tmp(22)),
                    createInstruction(WRITEARR, tmp(23), ".a[]", tmp(20))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10];
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, tmp(10), ":x"),
                    createInstruction(READARR, tmp(11), ".a[]", tmp(10)),
                    createInstruction(SET, tmp(12), tmp(11)),
                    createInstruction(SET, tmp(14), ":x"),
                    createInstruction(READARR, tmp(15), ".a[]", tmp(14)),
                    createInstruction(WRITEARR, tmp(15), ".a[]", tmp(12))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            x[a] += x[b] += x[c];
                            """,
                    createInstruction(SET, tmp(10), ":a"),
                    createInstruction(SET, tmp(12), ":b"),
                    createInstruction(SET, tmp(14), ":c"),
                    createInstruction(READARR, tmp(15), ".x[]", tmp(14)),
                    createInstruction(READARR, tmp(13), ".x[]", tmp(12)),
                    createInstruction(OP, "add", tmp(16), tmp(13), tmp(15)),
                    createInstruction(WRITEARR, tmp(16), ".x[]", tmp(12)),
                    createInstruction(READARR, tmp(11), ".x[]", tmp(10)),
                    createInstruction(OP, "add", tmp(17), tmp(11), tmp(16)),
                    createInstruction(WRITEARR, tmp(17), ".x[]", tmp(10))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            a = x[b] = @time;
                            """,
                    createInstruction(SET, tmp(10), ":b"),
                    createInstruction(SET, tmp(12), "@time"),
                    createInstruction(WRITEARR, tmp(12), ".x[]", tmp(10)),
                    createInstruction(SET, ":a", tmp(12))

            );
        }

        @Test
        void compilesArrayElementsAsInstructionParameters() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            ulocate(:ore, x[a], out x[b], out x[c]);
                            """,
                    createInstruction(SET, tmp(10), ":a"),
                    createInstruction(SET, tmp(12), ":b"),
                    createInstruction(SET, tmp(15), ":c"),
                    createInstruction(READARR, tmp(11), ".x[]", tmp(10)),
                    createInstruction(ULOCATE, "ore", "core", "true", tmp(11), tmp(14), tmp(17), tmp(18), tmp(19)),
                    createInstruction(WRITEARR, tmp(14), ".x[]", tmp(12)),
                    createInstruction(WRITEARR, tmp(17), ".x[]", tmp(15))
            );
        }

        @Test
        void compilesArrayElementsAsFunctionParameters() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            noinline def foo(in out x) x++; end;
                            foo(out x[a]);
                            """,
                    createInstruction(SET, tmp(10), ":a"),
                    createInstruction(READARR, tmp(11), ".x[]", tmp(10)),
                    createInstruction(SET, tmp(12), tmp(11)),
                    createInstruction(SET, ":foo:x", tmp(12)),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(WRITEARR, ":foo:x", ".x[]", tmp(10)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(13), ":foo:x"),
                    createInstruction(OP, "add", ":foo:x", ":foo:x", "1"),
                    createInstruction(SET, ":foo*retval", tmp(13)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }
    }

    @Nested
    class Identifiers {
        @Test
        void recognizesIconConstants() {
            assertCompilesTo("""
                            print(ITEM_COAL + "X");
                            """,
                    createInstruction(PRINT, q("\uF833X"))
            );
        }
    }

    @Nested
    class InternalArrays {
        @Test
        void compilesConstantArrayAccess() {
            assertCompilesTo("""
                            var a[10], b[10];
                            a[1] = b[2];
                            """,
                    createInstruction(SET, ".a*1", ".b*2")
            );
        }

        @Test
        void compilesSimpleArrayAccess() {
            assertCompilesTo("""
                            var a[10], b[10];
                            a[x] = b[y];
                            """,
                    createInstruction(SET, tmp(0), ":x"),
                    createInstruction(SET, tmp(2), ":y"),
                    createInstruction(READARR, tmp(3), ".b[][]", tmp(2)),
                    createInstruction(WRITEARR, tmp(3), ".a[][]", tmp(0))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            var a[10];
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, tmp(0), ":x"),
                    createInstruction(READARR, tmp(1), ".a[]", tmp(0)),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(SET, tmp(4), ":x"),
                    createInstruction(READARR, tmp(5), ".a[]", tmp(4)),
                    createInstruction(WRITEARR, tmp(5), ".a[]", tmp(2))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            var x[10];
                            x[a] += x[b] += x[c];
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(SET, tmp(4), ":c"),
                    createInstruction(READARR, tmp(5), ".x[][]", tmp(4)),
                    createInstruction(READARR, tmp(3), ".x[][]", tmp(2)),
                    createInstruction(OP, "add", tmp(6), tmp(3), tmp(5)),
                    createInstruction(WRITEARR, tmp(6), ".x[][]", tmp(2)),
                    createInstruction(READARR, tmp(1), ".x[][]", tmp(0)),
                    createInstruction(OP, "add", tmp(7), tmp(1), tmp(6)),
                    createInstruction(WRITEARR, tmp(7), ".x[][]", tmp(0))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            var x[10];
                            a = x[b] = @time;
                            """,
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(SET, tmp(2), "@time"),
                    createInstruction(WRITEARR, tmp(2), ".x[]", tmp(0)),
                    createInstruction(SET, ":a", tmp(2))
            );
        }

        @Test
        void compilesArrayElementsAsInstructionParameters() {
            assertCompilesTo("""
                            var x[10];
                            ulocate(:ore, x[a], out x[b], out x[c]);
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(SET, tmp(5), ":c"),
                    createInstruction(READARR, tmp(1), ".x[]", tmp(0)),
                    createInstruction(ULOCATE, "ore", "core", "true", tmp(1), tmp(4), tmp(7), tmp(8), tmp(9)),
                    createInstruction(WRITEARR, tmp(4), ".x[]", tmp(2)),
                    createInstruction(WRITEARR, tmp(7), ".x[]", tmp(5))
            );
        }

        @Test
        void compilesArrayElementsAsFunctionParameters() {
            assertCompilesTo("""
                            var x[10];
                            noinline def foo(in out x) x++; end;
                            foo(out x[a]);
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(READARR, tmp(1), ".x[]", tmp(0)),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(SET, ":foo:x", tmp(2)),
                    createInstruction(SETADDR, ":foo*retaddr", label(1)),
                    createInstruction(CALL, label(0), "*invalid", ":foo*retval"),
                    createInstruction(LABEL, label(1)),
                    createInstruction(WRITEARR, ":foo:x", ".x[]", tmp(0)),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(SET, tmp(3), ":foo:x"),
                    createInstruction(OP, "add", ":foo:x", ":foo:x", "1"),
                    createInstruction(SET, ":foo*retval", tmp(3)),
                    createInstruction(LABEL, label(2)),
                    createInstruction(RETURN, ":foo*retaddr")
            );
        }
    }

    @Nested
    class MemoryArrays {
        @Test
        void compilesSimpleArrayAccess() {
            assertCompilesTo("""
                            cell1[a] = cell2[b];
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(READ, tmp(3), "cell2", tmp(2)),
                    createInstruction(WRITE, tmp(3), "cell1", tmp(0))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            a = cell1;
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, ":a", "cell1"),
                    createInstruction(SET, tmp(0), ":x"),
                    createInstruction(READ, tmp(1), ":a", tmp(0)),
                    createInstruction(SET, tmp(2), tmp(1)),
                    createInstruction(SET, tmp(4), ":x"),
                    createInstruction(READ, tmp(5), ":a", tmp(4)),
                    createInstruction(WRITE, tmp(5), ":a", tmp(2))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            cell1[a] += cell1[b] += cell1[c];
                            """,
                    createInstruction(SET, tmp(0), ":a"),
                    createInstruction(SET, tmp(2), ":b"),
                    createInstruction(SET, tmp(4), ":c"),
                    createInstruction(READ, tmp(5), "cell1", tmp(4)),
                    createInstruction(READ, tmp(3), "cell1", tmp(2)),
                    createInstruction(OP, "add", tmp(6), tmp(3), tmp(5)),
                    createInstruction(WRITE, tmp(6), "cell1", tmp(2)),
                    createInstruction(READ, tmp(1), "cell1", tmp(0)),
                    createInstruction(OP, "add", tmp(7), tmp(1), tmp(6)),
                    createInstruction(WRITE, tmp(7), "cell1", tmp(0))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            a = cell1[b] = @time;
                            """,
                    createInstruction(SET, tmp(0), ":b"),
                    createInstruction(SET, tmp(2), "@time"),
                    createInstruction(WRITE, tmp(2), "cell1", tmp(0)),
                    createInstruction(SET, ":a", tmp(2))
            );
        }

        @Test
        void compilesSuspiciousArrayAccess() {
            assertCompilesTo("""
                            a = b + c;
                            a[0] = 1;
                            """,
                    createInstruction(OP, "add", tmp(0), ":b", ":c"),
                    createInstruction(SET, ":a", tmp(0)),
                    createInstruction(WRITE, "1", ":a", "0")
            );
        }

        @Test
        void compilesMainMemoryVariable() {
            assertCompilesTo(
                    "memory = cell1; memory[0] = rand(9**9);",
                    createInstruction(SET, ":memory", "cell1"),
                    createInstruction(OP, "rand", tmp(1), "387420489"),
                    createInstruction(WRITE, tmp(1), ":memory", "0")
            );
        }

        @Test
        void compilesMemoryAccessThroughParameter() {
            assertCompilesTo("""
                            param mem = bank1;
                            mem[0] = 5;
                            """,
                    createInstruction(SET, "mem", "bank1"),
                    createInstruction(WRITE, "5", "mem", "0")
            );
        }
    }

    @Nested
    class Subarrays {
        @Test
        void compilesInternalSubarrays() {
            assertCompiles("var a[10]; a[3 .. 5];");
        }

        @Test
        void reportsOutOfBoundsStartIndex() {
            assertGeneratesMessage(
                    "Subarray index -5 out of range 0 .. 9.",
                    "var a[10]; a[-5 .. 8];");
        }

        @Test
        void reportsOutOfBoundsEndIndex() {
            assertGeneratesMessage(
                    "Subarray index 12 out of range 0 .. 9.",
                    "var a[10]; a[8 .. 12];");
        }

        @Test
        void reportsNonConstantindices() {
            assertGeneratesMessage(
                    "Subarray specification must use constant range.",
                    "var a[10]; a[0 .. n];");
        }

        @Test
        void reportsNonIntegerIndex() {
            assertGeneratesMessage(
                    "Subarray specification must use integer range.",
                    "var a[10]; a[3 .. 5.5];");
        }

        @Test
        void reportsNegativeSubarraySize() {
            assertGeneratesMessage(
                    "Empty or invalid subarray range.",
                    "var a[10]; a [1 .. 0];");
        }

        @Test
        void reportsZeroSubarraySize() {
            assertGeneratesMessage(
                    "Empty or invalid subarray range.",
                    "var a[10]; a [1 ... 1];");
        }
    }
}
