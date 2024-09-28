package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.SourceFile;
import info.teksol.mindcode.compiler.generator.AstContextType;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Objects;

public class FunctionCall extends ControlBlockAstNode {
    private final String functionName;
    private final List<AstNode> params;

    FunctionCall(Token startToken, SourceFile sourceFile, String functionName, AstNode... params) {
        this(startToken, sourceFile, functionName, List.of(params));
    }

    FunctionCall(Token startToken, SourceFile sourceFile, String functionName, List<AstNode> params) {
        super(startToken, sourceFile, params);
        this.functionName = functionName;
        this.params = params;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<AstNode> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCall functionCall = (FunctionCall) o;
        return Objects.equals(functionName, functionCall.functionName) &&
                Objects.equals(params, functionCall.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, params);
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", params=" + params +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.CALL;
    }
}
