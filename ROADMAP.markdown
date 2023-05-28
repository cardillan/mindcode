# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

## In progress

I hope to include these in next release(s).

* An initial implementation of Common subexpression optimization. Will avoid implementing optimizations for handling 
  obvious optimization candidates for specific cases.
* Create much more processor tests - unit tests based on executing compiled code and comparing the output. They're 
  extremely useful at detecting bugs caused by unforeseen interference of different optimizers.
    
## Intense contemplation

### Common subexpression optimization

Still hazy. Notes so far:

* Will be implemented through control flow graph analysis
  * AST context structure might provide sufficient CFG representation; if not, a CFG will be built from it.  
  * Information about possible variable values will be gathered.
  * All operations will be evaluated using constrained mode (the operation takes two constrained variable in and 
    produces constrained output)
  * Recursive constrained-value expression evaluation will be used for expressions inside a loop. The constraints 
    will reflect possibility of repeating the operation indefinitely.
* Will probably require multi-pass optimizations
  * Some optimizers will only be run once after all passes (e.g. jump threading)
  * Some optimizers need to be run multiple times (e.g. single step jump elimination), this is not well handled in
    current implementation
* Constrained-value expression evaluation.
  * We should be able to at least partially evaluate expressions over variables with known constraints (such as 
    "non-negative", "less than x", "integer", and so on). 
  * Elimination of useless statements, e.g. `op add x y 0`, `op mul x y 1` or `set x x`.
* Known constraints on variable values will allow better expression optimization, especially with booleans

### Short-circuit boolean evaluation

* The `and` and `or` operators will be subject to short-circuiting. All other, including `&&` and `||`, are always 
evaluated fully.
* Short-circuiting will be done by the compiler. Optimizers will look for opportunities to avoid short-circuiting 
  for smaller or faster code.
* Short-circuiting will never be removed for:
  * Instructions which affect the world, (e.g. `print`, `draw`, `ucontrol`  etc. - it is necessary to keep track of 
    which opcodes do affect the world.)
  * Useful (as determined by CSE data flow analysis) assignments to variables.

### Optimizing code for speed 

