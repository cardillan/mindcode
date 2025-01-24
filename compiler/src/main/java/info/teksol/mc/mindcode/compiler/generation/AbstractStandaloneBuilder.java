package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;

/// A base for a standalone builder class. A new instance is created for each processed node. The class
/// provides all the methods the AbstractBuilder class does, but allows to store state inside the instance.
@NullMarked
public abstract class AbstractStandaloneBuilder<T extends AstMindcodeNode> extends AbstractBuilder {
    protected final T node;

    public AbstractStandaloneBuilder(AbstractBuilder builder, T node) {
        super(builder);
        this.node = node;
    }
}
