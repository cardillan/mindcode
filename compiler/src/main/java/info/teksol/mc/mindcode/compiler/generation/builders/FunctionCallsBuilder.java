package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstEnhancedCommentVisitor;
import info.teksol.mc.generated.ast.visitors.AstFunctionCallVisitor;
import info.teksol.mc.generated.ast.visitors.AstReturnStatementVisitor;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstReturnStatement;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.ReturnStack;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.util.Lazy;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FunctionCallsBuilder extends AbstractBuilder implements
        AstEnhancedCommentVisitor<ValueStore>,
        AstFunctionCallVisitor<ValueStore>,
        AstReturnStatementVisitor<ValueStore>
{
    private final Lazy<BuiltinFunctionAssertsBuilder> assertsBuilder;
    private final Lazy<BuiltinFunctionVarargsBuilder> varargsBuilder;
    private final Lazy<BuiltinFunctionMlogBuilder> mlogBuilder;
    private final Lazy<BuiltinFunctionTextOutputBuilder> textBuilder;
    private final Lazy<StandardFunctionCallsBuilder> callBuilder;

    public FunctionCallsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
        assertsBuilder = new Lazy<>(() -> new BuiltinFunctionAssertsBuilder(this));
        varargsBuilder = new Lazy<>(() -> new BuiltinFunctionVarargsBuilder(this));
        mlogBuilder = new Lazy<>(() -> new BuiltinFunctionMlogBuilder(this));
        textBuilder = new Lazy<>(() -> new BuiltinFunctionTextOutputBuilder(this));
        callBuilder = new Lazy<>(() -> new StandardFunctionCallsBuilder(this));
    }

    @Override
    public ValueStore visitEnhancedComment(AstEnhancedComment comment) {
        textBuilder.get().handleEnhancedComment(comment);
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitFunctionCall(AstFunctionCall node) {
        if (node.hasObject()) {
            return handleMethodCall(node);
        } else {
            return handleFunctionCall(node);
        }
    }

    @Override
    public ValueStore visitReturnStatement(AstReturnStatement node) {
        ReturnStack.ReturnRecord returnRecord = returnStack.getReturnRecord();
        if (returnRecord == null) {
            error(node, ERR.RETURN_OUTSIDE_FUNCTION);
            return LogicVoid.VOID;
        }

        if (returnRecord.value() instanceof LogicVariable target) {
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

    private ValueStore handleFunctionCall(AstFunctionCall call) {
        // Solve special cases
        return switch (call.getFunctionName()) {
            case "assertEquals" -> assertsBuilder.get().handleAssertEquals(call);
            case "assertPrints" -> assertsBuilder.get().handleAssertPrints(call);
            case "length"       -> varargsBuilder.get().handleLength(call);
            case "min", "max"   -> varargsBuilder.get().handleMinMax(call);
            case "mlog"         -> mlogBuilder.get().handleMlog(call, false);
            case "mlogSafe"     -> mlogBuilder.get().handleMlog(call, true);
            case "ascii"        -> textBuilder.get().handleAscii(call);
            case "printf"       -> textBuilder.get().handlePrintf(call);
            case "print"        -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.PRINT);
            case "println"      -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.PRINTLN);
            case "remark"       -> textBuilder.get().handleTextOutput(call, BuiltinFunctionTextOutputBuilder.Formatter.REMARK);
            default             -> callBuilder.get().handleFunctionCall(call);
        };
    }

    private ValueStore handleMethodCall(AstFunctionCall call) {
        return callBuilder.get().handleMethodCall(call);
    }
}
