package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.AstEnhancedCommentVisitor;
import info.teksol.generated.ast.visitors.AstFunctionCallVisitor;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstEnhancedComment;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import info.teksol.util.Lazy;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FunctionCallsBuilder extends AbstractBuilder implements
        AstFunctionCallVisitor<NodeValue>,
        AstEnhancedCommentVisitor<NodeValue>
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
    public NodeValue visitFunctionCall(AstFunctionCall node) {
        if (node.hasObject()) {
            return handleMethodCall(node);
        } else {
            return handleFunctionCall(node);
        }
    }

    @Override
    public NodeValue visitEnhancedComment(AstEnhancedComment comment) {
        textBuilder.get().handleEnhancedComment(comment);
        return LogicVoid.VOID;
    }

    private NodeValue handleFunctionCall(AstFunctionCall call) {
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

    private NodeValue handleMethodCall(AstFunctionCall node) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
