package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class DominatorTreeBuilder {
    private List<ControlFlowNode> nodes;
    private ControlFlowNode entryPoint = new ControlFlowNode(0);

    public DominatorTreeBuilder() {
        // We assume it is a single entrypoint by default
        this.nodes = List.of(entryPoint);
    }

    public void setEntryPoint(ControlFlowNode entryPoint) {
        this.entryPoint = entryPoint;

        Set<ControlFlowNode> visited = new HashSet<>();
        Deque<ControlFlowNode> worklist = new ArrayDeque<>();

        visited.add(entryPoint);
        worklist.add(entryPoint);

        while (!worklist.isEmpty()) {
            ControlFlowNode n = worklist.pop();
            for (ControlFlowNode succ : n.successors) {
                if (visited.add(succ)) {
                    worklist.add(succ);
                }
            }
        }

        nodes = List.copyOf(visited);
    }

    public Map<ControlFlowNode, Set<ControlFlowNode>> computeDominators() {
        Map<ControlFlowNode, Set<ControlFlowNode>> dom = new HashMap<>();

        // Initialization
        for (ControlFlowNode n : nodes) {
            if (n == entryPoint) {
                dom.put(n, new HashSet<>(Set.of(entryPoint)));
            } else {
                dom.put(n, new HashSet<>(nodes));
            }
        }

        boolean changed;
        do {
            changed = false;

            for (ControlFlowNode n : nodes) {
                if (n == entryPoint) continue;

                Set<ControlFlowNode> newDom = new HashSet<>(nodes);

                for (ControlFlowNode p : n.predecessors) {
                    newDom.retainAll(dom.get(p));
                }

                newDom.add(n);

                if (!newDom.equals(dom.get(n))) {
                    dom.put(n, newDom);
                    changed = true;
                }
            }

        } while (changed);

        return dom;
    }

    public Map<ControlFlowNode, ControlFlowNode> computeImmediateDominators(Map<ControlFlowNode, Set<ControlFlowNode>> dom) {
        Map<ControlFlowNode, ControlFlowNode> idom = new HashMap<>();

        for (ControlFlowNode n : nodes) {
            if (n == entryPoint) continue;

            Set<ControlFlowNode> strictDominators = new HashSet<>(dom.get(n));
            strictDominators.remove(n);

            ControlFlowNode immediate = null;

            for (ControlFlowNode d : strictDominators) {
                boolean isImmediate = true;

                for (ControlFlowNode other : strictDominators) {
                    if (other != d && dom.get(other).contains(d)) {
                        isImmediate = false;
                        break;
                    }
                }

                if (isImmediate) {
                    immediate = d;
                    break;
                }
            }

            if (immediate == null) {
                throw new IllegalStateException("No immediate dominator for " + n);
            }

            idom.put(n, immediate);
        }

        return idom;
    }

    public Map<ControlFlowNode, Set<ControlFlowNode>> buildDominatorTree(Map<ControlFlowNode, ControlFlowNode> idom) {
        Map<ControlFlowNode, Set<ControlFlowNode>> tree = new HashMap<>();

        for (ControlFlowNode n : nodes) {
            tree.put(n, new HashSet<>());
        }

        for (Map.Entry<ControlFlowNode, ControlFlowNode> e : idom.entrySet()) {
            ControlFlowNode child = e.getKey();
            ControlFlowNode parent = e.getValue();
            tree.get(parent).add(child);
        }

        return tree;
    }
}
