package info.teksol.mindcode.mindustry;

import java.util.List;
import java.util.Objects;

public class MOpcode {
    private final String opcode;
    private final List<String> args;

    public MOpcode(String opcode) {
        this(opcode, List.of());
    }

    public MOpcode(String opcode, String... args) {
        this(opcode, List.of(args));
    }

    public MOpcode(String opcode, List<String> args) {
        this.opcode = opcode;
        this.args = args;
    }


    public String getOpcode() {
        return opcode;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MOpcode that = (MOpcode) o;
        return Objects.equals(opcode, that.opcode) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opcode, args);
    }

    @Override
    public String toString() {
        return "MOpcode{" +
                "opcode='" + opcode + '\'' +
                ", args=" + args +
                '}';
    }
}
