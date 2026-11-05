package io.github.cardillan.mindcode.compiler.generation.builders;

import io.github.cardillan.mindcode.compiler.MindcodeInternalError;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstEnhancedComment;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstExpression;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstFunctionCall;
import io.github.cardillan.mindcode.compiler.ast.nodes.AstReturnStatement;
import io.github.cardillan.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mindcode.compiler.generation.AbstractCodeBuilder;
import io.github.cardillan.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mindcode.compiler.generation.ReturnStack;
import io.github.cardillan.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mindcode.generated.ast.visitors.AstEnhancedCommentVisitor;
import io.github.cardillan.mindcode.generated.ast.visitors.AstFunctionCallVisitor;
import io.github.cardillan.mindcode.generated.ast.visitors.AstReturnStatementVisitor;
import io.github.cardillan.mindcode.logic.arguments.LogicNumber;
import io.github.cardillan.mindcode.logic.arguments.LogicVariable;
import io.github.cardillan.mindcode.logic.arguments.LogicVoid;
import io.github.cardillan.mindcode.messages.ERR;
import io.github.cardillan.mindcode.messages.WARN;
import io.github.cardillan.mindcode.util.Lazy;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static io.github.cardillan.mindcode.messages.ERR.FUNCTION_CALL_WRONG_NUMBER_OF_ARGS;

