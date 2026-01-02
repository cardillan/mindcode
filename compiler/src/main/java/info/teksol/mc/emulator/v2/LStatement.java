package info.teksol.mc.emulator.v2;

import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> arguments() {
        return arguments;
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

    public String arg(int index) {
        return arguments.get(index);
    }


    public static LStatement create(MindustryMetadata metadata, String[] tokens, int tok) {
        String opcode = tokens[0];
        return metadata.getLogicStatementByOpcode(opcode).map(statement -> {
            List<String> defaults = statement.arguments();
            List<String> types = statement.argumentTypes();
            List<String> arguments = new ArrayList<>(defaults.size() + 1);

            for (int i = 1; i <= defaults.size(); i++) {
                String arg = i < tok ? tokens[i] : defaults.get(i - 1);
                String type = types.get(i - 1);
                boolean valid = switch (type) {
                    // Note: "int" can be a jump label as well. It's always parsed correctly.
                    // Special handling might be needed if int is added to instructions beside jump
                    case "int", "var", "boolean" -> true;
                    default -> metadata.getFileForClass(type)
                            .findAttribute(arg, opcode)
                            .filter("true"::equalsIgnoreCase).isPresent();
                };

                if (!valid) return new InvalidStatement();

                // Normalize booleans
                arguments.add("boolean".equals(type) ? String.valueOf(Boolean.parseBoolean(arg)) : arg);
            }

            return "jump".equals(opcode)
                    ? new JumpStatement(statement.privileged(), opcode, types, List.copyOf(arguments))
                    : new LStatement(statement.privileged(), opcode, types, List.copyOf(arguments));
        }).orElse(new InvalidStatement());
    }
}
