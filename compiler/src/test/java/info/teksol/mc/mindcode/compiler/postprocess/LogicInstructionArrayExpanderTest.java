package info.teksol.mc.mindcode.compiler.postprocess;

import info.teksol.mc.mindcode.compiler.CompilationPhase;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeGeneratorTest;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class LogicInstructionArrayExpanderTest extends AbstractCodeGeneratorTest {

    @Override
    protected CompilationPhase getTargetPhase() {
        return CompilationPhase.ALL;
    }

    @Test
    void expandsArrayAccess() {
        assertCompilesTo("""
                        const SIZE = 3;
                        param LIMIT = SIZE;
                        
                        var a[SIZE];
                        
                        // This loop will be unrolled
                        for i in 0 ... SIZE do
                            a[i] = i + 1;
                        end;
                        
                        // Using LIMIT prevents loop unrolling
                        for i in 0 ... LIMIT do
                            print(a[i]++);
                        end;
                        
                        // Verify the values were updated
                        print(a);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "15", "greaterThanEq", "0", "LIMIT"),
                createInstruction(SET, ".a*rret", "9"),
                createInstruction(OP, "mul", tmp(6), ":i", "2"),
                createInstruction(OP, "add", "@counter", "19", tmp(6)),
                createInstruction(OP, "add", ".a*w", ".a*r", "1"),
                createInstruction(SET, ".a*wret", "12"),
                createInstruction(OP, "add", "@counter", "25", tmp(6)),
                createInstruction(PRINT, ".a*r"),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "6", "lessThan", ":i", "LIMIT"),
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
        assertCompilesTo("""
                        #set boundary-checks = assert;
                        param LIMIT = 3;
                        
                        var a[3] = (1, 2, 3);
                        
                        for i in 0 ... LIMIT do
                            a[i] = a[i - 1];
                        end;
                        
                        // Verify the values were updated
                        print(a);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "18", "greaterThanEq", "0", "LIMIT"),
                createInstruction(OP, "sub", tmp(2), ":i", "1"),
                createInstruction(SET, ".a*rret", "11"),
                createInstruction(OP, "mul", tmp(5), tmp(2), "2"),
                createInstruction(ASSERT_BOUNDS, "multiple", "2", "0", "lessThanEq", tmp(5), "lessThanEq", "4", q("position 7:12: index out of bounds (0 to 2)")),
                createInstruction(OP, "add", "@counter", "22", tmp(5)),
                createInstruction(SET, ".a*w", ".a*r"),
                createInstruction(SET, ".a*wret", "16"),
                createInstruction(OP, "mul", tmp(6), ":i", "2"),
                createInstruction(ASSERT_BOUNDS, "multiple", "2", "0", "lessThanEq", tmp(6), "lessThanEq", "4", q("position 7:5: index out of bounds (0 to 2)")),
                createInstruction(OP, "add", "@counter", "28", tmp(6)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "6", "lessThan", ":i", "LIMIT"),
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
        assertCompilesTo("""
                        #set boundary-checks = minimal;
                        param LIMIT = 3;
                        
                        var a[3] = (1, 2, 3);
                        
                        for i in 0 ... LIMIT do
                            a[i] = a[i - 1];
                        end;
                        
                        // Verify the values were updated
                        print(a);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "20", "greaterThanEq", "0", "LIMIT"),
                createInstruction(OP, "sub", tmp(2), ":i", "1"),
                createInstruction(SET, ".a*rret", "12"),
                createInstruction(OP, "mul", tmp(5), tmp(2), "2"),
                createInstruction(JUMP, "9", "lessThan", tmp(5), "0"),
                createInstruction(JUMP, "10", "greaterThan", tmp(5), "4"),
                createInstruction(OP, "add", "@counter", "24", tmp(5)),
                createInstruction(SET, ".a*w", ".a*r"),
                createInstruction(SET, ".a*wret", "18"),
                createInstruction(OP, "mul", tmp(6), ":i", "2"),
                createInstruction(JUMP, "15", "lessThan", tmp(6), "0"),
                createInstruction(JUMP, "16", "greaterThan", tmp(6), "4"),
                createInstruction(OP, "add", "@counter", "30", tmp(6)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "6", "lessThan", ":i", "LIMIT"),
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
        assertCompilesTo("""
                        #set boundary-checks = simple;
                        param LIMIT = 3;
                        
                        var a[3] = (1, 2, 3);
                        
                        for i in 0 ... LIMIT do
                            a[i] = a[i - 1];
                        end;
                        
                        // Verify the values were updated
                        print(a);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "22", "greaterThanEq", "0", "LIMIT"),
                createInstruction(OP, "sub", tmp(2), ":i", "1"),
                createInstruction(SET, ".a*rret", "13"),
                createInstruction(OP, "mul", tmp(5), tmp(2), "2"),
                createInstruction(JUMP, "11", "lessThan", tmp(5), "0"),
                createInstruction(JUMP, "12", "lessThanEq", tmp(5), "4"),
                createInstruction(STOP),
                createInstruction(OP, "add", "@counter", "26", tmp(5)),
                createInstruction(SET, ".a*w", ".a*r"),
                createInstruction(SET, ".a*wret", "20"),
                createInstruction(OP, "mul", tmp(6), ":i", "2"),
                createInstruction(JUMP, "18", "lessThan", tmp(6), "0"),
                createInstruction(JUMP, "19", "lessThanEq", tmp(6), "4"),
                createInstruction(STOP),
                createInstruction(OP, "add", "@counter", "32", tmp(6)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "6", "lessThan", ":i", "LIMIT"),
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
        assertCompilesTo("""
                        #set boundary-checks = described;
                        param LIMIT = 3;
                        
                        var a[3] = (1, 2, 3);
                        
                        for i in 0 ... LIMIT do
                            a[i] = a[i - 1];
                        end;
                        
                        // Verify the values were updated
                        print(a);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(JUMP, "24", "greaterThanEq", "0", "LIMIT"),
                createInstruction(OP, "sub", tmp(2), ":i", "1"),
                createInstruction(SET, ".a*rret", "14"),
                createInstruction(OP, "mul", tmp(5), tmp(2), "2"),
                createInstruction(JUMP, "11", "lessThan", tmp(5), "0"),
                createInstruction(JUMP, "13", "lessThanEq", tmp(5), "4"),
                createInstruction(PRINT, q("position 7:12: index out of bounds (0 to 2)")),
                createInstruction(STOP),
                createInstruction(OP, "add", "@counter", "28", tmp(5)),
                createInstruction(SET, ".a*w", ".a*r"),
                createInstruction(SET, ".a*wret", "22"),
                createInstruction(OP, "mul", tmp(6), ":i", "2"),
                createInstruction(JUMP, "19", "lessThan", tmp(6), "0"),
                createInstruction(JUMP, "21", "lessThanEq", tmp(6), "4"),
                createInstruction(PRINT, q("position 7:5: index out of bounds (0 to 2)")),
                createInstruction(STOP),
                createInstruction(OP, "add", "@counter", "34", tmp(6)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "6", "lessThan", ":i", "LIMIT"),
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