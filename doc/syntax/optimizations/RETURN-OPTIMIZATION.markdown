# Return Optimization

Return Optimization is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

The Return Optimization replaces unconditional jumps to the final sequence of instructions representing a return from a recursive function (which is always three instructions long) with the entire return sequence. The jump execution is avoided at the price of two additional instructions.

---

[&#xAB; Previous: Print Merging](PRINT-MERGING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Single Step Elimination &#xBB;](SINGLE-STEP-ELIMINATION.markdown)
