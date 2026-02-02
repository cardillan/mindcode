package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.*;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FunctionDeclarationsBuilder extends AbstractCodeBuilder {
    public FunctionDeclarationsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    private boolean shouldCompileFunction(MindcodeFunction function) {
        // Do not compile functions from modules included remotely
        // Unused functions are compiled to report syntax errors, except libraries
        // Do not compile inline functions that have been used
        return function.getModule().getRemoteProcessors().isEmpty()
               && (function.isUsed() || !function.getSourcePosition().isLibrary())
               && !(function.isInline() && function.isUsed());
    }

    public void generateFunctions() {
        while (true) {
            // Find functions that have been called but not generated
            List<MindcodeFunction> functions = callGraph.getFunctions().stream()
                    .filter(f -> shouldCompileFunction(f) && f.isCalled() && !f.isGenerated())
                    .toList();

            if (functions.isEmpty()) break;
            functions.forEach(this::compileFunction);
        }

        // Compile unused functions to check for syntax errors
        List<MindcodeFunction> functions = callGraph.getFunctions().stream()
                .filter(f -> shouldCompileFunction(f) && !f.isEvaluated() && !f.isGenerated())
                .toList();

        functions.forEach(this::compileFunction);
    }

    public void compileFunction(MindcodeFunction function) {
        function.setGenerated();
        if (function.isInline()) {
            generateCodeForFunction(function.prepareInlinedForCall(nameCreator));
        } else {
            generateCodeForFunction(function);
        }
    }

    /// Used to compile a function body when the function is called implicitly
    public void placeFunctionBody(MindcodeFunction function) {
        assembler.enterFunctionAstNode(function, function.getDeclaration(), function.getPlacementCount());
        function.setGenerated();
        enterFunction(function, List.of());
        returnStack.enterFunction(processor.nextLabel(), LogicVoid.VOID);
        compileFunctionBody(function);
        returnStack.exitFunction();
        exitFunction(function);
        assembler.exitAstNode(function.getDeclaration());
    }

    private void generateCodeForFunction(MindcodeFunction function) {
        enterFunction(function, List.of());
        assembler.setActive(function.isUsed() || function.isEntryPoint());
        // TODO: replace getPlacementCount() with proper weight computation
        assembler.enterFunctionAstNode(function, function.getDeclaration(), function.getPlacementCount());
        if (function.getLabel() != null && !function.isExport()) {
            assembler.createLabel(function.getLabel());
        }

        final LogicValue returnValue = function.isVoid() ? LogicVoid.VOID : function.getFnRetVal();
        returnStack.enterFunction(processor.nextLabel(), returnValue);

        if (function.isRecursive()) {
            appendRecursiveFunctionDeclaration(function);
        } else if (function.isExport()) {
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
        boolean atomic = function.isAtomic();
        assembler.enterAstNode(function.getDeclaration(), atomic ? AstContextType.ATOMIC : AstContextType.FUNCTION_BODY);
        if (function.getProfile().isSymbolicLabels()) {
            assembler.createComment("Function: " + function.getDeclaration().toSourceCode());
        }

        if (atomic) {
            assembler.createWait(LogicNumber.ZERO).setAtomicWait(true);
            assert function.getAtomicLabel() != null;
            assembler.createLabel(function.getAtomicLabel());
        }
        ValueStore valueStore = function.isVoid()
                ? visitBody(function.getBody())
                : evaluateBody(function.getBody());

        if (!function.isVoid()) {
            assembler.createSet(function.getFnRetVal(), valueStore.getValue(assembler));
        }

        assembler.createLabel(returnStack.getReturnLabel());
        assembler.exitAstNode(function.getDeclaration(), atomic ? AstContextType.ATOMIC : AstContextType.FUNCTION_BODY);
    }

    private void appendRecursiveFunctionDeclaration(MindcodeFunction function) {
        compileFunctionBody(function);
        assembler.createReturnRec(context.stackTracker().getStackMemory());
    }

    private void appendRemoteFunctionDeclaration(MindcodeFunction function) {
        assert function.getRemoteLabel() != null;
        assembler.createLabel(function.getRemoteLabel());

        // The placement count is increased by one for entrypoints.
        // As remote functions are entrypoints, placement counts over one indicate the function has been called locally.
        if (function.getPlacementCount() > 1) {
            if (context.globalCompilerProfile().isSymbolicLabels()) {
                assembler.createSet(function.getFnRetAddr(), LogicVariable.remoteWaitAddr());
            } else {
                assembler.createSetAddress(function.getFnRetAddr(), getRemoteWaitLabel());
            }

            assert function.getLabel() != null;
            assembler.createLabel(function.getLabel());
        }

        compileFunctionBody(function);

        assembler.createSet(function.getFnFinished(), LogicBoolean.TRUE);

        // Jump to remote wait
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        if (function.getPlacementCount() > 1) {
            assembler.createReturn(function.getFnRetAddr());
        } else {
            assembler.createJumpUnconditional(getRemoteWaitLabel());
        }
        assembler.clearSubcontextType();
    }

    private void appendStacklessFunctionDeclaration(MindcodeFunction function) {
        compileFunctionBody(function);
        assembler.createReturn(function.getFnRetAddr());
    }
}
