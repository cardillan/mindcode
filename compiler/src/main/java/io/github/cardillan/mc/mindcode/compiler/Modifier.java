package io.github.cardillan.mc.mindcode.compiler;

import io.github.cardillan.mc.mindcode.compiler.antlr.MindcodeLexer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public enum Modifier {
    // Primary modifiers: mutually exclusive
    NONE            (-1),
    CONST           (MindcodeLexer.CONST),
    DEBUG           (MindcodeLexer.DEBUG),
    LINKED          (MindcodeLexer.LINKED),
    EXTERNAL        (MindcodeLexer.EXTERNAL),
    EXPORT          (MindcodeLexer.EXPORT),
    REMOTE          (MindcodeLexer.REMOTE),

    // Secondary modifiers: allowed for certain primary ones
    CACHED          (MindcodeLexer.CACHED, EXTERNAL, REMOTE),
    GUARDED         (MindcodeLexer.GUARDED, NONE, LINKED),
    MLOG            (MindcodeLexer.MLOG, NONE, DEBUG, EXPORT, REMOTE),
    NOINIT          (MindcodeLexer.NOINIT, NONE, LINKED, EXPORT, EXTERNAL, REMOTE),
    VOLATILE        (MindcodeLexer.VOLATILE, NONE, LINKED, EXPORT),
    ;

    private final int token;
    private final List<Modifier> requirements;

    Modifier(int token) {
        this.token = token;
        this.requirements = List.of();
    }

    Modifier(int token, Modifier... requirements) {
        this.token = token;
        this.requirements = List.of(requirements);
    }

    public List<Modifier> getRequirements() {
        return requirements;
    }

    public String keyword() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static EnumSet<Modifier> getPrimarySet() {
        return EnumSet.of(CONST, DEBUG, EXPORT, EXTERNAL, LINKED, REMOTE);
    }

    public static Modifier fromToken(int tokenType) {
        return Objects.requireNonNull(TOKENS.get(tokenType), "Unknown or invalid token " + tokenType);
    }

    private static final List<@Nullable Modifier> TOKENS = tokens();

    private static List<@Nullable Modifier> tokens() {
        int count = MindcodeLexer.VOCABULARY.getMaxTokenType();
        List<@Nullable Modifier> tokens = new ArrayList<>(Collections.nCopies(count, null));
        for (Modifier modifier : Modifier.values()) {
            if (modifier.token >= 0) {
                tokens.set(modifier.token, modifier);
            }
        }
        return tokens;
    }
}
