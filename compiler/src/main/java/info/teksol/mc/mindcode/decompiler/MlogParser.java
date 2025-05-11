package info.teksol.mc.mindcode.decompiler;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.opcodes.InstructionParameterType;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
public class MlogParser {
    private final InstructionProcessor instructionProcessor;
    private final String mlog;

    private final List<Object> content = new ArrayList<>();
    private final Map<Integer, String> labels = new HashMap<>();
    private final Set<String> usedLabels = new HashSet<>();

    private static final AstContext STATIC_AST_CONTEXT = AstContext.createStaticRootNode();

    public MlogParser(InstructionProcessor instructionProcessor, String mlog) {
        this.instructionProcessor = instructionProcessor;
        this.mlog = mlog;
    }

    public ParsedMlog parse() {
        while (pos < mlog.length()) {
            switch (mlog.charAt(pos)) {
                case '\n', '\r', ';', '\t', ' ' -> pos++;
                default -> processStatement();
            }
        }

        return new ParsedMlog(labels, content);
    }

    private int pos;
    private int start;
    private int labelIndex;
    private final List<String> tokens = new ArrayList<>();

    private String error(String message) {
        skipLine();
        return mlog.substring(start, pos) + ": " + message;
    }

    private boolean endOfLine() {
        return pos >= mlog.length() || mlog.charAt(pos) == '\n' || mlog.charAt(pos) == '\r' || mlog.charAt(pos) == ';';
    }

    private void skipLine() {
        while (!endOfLine()) pos++;
    }

    private void processStatement() {
        boolean expectSeparator = false;
        tokens.clear();
        start = pos;

        while (pos < mlog.length()) {
            if (endOfLine()) break;

            char c = mlog.charAt(pos);

            if (expectSeparator && c != ' ' && c != '#' && c != '\t') {
                content.add(error("Missing separator after string or token."));
                return;
            }

            expectSeparator = false;

            if (c == '#') {
                skipLine();
                break;
            } else if (c == '"') {
                String string = parseString();
                if (string == null) {
                    content.add(error("Missing closing quote \" before end of line."));
                    return;
                }
                tokens.add(string);
                expectSeparator = true;
            } else if (c != ' ' && c != '\t') {
                String token = parseToken();
                tokens.add(tokens.isEmpty() ? token : convert(token));
                expectSeparator = true;
            } else {
                pos++;
            }
        }

        if (!tokens.isEmpty()) {
            if (tokens.size() == 1 && tokens.getFirst().endsWith(":")) {
                // Symbolic label
                labels.put(content.size(), tokens.getFirst());
            } else {
                Opcode opcode = Opcode.fromOpcode(tokens.getFirst());
                if (opcode != null) {
                    List<MlogExpression> arguments = tokens.stream().skip(1).map(this::arg)
                            .collect(Collectors.toCollection(ArrayList::new));
                    List<InstructionParameterType> parameters = instructionProcessor.getParameters(opcode, arguments);
                    if (parameters != null) {
                        while (arguments.size() < parameters.size()) {
                            arguments.add(arg("0"));
                        }
                        content.add(new InstructionExpression(opcode, arguments.subList(0, parameters.size()), parameters));
                    } else {
                        content.add(error("Unrecognized instruction type of opcode '" + tokens.getFirst() + "'."));
                    }
                } else {
                    content.add(error("Unrecognized opcode '" + tokens.getFirst() + "'."));
                }
            }
        }
    }

    private MlogVariable arg(String mlog) {
        return new MlogVariable(mlog);
    }

    private @Nullable String parseString() {
        int from = pos;

        while (++pos < mlog.length()) {
            var c = mlog.charAt(pos);
            if (c == '\n' || c == '\r' || c == '"') {
                break;
            }
        }

        if (pos >= mlog.length() || mlog.charAt(pos) != '"') {
            return null;
        } else {
            return mlog.substring(from, ++pos);
        }
    }

    private String parseToken() {
        int from = pos;

        while (pos < mlog.length()) {
            char c = mlog.charAt(pos);
            if (c == '\n' || c == '\r' || c == ' ' || c == '#' || c == '\t' || c == ';') break;
            pos++;
        }

        return mlog.substring(from, pos);
    }

    private final Set<String> usedVariables = new HashSet<>();
    private final Map<String, String> variables = new HashMap<>();

    private String convert(String id) {
        if (id.isEmpty()) return id;
        return switch (id.charAt(0)) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@', '%' -> id;
            default -> variables.computeIfAbsent(id, this::normalize);
        };
    }

    private String normalize(String id) {
        if (id.startsWith(".") || id.startsWith(":")) id = id.substring(1);

        char[] chars = id.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch != '_' && (ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9')) {
                chars[i] = '_';
            }
        }
        String normalized = new String(chars);
        if (usedVariables.add(normalized)) return normalized;

        int index = 1;
        while (!usedVariables.add(normalized + index)) index++;
        return normalized + index;
    }
}
