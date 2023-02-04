package info.teksol.mindcode.ast;

public class InclusiveRange extends Range {
    InclusiveRange(AstNode firstValue, AstNode lastValue) {
        super(firstValue, lastValue);
    }

    @Override
    public String maxValueComparison() {
        return "<=";
    }
}