* It is already done in loop optimization
* A general approach:
  * all opportunities for speed optimizations will be identified and gathered,
  * if all optimizations are impossible without reaching 1000 instructions limit, the most effective ones will be 
    selected (based on instruction frequency analysis or [`#use statement`](#use-statement) - explicit 
    `#use goal = speed` code blocks will be prioritized).
* At least some optimizations (e.g. function inlining) will require AST tree node rebuild - it will probably be 
  easier than a recompile. Loop unrolling, on the other hand, seems feasible for optimizers to realize.

### Processor-variables backed arrays and loop optimizations

Processor-variables backed arrays will always have a horrible performance, but when designed together with loops and 
loop optimizations they might be very useful - and not so slow.

* Optimizations unrelated to arrays
  * Loop unrolling
  * Loop unrolling with fixed end condition: loops that have a computed start condition, but fixed end condition, 
    could be unrolled as a list of instructions without jumps. The loop will start by jumping to the instruction 
    corresponding to given start condition.
    * Needs fixed-size (in terms of the number of instructions) iterations. 
* The arrays
  * Declared at fixed size
  * Arrays, not lists - no add/remove. no inherent size
  * No pointers to arrays
  * Random access will be realized as an out-of-line function
    * Compute function address from index (address = 2 * index + offset)
    * For Writes: set value to be written to a variable
    * Make the call (set return address + jump)
      * Perform read/write and jump back
    * For reads: the read value is stored in a transfer variable
  * Access to a constant index will be identical to accessing a normal variable
  * Array access will be realized as a virtual instruction and resolved in the instruction resolution step
    * If future common subexpression optimization finds out an index is constant, the instruction is resolved to
      constant index access naturally
  * Maybe array assignments, esp. for same sized arrays
    * Perhaps by a library function that might be inlined if possible
  * Out-of-bound access checks: compiler directive
* Arrays in loops optimizations
  * Full loop unrolling for static loops 
  * For dynamic loops:
    * If the loop variable is used solely to access the array, transform the loop from loop over indexes to loop 
      over function access arrays
    * If the variable is dual-use, but simple iteration, use shadow variable for array access
    * Loop unrolling still possible, just with jump-out tests after each iteration
* For each syntax over arrays
  * Possible support to modify the underlying array through the loop control variable, or specific syntax (`yield exp`)

* Assign hints to AST Context nodes to be used by LoginInstructionGenerator in multi-pass compilation.  
 
## Planned

Things being pondered on from time to time.

### Language syntax

* Allow properties to be called on any expression. The nature of the call will be determined by the property name.
  * Use mimex to obtain all metadata needed to recognize all valid properties. 
* Allow empty optional arguments in function calls. At this moment, optional arguments can only be omitted at the
  end of the argument list.
* `in` boolean operator:
  * tests number is in range: `n in min .. max`
  * tests value is in enumerated set: `type in (@sorter, @inverted-sorter)`
* Varargs inline functions: the vararg can be processed using list iteration loop, or maybe passed to another vararg 
  function.
* `break` and `continue` in `case` expressions: `break` to exit the expression, `continue` to skip to the next `when`
  branch (?)
* Ruby-like parallel assignments, e.g. `a, b, c = 1, 2, 3` or even `a, b = b, a`
  * Perhaps a way could be found to declare a function as returning several values and parallel-assign them (e.g. 
    `def func(x, out y, out z) return (x + 1, x - 1) end  a, b = func(7)`). The syntax looks just weird. What if we 
    don't need some of the return values? 

#### #use statement

```
#use compiler-option = value, compiler-option = value, ...
  [code]
end
```

Compiles code within the code block applying certain compiler options (e.g. goal) to it. Some compiler options 
(target, optimization) will remain global and won't be available in `#use`. The intended purpose is to provide means 
to compile different parts of code for size or speed.

### Code generation / optimization

* Function calls:
  * Circular variable assignment: when compiling recursive functions, circular assignments are sometimes generated
    (e.g. `__tmpX = a; a = __tmpX`, the temporary variable can be a retval as well). These assignments will be just
    removed.
  * Parameters that are only passed to recursive calls and never modified won't be stored on stack.
  * Additional automatic inlining of non-recursive functions.
  * Replace jump to return instruction with the return instruction itself (increases code size).
* Boolean expression optimizations: encode `a and not b` as `a > b`, `a or not b` as `a >= b`, if both values are 
  known to be boolean.
* Temporary variable merging: after all optimizations, all temporary variables will be inspected for scope and
  variables with non-overlapping scopes will be merged into one, and renumbered starting from 0. Fewer temporary
  variables will make inspecting variables inside processors in Mindustry way easier.
* Improved code generation
  * Memory/instruction jump table for case expressions where possible
* Propagate constant string evaluation into inline functions.
* Multiple optimization passes (?)
* String constant distribution: `print("Answer is ", answer ? "yes" : "no")` would be turned into
  `print(answer ? "Answer is yes" : "Answer is no")`, saving one `print` instruction.
* More advanced optimizations:
  * Common subexpressions optimizations (maybe including repeated array access).
  * Loop unrolling, perhaps other loop optimizations.
  * Better jump threading / cross-jumping.
  * Forward store for external variables / arrays.
  * Tail recursion optimization.

### Schematics Builder

* Improve error reporting: pass tokens through AST tree/schematic components and provide file and line/column numbers  
  when reporting errors. Use this as a proof of concept for implementing better error reporting in Mindcode.
* Validate mlog code included in processor configuration against given processor target.
* Automatically generate names for unnamed processor links
* Schematic Analyzer, a tool to identify potential errors in schematics:
  * blocks not connected to power nodes
  * the power grid having more than one segment (i.e. not fully connected)
  * distribution blocks prone to clogging
  * liquid containers being fed more than one kind of liquid
  * determines item flow and unbalanced factory production/consumption ratios
* Automatically connect blocks to power nodes
  * by specifying an area in which all blocks will be connected,
  * by searching for optimal-ish distribution of connections among nodes. 
* Support for iterative block placement:
  * `<@block> rightwards` places the block to the right of the last placed block
  * `<@block> upwards from <label>` places the block upwards from labeled blocks
  * already existing blocks (typically blocks 2x2 or bigger placed in previous row/column) are skipped
* Automatically add tags based on categories or types of contained blocks
  * Allow loading configuration/tag mapping for categories from file so that it can be shared among schematics 
* Make decompiler assign labels to all blocks and use the labels for all block references (e.g. in bridge or power 
  node connections).
* Support for schematics reuse by:
  * placing schematic into schematic
  * extending existing schematic by adding or removing content
* Support for filling area with blocks
* Support for external compilers to create code for processors.

### User interface

* Webapp: after decompiling a schematic, redirect to Schematic Builder page using the decompiled source code.
* Compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`,
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change.
* Improve compiler error messages.
* Warn developers when the generated code goes over 1000 Mindustry instructions.
* Warn developers when potentially non-numeric value is being pushed to stack.
* Warn developers when variable is read before being written to.
* When the compiled program only contains basic instructions (including print and printflush), run it after 
  compilation and show the output on the web page. The same might be done for command-line compiler.
* Render an image of built schematic to show it in the web application.

### Other

* Improve [Mindustry Metadata Extractor](https://github.com/cardillan/mimex) to extract more metadata and 
  automatically put them at the right place in Mindcode.
* Add links to examples in documentation to open them in web app.

## Musings

Things that would be cool, that might be doable in some way given existing constraints, but where the exact way of 
doing them isn't clear yet.

* [Parallel comparison](#parallel-comparison)
* Should case expressions use strict comparison (===)? 
* Typed variables, parameters and function return values.
  * This would allow better optimization and some special features, such as function pointers.
  * Typed variables would have to be declared and could exist alongside untyped ones.
  * Compiler directive could be used to require all variables to be typed.
* Structures. Requires typed variables. All fields of a structure would be stored in separate processor variables. 
  Would allow functions providing more output values. 
* Function libraries. Now that inline and stackless functions are really useful, libraries might make sense.
* Multiprocessing support - some kind of library or framework (probably) that could be used to
  provide high-level support for inter-processor communication using either memory cells/banks,
  or even unit flags.
  * The first step is already being made: schematics creation tool would allow us to compile
    the entire multiprocessor schematic in one go.
* Function pointers:
  * Requires typed variables.
  * We'd need to track all possible assignments to ensure a non-recursive function isn't made recursive through 
    a function pointer.  
* Pointers to processor-variables backed arrays:
  * Requires typed variables.
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

Completed functionalities are no longer tracked here. All changes are now tracked in [changelog](CHANGELOG.markdown).
