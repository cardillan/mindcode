package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;

@NullMarked
public record DominatorTree(
        Map<Integer, Set<ControlFlowNode>> dom,
        Map<Integer, ControlFlowNode> idom,
        Map<Integer, Set<ControlFlowNode>> tree) {
}
