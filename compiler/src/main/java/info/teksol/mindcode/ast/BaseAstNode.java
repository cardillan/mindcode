package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AstContextType;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAstNode implements AstNode {
    private final InputPosition inputPosition;
    private final List<AstNode> children;

    protected BaseAstNode(InputPosition inputPosition) {
        this.inputPosition = inputPosition;
        this.children = List.of();
    }

    protected BaseAstNode(InputPosition inputPosition, AstNode... children) {
        this.inputPosition = inputPosition;
        this.children = safeCopy(children);
    }

    protected BaseAstNode(InputPosition inputPosition, List<? extends AstNode> children) {
        this.inputPosition = inputPosition;
        this.children = safeCopy(children);
    }

    protected BaseAstNode(InputPosition inputPosition, List<? extends AstNode> children1, List<? extends AstNode> children2, AstNode... other) {
        this.inputPosition = inputPosition;
        List<AstNode> tmp = new ArrayList<>(children1);
        tmp.addAll(children2);
        tmp.addAll(Arrays.asList(other));
        this.children = safeCopy(tmp);
    }

    protected BaseAstNode(InputPosition inputPosition, List<? extends AstNode> children, AstNode... other) {
        this.inputPosition = inputPosition;
        List<AstNode> tmp = new ArrayList<>(children);
        tmp.addAll(Arrays.asList(other));
        this.children = safeCopy(tmp);
    }

    public InputPosition getInputPosition() {
        return inputPosition;
    }

    @Override
    public List<AstNode> getChildren() {
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

    private static <E> List<E> safeCopy(List<? extends E> list) {
        return new ArrayList<>(list);
    }

    @SafeVarargs
    private static <E> List<E> safeCopy(E... values) {
        return new ArrayList<>(Arrays.asList(values));
    }
}
