package info.teksol.mc.emulator.mimex;

import info.teksol.mc.mindcode.logic.mimex.LogicStatement;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NullMarked
public class LStatement {
    private final boolean privileged;
    private final String opcode;
    private final List<String> types;
    private final List<String> arguments;

    public LStatement(boolean privileged, String opcode, List<String> types, List<String> arguments) {
        this.privileged = privileged;
        this.opcode = opcode;
        this.types = types;
        this.arguments = arguments;
    }

    public boolean privileged() {
        return privileged;
    }

    public String opcode() {
        return opcode;
    }

    public List<String> types() {
        return types;
    }

    public String type(int index) {
        return types.get(index);
    }

    public List<String> arguments() {
        return arguments;
    }

    public int args() {
        return arguments.size();
    }

    public String arg(int index) {
        return arguments.get(index);
    }

    public String arg0() {
        return arguments.getFirst();
    }

    public String arg1() {
        return arguments.get(1);
    }

    public String arg2() {
        return arguments.get(2);
    }

    public String arg3() {
        return arguments.get(3);
    }

    public String arg4() {
        return arguments.get(4);
    }

    public String arg5() {
        return arguments.get(5);
    }

    public String arg6() {
        return arguments.get(6);
    }

    public String arg7() {
        return arguments.get(7);
    }

    public String arg8() {
        return arguments.get(8);
    }

    public String arg9() {
        return arguments.get(9);
    }

    public static LStatement create(MindustryMetadata metadata, String[] tokens, int tok) {
        return metadata.getLogicStatementByOpcode(tokens[0])
                .or(() -> getBuiltinStatement(tokens[0]))
                .map(statement -> createStatement(metadata, tokens, tok, statement))
                .orElse(new InvalidStatement(tokens, tok));
    }

    private static Optional<? extends LogicStatement> getBuiltinStatement(String opcode) {
        return builtinStatements.stream().filter(s -> s.opcode().equals(opcode)).findFirst();
    }

    private static LStatement createStatement(MindustryMetadata metadata, String[] tokens, int tok, LogicStatement statement) {
        String opcode = tokens[0];
        List<String> defaults = statement.arguments();
        List<String> types = statement.argumentTypes();
        List<String> arguments = new ArrayList<>(defaults.size() + 1);

        for (int i = 1; i <= defaults.size(); i++) {
            String arg = i < tok ? tokens[i] : defaults.get(i - 1);
            String type = types.get(i - 1);
            boolean valid = switch (type) {
                // Note: "int" can be a jump label as well. It's always parsed correctly.
                // Special handling might be needed if int is added to instructions beside jump
                // @custom is a custom type, without checks
                case "int", "var", "boolean", "@custom" -> true;
                default -> metadata.getFileForClass(type)
                        .findAttribute(arg, opcode)
                        .filter("true"::equalsIgnoreCase).isPresent();
            };

            if (!valid) return new InvalidStatement(tokens, tok);

            // Normalize booleans
            arguments.add("boolean".equals(type) ? String.valueOf(Boolean.parseBoolean(arg)) : arg);
        }

        return "jump".equals(opcode)
                ? new JumpStatement(statement.privileged(), opcode, types, List.copyOf(arguments))
                : new LStatement(statement.privileged(), opcode, types, List.copyOf(arguments));
    }

    private static LogicStatement s(String opcode, String defaults, String types) {
        return new LogicStatement(opcode,
                splitCommas(defaults),
                splitCommas(types),
                List.of(),
                opcode,
                opcode,
                false,
                false,
                false,
                "",
                "",
                0.0,
                "");
    }

    private static final List<LogicStatement> builtinStatements = List.of(
            s("assertequals", "expected,actual,message", "var,var,var"),
            s("assertprints", "expected,message", "var,var"),
            s("assertflush", "", ""),
            s("assertBounds",
                    "multiple,multiple,min,lessThanEq,index,lessThanEq,max,error",
                    "@custom,var,var,@custom,var,@custom,var,var"
            ),
            s("error",
                    "null,null,null,null,null,null,null,null,null,null",
                    "var,var,var,var,var,var,var,var,var,var"
            )
    );

    private static List<String> splitCommas(String values) {
        return values.isBlank() ? List.of() : List.of(values.split(","));
    }

    @Override
    public String toString() {
        return opcode + " " + String.join(" ", arguments);
    }
}
