package info.teksol.mindcode.v3;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.v3.compiler.ast.AstBuilderContext;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRequire;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class CompilerContext implements AstBuilderContext {
    private static final ThreadLocal<CompilerContext> instance = new ThreadLocal<>();
    private final MessageConsumer messageConsumer;
    private final CompilerProfile compilerProfile;
    private final List<AstRequire> requirements = new ArrayList<>();

    private CompilerContext(MessageConsumer messageConsumer, CompilerProfile compilerProfile) {
        this.messageConsumer = messageConsumer;
        this.compilerProfile = compilerProfile;
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    public CompilerProfile getCompilerProfile() {
        return compilerProfile;
    }

    public List<AstRequire> getRequirements() {
        return requirements;
    }

    @Override
    public void addRequirement(AstRequire requirement) {
        requirements.add(requirement);
    }

    public static AstBuilderContext getAstBuilderContext() {
        return Objects.requireNonNull(instance.get());
    }

    static CompilerContext initialize(MessageConsumer messageConsumer, CompilerProfile compilerProfile) {
        CompilerContext context = new CompilerContext(messageConsumer, compilerProfile);
        instance.set(context);
        return context;
    }

    static CompilerContext getInstance() {
        return Objects.requireNonNull(instance.get());
    }
}
