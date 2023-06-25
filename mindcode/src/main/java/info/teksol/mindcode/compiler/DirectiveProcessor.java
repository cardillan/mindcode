package info.teksol.mindcode.compiler;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;
import org.intellij.lang.annotations.PrintFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
 */
public class DirectiveProcessor {
    private final CompilerProfile profile;
    private final Consumer<CompilerMessage> messageConsumer;

    private DirectiveProcessor(CompilerProfile profile, Consumer<CompilerMessage> messageConsumer) {
        this.profile = profile;
        this.messageConsumer = messageConsumer;
    }

    public static void processDirectives(Seq program, CompilerProfile profile, Consumer<CompilerMessage> messageConsumer) {
        DirectiveProcessor processor = new DirectiveProcessor(profile, messageConsumer);
        processor.visitNode(program);
    }

    private void error(@PrintFormat String format, Object... args) {
        messageConsumer.accept(MindcodeMessage.error(format.formatted(args)));
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
            error("Unknown compiler directive '%s'.", node.getOption());
        } else {
            handler.accept(profile, node.getValue());
        }
    }

    private void setTarget(CompilerProfile profile, String target) {
        switch (target) {
            case "ML6"  -> profile.setProcessorVersionEdition(ProcessorVersion.V6, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7", "ML7S" ->
                           profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7W" -> profile.setProcessorVersionEdition(ProcessorVersion.V7, ProcessorEdition.WORLD_PROCESSOR);
            case "ML7A", "ML7AS" ->
                           profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.STANDARD_PROCESSOR);
            case "ML7AW"-> profile.setProcessorVersionEdition(ProcessorVersion.V7A, ProcessorEdition.WORLD_PROCESSOR);
            default     -> error("Invalid value '%s' of compiler directive 'target'.", target);
        }
    }

    private void setOptimizationLevel(Optimization optimization, CompilerProfile profile, String level) {
        OptimizationLevel optLevel = OptimizationLevel.VALUE_MAP.get(level);
        if (optLevel == null) {
            error("Invalid value '%s' of compiler directive '%s'.", level, optimization.getOptionName());
        } else {
            profile.setOptimizationLevel(optimization, optLevel);
        }
    }

    private void setAllOptimizationsLevel(CompilerProfile profile, String level) {
        OptimizationLevel optLevel = OptimizationLevel.VALUE_MAP.get(level);
        if (optLevel == null) {
            error("Invalid value '%s' of compiler directive 'optimization'.", level);
        } else {
            profile.setAllOptimizationLevels(optLevel);
        }
    }

    private void setShortCircuitEval(CompilerProfile compilerProfile, String booleanEval) {
        switch (booleanEval) {
            case "short"    -> compilerProfile.setShortCircuitEval(true);
            case "full"     -> compilerProfile.setShortCircuitEval(false);
            default         ->  error("Invalid value '%s' of compiler directive 'booleanEval'.", booleanEval);
        }
    }

    private void setOptimizationPasses(CompilerProfile compilerProfile, String strPasses) {
        try {
            int passes = Integer.parseInt(strPasses);
            if (passes >= 1 && passes <= CompilerProfile.MAX_PASSES) {
                compilerProfile.setOptimizationPasses(passes);
                return;
            }
        } catch (NumberFormatException ex) {
            // Do nothing
        }
        error("Invalid value '%s' of compiler directive 'passes' (expected integer between 1 and  " + CompilerProfile.MAX_PASSES + ").", strPasses);
    }

    private void setGenerationGoal(CompilerProfile compilerProfile, String strGoal) {
        for (GenerationGoal goal : GenerationGoal.values()) {
            if (goal.name().equalsIgnoreCase(strGoal)) {
                compilerProfile.setGoal(goal);
                return;
            }
        }
        error("Invalid value '%s' of compiler directive 'goal'.", strGoal);
    }

    private void setMemoryModel(CompilerProfile compilerProfile, String strModel) {
        for (MemoryModel memoryModel : MemoryModel.values()) {
            if (memoryModel.name().equalsIgnoreCase(strModel)) {
                compilerProfile.setMemoryModel(memoryModel);
                return;
            }
        }
        error("Invalid value '%s' of compiler directive 'memory-model'.", strModel);
    }

    private final Map<String, BiConsumer<CompilerProfile, String>> OPTION_HANDLERS = createOptionHandlers();

    private Map<String, BiConsumer<CompilerProfile,String>> createOptionHandlers() {
        Map<String,BiConsumer<CompilerProfile,String>> map = new HashMap<>();
        map.put("target", this::setTarget);
        map.put("optimization", this::setAllOptimizationsLevel);
        map.put("booleanEval", this::setShortCircuitEval);
        map.put("passes", this::setOptimizationPasses);
        map.put("goal", this::setGenerationGoal);
        map.put("memory-model", this::setMemoryModel);
        for (Optimization opt : Optimization.values()) {
            map.put(opt.getOptionName(), (profile, level) -> setOptimizationLevel(opt, profile, level));
        }
        return map;
    }
}
