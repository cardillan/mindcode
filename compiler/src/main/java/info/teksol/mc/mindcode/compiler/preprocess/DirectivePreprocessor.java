package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.DirectiveProcessor;
import info.teksol.mc.profile.SyntacticMode;
import info.teksol.mc.profile.options.OptionScope;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

/// Processes compiler directives both global/module and local) in an AST node tree, modifying the given compiler
/// profile accordingly.
@NullMarked
public class DirectivePreprocessor extends AbstractMessageEmitter {
    private final DirectiveProcessor directiveProcessor;
    private CompilerProfile profile;

    private DirectivePreprocessor(PreprocessorContext context, CompilerProfile profile, OptionScope scope) {
        super(context.messageConsumer());
        this.directiveProcessor = context.directiveProcessor();
        this.directiveProcessor.setScopeLimit(scope);
        this.profile = profile;
    }

    public static void processGlobalDirectives(PreprocessorContext context, CompilerProfile profile, AstModule module) {
        if (containsModuleDeclaration(module)) {
            profile.setSyntacticMode(SyntacticMode.STRICT);
        }
        DirectivePreprocessor preprocessor = new DirectivePreprocessor(context, profile, OptionScope.GLOBAL);
        preprocessor.visitNode(module);
        module.setProfile(profile);
    }

    public static void processModuleDirectives(PreprocessorContext context, CompilerProfile globalProfile, AstModule module) {
        boolean isFullModule = containsModuleDeclaration(module);
        CompilerProfile profile = globalProfile.duplicate(!isFullModule);
        if (isFullModule) {
            profile.setSyntacticMode(SyntacticMode.STRICT);
        }
        DirectivePreprocessor preprocessor = new DirectivePreprocessor(context, profile, OptionScope.MODULE);
        preprocessor.visitNode(module);
        module.setProfile(profile);
    }

    private static boolean containsModuleDeclaration(AstMindcodeNode node) {
        return node instanceof AstModuleDeclaration || node.getChildren().stream().anyMatch(DirectivePreprocessor::containsModuleDeclaration);
    }

    private void visitNode(AstMindcodeNode node) {
        if (node instanceof AstDirectiveSet directive) {
            if (!directive.isLocal()) {
                directiveProcessor.processDirective(profile, directive);
            }
        } else {
            node.getChildren().forEach(this::visitNode);
        }
    }

    public static void processLocalDirectives(PreprocessorContext context, CompilerProfile globalProfile, AstProgram program) {
        DirectivePreprocessor preprocessor = new DirectivePreprocessor(context, globalProfile, OptionScope.LOCAL);
        preprocessor.visitNodeLocal(program);
    }

    // A stack of compiler profiles
    Deque<CompilerProfile> profileStack = new ArrayDeque<>();
    private @Nullable AstDirectiveSet lastLocalDirective = null;

    private void visitNodeLocal(AstMindcodeNode node) {
        if (node instanceof AstModule module) {
            // All nodes within a module need to inherit the module setting, not the global setting.
            // This is to handle the syntax mode ("strict" by default for modules).
            profile = node.getProfile();
        }

        if (lastLocalDirective == null) {
            // Skip the push for any node following a local directive
            // The previous profile for this node has already been pushed by the first local directive in a sequence
            profileStack.push(profile);
        }

        if (node instanceof AstDirectiveSet directive && directive.isLocal()) {
            // Don't duplicate otherwise, as consequent local directives merge
            if (lastLocalDirective == null) {
                profile = profile.duplicate(true);
            }

            lastLocalDirective = directive;
            directiveProcessor.processDirective(profile, directive);

            // By returning here, the profile is not restored from the stack, meaning the current
            // settings remain in effect for the next node.
            // We also know directives do not have children to process.
            return;
        }

        lastLocalDirective = null;

        node.setProfile(profile);
        node.getChildren().forEach(this::visitNodeLocal);

        checkUnusedSetLocals();
        profile = profileStack.pop();
    }

    private void checkUnusedSetLocals() {
        if (lastLocalDirective != null) {
            error(lastLocalDirective, ERR.SETLOCAL_NOT_USED);
            profile = profileStack.pop();
            lastLocalDirective = null;
        }
    }
}
