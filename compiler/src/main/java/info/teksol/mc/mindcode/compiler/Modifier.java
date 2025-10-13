package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public enum Modifier {
    // Primary modifiers: mutually exclusive
    NONE            (-1),
    CONST           (MindcodeLexer.CONST),
    LINKED          (MindcodeLexer.LINKED),
    EXTERNAL        (MindcodeLexer.EXTERNAL),
    EXPORT          (MindcodeLexer.EXPORT),
    REMOTE          (MindcodeLexer.REMOTE),

    // Secondary modifiers: allowed for certain primary ones
    CACHED          (MindcodeLexer.CACHED, EXTERNAL, REMOTE),
    GUARDED         (MindcodeLexer.GUARDED, NONE, LINKED),
    MLOG            (MindcodeLexer.MLOG, NONE, EXPORT, REMOTE),
    NOINIT          (MindcodeLexer.NOINIT, NONE, EXPORT, EXTERNAL, REMOTE),
    VOLATILE        (MindcodeLexer.VOLATILE, NONE, EXPORT),
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
        return EnumSet.of(CONST, LINKED, EXTERNAL, EXPORT, REMOTE);
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
