package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstRangedForLoopStatementVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRangedForLoopStatement;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RangedForLoopStatementsBuilder extends AbstractLoopBuilder implements AstRangedForLoopStatementVisitor<ValueStore> {
    private final CompileTimeEvaluator compileTimeEvaluator;

    public RangedForLoopStatementsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
        compileTimeEvaluator = context.compileTimeEvaluator();
    }

    @Override
    public ValueStore visitRangedForLoopStatement(AstRangedForLoopStatement node) {
        // TODO Compute number of repetitions only when requested by compiler option
        AstMindcodeNode lowerBound = compileTimeEvaluator.evaluate(node.getRange().getFirstValue());
        AstMindcodeNode upperBound = compileTimeEvaluator.evaluate(node.getRange().getLastValue());

        int multiplier = lowerBound instanceof LogicLiteral lowerValue && lowerValue.isNumericLiteral()
                         && upperBound instanceof LogicLiteral upperValue && upperValue.isNumericLiteral()
                ? (int) (upperValue.getDoubleValue() - lowerValue.getDoubleValue()) : LOOP_REPETITIONS;

        // Initialization
        assembler.setSubcontextType(AstSubcontextType.INIT, multiplier);

        // Encapsulate both: they're evaluated just here and not propagated anywhere
        ValueStore lowerValue = variables.excludeVariablesFromTracking(() -> evaluate(node.getRange().getFirstValue()));
        ValueStore upperValue = variables.excludeVariablesFromTracking(() -> evaluate(node.getRange().getLastValue()));

        // Store the upper value in a temporary variable registered in parent.
        LogicValue fixedUpperBound = defensiveCopy(upperValue, ArgumentType.TMP_VARIABLE);

        // Condition for variable exceeding the upper bound of the range
        Condition condition = outsideRangeCondition(node.getRange());
        ValueStore loopControlVariable = resolveLValue(node.getVariable());
        loopControlVariable.setValue(assembler, lowerValue.getValue(assembler));

        final LogicLabel beginLabel = nextLabel();
        LoopLabels loopLabels = enterLoop(node);

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, multiplier);
        assembler.createLabel(beginLabel);
        assembler.createJump(loopLabels.doneLabel(), condition, loopControlVariable.getValue(assembler), fixedUpperBound);

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, multiplier);
        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Update
        assembler.setSubcontextType(AstSubcontextType.UPDATE, multiplier);
        LogicValue oldValue = loopControlVariable.getValue(assembler);
        loopControlVariable.writeValue(assembler,
                tmp -> assembler.createOp(Operation.ADD, tmp, oldValue, LogicNumber.ONE));

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.doneLabel());
        assembler.clearSubcontextType();
        exitLoop(node);

        return LogicVoid.VOID;
    }
}