package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.logic.Condition;
import org.antlr.v4.runtime.Token;

public class InclusiveRange extends Range {
    InclusiveRange(Token startToken, InputFile inputFile, AstNode firstValue, AstNode lastValue) {
        super(startToken, inputFile, firstValue, lastValue);
    }

    @Override
    public String operator() {
        return "..";
    }

    @Override
    public Condition maxValueComparison() {
        return Condition.LESS_THAN_EQ;
    }
}
