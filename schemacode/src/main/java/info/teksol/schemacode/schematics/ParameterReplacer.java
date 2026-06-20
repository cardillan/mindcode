package info.teksol.schemacode.schematics;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// This more or less implements parsing all over again. Aargh!
public class ParameterReplacer {
    private final Map<String, String> replacements;
    private final char[] chars;
    private int pos;
    private @Nullable String replacement = null;
    private final StringBuilder result;

    public ParameterReplacer(String mlog, Map<String, String> replacements) {
        this.replacements = new HashMap<>(replacements);
        this.chars = mlog.toCharArray();
        this.result = new StringBuilder(mlog.length() + 100 * replacements.size());
    }

    public void replace() throws ReplacementException {
        while (pos < chars.length) {
            char c = chars[pos];
            switch (c) {
                case '\n', '\r', ';', ' ' -> {
                    result.append(c);
                    pos++;
                }
                default -> statement();
            }

            // After replacing all, there's no point to continue parsing
            if (replacements.isEmpty()) {
                result.append(chars, pos, chars.length - pos);
                break;
            }
        }
    }

    public Set<String> getAbsentParameters() {
        return replacements.keySet();
    }

    public String getResult() {
        return result.toString();
    }

    private void error(String message) throws ReplacementException {
        throw new ReplacementException(message);
    }

    void statement() throws ReplacementException {
        boolean expectNext = false;
        int tok = 0;
        boolean setStatement = false;

        replacement = null;
        while (pos < chars.length) {
            char c = chars[pos];

            if (c == '\n' || c == ';') {
                break;
            }

            if (expectNext && c != ' ' && c != '#' && c != '\t') {
                error("Expected space after string/token.");
            }

            expectNext = false;

            if (c == '#') {
                comment();
                break;
            } else if (c == '"') {
                string();
                if (replacement != null) {
                    result.append(replacement);
                    replacement = null;
                }
                tok++;
                expectNext = true;
            } else if (c != ' ' && c != '\t') {
                String token = token(tok == 0 || tok == 1 && setStatement);
                if (tok == 0 && "set".equals(token)) {
                    setStatement = true;
                } else if (tok == 1 && setStatement) {
                    replacement = replacements.remove(token);
                } else if (replacement != null) {
                    result.append(replacement);
                    replacement = null;
                }
                tok++;
                expectNext = true;
            } else {
                result.append(c);
                pos++;
            }
        }
    }

    void comment() {
        char c;
        while (pos < chars.length && (c = chars[pos++]) != '\n') result.append(c);
        if (pos < chars.length) result.append(chars[pos - 1]);
    }

    void string() throws ReplacementException {
        if (replacement == null) result.append('"');

        while (++pos < chars.length) {
            var c = chars[pos];
            if (replacement == null) result.append(c);

            if (c == '\n') {
                error("Missing closing quote \" before end of line.");
            } else if (c == '"') {
                pos++;
                break;
            }
        }
    }

    @Nullable String token(boolean extract) {
        int from = pos;

        while (pos < chars.length) {
            char c = chars[pos];
            if (c == '\n' || c == ' ' || c == '#' || c == '\t' || c == ';') break;

            if (replacement == null) result.append(c);
            pos++;
        }

        return extract ? new String(chars, from, pos - from) : null;
    }


    public static class ReplacementException extends Exception {
        public ReplacementException(String message) {
            super(message);
        }
    }
}
