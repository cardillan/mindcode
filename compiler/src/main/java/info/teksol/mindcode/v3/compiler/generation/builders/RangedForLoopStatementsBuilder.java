package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstRangedForLoopStatementVisitor;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRangedForLoopStatement;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RangedForLoopStatementsBuilder extends AbstractLoopBuilder implements AstRangedForLoopStatementVisitor<NodeValue> {
    private final CompileTimeEvaluator compileTimeEvaluator;

    public RangedForLoopStatementsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
        compileTimeEvaluator = context.compileTimeEvaluator();
    }

    @Override
    public NodeValue visitRangedForLoopStatement(AstRangedForLoopStatement node) {
        // TODO Compute number of repetitions only when requested by compiler option
        AstMindcodeNode lowerBound = compileTimeEvaluator.evaluate(node.getRange().getFirstValue());
        AstMindcodeNode upperBound = compileTimeEvaluator.evaluate(node.getRange().getLastValue());

        int multiplier = lowerBound instanceof LogicLiteral lowerValue && lowerValue.isNumericLiteral()
                         && upperBound instanceof LogicLiteral upperValue && upperValue.isNumericLiteral()
                ? (int) (upperValue.getDoubleValue() - lowerValue.getDoubleValue()) : LOOP_REPETITIONS;

        // Initialization
        codeBuilder.setSubcontextType(AstSubcontextType.INIT, multiplier);

        // Encapsulate both: they're evaluated just here and not propagated anywhere
        NodeValue lowerValue = variables.excludeVariablesFromTracking(() -> visit(node.getRange().getFirstValue()));
        NodeValue upperValue = variables.excludeVariablesFromTracking(() -> visit(node.getRange().getLastValue()));

        // Store the upper value in a temporary variable registered in parent.
        LogicValue fixedUpperBound = temporaryCopy(upperValue, ArgumentType.TMP_VARIABLE);

        // Condition for variable exceeding the upper bound of the range
        Condition condition = outsideRangeCondition(node.getRange());
        NodeValue loopControlVariable = resolveLValue(node.getVariable());
        loopControlVariable.setValue(codeBuilder, lowerValue.getValue(codeBuilder));

        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        codeBuilder.setSubcontextType(AstSubcontextType.CONDITION, multiplier);
        codeBuilder.createLabel(beginLabel);
        codeBuilder.createJump(loopLabels.doneLabel(), condition, loopControlVariable.getValue(codeBuilder), fixedUpperBound);

        // Loop body
        codeBuilder.setSubcontextType(AstSubcontextType.BODY, multiplier);
        visitStatements(node.getBody());

        // Update
        codeBuilder.createLabel(loopLabels.continueLabel());
        codeBuilder.setSubcontextType(AstSubcontextType.UPDATE, multiplier);
        LogicValue oldValue = loopControlVariable.getValue(codeBuilder);
        loopControlVariable.writeValue(codeBuilder,
                tmp -> codeBuilder.createOp(Operation.ADD, tmp, oldValue, LogicNumber.ONE));

        // Flow control
        codeBuilder.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        codeBuilder.createJumpUnconditional(beginLabel);

        // Exit
        codeBuilder.createLabel(loopLabels.doneLabel());
        codeBuilder.clearSubcontextType();
        exitLoop(node);

        return LogicVoid.VOID;
    }
}
