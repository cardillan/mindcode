package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

// Note: to is exclusive
public record Partition(int from, int to, LogicLabel majorityLabel) {
    public int size() {
        return to - from;
    }

    public boolean follows(Partition other) {
        return from == other.to;
    }
}
