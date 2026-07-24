package info.teksol.mc.emulator.mimex;

import info.teksol.mc.common.Globals;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.util.Utf8Utils;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public abstract class LParserBase implements LParser {
    protected final ParserMessageHandler errorHandler;
    protected final MindustryMetadata metadata;
    protected final LStrings strings;
    protected final Map<String, String> opNameChanges = opNameChanges();
    protected final Map<String, String> tokenChanges = tokenChanges();

    protected final String[] tokens = new String[16];
    protected final List<JumpIndex> jumps = new ArrayList<>();
    protected final Map<String, Integer> jumpLocations = new HashMap<>();
    protected final List<LStatement> statements = new ArrayList<>();
    protected final boolean enforceInstructionLimit;
    protected final boolean privileged;
    protected final char[] chars;

    // An error was encountered (and reported) during parsing
    protected boolean includeComments = false;
    protected boolean includeLabels = false;
    protected boolean error;
    protected int pos;
    protected int line;

    public LParserBase(ParserMessageHandler errorHandler, MindustryMetadata metadata, LStrings strings, String code,
            boolean privileged, boolean enforceInstructionLimit) {
        this.errorHandler = errorHandler;
        this.strings = strings;
        this.metadata = metadata;
        this.enforceInstructionLimit = enforceInstructionLimit;
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
        int from = pos;

        //read until \n or eof
        while (pos < chars.length && chars[pos++] != '\n') ;

        if (includeComments) {
            statements.add(new CommentStatement(new String(chars, from, pos - from)));
        }
    }

    void error(@PrintFormat String format, Object... args) {
        if (errorHandler.error(format, args)) {
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
    protected void checkRead() {
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
                if (jumpLocations.size() >= Globals.MAX_JUMPS) {
                    error("Too many jump locations. Max jumps: %d", Globals.MAX_JUMPS);
                }
                String label = tokens[0].substring(0, tokens[0].length() - 1);
                if (jumpLocations.containsKey(label)) {
                    error("Jump label already defined: \"%s\".", label);
                }
                jumpLocations.put(label, line);
                if (includeLabels) {
                    statements.add(new LabelStatement(tokens[0]));
                }
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
    public LParser includeComments() {
        includeComments = true;
        return this;
    }

    @Override
    public LParser includeLabels() {
        includeLabels = true;
        return this;
    }

    @Override
    public List<LStatement> parse() {
        jumps.clear();
        jumpLocations.clear();

        if (Utf8Utils.utf8Length(chars) > Globals.MAX_MLOG_BYTE_LENGTH) {
            error("Mlog file too long. Max length: %,d bytes", Globals.MAX_MLOG_BYTE_LENGTH);
            return List.of();
        }

        while (pos < chars.length) {
            if (line == Globals.MAX_INSTRUCTIONS && enforceInstructionLimit) {
                error("Too many instructions. Max instructions: %d", Globals.MAX_INSTRUCTIONS);
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

    protected record JumpIndex(JumpStatement jump, String location) {
    }
}
