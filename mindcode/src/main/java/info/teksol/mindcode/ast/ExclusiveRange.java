package info.teksol.mindcode.ast;

import info.teksol.mindcode.logic.Condition;

public class ExclusiveRange extends Range {
    ExclusiveRange(AstNode firstValue, AstNode lastValue) {
        super(firstValue, lastValue);
    }

    @Override
    public String operator() {
        return "...";
    }

    @Override
    public Condition maxValueComparison() {
        return Condition.LESS_THAN;
    }
}
