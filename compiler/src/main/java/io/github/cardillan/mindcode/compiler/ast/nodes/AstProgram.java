package io.github.cardillan.mindcode.compiler.ast.nodes;

import io.github.cardillan.annotations.AstNode;
import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstProgram extends AstBaseMindcodeNode {
    private final List<AstModule> modules;

    public AstProgram(CompilerProfile profile, SourcePosition sourcePosition, List<AstModule> modules) {
        super(sourcePosition, children(modules));
        if (modules.isEmpty()) throw new IllegalArgumentException("At least one module must be provided");
        this.modules = List.copyOf(modules);
        setProfile(profile);
    }

    public List<AstModule> getModules() {
        return modules;
    }

    public AstModule getMainModule() {
        return modules.getLast();
    }

    /// @return `true` if this program is a main program, and not a remotely callable module.
    public boolean isMainProgram() {
        return getMainModule().getDeclaration() == null;
    }

    @Override
    public AstNodeScope getScope() {
        return AstNodeScope.GLOBAL;
    }

    @Override
    public AstNodeScope getScopeRestriction() {
        return AstNodeScope.GLOBAL;
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
