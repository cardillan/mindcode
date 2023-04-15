# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

## In progress

Things being actively worked on.

 * [Schematics-creation tool](https://github.com/francois/mindcode/issues/90) 

## Planned

Things being pondered on from time to time.

### Language syntax

* `in` boolean operator: tests number is in range (e.g. `if n in min .. max then ...`)
* Varargs inline functions: the vararg can be processed using list iteration loop, or maybe passed to another vararg 
  function.
* `break` and `continue` in `case` expressions: `break` to exit the expression, `continue` to skip to the next `when`
  branch
* Ruby-like parallel assignments, e.g. `a, b, c = 1, 2, 3` or even `a, b = b, a`
* Short-circuit boolean evaluation:
  * All logical and/or operators may or may not be short-circuited (i.e. `and`, `or`, `&&`, `||`). Avoid side 
    effects, or place side effect call first.
  * In Mindustry Logic, invalid operations just do nothing; it is therefore not needed to use short-circuit to avoid, 
    say, a null pointer exception or invalid arithmetic operation. It might be cheaper to just execute the invalid code.
    It would make sense to use short-circuit to avoid costly operations, such as function calls or complicated
    expressions. Short-circuiting will therefore be applied based on code size. 
* Processor variables backed arrays (inevitably slow, but might be usable in actual code, especially with loop 
  unrolling).

### Code generation

* Temporary variable merging: after all optimizations, all temporary variables will be inspected for scope and
  variables with non-overlapping scopes will be merged into one, and renumbered starting from 0. Fewer temporary
  variables will make inspecting variables inside processors in Mindustry way easier.
* Function calls
  * parameters that are only passed to recursive calls and never modified won't be stored on stack
  * eliminate `__retval` variables/assignments where not needed
  * additional automatic inlining of non-recursive functions
  * replace jump to return instruction with the return instruction itself
* Strict Boolean evaluation (probably a compiler directive): ensure every boolean expression produces 0 or 1
* Improved code generation
  * memory jump table for case expressions where possible
  * code generation/optimization objective (speed for faster but larger code, size for smaller but slower code).
    Most of the time smaller is also faster in Mindustry Logic, but there might be a few exceptions.
* Optimization improvements
  * circular variable assignment: when compiling recursive functions, circular assignments are sometimes generated 
    (e.g. `__tmpX = a; a = __tmpX`, the temporary variable can be a retval as well). These assignments will be just 
    removed. Needs a global optimizer, as these assignments can cross stack operations.  
  * multiple optimization passes
  * multiple-use temporary variables optimization
  * determine effective variable types to assist with optimizations
  * constant expression evaluation in compiled code
    * elimination of useless statements, e.g. `op add x y 0`, `op mul x y 1` or `set x x`
  * boolean expressions
    * ternary operator assignment: instead of conditional jump/set/always jump/set, do set/conditional jump/set.
    * and/or boolean improvements: conditional jump on individual operands instead of evaluating the and/or and doing
      conditional jump on that (overlaps with short-circuit boolean eval)
    * and/or expression improvements: encode `a and not b` as `a > b`, `a or not b` as `a >= b`.
* More advanced optimizations:
  * common subexpressions optimizations (maybe including repeated array access)
  * loop unrolling / other loop optimizations
  * better jump threading / cross-jumping
  * forward store for external variables
  * tail recursion optimization

### User interface

* Compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change
* Improve compiler error messages
* Warn developers when the generated code goes over 1000 Mindustry instructions
* Warn developers when potentially non-numeric value is being pushed to stack
* Warn developers when variable is read before being written to

### Other

* Assimilator: tool to automatically acquire definitions of blocks, units, items, liquids, properties etc. 
  directly from Mindustry sources. Would certainly need supervision. but might help keep Mindcode up to spec with 
  latest Mindustry.

## Musings

Things that would be cool, that might be doable in some way given existing constraints,
but where the exact way of doing them isn't clear yet.

* [Parallel comparison](#parallel-comparison)
* Function libraries. Now that inline and stackless functions are really useful, libraries might make sense.
* Multiprocessing support - some kind of library or framework (probably) that could be used to
  provide high-level support for inter-processor communication using either memory cells/banks,
  or even unit flags.
  * The first step is already being made: schematics creation tool would allow us to compile
    the entire multiprocessor schematic in one go.

### Parallel comparison

Most useful for (or perhaps only supported in) case expressions, where the same functionality cannot be achieved right
now:

```
case (a, b)
    when (x1, y1)               // a == x1 and b == y1
    when (x2, y2), (x3, y3)     // a == x2 and b == y2 or a == x3 and b == y3
    when (x4, *)                // a == x4, do not test b
    when (*, *)                 // the same as else, wouldn't be allowed
end  
```

Or perhaps even more complicated

```
case (a, b)
    when (x1, y1)               // a == x1 and b == y1
    when (x2 or y2)             // a == x2 or b == y2
    when (x3, *), (*, y3)       // is the same as above without need for a more complicated syntax
    when (x4, y4) .. (x5, y5)   // Ouch! Just... no.
end  
```

Possibly in boolean expresions in general, although in those the utility is doubtful:

* `(a, b) == (x, y)` would be equivalent to `a == x and b == y`
* `(a, b) <= (x, y)` would be equivalent to `a <= x and b <= y`
* No need (or support) for `*` placeholder here.

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

### User interface

* display compiler output in the webapp
* compiler directives (`#set`) to parametrize code compilation and optimization
* optimizer strength setting - per optimizer (off, basic, aggressive)
