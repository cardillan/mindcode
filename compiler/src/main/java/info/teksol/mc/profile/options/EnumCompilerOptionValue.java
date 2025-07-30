package info.teksol.mc.profile.options;

import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class EnumCompilerOptionValue<E extends Enum<E>> extends CompilerOptionValue<E> {
    private final List<String> acceptedValues = new ArrayList<>();
    private final Map<String, E> valueMap = new HashMap<>();

    public EnumCompilerOptionValue(Enum<?> option, String optionName, String flag, String description, Class<E> valueType,
            OptionMultiplicity multiplicity, SemanticStability stability, OptionScope scope, OptionAvailability availability,
            OptionCategory category, E defaultValue) {
        super(option, optionName, flag, description, valueType, multiplicity, stability, scope, availability, category, defaultValue);

        for (E e : valueType.getEnumConstants()) {
            String value = e.name().toLowerCase(Locale.US);
            String dashes = value.replace('_', '-');
            valueMap.put(value, e);
            valueMap.put(dashes, e);
            acceptedValues.add(dashes);
        }
    }

    public EnumCompilerOptionValue(Enum<?> option, String flag, String description, Class<E> valueType, OptionMultiplicity multiplicity,
            SemanticStability stability, OptionScope scope, OptionAvailability availability, OptionCategory category, E defaultValue) {
        this(option, EnumUtils.toKebabCase(option), flag, description, valueType, multiplicity, stability,
                scope, availability, category, defaultValue);
    }

    @Override
    public @Nullable E convert(String value) {
        return valueMap.get(value);
    }

    @Override
    public List<String> acceptedValues() {
        return acceptedValues;
    }

    private E[] getEnumConstants(Class<E> enumType) {
        return enumType.getEnumConstants();
    }
}
