package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.generated.ast.visitors.AstDirectiveSetVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstProgram;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.DirectiveProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
@NullMarked
public class DirectivePreprocessor extends AbstractMessageEmitter implements AstDirectiveSetVisitor<@Nullable Void> {
    private final DirectiveProcessor directiveProcessor;
    private final CompilerProfile profile;

    private DirectivePreprocessor(PreprocessorContext context) {
        super(context.messageConsumer());
        this.directiveProcessor = context.directiveProcessor();
        this.profile = context.compilerProfile();
    }

    public static void processDirectives(PreprocessorContext context, AstProgram program) {
        DirectivePreprocessor processor = new DirectivePreprocessor(context);
        processor.visitNode(program);
    }

    private void visitNode(AstMindcodeNode node) {
        if (node instanceof AstDirectiveSet directive) {
            visitDirectiveSet(directive);
        } else {
            node.getChildren().forEach(this::visitNode);
        }
    }

    @Override
    public Void visitDirectiveSet(AstDirectiveSet directive) {
        directiveProcessor.processDirective(profile, directive);
        return null;
    }
}
