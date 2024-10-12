package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.logic.Condition;

public class ExclusiveRange extends Range {
    ExclusiveRange(InputPosition inputPosition, AstNode firstValue, AstNode lastValue) {
        super(inputPosition, firstValue, lastValue);
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
