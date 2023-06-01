# Compiler directives

Mindcode allows you to alter some compiler options in the source code using special `#set` commands.
The basic syntax is: 

```
#set option = value
```

Some of these options can be alternatively specified as parameters of the command line compiler.

Supported compiler options are described below.

## Option `target`

Use the `target` option to specify the Mindcode version:

```
#set target = ML6
```

Possible values for this option are:
* `ML6`: compile for Mindcode Logic version 6
* `ML7S`: compile for Mindcode Logic version 7 standard processors
* `ML7W`: compile for Mindcode Logic version 7 world processor
* `ML7AS`: compile for Mindcode Logic version 7 (revision A) standard processors
* `ML7AW`: compile for Mindcode Logic version 7 (revision A) world processor

## Option `goal`

Use the `goal` option to specify whether Mindcode should prefer to generate smaller code, or faster code. 
Possible values are:

* `size`: Mindcode tries to generate smaller code.
* `speed`: Mindcode can generate additional instructions, if it makes the resulting code faster. Currently, this 
  distinction is employed by [loop optimization](#loop-optimization), which can duplicate parts of the code
  to avoid unnecessary jumps.
* `auto`: reserved for future use, at this moment the setting is identical to `size`.

The default setting at this moment is `speed`. If you're hitting the 1000 instruction limit, try setting the goal to 
`size`.

Over time, additional `speed` specific optimizations will be added, as well as an automatic process to find the best 
balance between speed and size. 

## Option `optimization`

Use the `optimization` option to set the optimization level of the compiler:

```
#set optimization = basic
```

Possible values for this option are:
* `off`
* `basic`
* `aggressive`

The `off` setting deactivates all optimizations. The `basic` setting performs most of the available optimizations.
The `aggressive` optimizations performs all the available optimizations, even those that might not be wanted in some 
contexts or which might make understanding or modifying the resulting mlog code particularly difficult. For example, 
assignments to variables that aren't used anywhere else in the program will be removed with this setting. When 
compiling a code snippet just so you would see how Mindcode handles it:

```
x0 = 0.001
y0 = 0.002
x1 = x0 * x0 + y0 * y0
y1 = 2 * x0 * y0
```
we get
```
set x0 0.001
set y0 0.002
op mul __tmp0 x0 x0
op mul __tmp1 y0 y0
op add x1 __tmp0 __tmp1
op mul __tmp3 2 x0
op mul y1 __tmp3 y0
end
```

However, when compiling using the aggressive optimization level, things change considerably: 

```
#set optimization = aggressive
x0 = 0.001
y0 = 0.002
x1 = x0 * x0 + y0 * y0
y1 = 2 * x0 * y0
```
produces
```
end
```

None of the variables `x0`, `y0`, `x1` or `y1` is used for anything after being set up, and therefore all are eliminated.

The default optimization level for the web application compiler is `basic`, for the command line compiler it is 
`aggressive`.

## Individual optimization options

It is possible to set the level of individual optimization tasks. Every optimization is assigned a name,
and this name can be used in the compiler directive like this:

```
#set dead-code-elimination = aggressive
```

Not all optimizations support the `aggressive` level. For those the level `aggressive` is the same as `basic`.
The complete list of available optimizations, including the option name for setting the level of given optimization
and availability of the aggressive optimization level is:

| Optimization                                                    | Option name                   | Aggressive |
|-----------------------------------------------------------------|-------------------------------|:----------:|
| [Jump normalization](#jump-normalization)                       | jump-normalization            |     N      |
| [Dead code elimination](#dead-code-elimination)                 | dead-code-elimination         |     Y      |
| [Single step elimination](#single-step-elimination)             | single-step-elimination       |     N      |
| [Temporary inputs elimination](#temporary-inputs-elimination)   | input-temp-elimination        |     N      |
| [Temporary outputs elimination](#temporary-outputs-elimination) | output-temp-elimination       |     N      |
| [Expression optimization](#expression-optimization)             | expression-optimization       |     N      |
| [Case expression optimization](#case-expression-optimization)   | case-expression-optimization  |     N      |
| [Conditional jump optimization](#conditional-jump-optimization) | conditionals-optimization     |     N      |
| [Jump straightening](#jump-straightening)                       | jump-straightening            |     N      |
| [Loop optimization](#loop-optimization)                         | loop-optimization             |     Y      |
| [If expression optimization](#if-expression-optimization)       | if-expression-optimization    |     N      |
| [Jump threading](#jump-threading)                               | jump-threading                |     Y      |
| [Unreachable code elimination](#unreachable-code-elimination)   | unreachable-code-elimination  |     Y      |
| [Stack optimization](#stack-optimization)                       | stack-optimization            |     N      |
| [Function call optimization](#function-call-optimization)       | function-call-optimization    |     N      |
| [Return value optimization](#return-value-optimization)         | return-value-optimization     |     N      |
| [Print merging](#print-merging)                                 | print-merging                 |     Y      |

You normally shouldn't need to deactivate any optimization, but if there was a bug in some of the optimizers,
deactivating it might allow you to use Mindcode until a fix is available. Partially activated optimizations
aren't routinely tested, so by deactivating one you might even discover some new bugs. On the other hand, full
optimization suite is tested by running compiled code on an emulated Mindustry processor, so bugs will hopefully
be rare. 

In particular, some optimizers expect to work on code that was already processed by different optimizations,
so turning off some optimizations might render other optimizations ineffective. (This is *not* a bug, though.)  

# Compiler optimization

Code optimization runs on compiled (ML) code. The compiled code is inspected for sequences of instructions
which can be removed or replaced by faster but equivalent ML code.

The information on compiler optimizations is a bit technical.
It might be useful if you're trying to better understand how Mindcode generates the ML code.

## Jump normalization

This optimization handles conditional jumps whose condition is constant:

* always false conditional jumps are removed,
* always true conditional jumps are converted to unconditional ones.

The first case reduces the code size and speeds up execution. The second one in itself improves neither size not speed,
but allows those jumps to be handled by other optimizations aimed at unconditional jumps.

These conditional jumps will only be generated by explicit code, e.g. `while false` or `while true`.
(Note that `if false ... end` won't even get compiled since constant expression evaluation was introduced.)
If a variable is involved -- `ACTIVE = false; if ACTIVE ... end` -- the jump won't be removed/replaced.

## Dead code elimination

This optimization inspects the entire code and removes all instructions that write to variables,
if none of the variables written to are actually read anywhere in the code.

This optimization support `basic` and `aggressive` levels of optimization. On the `aggressive` level,
the optimization removes all unused assignment, even assignments to main variables.

Dead code eliminator also inspects your code and prints out suspicious variables:
* _Unused variables_: those are the variables that were, or could be, eliminated. On `basic` level,
  some unused variables might not be reported.
* _Uninitialized variables_: those are variables that are read by the program, but never written to.
  (Mindcode doesn't - yet - detects situations where variable is read before it is first written to.)

Both cases deserve closer inspection, as they might be a result of a typo in a variable name.

## Single step elimination

The Mindcode compiler sometimes generates sequences of unconditional jumps where each jump targets the next instruction.
This optimization finds them and removes all such jumps.

Technically, if we have a sequence
```
0: jump 2 ...
1: jump 2 ...
2: ...
```
we could eliminate both jumps. This optimization will only remove the second jump, because before that removal the first
one doesn't target the next instruction. However, such sequences aren't typically generated by the compiler.

## Temporary inputs elimination

The compiler sometimes creates temporary variables whose only function is to pass value to another instruction.
This optimization removes all assignments to temporary variables that are only used as arguments
in a subsequent instruction. The `set` instruction is removed, while the other instruction is updated
to replace the temp variable with the value used in the set statement.

The optimization is performed only when the following conditions are met:
1. The `set` instruction assigns to a `__tmp` variable.
2. The `__tmp` variable is used in exactly one other instruction, which follows the `set` instruction
   (the check is based on absolute instruction sequence in the program, not on the actual program flow).
3. All arguments of the other instruction referencing the `__tmp` variable are input ones.

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables
are removed by the stack usage optimization down the line.

Note: changes to the way unoptimized code is generated rendered this particular optimizer pretty much useless. It is 
kept around just in case it might become useful again. 

## Temporary outputs elimination

The compiler sometimes creates temporary variables whose only function is to store output value of an instruction before passing it somewhere else.
This optimization removes all assignments to temporary variables that carry over the output value
of the preceding instruction. The `set` instruction is removed, while the preceding instruction is updated
to replace the temp variable with the target variable used in the set statement.

The optimization is performed only when the following conditions are met:
1. The `set` instruction assigns from a `__tmp` variable.
2. The `__tmp` variable is used in exactly one other instruction. The other instruction 
   immediately precedes the instruction producing the `__tmp` variable 
3. All arguments of the other instruction referencing the `__tmp` variable are output ones.

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables
are removed by the stack usage optimization down the line.

## Expression optimization

This optimization looks for sequences of mathematical operations that can be performed more efficiently. Currently,
the following optimizations are available:

* `floor` function called on a multiplication by a constant or a division. Combines the two operations into one
  integer division (`idiv`) operation. In case of multiplication, the constant operand is inverted to become the 
  for the divisor in the `idiv` operation.

## Case expression optimization

Case expressions allocate temporary variable to hold the value of the input expression.
This optimization removes unnecessary case expression variable (`__ast`) and replaces it with the original
variable containing the value of the case expression. The set instruction is removed, while the other instructions
are updated to replace the `__ast` variable with the one used in the set statement.

The optimization is performed only when the following conditions are met:
1. The set instruction assigns to an `__ast` variable.
2. The set instruction is the first of all those using the `__ast` variable (the check is based on absolute
   instruction sequence in the program, not on the actual program flow).
3. Each subsequent instruction using the `__ast` variable conforms to the code generated by the compiler
   (i.e. has the form of `jump target <condition> __astX testValue`)

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables
are removed by the stack usage optimization down the line.

## Conditional jump optimization

Conditional jumps are sometimes compiled into an `op` instruction evaluating a boolean expression,
and a conditional jump acting on the value of the expression.

This optimization turns the following sequence of instructions:
```
   op <comparison> var1 A B
   jump label equal var2 false
```

into

```
   jump label <inverse of comparison> A B
```

Requirements:
1. `jump` is an equal comparison to `false`
2. `var1` and `var2` are identical
3. `var1` is a `__tmp` variable
4. `<comparison>` has an inverse

## Jump straightening

This optimization detects situations where a conditional jump skips a following, unconditional one and replaces it
with a single conditional jump with a reversed condition and a target of the second jump. Example:

```
jump __label0 equal __tmp9 false
jump __label1
label __label0
```

will be turned to

```
jump __label1 notEqual __tmp9 false
```

Optimization won't be done if the condition does not have an inverse (`strictEqual`).

These sequences of instructions may arise when using break or continue statements:

```
while true
  ...
  if not_alive
    break
  end
end
```

## Loop optimization

The loop optimizers improves loops with the condition at the beginning by performing these modifications:

* If the loop jump condition is invertible, the unconditional jump at the end of the loop to the loop condition is 
  replaced by a conditional jump with inverted loop condition targeting the first instruction of the loop body. This 
  doesn't affect the number of instructions, but executes one less instruction per loop.
  * If the loop condition isn't invertible (that is, the jump condition is '==='), the optimization isn't done, 
    since the saved jump would be spent on inverting the condition, and the code size would increase for no benefit 
    at all.  
* If the previous optimization was done, the optimization level is set to `aggressive`, and the loop condition is 
  known to be true before the first iteration of the loop, the jump at the front of the loop is removed. Only the 
  simplest cases, where the loop control variable is set by an instruction immediately preceding the front jump and 
  the jump condition compares the control variable to a constant, are handled. Many loop conditions fit these 
  criteria though, namely all constant-range iteration loops.
* If the loop conditions is a complex expression spanning several instructions, it can still be replicated at the 
  end of the loop, if the code generation goal is set to `speed` (the default setting at the moment). Since this 
  increases the code size, at most three instructions are copied in this way. One instruction execution per loop is 
  still saved this way, at the price of increased code size.

The result of the first two optimizations in the list can be seen here:

```
#set loop-optimization = aggressive
for i in 0 ... 10
    cell1[i] = 1
end
print("Done.")
```

produces 

```
set i 0
write 1 cell1 i
op add i i 1
jump 1 lessThan i 10
print "Done."
end
```

Executing the entire loop (including the `i` variable initialization) takes 31 steps. Without optimization, the loop 
would require 42 steps. That's quite significant difference, especially for small loops.

The third modification is demonstrated here:

```
#set loop-optimization = aggressive
#set goal = speed

while switch1.enabled and switch2.enabled
    print("Doing something.")
end
print("A switch has been reset.")
```

which produces:

```
sensor __tmp0 switch1 @enabled
sensor __tmp1 switch2 @enabled
op land __tmp2 __tmp0 __tmp1
jump 9 equal __tmp2 false
print "Doing something."
sensor __tmp0 switch1 @enabled
sensor __tmp1 switch2 @enabled
op land __tmp2 __tmp0 __tmp1
jump 4 notEqual __tmp2 false
print "A switch has been reset."
end
```

## If expression optimization

This optimization consists of three types of modifications performed on blocks of code created by if/ternary 
expressions. All possible optimizations are done independently.

### Value propagation

The value of ternary expressions and if expressions is sometimes assigned to a user-defined variable. In these 
situations, the true and false branches of the if/ternary expression assign the value to a temporary variable, which 
is then assigned to the user variable. This optimization detects these situations and when possible, assigns the 
final value to the user variable directly in the true/false branches:

```
abs = if x < 0
    negative += 1;  // The semicolon is needed to separate the two statements
    -x
else
    positive += 1
    x
end
```

produces this code:

```
jump 4 greaterThanEq x 0
op add negative negative 1
op mul abs x -1
jump 6 always 0 0
op add positive positive 1
set abs x
end
```

As the example demonstrates, value propagation works on more than just the `set` instruction. All instructions 
producing a single value are handled, specifically:

* `set`
* `op`
* `read`
* `sensor`
* `packcolor`

### Forward assignment

Some conditional expressions can be rearranged to save instructions while keeping execution time unchanged:

```
print(x < 0 ? "negative" : "positive")
```

Without If expression optimization, the produced code is

```
jump 3 greaterThanEq x 0
set __tmp1 "negative"
jump 4 always 0 0
set __tmp1 "positive"
print __tmp1
```

Execution speed:
* x is negative: 4 instructions (0, 1, 2, 4) are executed,
* x is positive: 3 instructions (0, 3, 4) are executed. 

The if expression optimization turns the code into this:

```
set __tmp1 "positive"
jump 3 greaterThanEq x 0
set __tmp1 "negative"
print __tmp1
```

Execution speed:
* x is negative: 4 instructions (0, 1, 2, 3) are executed,
* x is positive: 3 instructions (0, 1, 3) are executed.

The execution time is the same. However, one less instruction is generated.

The forward assignment optimization can be done if at least one of the branches consist of just one instruction, and 
both branches produce a value which is then used. Depending on the type of condition and the branch sizes,
either true branch or false branch can get eliminated this way. Average execution time remains the same, although in 
some cases the number of executed instructions per branch can change by one (total number of instructions executed 
by both branches remains the same).

### Compound condition elimination

The instruction generator always generates true branch first. In some cases, the jump condition cannot be expressed 
as a single jump and requires additional instruction (this only happens with the strict equality operator `===`, 
which doesn't have an opposite operating in Mindustry Logic).

The additional instruction can be avoided when the true and false branches in the code are swapped. When this 
optimizer detects such a situation, it does exactly that:

```
if @unit.dead === 0
    print("alive")
else
    print("dead")
end
```

Notice the `print("dead")` occurs before `print("alive")` now:

```
sensor __tmp0 @unit @dead
jump 4 strictEqual __tmp0 0
print "dead"
jump 0 always 0 0
print "alive"
end
```

### Chained if-else statements

The `elsif` statements are equivalent to nesting the elsif part in the `else` branch of the outer expression. 
Optimizations of these nested statements work as expected:

```
y = if x < 0
    "negative"
elsif x > 0
    "positive"
else
    "zero"
end
```

produces

```
set y "negative"
jump 5 lessThan x 0
set y "zero"
jump 5 lessThanEq x 0
set y "positive"
end
```

saving three instructions over the code without if statement optimization:

```
jump 3 greaterThanEq x 0
set __tmp1 "negative"
jump 8 always 0 0
jump 6 lessThanEq x 0
set __tmp3 "positive"
jump 7 always 0 0
set __tmp3 "zero"
set __tmp1 __tmp3
set y __tmp1
end
```

## Jump threading

If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected
to the target of the second jump, repeated until the end of jump chain is reached. Moreover:

* on `aggressive` level, `end` instruction is handled identically to `jump 0 always`,
* conditional jumps in the jump chain are followed if
  * their condition is identical to the condition the first jump, and
  * the condition arguments do not contain a volatile variable (`@time`, `@tick`, `@counter` etc.).

No instructions are removed or added, but the execution of the code is faster.

## Unreachable code elimination

This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might appear:

1. Jump threading can create unreachable jumps that are no longer targeted.
2. User-created unreachable regions, such as `while false ... end`.
3. User defined functions which are called from an unreachable region.

Instruction removal is done in loops until no instructions are removed. This way entire branches
of unreachable code (i.e. all code inside the `while false ... end` statement) should be eliminated,
assuming the unconditional jump normalization optimizer was also active.

The `end` instruction, even when not accessible, is not removed unless the optimization level is `aggressive`.
Main body program and function definitions are each terminated by the `end` instruction and removing it
might make the produced code somewhat less readable.

## Stack optimization

Optimizes the stack usage -- eliminates `push`/`pop` instruction pairs determined to be unnecessary. Several
independent optimizations are performed:

* `push`/`pop` instruction elimination for variables that are not used anywhere apart from the push/pop instructions.
  This happens when variables are eliminated by other optimizations. The optimization is done globally, in a single 
  pass across the entire program.
* `push`/`pop` instructions elimination for variables that are read neither by any instruction between the call 
  instruction and the end of the function, nor by any instruction which is part of the same loop as the call 
  instruction. 

## Function call optimization

This optimizer eliminates unnecessary function parameters and local variables (replaces them by the argument
or value assigned to them). Significantly improves inline functions, but seldom might improve other function calls
as well. The optimizer processes individual functions (inline and out-of-line) one by one and searches for 
`set` instructions assigning a value to a variable (i.e. `set target value`) and checks these preconditions are met:

1. The target variable is a local variable or parameter -- has the function prefix followed by an underscore,
   e.g. `__fn0_`. This excludes `__fnXretaddr` and `__fnXretval` variables, which must be preserved.
2. The target variable is modified exactly once, i.e. there isn't any other instruction besides the original `set`
   instruction which would modify the variable. This includes `push`/`pop` instructions, but if those are unnecessary,
   they would be already removed by prior optimizers.
3. The value being assigned to the target variable is not volatile (e.g. `@time`, `@tick`, `@counter` etc.)
   and is not modified anywhere in the function.
4. If the function contains a `call` instruction, the value is not a global variable. Global variables might be
   modified by the called function. (Block names, while technically also global, are not affected, as these are
   effectively constant.)

When the conditions are met, the following happens:
* The original instruction assigning value to the target variable is removed.
* Every remaining occurrence of target variable is replaced with the assigned value.

Functions are located in the code using the entry and exit labels marked with function prefix.

## Return value optimization

Optimizes passing return values to callers.

Function return values are carried by `__retval` variables instead of `__tmp` ones, because the original variable 
providing the return value -- `__fnXretval` -- might get overwritten during another function call before the return 
value is used. Standard temporary variable optimizations are not applied to `__retval`s.

This optimizer looks for a set instruction in the form `set __retvalX variable`. The `__retvalX` is expected to be 
used by one other instruction. The optimizer removes the set instruction and replaces the `__retvalX` by `variable`
in the other instruction if the following conditions are met:

1. The `__retval` variable is used in exactly one other instruction, which follows the set instruction (the check is 
   based on absolute instruction sequence in the program, not on the actual program flow). Push and pop instructions 
   aren't considered.
2. The block of code between the `set` instruction and the other instruction is localized (doesn't contain jumps into 
   the code block from the outside -- function calls aren't considered). Range iteration loop may produce such code.
3. The other variable is not modified in the code block.
4. If the variable is a `__fnXretval`: the code block must not contain any function calls - not just calls to the 
   `__fnX` function, but calls to any function - we don't know what may happen inside a function call.
5. If the variable is not a `__fnXretval`: the variable must not be volatile, and if it is global, the code block 
   must not contain any function calls.

## Print merging

This optimization merges together print instructions with string literal arguments.
The print instructions will get merged even if they aren't consecutive, assuming there aren't instructions
that could break the print sequence (`jump`, `label` or `print <variable>`).

Effectively, this optimization eliminates a `print` instruction by turning this

```
println("Items: ", items)
println("Time: " @time)
```
into this:

```
print("Items: ", items)
print("\nTime: ", @time "\n")
```

On `aggressive` level, all constant values - not just string constants - are merged. For example:

```
#set optimization = aggressive
const MAX_VALUE = 10
printf("Step $i of $MAX_VALUE\n")
```

produces
```
print "Step "
print i
print " of 10\n"
```

On basic optimization level, the output would be:

```
print "Step "
print i
print " of "
print 10
print "\n"
```
### String length limit

On `basic` level, the optimization won't merge print instructions if the merge would produce a string
longer than 34 characters (36 when counting the double quotes). On `aggressive` level, such instructions
will be merged regardless. This can create long string constants, but according to our tests these can be pasted
into Mindustry processors even if they're longer than what the Mindustry GUI allows to enter.

---

[« Previous: Functions](SYNTAX-4-FUNCTIONS.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
