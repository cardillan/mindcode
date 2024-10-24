package info.teksol.mindcode.ast;

import java.util.List;

public class AstIndentedPrinter extends BaseAstVisitor<String> {
    private static final String SINGLE_INDENT = "    ";
    private final StringBuilder buffer = new StringBuilder();
    private final int level;
    private String newLine = "\n";

    private AstIndentedPrinter(int level) {
        super(m -> {});
        this.level = level;
    }

    protected void close(String text) {
        newLine = newLine.substring(0, newLine.length() - SINGLE_INDENT.length());
        buffer.append(newLine).append(text);
    }

    protected void newLine(String text) {
        buffer.append(text).append(newLine);
    }

    protected void open(String text) {
        newLine = newLine + SINGLE_INDENT;
        buffer.append(text).append(newLine);
    }

    protected void print(Object object) {
        buffer.append(object);
    }

    private String printIndentedInternal(AstNode node) {
        visit(node);
        return buffer.toString();
    }

    protected void visit(String text) {
        if (text == null) {
            buffer.append("null");
        } else {
            buffer.append('\'').append(text).append('\'');
        }
    }

    @Override
    public String visitAssignment(Assignment node) {
        open("Assignment{");
        print("var="); visit(node.getVar()); newLine(",");
        print("value="); visit(node.getValue());
        close("}");
        return null;
    }

    @Override
    public String visitBinaryOp(BinaryOp node) {
        open("BinaryOp{");
        print("left="); visit(node.getLeft()); newLine(",");
        print("op="); visit(node.getOp()); newLine(",");
        print("right="); visit(node.getRight());
        close("}");
        return null;
    }

    @Override
    public String visitBoolBinaryOp(BoolBinaryOp node) {
        open("BoolBinaryOp{");
        print("left="); visit(node.getLeft()); newLine(",");
        print("op="); visit(node.getOp()); newLine(",");
        print("right="); visit(node.getRight());
        close("}");
        return null;
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteral node) {
        print(node);
        return null;
    }

    @Override
    public String visitBreakStatement(BreakStatement node) {
        print(node);
        return null;
    }

    @Override
    public String visitCaseAlternative(CaseAlternative node) {
        open("CaseAlternative{");
        print("values="); visitList(node.getValues()); newLine(",");
        print("body="); visit(node.getBody());
        close("}");
        return null;
    }

    @Override
    public String visitCaseExpression(CaseExpression node) {
        open("CaseExpression{");
        print("condition="); visit(node.getCondition()); newLine(",");
        print("alternatives="); visitList(node.getAlternatives()); newLine(",");
        print("elseBranch="); visit(node.getElseBranch());
        close("}");
        return null;
    }

    @Override
    public String visitConstant(Constant node) {
        open("Constant{");
        print("name="); visit(node.getName()); newLine(",");
        print("value="); visit(node.getValue());
        close("}");
        return null;
    }

    @Override
    public String visitContinueStatement(ContinueStatement node) {
        print(node);
        return null;
    }

    @Override
    public String visitControl(Control node) {
        open("Control{");
        print("target="); visit(node.getTarget()); newLine(",");
        print("property="); visit(node.getProperty()); newLine(",");
        print("params="); visitList(node.getParams());
        close("}");
        return null;
    }

    @Override
    public String visitDirective(Directive node) {
        open("Directive{");
        print("name='"); visit(node.getOption()); newLine("',");
        print("value='"); visit(node.getValue());
        close("'}");
        return null;
    }

    @Override
    public String visitDoWhileStatement(DoWhileExpression node) {
        open("DoWhileExpression{");
        print("label="); visit(node.getLabel()); newLine(",");
        print("body="); visit(node.getBody()); newLine(",");
        print("condition="); visit(node.getCondition());
        close("}");
        return null;
    }

    @Override
    public String visitForEachStatement(ForEachExpression node) {
        open("ForEachExpression{");
        print("label="); visit(node.getLabel()); newLine(",");
        print("iterators="); visitList(node.getIterators()); newLine(",");
        print("values="); visitList(node.getValues()); newLine(",");
        print("body="); visit(node.getBody());
        close("}");
        return null;
    }

    @Override
    public String visitFormattableLiteral(FormattableLiteral node) {
        print(node);
        return null;
    }

    @Override
    public String visitFunctionCall(FunctionCall node) {
        open("FunctionCall{");
        print("functionName="); visit(node.getFunctionName()); newLine(",");
        print("params="); visitList(node.getParams());
        close("}");
        return null;
    }

    @Override
    public String visitFunctionDeclaration(FunctionDeclaration node) {
        open("FunctionDeclaration{");
        print("name="); visit(node.getName()); newLine(",");
        print("params="); visitList(node.getParams()); newLine(",");
        print("body="); visit(node.getBody());
        close("}");
        return null;
    }

