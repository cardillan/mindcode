package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.helper.MessageLocalization;
import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.*;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NullMarked
public class LowerCaseEnumArgumentType<T extends Enum<T>> implements ArgumentType<T>, MetavarInference {
    protected final Class<T> type;
    protected final List<T> values;
    protected final List<String> matches;

    protected LowerCaseEnumArgumentType(Class<T> type, Predicate<T> filter) {
        this.type = type;
        this.values = Stream.of(type.getEnumConstants()).filter(filter).toList();
        this.matches = values.stream().map(this::toStringRepresentation).toList();
    }

    public static <T extends Enum<T>> LowerCaseEnumArgumentType<T> forEnum(Class<T> type, Predicate<T> filter) {
        return new LowerCaseEnumArgumentType<>(type, filter);
    }

    public static <T extends Enum<T>> LowerCaseEnumArgumentType<T> forEnum(Class<T> type) {
        return new LowerCaseEnumArgumentType<>(type, t -> true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static LowerCaseEnumArgumentType<?> forClass(Class<?> type) {
        if (!type.isEnum()) {
            throw new IllegalArgumentException("Type must be an enum");
        }
        return new LowerCaseEnumArgumentType(type, t -> true);
    }

    protected String toStringRepresentation(T t) {
        return t.name().toLowerCase(Locale.US).replace('_', '-');
    }

    public T convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        for (int i = 0; i < values.size(); i++) {
            if (matches.get(i).equals(value)) {
                return values.get(i);
            }
        }

        String choices = TextHelper.concat(matches, 0, ",", "{", "}");
        throw new ArgumentParserException(String.format(TextHelper.LOCALE_ROOT,
                MessageLocalization.localize(parser.getConfig().getResourceBundle(), "couldNotConvertChooseFromError"),
                value, choices), parser, arg);
    }

    public String[] inferMetavar() {
        return new String[]{TextHelper.concat(matches, 0, ",", "{", "}")};
    }
}
