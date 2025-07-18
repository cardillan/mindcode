package info.teksol.mc.profile;

import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorEdition;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.util.StringSimilarity;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

@NullMarked
public class DirectiveProcessor extends AbstractMessageEmitter {
    private DirectiveScope scope = DirectiveScope.GLOBAL;

    private interface OptionHandler {
        void handle(CompilerProfile profile, AstDirectiveSet directive);
    }

    private final Map<String, OptionHandler> OPTION_HANDLERS = createOptionHandlers();

    public DirectiveProcessor(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void setScope(DirectiveScope scope) {
        this.scope = scope;
    }

    public void processDirective(CompilerProfile profile, AstDirectiveSet directive) {
        DirectiveScope previousScope = scope;
        if (directive.isLocal()) scope = DirectiveScope.LOCAL;

        String directiveText = directive.getOption().getText();
        OptionHandler handler = OPTION_HANDLERS.get(directiveText);
        if (handler == null) {
            Optional<String> alternative = StringSimilarity.findBestAlternative(directiveText, OPTION_HANDLERS.keySet());
            if (alternative.isPresent()) {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN_WITH_SUGGESTION, directiveText, alternative.get());
            } else {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN, directiveText);
            }
        } else {
            handler.handle(profile, directive);
        }

        scope = previousScope;
    }
        
    private void firstValueError(AstDirectiveSet node, @PrintFormat String format, Object... args) {
        error(node.getValues().getFirst().sourcePosition(), format, args);
    }

    private boolean validateSingleValue(AstDirectiveSet node) {
        if (node.getValues().isEmpty()) {
            error(node.getOption(), ERR.DIRECTIVE_NO_VALUE, node.getOption().getText());
        } else if (node.getValues().size() > 1) {
            error(node.getOption(), ERR.DIRECTIVE_MULTIPLE_VALUES, node.getOption().getText());
        }

        return !node.getValues().isEmpty();
    }

    private void setProfile(CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            String strValue = node.getValues().getFirst().getText();
            try {
                profile.decode(strValue);
                return;
            } catch (NumberFormatException ignored) {
            }
            firstValueError(node, ERR.DIRECTIVE_INVALID_VALUE, strValue, node.getOption().getText());
        }
    }

    private void setSortVariables(CompilerProfile profile, AstDirectiveSet node) {
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

    private void setSyntax(CompilerProfile profile, AstDirectiveSet node) {
        if (validateSingleValue(node)) {
            SyntacticMode syntacticMode = SyntacticMode.byName(node.getValues().getFirst().getText());
            if (syntacticMode == null) {
                reportWrongOptionValue(node, node.getValues().getFirst(), SyntacticMode.allowedValues());
            } else if (scope == DirectiveScope.MODULE && syntacticMode != SyntacticMode.STRICT) {
                error(node.getValues().getFirst(), ERR.DIRECTIVE_INVALID_SYNTAX_FOR_MODULE);
            } else {
                profile.setSyntacticMode(syntacticMode);
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
                error(node.getValues().getFirst(), ERR.DIRECTIVE_INVALID_VALUE_WITH_VALUES,
                        strValue, node.getOption().getText(), trueValue, falseValue);
            }
        }
    }

    private void setBooleanOption(AstDirectiveSet node, Consumer<Boolean> optionSetter) {
        setBooleanOption(node, optionSetter, "true", "false");
    }

    private Map<String, OptionHandler> createOptionHandlers() {
        Map<String, OptionHandler> map = new HashMap<>();

        map.put("auto-printflush",              (profile, node) -> setBooleanOption(node, profile::setAutoPrintflush));
        map.put("boolean-eval",                 (profile, node) -> setBooleanOption(node, profile::setShortCircuitEval, "short", "full"));
        map.put("boundary-checks",              (profile, node) -> setEnumOption(node, RuntimeChecks::byName, profile::setBoundaryChecks, RuntimeChecks::allowedValues));
        map.put("builtin-evaluation",           (profile, node) -> setEnumOption(node, BuiltinEvaluation::byName, profile::setBuiltinEvaluation, BuiltinEvaluation::allowedValues));
        map.put("case-configuration",           (profile, node) -> setIntOption(node, profile::setCaseConfiguration, 0, Integer.MAX_VALUE));
        map.put("case-optimization-strength",   (profile, node) -> setIntOption(node, profile::setCaseOptimizationStrength, 0, profile.getMaxCaseOptimizationStrength()));
        map.put("debug-output",                 (profile, node) -> setBooleanOption(node, profile::setDebugOutput));
        map.put("function-prefix",              (profile, node) -> setBooleanOption(node, profile::setShortFunctionPrefix, "short", "long"));
        map.put("goal",                         (profile, node) -> setEnumOption(node, GenerationGoal::byName, profile::setGoal, GenerationGoal::allowedValues));
        map.put("instruction-limit",            (profile, node) -> setIntOption(node, profile::setInstructionLimit, 1, profile.getMaxInstructionLimit()));
        map.put("mlog-block-optimization",      (profile, node) -> setBooleanOption(node, profile::setMlogBlockOptimization));
        map.put("mlog-indent",                  (profile, node) -> setIntOption(node, profile::setMlogIndent, 0, CompilerProfile.MAX_MLOG_INDENT));
        map.put("optimization",                 (profile, node) -> setEnumOption(node, OptimizationLevel::byName, profile::setAllOptimizationLevels, OptimizationLevel::allowedValues));
        map.put("output-profiling",             (profile, node) -> setBooleanOption(node, profile::setOutputProfiling));
        map.put("passes",                       (profile, node) -> setIntOption(node, profile::setOptimizationPasses, 1, profile.getMaxPasses()));
        map.put("print-unresolved",             (profile, node) -> setEnumOption(node, FinalCodeOutput::byName, profile::setFinalCodeOutput, FinalCodeOutput::allowedValues));
        map.put("profile",                      this::setProfile);
        map.put("remarks",                      (profile, node) -> setEnumOption(node, Remarks::byName, profile::setRemarks, Remarks::allowedValues));
        map.put("sort-variables",               this::setSortVariables);
        map.put("symbolic-labels",              (profile, node) -> setBooleanOption(node, profile::setSymbolicLabels));
        map.put("syntax",                       this::setSyntax);
        map.put("target",                       this::setTarget);
        map.put("target-guard",                 (profile, node) -> setBooleanOption(node, profile::setTargetGuard));
        map.put("text-jump-tables",             (profile, node) -> setBooleanOption(node, profile::setTextJumpTables));
        map.put("unsafe-case-optimization",     (profile, node) -> setBooleanOption(node, profile::setUnsafeCaseOptimization));

        for (Optimization optimization : Optimization.LIST) {
            map.put(optimization.getOptionName(),                                                                          (profile, node) -> setEnumOption(node,
                    OptimizationLevel::byName, value -> profile.setOptimizationLevel(optimization, value),
                    OptimizationLevel::allowedValues));
        }

        for (ExecutionFlag flag : ExecutionFlag.values()) {
            if (flag.isSettable()) {
                map.put(flag.getOptionName(),                                                                          (profile, node) -> setBooleanOption(node, value -> profile.setExecutionFlag(flag, value)));
            }
        }

        return map;
    }

    private <E extends Enum<E>> void reportWrongOptionValue(AstDirectiveSet node,
            AstDirectiveValue value, Collection<String> allowedValues) {
        Optional<String> bestAlternative = StringSimilarity.findBestAlternative(value.getText(), allowedValues);
        if (bestAlternative.isPresent()) {
            error(value, ERR.DIRECTIVE_INVALID_VALUE_WITH_SUGGESTION, value.getText(),
                    node.getOption().getText(), bestAlternative.get());
        } else {
            error(value, ERR.DIRECTIVE_INVALID_VALUE, value.getText(), node.getOption().getText());
        }
    }
}