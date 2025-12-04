# Code Optimization

## Static and dynamic optimizations

Most of the optimizations Mindcode performs are fairly simple: they're relatively small changes to the code that are known to improve the code size, the (average) execution time, or both, while also not making either of these two metrics worse. We call these optimizations _static optimizations_, and they're always applied. A few of the optimizers are capable of performing more complex optimizations that may increase the code size to achieve a better performance or sacrifice some performance to decrease code size. These optimizations are called _dynamic optimizations_.

Mindcode provides the [`goal` option](SYNTAX-5-OTHER.markdown#option-goal) to specify which dynamic optimizations to apply:

* `speed` (the default value): Mindcode applies dynamic optimizations that make the resulting code larger, but faster, while adhering to the current [instruction limit](SYNTAX-5-OTHER.markdown#option-instruction-limit). When several possible optimizations of this kind are available, the ones having the best effect (the highest speedup per additional instruction generated) are applied first until the instruction limit is reached.
* `neutral`: Mindcode applies dynamic optimizations improving at least one of the aspects (code size, performance, or both) while not making the other aspect worse.
* `size`: Mindcode applies dynamic optimizations leading to the smallest code possible, even at the expense of execution speed.

The `goal` option's scope is local, and it is possible to set the desired goal for individual functions, statements, or blocks of code (see [Option scopes](SYNTAX-5-OTHER.markdown#option-scope)). As a consequence, dynamic optimizations matching two or even all three goals may be available. The optimizer performs all possible `size` optimizations first, then all possible `neutral` optimizations, and finally all possible `speed` optimizations.

### Optimization efficiency

To decide which dynamic optimizations to apply given the optimization goal, three metrics are computed for each possible optimization:

* **Size**: the number of additional instructions that will be created by the optimization. When the optimization reduces the number of instructions, the **Size** is negative.
* **Benefit**: a measure of execution improvement achieved by the optimization. The **Benefit** is negative when the optimization results in a slower code execution.
* **Efficiency**: this measure gives the overall value of the optimization, which allows it to be compared with other optimizations. The **Efficiency** computation depends on the optimization goal:
  * `speed`: the **Efficiency** of the optimization is computed by dividing the **Benefit** by the **Size**, providing a measure of execution speed improvement per additional instruction of code. When the **Size** is zero or negative, the **Efficiency** is infinite.
  * `neutral`: the **Efficiency** of the optimization is computed as a negative product of the **Size** and the **Benefit**. As **Size** is guaranteed to be non-positive, and **Benefit** is guaranteed to be non-negative, the **Efficiency** is always positive.
  * `size`: in this case, the optimizer always chooses the smallest code, so the **Efficiency** equals to the negative **Size** (i.e., code size savings) of the optimization.  

### Optimization benefit

Calculating the benefit is a key part of the optimization evaluation. Increasing or decreasing the execution speed of code which gets executed a lot provides more benefit or drawback than similar change to a code that is executed just once. Mindcode therefore assigns each instruction a _weight_, a measure of how often the instruction is expected to be executed. In general, it is impossible to compute this number precisely. Mindcode uses a straightforward algorithm to determine instruction weights:

* At the beginning of code generation, the current weight is established:
  * one; for the main program,
  * the number of places a function is called from for out-of-line or recursive functions.
* The current weight is assigned to each generated instruction.
* When entering a branch of an `if` statement, the current weight is halved. It is restored when exiting the branch. This corresponds to a very simplistic expectation that the condition will be evaluated to true 50% of the time, and therefore a branch in the `if` statement will be executed only half as often as the surrounding code.
* When entering a `when` branch of a `case` expression, the weight is divided by the number of branches in the expression. The reasoning (and inaccuracy) is analogous to the `if` statement approximation described above.
* When entering a loop body, the weight is multiplied by the number of loop's iterations. When the number of iterations cannot be determined at compile time, 25 is used instead.
* The weight of stackless and recursive functions is adjusted:
  * Stackless function weights are iteratively recomputed as a total weight of the respective `CALL` instructions of the given function.
  * Recursive function weights are then computed as a total weight of the respective `CALLREC` instructions of the given function. No iterative updating is made for recursive functions.
* When instructions are duplicated or moved during optimizations, their weights are adjusted according to the context into which they were duplicated or moved.

The benefit of an optimization is then computed as the total weight of instructions that would be avoided thanks to the optimization. The net result is that Mindcode strongly prefers optimizing code inside loops, and defers optimizations inside the branches of `if` and `case` statements.

### Optimization for speed

When the optimization goal is `speed`, there is a constraint on the total code size (1000 instructions by default). The optimizations are applied in the order of effectiveness until the total code size reaches the constraint, or all optimization opportunities are exhausted.

Some optimizers (e.g., [Case Switching](optimizations/CASE-SWITCHING.markdown)) can produce several different ways to optimize the same portion of code, of which one needs to be selected. For the `speed` optimization goal, the entire group of mutually exclusive optimizations is evaluated against other possible optimizations, choosing the most effective optimization while respecting the total code size constraint. 

Oftentimes, after an optimization is applied, opportunities for further optimizations crop up. The additional static optimizations might even reduce the code size again, providing space for further speed optimizations. Mindcode is unable to include the effects of these static optimizations when computing dynamic optimization effects, but uses the additional instruction space when applying any remaining optimizations.     

Mindcode writes the possible optimizations it considers to the log file. For better understanding, this includes optimizations exceeding the total code size constraint by a small margin. It is possible to let Mindcode perform additional optimizations by changing the value of the total code size constraint, using the  [`instruction-limit` option](SYNTAX-5-OTHER.markdown#option-instruction-limit). Sometimes, increasing the instruction limit a bit can produce a code which still fits into the total code size constraint, due to the optimizer being too pessimistic about the code size required to perform an optimization. Here's a [demonstration](https://github.com/cardillan/mindcode/discussions/106) of this approach being applied to a real-life code example.  

### Optimization for size and neutral optimization

These two optimization goals do not increase code size and therefore aren't subject to the total code size constraint. In these cases, all possible optimizations are eventually applied. If there's a group of mutually exclusive optimizations, the best one according to the optimization goal is always selected. 

## Individual Mindcode optimizations

The information on compiler optimizations is a bit technical. It might be useful if you're trying to better understand how Mindcode generates the mlog code.

Individual optimizations are described in separate documents:

* [Array Optimization](optimizations/ARRAY-OPTIMIZATION.markdown): finding optimal array-access implementation for internal arrays.
* [Boolean Optimization](optimizations/BOOLEAN-OPTIMIZATION.markdown): simplifying boolean expressions and/or implementing them using the `select` instruction.
* [Case Expression Optimization](optimizations/CASE-EXPRESSION-OPTIMIZATION.markdown): eliminating temporary variables created to execute case expressions.
* [Case Switching](optimizations/CASE-SWITCHING.markdown): modifying suitable case expressions to use jump tables or value translations.
* [Condition Optimization](optimizations/CONDITION-OPTIMIZATION.markdown): merging an op instruction producing a boolean expression into the following conditional jump.
* [Data Flow Optimization](optimizations/DATA-FLOW-OPTIMIZATION.markdown): improving variable assignments and expressions, analyzing data flow for other optimizations.
* [Dead Code Elimination](optimizations/DEAD-CODE-ELIMINATION.markdown): eliminating writes to compiler- or user-defined variables that are not used.
* [Expression Optimization](optimizations/EXPRESSION-OPTIMIZATION.markdown): optimizing some common mathematical expressions.
* [Function Inlining](optimizations/FUNCTION-INLINING.markdown): inlining stackless function calls.
* [If Expression Optimization](optimizations/IF-EXPRESSION-OPTIMIZATION.markdown): improving ternary/if expressions.
* [Jump Normalization](optimizations/JUMP-NORMALIZATION.markdown): replacing always true conditional jumps with unconditional ones, removing always false jumps.
* [Jump Straightening](optimizations/JUMP-STRAIGHTENING.markdown): simplifying sequences of intertwined jumps.
* [Jump Threading](optimizations/JUMP-THREADING.markdown): eliminating chained jumps.
* [Loop Hoisting](optimizations/LOOP-HOISTING.markdown): moving invariant code out of loops.
* [Loop Optimization](optimizations/LOOP-OPTIMIZATION.markdown): improving loop conditions.
* [Loop Unrolling](optimizations/LOOP-UNROLLING.markdown): unrolling loops with a fixed number of iterations.
* [Print Merging](optimizations/PRINT-MERGING.markdown): merging consecutive print statements outputting constant values.
* [Return Optimization](optimizations/RETURN-OPTIMIZATION.markdown): speeding up return statements in recursive and stackless functions.
* [Single Step Elimination](optimizations/SINGLE-STEP-ELIMINATION.markdown): eliminating jumps to the next instruction.
* [Stack Optimization](optimizations/STACK-OPTIMIZATION.markdown): optimizing variable storage on the stack.
* [Temp Variables Elimination](optimizations/TEMP-VARIABLES-ELIMINATION.markdown): eliminating temporary variables created to extract values from instructions.
* [Unreachable Code Elimination](optimizations/UNREACHABLE-CODE-ELIMINATION.markdown): eliminating instructions made unreachable by optimizations or false conditions.

---

[&#xAB; Previous: Compiler Options](SYNTAX-5-OTHER.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Extending Mindcode &#xBB;](SYNTAX-EXTENSIONS.markdown)
