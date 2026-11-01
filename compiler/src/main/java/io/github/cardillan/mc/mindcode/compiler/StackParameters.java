package io.github.cardillan.mc.mindcode.compiler;

import io.github.cardillan.mc.mindcode.compiler.callgraph.MindcodeFunction;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record StackParameters(MindcodeFunction function, List<LogicVariable> variables, int depth, boolean fixedDepth,
                              int frameSize, int returnOffset, int additionalSize) {

    public int totalSize() {
        return depth * frameSize + additionalSize;
    }
}
