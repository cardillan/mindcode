package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

/// Convenience interface for instruction creation. To be implemented by classes which manage their AST contexts,
/// which is therefore not provided for individual instruction creation.
///
/// The implementing class only needs to implement the non-specific instruction creation method. All the specific
/// methods are inherited from the interface.
@NullMarked
public interface ContextfulInstructionCreator {

    LogicInstruction createInstruction(Opcode opcode, List<LogicArgument> arguments);

    default LogicInstruction createInstruction(Opcode opcode, LogicArgument... arguments) {
        return createInstruction(opcode, List.of(arguments));
    }

    default CallRecInstruction createCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr, LogicVariable returnValue) {
        return (CallRecInstruction) createInstruction(CALLREC, stack, callAddr, retAddr, returnValue);
    }

    default CallInstruction createCallStackless(LogicAddress address, LogicVariable retAddr, LogicVariable returnValue) {
        return (CallInstruction) createInstruction(CALL, address, retAddr, returnValue);
    }

    default CommentInstruction createComment(LogicArgument comment) {
        return (CommentInstruction) createInstruction(COMMENT, comment);
    }

    default CommentInstruction createComment(String comment) {
        return (CommentInstruction) createInstruction(COMMENT, LogicString.create(comment));
    }

    default ControlInstruction createControl(LogicKeyword property, LogicValue target, LogicValue value) {
        return (ControlInstruction) createInstruction(CONTROL, property, target, value);
    }

    default EndInstruction createEnd() {
        return (EndInstruction) createInstruction(END);
    }

    default FormatInstruction createFormat(LogicValue what) {
        return (FormatInstruction) createInstruction(FORMAT, what);
    }

    default GetlinkInstruction createGetLink(LogicVariable result, LogicValue index) {
        return (GetlinkInstruction) createInstruction(GETLINK, result, index);
    }

    default JumpInstruction createJump(LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        return (JumpInstruction) createInstruction(JUMP, target, condition, x, y);
    }

    default JumpInstruction createJumpUnconditional(LogicLabel target) {
        return (JumpInstruction) createInstruction(JUMP, target, Condition.ALWAYS);
    }

    default LabelInstruction createLabel(LogicLabel label) {
        return (LabelInstruction) createInstruction(LABEL, label);
    }

    default LookupInstruction createLookup(LogicKeyword type, LogicVariable result, LogicValue index) {
        return (LookupInstruction) createInstruction(LOOKUP, type, result, index);
    }

    default MultiCallInstruction createMultiCall(LogicLabel target, LogicVariable offset, LogicLabel marker) {
        return (MultiCallInstruction) createInstruction(MULTICALL, target, offset).setMarker(marker);
    }

    default MultiJumpInstruction createMultiJump(LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        return (MultiJumpInstruction) createInstruction(MULTIJUMP, target, value, offset).setMarker(marker);
    }

    default MultiJumpInstruction createMultiJump(LogicVariable target, LogicLabel marker) {
        return (MultiJumpInstruction) createInstruction(MULTIJUMP, target, LogicNumber.ZERO, LogicNumber.ZERO).setMarker(marker);
    }

    default MultiLabelInstruction createMultiLabel(LogicLabel label, LogicLabel marker) {
        return (MultiLabelInstruction) createInstruction(MULTILABEL, label).setMarker(marker);
    }

    default NoOpInstruction createNoOp() {
        return (NoOpInstruction) createInstruction(NOOP);
    }

    default OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first) {
        return (OpInstruction) createInstruction(OP, operation, target, first);
    }

    default OpInstruction createOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return (OpInstruction) createInstruction(OP, operation, target, first, second);
    }

    default PopInstruction createPop(LogicVariable stack, LogicVariable value) {
        return (PopInstruction) createInstruction(POP, stack, value);
    }

    default PrintInstruction createPrint(LogicValue what) {
        return (PrintInstruction) createInstruction(PRINT, what);
    }

    default PrintCharInstruction createPrintChar(LogicValue what) {
        return (PrintCharInstruction) createInstruction(PRINTCHAR, what);
    }

    default PrintflushInstruction createPrintflush(LogicVariable messageBlock) {
        return (PrintflushInstruction) createInstruction(PRINTFLUSH, messageBlock);
    }

    default PushInstruction createPush(LogicVariable stack, LogicVariable value) {
        return (PushInstruction) createInstruction(PUSH, stack, value);
    }

    default ReadInstruction createRead(LogicVariable result, LogicVariable memory, LogicValue index) {
        return (ReadInstruction) createInstruction(READ, result, memory, index);
    }

    default ReadArrInstruction createReadArr(LogicVariable result, LogicArray array, LogicValue index) {
        return (ReadArrInstruction) createInstruction(READARR, result, array, index);
    }

    default RemarkInstruction createRemark(LogicValue what) {
        return (RemarkInstruction) createInstruction(REMARK, what);
    }

    default ReturnInstruction createReturn(LogicVariable address) {
        return (ReturnInstruction) createInstruction(RETURN, address);
    }

    default ReturnRecInstruction createReturnRec(LogicVariable stack) {
        return (ReturnRecInstruction) createInstruction(RETURNREC, stack);
    }

    default SensorInstruction createSensor(LogicVariable result, LogicValue target, LogicValue property) {
        return (SensorInstruction) createInstruction(SENSOR, result, target, property);
    }

    default SetInstruction createSet(LogicVariable target, LogicValue value) {
        return (SetInstruction) createInstruction(SET, target, value);
    }

    default SetInstruction createSet(LogicBuiltIn target, LogicValue value) {
        return (SetInstruction) createInstruction(SET, target, value);
    }

    default SetAddressInstruction createSetAddress(LogicVariable variable, LogicLabel address) {
        return (SetAddressInstruction) createInstruction(SETADDR, variable, address);
    }

    default StopInstruction createStop() {
        return (StopInstruction) createInstruction(STOP);
    }

    default WriteInstruction createWrite(LogicValue value, LogicVariable memory, LogicValue index) {
        return (WriteInstruction) createInstruction(WRITE, value, memory, index);
    }

    default WriteArrInstruction createWriteArr(LogicValue value, LogicArray array, LogicValue index) {
        return (WriteArrInstruction) createInstruction(WRITEARR, value, array, index);
    }
}
