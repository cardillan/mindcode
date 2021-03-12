package info.teksol.mindcode;

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

            default:
                throw new MindustryConverterException("Don't know how to convert " + opcode);
        }
    }

    private static void printSensor(MOpcode opcode, StringBuilder buffer) {
        buffer.append("sensor ");
        buffer.append(opcode.getArgs().get(0));
        buffer.append(" ");
        buffer.append(opcode.getArgs().get(1));
        buffer.append(" ");
        buffer.append(opcode.getArgs().get(2));
        buffer.append("\n");
    }

    private static void printEnd(MOpcode opcode, StringBuilder buffer) {
        buffer.append("end\n");
    }

    private static void printPrint(MOpcode opcode, StringBuilder buffer) {
        buffer.append("print ");
        buffer.append(opcode.getArgs().get(0));
        buffer.append("\n");
    }

    private static void printJump(MOpcode opcode, StringBuilder buffer) {
        buffer.append("jump ");
        printUpToFourArgs(opcode, buffer);
    }

    private static void printOp(MOpcode opcode, StringBuilder buffer) {
        buffer.append("op ");
        printUpToFourArgs(opcode, buffer);
    }

    private static void printSet(MOpcode opcode, StringBuilder buffer) {
        buffer.append("set ");
        buffer.append(opcode.getArgs().get(0));
        buffer.append(" ");
        buffer.append(opcode.getArgs().get(1));
        buffer.append("\n");
    }

    private static void printUpToFourArgs(MOpcode opcode, StringBuilder buffer) {
        switch (opcode.getArgs().size()) {
            case 4:
                buffer.append(opcode.getArgs().get(0));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(1));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(2));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(3));
                buffer.append("\n");
                break;

            case 3:
                buffer.append(opcode.getArgs().get(0));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(1));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(2));
                buffer.append(" 0\n");
                break;

            case 2:
                buffer.append(opcode.getArgs().get(0));
                buffer.append(" ");
                buffer.append(opcode.getArgs().get(1));
                buffer.append(" 0 0\n");
                break;

            case 1:
                buffer.append(opcode.getArgs().get(0));
                buffer.append(" 0 0 0\n");
                break;

            default:
                throw new MindustryConverterException("Don't know how to convert op with argument count not in 1..4: " + opcode);
        }
    }
}
