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
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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

    public static void processLocalDirectives(PreprocessorContext context, CompilerProfile globalProfile, AstMindcodeNode node) {
        DirectivePreprocessor preprocessor = new DirectivePreprocessor(context, globalProfile, OptionScope.LOCAL);
        if (node instanceof AstFunctionDeclaration declaration) {
            declaration.getDirectives().forEach(preprocessor::visitNodeLocal);
        }
        preprocessor.visitNodeLocal(node);
    }

    // A stack of compiler profiles
    private final Deque<CompilerProfile> profileStack = new ArrayDeque<>();
    private final List<AstDirectiveSet> lastLocalDirectives = new ArrayList<>();
    private @Nullable AstDocComment lastDocComment = null;

    private void visitNodeLocal(AstMindcodeNode node) {
        if (node instanceof AstModule module) {
            // All nodes within a module need to inherit the module setting, not the global setting.
            // This is to handle the syntax mode ("strict" by default for modules).
            profile = node.getProfile();
        }

        if (lastLocalDirectives.isEmpty()) {
            // Skip the push for any node following a local directive
            // The previous profile for this node has already been pushed by the first local directive in a sequence
            profileStack.push(profile);
        }

        if (node instanceof AstDirectiveSet directive && directive.isLocal()) {
            // Don't duplicate otherwise, as consequent local directives merge
            if (lastLocalDirectives.isEmpty()) {
                profile = profile.duplicate(true);
            }

            lastLocalDirectives.add(directive);
            if (directive.getDocComment() != null) {
                lastDocComment = directive.getDocComment();
            }

            directiveProcessor.processDirective(profile, directive);

            // By returning here, the profile is not restored from the stack, meaning the current
            // settings remain in effect for the next node.
            // We also know directives do not have children to process.
            return;
        }

        if (node instanceof AstFunctionDeclaration function) {
            function.addDirectives(lastLocalDirectives);
        }
        lastLocalDirectives.clear();

        if (lastDocComment != null) {
            node.setDocComment(lastDocComment);
            lastDocComment = null;
        }

        node.setProfile(profile);
        node.getChildren().forEach(this::visitNodeLocal);

        checkUnusedSetLocals();
        profile = profileStack.pop();
    }

    private void checkUnusedSetLocals() {
        if (!lastLocalDirectives.isEmpty()) {
            lastLocalDirectives.forEach(directive -> error(directive, ERR.SETLOCAL_NOT_USED));
            profile = profileStack.pop();
            lastLocalDirectives.clear();
        }
    }
}
