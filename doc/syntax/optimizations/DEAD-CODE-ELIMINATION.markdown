# Dead Code Elimination

This optimization inspects the entire code and removes all instructions that write to non-volatile variables if none of the variables written to are actually read anywhere in the code.

Dead Code Elimination also inspects your code and prints out suspicious variables:
* _Unused variables_: those are the variables that are unused and possibly were eliminated.
* _Uninitialized variables_: those are global variables that are read by the program but never written to. (The [Data Flow Optimization](DATA-FLOW-OPTIMIZATION.markdown) detects uninitialized local and function variables.)

Both cases deserve a closer inspection, as they might indicate a problem with your program.

---

[&#xAB; Previous: Data Flow Optimization](DATA-FLOW-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Expression Optimization &#xBB;](EXPRESSION-OPTIMIZATION.markdown)
