package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;

import java.util.List;
import java.util.Objects;

public class FunctionCall extends ControlBlockAstNode {
    private final String functionName;
    private final List<AstNode> params;

    FunctionCall(InputPosition inputPosition, String functionName, AstNode... params) {
        this(inputPosition, functionName, List.of(params));
    }

    FunctionCall(InputPosition inputPosition, String functionName, List<AstNode> params) {
        super(inputPosition, params);
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
