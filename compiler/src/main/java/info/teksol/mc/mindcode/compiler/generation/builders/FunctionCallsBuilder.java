package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.AstEnhancedCommentVisitor;
import info.teksol.mc.generated.ast.visitors.AstFunctionCallVisitor;
import info.teksol.mc.generated.ast.visitors.AstReturnStatementVisitor;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstReturnStatement;
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
            error(node, "Return statement outside of a function.");
            return LogicVoid.VOID;
        }

        if (returnRecord.value() instanceof LogicVariable target) {
            if (node.getReturnValue() == null) {
                error(node, "Missing return value in 'return' statement.");
            } else {
                final ValueStore expression = evaluate(node.getReturnValue());
                if (expression == LogicVoid.VOID) {
                    warn(node.getReturnValue(), "Expression doesn't have any value. Using value-less expressions in return statements is deprecated.");
                }
                returnRecord.value().setValue(assembler, expression.getValue(assembler));
            }
            assembler.createJumpUnconditional(returnRecord.label());
        } else if (returnRecord.value() instanceof LogicVoid) {
            if (node.getReturnValue() != null) {
                error(node, "Cannot return a value from a 'void' function.");
                // Process the expression anyway to locate errors in it
                evaluate(node.getReturnValue());
            }
            assembler.createJumpUnconditional(returnRecord.label());
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