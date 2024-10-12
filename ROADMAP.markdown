# Roadmap

This documents servers as a scratch pad to track ideas and possible enhancements to Mindcode.

A significant update is planned. Planned changes are described 
[here](https://github.com/cardillan/mindcode/discussions/142). Comments and suggestions are welcome.

# Current priorities

* Bug fixes and [incremental improvements](#incremental-improvements)
* [Inferring invariants of variables](#inferring-invariants-of-variables)
* [Speculative optimization for speed](#speculative-optimization-for-speed)
* [External variable optimizations](#external-variable-optimizations)
* [Function pointers](#function-pointers)

# Incremental improvements

None planned.

# Other small or internal improvements

* Handling syntax errors - web & command line apps:
  * Display message and link to the discussion, prompt users to ask for help if stuck
* Handling internal errors:
  * Web app:
    * Store the source file in a separate table for errors  
    * Display message "The error has been logged and will be investigated."
  * Command line app: display message "An internal error occurred. Please report the error at ..."
* Fix incorrect headings of Data Flow Optimization passes executed after applying selected speed optimization.
* Refactor stackless function calls
  * UnreachableCodeEliminator now tracks code paths and recognizes CALL instruction; it is no longer necessary to
    track all possible return points from a stackless function.
  * Rename GOTOLABEL --> OFFSETLABEL
  * Remove marker from GOTO instruction, GOTOs will target normal LABELs
  * Make ListIterator use GOTOOFFSET (with an offset of 0, hmmm.....)
  * Only GOTOOFFSET/OFFSETLABEL instructions will be related via marker
  * Remove exception from OptimizationContext.duplicateLabel (see TODO STACKLESS_CALL)
* Optimization speedups
  * DataFlowOptimizer: cache VariableState instances at the entry and exit points end of each context. If the
    inbound instance is equal to the cached one, use the cached outbound instance and avoid new processing.
  * Only recreate OptimizationActions of the action whose context(s) are affected by previous optimizations.

# Improving error/warning messages

* Compatibility warnings:
  * warn when `configure` main variable is used in V7 -- ML changes it to `config`,
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change.
* Warn when the generated code goes over 1000 Mindustry instructions.
* Warn when potentially non-numeric value is being pushed on the stack.

  
# Additional syntax enhancements

* Ruby-like parallel assignments, e.g. `a, b, c = 1, 2, 3` or even `a, b = b, a`.
* Varargs inline functions (??)
  * Function needs to be explicitly declared inline
  * `inline def foo(a, b, c, x...) ... end`
  * The vararg can be processed using list iteration loop, or maybe passed to another vararg function:
    `def foo(arg...) for a in arg print(a) end end def bar(arg...) foo(arg) end`
    
## #set local compiler directive/statement

```
#set local compiler-option = value, compiler-option = value, ...;
```

Compiles the next statement/expression applying certain compiler options (e.g. `goal`) to it. Some compiler options
(`target`, `optimization`) will remain global and won't be available in `#set local`. The intended purpose is to 
provide means to compile different parts of code for size or speed.

The specific options would probably have to be stored in AST contexts.

## Typed variables

Typed variables, parameters and function return values.
* This would allow better optimization and some special features, such as function pointers.
* Typed variables would have to be declared and could exist alongside untyped ones.
* Compiler directive could be created to require all variables to be declared and typed.
* Problems:
  * Types of some expressions might not be possible to determine statically, e.g. the type of `block.sensor(property)` 
    value depends on the property being sensed.   

## Records/structures

* Requires typed variables.
* Just a way to bind several variables together. Only static allocation.
* Allows returning multiple values from functions.
* Will compile down to individual variables, which will then be optimized by Data Flow Optimization.
* Possible support for arrays of records

# Speculative optimization for speed

* `instruction-overload` compiler option: a numerical value. Nonzero value allows performing speed optimizations that
  would exceed instruction space by at most the given quota, followed by other optimization passes specified by the
  optimizer. If the code size after the additional optimizations fits instruction space, the optimization is
  committed; if it doesn't, the optimization is rolled back and rejected forever. Limited to 200 in the web
  application. Needs the following:
  * way for the speed optimizers to specify which other optimizations to run,
  * mechanism for rolling back rejected optimizations,
  * mechanism for keeping track of rejected optimizations.

# External variable optimizations

* External variable value reuse
  * When a value is read or written to a memory cell, store it in a shadow variable and don't reread it if not 
    necessary, unless the memory cell was declared `volatile`.
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
    * Do not write the same value if it is known to be unchanged.
* Volatile values: always reread, never reuse last value.
  * Specific built-in ones (already done).
  * All sensed properties (already done - the entire `sensor` instruction is deemed volatile).
  * New compiler directive will allow to declare memory model for a memory block, a linked block, a built-in variable
    or a global variable (because of the `sync` instruction), e.g. `#declare variable [volatile | aliased | restriced]`.

# Function pointers

* Assign a function address to a variable:
  * `fptr = function` (note: no brackets)
  * Assigning the function definition: `fptr = def foo(n) print(n) end`. Not sure about this, but why not?
  * Lambda/anonymous function syntax: `fptr = n -> print(n)` or `fptr = (m, n) -> (x = m * n; print(x))`
* Global analysis of function assignments
  * If a variable is assigned a function address, it must not be assigned anything which is not a function 
    address (global analysis).
  * All function addresses assigned to a single function pointer variable must belong to functions having the same
    number of arguments.
  * Assignments between function pointers are tracked too. An alias graph will be created, each connected
    segment in the graph must be assigned compatible functions (same number of parameters). Each segment - **function 
    group** - gets its own set of transfer variables.
  * Function call must use the correct number of arguments for the given function.
  * Only stackless/recursive user defined functions can be assigned to function pointers.
  * Function pointers can be passed to other functions as arguments, assignments to arguments are tracked just
    like everything else, except calls through function pointers, which need to be handled separately.
  * Calling recursive functions is possible. A function can be made recursive by calls via function pointer(!)
* Calling mechanism
  * For each function group a separate set of variables will be allocated: `__fgN_retaddr`, and `__fgN_arg0` to
    `__fgN_argM` for individual arguments ("fg" as in "function group").
  * When making a call, return address and arguments will be stored in the `__fgN` variables. Calls of recursive and
    stackless functions through a function pointer are the same.
  * A function callable through function pointer will be prepended a header that copies the `__fgN` arguments and
    return variable to actual `__fnX` arguments and return variable. If it is a recursive function, the header
    will store `__fgN_retaddr` on stack to emulate a recursive call.
  * Return address will be set to a code region after the function body which copies the `__fnN` variables to the 
    `_fgN` ones and then returns to the caller. 
  * Specific optimizers for function pointer calls
    * If the return value is not used, function return address will be set directly to the caller's return address. 
    * If the function is only called through a function pointer, copying `__fgN` variables to `__fpN` variables 
      might be avoided, if the values are preserved.
  * Function call graph will need to be built upon function groups too.
* Possible handling of calling a null function pointer:
  * Skips to the beginning of a program, resetting it. Nothing needs to be done.
  * Null function pointer corresponds to a special function that does nothing and just returns. All function
    pointers need to be initialized, and assigning null to them is replaced by assigning the special function
    address to them. Problem with global variables, where initialization isn't easy to track.
  * Null function pointer corresponds to a special function that indicates an error and stops the program
    execution. The same prerequisites as above. Ask Anuken for an extension to the stop instruction.
    
# Internal arrays

Individual elements of internal arrays will be stored in processor variables. Accessing the elements via a dynamic 
index will be realized through compiler-defined functions. When used together with loop 
unrolling or data flow optimization, they might be very useful - and as fast as regular variables.

* Array name is not a variable - neither an l-value nor an r-value.
  * No pointers to arrays
* Arrays, not lists - no add/remove, no inherent size
* Random access will be realized using out-of-line functions
  * Read and write function for each array
    * Switched case, `goto` instructions at each branch end
  * Immediate benefit from existing optimizations (automatic inlining, loop unrolling etc.)
  * Needs to update function inliner to estimate the cost of inlining constant index calls as zero.
    * Accessing an array item at constant index will be identical to accessing a normal variable
* Alternative: virtual instructions. Might allow better loop optimization without inlining.
* Maybe array assignments, esp. for same sized arrays
* Out-of-bound access checks: compiler directive
* For each syntax over arrays - support modification of the underlying array through `out` control loop variables

## Further developments

* Pointers to internal arrays:
  * Might require typed variables.
  * Would allow passing arrays to out-of-line functions.

# Internal stack

Stack stored in processor variables, similar to internal arrays.

# Code generation improvements

* `math-model` compiler option: `strict` or `relaxed`. Under the `strict` model, the following will apply:
  * Loss of precision during numeric literal conversion is disallowed. If such a literal is found in the source code,
    the compilation will fail. Optimizations that would cause precision loss will be blocked.
  * Restoring variable value by inverting the operation performed on it will be disallowed.
  * Assumptions such as `x < x + 1` for a general value of `x` won't be made (for large values of `x`, this might
    not be computationally true: `x = 10 ** 20; y = x + 1; print(x == y)` gives `1`).

## Short-circuit boolean evaluation

Two basic approaches
* Implemented in compiler; can use different operators for short-cirćuit and full evaluation syntax like Ruby  
* Implemented as a postprocessing/optimization; cannot be specified in code. 

### Compiler approach

* The `and` and `or` operators will be subject to short-circuiting. All other, including `&&` and `||`, will always
  be evaluated fully.
* Short-circuiting will be done by the compiler. Optimizers will look for opportunities to avoid short-circuiting
  for smaller or faster code.
* Short-circuiting will never be removed for:
  * Instructions which affect the world, (e.g. `print`, `draw`, `ucontrol`  etc. - it is necessary to keep track of
    which opcodes do affect the world.)
  * Useful (as determined by data flow analysis) assignments to variables.

### Optimizer approach

* Use short-circuiting where it produces shortest code.
* Code without side effects can be short-circuited/rearranged at will.

# Optimization improvements

## Data Flow Optimization

* Expand handling of expressions by the Data Flow Optimization:
  * handle multiplication by zero, multiplication/division by one, and addition/subtraction of zero directly (i.e.
    independently of the Expression Optimization).
  * When there are two subsequent MUL/DIV or ADD/SUB instructions, the first one not being used by any other
    instruction and the second one containing a constant, move the constant to the first instruction. Would facilitate
    constant folding for more complex expressions.
  * Add factoring-out capability to more complex expressions.
* Correctly resolve case expressions (both jump table based and condition based) when the input value is effectively 
  constant.
* Recognize dead writes inside loops: `i = 0; while switch1.enabled i += 1 end`: writes to `i` are dead, but not 
  recognized as such. 
* Identify uninitialized global variables (now only reported when they are never written to; use the same logic as 
  for main/local variables instead).
* Pulling invariant code out of if branches.
* Generalized constant folding on expression tree, including factoring constants out of complex expressions.
  * Instruction reordering for better constant folding/subexpression optimization
    * If an expression being assigned to a user variable is identical to a prior expression assigned to a temporary
      variable, try to move the assignment to the user variable before the temporary variable. Might allow reusing the
      user variable instead of the temporary one.
* Visit stackless functions on calls - global variables:
  * Every result of a visit to stackless function would have to be merged together and then applied to optimizations
    on that function.
  * Could help keeping track of global variables and memory blocks.
  * Could be used to create more versions of a function, possibly inlining some of them, based on (dis)similarities
    of variable states between visits (probably quite complex.)
* Code path splitting
  * If a variable is known to take on several distinct values and is part of several control statements or complex 
    expressions, create a jump table targeting specialized code for some or all of the values the 
    variable can attain.
  * Needs to create metric for the complexity to use this only when appropriate.
  * Example: `bar = foo ? 5 : 10; for i in 1 ... bar cell1[i] = 0 end` - after the code path splitting optimization,
    there could be two unrolled loops.

### Inferring invariants of variables

* Constraints on variable values inferred from instruction producing the value
  * e.g. `op max result input 10` limits the value of `result` to `10`
* Constraints on variable values inferred from conditions
  *  "non-null", "non-negative", "less than x", "equal to"
* Constraints on relationships between variables
  * Not equal, equal, less (or equal) than/greater (or equal) than
  * `op max result input 10` establishes `result <= input`
  * Linear transformation (y = ax + b), with constant or even variable a/b.
* Inferred invariants will allow better expression optimization, especially with booleans
  * Control loop variable aliasing: when a linearly transformed value of a control loop variable is used (say, for
    array index access) and the control loop variable has no other uses, apply the linear transformation to the
    loop variable, initialization code and condition.
  * Step sync: convert `while <exp> i += 1; j = a * i + b end` to `j = i + b; while <exp> i += 1; j += a end`
* Boolean expression optimizations: encode `a and not b` as `a > b`, `a or not b` as `a >= b`, if both values are
  known to be boolean.

## Function inlining / call optimization

* `noinline` keyword to prevent function from being ever inlined. Useful when I want the function to be called from 
  an unrolled loop; inlining it might prevent the loop from unrolling.
* When just one of the parameters passed to the function is variable with a few discrete values and the others are
  fixed, create a switched expression and inline the function separately for each value (???)
  * Code path splitting handles this in a general manner 
* If there are several combinations of argument values, each used more than once, it might make sense to create a
  copy of the function for each distinct combination of the argument values and applying the above optimization to
  it.
* When a read-only parameter is always passed the same argument value (a literal), globally replace the parameter
  with that literal, saving the assignment. In combination with the above point.
* When a function always returns the same value, replace the return value variable with that value. Handle specific
  case of function always returning one of its input arguments.

## Recursive function optimization

* Values pushed to stack need not be assigned to their proper variables first, a temp can be stored instead.
* When an argument to a recursive function call is modified in a reversible way (such as `foo(n - 1)`), instead of
  push/pop protection, revert the operation after the function call returns. Implement strict/relaxed math model to
  let the user block this in case the reversed operation produces result not equal to the original one.

## Case Switching

* Only perform the optimization when the input value is a known integer
* Support ranges in when branches
* Omit range checking where possible (requires invariant inferring)
* Cases with sparse sets of when branches: convert the largest segment from case values with a density
  higher than 0.5, leave other values to conditional jumps
* On `advanced` level, convert switches that have overlapping values.

### Case switching over built-in constants

Process case expressions based on item/liquid/unit/block etc. Applies when all when branches contain a built-in 
constant of the same type. Example:

```
case itemType
  when @coal then A
  when @lead then B
  when @graphite then C
  ...
end 
```

would compile into

```
sensor offset itemType @id
op min offset offset maximal_used_id + 1
op add @counter <start_of_jump_table - minimal_when_value - 1> offset
start_of_jump_table:
jump <branch for id 0>
jump <branch for id 1>
...
jump <else branch>
branch for id 0:
<jump to else branch if itemType !=== constant_with_id_0> 
```

The `op min` instructions perhaps might be avoided under some circumstances.

## Loop unrolling

* Loop peeling - unroll first n iterations (possibly with conditions in between), followed by the loop. Especially
  useful if the loop is known to iterate at least n times.
* Jump-to-middle loop unrolling
  * Using a jump table.
  * Loops with fixed end condition
  * Loops with fixed start that can run backwards (e.g. `for i in 0 .. n cell1[i] = 0 end` - jump to position
    corresponding to `n` and proceed to `0`).
    * Need a limit on highest possible value of `n`
* Loop unswitching (if in loop --> loops in if)
* Loop fusion???

## Loop hoisting

* Generalized method of identifying finding loop invariant code.

## Unreachable code elimination improvements

* Detect `end` instructions which are **always** executed in a user defined function and terminate the code path
  when such a function is called.

## Optimization for speed

* Currently, the optimizer realizes speed optimizations one by one, always choosing the highest benefit. Better
  utilization of instruction space _might_ be achieved by searching for a solution of the knapsack problem.

## Optimization for size

* Case expressions: if each branch of a case expression provides just the expression value, rearrange each branch
  to setting the expression value to the output variable and jump out if the branch was selected. Saves one jump
  per branch, at most 1/3 instructions. Doubles execution time in average case.

# Schematics Builder

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

# User interface

* Webapp: after decompiling a schematic, redirect to Schematic Builder page using the decompiled source code.
* When the compiled program only contains basic instructions (including print and printflush), run it after 
  compilation and show the output on the web page. The same might be done for command-line compiler.
* Render an image of built schematic to show it in the web application.

# Other

* Temporary variable merging: after all optimizations, all temporary variables will be inspected for scope and
  variables with non-overlapping scopes will be merged into one, and renumbered starting from 0. Fewer temporary
  variables will make inspecting variables inside processors in Mindustry way easier.
* Propagate constant string evaluation into inline functions (?).
* More advanced optimizations:
  * Better jump threading / cross-jumping.
  * Tail recursion optimization.
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
  or even unit flags. Schemacode would be used to produce a schematic with several processors.

### Parallel comparison

Most useful for (or perhaps only supported in) case expressions, where similar functionality cannot be easily achieved 
right now:

```
case (a, b)
    when (x1, y1)               // a == x1 and b == y1
    when (x2, y2), (x3, y3)     // a == x2 and b == y2 or a == x3 and b == y3
    when (x4, )                 // a == x4, do not test b
    when (x5, ), (, y5)         // a == x5 or b == y5
    when (x6, y6) .. (x7, y7)   // a in x6 .. x7 and b in y6 .. y7
    when (,)                    // the same as else, wouldn't be allowed
end  
```

Possibly in boolean expressions in general, although in those the utility is doubtful:

* `(a, b) == (x, y)` would be equivalent to `a == x and b == y`
* `(a, b) <= (x, y)` would be equivalent to `a <= x and b <= y`
* No need (or support) for unspecified argument here.

# On hold

There are no plans to do any of these. We keep them around just in case.

* Loop unrolling: generate new names for temporary variables inside the loop. Probably not needed at the moment.
* Support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* Integrate a better code editor in the webapp, rather than a plain old `<textarea>`

# Refused

* Implement recursive calls by storing function return variable on stack like other variables, instead of pushing it 
  on the stack at the time of the call
  * Pro: storing return address is avoided on non-recursive calls (such as the first call of the function)
  * Pro: it would be possible to eliminate callrec and return instruction, making stackless and recursive calls more 
    similar.
  * Con: recursive calls are costlier (callrec + return have 6 instructions in total, while call + push + pop + goto 
    have 8).
  * In case of recursive-heavy algorithms (e.g. quicksort) the penalty is substantial. Recursive functions are 
    generally not very useful, and if someone is compelled to use them anyway, let's make them as efficient as 
    possible. 
* Make EOL an expression separator in addition to a semicolon and make expression separator compulsory.
  * Pro: removes ambiguity in function calls and perhaps other expressions, without having to put semicolon at the 
    end of the lines
  * Con: it would no longer be possible to split longer expressions on several lines without escaping EOLs. 