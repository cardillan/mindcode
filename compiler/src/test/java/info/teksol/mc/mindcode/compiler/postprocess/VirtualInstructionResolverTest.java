package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.RuntimeErrorReporting;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class VirtualInstructionResolverTest extends AbstractCodeGeneratorTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.ALL;
    }

    private String createCode(RuntimeErrorReporting checks, boolean external) {
        return """
                #set target = 7;
                #set error-reporting = %s;
                #set array-optimization = none;
                allocate heap in cell1;
                param LIMIT = 3;
                
                %s a[3] = (1, 2, 3);
                
                i = 0;
                do
                    a[i] = a[i - 1];
                while ++i < LIMIT;
                
                // Verify the values were updated
                print(a);
                """.formatted(checks.name().toLowerCase(), external ? "external" : "var");
    }

    @Nested
    class InternalArrays {
        private final boolean external = false;

        @Test
        void expandsArrayAccess() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.NONE, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SET, ".a*rret", "10"),
                    createInstruction(SET, ".a*wret", "13"),
                    createInstruction(OP, "sub", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(6), tmp(2), "2"),
                    createInstruction(OP, "add", "@counter", "19", tmp(6)),
                    createInstruction(SET, ".a*w", ".a*r"),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(OP, "add", "@counter", "25", tmp(7)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "7", "lessThan", ":i", "LIMIT"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckAssert() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.ASSERT, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SET, ".a*rret", "11"),
                    createInstruction(SET, ".a*wret", "15"),
                    createInstruction(OP, "sub", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(6), tmp(2), "2"),
                    createInstruction(ASSERT_BOUNDS, "multiple", "2", "0", "lessThanEq", tmp(6), "lessThanEq", "4", q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(OP, "add", "@counter", "21", tmp(6)),
                    createInstruction(SET, ".a*w", ".a*r"),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(ASSERT_BOUNDS, "multiple", "2", "0", "lessThanEq", tmp(7), "lessThanEq", "4", q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(OP, "add", "@counter", "27", tmp(7)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "7", "lessThan", ":i", "LIMIT"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckMinimal() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.MINIMAL, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SET, ".a*rret", "12"),
                    createInstruction(SET, ".a*wret", "17"),
                    createInstruction(OP, "sub", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(6), tmp(2), "2"),
                    createInstruction(JUMP, "9", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "10", "greaterThan", ":i", "3"),
                    createInstruction(OP, "add", "@counter", "23", tmp(6)),
                    createInstruction(SET, ".a*w", ".a*r"),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(JUMP, "14", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "15", "greaterThan", ":i", "2"),
                    createInstruction(OP, "add", "@counter", "29", tmp(7)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "7", "lessThan", ":i", "LIMIT"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckSimple() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.SIMPLE, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SET, ".a*rret", "13"),
                    createInstruction(SET, ".a*wret", "19"),
                    createInstruction(OP, "sub", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(6), tmp(2), "2"),
                    createInstruction(JUMP, "11", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "12", "lessThanEq", ":i", "3"),
                    createInstruction(STOP),
                    createInstruction(OP, "add", "@counter", "25", tmp(6)),
                    createInstruction(SET, ".a*w", ".a*r"),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(JUMP, "17", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "18", "lessThanEq", ":i", "2"),
                    createInstruction(STOP),
                    createInstruction(OP, "add", "@counter", "31", tmp(7)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "7", "lessThan", ":i", "LIMIT"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckDescribed() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.DESCRIBED, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(SET, ".a*0", "1"),
                    createInstruction(SET, ".a*1", "2"),
                    createInstruction(SET, ".a*2", "3"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(SET, ".a*rret", "14"),
                    createInstruction(SET, ".a*wret", "21"),
                    createInstruction(OP, "sub", tmp(2), ":i", "1"),
                    createInstruction(OP, "mul", tmp(6), tmp(2), "2"),
                    createInstruction(JUMP, "11", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "13", "lessThanEq", ":i", "3"),
                    createInstruction(PRINT, q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(STOP),
                    createInstruction(OP, "add", "@counter", "27", tmp(6)),
                    createInstruction(SET, ".a*w", ".a*r"),
                    createInstruction(OP, "mul", tmp(7), ":i", "2"),
                    createInstruction(JUMP, "18", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "20", "lessThanEq", ":i", "2"),
                    createInstruction(PRINT, q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(STOP),
                    createInstruction(OP, "add", "@counter", "33", tmp(7)),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "7", "lessThan", ":i", "LIMIT"),
                    createInstruction(PRINT, ".a*0"),
                    createInstruction(PRINT, ".a*1"),
                    createInstruction(PRINT, ".a*2"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }
    }

    @Nested
    class ExternalArrays {
        private final boolean external = true;

        @Test
        void expandsArrayAccess() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.NONE, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(OP, "sub", tmp(6), ":i", "1"),
                    createInstruction(READ, tmp(7), "cell1", tmp(6)),
                    createInstruction(WRITE, tmp(7), "cell1", ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "5", "lessThan", ":i", "LIMIT"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "cell1", "1"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(9), "cell1", "2"),
                    createInstruction(PRINT, tmp(9))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckAssert() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.ASSERT, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(OP, "sub", tmp(6), ":i", "1"),
                    createInstruction(ASSERT_BOUNDS, "multiple", "1", "0", "lessThanEq", tmp(6), "lessThanEq", "2", q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(READ, tmp(7), "cell1", tmp(6)),
                    createInstruction(ASSERT_BOUNDS, "multiple", "1", "0", "lessThanEq", ":i", "lessThanEq", "2", q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(WRITE, tmp(7), "cell1", ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "5", "lessThan", ":i", "LIMIT"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "cell1", "1"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(9), "cell1", "2"),
                    createInstruction(PRINT, tmp(9))

            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckMinimal() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.MINIMAL, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(OP, "sub", tmp(6), ":i", "1"),
                    createInstruction(JUMP, "6", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "7", "greaterThan", ":i", "3"),
                    createInstruction(READ, tmp(7), "cell1", tmp(6)),
                    createInstruction(JUMP, "9", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "10", "greaterThan", ":i", "2"),
                    createInstruction(WRITE, tmp(7), "cell1", ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "5", "lessThan", ":i", "LIMIT"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "cell1", "1"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(9), "cell1", "2"),
                    createInstruction(PRINT, tmp(9))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckSimple() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.SIMPLE, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(OP, "sub", tmp(6), ":i", "1"),
                    createInstruction(JUMP, "8", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "9", "lessThanEq", ":i", "3"),
                    createInstruction(STOP),
                    createInstruction(READ, tmp(7), "cell1", tmp(6)),
                    createInstruction(JUMP, "12", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "13", "lessThanEq", ":i", "2"),
                    createInstruction(STOP),
                    createInstruction(WRITE, tmp(7), "cell1", ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "5", "lessThan", ":i", "LIMIT"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "cell1", "1"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(9), "cell1", "2"),
                    createInstruction(PRINT, tmp(9))
            );
        }

        @Test
        void expandsArrayAccessWithBoundCheckDescribed() {
            assertCompilesTo(
                    createCode(RuntimeErrorReporting.DESCRIBED, external),

                    createInstruction(SET, "LIMIT", "3"),
                    createInstruction(WRITE, "1", "cell1", "0"),
                    createInstruction(WRITE, "2", "cell1", "1"),
                    createInstruction(WRITE, "3", "cell1", "2"),
                    createInstruction(SET, ":i", "0"),
                    createInstruction(OP, "sub", tmp(6), ":i", "1"),
                    createInstruction(JUMP, "8", "lessThan", ":i", "1"),
                    createInstruction(JUMP, "10", "lessThanEq", ":i", "3"),
                    createInstruction(PRINT, q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(STOP),
                    createInstruction(READ, tmp(7), "cell1", tmp(6)),
                    createInstruction(JUMP, "13", "lessThan", ":i", "0"),
                    createInstruction(JUMP, "15", "lessThanEq", ":i", "2"),
                    createInstruction(PRINT, q("position 11:5: index out of bounds (0 to 2)")),
                    createInstruction(STOP),
                    createInstruction(WRITE, tmp(7), "cell1", ":i"),
                    createInstruction(OP, "add", ":i", ":i", "1"),
                    createInstruction(JUMP, "5", "lessThan", ":i", "LIMIT"),
                    createInstruction(READ, tmp(0), "cell1", "0"),
                    createInstruction(PRINT, tmp(0)),
                    createInstruction(READ, tmp(1), "cell1", "1"),
                    createInstruction(PRINT, tmp(1)),
                    createInstruction(READ, tmp(9), "cell1", "2"),
                    createInstruction(PRINT, tmp(9))
            );
        }
    }

    @Nested
    class LoopHoistingTest {

        @Test
        void expandsLoopWithArrayAccess() {
            // This test is set up to perform loop hoisting first (which hoists the instructions setting
            // return addresses for the array element access), and loop unrolling second. The loop unrolling
            // must copy the setaddr instructions back into the loop. The order of the instruction matters!
            assertCompilesTo("""
                    #set target = 7;
                    #set instruction-limit = 60;
                    #set array-optimization = none;
                    
                    const SIZE = 4;
                    var a[SIZE];
                    
                    for i in 0 ... 4 do
                        a[floor(rand(SIZE))]++;
                    end;
                    
                    print(a[floor(rand(SIZE))]);
                    """,
                    createInstruction(OP, "rand", tmp(0), "4"),
                    createInstruction(OP, "floor", tmp(2), tmp(0)),
                    createInstruction(OP, "mul", tmp(10), tmp(2), "2"),
                    createInstruction(SET, ".a*rret", "5"),
                    createInstruction(OP, "add", "@counter", "39", tmp(10)),
                    createInstruction(OP, "add", ".a*w", ".a*r", "1"),
                    createInstruction(SET, ".a*wret", "8"),
                    createInstruction(OP, "add", "@counter", "47", tmp(10)),
                    createInstruction(OP, "rand", tmp(0), "4"),
                    createInstruction(OP, "floor", tmp(2), tmp(0)),
                    createInstruction(OP, "mul", tmp(10), tmp(2), "2"),
                    createInstruction(SET, ".a*rret", "13"),
                    createInstruction(OP, "add", "@counter", "39", tmp(10)),
                    createInstruction(OP, "add", ".a*w", ".a*r", "1"),
                    createInstruction(SET, ".a*wret", "16"),
                    createInstruction(OP, "add", "@counter", "47", tmp(10)),
                    createInstruction(OP, "rand", tmp(0), "4"),
                    createInstruction(OP, "floor", tmp(2), tmp(0)),
                    createInstruction(OP, "mul", tmp(10), tmp(2), "2"),
                    createInstruction(SET, ".a*rret", "21"),
                    createInstruction(OP, "add", "@counter", "39", tmp(10)),
                    createInstruction(OP, "add", ".a*w", ".a*r", "1"),
                    createInstruction(SET, ".a*wret", "24"),
                    createInstruction(OP, "add", "@counter", "47", tmp(10)),
                    createInstruction(OP, "rand", tmp(0), "4"),
                    createInstruction(OP, "floor", tmp(2), tmp(0)),
                    createInstruction(OP, "mul", tmp(10), tmp(2), "2"),
                    createInstruction(SET, ".a*rret", "29"),
                    createInstruction(OP, "add", "@counter", "39", tmp(10)),
                    createInstruction(OP, "add", ".a*w", ".a*r", "1"),
                    createInstruction(SET, ".a*wret", "32"),
                    createInstruction(OP, "add", "@counter", "47", tmp(10)),
                    createInstruction(OP, "rand", tmp(5), "4"),
                    createInstruction(OP, "floor", tmp(6), tmp(5)),
                    createInstruction(SET, ".a*rret", "37"),
                    createInstruction(OP, "mul", tmp(12), tmp(6), "2"),
                    createInstruction(OP, "add", "@counter", "39", tmp(12)),
                    createInstruction(PRINT, ".a*r"),
                    createInstruction(END),
                    createInstruction(SET, ".a*r", ".a*0"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*1"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*2"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*r", ".a*3"),
                    createInstruction(SET, "@counter", ".a*rret"),
                    createInstruction(SET, ".a*0", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*1", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*2", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(SET, ".a*3", ".a*w"),
                    createInstruction(SET, "@counter", ".a*wret"),
                    createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
            );
        }
    }
}
