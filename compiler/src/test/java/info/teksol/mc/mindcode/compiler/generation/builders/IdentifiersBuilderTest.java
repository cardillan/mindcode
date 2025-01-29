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
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(READ, var(12), "cell1", "12"),
                    createInstruction(WRITE, var(12), "cell1", "1")
            );
        }

        @Test
        void compilesSimpleArrayAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10], b[10];
                            a[x] = b[y];
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(SET, var(20), ":x"),
                    createInstruction(OP, "add", var(22), ":y", "10"),
                    createInstruction(READ, var(23), "cell1", var(22)),
                    createInstruction(WRITE, var(23), "cell1", var(20))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external a[10];
                            a[a[x]] = a[x];
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(SET, var(10), ":x"),
                    createInstruction(READ, var(11), "cell1", var(10)),
                    createInstruction(SET, var(12), var(11)),
                    createInstruction(SET, var(14), ":x"),
                    createInstruction(READ, var(15), "cell1", var(14)),
                    createInstruction(WRITE, var(15), "cell1", var(12))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            x[a] += x[b] += x[c];
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(SET, var(10), ":a"),
                    createInstruction(SET, var(12), ":b"),
                    createInstruction(SET, var(14), ":c"),
                    createInstruction(READ, var(15), "cell1", var(14)),
                    createInstruction(READ, var(13), "cell1", var(12)),
                    createInstruction(OP, "add", var(16), var(13), var(15)),
                    createInstruction(WRITE, var(16), "cell1", var(12)),
                    createInstruction(READ, var(11), "cell1", var(10)),
                    createInstruction(OP, "add", var(17), var(11), var(16)),
                    createInstruction(WRITE, var(17), "cell1", var(10))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            a = x[b] = @time;
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(SET, var(10), ":b"),
                    createInstruction(SET, var(12), "@time"),
                    createInstruction(WRITE, var(12), "cell1", var(10)),
                    createInstruction(SET, ":a", var(12))

            );
        }

        @Test
        void compilesArrayElementsAsInstructionParameters() {
            assertCompilesTo("""
                            allocate heap in cell1;
                            external x[10];
                            ulocate(ore, x[a], out x[b], out x[c]);
                            """,
                    createInstruction(LABEL, var(1000)),
                    createInstruction(JUMP, var(1000), "equal", "cell1", "null"),
                    createInstruction(SET, var(10), ":a"),
                    createInstruction(SET, var(12), ":b"),
                    createInstruction(SET, var(15), ":c"),
                    createInstruction(READ, var(11), "cell1", var(10)),
                    createInstruction(ULOCATE, "ore", "core", "true", var(11), var(14), var(17), var(18), var(19)),
                    createInstruction(WRITE, var(14), "cell1", var(12)),
                    createInstruction(WRITE, var(17), "cell1", var(15))
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
                    createInstruction(LABEL, var(1001)),
                    createInstruction(JUMP, var(1001), "equal", "cell1", "null"),
                    createInstruction(SET, var(10), ":a"),
                    createInstruction(READ, var(11), "cell1", var(10)),
                    createInstruction(SET, var(12), var(11)),
                    createInstruction(SET, ":fn0:x", var(12)),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1002)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(WRITE, ":fn0:x", "cell1", var(10)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, var(13), ":fn0:x"),
                    createInstruction(OP, "add", ":fn0:x", ":fn0:x", "1"),
                    createInstruction(SET, ":fn0*retval", var(13)),
                    createInstruction(LABEL, var(1003)),
                    createInstruction(RETURN, ":fn0*retaddr")
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
                    createInstruction(PRINT, q("\uF833X")),
                    createInstruction(END)
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
                    createInstruction(SET, var(0), ":x"),
                    createInstruction(SET, var(2), ":y"),
                    createInstruction(READARR, var(3), "b[]", var(2)),
                    createInstruction(WRITEARR, var(3), "a[]", var(0))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            var a[10];
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, var(0), ":x"),
                    createInstruction(READARR, var(1), "a[]", var(0)),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(SET, var(4), ":x"),
                    createInstruction(READARR, var(5), "a[]", var(4)),
                    createInstruction(WRITEARR, var(5), "a[]", var(2))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            var x[10];
                            x[a] += x[b] += x[c];
                            """,
                    createInstruction(SET, var(0), ":a"),
                    createInstruction(SET, var(2), ":b"),
                    createInstruction(SET, var(4), ":c"),
                    createInstruction(READARR, var(5), "x[]", var(4)),
                    createInstruction(READARR, var(3), "x[]", var(2)),
                    createInstruction(OP, "add", var(6), var(3), var(5)),
                    createInstruction(WRITEARR, var(6), "x[]", var(2)),
                    createInstruction(READARR, var(1), "x[]", var(0)),
                    createInstruction(OP, "add", var(7), var(1), var(6)),
                    createInstruction(WRITEARR, var(7), "x[]", var(0))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            var x[10];
                            a = x[b] = @time;
                            """,
                    createInstruction(SET, var(0), ":b"),
                    createInstruction(SET, var(2), "@time"),
                    createInstruction(WRITEARR, var(2), "x[]", var(0)),
                    createInstruction(SET, ":a", var(2))
            );
        }

        @Test
        void compilesArrayElementsAsInstructionParameters() {
            assertCompilesTo("""
                            var x[10];
                            ulocate(ore, x[a], out x[b], out x[c]);
                            """,
                    createInstruction(SET, var(0), ":a"),
                    createInstruction(SET, var(2), ":b"),
                    createInstruction(SET, var(5), ":c"),
                    createInstruction(READARR, var(1), ".x[]", var(0)),
                    createInstruction(ULOCATE, "ore", "core", "true", var(1), var(4), var(7), var(8), var(9)),
                    createInstruction(WRITEARR, var(4), ".x[]", var(2)),
                    createInstruction(WRITEARR, var(7), ".x[]", var(5))
            );
        }

        @Test
        void compilesArrayElementsAsFunctionParameters() {
            assertCompilesTo("""
                            var x[10];
                            noinline def foo(in out x) x++; end;
                            foo(out x[a]);
                            """,
                    createInstruction(SET, var(0), ":a"),
                    createInstruction(READARR, var(1), ".x[]", var(0)),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(SET, ":fn0:x", var(2)),
                    createInstruction(SETADDR, ":fn0*retaddr", var(1001)),
                    createInstruction(CALL, var(1000), ":fn0*retval"),
                    createInstruction(LABEL, var(1001)),
                    createInstruction(WRITEARR, ":fn0:x", ".x[]", var(0)),
                    createInstruction(END),
                    createInstruction(LABEL, var(1000)),
                    createInstruction(SET, var(3), ":fn0:x"),
                    createInstruction(OP, "add", ":fn0:x", ":fn0:x", "1"),
                    createInstruction(SET, ":fn0*retval", var(3)),
                    createInstruction(LABEL, var(1002)),
                    createInstruction(RETURN, ":fn0*retaddr")
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
                    createInstruction(SET, var(0), "a"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(READ, var(3), "cell2", var(2)),
                    createInstruction(WRITE, var(3), "cell1", var(0))
            );
        }

        @Test
        void compilesComplexArrayAccess() {
            assertCompilesTo("""
                            a = cell1;
                            a[a[x]] = a[x];
                            """,
                    createInstruction(SET, "a", "cell1"),
                    createInstruction(SET, var(0), "x"),
                    createInstruction(READ, var(1), "a", var(0)),
                    createInstruction(SET, var(2), var(1)),
                    createInstruction(SET, var(4), "x"),
                    createInstruction(READ, var(5), "a", var(4)),
                    createInstruction(WRITE, var(5), "a", var(2))
            );
        }

        @Test
        void compilesMultipleArrayAccesses() {
            assertCompilesTo("""
                            cell1[a] += cell1[b] += cell1[c];
                            """,
                    createInstruction(SET, var(0), "a"),
                    createInstruction(SET, var(2), "b"),
                    createInstruction(SET, var(4), "c"),
                    createInstruction(READ, var(5), "cell1", var(4)),
                    createInstruction(READ, var(3), "cell1", var(2)),
                    createInstruction(OP, "add", var(6), var(3), var(5)),
                    createInstruction(WRITE, var(6), "cell1", var(2)),
                    createInstruction(READ, var(1), "cell1", var(0)),
                    createInstruction(OP, "add", var(7), var(1), var(6)),
                    createInstruction(WRITE, var(7), "cell1", var(0))
            );
        }

        @Test
        void compilesChainedAssignmentsWithVolatileVariables() {
            assertCompilesTo("""
                            a = cell1[b] = @time;
                            """,
                    createInstruction(SET, var(0), "b"),
                    createInstruction(SET, var(2), "@time"),
                    createInstruction(WRITE, var(2), "cell1", var(0)),
                    createInstruction(SET, "a", var(2))
            );
        }

        @Test
        void compilesSuspiciousArrayAccess() {
            assertCompilesTo("""
                            a = b + c;
                            a[0] = 1;
                            """,
                    createInstruction(OP, "add", var(0), "b", "c"),
                    createInstruction(SET, "a", var(0)),
                    createInstruction(WRITE, "1", "a", "0")
            );
        }

        @Test
        void compilesMainMemoryVariable() {
            assertCompilesTo(
                    "memory = cell1; memory[0] = rand(9**9);",
                    createInstruction(SET, "memory", "cell1"),
                    createInstruction(OP, "rand", var(0), "387420489"),
                    createInstruction(WRITE, var(0), "memory", "0"),
                    createInstruction(END)
            );
        }

        @Test
        void compilesMemoryAccessThroughParameter() {
            assertCompilesTo("""
                            param mem = bank1;
                            mem[0] = 5;
                            """,
                    createInstruction(SET, "mem", "bank1"),
                    createInstruction(WRITE, "5", "mem", "0"),
                    createInstruction(END)
            );
        }
    }
}
