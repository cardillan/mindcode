package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.GenerationGoal;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class LoopOptimizerTest extends AbstractOptimizerTest<LoopOptimizer> {

    @Override
    protected Class<LoopOptimizer> getTestedClass() {
        return LoopOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return Optimization.LIST;
    }

    @Override
    protected CompilerProfile createCompilerProfile() {
        return super.createCompilerProfile().setGoal(GenerationGoal.SPEED);
    }

    @Test
    void optimizesRangedForLoops() {
        assertCompilesTo("""
                        for i in 0 ... 1000 do
                            cell1[i] = 1;
                        end;
                        print("Done.");
                        """,
                createInstruction(SET, "i", "0"),
                createInstruction(LABEL, var(1003)),
                createInstruction(WRITE, "1", "cell1", "i"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1003), "lessThan", "i", "1000"),
                createInstruction(PRINT, q("Done."))
        );
    }

    @Test
    void optimizesWhileLoop() {
        assertCompilesTo("""
                        i = 1000;
                        while i > 0 do
                            print(i);
                            i -= 1;
                        end;
                        """,
                createInstruction(SET, "i", "1000"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(OP, "sub", "i", "i", "1"),
                createInstruction(JUMP, var(1003), "greaterThan", "i", "0")
        );
    }

    @Test
    void optimizesWhileLoopComparingToNull() {
        assertCompilesTo("""
                        block = null;
                        while block == null do
                            block = getlink(1);
                        end;
                        print(block);
                        """,
                createInstruction(SET, "block", "null"),
                createInstruction(LABEL, var(1003)),
                createInstruction(GETLINK, "block", "1"),
                createInstruction(JUMP, var(1003), "equal", "block", "null"),
                createInstruction(PRINT, "block")
        );
    }

    @Test
    void optimizesWhileLoopStrictEqual() {
        assertCompilesTo(
                expectedMessages()
                        .add("Variable 'state' is not initialized.")
                        .add("Variable 'i' is not initialized."),
                """
                        while state === 0 do
                            print(i);
                            state = @unit.@dead;
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(OP, "strictEqual", var(0), "state", "0"),
                createInstruction(JUMP, "__start__", "equal", var(0), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(SENSOR, "state", "@unit", "@dead"),
                createInstruction(JUMP, var(1003), "strictEqual", "state", "0")
        );
    }

    @Test
    void optimizesWhileLoopWithInitialization() {
        assertCompilesTo("""
                        count = 0;
                        while switch1.@enabled do
                            print(count += 1);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "count", "0"),
                createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                createInstruction(JUMP, "__start__", "equal", var(0), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(OP, "add", "count", "count", "1"),
                createInstruction(PRINT, "count"),
                createInstruction(SENSOR, var(0), "switch1", "@enabled"),
                createInstruction(JUMP, var(1003), "notEqual", var(0), "false")
        );
    }

    @Test
    void optimizesWhileLoopWithInitializationAndStrictEqual() {
        assertCompilesTo("""
                        while @unit.@dead === 0 do
                            print("Got unit!");
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(OP, "strictEqual", var(1), var(0), "0"),
                createInstruction(JUMP, "__start__", "equal", var(1), "false"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, q("Got unit!")),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1003), "strictEqual", var(0), "0")
        );
    }

    @Test
    void optimizesRangedForLoopsWithBreak() {
        assertCompilesTo("""
                        for i in 1 .. 1000 do
                            print(i);
                            if i > 5 then
                                break;
                            end;
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SET, "i", "1"),
                createInstruction(LABEL, var(1005)),
                createInstruction(PRINT, "i"),
                createInstruction(JUMP, "__start__", "greaterThan", "i", "5"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1005), "lessThanEq", "i", "1000")
        );
    }

    @Test
    void optimizesWhileLoopWithContinue() {
        assertCompilesTo("""
                        i = 1000;
                        while i > 0 do
                            i -= 1;
                            if i == 4 then
                                continue;
                            end;
                            print(i);
                        end;
                        """,
                createInstruction(SET, "i", "1000"),
                createInstruction(LABEL, var(1005)),
                createInstruction(OP, "sub", "i", "i", "1"),
                createInstruction(JUMP, var(1001), "equal", "i", "4"),
                createInstruction(PRINT, "i"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1005), "greaterThan", "i", "0")
        );
    }

    @Test
    void optimizesBitReadTest() {
        assertCompilesTo("""
                        def getBit(bitIndex)
                          bitIndex % 2;
                        end;

                        for i in 0 ... 1000 do
                            print(getBit(i) ? 1 : 0);
                        end;
                        """,
                createInstruction(SET, ":i", "0"),
                createInstruction(LABEL, label(6)),
                createInstruction(OP, "mod", tmp(0), ":i", "2"),
                createInstruction(SELECT, tmp(2), "notEqual", tmp(0), "false", "1", "0"),
                createInstruction(PRINT, tmp(2)),
                createInstruction(OP, "add", ":i", ":i", "1"),
                createInstruction(JUMP, label(6), "lessThan", ":i", "1000")
        );
    }

    @Test
    void optimizesUpdatesInConditions() {
        assertCompilesTo("""
                        i = 0;
                        while (i += 1) <= 2000 do
                            print(i);
                        end;
                        """,
                createInstruction(SET, "i", "1"),
                createInstruction(LABEL, var(1003)),
                createInstruction(PRINT, "i"),
                createInstruction(OP, "add", "i", "i", "1"),
                createInstruction(JUMP, var(1003), "lessThanEq", "i", "2000")
        );
    }
}