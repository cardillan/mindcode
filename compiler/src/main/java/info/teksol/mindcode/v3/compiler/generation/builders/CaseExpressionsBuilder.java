package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstCaseExpressionVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstCaseAlternative;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstCaseExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRange;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CaseExpressionsBuilder extends AbstractBuilder implements AstCaseExpressionVisitor<NodeValue> {
    public CaseExpressionsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitCaseExpression(AstCaseExpression node) {
        LogicVariable resultVar = nextNodeResultTemp();
        LogicLabel exitLabel = nextLabel();

        double multiplier = 1.0 / node.getAlternatives().size();
        int remain = node.getAlternatives().size();
        codeBuilder.setSubcontextType(AstSubcontextType.INIT, 1.0);
        LogicValue caseValue = temporaryCopy(visit(node.getExpression()), ArgumentType.AST_VARIABLE);

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
                        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        LogicLabel nextExp = nextLabel();       // Next value in when list
                        NodeValue minValue = visit(range.getFirstValue());
                        codeBuilder.createJump(nextExp, Condition.LESS_THAN, caseValue, minValue.getValue(codeBuilder));
                        // The max value is only evaluated when the min value lets us through
                        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        NodeValue maxValue = visit(range.getLastValue());
                        codeBuilder.createJump(bodyLabel, insideRangeCondition(range), caseValue, maxValue.getValue(codeBuilder));
                        codeBuilder.createLabel(nextExp);
                    }
                    else {
                        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, whenMultiplier);
                        NodeValue whenValue = visit(value);
                        codeBuilder.createJump(bodyLabel, Condition.EQUAL, caseValue, whenValue.getValue(codeBuilder));
                    }
                }
            });

            // No match in the "when" value list: skip to the next alternative
            codeBuilder.createJumpUnconditional(nextAlt);

            // Body of the alternative
            codeBuilder.setSubcontextType(AstSubcontextType.BODY, multiplier);
            codeBuilder.createLabel(bodyLabel);
            NodeValue bodyValue = visitExpressions(alternative.getBody());
            codeBuilder.createSet(resultVar, bodyValue.getValue(codeBuilder));
            codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
            codeBuilder.createJumpUnconditional(exitLabel);
            codeBuilder.createLabel(nextAlt);
            codeBuilder.clearSubcontextType();
        }

        codeBuilder.setSubcontextType(AstSubcontextType.ELSE, multiplier);
        NodeValue elseValue = visitExpressions(node.getElseBranch());
        codeBuilder.createSet(resultVar, elseValue.getValue(codeBuilder));
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        codeBuilder.createLabel(exitLabel);
        codeBuilder.clearSubcontextType();

        return resultVar;
    }
}
