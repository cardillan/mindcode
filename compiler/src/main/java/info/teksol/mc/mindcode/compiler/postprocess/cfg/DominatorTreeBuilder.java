package info.teksol.mc.mindcode.compiler.postprocess.cfg;

import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.stream.Collectors;

@NullMarked
public class DominatorTreeBuilder {
    private List<ControlFlowNode> nodes;
    private Map<Integer, ControlFlowNode> nodeMap;
    private ControlFlowNode entryPoint = new ControlFlowNode(0);

    public DominatorTreeBuilder() {
        // Empty graph
        this.nodes = List.of(entryPoint);
        this.nodeMap = Map.of(0, entryPoint);
    }

    public DominatorTree build(ControlFlowNode entryPoint) {
        setEntryPoint(entryPoint);
        Map<Integer, Set<ControlFlowNode>> dom = computeDominators();
        Map<Integer, ControlFlowNode> idom = computeImmediateDominators(dom);
        Map<Integer, Set<ControlFlowNode>> tree = buildDominatorTree(idom);
        return new DominatorTree(dom, idom, tree);
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
        nodeMap = nodes.stream().collect(Collectors.toMap(ControlFlowNode::getIndex, v -> v));
    }

    public Map<Integer, Set<ControlFlowNode>> computeDominators() {
        Map<Integer, Set<ControlFlowNode>> dom = new HashMap<>();

        // Initialization
        for (ControlFlowNode n : nodes) {
            if (n == entryPoint) {
                dom.put(n.index, new HashSet<>(Set.of(entryPoint)));
            } else {
                dom.put(n.index, new HashSet<>(nodes));
            }
        }

        boolean changed;
        do {
            changed = false;

            for (ControlFlowNode n : nodes) {
                if (n == entryPoint) continue;

                Set<ControlFlowNode> newDom = new HashSet<>(nodes);

                for (ControlFlowNode p : n.predecessors) {
                    newDom.retainAll(dom.get(p.index));
                }

                newDom.add(n);

                if (!newDom.equals(dom.get(n.index))) {
                    dom.put(n.index, newDom);
                    changed = true;
                }
            }

        } while (changed);

        return dom;
    }

    public Map<Integer, ControlFlowNode> computeImmediateDominators(Map<Integer, Set<ControlFlowNode>> dom) {
        Map<Integer, ControlFlowNode> idom = new HashMap<>();

        for (ControlFlowNode n : nodes) {
            if (n == entryPoint) continue;

            Set<ControlFlowNode> strictDominators = new HashSet<>(dom.get(n.index));
            strictDominators.remove(n);

            ControlFlowNode immediate = null;

            for (ControlFlowNode d : strictDominators) {
                boolean isImmediate = true;

                for (ControlFlowNode other : strictDominators) {
                    if (other != d && dom.get(other.index).contains(d)) {
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

            idom.put(n.index, immediate);
        }

        return idom;
    }

    public Map<Integer, Set<ControlFlowNode>> buildDominatorTree(Map<Integer, ControlFlowNode> idom) {
        Map<Integer, Set<ControlFlowNode>> tree = new HashMap<>();

        for (ControlFlowNode n : nodes) {
            tree.put(n.index, new HashSet<>());
        }

        for (Map.Entry<Integer, ControlFlowNode> e : idom.entrySet()) {
            Integer child = e.getKey();
            ControlFlowNode parent = e.getValue();
            tree.get(parent.index).add(nodeMap.get(child));
        }

        return tree;
    }
}
