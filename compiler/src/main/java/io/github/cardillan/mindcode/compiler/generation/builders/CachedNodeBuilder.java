package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.ast.nodes.AstCachedNode;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstMindcodeNode;
import io.github.cardillan.mindcode.compiler.generation.AbstractCodeBuilder;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.generated.ast.visitors.AstCachedNodeVisitor;
import org.jspecify.annotations.NullMarked;

import java.util.IdentityHashMap;
import java.util.Map;

@NullMarked
public class CachedNodeBuilder extends AbstractCodeBuilder implements AstCachedNodeVisitor<ValueStore> {
    private final Map<AstMindcodeNode, ValueStore> nodeCache = new IdentityHashMap<>();

    public CachedNodeBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitCachedNode(AstCachedNode node) {
        ValueStore previous = nodeCache.get(node);
        if (previous == null) {
            ValueStore eval = evaluate(node.getExpression());
            nodeCache.put(node, eval);
            return eval;
        } else {
            return previous;
        }
    }
}
