package info.teksol.mc.mindcode.compiler.declarations;

import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static info.teksol.mc.mindcode.compiler.generation.builders.DeclarationsBuilder.createLinkedIdentifiersList;

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
        visit(program);
    }

    private void visit(AstMindcodeNode node) {
        // We don't enter a local scope at all, therefore, we only ever process the global scope
        // Do not process code from remote modules
        if (node.getScope() == AstNodeScope.LOCAL || node instanceof AstModule module && !module.getRemoteProcessors().isEmpty()) return;

        if (node instanceof AstParameter parameter) {
            variables.addUnresolvedGlobal(parameter.getParameterName());
        }

        if (node instanceof AstVariablesDeclaration declaration) {
            declaration.getVariables().stream().map(AstVariableSpecification::getName).forEach(variables::addUnresolvedGlobal);

            if (declaration.getModifiers().stream().map(AstVariableModifier::getModifier).anyMatch(LINKED_MODIFIERS::contains)) {
                // Process initial values when not compiling for schematics, or there's no symbolic link declaration
                boolean processInitialValues = !variables.hasSchematicLinks() || declaration.getModifiers().stream().noneMatch(
                        m -> m.getModifier() == Modifier.LINKED && m.getParametrization() != null);
                declaration.getVariables().forEach(specification -> processLinkedVariableSpecification(specification, processInitialValues));
            }
        }

        node.getChildren().forEach(this::visit);
    }

    private void processLinkedVariableSpecification(AstVariableSpecification specification, boolean processInitialValues) {
        if (specification.getExpressions().isEmpty()) {
            variables.addLinkedName(specification.getIdentifier());
        } else if (processInitialValues) {
            for (AstExpression expression : specification.getExpressions()) {
                switch (expression) {
                    case AstIdentifier identifier -> variables.addLinkedName(identifier);
                    case AstRange range -> createLinkedIdentifiersList(CompilerMessageEmitter.DISCARD, range)
                            .forEach(variables::addLinkedName);
                    default -> {}
                }
            }
        }
    }

    private static final Set<Modifier> LINKED_MODIFIERS = Set.of(Modifier.LINKED, Modifier.GUARDED);
}
