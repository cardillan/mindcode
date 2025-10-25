package info.teksol.mc.profile;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveSet;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstDirectiveValue;
import info.teksol.mc.profile.options.CompilerOptionFactory;
import info.teksol.mc.profile.options.CompilerOptionValue;
import info.teksol.mc.profile.options.OptionMultiplicity;
import info.teksol.mc.profile.options.OptionScope;
import info.teksol.mc.util.StringSimilarity;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@NullMarked
public class DirectiveProcessor extends AbstractMessageEmitter {
    private static final Map<String, Enum<?>> OPTION_MAP = createOptionMap();

    private OptionScope scopeLimit = OptionScope.GLOBAL;

    public DirectiveProcessor(MessageConsumer messageConsumer) {
        super(messageConsumer);
    }

    public void setScopeLimit(OptionScope scopeLimit) {
        this.scopeLimit = scopeLimit;
    }

    private <T> List<T> convertValues(CompilerOptionValue<T> option, AstDirectiveSet node) {
        List<T> result = new ArrayList<>();
        for (AstDirectiveValue value : node.getValues()) {
            T converted = option.convert(value.getText());
            if (converted == null) {
                reportWrongOptionValue(node, value, option.acceptedValues());
            } else if (option.accepts(converted, s -> error(value, "%s", s))) {
                result.add(converted);
            }
        }
        return result;
    }

    private <T> void setOptionValue(CompilerOptionValue<T> option, AstDirectiveSet node) {
        if (!option.getScope().isIncludedIn(scopeLimit)) {
            if (scopeLimit == OptionScope.LOCAL && node.isLocal()) {
                error(node, ERR.DIRECTIVE_IS_NOT_LOCAL, option.getOptionName());
            }
            return;
        }

        if (node.getValues().isEmpty() && !option.multiplicity.matchesMultiple()) {
            if (option.multiplicity.matchesNone()) {
                option.setValue(option.getConstValue());
            } else {
                error(node.getOption(), ERR.DIRECTIVE_NO_VALUE, node.getOption().getText());
            }
        } else {
            List<T> converted = convertValues(option, node);

            if (node.getValues().size() > 1 && !option.multiplicity.matchesMultiple() || option.multiplicity == OptionMultiplicity.ZERO) {
                error(node.getOption(), ERR.DIRECTIVE_MULTIPLE_VALUES, node.getOption().getText());
            }

            if (converted.size() == node.getValues().size()) {
                if (!option.setValues(converted, scopeLimit)) {
                    error(node.getOption(), ERR.DIRECTIVE_VALUE_IS_NOT_LOCAL, node.getValues().getFirst().getText());
                }
            }
        }
    }

    public void processDirective(CompilerProfile profile, AstDirectiveSet directive) {
        String directiveText = directive.getOption().getText();
        Enum<?> optionEnum = OPTION_MAP.get(directiveText);
        if (optionEnum == null) {
            if (directiveText.equals("profile")) {
                if (validateSingleValue(directive)) {
                    profile.decode(directive.getValues().getFirst().getText());
                }
                return;
            }

            Optional<String> alternative = StringSimilarity.findBestAlternative(directiveText, OPTION_MAP.keySet());
            if (alternative.isPresent()) {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN_WITH_SUGGESTION, directiveText, alternative.get());
            } else {
                error(directive.getOption(), ERR.DIRECTIVE_UNKNOWN, directiveText);
            }
        } else {
            setOptionValue(profile.getOption(optionEnum), directive);
        }
    }
        
    private boolean validateSingleValue(AstDirectiveSet node) {
        if (node.getValues().isEmpty()) {
            error(node.getOption(), ERR.DIRECTIVE_NO_VALUE, node.getOption().getText());
        } else if (node.getValues().size() > 1) {
            error(node.getOption(), ERR.DIRECTIVE_MULTIPLE_VALUES, node.getOption().getText());
        }

        return !node.getValues().isEmpty();
    }

    private static Map<String, Enum<?>> createOptionMap() {
        return CompilerOptionFactory.createCompilerOptions(false).values().stream()
                .filter(value -> value.availability.isDirective())
                .collect(Collectors.toMap(CompilerOptionValue::getOptionName, CompilerOptionValue::getOption));
    }

    private <E extends Enum<E>> void reportWrongOptionValue(AstDirectiveSet node,
            AstDirectiveValue value, List<String> allowedValues) {
        if (allowedValues.isEmpty()) {
            error(value.sourcePosition(), ERR.DIRECTIVE_INVALID_VALUE, value.getText(), node.getOption().getText());
        } else {
            Optional<String> bestAlternative = StringSimilarity.findBestAlternative(value.getText(), allowedValues);
            if (bestAlternative.isPresent()) {
                error(value, ERR.DIRECTIVE_INVALID_VALUE_WITH_SUGGESTION, value.getText(),
                        node.getOption().getText(), bestAlternative.get());
            } else {
                error(value, ERR.DIRECTIVE_INVALID_VALUE_WITH_VALUES, value.getText(), node.getOption().getText(),
                        joinWithAnd(allowedValues));
            }
        }
    }

    private static String joinWithAnd(List<String> values) {
        if (values.size() <= 2) {
            return values.stream().collect(Collectors.joining("' and '", "'", "'"));
        } else {
            String first = values.stream().limit(values.size() - 1)
                    .collect(Collectors.joining("', '", "'", "'"));

            return first + " and '" + values.getLast() + "'";
        }
    }
}