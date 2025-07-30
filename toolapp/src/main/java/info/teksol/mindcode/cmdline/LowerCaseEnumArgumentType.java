package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.impl.type.CaseInsensitiveEnumArgumentType;

import java.util.Locale;

public class LowerCaseEnumArgumentType<T extends Enum<T>>
        extends CaseInsensitiveEnumArgumentType<T> {

    public LowerCaseEnumArgumentType(Class<T> type) {
        super(type, Locale.ROOT);
    }

    public static <T extends Enum<T>> LowerCaseEnumArgumentType<T> forEnum(Class<T> type) {
        return new LowerCaseEnumArgumentType<>(type);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static LowerCaseEnumArgumentType<?> forClass(Class<?> type) {
        if (!type.isEnum()) {
            throw new IllegalArgumentException("Type must be an enum");
        }
        return new LowerCaseEnumArgumentType(type);
    }


    @Override
    protected String toStringRepresentation(T t) {
        return t.name().toLowerCase(Locale.US).replace('_', '-');
    }

    @Override
    protected Object[] getStringRepresentations() {
        T[] enumConstants = type_.getEnumConstants();
        Object[] names = new String[enumConstants.length];
        for (int i = 0; i < enumConstants.length; i++) {
            names[i] = toStringRepresentation(enumConstants[i]);
        }
        return names;
    }
}

