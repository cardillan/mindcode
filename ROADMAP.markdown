# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

## In progress

Larger changes being actively worked on.

 * [Schematics-creation tool](https://github.com/francois/mindcode/issues/90) 

## Planned

Things being pondered on from time to time.

### Language syntax

* Short-circuit boolean evaluation:
  * All logical and/or operators may or may not be short-circuited (i.e. `and`, `or`, `&&`, `||`). Avoid side
    effects, or place side effect call first.
  * In Mindustry Logic, invalid operations just do nothing; it is therefore not needed to use short-circuit to avoid,
    say, a null pointer exception or invalid arithmetic operation. It might be cheaper to just execute the invalid code.
    It would make sense to use short-circuit to avoid costly operations, such as function calls or complicated
    expressions. Short-circuiting will therefore be applied based on code size.
  * Code size needs to be known, will be either fully implemented in the optimizer, or will recompile parse tree
    applying hints learned during optimizations.
* Allow empty optional arguments in function calls. At this moment, optional arguments can only be omitted at the 
  end of the argument list. 
* `in` boolean operator: tests number is in range (e.g. `if n in min .. max then ...`)
* Varargs inline functions: the vararg can be processed using list iteration loop, or maybe passed to another vararg 
  function.
* `break` and `continue` in `case` expressions: `break` to exit the expression, `continue` to skip to the next `when`
  branch
* Ruby-like parallel assignments, e.g. `a, b, c = 1, 2, 3` or even `a, b = b, a`
  * Perhaps a way could be found to declare a function as returning several values and parallel-assign them (e.g. 
    `def func(x, out y, out z) return (x + 1, x - 1) end  a, b = func(7)`). The syntax looks just weird. What if we 
    don't need some of the return values? 
* Processor variables backed arrays (inevitably slow, but might be usable in actual code, especially with loop 
  unrolling).

### Code generation

* Determine effective variable types to assist with optimizations (globally at first).
* Temporary variable merging: after all optimizations, all temporary variables will be inspected for scope and
  variables with non-overlapping scopes will be merged into one, and renumbered starting from 0. Fewer temporary
  variables will make inspecting variables inside processors in Mindustry way easier.
* Function calls:
  * Circular variable assignment: when compiling recursive functions, circular assignments are sometimes generated
    (e.g. `__tmpX = a; a = __tmpX`, the temporary variable can be a retval as well). These assignments will be just
    removed. Needs a global optimizer, as these assignments can cross stack operations.
  * Parameters that are only passed to recursive calls and never modified won't be stored on stack.
  * Additional automatic inlining of non-recursive functions.
  * Replace jump to return instruction with the return instruction itself (increases code size).
* Strict Boolean evaluation (probably a compiler directive): ensure every boolean expression produces 0 or 1
* Improved code generation
  * Memory jump table for case expressions where possible
  * Code generation/optimization objective (speed for faster but larger code, size for smaller but slower code)
    Most of the time smaller is also faster in Mindustry Logic, but there might be a few exceptions.
* Optimization improvements
  * Multiple-use temporary variables optimization - typically as a result of ternary operator or case expression.
  * Boolean expressions:
    * and/or expression improvements: encode `a and not b` as `a > b`, `a or not b` as `a >= b` (requires the 
      previous optimization),
    * ternary operator assignment: instead of conditional jump/set/always jump/set, do set/conditional jump/set.
  * Constant expression evaluation in compiled code / expression optimization.
    * Elimination of useless statements, e.g. `op add x y 0`, `op mul x y 1` or `set x x`.
  * Multiple optimization passes (?)
* More advanced optimizations:
  * Common subexpressions optimizations (maybe including repeated array access).
  * Loop unrolling (at first just for for-each loops), perhaps other loop optimizations.
  * Better jump threading / cross-jumping.
  * Forward store for external variables / arrays.
  * Tail recursion optimization.

### User interface

* Compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`,
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change.
* Improve compiler error messages.
* Warn developers when the generated code goes over 1000 Mindustry instructions.
* Warn developers when potentially non-numeric value is being pushed to stack.
* Warn developers when variable is read before being written to.

### Other

* LogicInstruction improvements: compiled instructions store all arguments as strings. Optimizers needs to know 
  sometimes whether an argument is a numeric literal, a local variable, a global variable or a Mindustry object. 
  Arguments are unnecessarily repeatedly analyzed to obtain this information, which was already known when the 
  instruction was generated in the first place. Possibly use ints for arguments like Mindustry does.
* Assimilator: tool to automatically acquire definitions of blocks, units, items, liquids, properties etc. 
  directly from Mindustry sources. Would certainly need supervision, but might help keep Mindcode up to spec with 
  latest Mindustry.

## Musings

Things that would be cool, that might be doable in some way given existing constraints, but where the exact way of 
doing them isn't clear yet.

* [Parallel comparison](#parallel-comparison)
* Function libraries. Now that inline and stackless functions are really useful, libraries might make sense.
* Multiprocessing support - some kind of library or framework (probably) that could be used to
  provide high-level support for inter-processor communication using either memory cells/banks,
  or even unit flags.
  * The first step is already being made: schematics creation tool would allow us to compile
    the entire multiprocessor schematic in one go.
* Function pointers
  * Use mechanism similar to constant tracking to make sure that a variable used as a function pointer is only ever 
    assigned a function address to it.
  * We'd need to track all possible assignments to ensure a non-recursive function isn't made recursive through 
    a function pointer.  
* Pointers to memory-backed arrays?
  * Would allow passing arrays to out-of-line functions.

### Parallel comparison

Most useful for (or perhaps only supported in) case expressions, where similar functionality cannot be easily achieved 
right now:

```
case (a, b)
    when (x1, y1)               // a == x1 and b == y1
    when (x2, y2), (x3, y3)     // a == x2 and b == y2 or a == x3 and b == y3
    when (x4, )                 // a == x4, do not test b
    when (x5, ), (, y5)         // a == x5 or b == y5
    when (x4, y4) .. (x5, y5)   // Ouch! Maybe one day
    when (,)                    // the same as else, wouldn't be allowed
end  
```

Possibly in boolean expressions in general, although in those the utility is doubtful:

* `(a, b) == (x, y)` would be equivalent to `a == x and b == y`
* `(a, b) <= (x, y)` would be equivalent to `a <= x and b <= y`
* No need (or support) for unspecified argument here.

## On hold

There are no plans to do any of these. We keep them around just in case.

* Support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* #17 `if` operator: `break if some_cond` is equivalent to `if some_cond break end`. It's just a less verbose way of
  doing it.
* Improved data types: 2d vector
* Integrate a better code editor in the webapp, rather than a plain old `<textarea>`

## Completed

### Language syntax

* loops
    * while
    * for
* expressions!
    * `move(center_x + radius * sin(@tick), center_y + radius * cos(@tick))` moves a unit in a circle around a specific point
* if expressions (ternary operator)
* auto-allocated global variables
* switch / case expression
* add support for drawing primitives
* add support for `uradar`
* add support for `ulocate`
* add support for `end`
* functions / reusable procedures
* further optimize the generated Logic
* optimize getlink / set
* optimize sensor / set
* skip comments when determining the return value of expressions (case/when, if/else, etc.)
* #16 indirect sensor access. We can't do `resource = @silicon ; CONTAINER.resource`. This tries to call `sensor
  result CONTAINER @resource`, which doesn't make any sense.
* #17 `break` and `continue`, to better control iteration
* #19 inline functions, meaning functions that are inlined at the call-site
* add support for passing non-numerics into/out of non-recursive functions
* evaluation of constant expressions at compile time
* constant declaration: constants would be evaluated at compile time and wouldn't use a variable
* Binary literals (0b0000)

### Code generation

* Eliminate `__retval` variables/assignments where not needed.

### User interface

* display compiler output in the webapp
* compiler directives (`#set`) to parametrize code compilation and optimization
* optimizer strength setting - per optimizer (off, basic, aggressive)
