# Single Step Elimination

This optimizer simplifies the following sequences of jumps that are a result of the code generation and various optimizations:

* A conditional or unconditional jump targeting the next instruction.
* A conditional or unconditional jump is removed if there is an identical jump immediately following it. The second jump may be a target of another jump.
* The `end` and `jump 0 always` instructions at the very end of the program are removed, as the processor will jump to the first instruction of the program upon reaching the end of the instruction list anyway, saving execution of one instruction.
* A jump is removed if there is an identical jump preceding it, and these conditions are met:
    * the jump doesn't contain volatile variables,
    * there are no instructions affecting control flow between the jumps, including landing points of other jumps,
    * there are no instructions modifying the jump condition variables between the jumps.

The rationale behind the optimization described in the last point is that if any of the removed conditional jumps conditions were evaluated to `true`, so would be the condition of the first jump in the sequence, so the other jumps cannot fire, even though the value of the condition isn't known at the compile time. These sequences of jumps may appear as a result of unrolled loops.

Note: this optimization does not affect jumps that are part of a larger structure (e.g., jump tables).

---

[&#xAB; Previous: Return Optimization](RETURN-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Stack Optimization &#xBB;](STACK-OPTIMIZATION.markdown)
