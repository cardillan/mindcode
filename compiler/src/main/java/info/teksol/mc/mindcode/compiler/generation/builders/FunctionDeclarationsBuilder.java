package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionParameter;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
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
        // Do not compile functions from modules included remotely
        if (function.getModule().getRemoteProcessor() != null) return;

        // Unused function are compiled to report syntax errors, except libraries
        if (!function.isUsed() && function.getSourcePosition().isLibrary()) return;

        if (function.isInline()) {
            if (function.isUsed()) {
                // This function has been compiled elsewhere
                return;
            }

            generateCodeForFunction(function.prepareInlinedForCall(processor.nextFunctionPrefix(function)));
        } else {
            generateCodeForFunction(function);
        }
    }

    /// Used to compile function body when the function is called implicitly
    public void placeFunctionBody(MindcodeFunction function) {
        enterFunction(function, List.of());
        returnStack.enterFunction(processor.nextLabel(), LogicVoid.VOID);
        compileFunctionBody(function);
        returnStack.exitFunction();
        exitFunction(function);
    }

    private void generateCodeForFunction(MindcodeFunction function) {
        enterFunction(function, List.of());
        assembler.setActive(function.isUsed() || function.isEntryPoint());
        // TODO: replace getPlacementCount() with proper weight computation
        assembler.enterFunctionAstNode(function, function.getDeclaration(), function.getPlacementCount());
        if (function.getLabel() != null) {
            assembler.createLabel(function.getLabel());
        }

        final LogicValue returnValue = function.isVoid() ? LogicVoid.VOID : LogicVariable.fnRetVal(function);
        returnStack.enterFunction(processor.nextLabel(), returnValue);

        if (function.isRecursive()) {
            appendRecursiveFunctionDeclaration(function);
        } else if (function.isRemote()) {
            appendRemoteFunctionDeclaration(function);
        } else {
            appendStacklessFunctionDeclaration(function);
        }
        assembler.createCompilerEnd();

        returnStack.exitFunction();
        assembler.exitAstNode(function.getDeclaration());
        assembler.setActive(true);
        exitFunction(function);
    }

    private void compileFunctionBody(MindcodeFunction function) {
        ValueStore valueStore = function.isVoid()
                ? visitBody(function.getBody())
                : evaluateBody(function.getBody());

        if (!function.isVoid()) {
            assembler.createSet(LogicVariable.fnRetVal(function), valueStore.getValue(assembler));
        }

        assembler.createLabel(returnStack.getReturnLabel());
    }

    private void appendRecursiveFunctionDeclaration(MindcodeFunction function) {
        compileFunctionBody(function);
        assembler.createReturnRec(context.stackTracker().getStackMemory());
    }

    private void appendRemoteFunctionDeclaration(MindcodeFunction function) {
        compileFunctionBody(function);

        // Return value
        if (!function.isVoid()) {
            assembler.createWrite(LogicVariable.fnRetVal(function), LogicVariable.MAIN_PROCESSOR,
                    LogicVariable.fnRetVal(function).getMlogString());
        }

        // Output parameters
        function.getParameters().stream()
                .filter(FunctionParameter::isOutput)
                .map(LogicVariable.class::cast)
                .forEach(p -> assembler.createWrite(p, LogicVariable.MAIN_PROCESSOR,p.getMlogString()));

        // Finished flag
        assembler.createWrite(LogicBoolean.TRUE, LogicVariable.MAIN_PROCESSOR,
                LogicVariable.fnFinished(function).getMlogString());

        // Jump to remote wait
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        assembler.createJumpUnconditional(getRemoteWaitLabel());
        assembler.clearSubcontextType();
    }

    private void appendStacklessFunctionDeclaration(MindcodeFunction function) {
        compileFunctionBody(function);
        assembler.createReturn(LogicVariable.fnRetAddr(function.getPrefix()));
    }
}
