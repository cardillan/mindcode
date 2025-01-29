package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

/// Convenience interface for instruction creation. To be implemented by classes which don't manage AST contexts,
/// which must therefore be provided on every function call.
///
/// The implementing class only needs to implement the non-specific instruction creation method. All the specific
/// methods are inherited from the interface.
@NullMarked
public interface ContextlessInstructionCreator {

    LogicInstruction createInstruction(AstContext astContext, Opcode opcode, List<LogicArgument> arguments);

    default LogicInstruction createInstruction(AstContext astContext, Opcode opcode, LogicArgument... arguments) {
        return createInstruction(astContext, opcode, List.of(arguments));
    }

    default CallRecInstruction createCallRecursive(AstContext astContext, LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr, LogicVariable returnValue) {
        return (CallRecInstruction) createInstruction(astContext, CALLREC, stack, callAddr, retAddr, returnValue);
    }

    default CallInstruction createCallStackless(AstContext astContext, LogicAddress address, LogicVariable returnValue) {
        return (CallInstruction) createInstruction(astContext, CALL, address, returnValue);
    }

    default ControlInstruction createControl(AstContext astContext, LogicKeyword property, LogicValue target, LogicValue value) {
        return (ControlInstruction) createInstruction(astContext, CONTROL, property, target, value);
    }

    default EndInstruction createEnd(AstContext astContext) {
        return (EndInstruction) createInstruction(astContext, END);
    }

    default FormatInstruction createFormat(AstContext astContext, LogicValue what) {
        return (FormatInstruction) createInstruction(astContext, FORMAT, what);
    }

    default GetlinkInstruction createGetLink(AstContext astContext, LogicVariable result, LogicValue index) {
        return (GetlinkInstruction) createInstruction(astContext, GETLINK, result, index);
    }

    default JumpInstruction createJump(AstContext astContext, LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        return (JumpInstruction) createInstruction(astContext, JUMP, target, condition, x, y);
    }

    default JumpInstruction createJumpUnconditional(AstContext astContext, LogicLabel target) {
        return (JumpInstruction) createInstruction(astContext, JUMP, target, Condition.ALWAYS);
    }

    default LabelInstruction createLabel(AstContext astContext, LogicLabel label) {
        return (LabelInstruction) createInstruction(astContext, LABEL, label);
    }

    default LookupInstruction createLookup(AstContext astContext, LogicKeyword type, LogicVariable result, LogicValue index) {
        return (LookupInstruction) createInstruction(astContext, LOOKUP, type, result, index);
    }

    default MultiJumpInstruction createMultiJump(AstContext astContext, LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        return (MultiJumpInstruction) createInstruction(astContext, MULTIJUMP, target, value, offset, marker);
    }

    default MultiJumpInstruction createMultiJump(AstContext astContext, LogicVariable target, LogicLabel marker) {
        return (MultiJumpInstruction) createInstruction(astContext, MULTIJUMP, target, LogicNumber.ZERO, LogicNumber.ZERO, marker);
    }

    default MultiLabelInstruction createMultiLabel(AstContext astContext, LogicLabel label, LogicLabel marker) {
        return (MultiLabelInstruction) createInstruction(astContext, MULTILABEL, label, marker);
    }

    default NoOpInstruction createNoOp(AstContext astContext) {
        return (NoOpInstruction) createInstruction(astContext, NOOP);
    }

    default OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first) {
        return (OpInstruction) createInstruction(astContext, OP, operation, target, first);
    }

    default OpInstruction createOp(AstContext astContext, Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        return (OpInstruction) createInstruction(astContext, OP, operation, target, first, second);
    }

    default PopInstruction createPop(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return (PopInstruction) createInstruction(astContext, POP, memory, value);
    }

    default PrintInstruction createPrint(AstContext astContext, LogicValue what) {
        return (PrintInstruction) createInstruction(astContext, PRINT, what);
    }

    default PrintflushInstruction createPrintflush(AstContext astContext, LogicVariable messageBlock) {
        return (PrintflushInstruction) createInstruction(astContext, PRINTFLUSH, messageBlock);
    }

    default PushInstruction createPush(AstContext astContext, LogicVariable memory, LogicVariable value) {
        return (PushInstruction) createInstruction(astContext, PUSH, memory, value);
    }

    default ReadInstruction createRead(AstContext astContext, LogicVariable result, LogicVariable memory, LogicValue index) {
        return (ReadInstruction) createInstruction(astContext, READ, result, memory, index);
    }

    default ReadArrInstruction createReadArr(AstContext astContext, LogicVariable result, LogicArray array, LogicValue index) {
        return (ReadArrInstruction) createInstruction(astContext, READARR, result, array, index);
    }

    default RemarkInstruction createRemark(AstContext astContext, LogicValue what) {
        return (RemarkInstruction) createInstruction(astContext, REMARK, what);
    }

    default ReturnInstruction createReturn(AstContext astContext, LogicVariable address) {
        return (ReturnInstruction) createInstruction(astContext, RETURN, address);
    }

    default ReturnRecInstruction createReturnRec(AstContext astContext, LogicVariable stack) {
        return (ReturnRecInstruction) createInstruction(astContext, RETURNREC, stack);
    }

    default SensorInstruction createSensor(AstContext astContext, LogicVariable result, LogicValue target, LogicValue property) {
        return (SensorInstruction) createInstruction(astContext, SENSOR, result, target, property);
    }

    default SetInstruction createSet(AstContext astContext, LogicVariable target, LogicValue value) {
        return (SetInstruction) createInstruction(astContext, SET, target, value);
    }

    default SetInstruction createSet(AstContext astContext, LogicBuiltIn target, LogicValue value) {
        return (SetInstruction) createInstruction(astContext, SET, target, value);
    }

    default SetAddressInstruction createSetAddress(AstContext astContext, LogicVariable variable, LogicLabel address) {
        return (SetAddressInstruction) createInstruction(astContext, SETADDR, variable, address);
    }

    default StopInstruction createStop(AstContext astContext) {
        return (StopInstruction) createInstruction(astContext, STOP);
    }

    default WriteInstruction createWrite(AstContext astContext, LogicValue value, LogicVariable memory, LogicValue index) {
        return (WriteInstruction) createInstruction(astContext, WRITE, value, memory, index);
    }

    default WriteArrInstruction createWriteArr(AstContext astContext, LogicValue value, LogicArray array, LogicValue index) {
        return (WriteArrInstruction) createInstruction(astContext, WRITEARR, value, array, index);
    }
}
