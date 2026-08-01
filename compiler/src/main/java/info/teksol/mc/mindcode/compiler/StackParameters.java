package info.teksol.mc.mindcode.compiler;

import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record StackParameters(MindcodeFunction function, List<LogicVariable> variables, int depth, boolean fixedDepth,
                              int frameSize, int returnOffset, int additionalSize) {

    public int totalSize() {
        return depth * frameSize + additionalSize;
    }

    public StackParameters withDepth(int depth) {
        return new StackParameters(function, variables, depth, fixedDepth, frameSize, returnOffset, additionalSize);
    }
}
