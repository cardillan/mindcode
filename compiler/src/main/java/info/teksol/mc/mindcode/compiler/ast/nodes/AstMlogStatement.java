package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
@AstNode
public class AstMlogStatement extends AstFragment {
    private final @Nullable AstIdentifier label;
    private final @Nullable AstMlogInstruction instruction;
    private final @Nullable AstMlogComment comment;

    public AstMlogStatement(SourcePosition sourcePosition, @Nullable AstIdentifier label,
            @Nullable AstMlogInstruction instruction, @Nullable AstMlogComment comment) {
        super(sourcePosition, children(label, instruction, comment));
        this.label = label;
        this.instruction = instruction;
        this.comment = comment;
    }

    public @Nullable AstIdentifier getLabel() {
        return label;
    }

    public String getLabelName() {
        return Objects.requireNonNull(label).getName();
    }

    public boolean isLabel() {
        return label != null;
    }

    public @Nullable AstMlogInstruction getInstruction() {
        return instruction;
    }

    public boolean isInstruction() {
        return instruction != null;
    }

    public @Nullable AstMlogComment getComment() {
        return comment;
    }

    public String getCommentText() {
        return Objects.requireNonNull(comment).getComment();
    }

    public boolean isComment() {
        return comment != null && instruction == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstMlogStatement that = (AstMlogStatement) o;
        return Objects.equals(label, that.label) && Objects.equals(instruction, that.instruction) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(label);
        result = 31 * result + Objects.hashCode(instruction);
        result = 31 * result + Objects.hashCode(comment);
        return result;
    }
}
