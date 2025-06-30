package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.AstNodeToString;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@NullMarked
public abstract class AstBaseMindcodeNode implements AstMindcodeNode {
    private final SourcePosition sourcePosition;
    private final List<AstMindcodeNode> children;
    protected final @Nullable AstDocComment docComment;

    protected AstBaseMindcodeNode(SourcePosition sourcePosition) {
        this.sourcePosition = sourcePosition;
        this.children = List.of();
        this.docComment = null;
    }

    protected AstBaseMindcodeNode(SourcePosition sourcePosition, List<AstMindcodeNode> children) {
        this.sourcePosition = sourcePosition;
        this.children = List.copyOf(children);
        this.docComment = null;
    }

    protected AstBaseMindcodeNode(SourcePosition sourcePosition, List<AstMindcodeNode> children,
            @Nullable AstDocComment docComment) {
        this.sourcePosition = sourcePosition;
        this.children = List.copyOf(children);
        this.docComment = docComment;
    }

    protected static List<AstMindcodeNode> children(Stream<? extends @Nullable AstMindcodeNode> nodes) {
        return nodes.filter(Objects::nonNull).<@NonNull AstMindcodeNode>map(Objects::requireNonNull).toList();
    }

    protected static List<AstMindcodeNode> children(@Nullable AstMindcodeNode... nodes) {
        return children(Stream.of(nodes));
    }

    @SafeVarargs
    protected static List<AstMindcodeNode> children(List<? extends @Nullable AstMindcodeNode>... lists) {
        return children(Stream.of(lists).flatMap(List::stream));
    }

    protected static List<@Nullable AstMindcodeNode> list(@Nullable AstMindcodeNode... nodes) {
        return Arrays.asList(nodes);
    }

    @Override
    public CompilerProfile getProfile() {
        return null;
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public @Nullable AstDocComment getDocComment() {
        return docComment;
    }

    @Override
    public List<AstMindcodeNode> getChildren() {
        return children;
    }

    @Override
    public AstContextType getContextType() {
        return AstContextType.NONE;
    }

    @Override
    public AstSubcontextType getSubcontextType() {
        return AstSubcontextType.BASIC;
    }

    // The method is made final. toString for all classes in the hierarchy is implemented using automatically
    // generated code.
    @Override
    public final String toString() {
        return AstNodeToString.INSTANCE.visit(this);
    }
}
