package info.teksol.mc.mindcode.compiler.ast;

import info.teksol.annotations.BaseClass;
import info.teksol.mc.generated.ast.AstNodeVisitor;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
@BaseClass("AstIndentedPrinter")
public abstract class AbstractAstIndentedPrinter implements AstNodeVisitor<String> {
    private static final String SINGLE_INDENT = "    ";
    private static final String newLine = "\n";

    // Indentation cache
    private final List<String> indents = new ArrayList<>(List.of(""));
    private int indent = 0;

    private final StringBuilder buffer = new StringBuilder();

    public String print(AstMindcodeNode node) {
        buffer.setLength(0);
        visit(node);
        String result = buffer.toString();
        buffer.setLength(0);
        return result;
    }

    private void indentInc() {
        indent++;
        if (indent >= indents.size()) {
            indents.add(indents.get(indent - 1) + SINGLE_INDENT);
        }
    }

    private void indentDec() {
        if (indent > 0) indent--;
    }

    private String indent() {
        return indents.get(indent);
    }

    protected void newLine(String text) {
        buffer.append(text).append(newLine).append(indent());
    }

    protected void open(String text) {
        indentInc();
        buffer.append(text).append(newLine).append(indent());
    }

    protected void close(String text) {
        indentDec();
        buffer.append(newLine).append(indent()).append(text);
    }

    protected void print(String text) {
        buffer.append(text);
    }

    protected void printObject(@Nullable Object object) {
        switch (object) {
            case String string          -> buffer.append('"').append(string).append('"');
            case AstMindcodeNode node   -> visit(node);
            case List<?> list           -> printList(list);
            case null                   -> buffer.append("null");
            default                     -> buffer.append(object);
        }
    }

    protected void printList(List<?> list) {
        if (list.isEmpty()) {
            print("null");
        } else if (list.size() == 1) {
            printObject(list.getFirst());
        } else {
            open("[");
            printObject(list.getFirst());
            list.subList(1, list.size()).forEach(o -> {
                newLine(",");
                printObject(o);
            });
            close("]");
        }
    }
}
