# Jump Normalization

This optimization handles conditional jumps whose condition can be fully evaluated:

* constantly false conditional jumps are removed,
* constantly true conditional jumps are converted to unconditional ones.

A condition can be fully evaluated if both of its operands are literals or if they're variables whose values were determined to be constant by the [Data Flow Optimization](DATA-FLOW-OPTIMIZATION.markdown).

The first case reduces the code size and speeds up execution. The second one in itself improves neither size nor speed but allows those jumps to be handled by other optimizations aimed at unconditional jumps.

---

[&#xAB; Previous: Instruction Reordering](INSTRUCTION-REORDERING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Jump Straightening &#xBB;](JUMP-STRAIGHTENING.markdown)
