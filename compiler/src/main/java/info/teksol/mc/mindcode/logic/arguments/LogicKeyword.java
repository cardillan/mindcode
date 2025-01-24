package info.teksol.mc.mindcode.logic.arguments;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class LogicKeyword extends AbstractArgument {
    private final String keyword;

    private LogicKeyword(String keyword) {
        super(ArgumentType.KEYWORD, ValueMutability.IMMUTABLE);
        this.keyword = Objects.requireNonNull(keyword);
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toMlog() {
        return keyword;
    }

    @Override
    public String toString() {
        return "LogicKeyword{" +
                "keyword='" + keyword + '\'' +
                '}';
    }

    public static LogicKeyword create(String keyword) {
        return new LogicKeyword(keyword);
    }

    public static final LogicKeyword INVALID = create("");
}
