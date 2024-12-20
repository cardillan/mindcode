package info.teksol.mindcode.v3.compiler.preprocessor;

import info.teksol.generated.ast.visitors.AstAllocationVisitor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import org.jspecify.annotations.NullMarked;

/**
 * Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
 */
@NullMarked
public class AllocationPreprocessor extends AbstractMessageEmitter implements AstAllocationVisitor<Void> {
    private final PreprocessorContext context;

    public AllocationPreprocessor(PreprocessorContext context) {
        super(context.messageConsumer());
        this.context = context;
    }

    @Override
    public Void visitAllocation(AstAllocation node) {
        switch (node.getType()) {
            case STACK -> {
                if (context.stackAllocation() != null) {
                    error(node, "Multiple stack allocation declarations.");
                } else {
                    context.setStackAllocation(node);
                }
            }
            case HEAP -> {
                if (context.heapAllocation() != null) {
                    error(node, "Multiple heap allocation declarations.");
                } else {
                    context.setHeapAllocation(node);
                }
            }
            default -> throw new MindcodeInternalError("Unknown allocation type: " + node.getType());
        }

        return null;
    }
}
