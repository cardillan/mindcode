package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public abstract class LParserBase implements LParser {
    private static final int maxInstructions = 1000;
    private static final int maxJumps = 500;

    private final EmulatorErrorHandler errorHandler;
    private final MindustryMetadata metadata;
    private final LStrings strings;
    private final Map<String, String> opNameChanges = opNameChanges();
    private final Map<String, String> tokenChanges = tokenChanges();

    private final String[] tokens = new String[16];
    private final List<JumpIndex> jumps = new ArrayList<>();
    private final Map<String, Integer> jumpLocations = new HashMap<>();
    private final List<LStatement> statements = new ArrayList<>();
    private final boolean privileged;
    private final char[] chars;

    // An error was encountered (and reported) during parsing
    private boolean error;
    private int pos;
    private int line;

    public LParserBase(EmulatorErrorHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code, boolean privileged) {
        this.errorHandler = errorHandler;
        this.strings = strings;
        this.metadata = metadata;
        this.privileged = privileged;
        this.chars = code.toCharArray();
    }

    protected Map<String, String> opNameChanges() {
        return Map.of(
                "atan2", "angle",
                "dst", "len"
        );
    }

    protected Map<String, String> tokenChanges() {
        return Map.of();
    }

    void comment() {
        //read until \n or eof
        while (pos < chars.length && chars[pos++] != '\n') ;
    }

    void error(@PrintFormat String format, Object... args) {
        if (errorHandler.error(ExecutionFlag.ERR_PARSE_ERROR, format, args)) {
            error = true;
        }
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
            if (tok >= tokens.length) {
                error("Line too long; may only contain %d tokens", tokens.length);
            }

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
                    error("Too many jump locations. Max jumps: %d", maxJumps);
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
                    tokens[i] = tokenChanges.getOrDefault(tokens[i], tokens[i]);
                }

                LStatement st = LStatement.create(metadata, tokens, tok);

                //discard misplaced privileged instructions
                if (!privileged && st.privileged()) {
                    st = new InvalidStatement(tokens, tok);
                    error("Privileged instruction found in non-privileged processor: %s", st);
                } else if (st instanceof InvalidStatement) {
                    error("Invalid instruction: %s", st);
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

    @Override
    public List<LStatement> parse(boolean enforceInstructionLimit) {
        jumps.clear();
        jumpLocations.clear();

        while (pos < chars.length) {
            if (line == maxInstructions && enforceInstructionLimit) {
                error("Too many instructions. Max instructions: %d", maxInstructions);
                break;
            }
            switch (chars[pos]) {
                case '\n', ';', ' ' -> pos++; //skip newlines and spaces
                case '\r' -> pos += 2; //skip the newline after the \r
                default -> statement();
            }
        }

        //load destination indices
        for (var i : jumps) {
            if (!jumpLocations.containsKey(i.location)) {
                error("Undefined jump location: '%s'. Make sure the jump label exists and is typed correctly.", i.location);
            }
            i.jump.destIndex = jumpLocations.getOrDefault(i.location, -1);
        }

        return statements;
    }

    @Override
    public boolean isError() {
        return error;
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
