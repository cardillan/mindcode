package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
class ContextMatcher {
    private final AstContext parent;
    private final AstSubcontextType subcontextType;
    private final List<AstContext> contexts;
    int lastMatchIndex = 0;

    public ContextMatcher(List<AstContext> contexts) {
        this.parent = contexts.getFirst().existingParent();
        this.subcontextType = contexts.getFirst().subcontextType();
        this.contexts = contexts;
    }

    public boolean matches(AstContext context) {
        if (context.parent() == parent && context.matches(subcontextType)) return true;

        for (int i = 0; i < contexts.size(); i++) {
            int index = (i + lastMatchIndex) % contexts.size();
            if (context.belongsTo(contexts.get(index))) {
                lastMatchIndex = index;
                return true;
            }
        }
        return false;
    }
}
