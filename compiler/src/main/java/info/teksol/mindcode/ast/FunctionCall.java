package info.teksol.mindcode.ast;

import java.util.List;
import java.util.Objects;

public class FunctionCall extends BaseAstNode {
    private final String functionName;
    private final List<AstNode> params;

    FunctionCall(String functionName, AstNode... params) {
        this(functionName, List.of(params));
    }

    FunctionCall(String functionName, List<AstNode> params) {
        super(params);
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
}
