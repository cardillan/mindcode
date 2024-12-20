package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.compiler.instructions.LogicInstruction;
import info.teksol.mindcode.logic.*;

public class CodeBuilder {
    private final InstructionProcessor processor;
    private AstContext astContext;

    public CodeBuilder(CodeGeneratorContext context) {
        processor = context.instructionProcessor();
    }

    public void add(LogicInstruction instruction) {

    }

    public void addCallRecursive(LogicVariable stack, LogicLabel callAddr, LogicLabel retAddr, LogicVariable returnValue) {
        add(processor.createCallRecursive(astContext, stack, callAddr, retAddr, returnValue));
    }

    public void addCallStackless(LogicAddress address, LogicVariable returnValue) {
        add(processor.createCallStackless(astContext, address, returnValue));
    }

    public void addEnd(AstContext astContext) {
        add(processor.createEnd(astContext));
    }

    public void addFormat(LogicValue what) {
        add(processor.createFormat(astContext, what));
    }

    public void addGetLink(LogicVariable result, LogicValue index) {
        add(processor.createGetLink(astContext, result, index));
    }

    public void addGoto(LogicVariable address, LogicLabel marker) {
        add(processor.createGoto(astContext, address, marker));
    }

    public void addGotoLabel(LogicLabel label, LogicLabel marker) {
        add(processor.createGotoLabel(astContext, label, marker));
    }

    public void addGotoOffset(LogicLabel target, LogicVariable value, LogicNumber offset, LogicLabel marker) {
        add(processor.createGotoOffset(astContext, target, value, offset, marker));
    }

    public void addJump(LogicLabel target, Condition condition, LogicValue x, LogicValue y) {
        add(processor.createJump(astContext, target, condition, x, y));
    }

    public void addJumpUnconditional(LogicLabel target) {
        add(processor.createJumpUnconditional(astContext, target));
    }

    public void addLabel(LogicLabel label) {
        add(processor.createLabel(astContext, label));
    }

    public void addLookup(LogicKeyword type, LogicVariable result, LogicValue index) {
        add(processor.createLookup(astContext, type, result, index));
    }

    public void addNoOp(AstContext astContext) {
        add(processor.createNoOp(astContext));
    }

    public void addOp(Operation operation, LogicVariable target, LogicValue first) {
        add(processor.createOp(astContext, operation, target, first));
    }

    public void addOp(Operation operation, LogicVariable target, LogicValue first, LogicValue second) {
        add(processor.createOp(astContext, operation, target, first, second));
    }

    public void addPop(LogicVariable memory, LogicVariable value) {
        add(processor.createPop(astContext, memory, value));
    }

    public void addPrint(LogicValue what) {
        add(processor.createPrint(astContext, what));
    }

    public void addPrintflush(LogicVariable messageBlock) {
        add(processor.createPrintflush(astContext, messageBlock));
    }

    public void addPush(LogicVariable memory, LogicVariable value) {
        add(processor.createPush(astContext, memory, value));
    }

    public void addRead(LogicVariable result, LogicVariable memory, LogicValue index) {
        add(processor.createRead(astContext, result, memory, index));
    }

    public void addRemark(LogicValue what) {
        add(processor.createRemark(astContext, what));
    }

    public void addReturn(LogicVariable stack) {
        add(processor.createReturn(astContext, stack));
    }

    public void addSensor(LogicVariable result, LogicValue target, LogicValue property) {
        add(processor.createSensor(astContext, result, target, property));
    }

    public void addSet(LogicVariable target, LogicValue value) {
        add(processor.createSet(astContext, target, value));
    }

    public void addSet(LogicBuiltIn target, LogicValue value) {
        add(processor.createSet(astContext, target, value));
    }

    public void addSetAddress(LogicVariable variable, LogicLabel address) {
        add(processor.createSetAddress(astContext, variable, address));
    }

    public void addStop(AstContext astContext) {
        add(processor.createStop(astContext));
    }

    public void addWrite(LogicValue value, LogicVariable memory, LogicValue index) {
        add(processor.createWrite(astContext, value, memory, index));
    }
}
