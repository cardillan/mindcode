package info.teksol.mindcode.compiler.optimization;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static info.teksol.mindcode.logic.Condition.EQUAL;
import static info.teksol.mindcode.logic.Condition.NOT_EQUAL;
import static info.teksol.mindcode.logic.Opcode.*;

@Order(99)
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
                createInstruction(JUMP, var(1001), "notEqual", "UNIT", "@poly"),
                createInstruction(SET, var(0), "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1003), "notEqual", "UNIT", "@mega"),
                createInstruction(SET, var(0), "2"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(0), "3"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "x", var(0)),
                createInstruction(PRINT, "x"),
                createInstruction(END)
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
                createInstruction(SENSOR, var(0), "vault1", "@firstItem"),
                createInstruction(JUMP, var(1001), "notEqual", var(0), "@lead"),
                createInstruction(SET, var(1), "1"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1003), "notEqual", var(0), "@coal"),
                createInstruction(SET, var(1), "2"),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(1), "3"),
                createInstruction(LABEL, var(1000)),
                createInstruction(SET, "x", var(1)),
                createInstruction(PRINT, "x"),
                createInstruction(END)
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
                        createInstruction(END)
                ),
                List.of(
                        createInstruction(SENSOR, var, vault1, firstItem),
                        createInstruction(JUMP, label0, EQUAL, var, lead),
                        createInstruction(JUMP, label1, EQUAL, var, coal),
                        createInstruction(END)
                )
        );
    }

    @Test
    void ignoresNonAstVariables() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(JUMP, label0, NOT_EQUAL, var, lead),
                createInstruction(JUMP, label1, NOT_EQUAL, var, coal),
                createInstruction(END)
        );
    }

    @Test
    void ignoresNonAstJumps() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(SET, ast0, var),
                createInstruction(JUMP, label0, NOT_EQUAL, ast0, lead),
                createInstruction(JUMP, label1, NOT_EQUAL, coal, ast0),
                createInstruction(END)
        );
    }

    @Test
    void ignoresWrongOrder() {
        assertDoesNotOptimize(
                createInstruction(SENSOR, var, vault1, firstItem),
                createInstruction(JUMP, label0, NOT_EQUAL, ast0, lead),
                createInstruction(SET, ast0, var),
                createInstruction(JUMP, label1, NOT_EQUAL, ast0, coal),
                createInstruction(END)
        );
    }
}
