package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class JumpNormalizerTest extends AbstractOptimizerTest<JumpNormalizer> {

    @Override
    protected Class<JumpNormalizer> getTestedClass() {
        return JumpNormalizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(Optimization.JUMP_NORMALIZATION);
    }

    @Test
    void normalizesConditionalJump() {
        assertCompilesTo("""
                        while false do
                            print("Here");
                        end;
                        """,
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(2), "always"),
                createInstruction(PRINT, q("Here")),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(2))
        );
    }

    @Test
    void removesAlwaysFalseJump() {
        assertCompilesTo("""
                        while true do
                            1;
                        end;
                        """,
                createInstruction(LABEL, label(0)),
                createInstruction(JUMP, label(0), "always")
        );
    }

    @Test
    void evaluatesBuiltInEqualComparison() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        if @coal == @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("no"))
        );
    }

    @Test
    void evaluatesBuiltInNonEqualComparison() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        if @coal != @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("yes"))
        );
    }

    @Test
    void evaluatesBuiltInLessThanOrEqualComparison() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        if @coal <= @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("yes"))
        );
    }

    @Test
    void evaluatesBuiltInLessThanComparison() {
        assertCompilesTo("""
                        #set optimization = advanced;
                        if @coal < @lead then
                            print("yes");
                        else
                            print("no");
                        end;
                        """,
                createInstruction(PRINT, q("no"))
        );
    }
}
