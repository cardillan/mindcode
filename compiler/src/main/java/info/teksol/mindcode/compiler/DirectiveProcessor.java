package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.Opcode;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
 */
public class DirectiveProcessor {
    private final CompilerProfile profile;

    private DirectiveProcessor(CompilerProfile profile) {
        this.profile = profile;
    }

    public static void processDirectives(Seq program, CompilerProfile profile) {
        DirectiveProcessor processor = new DirectiveProcessor(profile);
        processor.visitNode(program);
    }

    private void visitNode(AstNode node) {
        if (node instanceof Directive) {
            processDirective((Directive) node);
        } else {
            node.getChildren().forEach(this::visitNode);
        }
        ;
    }

    private void processDirective(Directive node) {
        BiConsumer<CompilerProfile, String> handler = OPTION_HANDLERS.get(node.getOption());
        if (handler == null) {
            throw new UnknownCompilerDirectiveException("Unknown compiler directive '" + node.getOption() + "'");
        }

        handler.accept(profile, node.getValue());
    }

    private static void setTarget(CompilerProfile profile, String target) {
        switch (target) {
            case "ML6":
                profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.STANDARD_PROCESSOR);
                break;
            case "ML7":
            case "ML7S":
                profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR);
                break;
            case "ML7W":
                profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);
                break;
            default:
                throw new InvalidCompilerDirectiveException("Invalid value '" + target + "' of compiler directive 'target'");
        }
    }

    private static void setOptimizationLevel(Optimization optimization, CompilerProfile profile, String level) {
        OptimizationLevel optLevel = OptimizationLevel.VALUE_MAP.get(level);
        if (optLevel == null) {
            throw new InvalidCompilerDirectiveException("Invalid value '" + level + "' of compiler directive '" + optimization.getName() + "'");
        }

        profile.setOptimizationLevel(optimization, optLevel);
    }

    private static void setAllOptimizationsLevel(CompilerProfile profile, String level) {
        OptimizationLevel optLevel = OptimizationLevel.VALUE_MAP.get(level);
        if (optLevel == null) {
            throw new InvalidCompilerDirectiveException("Invalid value '" + level + "' of compiler directive 'optimization'");
        }

        profile.setAllOptimizationLevels(optLevel);
    }

    private static final Map<String, BiConsumer<CompilerProfile, String>> OPTION_HANDLERS = createOptionHandlers();

    private static Map<String, BiConsumer<CompilerProfile,String>> createOptionHandlers() {
        Map<String,BiConsumer<CompilerProfile,String>> map = new HashMap<>();
        map.put("target", DirectiveProcessor::setTarget);
        map.put("optimization", DirectiveProcessor::setAllOptimizationsLevel);
        for (Optimization opt : Optimization.values()) {
            map.put(opt.getName(), (profile, level) -> setOptimizationLevel(opt, profile, level));
        }
        return map;
    }
}
