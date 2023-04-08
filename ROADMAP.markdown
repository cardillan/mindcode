# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

## In progress

Things being actively worked on.

 * [Schematics-creation tool](https://github.com/francois/mindcode/issues/90) 

## Planned

Things being pondered on from time to time.

### Language syntax

* `in` boolean operator: tests number is in range (e.g. `if n in min .. max then ...`)
* Varargs inline functions: the vararg can be processed using list iteration loop, or maybe passed to another vararg function.
* `break` and `continue` in `case` expressions: `break` to exit the expression, `continue` to skip to the next `when` branch
* Ruby-like parallel assignments, e.g. `a, b, c = 1, 2, 3` or even `a, b = b, a`
* Short-circuit boolean evaluation in some form:
  * Ruby-like (`and`, `or` for short-circuit, `&&`, `||` for full evaluation), or
  * "best-effort" basis (no guarantees either way).
* Processor variables backed arrays (inevitably slow, but might be usable in actual code).
 
### Code generation

* Function calls
  * additional automatic inlining of non-recursive functions
  * parameters that are only passed to recursive calls and never modified won't be stored on stack
  * eliminate `__retval` variables/assignments where not needed
  * replace jump to return instruction with the return instruction itself
* Improved code generation
  * memory jump table for case expressions where possible
  * code generation/optimization objective (speed for faster but larger code, size for smaller but slower code).
    Most of the time smaller is also faster in Mindustry Logic, but there might be a few exceptions.
* Optimization improvements
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
  * better jump threading / crossjumping
  * forward store for external variables
  * tail recursion optimization

### User interface

* compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change
* improve compiler error messages
* warn developers when the generated code goes over 1000 Mindustry instructions
* warn developers when potentially non-numeric value is being pushed to stack
* warn developers when variable is read before being written to

## Musings

Things that would be cool, that might be doable in some way given existing constraints,
but where the exact way of doing them isn't clear yet.

* Function libraries. Now that inline and stackless functions are really useful, libraries might make sense. 
* Multiprocessing support - some kind of library or framework (probably) that could be used to 
  provide high-level support for inter-processor communication using either memory cells/banks,
  or even unit flags.
  * The first step is already being made: schematics creation tool would allow us to compile
    the entire multiprocessor component in one go.  

## On hold

There are no plans to do any of these. We keep them around just in case.

* Support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* #17 `if` operator: `break if some_cond` is equivalent to `if some_cond break end`. It's just a less verbose way of doing it.
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

### User interface

* display compiler output in the webapp
* compiler directives (`#set`) to parametrize code compilation and optimization
* optimizer strength setting - per optimizer (off, basic, aggressive)
