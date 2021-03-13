package info.teksol.mindcode.mindustry;

import info.teksol.mindcode.GenerationException;

import java.util.List;

public class MOpcodePrinter {
    public static String toString(List<MOpcode> opcodes) {
        final StringBuilder buffer = new StringBuilder();
        opcodes.forEach((opcode) -> print(opcode, buffer));
        return buffer.toString();
    }

    private static void print(MOpcode opcode, StringBuilder buffer) {
        switch (opcode.getOpcode()) {
            case "set":
                printSet(opcode, buffer);
                break;

            case "op":
                printOp(opcode, buffer);
                break;

            case "jump":
                printJump(opcode, buffer);
                break;

            case "print":
                printPrint(opcode, buffer);
                break;

            case "end":
                printEnd(opcode, buffer);
                break;

            case "sensor":
                printSensor(opcode, buffer);
                break;

            case "control":
                printControl(opcode, buffer);
                break;

            case "write":
                printWrite(opcode, buffer);
                break;

            case "read":
                printRead(opcode, buffer);
                break;

            case "ubind":
                printUbind(opcode, buffer);
                break;

            case "ucontrol":
                printUcontrol(opcode, buffer);
                break;

            default:
                throw new GenerationException("Don't know how to convert " + opcode);
        }
    }

    private static void printUcontrol(MOpcode opcode, StringBuilder buffer) {
        // ucontrol move 14 15 0 0 0
        buffer.append("ucontrol ");
        addArgs(6, buffer, opcode);
    }

    private static void printUbind(MOpcode opcode, StringBuilder buffer) {
        buffer.append("ubind ");
        addArgs(1, buffer, opcode);
    }

    private static void printRead(MOpcode opcode, StringBuilder buffer) {
        buffer.append("read ");
        addArgs(3, buffer, opcode);
    }

    private static void printWrite(MOpcode opcode, StringBuilder buffer) {
        buffer.append("write ");
        addArgs(3, buffer, opcode);
    }

    private static void printControl(MOpcode opcode, StringBuilder buffer) {
        buffer.append("control ");
        addArgs(6, buffer, opcode);
    }

    private static void printSensor(MOpcode opcode, StringBuilder buffer) {
        buffer.append("sensor ");
        addArgs(3, buffer, opcode);
    }

    private static void printEnd(MOpcode opcode, StringBuilder buffer) {
        buffer.append("end");
        addArgs(0, buffer, opcode);
    }

    private static void printPrint(MOpcode opcode, StringBuilder buffer) {
        buffer.append("print ");
        addArgs(1, buffer, opcode);
    }

    private static void printJump(MOpcode opcode, StringBuilder buffer) {
        buffer.append("jump ");
        addArgs(4, buffer, opcode);
    }

    private static void printOp(MOpcode opcode, StringBuilder buffer) {
        buffer.append("op ");
        addArgs(4, buffer, opcode);
    }

    private static void printSet(MOpcode opcode, StringBuilder buffer) {
        buffer.append("set ");
        addArgs(2, buffer, opcode);
    }


    private static void addArgs(int count, StringBuilder buffer, MOpcode opcode) {
        for (int i = 0; i < count; i++) {
            if (opcode.getArgs().size() > i) {
                buffer.append(opcode.getArgs().get(i));
            } else {
                buffer.append("0");
            }

            buffer.append(" ");
        }
        buffer.append("\n");
    }
}
