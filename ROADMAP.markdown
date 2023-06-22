# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

## Current priorities

* When an uninitialized temporary variable is found by Dead Code Eliminator, generate compilation error. This happens 
  as a result of some optimizer bug. 
* Optimization for speed
  * Alter optimizers to collect statistics about possible speedups and their costs (in terms of instructions added 
    per percentage of executions step avoided, weighed by the node weight) - only in the ITERATED phase. It would make 
    sense to allow optimizers to offer alternatives, complicating the optimal selection problem slightly.
  * At the end of the phase, count available instruction space and if not all speedups can be realized, 
    choose the most efficient ones until available space is exhausted (this is a knapsack problem).
  * In the next phase, individual optimizers will realize the accepted optimizations first. Reported cost must be 
    conservative - an optimization must not create more additional instructions than predicted. They may then
    re-optimize the resulting code and propose additional optimizations (it is expected that actual code increases  
    will be smaller than anticipated, since the newly generated code may be further optimized by other optimizers).
  * ITERATED phase end when no optimizer modified the code, and none of proposed optimizations is feasible. Also, 
    new compiler option to limit the number of passes - 3 for the web app, 25(?) for the command line tool by default. 
  * Possible optimizations for speed:
    * Inlining: a special case. If an inlining optimization is elected, the instruction list will be rebuilt from
      the AST tree marking the function inlined, re-running all optimizations from start. Maximum number of inlining 
      rounds should be limited (say, 1 or 2, or a compiler option). 
    * Loop unrolling:
      * Full loop unrolling fixed bounds loops
      * Loop peeling - unroll first n iterations (with conditions in between), followed by the loop.
      * Jump-to-middle loop unrolling for loops with fixed end condition
      * Jump-to-middle loop unrolling for loops with fixed start that can run in the opposite direction (e.g.
        `for i in 0 .. n cell1[i] = 0 end` - jump to position corresponding to `n` and proceed to `0`).
      * Loop unswitching (if in loop --> loops in if)
      * Loop fusion???
    * Switched case expression
      * All or a subset of when branches can be rearranged and an in-memory jump table created. 
    * Return optimization: replace jump to return instruction with the return instruction itself.
* Optimization for size: when the goal is set to `size`, selected optimizers may try to replace code with smaller, 
  slower alternatives
  * Case expressions: if each branch of a case expression provides just the expression value, rearrange each branch 
    to setting the expression value to the output variable and jump out if the branch was selected. Saves one jump 
    per branch, at most 1/3 instructions. Doubles execution time in average case. 
* Add block comments to allow commenting/uncommenting blocks of code when battling a syntax error (better syntax 
  error reporting would be much more preferable, but quite hard to implement). 
* Improve data flow optimization around function calls further:
  * Values pushed to stack need not be assigned to their proper variables first, a temp can be stored instead.
  * Create global optimizer to handle functions with constant return values. Handle specific case of function
    always returning one of its input arguments.
* Loop unrolling - already possible and promising big returns.
* Stack optimizer: when an argument to recursive function call is modified in a reversible way (such as `foo(n - 1)`),
  instead of push/pop protection, revert the operation after the function call returns. Implement strict/relaxed 
  math model to let the user block this in case the reversed operation produces result not equal to the original one.
* `math-model` compiler option: `strict` or `relaxed`. Under the `strict` model, the following will apply:
  * Loss of precision during numeric literal conversion is disallowed. If such a literal is found in the source code,
    the compilation will fail. Optimizations that would cause precision loss will be blocked.
  * Restoring variable value by inverting the operation performed on it will be disallowed.
  * Assumptions such as `x < x + 1` for a general value of `x` won't be made (for large values of `x`, this might 
    not be computationally true: `x = 10 ** 20; y = x + 1; print(x == y)` gives `1`).
