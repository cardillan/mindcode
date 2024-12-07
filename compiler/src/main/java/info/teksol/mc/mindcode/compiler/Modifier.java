package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;

import java.util.*;

public enum Modifier {
    CACHED          (MindcodeLexer.CACHED),
    EXTERNAL        (MindcodeLexer.EXTERNAL),
    LINKED          (MindcodeLexer.LINKED),
    NOINIT          (MindcodeLexer.NOINIT),
    VOLATILE        (MindcodeLexer.VOLATILE),
    ;

    private final int token;

    Modifier(int token) {
        this.token = token;
    }

    public boolean isCompatibleWith(Set<Modifier> others) {
        return COMPATIBILITY_MAP.get(this).containsAll(others);
    }

    private static final List<Modifier> TOKENS = tokens();
    private static final Map<Modifier, Set<Modifier>> COMPATIBILITY_MAP = compatibilityMap();

    public static Modifier fromToken(int tokenType) {
        return Objects.requireNonNull(TOKENS.get(tokenType), "Unknown or invalid token " + tokenType);
    }

    private static List<Modifier> tokens() {
        int count = MindcodeLexer.VOCABULARY.getMaxTokenType();
        List<Modifier> tokens = new ArrayList<>(Collections.nCopies(count, null));
        for (Modifier modifier : Modifier.values()) {
            if (modifier.token >= 0) {
                tokens.set(modifier.token, modifier);
            }
        }
        return tokens;
    }

    private static Map<Modifier, Set<Modifier>> compatibilityMap() {
        Map<Modifier, Set<Modifier>> map = new HashMap<>();
        map.put(CACHED, Set.of(EXTERNAL, NOINIT));
        map.put(EXTERNAL, Set.of(CACHED, NOINIT));
        map.put(LINKED, Set.of(NOINIT));
        map.put(NOINIT, Set.of(CACHED, EXTERNAL, LINKED, VOLATILE));
        map.put(VOLATILE, Set.of(NOINIT));
        return map;
    }
}
