package info.teksol.mc.mindcode.compiler.optimization;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mc.mindcode.logic.arguments.Condition.EQUAL;
import static info.teksol.mc.mindcode.logic.arguments.Condition.NOT_EQUAL;
import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CaseExpressionOptimizerTest extends AbstractOptimizerTest<CaseExpressionOptimizer> {

    @Override
    protected Class<CaseExpressionOptimizer> getTestedClass() {
        return CaseExpressionOptimizer.class;
    }

    @Override
    protected List<Optimization> getAllOptimizations() {
        return List.of(
                Optimization.TEMP_VARIABLES_ELIMINATION,
                Optimization.JUMP_STRAIGHTENING,
                Optimization.CASE_EXPRESSION_OPTIMIZATION
        );
    }

    @Test
    void optimizesCaseWithVariable() {
        assertCompilesTo("""
                        x = case UNIT
                            when @poly then 1;
                            when @mega then 2;
                            else 3;
                        end;
                        print(x);
                        """,
                createInstruction(JUMP, label(1), "notEqual", ".UNIT", "@poly"),
                createInstruction(SET, tmp(0), "1"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(3), "notEqual", ".UNIT", "@mega"),
                createInstruction(SET, tmp(0), "2"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(3)),
                createInstruction(SET, tmp(0), "3"),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, ":x", tmp(0)),
                createInstruction(PRINT, ":x")
        );
    }

    @Test
    void optimizesCaseWithExpression() {
        assertCompilesTo("""
                        x = case vault1.@firstItem
                            when @lead then 1;
                            when @coal then 2;
                            else 3;
                        end;
                        print(x);
                        """,
                createInstruction(SENSOR, tmp(1), "vault1", "@firstItem"),
                createInstruction(JUMP, label(1), "notEqual", tmp(1), "@lead"),
                createInstruction(SET, tmp(0), "1"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(1)),
                createInstruction(JUMP, label(3), "notEqual", tmp(1), "@coal"),
                createInstruction(SET, tmp(0), "2"),
                createInstruction(JUMP, label(0), "always"),
                createInstruction(LABEL, label(3)),
                createInstruction(SET, tmp(0), "3"),
                createInstruction(LABEL, label(0)),
                createInstruction(SET, ":x", tmp(0)),
                createInstruction(PRINT, ":x")
        );
    }


    @Test
    void optimizesMinimalSequence() {
        assertOptimizesTo(
                List.of(
                        createInstruction(SENSOR, var, vault1, firstItem),
                        createInstruction(SET, ast0, var),
                        createInstruction(JUMP, label0, EQUAL, ast0, lead),
                        createInstruction(JUMP, label1, EQUAL, ast0, coal),
                        createInstruction(LABEL, label0),
                        createInstruction(LABEL, label1)
                ),
                List.of(
                        createInstruction(SENSOR, var, vault1, firstItem),
                        createInstruction(JUMP, label0, EQUAL, var, lead),
                        createInstruction(JUMP, label1, EQUAL, var, coal),
                        createInstruction(LABEL, label0),
                        createInstruction(LABEL, label1)
                )
        );
    }

    @Test
    void ignoresNonAstVariables() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(JUMP, label0, NOT_EQUAL, var, lead),
                createInstruction(JUMP, label1, NOT_EQUAL, var, coal),
                createInstruction(LABEL, label0),
                createInstruction(LABEL, label1)
        );
    }

    @Test
    void ignoresNonAstJumps() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(SET, ast0, var),
                createInstruction(JUMP, label0, NOT_EQUAL, ast0, lead),
                createInstruction(JUMP, label1, NOT_EQUAL, coal, ast0),
                createInstruction(LABEL, label0),
                createInstruction(LABEL, label1)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(JUMP, label0, NOT_EQUAL, ast0, lead),
                createInstruction(SET, ast0, var),
                createInstruction(JUMP, label1, NOT_EQUAL, ast0, coal),
                createInstruction(LABEL, label0),
                createInstruction(LABEL, label1)
        );
    }
}
