package info.teksol.mindcode.compiler;

import info.teksol.emulator.processor.ExecutionFlag;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.Directive;
import info.teksol.mindcode.ast.DirectiveText;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mindcode.logic.ProcessorEdition;
import info.teksol.mindcode.logic.ProcessorVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
 */
public class DirectiveProcessor extends AbstractMessageEmitter {
    private final CompilerProfile profile;

    private DirectiveProcessor(CompilerProfile profile, Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        this.profile = profile;
    }

    public static void processDirectives(Seq program, CompilerProfile profile, Consumer<MindcodeMessage> messageConsumer) {
        DirectiveProcessor processor = new DirectiveProcessor(profile, messageConsumer);
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
        BiConsumer<CompilerProfile, Directive> handler = OPTION_HANDLERS.get(node.getOption().getText());
        if (handler == null) {
            error(node.getOption(), "Unknown compiler directive '%s'.", node.getOption().getText());
        } else {
            handler.accept(profile, node);
        }
    }

    private void setTarget(CompilerProfile profile, Directive node) {
        String target = node.getValue().getText().toUpperCase();

        if (target.startsWith("ML")) {
            ProcessorEdition edition = ProcessorEdition.byCode(target.charAt(target.length() - 1));
            ProcessorVersion version = ProcessorVersion.byCode(target.substring(2, target.length() - (edition == null ? 0 : 1)));

            if (version != null) {
                profile.setProcessorVersionEdition(version, edition != null ? edition : ProcessorEdition.S);
                return;
            }
        }

        error(node.getValue(), "Invalid value '%s' of compiler directive 'target'.", node.getValue().getText());
    }

    private void setOptimizationLevel(Optimization optimization, CompilerProfile profile, Directive node) {
        OptimizationLevel optLevel = OptimizationLevel.byName(node.getValue().getText());
        if (optLevel == null) {
            error(node.getValue(), "Invalid value '%s' of compiler directive '%s'.", node.getValue().getText(), optimization.getOptionName());
        } else {
            profile.setOptimizationLevel(optimization, optLevel);
        }
    }

    private void setExecutionFlag(ExecutionFlag flag, CompilerProfile profile, Directive node) {
        Boolean value =
                "false".equals(node.getValue().getText()) ? Boolean.FALSE :
                "true".equals(node.getValue().getText()) ? Boolean.TRUE : null;
        if (value == null) {
            error(node.getValue(), "Invalid value '%s' of compiler directive '%s'.", node.getValue().getText(), flag.getOptionName());
        } else {
            profile.setExecutionFlag(flag, value);
        }
    }

    private void setAllOptimizationsLevel(CompilerProfile profile, Directive node) {
        OptimizationLevel optLevel = OptimizationLevel.byName(node.getValue().getText());
        if (optLevel == null) {
            error(node.getValue(), "Invalid value '%s' of compiler directive 'optimization'.", node.getValue().getText());
        } else {
            profile.setAllOptimizationLevels(optLevel);
        }
    }

    private void setShortCircuitEval(CompilerProfile compilerProfile, Directive node) {
        switch (node.getValue().getText()) {
            case "short"    -> compilerProfile.setShortCircuitEval(true);
            case "full"     -> compilerProfile.setShortCircuitEval(false);
            default         ->  error(node.getValue(), "Invalid value '%s' of compiler directive 'booleanEval'.", node.getValue().getText());
        }
    }

    private void setInstructionLimit(CompilerProfile compilerProfile, Directive node) {
        try {
            int limit = Integer.parseInt(node.getValue().getText());
            if (limit >= 1 && limit <= CompilerProfile.MAX_INSTRUCTIONS) {
                compilerProfile.setInstructionLimit(limit);
                return;
            }
        } catch (NumberFormatException ex) {
            // Do nothing
        }
        error(node.getValue(), "Invalid value '%s' of compiler directive 'instruction-limit' (expected integer between 1 and  %d).",
                node.getValue().getText(), CompilerProfile.MAX_INSTRUCTIONS);
    }

