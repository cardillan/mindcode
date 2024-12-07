package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.generated.ast.visitors.AstDirectiveSetVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstProgram;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.*;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/// Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
@NullMarked
public class DirectivePreprocessor extends AbstractMessageEmitter implements AstDirectiveSetVisitor<@Nullable Void> {
    private final CompilerProfile profile;
    private final Map<String, BiConsumer<CompilerProfile, AstDirectiveSet>> OPTION_HANDLERS = createOptionHandlers();

    private DirectivePreprocessor(PreprocessorContext context) {
        super(context.messageConsumer());
        this.profile = context.compilerProfile();
    }

    public static void processDirectives(PreprocessorContext context, AstProgram program) {
        DirectivePreprocessor processor = new DirectivePreprocessor(context);
        processor.visitNode(program);
    }

    private void visitNode(AstMindcodeNode node) {
        if (node instanceof AstDirectiveSet directive) {
            processDirective(directive);
        } else {
            node.getChildren().forEach(this::visitNode);
        }
    }

    private void processDirective(AstDirectiveSet directive) {
        BiConsumer<CompilerProfile, AstDirectiveSet> handler = OPTION_HANDLERS.get(directive.getOption().getText());
        if (handler == null) {
            error(directive.getOption(), "Unknown compiler directive '%s'.", directive.getOption().getText());
        } else {
            handler.accept(profile, directive);
        }
    }

    @Override
    public Void visitDirectiveSet(@NonNull AstDirectiveSet directive) {
        BiConsumer<CompilerProfile, AstDirectiveSet> handler = OPTION_HANDLERS.get(directive.getOption().getText());
        if (handler == null) {
            error(directive.getOption(), "Unknown compiler directive '%s'.", directive.getOption().getText());
        } else {
            handler.accept(profile, directive);
        }
        return null;
    }

    private void firstValueError(AstDirectiveSet node, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(node.getValues().getFirst().sourcePosition(), format, args));
    }

    private boolean validateSingleValue(AstDirectiveSet node) {
        if (node.getValues().isEmpty()) {
            error(node.getOption(), "No value specified for option '%s'.", node.getOption().getText());
        } else if (node.getValues().size() > 1) {
            error(node.getOption(), "Multiple values specified for option '%s'.", node.getOption().getText());
        }

        return !node.getValues().isEmpty();
    }

    private void setAllOptimizationsLevel(CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            OptimizationLevel optLevel = OptimizationLevel.byName(strValue);
            if (optLevel != null) {
                profile.setAllOptimizationLevels(optLevel);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setExecutionFlag(ExecutionFlag flag, CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            switch (strValue) {
                case "true"  -> profile.setExecutionFlag(flag, true);
                case "false" -> profile.setExecutionFlag(flag, false);
                default      -> firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setGenerationGoal(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            GenerationGoal goal = GenerationGoal.byName(strValue);
            if (goal != null) {
                profile.setGoal(goal);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setInstructionLimit(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                int limit = Integer.parseInt(strValue);
                if (limit >= 1 && limit <= CompilerProfile.MAX_INSTRUCTIONS) {
                    compilerProfile.setInstructionLimit(limit);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, "Invalid value '%s' of compiler directive '%s' (expected integer between 1 and  %d).",
                    strValue, node.getOption().getText(), CompilerProfile.MAX_INSTRUCTIONS);
        }
    }

    private void setMemoryModel(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            MemoryModel memoryModel = MemoryModel.byName(strValue);
            if (memoryModel != null) {
                profile.setMemoryModel(memoryModel);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setLinkGuards(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            switch (strValue) {
                case "true"  -> profile.setLinkedBlockGuards(true);
                case "false" -> profile.setLinkedBlockGuards(false);
                default      -> firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setOptimizationLevel(Optimization optimization, CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String level = node.getValues().getFirst().getText();
            OptimizationLevel optLevel = OptimizationLevel.byName(level);
            if (optLevel != null) {
                profile.setOptimizationLevel(optimization, optLevel);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", level, node.getOption().getText());
            }
        }
    }

    private void setOptimizationPasses(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                int passes = Integer.parseInt(strValue);
                if (passes >= 1 && passes <= CompilerProfile.MAX_PASSES) {
                    compilerProfile.setOptimizationPasses(passes);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, "Invalid value '%s' of compiler directive '%s' (expected integer between 1 and  %d).",
                    strValue, node.getOption().getText(), CompilerProfile.MAX_PASSES);
        }
    }

    private void setProfile(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                compilerProfile.decode(strValue);
                return;
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, "Invalid value '%s' of compiler directive '%s'.",strValue, node.getOption().getText());
        }
    }

    private void setRemarks(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            Remarks remarks = Remarks.byName(strValue);
            if (remarks != null) {
                profile.setRemarks(remarks);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setShortCircuitEval(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            switch (strValue) {
                case "short" -> compilerProfile.setShortCircuitEval(true);
                case "full"  -> compilerProfile.setShortCircuitEval(false);
                default      -> firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setSortVariables(CompilerProfile compilerProfile, AstDirectiveSet node) {
        List<SortCategory> sortCategories = new ArrayList<>();
        for (AstDirectiveValue value : node.getValues()) {
            SortCategory sortCategory = SortCategory.byName(value.getText());
            if (sortCategory == null) {
                error(value, "Invalid value '%s' of compiler directive '%s'.", value.getText(), node.getOption().getText());
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

    private void setSyntax(CompilerProfile compilerProfile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            SyntacticMode syntacticMode = SyntacticMode.byName(strValue);
            if (syntacticMode != null) {
                profile.setSyntacticMode(syntacticMode);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private void setTarget(CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            ProcessorEdition edition = ProcessorEdition.byCode(strValue.charAt(strValue.length() - 1));
            ProcessorVersion version = ProcessorVersion.byCode(strValue.substring(
                    strValue.startsWith("ML") ? 2 : 0,
                    strValue.length() - (edition == null ? 0 : 1)));

            if (version != null) {
                profile.setProcessorVersionEdition(version, edition != null ? edition : ProcessorEdition.S);
            } else {
                firstValueError(node,"Invalid value '%s' of compiler directive '%s'.", strValue, node.getOption().getText());
            }
        }
    }

    private Map<String, BiConsumer<CompilerProfile, AstDirectiveSet>> createOptionHandlers() {
        Map<String,BiConsumer<CompilerProfile, AstDirectiveSet>> map = new HashMap<>();
        map.put("boolean-eval", this::setShortCircuitEval);
        map.put("goal", this::setGenerationGoal);
        map.put("instruction-limit", this::setInstructionLimit);
        map.put("memory-model", this::setMemoryModel);
        map.put("link-guards", this::setLinkGuards);
        map.put("optimization", this::setAllOptimizationsLevel);
        map.put("passes", this::setOptimizationPasses);
        map.put("profile", this::setProfile);
        map.put("remarks", this::setRemarks);
        map.put("sort-variables", this::setSortVariables);
        map.put("syntax", this::setSyntax);
        map.put("target", this::setTarget);
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
