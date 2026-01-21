package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@NullMarked
public enum FunctionModifier {
    INLINE          (MindcodeLexer.INLINE),
    NOINLINE        (MindcodeLexer.NOINLINE),
    EXPORT          (MindcodeLexer.EXPORT),
    ATOMIC          (MindcodeLexer.ATOMIC),
    DEBUG           (MindcodeLexer.DEBUG),

    // Deprecated - equivalent to EXPORT
    REMOTE          (MindcodeLexer.REMOTE),
    ;

    private final int token;

    FunctionModifier(int token) {
        this.token = token;
    }

    public boolean compatible(FunctionModifier other) {
        return switch (this) {
            case INLINE -> other != NOINLINE && other != EXPORT && other != REMOTE;
            case NOINLINE -> other != INLINE;
            case EXPORT -> other != INLINE && other != DEBUG && other != REMOTE;
            case DEBUG -> other != EXPORT && other != REMOTE;
            case REMOTE -> other != INLINE && other != DEBUG && other != EXPORT;
            default -> true;
        };
    }

    public String keyword() {
        return MindcodeLexer.VOCABULARY.getLiteralName(token).replace("'", "");
    }

    private static final List<@Nullable FunctionModifier> TOKENS = tokens();

    public static FunctionModifier fromToken(int tokenType) {
        return Objects.requireNonNull(TOKENS.get(tokenType), "Unknown or invalid token " + tokenType);
    }

    private static List<@Nullable FunctionModifier> tokens() {
        int count = MindcodeLexer.VOCABULARY.getMaxTokenType();
        List<@Nullable FunctionModifier> tokens = new ArrayList<>(Collections.nCopies(count, null));
        for (FunctionModifier FunctionModifier : FunctionModifier.values()) {
            if (FunctionModifier.token >= 0) {
                tokens.set(FunctionModifier.token, FunctionModifier);
            }
        }
        return tokens;
    }
}
