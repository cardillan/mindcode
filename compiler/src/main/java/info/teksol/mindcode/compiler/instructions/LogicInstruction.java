package info.teksol.mindcode.compiler.instructions;

import info.teksol.mindcode.logic.Opcode;

import java.util.List;

public interface LogicInstruction {
    boolean isWrite();

    boolean isPrint();

    boolean isPrintflush();

    boolean isJump();

    boolean isSet();

    boolean isOp();

    boolean isRead();

    boolean isUControl();

    boolean isLabel();

    boolean isGetlink();

    boolean isSensor();

    boolean isEnd();

    boolean isPush();

    boolean isPop();

    boolean isPushOrPop();

    boolean isCall();

    boolean isReturn();

    boolean isGoto();

    Opcode getOpcode();

    List<String> getArgs();

    String getArg(int index);

    String getMarker();

    boolean matchesMarker(String marker);

    default CallInstruction asCall() { return (CallInstruction) this; }

    default GotoInstruction asGoto() {
        return (GotoInstruction) this;
    }

    default JumpInstruction asJump() {
        return (JumpInstruction) this;
    }

    default LabelInstruction asLabel() {
        return (LabelInstruction) this;
    }

    default OpInstruction asOp() {
        return (OpInstruction) this;
    }

    default PrintInstruction asPrint() {
        return (PrintInstruction) this;
    }
    default PushOrPopInstruction asPushOrPop() {
        return (PushOrPopInstruction) this;
    }

    default ReadInstruction asRead() { return (ReadInstruction) this; }

    default ReturnInstruction asReturn() {
        return (ReturnInstruction) this;
    }

    default SetInstruction asSet() { return (SetInstruction) this; }

    default WriteInstruction asWrite() { return (WriteInstruction) this; }
}
