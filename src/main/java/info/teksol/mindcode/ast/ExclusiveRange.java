package info.teksol.mindcode.ast;

public class ExclusiveRange extends Range {
    ExclusiveRange(AstNode firstValue, AstNode lastValue) {
        super(firstValue, lastValue);
    }

    @Override
    public AstNode buildLoopExitCondition(AstNode name) {
        return new BinaryOp(
                name,
                "<",
                getLastValue()
        );
    }
}
