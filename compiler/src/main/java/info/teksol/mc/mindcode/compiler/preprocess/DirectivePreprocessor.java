package info.teksol.mc.mindcode.compiler.preprocess;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.generated.ast.visitors.AstDirectiveSetVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.CompilerMessage;
import info.teksol.mc.messages.ERR;
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

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// Processes compiler directives in an AST node tree, modifying the given compiler profile accordingly.
@NullMarked
public class DirectivePreprocessor extends AbstractMessageEmitter implements AstDirectiveSetVisitor<@Nullable Void> {
    private final CompilerProfile profile;
    private final Map<String, Consumer<AstDirectiveSet>> OPTION_HANDLERS = createOptionHandlers();

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
            visitDirectiveSet(directive);
        } else {
            node.getChildren().forEach(this::visitNode);
        }
    }

    @Override
    public Void visitDirectiveSet(@NonNull AstDirectiveSet directive) {
        String directiveText = directive.getOption().getText();
        Consumer<AstDirectiveSet> handler = OPTION_HANDLERS.get(directiveText);
        if (handler == null) {
            Optional<String> alternative = findBestAlternative(directiveText, OPTION_HANDLERS.keySet());
            if (alternative.isPresent()) {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN_WITH_SUGGESTION, directiveText, alternative.get());
            } else {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN, directiveText);
            }
        } else {
            handler.accept(directive);
        }
        return null;
    }

    private void firstValueError(AstDirectiveSet node, @PrintFormat String format, Object... args) {
        addMessage(CompilerMessage.error(node.getValues().getFirst().sourcePosition(), format, args));
    }

    private boolean validateSingleValue(AstDirectiveSet node) {
        if (node.getValues().isEmpty()) {
            error(node.getOption(), ERR.DIRECTIVE_NO_VALUE, node.getOption().getText());
        } else if (node.getValues().size() > 1) {
            error(node.getOption(), ERR.DIRECTIVE_MULTIPLE_VALUES, node.getOption().getText());
        }

        return !node.getValues().isEmpty();
    }

    private void setProfile(AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                profile.decode(strValue);
                return;
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, ERR.DIRECTIVE_INVALID_VALUE,strValue, node.getOption().getText());
        }
    }

    private void setSortVariables(AstDirectiveSet node) {
        List<SortCategory> sortCategories = new ArrayList<>();
        for (AstDirectiveValue value : node.getValues()) {
            SortCategory sortCategory = SortCategory.byName(value.getText());
            if (sortCategory == null) {
                reportWrongOptionValue(node, value, SortCategory.allowedValues());
            } else {
                sortCategories.add(sortCategory);
            }
        }

        if (sortCategories.isEmpty()) {
            profile.setSortVariables(SortCategory.getAllCategories());
        } else if (sortCategories.equals(List.of(SortCategory.NONE))) {
            profile.setSortVariables(List.of());
        } else {
            profile.setSortVariables(sortCategories);
        }
    }

    private void setTarget(AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            ProcessorEdition edition = ProcessorEdition.byCode(strValue.charAt(strValue.length() - 1));
            ProcessorVersion version = ProcessorVersion.byCode(strValue.substring(
                    strValue.startsWith("ML") ? 2 : 0,
                    strValue.length() - (edition == null ? 0 : 1)));

            if (version != null) {
                profile.setProcessorVersionEdition(version, edition != null ? edition : ProcessorEdition.S);
            } else {
                firstValueError(node, ERR.DIRECTIVE_INVALID_VALUE, strValue, node.getOption().getText());
            }
        }
    }

    private void setIntOption(AstDirectiveSet node, IntConsumer optionSetter, int minValue, int maxValue) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                int value = Integer.parseInt(strValue);
                if (value >= minValue && value <= maxValue) {
                    optionSetter.accept(value);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, ERR.DIRECTIVE_VALUE_OUT_OF_RANGE, strValue, node.getOption().getText(), minValue, maxValue);
        }
    }

    private <E> void setEnumOption(AstDirectiveSet node, Function<String, @Nullable E> convertor,
                Consumer<E> optionSetter, Supplier<Collection<String>> allowedValuesSupplier) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            E value = convertor.apply(strValue);
            if (value != null) {
                optionSetter.accept(value);
            } else {
                reportWrongOptionValue(node, node.getValues().getFirst(), allowedValuesSupplier.get());
            }
        }
    }

    private void setBooleanOption(AstDirectiveSet node, Consumer<Boolean> optionSetter, String trueValue, String falseValue) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            if (strValue.equals(trueValue)) {
                optionSetter.accept(true);
            } else if (strValue.equals(falseValue)) {
                optionSetter.accept(false);
            } else {
                reportWrongOptionValue(node, node.getValues().getFirst(), List.of(trueValue, falseValue));
            }
        }
    }

    private void setBooleanOption(AstDirectiveSet node, Consumer<Boolean> optionSetter) {
        setBooleanOption(node, optionSetter, "true", "false");
    }

    private Map<String, Consumer<AstDirectiveSet>> createOptionHandlers() {
        Map<String, Consumer<AstDirectiveSet>> map = new HashMap<>();

        map.put("auto-printflush",      node -> setBooleanOption(node, profile::setAutoPrintflush));
        map.put("boolean-eval",         node -> setBooleanOption(node, profile::setShortCircuitEval, "short", "full"));
        map.put("boundary-checks",      node -> setEnumOption(node, RuntimeChecks::byName, profile::setBoundaryChecks, RuntimeChecks::allowedValues));
        map.put("function-prefix",      node -> setBooleanOption(node, profile::setShortFunctionPrefix, "short", "long"));
        map.put("goal",                 node -> setEnumOption(node, GenerationGoal::byName, profile::setGoal, GenerationGoal::allowedValues));
        map.put("instruction-limit",    node -> setIntOption(node, profile::setInstructionLimit, 1, CompilerProfile.MAX_INSTRUCTIONS));
        map.put("link-guards",          node -> setBooleanOption(node, profile::setLinkedBlockGuards));
        map.put("optimization",         node -> setEnumOption(node, OptimizationLevel::byName, profile::setAllOptimizationLevels, OptimizationLevel::allowedValues));
        map.put("passes",               node -> setIntOption(node, profile::setOptimizationPasses, 1, CompilerProfile.MAX_PASSES));
        map.put("print-unresolved",     node -> setEnumOption(node, FinalCodeOutput::byName, profile::setFinalCodeOutput, FinalCodeOutput::allowedValues));
        map.put("profile",              this::setProfile);
        map.put("remarks",              node -> setEnumOption(node, Remarks::byName, profile::setRemarks, Remarks::allowedValues));
        map.put("sort-variables",       this::setSortVariables);
        map.put("symbolic-labels",      node ->  setBooleanOption(node, profile::setSymbolicLabels));
        map.put("syntax",               node ->  setEnumOption(node, SyntacticMode::byName, profile::setSyntacticMode, SyntacticMode::allowedValues));
        map.put("target",               this::setTarget);

        for (Optimization optimization : Optimization.LIST) {
            map.put(optimization.getOptionName(), node ->  setEnumOption(node,
                    OptimizationLevel::byName, value -> profile.setOptimizationLevel(optimization,value),
                    OptimizationLevel::allowedValues));
        }

        for (ExecutionFlag flag : ExecutionFlag.values()) {
            if (flag.isSettable()) {
                map.put(flag.getOptionName(), node ->  setBooleanOption(node, value -> profile.setExecutionFlag(flag, value)));
            }
        }

        return map;
    }

    private <E extends Enum<E>> void reportWrongOptionValue(AstDirectiveSet node, AstDirectiveValue value, Collection<String> allowedValues) {
        Optional<String> bestAlternative = findBestAlternative(value.getText(), allowedValues);
        if (bestAlternative.isPresent()) {
            error(value, ERR.DIRECTIVE_INVALID_VALUE_WITH_SUGGESTION, value.getText(),
                    node.getOption().getText(), bestAlternative.get());
        } else {
            error(value, ERR.DIRECTIVE_INVALID_VALUE, value.getText(), node.getOption().getText());
        }
    }

    // Implementation of the Levenshtein Edit Distance
    // See https://rosettacode.org/wiki/Levenshtein_distance#Java
    private int editDistance0(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private double editDistance(String existingValue, String userValue) {
        String[] words = existingValue.split("-");
        return Stream.of(words).mapToInt(s ->editDistance0(s, userValue)).average().orElse(0.0);
    }

    private Optional<String> findBestAlternative(String value, Collection<String> allowedValues) {
        return allowedValues.stream()
                .min(Comparator.comparingDouble(a -> editDistance(a, value)));
    }

    private Optional<String> findBestAlternative(String value, String[] allowedValues) {
        return Stream.of(allowedValues)
                .min(Comparator.comparingDouble(a -> editDistance(a, value)));
    }
}
