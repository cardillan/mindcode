package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
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
        if (node instanceof Directive directive) {
            processDirective(directive);
        } else {
            node.getChildren().forEach(this::visitNode);
        }
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
            case "ML6"  -> profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7", "ML7S" ->
                           profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7W" -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);
            case "ML7A", "ML7AS" ->
                           profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7AW"-> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.WORLD_PROCESSOR);
            default     -> throw new InvalidCompilerDirectiveException("Invalid value '" + target + "' of compiler directive 'target'");
        }
    }

    private static void setOptimizationLevel(Optimization optimization, CompilerProfile profile, String level) {
        OptimizationLevel optLevel = OptimizationLevel.VALUE_MAP.get(level);
        if (optLevel == null) {
            throw new InvalidCompilerDirectiveException("Invalid value '" + level + "' of compiler directive '" + optimization.getOptionName() + "'");
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

    private static void setShortCircuitEval(CompilerProfile compilerProfile, String booleanEval) {
        switch (booleanEval) {
            case "short"    -> compilerProfile.setShortCircuitEval(true);
            case "full"     -> compilerProfile.setShortCircuitEval(false);
            default         ->  throw new InvalidCompilerDirectiveException("Invalid value '" + booleanEval + "' of compiler directive 'booleanEval'");
        }
    }

    private static void setGenerationGoal(CompilerProfile compilerProfile, String goal) {
        switch (goal) {
            case "size"     -> compilerProfile.setGoal(GenerationGoal.SIZE);
            case "speed"    -> compilerProfile.setGoal(GenerationGoal.SPEED);
            case "auto"     -> compilerProfile.setGoal(GenerationGoal.AUTO);
            default         ->  throw new InvalidCompilerDirectiveException("Invalid value '" + goal + "' of compiler directive 'goal'");
        }
    }

    private static final Map<String, BiConsumer<CompilerProfile, String>> OPTION_HANDLERS = createOptionHandlers();

    private static Map<String, BiConsumer<CompilerProfile,String>> createOptionHandlers() {
        Map<String,BiConsumer<CompilerProfile,String>> map = new HashMap<>();
        map.put("target", DirectiveProcessor::setTarget);
        map.put("optimization", DirectiveProcessor::setAllOptimizationsLevel);
        map.put("booleanEval", DirectiveProcessor::setShortCircuitEval);
        map.put("goal", DirectiveProcessor::setGenerationGoal);
        for (Optimization opt : Optimization.values()) {
            map.put(opt.getOptionName(), (profile, level) -> setOptimizationLevel(opt, profile, level));
        }
        return map;
    }
}
