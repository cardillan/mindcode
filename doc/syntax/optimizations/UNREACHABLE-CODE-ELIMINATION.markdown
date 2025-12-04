# Unreachable Code Elimination

This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might appear:

1. Jump Threading can create unreachable jumps that are no longer targeted.
2. User-created unreachable regions, such as `while false ... end`, or a code following a `while true` loop.
3. User-defined functions which are called from an unreachable region.

Instruction removal is done by analyzing the control flow of the program and removing instructions that are never executed. Since the analysis is static, it may miss sections of code unreachable due to the dynamic behavior of the program.

---

[&#xAB; Previous: Temp Variables Elimination](TEMP-VARIABLES-ELIMINATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown)
