package info.teksol.mc.emulator.v2;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public abstract class LParserBase {
    private static final int maxJumps = 500;

    private final LStrings strings;
    private final MindustryMetadata metadata;
    private final Map<String, String> opNameChanges = opNameChanges();

    private final String[] tokens = new String[16];
    private final List<JumpIndex> jumps = new ArrayList<>();
    private final Map<String, Integer> jumpLocations = new HashMap<>();
    private final List<LStatement> statements = new ArrayList<>();
    private final boolean privileged;
    private final char[] chars;

    private int maxInstructions = Integer.MAX_VALUE;
    private int pos;
    private int line;

    public LParserBase(LStrings strings, MindustryMetadata metadata, String text, boolean privileged) {
        this.strings = strings;
        this.metadata = metadata;
        this.privileged = privileged;
        this.chars = text.toCharArray();
    }

    protected Map<String, String> opNameChanges() {
        return Map.of(
                "atan2", "angle",
                "dst", "len"
        );
    }

    void comment() {
        //read until \n or eof
        while (pos < chars.length && chars[pos++] != '\n') ;
    }

    void error(String message) {
        throw new RuntimeException("Invalid code. " + message);
    }

    String string() {
        int from = pos;

        while (++pos < chars.length) {
            var c = chars[pos];
            if (c == '\n') {
                error("Missing closing quote \" before end of line.");
            } else if (c == '"') {
                break;
            }
        }

        if (pos >= chars.length || chars[pos] != '"') error("Missing closing quote \" before end of file.");

        return new String(chars, from, ++pos - from);
    }

    String token() {
        int from = pos;

        while (pos < chars.length) {
            char c = chars[pos];
            if (c == '\n' || c == ' ' || c == '#' || c == '\t' || c == ';') break;
            pos++;
        }

        return new String(chars, from, pos - from);
    }

    /**
     * Apply changes after reading a list of tokens.
     */
    void checkRead() {
        if (tokens[0].equals("op")) {
            //legacy name change
            tokens[1] = opNameChanges.getOrDefault(tokens[1], tokens[1]);
        }
    }

    /**
     * Reads the next statement until EOL/EOF.
     */
    void statement() {
        boolean expectNext = false;
        int tok = 0;

        while (pos < chars.length) {
            char c = chars[pos];
            if (tok >= tokens.length) error("Line too long; may only contain " + tokens.length + " tokens");

            //reached end of line, bail out.
            if (c == '\n' || c == ';') break;

            if (expectNext && c != ' ' && c != '#' && c != '\t') {
                error("Expected space after string/token.");
            }

            expectNext = false;

            if (c == '#') {
                comment();
                break;
            } else if (c == '"') {
                tokens[tok++] = string();
                expectNext = true;
            } else if (c != ' ' && c != '\t') {
                tokens[tok++] = token();
                expectNext = true;
            } else {
                pos++;
            }
        }

        //only process lines with at least 1 token
        if (tok > 0) {
            checkRead();

            //store jump location, always ends with colon
            if (tok == 1 && tokens[0].charAt(tokens[0].length() - 1) == ':') {
                if (jumpLocations.size() >= maxJumps) {
                    error("Too many jump locations. Max jumps: " + maxJumps);
                }
                jumpLocations.put(tokens[0].substring(0, tokens[0].length() - 1), line);
            } else {
                String jumpLoc = null;
                boolean wasJump = tokens[0].equals("jump") && tok > 1 && !strings.canParseInt(tokens[1]);
                //clean up jump position before parsing
                if (wasJump) {
                    jumpLoc = tokens[1];
                    tokens[1] = "-1";
                }

                for (int i = 1; i < tok; i++) {
                    if (tokens[i].equals("@configure")) tokens[i] = "@config";
                    if (tokens[i].equals("configure")) tokens[i] = "config";
                }

                LStatement st = LStatement.create(metadata, tokens, tok);

                //discard misplaced privileged instructions
                if (!privileged && st.privileged()) {
                    st = new InvalidStatement();
                }

                //store jumps that use labels
                if (st instanceof JumpStatement jump && wasJump) {
                    jumps.add(new JumpIndex(jump, jumpLoc));
                }

                statements.add(st);
                line++;
            }
        }
    }

    List<LStatement> parse() {
        jumps.clear();
        jumpLocations.clear();

        while (pos < chars.length && line < maxInstructions) {
            switch (chars[pos]) {
                case '\n', ';', ' ' -> pos++; //skip newlines and spaces
                case '\r' -> pos += 2; //skip the newline after the \r
                default -> statement();
            }
        }

        //load destination indices
        for (var i : jumps) {
            if (!jumpLocations.containsKey(i.location)) {
                error("Undefined jump location: \"" + i.location + "\". Make sure the jump label exists and is typed correctly.");
            }
            i.jump.destIndex = jumpLocations.getOrDefault(i.location, -1);
        }

        return statements;
    }

    static class JumpIndex {
        JumpStatement jump;
        String location;

        public JumpIndex(JumpStatement jump, String location) {
            this.jump = jump;
            this.location = location;
        }
    }
}
