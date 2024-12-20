package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.generator.AstContext;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstAllocation;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final @Nullable AstAllocation stackAllocation;
    private final @Nullable AstAllocation heapAllocation;
    private final CompileTimeEvaluator compileTimeEvaluator;
    private final FunctionMapper functionMapper;

    private final AstContext rootAstContext;

    // Stateful instances
    private final CallGraph callGraph;
    private final ReturnStack returnStack;
    private LoopStack loopStack;

    private AstContext astContext;
    private LogicFunction currentFunction;

    public CodeGenerator(CodeGeneratorContext context) {
        super(context.messageConsumer());
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        stackAllocation = context.stackAllocation();
        heapAllocation = context.heapAllocation();
        compileTimeEvaluator = context.compileTimeEvaluator();
        functionMapper = FunctionMapperFactory.getFunctionMapper(processor, () -> astContext, messageConsumer);

        rootAstContext = AstContext.createRootNode(profile);

        callGraph = context.callGraph();
        returnStack = new ReturnStack(messageConsumer);
        loopStack = new LoopStack(messageConsumer);

        astContext = rootAstContext;
        currentFunction = callGraph.getMain();
    }

    public CodeGeneratorOutput generateCode(AstProgram program) {
        verifyStackAllocation();
        return new CodeGeneratorOutput(List.of(), rootAstContext);
    }

    private void verifyStackAllocation() {
        if (stackAllocation == null) {
            callGraph.recursiveFunctions().filter(LogicFunction::isUsed).forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }
    }

    private void setSubcontextType(AstSubcontextType subcontextType, double multiplier) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(subcontextType, multiplier);
    }

    private void setSubcontextType(info.teksol.mindcode.compiler.generator.LogicFunction function, AstSubcontextType subcontextType) {
        if (astContext.node() != null && astContext.subcontextType() != astContext.node().getSubcontextType()) {
            clearSubcontextType();
        }
        astContext = astContext.createSubcontext(function, subcontextType, 1.0);
    }

    private void clearSubcontextType() {
        astContext = astContext.parent();
    }

}
