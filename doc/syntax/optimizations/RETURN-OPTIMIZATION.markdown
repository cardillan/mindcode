# Return Optimization

Return Optimization is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

The Return Optimization is simple: whenever there's an unconditional jump to the final sequence of instructions representing a return from the call (which is always three instructions long), the jump is replaced by the entire return sequence. The jump execution is avoided at the price of two additional instructions.

The impact of this optimization is probably marginal. Recursive functions are of limited use by themselves, and this optimization only applies in a rather specific context.

---

[&#xAB; Previous: Print Merging](PRINT-MERGING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Single Step Elimination &#xBB;](SINGLE-STEP-ELIMINATION.markdown)