    @Override
    public String visitFunctionParameter(FunctionParameter node) {
        print(node);
        return null;
    }

    @Override
    public String visitHeapAccess(HeapAccess node) {
        open("HeapAccess{");
        print("cellName="); visit(node.getCellName()); newLine(",");
        print("address="); visit(node.getAddress());
        close("}");
        return null;
    }

    @Override
    public String visitHeapAllocation(HeapAllocation node) {
        open("HeapAllocation{");
        print("name="); visit(node.getName()); newLine(",");
        print("range="); visit(node.getRange());
        close("}");
        return null;
    }

    @Override
    public String visitIfExpression(IfExpression node) {
        open("IfExpression{");
        print("condition="); visit(node.getCondition()); newLine(",");
        print("trueBranch="); visit(node.getTrueBranch()); newLine(",");
        print("falseBranch="); visit(node.getFalseBranch());
        close("}");
        return null;
    }

    @Override
    public String visitIterator(Iterator node) {
        print(node);
        return null;
    }

    protected void visitList(List<? extends AstNode> nodes) {
        if (nodes.size() == 1) {
            print("["); visit(nodes.get(0)); print("]");
        } else {
            open("[");
            for (int i = 0; i < nodes.size(); i++) {
                visit(nodes.get(i));
                if (i < nodes.size() - 1) {
                    newLine(",");
                }
            }
            close("]");
        }
    }

    @Override
    public String visitNoOp(NoOp node) {
        print(node);
        return null;
    }

    @Override
    public String visitNullLiteral(NullLiteral node) {
        print(node);
        return null;
    }

    @Override
    public String visitNumericLiteral(NumericLiteral node) {
        print(node);
        return null;
    }

    @Override
    public String visitNumericValue(NumericValue node) {
        print(node);
        return null;
    }

    @Override
    public String visitParameter(ProgramParameter node) {
        open("Parameter{");
        print("name="); visit(node.getName()); newLine(",");
        print("value="); visit(node.getValue());
        close("}");
        return null;
    }

    @Override
    public String visitPropertyAccess(PropertyAccess node) {
        open("PropertyAccess{");
        print("target="); visit(node.getTarget()); newLine(",");
        print("property="); visit(node.getProperty());
        close("}");
        return null;
    }

    @Override
    public String visitRange(Range node) {
        print(node);
        return null;
    }

    @Override
    public String visitRangedForExpression(RangedForExpression node) {
        open("RangedForExpression{");
        print("label="); visit(node.getLabel()); newLine(",");
        print("variable="); visit(node.getVariable()); newLine(",");
        print("range="); visit(node.getRange()); newLine(",");
        print("body="); visit(node.getBody());
        close("}");
        return null;
    }

    @Override
    public String visitRef(Ref node) {
        print(node);
        return null;
    }

    @Override
    public String visitReturnStatement(ReturnStatement node) {
        open("ReturnStatement{");
        print("fnRetVal="); visit(node.getRetval());
        close("}");
        return null;
    }

    @Override
    public String visitSeq(Seq node) {
        if (node.getRest() instanceof NoOp) {
            print("Seq{rest="); visit(node.getRest()); print(", last="); visit(node.getLast()); print("}");
        } else {
            if (node.getRest() instanceof Seq && level <= 1) {
                // Linearize nested Seq
                open("Seq[");
                visitSeqInner((Seq)node.getRest());
                visit(node.getLast());
                close("]");
            } else {
                open("Seq{");
                print("rest="); visit(node.getRest()); newLine(",");
                print("last="); visit(node.getLast());
                close("}");
            }
        }
        return null;
    }

    private void visitSeqInner(Seq node) {
        if (node.getRest() instanceof Seq seq) {
            visitSeqInner(seq);
        }
        visit(node.getLast()); newLine(",");
    }

    @Override
    public String visitStackAllocation(StackAllocation node) {
        open("StackAllocation{");
        print("name="); visit(node.getStack().toMlog()); newLine(",");
        print("range="); visit(node.getRange());
        close("}");
        return null;
    }

    @Override
    public String visitStringLiteral(StringLiteral node) {
        print(node);
        return null;
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        open("UnaryOp{");
        print("op="); visit(node.getOp()); newLine(",");
        print("expression="); visit(node.getExpression());
        close("}");
        return null;
    }

    @Override
    public String visitVarRef(VarRef node) {
        print(node);
        return null;
    }

    @Override
    public String visitWhileStatement(WhileExpression node) {
        open("WhileExpression{");
        print("label="); visit(node.getLabel()); newLine(",");
        print("initialization="); visit(node.getInitialization()); newLine(",");
        print("condition="); visit(node.getCondition()); newLine(",");
        print("body="); visit(node.getBody());
        close("}");
        return null;
    }

    public static String printIndented(AstNode node, int level) {
        return new AstIndentedPrinter(level).printIndentedInternal(node);
    }
}
