package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public interface LogicInstruction {

    Opcode getOpcode();

    List<String> getArgs();

    String getArg(int index);

    String getMarker();

    boolean matchesMarker(String marker);
}
