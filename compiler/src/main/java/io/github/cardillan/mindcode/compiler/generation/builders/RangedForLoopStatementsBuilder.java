package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstIdentifier;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstMindcodeNode;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstRangedForLoopStatement;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.evaluator.CompileTimeEvaluator;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.LoopStack.LoopLabels;
import io.github.cardillan.mindcode.compiler.generation.variables.Modifiers;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.compiler.generation.variables.VariableScope;
import io.github.cardillan.mindcode.generated.ast.visitors.AstRangedForLoopStatementVisitor;
import io.github.cardillan.mindcode.logic.arguments.*;
import io.github.cardillan.mindcode.messages.ERR;
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
        AstMindcodeNode lowerBound = compileTimeEvaluator.evaluate(node.getRange().getFirst(), isLocalContext(), false);
        AstMindcodeNode upperBound = compileTimeEvaluator.evaluate(node.getRange().getLast(), isLocalContext(), false);

        int multiplier = lowerBound instanceof LogicLiteral lowerValue && lowerValue.isNumericLiteral()
                         && upperBound instanceof LogicLiteral upperValue && upperValue.isNumericLiteral()
                ? (int) (upperValue.getDoubleValue() - lowerValue.getDoubleValue()) : LOOP_REPETITIONS;

        // Initialization
        assembler.setSubcontextType(AstSubcontextType.INIT, 1.0);

        // Encapsulate both: they're evaluated just here and not propagated anywhere
        ValueStore lowerValue = variables.excludeVariablesFromTracking(() -> evaluate(node.getRange().getFirst()));
        ValueStore upperValue = variables.excludeVariablesFromTracking(() -> evaluate(node.getRange().getLast()));

        if (node.hasDeclaration()) {
            if (node.getVariable() instanceof AstIdentifier identifier) {
                variables.createVariable(isLocalContext(), identifier, VariableScope.LOOP_CONTROL, Modifiers.EMPTY);
            } else {
                error(node.getVariable(), ERR.IDENTIFIER_EXPECTED);
            }
        } else if (globalProfile.isStrict()) {
            error(node, ERR.LOOP_CONTROL_VARIABLE_NOT_DECLARED);
        }

        if (node.getVariable() instanceof AstIdentifier identifier){
            variables.registerLoopControlVariable(identifier, node.hasDeclaration());
        }

        ValueStore loopControlVariable = resolveLValue(node.getVariable());

        LogicValue fixedFinalBound;
        Condition condition;

        if (node.isDescending()) {
            // Store the lower value in a temporary variable registered in parent.
            fixedFinalBound = lowerValue instanceof LogicBuiltIn builtin
                    ? builtin
                    : assembler.defensiveCopy(lowerValue, ArgumentType.TMP_VARIABLE);

            condition = Condition.LESS_THAN;

            if (node.getRange().isExclusive()) {
                loopControlVariable.writeValue(assembler,
                        target -> assembler.createOp(Operation.SUB, target, upperValue.getValue(assembler), LogicNumber.ONE));
            } else {
                loopControlVariable.setValue(assembler, upperValue.getValue(assembler));
            }
        } else {
            // Store the upper value in a temporary variable registered in parent.
            fixedFinalBound = upperValue instanceof LogicBuiltIn builtin
                    ? builtin
                    : assembler.defensiveCopy(upperValue, ArgumentType.TMP_VARIABLE);

            // Condition for variable exceeding the upper bound of the range
            condition = outsideRangeCondition(node.getRange());

            loopControlVariable.setValue(assembler, lowerValue.getValue(assembler));
        }


        final LogicLabel beginLabel = assembler.nextLabel();
        LoopLabels loopLabels = enterLoop(node, "for");

        // Condition
        assembler.setSubcontextType(AstSubcontextType.CONDITION, multiplier);
        assembler.createLabel(beginLabel);
        assembler.createJump(loopLabels.breakLabel(), condition, loopControlVariable.getValue(assembler), fixedFinalBound);

        // Loop body
        assembler.setSubcontextType(AstSubcontextType.BODY, multiplier);
        visitBody(node.getBody());

        // Continue label
        // The label needs to be part of loop body so that it gets copied on loop unrolling
        assembler.createLabel(loopLabels.continueLabel());

        // Update
        assembler.setSubcontextType(AstSubcontextType.UPDATE, multiplier);
        LogicValue oldValue = loopControlVariable.getValue(assembler);
        loopControlVariable.writeValue(assembler, target -> assembler.createOp(
                node.isDescending() ? Operation.SUB : Operation.ADD, target, oldValue, LogicNumber.ONE));

        // Flow control
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, multiplier);
        assembler.createJumpUnconditional(beginLabel);

        // Exit
        assembler.createLabel(loopLabels.breakLabel());
        assembler.clearSubcontextType();
        exitLoop(loopLabels);

        return LogicVoid.VOID;
    }
}
