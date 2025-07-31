package info.teksol.mindcode.cmdline;

import net.sourceforge.argparse4j.helper.TextHelper;
import net.sourceforge.argparse4j.inf.ArgumentChoice;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@NullMarked
public class CaseInsensitiveChoices implements ArgumentChoice {
    private final TreeSet<String> choices = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    public CaseInsensitiveChoices(List<String> values) {
        choices.addAll(values);
    }

    @Override
    public boolean contains(Object val) {
        if (val instanceof String) {
            return choices.contains(val);
        } else {
            throw new IllegalArgumentException(String.format(
                    TextHelper.LOCALE_ROOT,
                    "type mismatch (Make sure that you specified correct Argument.type()):"
                    + " expected: String actual: %s", val.getClass().getName()));

        }
    }

    @Override
    public String textualFormat() {
        return choices.stream().collect(Collectors.joining(",", "{", "}"));
    }
}
