package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class Indenter {
    private final String indent;
    private final List<String> indents  = new ArrayList<>(List.of(""));

    public Indenter(String indent) {
        this.indent = indent;
    }

    public String getIndent(int level) {
        if (level >= indents.size()) {
            for (int i = indents.size(); i <= level; i++) {
                indents.add(indents.get(i - 1) + indent);
            }
        }
        return indents.get(level);
    }
}
