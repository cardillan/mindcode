# Jump Threading

If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected to the target of the second jump, repeated until the end of a jump chain is reached. Moreover:

* `end` instruction is handled identically to `jump 0 always`,
* conditional jumps in the jump chain are followed if:
    * their condition is identical to the condition of the first jump in the chain, and
    * the condition arguments do not contain a volatile variable (`@time`, `@tick`, `@counter` etc.),
* unconditional jumps targeting an indirect jump (i.e., an instruction assigning value to `@counter`) are replaced with the indirect jump itself,
* on the `experimental` level, when symbolic labels aren't used, the following function-call-related optimizations are also available:
    * the return address of a function call is redirected to the target of the following unconditional jump,
    * a conditional or unconditional jump to a function call is redirected directly to the function.

No instructions are directly removed or added, but the execution of the code is faster; furthermore, some jumps in the jump chain may be removed later by the [Unreachable Code Elimination](UNREACHABLE-CODE-ELIMINATION.markdown).

---

[&#xAB; Previous: Jump Straightening](JUMP-STRAIGHTENING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Loop Hoisting &#xBB;](LOOP-HOISTING.markdown)
