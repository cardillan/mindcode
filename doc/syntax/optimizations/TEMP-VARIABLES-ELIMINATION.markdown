# Temp Variables Elimination

The compiler sometimes creates temporary variables whose only function is to carry some value to another instruction. This optimization removes such temporary variables that only carry the value to an adjacent instruction. The `set` instruction is removed, while the adjacent instruction is updated to replace the temporary variable with the other variable used in the `set` instruction.

The optimization is performed only when the following conditions are met:

* The `set` instruction assigns/reads a temporary variable.
* The temporary variable is used in exactly one other instruction, adjacent to the `set` instruction.
* All arguments of the other instruction referencing the temporary variable are either input ones (the `set` instruction precedes the other instruction) or output ones (the `set` instruction follows the other instruction).

An additional optimization is performed when an instruction has a temporary output variable which isn't read by any other instruction. In this case, the unused output variable is replaced by `0` (literal zero value). Such an instruction will be executed correctly by Mindustry Logic, but a new variable will be allocated for the replaced argument.

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables are removed by the [Stack Optimization](STACK-OPTIMIZATION.markdown) down the line.

---

[&#xAB; Previous: Stack Optimization](STACK-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Unreachable Code Elimination &#xBB;](UNREACHABLE-CODE-ELIMINATION.markdown)
