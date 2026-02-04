package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.Set;

@NullMarked
public record DominatorTree(
        Map<ControlFlowNode, Set<ControlFlowNode>> dom,
        Map<ControlFlowNode, ControlFlowNode> idom,
        Map<ControlFlowNode, Set<ControlFlowNode>> tree) {
}
