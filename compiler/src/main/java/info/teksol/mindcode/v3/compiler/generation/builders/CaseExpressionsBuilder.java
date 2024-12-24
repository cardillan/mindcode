package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstCaseExpressionVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstCaseAlternative;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRange;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CaseExpressionsBuilder extends AbstractBuilder implements AstCaseExpressionVisitor<NodeValue> {
    public CaseExpressionsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public NodeValue visitCaseExpression(AstCaseExpression node) {
        LogicVariable resultVar = nextNodeResultTemp();
        LogicLabel exitLabel = nextLabel();

        double multiplier = 1.0 / node.getAlternatives().size();
        int remain = node.getAlternatives().size();
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);
        LogicValue caseValue = temporaryCopy(evaluate(node.getExpression()), ArgumentType.AST_VARIABLE);

        for (AstCaseAlternative alternative : node.getAlternatives()) {
            LogicLabel nextAlt = nextLabel();         // Next alternative
            LogicLabel bodyLabel = nextLabel();       // Body of this alternative
            double whenMultiplier = multiplier * remain--;

            variables.excludeVariablesFromTracking(() -> {
                // Each matching value, including the last one, causes a jump to the "when" body
                // At the end of the list is a jump to the next alternative (next "when" branch)
                // JumpOverJumpEliminator will improve the generated code
                for (AstExpression value : alternative.getValues()) {
                    if (value instanceof AstRange range) {
                        // Range evaluation requires two comparisons. Instead of using "and" operator, we compile them into two jumps
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        LogicLabel nextExp = nextLabel();       // Next value in when list
                        NodeValue minValue = evaluate(range.getFirstValue());
                        assembler.createJump(nextExp, Condition.LESS_THAN, caseValue, minValue.getValue(assembler));
                        // The max value is only evaluated when the min value lets us through
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        NodeValue maxValue = evaluate(range.getLastValue());
                        assembler.createJump(bodyLabel, insideRangeCondition(range), caseValue, maxValue.getValue(assembler));
                        assembler.createLabel(nextExp);
                    }
                    else {
                        assembler.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        NodeValue whenValue = evaluate(value);
                        assembler.createJump(bodyLabel, Condition.EQUAL, caseValue, whenValue.getValue(assembler));
                    }
                }
            });

            // No match in the "when" value list: skip to the next alternative
            assembler.createJumpUnconditional(nextAlt);

            // Body of the alternative
            assembler.setSubcontextType(AstSubcontextType.BODY, multiplier);
            assembler.createLabel(bodyLabel);
            NodeValue bodyValue = visitExpressions(alternative.getBody());
            assembler.createSet(resultVar, bodyValue.getValue(assembler));
            assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
            assembler.createJumpUnconditional(exitLabel);
            assembler.createLabel(nextAlt);
            assembler.clearSubcontextType();
        }

        assembler.setSubcontextType(AstSubcontextType.ELSE, multiplier);
        NodeValue elseValue = visitExpressions(node.getElseBranch());
        assembler.createSet(resultVar, elseValue.getValue(assembler));
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        assembler.createLabel(exitLabel);
        assembler.clearSubcontextType();

        return resultVar;
    }
}
