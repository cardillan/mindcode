package info.teksol.mindcode.v3;

import info.teksol.mindcode.v3.compiler.ast.AstBuilderContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CompilerContext implements AstBuilderContext {
    private final @NotNull List<AstRequire> requirements = new ArrayList<>();


    @Override
    public void addRequirement(AstRequire requirement) {

    }
}
