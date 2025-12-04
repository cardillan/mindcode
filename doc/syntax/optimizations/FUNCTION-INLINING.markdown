# Function Inlining

Function Inlining is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

## The fundamentals of function inlining

Function inlining converts out-of-line function calls into inline function calls. This conversion alone saves a few execution steps: storing the return address, jumping to the function body, and jumping back at the original address. However, additional optimizations might be available once a function is inlined, especially if the inlined function call is using constant argument values. In such a situation, many other powerful optimizations, such as constant folding or loop unrolling, may become available.

User-defined, non-recursive function which is called just once in the entire program is automatically inlined, and this cannot be prevented except by the `noinline` keyword: such code is always both faster and smaller. It is also possible to declare individual functions using the `inline` keyword, forcing all calls of such functions to be inlined.

## Automatic function inlining

This optimization can inline additional functions that aren't recursive and also aren't declared `inline` or `noinline` in the source code. If there's enough instruction space, all function calls may be inlined and the original function body removed from the program.

When there isn't enough instruction space, only a single one or several specific function calls may be inlined; in this case, the original function body remains in the program and is used by the function calls that weren't inlined. If there are only the last two function calls remaining, either both of them or none of them will be inlined.

It is therefore no longer necessary to use the `inline` keyword, except in cases when Mindcode's automatic inlining chooses function different from the one(s) you prefer to be inlined, or when using functions with variable number of parameters.

---

[&#xAB; Previous: Expression Optimization](EXPRESSION-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: If Expression Optimization &#xBB;](IF-EXPRESSION-OPTIMIZATION.markdown)
