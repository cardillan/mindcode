package info.teksol.mc.mindcode.compiler.declarations;

import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class DeclarationsProcessor extends CompilerMessageEmitter {
    private final AstProgram program;
    private final Variables variables;

    public static void processDeclarations(DeclarationsProcessorContext context, AstProgram program) {
        new DeclarationsProcessor(context, program).processDeclarations();
    }

    private DeclarationsProcessor(DeclarationsProcessorContext context, AstProgram program) {
        super(context.messageConsumer());
        this.program = program;
        this.variables = context.variables();
    }

    private void processDeclarations() {
        visitNode(program);
    }

    private void visitNode(AstMindcodeNode nodeToVisit) {
        // We don't enter a local scope at all, therefore, we only ever process the global scope
        if (nodeToVisit.getScope() == AstNodeScope.LOCAL) {
            return;
        }

        if (nodeToVisit instanceof AstVariablesDeclaration declaration
                && declaration.getModifiers().stream().map(AstVariableModifier::getModifier).anyMatch(LINKED_MODIFIERS::contains)) {
            declaration.getVariables().forEach(this::processVariableSpecification);
        }

        nodeToVisit.getChildren().forEach(this::visitNode);
    }

    private void processVariableSpecification(AstVariableSpecification specification) {
        if (specification.getExpressions().isEmpty()) {
            variables.addLinkedName(specification.getName());
        } else {
            specification.getExpressions().stream()
                    .filter(AstIdentifier.class::isInstance)
                    .map(AstIdentifier.class::cast)
                    .forEach(identifier -> variables.addLinkedName(identifier.getName()));
        }
    }

    private static final Set<Modifier> LINKED_MODIFIERS = Set.of(Modifier.LINKED, Modifier.GUARDED);
}
