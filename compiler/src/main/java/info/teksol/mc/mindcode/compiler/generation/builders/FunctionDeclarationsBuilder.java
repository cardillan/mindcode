package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicValue;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FunctionDeclarationsBuilder extends AbstractBuilder {
    public FunctionDeclarationsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    public void compileFunction(MindcodeFunction function) {
        if (!function.isUsed() && function.getSourcePosition().isLibrary()) return;

        if (function.isInline()) {
            if (function.isUsed()) {
                // This function has been compiled elsewhere
                return;
            }

            generateCodeForFunction(function.prepareInlinedForCall(processor.nextFunctionPrefix()));
        } else {
            generateCodeForFunction(function);
        }
    }

    private void generateCodeForFunction(MindcodeFunction function) {
        enterFunction(function, List.of());
        assembler.setActive(function.isUsed());
        // TODO: replace getPlacementCount() with proper weight computation
        assembler.enterFunctionAstNode(function, function.getDeclaration(), function.getPlacementCount());
        if (function.getLabel() != null) {
            assembler.createLabel(function.getLabel());
        }

        final LogicValue returnValue = function.isVoid() ? LogicVoid.VOID : LogicVariable.fnRetVal(function);
        returnStack.enterFunction(processor.nextLabel(), returnValue);

        if (function.isRecursive()) {
            appendRecursiveFunctionDeclaration(function);
        } else {
            appendStacklessFunctionDeclaration(function);
        }
        assembler.createCompilerEnd();

        returnStack.exitFunction();
        assembler.exitAstNode(function.getDeclaration());
        assembler.setActive(true);
        exitFunction(function);
    }

    private void appendRecursiveFunctionDeclaration(MindcodeFunction function) {
        ValueStore valueStore = function.isVoid()
                ? visitBody(function.getBody())
                : evaluateBody(function.getBody());

        if (!function.isVoid()) {
            assembler.createSet(LogicVariable.fnRetVal(function),  valueStore.getValue(assembler));
        }

        assembler.createLabel(returnStack.getReturnRecord().label());
        assembler.createReturn(context.stackTracker().getStackMemory());
    }

    private void appendStacklessFunctionDeclaration(MindcodeFunction function) {
        ValueStore valueStore = function.isVoid()
                ? visitBody(function.getBody())
                : evaluateBody(function.getBody());

        if (!function.isVoid()) {
            assembler.createSet(LogicVariable.fnRetVal(function),  valueStore.getValue(assembler));
        }

        assembler.createLabel(returnStack.getReturnRecord().label());

        // TODO (STACKLESS_CALL) We no longer need to track relationship between return from the stackless call and callee
        //      Use GOTO_OFFSET for list iterator, drop marker from GOTO and target simple labels
        String functionPrefix = function.getPrefix();
        assembler.createGoto(LogicVariable.fnRetAddr(functionPrefix), LogicLabel.symbolic(functionPrefix));
    }
}
