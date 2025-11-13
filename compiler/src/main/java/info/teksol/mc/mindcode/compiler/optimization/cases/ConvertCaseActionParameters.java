package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ConvertCaseActionParameters(
        int group,
        AstContext context,
        LogicVariable variable,
        CaseExpression caseExpression,
        int originalCost,
        int originalSteps,
        boolean mindustryContent,
        boolean removeRangeCheck,
        boolean symbolic,
        boolean considerElse) {

    private ConvertCaseActionParameters(ConvertCaseActionParameters other) {
        this(other.group, other.context, other.variable, other.caseExpression.duplicate(), other.originalCost, other.originalSteps,
                other.mindustryContent, other.removeRangeCheck, other.symbolic, other.considerElse);
    }

    public ConvertCaseActionParameters duplicate() {
        return new ConvertCaseActionParameters(this);
    }

    public boolean handleNulls() {
        return mindustryContent || caseExpression.hasNullKey();
    }
}
