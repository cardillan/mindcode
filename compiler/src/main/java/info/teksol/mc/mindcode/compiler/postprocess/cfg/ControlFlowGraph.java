package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import java.util.List;
import java.util.Map;

public record ControlFlowGraph(
        List<ControlFlowNode> nodes,
        Map<Integer, ControlFlowNode> nodeMap,
        List<ControlFlowNode> entryPoints) {

    public static final ControlFlowGraph EMPTY = new ControlFlowGraph(List.of(), Map.of(), List.of());
}