@NullMarked
public class FunctionCallsBuilder extends AbstractCodeBuilder implements
        AstEnhancedCommentVisitor<ValueStore>,
        AstFunctionCallVisitor<ValueStore>,
        AstReturnStatementVisitor<ValueStore>
{
    private final Lazy<BuiltinFunctionAssertsBuilder> assertsBuilder;
    private final Lazy<BuiltinFunctionVarargsBuilder> varargsBuilder;
    private final Lazy<BuiltinFunctionMlogBuilder> mlogBuilder;
    private final Lazy<BuiltinFunctionTextOutputBuilder> textBuilder;
    private final Lazy<StandardFunctionCallsBuilder> callBuilder;
    private final Map<String, Function<AstFunctionCall, ValueStore>> builtinHandlers;

    public FunctionCallsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
        assertsBuilder = new Lazy<>(() -> new BuiltinFunctionAssertsBuilder(this));
        varargsBuilder = new Lazy<>(() -> new BuiltinFunctionVarargsBuilder(this));
        mlogBuilder = new Lazy<>(() -> new BuiltinFunctionMlogBuilder(this));
        textBuilder = new Lazy<>(() -> new BuiltinFunctionTextOutputBuilder(this));
        callBuilder = new Lazy<>(() -> new StandardFunctionCallsBuilder(this));
        builtinHandlers = createBuiltinFunctionHandlers();
    }

    @Override
    public ValueStore visitEnhancedComment(AstEnhancedComment comment) {
        textBuilder.get().handleEnhancedComment(comment);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitFunctionCall(AstFunctionCall call) {
        return handleCall(call, false);
    }

    @Override
    public ValueStore visitReturnStatement(AstReturnStatement node) {
        ReturnStack.ReturnRecord returnRecord = returnStack.getReturnRecord();
        if (returnRecord == null) {
            error(node, ERR.RETURN_OUTSIDE_FUNCTION);
            return LogicVoid.VOID;
        }

        if (returnRecord.value() instanceof LogicVariable) {
            if (node.getReturnValue() == null) {
                error(node, ERR.RETURN_MISSING_VALUE);
            } else {
                final ValueStore expression = evaluate(node.getReturnValue());
                if (expression == LogicVoid.VOID) {
                    warn(node.getReturnValue(), WARN.VOID_RETURN);
                }
                returnRecord.value().setValue(assembler, expression.getValue(assembler));
            }
            assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
            assembler.createJumpUnconditional(returnRecord.label());
            assembler.clearSubcontextType();
        } else if (returnRecord.value() instanceof LogicVoid) {
            if (node.getReturnValue() != null) {
                error(node.getReturnValue(), ERR.RETURN_VALUE_IN_VOID_FUNCTION);
                // Process the expression anyway to locate errors in it
                evaluate(node.getReturnValue());
            }
            assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
            assembler.createJumpUnconditional(returnRecord.label());
            assembler.clearSubcontextType();
        } else {
            throw new MindcodeInternalError("Unexpected function return value holder " + returnRecord.value());
        }
        return LogicVoid.VOID;
    }

    private ValueStore handleCall(AstFunctionCall call, boolean async) {
        if (call.hasObject()) {
            // Method call
            return callBuilder.get().handleCall(call, async);
        } else {
            // Function call: try built-in functions first
            Function<AstFunctionCall, ValueStore> handler = builtinHandlers.get(call.getFunctionName());
            if (handler != null) {
                if (async) {
                    error(call, ERR.FUNCTION_CALL_ASYNC_UNSUPPORTED, call.getFunctionName());
                }
                return handler.apply(call);
            } else {
                return callBuilder.get().handleCall(call, async);
            }
        }
    }

    private ValueStore handleAsync(AstFunctionCall call) {
        if (call.getArguments().size() != 1) {
            error(call, FUNCTION_CALL_WRONG_NUMBER_OF_ARGS,
                    call.getFunctionName(), 1, call.getArguments().size());
            return LogicNumber.ZERO;
        }

        AstExpression innerCall = Objects.requireNonNull(call.getArgument(0).getExpression());

        if (innerCall instanceof AstFunctionCall asyncCall) {
            handleCall(asyncCall, true);
        } else {
            error(call, ERR.ASYNC_WRONG_ARGUMENT, call.getFunctionName());
            evaluate(innerCall);        // Report syntax errors here
        }

        return LogicVoid.VOID;
    }

    private Map<String, Function<AstFunctionCall, ValueStore>> createBuiltinFunctionHandlers() {
        Map<String, Function<AstFunctionCall, ValueStore>> map = new HashMap<>();

        map.put("assert",           call -> assertsBuilder.get().handleAssert(call));
        map.put("assertEquals",     call -> assertsBuilder.get().handleAssertEquals(call));
        map.put("assertPrints",     call -> assertsBuilder.get().handleAssertPrints(call));
        map.put("assertType",       call -> assertsBuilder.get().handleAssertType(call));

        map.put("async",            this::handleAsync);
        map.put("await",            call -> callBuilder.get().handleAwait(call));
        map.put("finished",         call -> callBuilder.get().handleFinished(call));
        map.put("verifySignature",  call -> callBuilder.get().handleVerifySignature(call));

        map.put("length",           call -> varargsBuilder.get().handleLength(call));
        map.put("min",              call -> varargsBuilder.get().handleMinMax(call));
        map.put("max",              call -> varargsBuilder.get().handleMinMax(call));

        map.put("mlog",             call -> mlogBuilder.get().handleMlog(call, false, false));
        map.put("mlogSafe",         call -> mlogBuilder.get().handleMlog(call, true, false));
        map.put("mlogText",         call -> mlogBuilder.get().handleMlog(call, false, true));

        map.put("ascii",            call -> textBuilder.get().handleAscii(call));
        map.put("char",             call -> textBuilder.get().handleChar(call));
        map.put("emitLog",          call -> textBuilder.get().handleEmitLog(call));
        map.put("encode",           call -> textBuilder.get().handleEncode(call));
        map.put("error",            call -> textBuilder.get().handleError(call));
        map.put("printf",           call -> textBuilder.get().handlePrintf(call));
        map.put("print",            call -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.PRINT));
        map.put("println",          call -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.PRINTLN));
        map.put("remark",           call -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.REMARK));
        map.put("strlen",           call -> textBuilder.get().handleStrlen(call));
        return map;
    }
}
