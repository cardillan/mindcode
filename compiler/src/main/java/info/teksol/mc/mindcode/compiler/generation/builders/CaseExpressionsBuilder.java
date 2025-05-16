package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstCaseExpressionVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CaseExpressionsBuilder extends AbstractBuilder implements AstCaseExpressionVisitor<ValueStore> {
    public CaseExpressionsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitCaseExpression(AstCaseExpression node) {
        LogicVariable resultVar = assembler.nextNodeResultTemp();
        LogicLabel exitLabel = assembler.nextLabel();

        double multiplier = 1.0 / node.getAlternatives().size();
        int remain = node.getAlternatives().size();
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);
        LogicValue caseValue = assembler.defensiveCopy(evaluate(node.getExpression()), ArgumentType.AST_VARIABLE);

        boolean hasNull = node.getAlternatives().stream().flatMap(a -> a.getValues().stream())
                .anyMatch(v -> v instanceof AstLiteralNull);

        for (AstCaseAlternative alternative : node.getAlternatives()) {
            LogicLabel nextAlt = assembler.nextLabel();         // Next alternative
            LogicLabel bodyLabel = assembler.nextLabel();       // Body of this alternative
            double whenMultiplier = multiplier * remain--;

            variables.excludeVariablesFromTracking(() -> {
                // Each matching value, including the last one, causes a jump to the "when" body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstExpression value : alternative.getValues()) {
                    if (value instanceof AstRange range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        LogicLabel nextExp = assembler.nextLabel();       // Next value in when list
                        ValueStore minValue = evaluate(range.getFirstValue());
                        assembler.createJump(nextExp, Condition.LESS_THAN, caseValue, minValue.getValue(assembler));
                        // The max value is only evaluated when the min value lets us through
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        ValueStore maxValue = evaluate(range.getLastValue());
                        assembler.createJump(bodyLabel, insideRangeCondition(range), caseValue, maxValue.getValue(assembler));
                        assembler.createLabel(nextExp);
                    } else if (value instanceof AstLiteralNull) {
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        ValueStore whenValue = evaluate(value);
                        assembler.createJump(bodyLabel, Condition.STRICT_EQUAL, caseValue, whenValue.getValue(assembler));
                    } else {
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        ValueStore whenValue = evaluate(value);
                        if (hasNull && value instanceof AstLiteralNumeric && whenValue instanceof LogicNumber n && n.getDoubleValue() == 0.0) {
                            assembler.createJump(bodyLabel, Condition.STRICT_EQUAL, caseValue, whenValue.getValue(assembler));
                        } else {
                            assembler.createJump(bodyLabel, Condition.EQUAL, caseValue, whenValue.getValue(assembler));
                        }
                    }
                }
            });

            // No match in the "when" value list: skip to the next alternative
            assembler.createJumpUnconditional(nextAlt);

            // Body of the alternative
            assembler.setSubcontextType(AstSubcontextType.BODY, multiplier);
            assembler.createLabel(bodyLabel);
            ValueStore bodyValue = evaluateBody(alternative.getBody());
            assembler.createSet(resultVar, bodyValue.getValue(assembler));
            assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
            assembler.createJumpUnconditional(exitLabel);
            assembler.createLabel(nextAlt);
            assembler.clearSubcontextType();
        }

        assembler.setSubcontextType(AstSubcontextType.ELSE, multiplier);
        ValueStore elseValue = evaluateBody(node.getElseBranch());
        assembler.createSet(resultVar, elseValue.getValue(assembler));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        assembler.createLabel(exitLabel);
        assembler.clearSubcontextType();

        return resultVar;
    }
}
