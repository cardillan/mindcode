# Stack Optimization

Optimizes the stack usage -- eliminates `push`/`pop` instruction pairs determined to be unnecessary. The following optimizations are performed:

* `push`/`pop` instruction elimination for function variables that are not used anywhere apart from the push/pop instructions. This happens when variables are eliminated by other optimizations. The optimization is done globally, in a single pass across the entire program.
* `push`/`pop` instruction elimination for function variables that are read neither by any instruction between the call instruction and the end of the function nor by any instruction that is part of the same loop as the call instruction. Implicit reads by recursive calls to the same function with the value of the parameter unchanged are
  also detected.
* `push`/`pop` instruction elimination for function parameters/variables that are never modified within the function.

---

[&#xAB; Previous: Single Step Elimination](SINGLE-STEP-ELIMINATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Temp Variables Elimination &#xBB;](TEMP-VARIABLES-ELIMINATION.markdown)
