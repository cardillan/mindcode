package info.teksol.mindcode.ast;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.stream.Collectors;

public class AstPrettyPrinter extends BaseAstVisitor<String> {
    private final InstructionProcessor instructionProcessor;
    private final StringBuilder buffer = new StringBuilder();

    public AstPrettyPrinter(InstructionProcessor instructionProcessor) {
        super(m -> {});
        this.instructionProcessor = instructionProcessor;
    }

    public String prettyPrint(AstNode node) {
        visit(node);
        return buffer.toString();
    }

    @Override
    public String visitRef(Ref node) {
        buffer.append("@");
        buffer.append(node.getName());
        return null;
    }

    @Override
    public String visitRequirement(Requirement node) {
        buffer.append("require ");
        if (node.isLibrary()) {
            buffer.append(node.getFile());
        } else {
            buffer.append('"').append(node.getFile()).append('"');
        }
        return null;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        buffer.append("if ");
        visit(node.getCondition());
        buffer.append("\n");
        visit(node.getTrueBranch());
        buffer.append("\nelse\n");
        visit(node.getFalseBranch());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitIterator(Iterator node) {
        if (node.isOutModifier()) {
            buffer.append("out ");
        }
        visit(node.getVarRef());
        return null;
    }

    @Override
    public String visitHeapAccess(HeapAccess node) {
        buffer.append(node.getCellName());
        buffer.append("[");
        visit(node.getAddress());
        buffer.append("]");
        return null;
    }

    @Override
    public String visitControl(Control node) {
        visit(node.getTarget());
        buffer.append(".");
        buffer.append(node.getProperty());
        switch (node.getArguments().size()) {
            case 0:
                throw new MindcodeInternalError("Unexpected Control AST node with no parameters");

            case 1:
                buffer.append(" = ");
                visit(node.getArguments().get(0));
                break;

            default:
                buffer.append("(");
                for (int i = 0; i < node.getArguments().size(); i++) {
                    if (i > 0) buffer.append(", ");
                    visit(node.getArguments().get(i));
                }
                buffer.append(")");
                break;
        }
        return null;
    }

    @Override
    public String visitConstant(Constant node) {
        buffer.append("const ").append(node.getName()).append(" = ");
        visit(node.getValue());
        return null;
    }

    @Override
    public String visitParameter(ProgramParameter node) {
        buffer.append("param ").append(node.getName()).append(" = ");
        visit(node.getValue());
        return null;
    }

    @Override
    public String visitDirective(Directive node) {
        buffer.append("#set ").append(node.getOption()).append(" = ").append(node.getValue());
        return null;
    }

    @Override
    public String visitDirectiveText(DirectiveText node) {
        return null;
    }

    @Override
    public String visitForEachStatement(ForEachExpression node) {
        if (node.getLabel() != null) {
            buffer.append(node.getLabel()).append(": ");
        }
        buffer.append("for ").append(
                node.getIterators().stream().map(Object::toString).collect(Collectors.joining(", "))
        ).append(" in (");
        if (!node.getValues().isEmpty()) {
            visit(node.getValues().get(0));
            for (int i = 1; i < node.getValues().size(); i++) {
                buffer.append(", ").append(node.getValues().get(i));
            }
        }
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitRangedForExpression(RangedForExpression node) {
        if (node.getLabel() != null) {
            buffer.append(node.getLabel()).append(": ");
        }
        buffer.append("for ").append(node.getVariable()).append(" in ");
        buffer.append(node.getRange().getFirstValue());
        buffer.append(node.getRange().operator());
        buffer.append(node.getRange().getLastValue());
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitWhileStatement(WhileExpression node) {
        visit(node.getInitialization());
        if (node.getLabel() != null) {
            buffer.append(node.getLabel()).append(": ");
        }
        buffer.append("while ");
        visit(node.getCondition());
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitDoWhileStatement(DoWhileExpression node) {
        buffer.append("do \n");
        visit(node.getBody());
        buffer.append("\nloop while ");
        visit(node.getCondition());
        buffer.append("\n");
        return null;
    }

    @Override
    public String visitVarRef(VarRef node) {
        buffer.append(node.getName());
        return null;
    }

    @Override
    public String visitAssignment(Assignment node) {
        visit(node.getVar());
        buffer.append(" = ");
        visit(node.getValue());
        return null;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        buffer.append(node.getOp());
        visit(node.getExpression());
        return null;
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        buffer.append('"');
        buffer.append(node.getText());
        buffer.append('"');
        return null;
    }

    @Override
    public String visitFormattableLiteral(FormattableLiteral node) {
        buffer.append('$').append('"');
        buffer.append(node.getText());
        buffer.append('"');
        return null;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        try {
            buffer.append(node.toLogicLiteral(instructionProcessor).toMlog());
        } catch (NumericLiteral.NoValidMlogRepresentationException e) {
            buffer.append(e.getLiteral());
            buffer.append(" <no valid mlog representation>");
        }
        return null;
    }

    @Override
    public String visitNumericValue(NumericValue node) {
        buffer.append(node.toLogicLiteral(instructionProcessor).toMlog());
        return null;
    }

    @Override
    public String visitNullLiteral(NullLiteral node) {
        buffer.append("null");
        return null;
    }

    @Override
    public String visitNoOp(NoOp node) {
        return null;
    }

    @Override
    public String visitFunctionArgument(FunctionArgument node) {
        if (node.hasExpression()) {
            if (node.hasInModifier()) buffer.append("in ");
            if (node.hasOutModifier()) buffer.append("out ");
            visit(node.getExpression());
        } else {
            buffer.append(" ");
        }
        return null;
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        buffer.append(node.getFunctionName());
        buffer.append("(");
        for (int i = 0; i < node.getArguments().size() - 1; i++) {
            visit(node.getArguments().get(i));
            buffer.append(", ");
        }
        if (!node.getArguments().isEmpty()) {
            visit(node.getArguments().get(node.getArguments().size() - 1));
        }
        buffer.append(")");
        return null;
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteral node) {
        buffer.append(node.getValue());
        return null;
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        visit(node.getLeft());
        buffer.append(" ");
        buffer.append(node.getOp());
        buffer.append(" ");
        visit(node.getRight());
        return null;
    }

    @Override
    public String visitBoolBinaryOp(BoolBinaryOp node) {
        return visitBinaryOp(node);
    }

    @Override
    public String visitSeq(Seq seq) {
        visit(seq.getRest());
        buffer.append("\n");
        visit(seq.getLast());
        buffer.append("\n");
        return null;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        visit(node.getTarget());
        buffer.append(".");
        buffer.append(node.getProperty());
        return null;
    }

    @Override
    public String visitCaseExpression(CaseExpression node) {
        buffer.append("case ");
        visit(node.getCondition());
        buffer.append("\n");
        for (int i = 0; i < node.getAlternatives().size(); i++) {
            buffer.append("when ");
            visit(node.getAlternatives().get(i).getValues().get(0));
            for (int j = 1; j < node.getAlternatives().get(i).getValues().size(); j++) {
                buffer.append(", ");
                visit(node.getAlternatives().get(i).getValues().get(j));
            }
            buffer.append("\n");
            visit(node.getAlternatives().get(i).getBody());
        }
        buffer.append("else\n");
        visit(node.getElseBranch());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitCaseAlternative(CaseAlternative node) {
        // Case alternatives are handled in visitCaseExpression
        return null;
    }

    @Override
    public String visitRange(Range node) {
        // Ranges are handled elsewhere
        return null;
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        buffer.append("def ");
        buffer.append(node.getName());
        if (!node.getParams().isEmpty()) {
            buffer.append("(");
            for (int i = 0; i < node.getParams().size() - 1; i++) {
                visit(node.getParams().get(i));
                buffer.append(", ");
            }
            if (!node.getParams().isEmpty()) {
                visit(node.getParams().get(node.getParams().size() - 1));
            }
            buffer.append(")");
        }
        buffer.append("\n");
        visit(node.getBody());
        buffer.append("end\n");
        return null;
    }

    @Override
    public String visitFunctionParameter(FunctionParameter node) {
        buffer.append(node.hasInModifier() ? "in " : "")
                .append(node.hasOutModifier() ? "out " : "")
                .append(node.getName());
        return null;
    }

    @Override
    public String visitHeapAllocation(HeapAllocation node) {
        buffer.append("allocate heap in ");
        buffer.append(node.getName());
        buffer.append("[");
        buffer.append(node.getRange().getFirstValue());
        buffer.append(node.getRange().operator());
        buffer.append(node.getRange().getLastValue());
        buffer.append("]");
        return null;
    }

    @Override
    public String visitStackAllocation(StackAllocation node) {
        buffer.append("allocate stack in ");
        buffer.append(node.getStack());
        buffer.append("[");
        buffer.append(node.getRange().getFirstValue());
        buffer.append(node.getRange().operator());
        buffer.append(node.getRange().getLastValue());
        buffer.append("]");
        return null;
    }

    @Override
    public String visitBreakStatement(BreakStatement node) {
        buffer.append("break");
        if (node.getLabel() != null) {
            buffer.append(' ').append(node.getLabel());
        }
        return null;
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        buffer.append("continue");
        if (node.getLabel() != null) {
            buffer.append(' ').append(node.getLabel());
        }
        return null;
    }

    @Override
    public String visitReturnStatement(ReturnStatement node) {
        buffer.append("return ");
        visit(node.getRetval());
        return null;
    }

    @Override
    public String visitVoidLiteral(VoidLiteral node) {
        buffer.append("void");
        return null;
    }
}
