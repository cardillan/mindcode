package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.InputPosition;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.IntRange;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstFunctionDeclaration extends AstDeclaration {
    private final AstIdentifier identifier;
    private final DataType dataType;
    private final List<AstFunctionParameter> parameters;
    private final List<AstMindcodeNode> body;
    private final boolean inline;
    private final boolean noinline;

    public AstFunctionDeclaration(InputPosition inputPosition, @Nullable AstDocComment docComment, AstIdentifier identifier,
            DataType dataType, List<AstFunctionParameter> parameters, List<AstMindcodeNode> body,
            boolean inline, boolean noinline) {
        super(inputPosition, children(list(identifier), parameters, body), docComment);
        this.identifier = identifier;
        this.dataType = dataType;
        this.parameters = parameters;
        this.body = body;
        this.inline = inline;
        this.noinline = noinline;
    }

    public AstIdentifier getIdentifier() {
        return identifier;
    }

    public String getName() {
        return identifier.getName();
    }

    public DataType getDataType() {
        return dataType;
    }

    public List<AstFunctionParameter> getParameters() {
        return parameters;
    }

    public AstFunctionParameter getParameter(int index) {
        return parameters.get(index);
    }

    public List<AstMindcodeNode> getBody() {
        return body;
    }

    public boolean isInline() {
        return inline;
    }

    public boolean isNoinline() {
        return noinline;
    }

    public boolean isVarargs() {
        return !parameters.isEmpty() && parameters.getLast().isVarargs();
    }

    public IntRange getParameterCount() {
        if (parameters.isEmpty()) {
            return new IntRange(0, 0);
        } else if (isVarargs()) {
            int min = CollectionUtils.lastIndexOf(parameters.subList(0, parameters.size() - 1), AstFunctionParameter::isCompulsory) + 1;
            return new IntRange(min, Integer.MAX_VALUE);
        } else {
            int min = CollectionUtils.lastIndexOf(parameters, AstFunctionParameter::isCompulsory) + 1;
            return new IntRange(min, parameters.size());
        }
    }

    public int computeCallSize() {
        // Call size: setting up return address, jump to function, jump back from function, plus passing arguments
        // Note: the function return value is set by the function and not generated at the call site,
        //       therefore it is not counted.
        return 3 + parameters.stream().mapToInt(AstFunctionParameter::callSize).sum();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionDeclaration that = (AstFunctionDeclaration) o;
        return inline == that.inline && noinline == that.noinline && identifier.equals(that.identifier) && dataType == that.dataType
               && parameters.equals(that.parameters) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + Boolean.hashCode(inline);
        result = 31 * result + Boolean.hashCode(noinline);
        return result;
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