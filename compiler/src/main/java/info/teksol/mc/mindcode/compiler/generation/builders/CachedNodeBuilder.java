package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstCachedNodeVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstCachedNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
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
