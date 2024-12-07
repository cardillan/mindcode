package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;

public interface LogicLiteral extends LogicValue, LogicReadable {

    @Override
    default boolean isLvalue() {
        return false;
    }

    @Override
    default boolean isLiteral() {
        return true;
    }

    @Override
    default boolean isConstant() {
        return true;
    }

    @Override
    default boolean canEvaluate() {
        return true;
    }

    boolean isNull();

    double getDoubleValue();

    AstMindcodeNode asAstNode(SourcePosition position);
}
