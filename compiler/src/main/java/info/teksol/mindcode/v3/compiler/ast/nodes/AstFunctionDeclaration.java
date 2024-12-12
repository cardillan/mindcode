package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.v3.DataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class AstFunctionDeclaration extends AstDeclaration {
    private final AstIdentifier name;
    private final DataType dataType;
    private final List<AstFunctionParameter> parameters;
    private final List<AstMindcodeNode> body;
    private final boolean inline;
    private final boolean noinline;

    public AstFunctionDeclaration(InputPosition inputPosition, @Nullable AstDocComment docComment, AstIdentifier name,
            DataType dataType, List<AstFunctionParameter> parameters, List<AstMindcodeNode> body,
            boolean inline, boolean noinline) {
        super(inputPosition, docComment);
        this.name = name;
        this.dataType = dataType;
        this.parameters = parameters;
        this.body = body;
        this.inline = inline;
        this.noinline = noinline;
    }

    public AstIdentifier getName() {
        return name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public List<AstFunctionParameter> getParameters() {
        return parameters;
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

    public int computeCallSize() {
        // Call size: setting up return address, jump to function, jump back from function, plus passing arguments
        // Note: the function return value is set by the function and not generated at the call site,
        //       therefore it is not counted.
        return 3 + parameters.stream().mapToInt(AstFunctionParameter::callSize).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstFunctionDeclaration that = (AstFunctionDeclaration) o;
        return inline == that.inline && noinline == that.noinline && name.equals(that.name) && dataType == that.dataType
               && parameters.equals(that.parameters) && body.equals(that.body);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dataType.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + Boolean.hashCode(inline);
        result = 31 * result + Boolean.hashCode(noinline);
        return result;
    }

    @Override
    public String toString() {
        return "AstFunctionDeclaration{" +
               "name=" + name +
               ", dataType=" + dataType +
               ", parameters=" + parameters +
               ", body=" + body +
               ", inline=" + inline +
               ", noinline=" + noinline +
               ", docComment=" + docComment +
               '}';
    }
}
