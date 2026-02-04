package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import java.util.List;

public record ControlFlowGraph(List<ControlFlowNode> nodes, List<ControlFlowNode> entryPoints) {
}