* External variable value reuse
  * When a value is read or written to a memory cell, store it and don't reread it if not necessary, unless the 
    memory cell was declared `volatile`.
  * Needs to reset stored values on possible overwrites.
    * Tracking of possible memory block aliases (will be done globally) - all memory blocks obtained via instructions 
      (`getlink`, `getBlock`, `ulocate`) are considered aliased (the optimizer cannot exclude the possibility 
      they're the same block). Can be overridden by setting the memory model or individual variable to `restricted`.
    * Tracking of possible index aliases - don't know how to do in general case, probably will just reset 
      everything whenever any write to a memory occurs. Possible tracking of rewrites for specific constant indexes
      (will cover external variables).
      * If it was possible to establish that two expression values are distinct, we might utilize this knowledge and 
        not reset the value represented by a known-to-be-distinct expression when a memory write occurs at the other 
        expression index. We might consider expressions that differ by an integer constant distinct. 
* Volatile values: always reread, never reuse last value
  * Specific built-in ones (already done)
  * All sensed properties (already done - the entire `sensor` instruction is deemed volatile)
  * New compiler directive will allow to declare memory model for a memory block, linked block or built-in variable
    (`#declare variable [volatile | aliased | restriced]`).
* More expression optimizations:
  * replace addition/subtraction of 0 by assignment,
  * replace multiplication/division by 1 by assignment,
  * probably done by ExpressionOptimizer to avoid bloating of Data Flow optimizer. Requires multiple optimization 
    passes.
* Pulling invariant code out of loops/if branches.
* Instruction reordering for better constant folding/subexpression optimization
  * If an expression being assigned to a user variable is identical to a prior expression assigned to a temporary
    variable, try to move the assignment to the user variable before the temporary variable. Might allow reusing the
    user variable instead of the temporary one.
* Global variable type inferring.
  * More precise might be obtained through data flow analysis. This will be a start.
  * Temporary variables are typically single-use, global type inferring might work very well for them.

## Planned

### Constant folding and common subexpression optimization

* Generalized constant folding on expression tree, including factoring constants out of complex expressions.
  * A theory-based approach is probably needed.  
* Expression distribution:
  * `print(value ? "a" : "b")` could be turned into `if value print("a") else print("b") end`
  * Might make sense for other instructions as well.
  * Is useful when at least one value produced by the if statement is a constant.  
* Inferring invariants: use global analysis/data flow analysis to infer invariants
  * Constraints on variable values inferred from conditions
    *  "non-null", "non-negative", "less than x", "boolean", "integer"
  * Constraints on relationships between variables
    * Not equal/equal
    * Less (or equal) than/greater (or equal) than
    * Linear transformation (y = ax + b), with constant or even variable a/b.
  * Inferred invariants will allow better expression optimization, especially with booleans
    * Control loop variable aliasing: when a linearly transformed value of a control loop variable is used (say, for 
      array index access) and the control loop variable has no other uses, apply the linear transformation to the 
      loop variable, initialization code and condition.

### Short-circuit boolean evaluation

* The `and` and `or` operators will be subject to short-circuiting. All other, including `&&` and `||`, will always 
  be evaluated fully.
* Short-circuiting will be done by the compiler. Optimizers will look for opportunities to avoid short-circuiting 
  for smaller or faster code.
* Short-circuiting will never be removed for:
  * Instructions which affect the world, (e.g. `print`, `draw`, `ucontrol`  etc. - it is necessary to keep track of 
    which opcodes do affect the world.)
  * Useful (as determined by data flow analysis) assignments to variables.

### Optimizing code for speed 

* It is already done in loop optimization
* A general approach:
  * all opportunities for speed optimizations will be identified and gathered,
  * if all optimizations are impossible without reaching 1000 instructions limit, the most effective ones will be 
    selected (based on instruction frequency analysis or [`#use statement`](#use-statement) - explicit 
    `#use goal = speed` code blocks will be prioritized).
* At least some optimizations (e.g. function inlining) will require recompiling the AST tree node - it will probably 
  be easier than manipulating the compiled code. Loop unrolling, on the other hand, seems feasible for optimizers to 
  realize.

### Processor-variables backed arrays and loop optimizations

Processor-variables backed arrays will always have a horrible performance, but when designed together with loops and 
loop optimizations they might be very useful - and not so slow.

* Optimizations unrelated to arrays
  * Loop unrolling with fixed end condition: loops that have a computed start condition, but fixed end condition, 
    could be unrolled as a list of instructions without jumps. The loop will start by jumping to the instruction 
    corresponding to given start condition.
    * Needs fixed-size (in terms of the number of instructions) iterations. 
* The arrays
  * Declared at fixed size
  * Arrays, not lists - no add/remove, no inherent size
  * No pointers to arrays
  * Random access will be realized as an out-of-line function
    * Compute function address from index (address = 2 * index + offset)
    * For Writes: set value to be written to a transfer variable
    * Make the call (set return address + jump)
      * Perform read/write and jump back
    * For reads: the read value is stored in a transfer variable
  * Accessing an array item at constant index will be identical to accessing a normal variable
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
      over function access pointers (see also linear transformation of loop variable)
    * If the variable is dual-use, but simple iteration, use shadow variable for array access
    * Loop unrolling still possible, just with jump-out tests after each iteration
* For each syntax over arrays
  * Possible support to modify the underlying array through the loop control variable, or specific syntax (`yield exp`)
  
* Assign hints to AST Context nodes to be used by LoginInstructionGenerator in multi-pass compilation.  

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
* Typed variables, parameters and function return values.
  * This would allow better optimization and some special features, such as function pointers.
  * Typed variables would have to be declared and could exist alongside untyped ones.
  * Compiler directive could be used to require all variables to be typed.
* Records.
  * Requires typed variables.
  * Just a way to bind several variables together. Only static allocation.
  * Allows returning multiple values from functions.
  * Will compile down to individual variables, which will then be optimized by Data Flow optimization.

#### #use statement

```
#use compiler-option = value, compiler-option = value, ...
  [code]
end
```

Compiles code within the code block applying certain compiler options (e.g. `goal`) to it. Some compiler options 
(`target`, `optimization`) will remain global and won't be available in `#use`. The intended purpose is to provide 
means to compile different parts of code for size or speed.

### Code generation / optimization

* Function calls:
  * Additional automatic inlining of non-recursive functions.
  * Replace jump to return instruction with the return instruction itself (increases code size).
* Boolean expression optimizations: encode `a and not b` as `a > b`, `a or not b` as `a >= b`, if both values are 
  known to be boolean.
* Temporary variable merging: after all optimizations, all temporary variables will be inspected for scope and
  variables with non-overlapping scopes will be merged into one, and renumbered starting from 0. Fewer temporary
  variables will make inspecting variables inside processors in Mindustry way easier.
* Improved code generation
  * Memory/instruction jump table for case expressions where possible
* Propagate constant string evaluation into inline functions (?).
* More advanced optimizations:
  * Better jump threading / cross-jumping.
  * Forward store for external variables / arrays (?).
  * Tail recursion optimization.

### Further Data flow analysis enhancements

* Visit stackless functions on calls (??):
  * Every result of a visit to stackless function would have to be merged together and then applied to optimizations
    on that function.
  * Could help keeping track of global variables and memory blocks.
  * Could be used to create more versions of a function, possibly inlining some of them, based on (dis)similarities
    of variable states between visits (probably quite complex.)

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
  * determine item flow and unbalanced factory production/consumption ratios
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
* Support for external compilers (mlogjs/pyndustric?) to create code for processors - must have command line compilers.

### User interface

* Webapp: after decompiling a schematic, redirect to Schematic Builder page using the decompiled source code.
* Compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`,
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change.
* Improve compiler error messages.
* Warn developers when the generated code goes over 1000 Mindustry instructions.
* Warn developers when potentially non-numeric value is being pushed on the stack.
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

* Virtual no-op instruction. Will be resolved to nothing. Instructions to be removed will be replaced with this
  instruction instead, allowing the optimizers to remove instructions while preserving AST context structure of the
  program (unreachable code elimination, jump normalization).
  * Was tried in a preliminary implementation and doesn't seem to bring about any benefit at all.
* Support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* #17 `if` operator: `break if some_cond` is equivalent to `if some_cond break end`. It's just a less verbose way of
  doing it.
* Improved data types: 2d vector
* Integrate a better code editor in the webapp, rather than a plain old `<textarea>`

## Completed

Completed functionalities are no longer tracked here. All changes are now tracked in [changelog](CHANGELOG.markdown).
