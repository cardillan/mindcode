package info.teksol.mc.profile.options;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public abstract class CompilerOptionValue<T> implements CompilerOption{
    public final Enum<?> option;
    public final String optionName;
    public final String flag;
    public final String description;
    public final Class<T> valueType;
    public final OptionAvailability availability;
    public final SemanticStability stability;
    public final OptionCategory category;
    public final OptionScope scope;
    public final OptionMultiplicity multiplicity;
    public final List<T> defaultValues;
    private final List<T> values = new ArrayList<>();
    private final List<T> constValues = new ArrayList<>();

    private Function<@Nullable List<T>, List<T>> listProcessor = list -> list == null ? List.of() : list;

    private final List<Consumer<List<T>>> changeListener = new ArrayList<>();

    public CompilerOptionValue(Enum<?> option, String optionName, String flag, String description, Class<T> valueType,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, List<T> defaultValues) {
        this.option = option;
        this.optionName = optionName;
        this.flag = flag;
        this.description = description;
        this.valueType = valueType;
        this.availability = availability;
        this.stability = stability;
        this.category = category;
        this.scope = scope;
        this.multiplicity = multiplicity;
        this.defaultValues = defaultValues;
        this.values.addAll(defaultValues);
        this.constValues.addAll(defaultValues);
    }

    public CompilerOptionValue(Enum<?> option, String optionName, String flag, String description, Class<T> valueType,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, T defaultValue) {
        this(option, optionName, flag, description, valueType, multiplicity, stability, scope, availability, category, List.of(defaultValue));
    }

    public CompilerOptionValue(Enum<?> option, String flag, String description, Class<T> valueType,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, T defaultValue) {
        this(option, EnumUtils.toKebabCase(option), flag, description, valueType, multiplicity, stability, scope, availability, category, List.of(defaultValue));
    }

    public Enum<?> getOption() {
        return option;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getFlag() {
        return flag;
    }

    public String[] getNameOrFlag() {
        if (!flag.isEmpty()) {
            return optionName.isEmpty() ? new String[]{"-" + flag} : new String[]{"-" + flag, "--" + optionName};
        } else {
            return new String[]{"--" + optionName};
        }
    }

    public String getDescription() {
        return description;
    }

    public Class<T> getValueType() {
        return valueType;
    }

    public OptionAvailability getAvailability() {
        return availability;
    }

    public SemanticStability getStability() {
        return stability;
    }

    public OptionCategory getCategory() {
        return category;
    }

    public OptionScope getScope() {
        return scope;
    }

    public OptionMultiplicity getMultiplicity() {
        return multiplicity;
    }

    public T getConstValue() {
        return constValues.getFirst();
    }

    public CompilerOptionValue<T> setConstValue(T emptyValue) {
        this.constValues.clear();
        this.constValues.add(emptyValue);
        return this;
    }

    public CompilerOptionValue<T> setListProcessor(Function<@Nullable List<T>, List<T>> listProcessor) {
        this.listProcessor = listProcessor;
        return this;
    }

    public CompilerOptionValue<T> addChangeListener(Consumer<List<T>> listener) {
        changeListener.add(listener);
        return this;
    }

    public abstract @Nullable T convert(String value);

    public boolean accepts(T value) {
        return true;
    }

    public List<String> acceptedValues() {
        return List.of();
    }

    public String acceptedValuesDescription() {
        return acceptedValues().stream().collect(Collectors.joining(",", "{", "}"));
    }

    public T getValue() {
        return values.getFirst();
    }

    public void setValue(T value) {
        setValues(List.of(value));
    }

    public List<T> getValues() {
        return List.copyOf(values);
    }

    public void setValues(List<T> values) {
        List<T> processed = listProcessor.apply(values);
        if (processed.stream().anyMatch(value -> !valueType.isInstance(value))) {
            throw new IllegalArgumentException("Value is not of type " + valueType.getName());
        }
        this.values.clear();
        this.values.addAll(processed);
        changeListener.forEach(listener -> listener.accept(processed));
    }

    public boolean setValues(List<T> values, OptionScope scope) {
        setValues(values);
        return true;
    }

    public Object getDefaultValue() {
        return multiplicity.matchesMultiple() ? List.copyOf(defaultValues) : defaultValues.getFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        CompilerOptionValue<?> that = (CompilerOptionValue<?>) o;
        return option.equals(that.option) && values.equals(that.values);
    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String strValues = multiplicity.matchesMultiple()
                ? values.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"))
                : values.getFirst().toString();
        return option.getClass().getSimpleName() + "." + option.name() + " = " + strValues;
    }
}
