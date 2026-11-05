package io.github.cardillan.mindcode.compiler.optimization.cases;

import io.github.cardillan.mindcode.compiler.astcontext.AstContext;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.mimex.ContentType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ConvertCaseActionParameters(
        int group,
        AstContext context,
        LogicVariable variable,
        CaseExpression caseExpression,
        int originalCost,
        int originalSteps,
        ContentType contentType,        // ContentType.UNKNOWN represents an integer
        boolean removeRangeCheck,
        boolean symbolic,
        boolean considerElse) {

    private ConvertCaseActionParameters(ConvertCaseActionParameters other) {
        this(other.group, other.context, other.variable, other.caseExpression.duplicate(), other.originalCost, other.originalSteps,
                other.contentType, other.removeRangeCheck, other.symbolic, other.considerElse);
    }

    public ConvertCaseActionParameters duplicate() {
        return new ConvertCaseActionParameters(this);
    }

    public boolean mindustryContent() {
        return contentType != ContentType.UNKNOWN;
    }

    public boolean handleNulls() {
        return mindustryContent() || caseExpression.hasNullKey();
    }
}
