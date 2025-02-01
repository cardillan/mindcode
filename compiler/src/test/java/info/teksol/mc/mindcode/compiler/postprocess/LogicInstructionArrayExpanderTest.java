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
                        print(a[0]);
                        print(a[1]);
                        print(a[2]);
                        """,
                createInstruction(SET, "LIMIT", "3"),
                createInstruction(SET, ".a*0", "1"),
                createInstruction(SET, ".a*1", "2"),
                createInstruction(SET, ".a*2", "3"),
                createInstruction(SET, ":i", "0"),
                createInstruction(SET, ".a*rret", "10"),
                createInstruction(SET, ".a*wret", "13"),
                createInstruction(JUMP, "16", "greaterThanEq", "0", "LIMIT"),
                createInstruction(OP, "mul", var(6), ":i", "2"),
                createInstruction(OP, "add", "@counter", "26", var(6)),
                createInstruction(OP, "add", var(4), ".a*r", "1"),
                createInstruction(SET, ".a*w", var(4)),
                createInstruction(OP, "add", "@counter", "20", var(6)),
                createInstruction(PRINT, ".a*r"),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, "8", "lessThan", ":i", "LIMIT"),
                createInstruction(PRINT, ".a*0"),
                createInstruction(PRINT, ".a*1"),
                createInstruction(PRINT, ".a*2"),
                createInstruction(END),
                createInstruction(SET, ".a*0", ".a*w"),
                createInstruction(SET, "@counter", ".a*wret"),
                createInstruction(SET, ".a*1", ".a*w"),
                createInstruction(SET, "@counter", ".a*wret"),
                createInstruction(SET, ".a*2", ".a*w"),
                createInstruction(SET, "@counter", ".a*wret"),
                createInstruction(SET, ".a*r", ".a*0"),
                createInstruction(SET, "@counter", ".a*rret"),
                createInstruction(SET, ".a*r", ".a*1"),
                createInstruction(SET, "@counter", ".a*rret"),
                createInstruction(SET, ".a*r", ".a*2"),
                createInstruction(SET, "@counter", ".a*rret"),
                createInstruction(PRINT, q(CompilerProfile.SIGNATURE))
        );
    }

}