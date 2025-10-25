package info.teksol.mc.mindcode.compiler.optimization;

import info.teksol.mc.mindcode.logic.arguments.Condition;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.compiler.optimization.Optimization.JUMP_STRAIGHTENING;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class JumpStraighteningTest extends AbstractOptimizerTest<JumpStraightening> {

    @Override
    protected Class<JumpStraightening> getTestedClass() {
        return JumpStraightening.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(JUMP_STRAIGHTENING);
    }

    @Test
    void optimizesBreakInWhileLoop() {
        assertCompilesTo("""
                        #set emulate-strict-not-equal = false;
                        #set dead-code-elimination = advanced;
                        #set single-step-elimination = advanced;
                        #set jump-optimization = advanced;
                        #set jump-straightening = advanced;
                        while true do
                            print("In loop");
                            if @unit.@dead === 0 then
                                break;
                            end;
                        end;
                        print("Out of loop");
                        """,
                createInstruction(LABEL, var(1000)),
                createInstruction(JUMP, var(1002), "equal", "true", "false"),
                createInstruction(PRINT, q("In loop")),
                createInstruction(SENSOR, var(0), "@unit", "@dead"),
                createInstruction(JUMP, var(1002), "strictEqual", var(0), "0"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002)),
                createInstruction(PRINT, q("Out of loop"))
        );
    }

    @Test
    void optimizesBreakInWhileLoopTarget8() {
        assertCompilesTo("""
                        #set dead-code-elimination = advanced;
                        #set single-step-elimination = advanced;
                        #set jump-optimization = advanced;
                        #set jump-straightening = advanced;
                        while true do
                            print("In loop");
                            if @unit.@dead === 0 then
                                break;
                            end;
                        end;
                        print("Out of loop");
                        """,
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(2), "equal", "true", "false"),
                createInstruction(PRINT, q("In loop")),
                createInstruction(SENSOR, tmp(0), "@unit", "@dead"),
                createInstruction(JUMP, label(0), "strictNotEqual", tmp(0), "0"),
                createInstruction(LABEL, label(2)),
                createInstruction(PRINT, q("Out of loop"))
        );
    }

    @Test
    void optimizesFunctionReturn() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        displayItem();
                        
                        void displayItem()
                            amount = vault1.@coal;
                            if amount == 0 then return; end;
                            print(amount % 10);
                        end;
                        """,
                createInstruction(LABEL, "__start__"),
                createInstruction(SENSOR, "__fn0_amount", "vault1", "@coal"),
                createInstruction(JUMP, "__start__", "equal", "__fn0_amount", "0"),
                createInstruction(OP, "mod", var(4), "__fn0_amount", "10"),
                createInstruction(PRINT, var(4))
        );
    }

    @Test
    void optimizesMinimalSequence() {
        assertOptimizesTo(
                List.of(
                        createInstruction(LABEL, label0),
                        createInstruction(JUMP, label1, Condition.EQUAL, a, b),
                        createInstruction(JUMP, label0, Condition.ALWAYS),
                        createInstruction(LABEL, label1),
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(LABEL, label0),
                        createInstruction(JUMP, label0, Condition.NOT_EQUAL, a, b),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresStrictEqual() {
        profile.setEmulateStrictNotEqual(false);
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.STRICT_EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }

    @Test
    void ignoresDistantJumps() {
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.STRICT_EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.ALWAYS),
                createInstruction(PRINT, a),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }

    @Test
    void ignoresConditionalJumps() {
        assertDoesNotOptimize(
                createInstruction(LABEL, label0),
                createInstruction(JUMP, label1, Condition.EQUAL, a, b),
                createInstruction(JUMP, label0, Condition.EQUAL, c, d),
                createInstruction(LABEL, label1),
                createInstruction(END)
        );
    }
}