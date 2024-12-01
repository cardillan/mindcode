package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.IntRange;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration extends BaseAstNode {
    private final String codeDoc;
    private final boolean inline;
    private final boolean noinline;
    private final boolean procedure;
    private final String name;
    private final int callSize;

    private final List<FunctionParameter> params;
    private final AstNode body;

    public FunctionDeclaration(InputPosition inputPosition, String codeDoc, boolean inline, boolean noinline, boolean procedure,
            String name, List<FunctionParameter> params, AstNode body) {
        super(inputPosition, body);
        if (inline && noinline) {
            throw new IllegalArgumentException("Both inline and noinline specified.");
        }
        this.codeDoc = codeDoc;
        this.inline = inline;
        this.noinline = noinline;
        this.procedure = procedure;
        this.name = Objects.requireNonNull(name);
        this.params = Objects.requireNonNull(params);
        this.body = Objects.requireNonNull(body);
        this.callSize = computeCallSize();
    }

    public String getCodeDoc() {
        return codeDoc;
    }

    private int computeCallSize() {
        // Call size: setting up return address, jump to function, jump back from function
        // Note: the function return value is set by the function and not generated at the call site,
        //       therefore it is not counted.
        int count = 3;
        for (FunctionParameter param : params) {
            // One instruction per input parameter (setting up the value)
            // One instruction per output parameter (retrieving the output value)
            // in out parameter generates two instructions!
            count += (param.isInput() ? 1 : 0) + (param.isOutput() ? 1 : 0);
        }
        return count;
    }

    /**
     * Returns the size of a non-recursive function call to this function. The size corresponds both to the
     * number of instructions generated and the number of steps executed per call. It is assumed that all output
     * parameters produced by the function are read - if they aren't, the corresponding instruction might not
     * be generated.
     * <p>
     * A return (jump back to the call site) instruction is included in the count. This additional instruction
     * is always generated, but might not be executed, fo example when returning from the middle of the function.
     * <p>
     * The returned value makes no sense for vararg functions, as vararg functions must always be compiled inline.
     * For inlined functions, the returned value would be valid if the function wasn't inlined.
     *
     * @return the number of instructions needed for a non-recursive call to this function
     */
    public int getCallSize() {
        return callSize;
    }

    public boolean isInline() {
        return inline;
    }

    public boolean isNoinline() {
        return noinline;
    }

    public boolean isProcedure() {
        return procedure;
    }

    public boolean isVarArg() {
        return !params.isEmpty() && params.getLast().isVarArgs();
    }

    public IntRange getParameterCount() {
        if (params.isEmpty()) {
            return new IntRange(0, 0);
        } else if (isVarArg()) {
            int min = params.size() == 1 ? 0 :
                    CollectionUtils.findLastIndex(params.subList(0, params.size() - 1), FunctionParameter::isCompulsory) + 1;
            return new IntRange(min, Integer.MAX_VALUE);
        } else {
            int min = CollectionUtils.findLastIndex(params, FunctionParameter::isCompulsory) + 1;
            return new IntRange(min, params.size());
        }
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getParams() {
        return params;
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionDeclaration that = (FunctionDeclaration) o;
        return inline == that.inline && noinline == that.noinline
                && name.equals(that.name) && params.equals(that.params) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(inline);
        result = 31 * result + Boolean.hashCode(noinline);
        result = 31 * result + name.hashCode();
        result = 31 * result + params.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FunctionDeclaration{" +
                "inline=" + inline +
                ", noinline=" + noinline +
                ", procedure=" + procedure +
                ", name='" + name + '\'' +
                ", params=" + params +
                ", body=" + body +
                '}';
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.FUNCTION;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BODY;
    }
}
