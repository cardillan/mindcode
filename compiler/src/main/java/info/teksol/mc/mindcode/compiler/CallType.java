package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum CallType {
    NONE            (-1),
    INLINE          (MindcodeLexer.INLINE),
    NOINLINE        (MindcodeLexer.NOINLINE),
    REMOTE          (MindcodeLexer.REMOTE),
    ;

    private final int token;

    CallType(int token) {
        this.token = token;
    }

    private static final List<@Nullable CallType> TOKENS = tokens();

    public static CallType fromToken(int tokenType) {
        return Objects.requireNonNull(TOKENS.get(tokenType), "Unknown or invalid token " + tokenType);
    }

    private static List<@Nullable CallType> tokens() {
        int count = MindcodeLexer.VOCABULARY.getMaxTokenType();
        List<@Nullable CallType> tokens = new ArrayList<>(Collections.nCopies(count, null));
        for (CallType CallType : CallType.values()) {
            if (CallType.token >= 0) {
                tokens.set(CallType.token, CallType);
            }
        }
        return tokens;
    }
}