    private void setOptimizationPasses(CompilerProfile compilerProfile, Directive node) {
        String strPasses = node.getValue().getText();
        try {
            int passes = Integer.parseInt(node.getValue().getText());
            if (passes >= 1 && passes <= CompilerProfile.MAX_PASSES) {
                compilerProfile.setOptimizationPasses(passes);
                return;
            }
        } catch (NumberFormatException ex) {
            // Do nothing
        }
        error(node.getValue(), "Invalid value '%s' of compiler directive 'passes' (expected integer between 1 and  %d).",
                node.getValue().getText(), CompilerProfile.MAX_PASSES);
    }

    private void setGenerationGoal(CompilerProfile compilerProfile, Directive node) {
        for (GenerationGoal goal : GenerationGoal.values()) {
            if (goal.name().equalsIgnoreCase(node.getValue().getText())) {
                compilerProfile.setGoal(goal);
                return;
            }
        }
        error(node.getValue(), "Invalid value '%s' of compiler directive 'goal'.", node.getValue().getText());
    }

    private void setRemarks(CompilerProfile compilerProfile, Directive node) {
        for (Remarks value : Remarks.values()) {
            if (value.name().equalsIgnoreCase(node.getValue().getText())) {
                compilerProfile.setRemarks(value);
                return;
            }
        }
        error(node.getValue(), "Invalid value '%s' of compiler directive 'remarks'.", node.getValue().getText());
    }

    private void setMemoryModel(CompilerProfile compilerProfile, Directive node) {
        for (MemoryModel memoryModel : MemoryModel.values()) {
            if (memoryModel.name().equalsIgnoreCase(node.getValue().getText())) {
                compilerProfile.setMemoryModel(memoryModel);
                return;
            }
        }
        error(node.getValue(), "Invalid value '%s' of compiler directive 'memory-model'.", node.getValue().getText());
    }

    private void setSortVariables(CompilerProfile compilerProfile, Directive node) {
        List<SortCategory> sortCategories = new ArrayList<>();
        for (DirectiveText value : node.getValues()) {
            SortCategory sortCategory = SortCategory.byName(value.getText());
            if (sortCategory == null) {
                error(value, "Invalid value '%s' of compiler directive 'sort-variables'.", value.getText());
            } else {
                sortCategories.add(sortCategory);
            }
        }

        if (sortCategories.isEmpty()) {
            compilerProfile.setSortVariables(SortCategory.getAllCategories());
        } else if (sortCategories.equals(List.of(SortCategory.NONE))) {
            compilerProfile.setSortVariables(List.of());
        } else {
            compilerProfile.setSortVariables(sortCategories);
        }
    }

    private final Map<String, BiConsumer<CompilerProfile, Directive>> OPTION_HANDLERS = createOptionHandlers();

    private Map<String, BiConsumer<CompilerProfile,Directive>> createOptionHandlers() {
        Map<String,BiConsumer<CompilerProfile,Directive>> map = new HashMap<>();
        map.put("target", this::setTarget);
        map.put("optimization", this::setAllOptimizationsLevel);
        map.put("boolean-eval", this::setShortCircuitEval);
        map.put("instruction-limit", this::setInstructionLimit);
        map.put("passes", this::setOptimizationPasses);
        map.put("goal", this::setGenerationGoal);
        map.put("remarks", this::setRemarks);
        map.put("memory-model", this::setMemoryModel);
        map.put("sort-variables", this::setSortVariables);
        for (Optimization opt : Optimization.LIST) {
            map.put(opt.getOptionName(), (profile, level) -> setOptimizationLevel(opt, profile, level));
        }
        for (ExecutionFlag flag : ExecutionFlag.values()) {
            if (flag.isSettable()) {
                map.put(flag.getOptionName(), (profile, level) -> setExecutionFlag(flag, profile, level));
            }
        }

        return map;
    }
}
