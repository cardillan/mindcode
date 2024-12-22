package info.teksol.mindcode.v3.compiler.ast.nodes;

import info.teksol.annotations.AstNode;
import info.teksol.mindcode.InputPosition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
@AstNode
public class AstProgram extends AstBaseMindcodeNode {
    private final List<AstModule> modules;

    public AstProgram(InputPosition inputPosition, List<AstModule> modules) {
        super(inputPosition, children(modules));
        this.modules = List.copyOf(modules);
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
