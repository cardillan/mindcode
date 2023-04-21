package info.teksol.mindcode.logic;

import java.util.Objects;

public class LogicKeyword extends AbstractArgument {
    private final String keyword;

    private LogicKeyword(String keyword) {
        super(ArgumentType.KEYWORD);
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
}
