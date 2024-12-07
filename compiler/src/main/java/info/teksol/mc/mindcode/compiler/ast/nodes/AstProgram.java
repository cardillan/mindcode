package info.teksol.mc.mindcode.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstProgram extends AstBaseMindcodeNode {
    private final List<AstModule> modules;

    public AstProgram(SourcePosition sourcePosition, List<AstModule> modules) {
        super(sourcePosition, children(modules));
        this.modules = List.copyOf(modules);
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.GLOBAL;
    }

    public List<AstModule> getModules() {
        return modules;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AstProgram that = (AstProgram) o;
        return modules.equals(that.modules);
    }

    @Override
    public int hashCode() {
        return modules.hashCode();
    }
}
