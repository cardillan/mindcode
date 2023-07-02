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

## Option `memory-model`

This option has been added to support future enhancements of Mindcode. Setting the option doesn't have any effect at 
this moment. 

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

| Optimization                                                        | Option name                  | Aggressive |
|---------------------------------------------------------------------|------------------------------|:----------:|
| [Jump Normalization](#jump-normalization)                           | jump-normalization           |     N      |
| [Dead Code Elimination](#dead-code-elimination)                     | dead-code-elimination        |     Y      |
| [Single Step Elimination](#single-step-elimination)                 | single-step-elimination      |     N      |
| [Temporary Variables Elimination](#temporary-variables-elimination) | temp-variables-elimination   |     N      |
| [Expression Optimization](#expression-optimization)                 | expression-optimization      |     N      |
| [Case Expression Optimization](#case-expression-optimization)       | case-expression-optimization |     N      |
| [Conditional Jump Optimization](#conditional-jump-optimization)     | conditionals-optimization    |     N      |
| [Jump Straightening](#jump-straightening)                           | jump-straightening           |     N      |
| [Loop Optimization](#loop-optimization)                             | loop-optimization            |     Y      |
| [If Expression Optimization](#if-expression-optimization)           | if-expression-optimization   |     N      |
| [Data Flow optimization](#data-flow-optimization)                   | data-flow-optimization       |     Y      |
| [Jump Threading](#jump-threading)                                   | jump-threading               |     Y      |
| [Unreachable Code Elimination](#unreachable-code-elimination)       | unreachable-code-elimination |     Y      |
| [Stack Optimization](#stack-optimization)                           | stack-optimization           |     N      |
| [Print Merging](#print-merging)                                     | print-merging                |     Y      |

You normally shouldn't need to deactivate any optimization, but if there was a bug in some of the optimizers,
deactivating it might allow you to use Mindcode until a fix is available. Partially activated optimizations
aren't routinely tested, so by deactivating one you might even discover some new bugs. On the other hand, full
optimization suite is tested by running compiled code on an emulated Mindustry processor, so bugs will hopefully
be rare. 

In particular, some optimizers expect to work on code that was already processed by different optimizations,
so turning off some optimizations might render other optimizations ineffective. (This is *not* a bug, though.)  

# Compiler optimization

Code optimization runs on compiled (ML) code. The compiled code is inspected for sequences of instructions
which can be removed or replaced by equivalent, but superior sequence of instructions. The new sequence might be 
smaller than the original one, or even larger than the original one, if it is executing faster
(see the [`goal` option](#option-goal)).  

The information on compiler optimizations is a bit technical. It might be useful if you're trying to better 
understand how Mindcode generates the ML code.

## Jump Normalization

This optimization handles conditional jumps whose condition can be fully evaluated:

* always false conditional jumps are removed,
* always true conditional jumps are converted to unconditional ones.

A condition can be fully evaluated constant if both of its operands are literals, or if they're variables whose values 
were determined to be constant by the data flow analysis. 

The first case reduces the code size and speeds up execution. The second one in itself improves neither size not speed,
but allows those jumps to be handled by other optimizations aimed at unconditional jumps.

## Dead Code Elimination

This optimization inspects the entire code and removes all instructions that write to variables,
if none of the variables written to are actually read anywhere in the code.

This optimization support `basic` and `aggressive` levels of optimization. On the `aggressive` level,
the optimization removes all unused assignment, even assignments to a global or a main variable.

Dead code eliminator also inspects your code and prints out suspicious variables:
* _Unused variables_: those are the variables that were, or could be, eliminated. On `basic` level,
  some unused variables might not be reported.
* _Uninitialized variables_: those are variables that are read by the program, but never written to.
  (The Data Flow optimization performs a more thorough analysis which is able to detect variables that might be 
  read by the program before they were first written to. A single variable might be therefore reported as 
  uninitialized twice.)

Both cases deserve closer inspection, as they might be a result of a typo in a variable name.

## Single Step Elimination

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

## Temporary Variables Elimination

The compiler sometimes creates temporary variables whose only function is to store output value of an instruction 
before passing it somewhere else. This optimization removes all assignments to temporary variables that carry over 
the output value of the preceding instruction. The `set` instruction is removed, while the preceding instruction is 
updated to replace the temp variable with the target variable used in the set statement.

The optimization is performed only when the following conditions are met:
1. The `set` instruction assigns from a temporary variable.
2. The temporary variable is used in exactly one other instruction. The other instruction 
   immediately precedes the instruction producing the temporary variable 
3. All arguments of the other instruction referencing the temporary variable are output ones.

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables
are removed by the stack usage optimization down the line.

## Expression Optimization

This optimization looks for sequences of mathematical operations that can be performed more efficiently. Currently,
the following optimizations are available:

* `floor` function called on a multiplication by a constant or a division. Combines the two operations into one
  integer division (`idiv`) operation. In the case of multiplication, the constant operand is inverted to become the
  divisor in the `idiv` operation.
* All set instructions assigning a variable to itself (e.g. `set x x`) are removed. 

## Case Expression Optimization

Case expressions allocate temporary variable to hold the value of the input expression, even if the input expression 
is actually a user defined variable. This optimization detects these instances, removes the temporary variable 
and replaces it with the original variable containing the value of the input expression. The set instruction is 
removed, while the other instructions are updated to replace the temporary variable with the one used in the set 
statement.

The optimization is performed only when the following conditions are met:
1. The set instruction assigns to a case-expression temporary variable.
2. The set instruction is the first of all those using the temporary variable (the check is based on absolute
   instruction sequence in the program, not on the actual program flow).
3. Each subsequent instruction using the temporary variable conforms to the code generated by the compiler
   (i.e. has the form of `jump target <condition> __astX testValue`)

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables
are removed by the stack usage optimization down the line.

## Conditional Jump Optimization

Conditional jumps are sometimes compiled into an `op` instruction evaluating a boolean expression, and a conditional 
jump acting on the value of the expression.

This optimization turns the following sequence of instructions:
```
   op <comparison> var A B
   jump label equal/notEqual var false
```

into

```
   jump label <inverse of comparison>/<comparison> A B
```

Requirements:
1. `jump` is an `equal`/`notEqual` comparison to `false`
2. `var` is a temporary variable
3. `var` is not used anywhere in the program except these two instructions
4. `<comparison>` has an inverse/`<comparison>` exists as a condition

## Jump Straightening

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

## Loop Optimization

The loop optimization improves loops with the condition at the beginning by performing these modifications:

* If the loop jump condition is invertible, the unconditional jump at the end of the loop to the loop condition is 
  replaced by a conditional jump with inverted loop condition targeting the first instruction of the loop body. This 
  doesn't affect the number of instructions, but executes one less instruction per loop.
  * If the loop condition isn't invertible (that is, the jump condition is '==='), the optimization isn't done, 
    since the saved jump would be spent on inverting the condition, and the code size would increase for no benefit 
    at all.  
* If the previous optimization was done, the optimization level is set to `aggressive`, and the loop condition is 
  known to be true before the first iteration of the loop, the optimizer removes the jump at the front of the loop. 
  Only the simplest cases, where the loop control variable is set by an instruction immediately preceding the front 
  jump and the jump condition compares the control variable to a constant, are handled. Many loop conditions fit these 
  criteria though, namely all constant-range iteration loops.
* Loop conditions that are a complex expression spanning several instructions can still be replicated at the 
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

## If Expression Optimization

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

Without If Expression Optimization, the produced code is

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

## Data Flow optimization

This optimization inspects the actual data flow in the program and removes instructions and variables (both user 
defined and temporary) that are dispensable or have no effect on the program execution. Each individual optimization 
performed is described separately below.

Data Flow optimizations can have profound effect on the resulting code. User-defined variables can get completely 
eliminated, and variables in expressions can get replaced by different variables that were determined to hold the 
same value. The goal of these replacements is to allow eliminating some instructions. The optimizer doesn't try to 
avoid variable replacements that do not lead to instruction elimination - this would make the resulting code more 
understandable, but the optimizer would have to be more complex and therefore more prone to errors.

> **Note:** One of Mindcode goals is to support making small changes to the compiled code, allowing users to change 
> crucial parameters in the compiled code without a need to recompile entire program. To this end, assignments to 
> [global variables](SYNTAX-1-VARIABLES.markdown#global-variables) are never removed. Any changes to `set` 
> instructions in the compiled code assigning value to global variables are fully supported and the resulting program
> is fully functional, as if the value was assigned to the global variable in the source code itself.
>
> [Main variables](SYNTAX-1-VARIABLES.markdown#main-variables) are treated in the same way, unless the optimization
> level is set to `aggressive`. In this case, main variables can be completely removed by the program, and even if
> they stay in the compiled code, changing the value assigned to a main variable may not produce the same effect as 
> compiling the program with the other value. In other words, changing a value assigned to main variable in the 
> compiled code may break the compiled program.  

### Handling of uninitialized variables

The data flow analysis reveals cases where variables might not be properly initialized, i.e. situations where a 
value of a variable is read before it is known that some value has been written to the variable. Warnings are 
generated for each uninitialized variable found.

Since Mindustry Logic executes the code repeatedly while preserving variable values, not initializing a variable 
might be a valid choice, relying on the fact that all variables are assigned a value of `null` by Mindustry at the 
beginning. If you intentionally leave a variable uninitialized, you may just ignore the warning. To avoid the 
warning, move the entire code into an infinite loop and initialize the variables before that loop. For example:

```
count += 1
print(count)
printflush(message1)
```

can be rewritten as

```
count = 0
while true
    count += 1
    print(count)
    printflush(message1)
end
```

Data Flow optimization assumes that values assigned to uninitialized variables might be reused on the next program 
execution. Assignments to uninitialized variables before calling the `end()` function are therefore protected, while 
assignments to initialized variables aren't - they will be overwritten on the next program execution anyway:

```
#set optimization = aggressive
foo = rand(10)
if initialized == 0
print("Initializing...")
// Do some initialization
initialized = 1
foo = 1
end()
end
print("Doing actual work")
print(initialized)
print(foo)
```

produces this code:

```
op rand foo 10 0
jump 5 notEqual initialized 0
print "Initializing..."
set initialized 1
end
print "Doing actual work"
print initialized
print foo
end
```

Notice the `initialized = 1` statement is preserved, while `foo = 1` is not.

See also [`end()` function](SYNTAX-3-STATEMENTS.markdown#end-function).

### Unnecessary assignment elimination

All assignments to variables (except for global variables and main variables on `basic` level) are inspected and 
unnecessary assignments are removed. The assignment is unnecessary if the variable is not read after being assigned, 
or if it is not read before another assignment to the variable is made:

```
#set optimization = aggressive
a = rand(10)
a = rand(20)
print(a)
a = rand(30)
```

compiles to:

```
op rand a 20 0
print a
end
```

The first assignment to `a` is removed, because `a` is not read before another value is assigned to it. The last
assignment to `a` is removed, because `a` is not read after that assignment at all.

An assignment can also become unnecessary due to other optimizations carried by this optimizer. 

### Constant propagation

When a variable is used in an instruction and the value of the variable is known to be a constant value, the variable 
itself is replaced by the constant value. This can in turn make the original assignment unnecessary. See for example:

```
#set optimization = aggressive
a = 10
b = 20
c = @tick + b
printf("$a, $b, $c.")
```

produces

```
op add c @tick 20
print "10, 20, "
print c
print "."
end
```

### Constant folding

Constant propagation described above ensures that constant values are used instead of variables where possible. When 
a deterministic operation is performed on constant values (such as addition by the `op add` instruction), constant 
folding evaluates the expression and replaces the operation with the resulting value, eliminating an instruction.
For example:

```
#set optimization = aggressive
a = 10
b = 20
c = a + b
printf("$a, $b, $c.")
```

produces

```
print "10, 20, 30."
end
```

Looks quite spectacular, doesn't it? Here's what happened:

* The optimizer figured out that variables `a` and `b` are not needed, because they only hold a constant value.
* Then it found out the `c = a + b` expression has a constant value too.
* What was left was a sequence of print statements, each printing a constant value.
  [Print Merging optimization](#print-merging) on `aggressive` level then merged it all together.

Not every opportunity for constant folding is detected at this moment. While `x = 1 + y + 2` is optimized to
`op add x y 3`, `x = 1 + y + z + 2` it too complex to process as this moment and the constant values of `1` and `2` 
won't be added at compile time. 

If the result of a constant expression doesn't have a valid mlog representation, the optimization is not performed. 
In other cases, [loss of precision](SYNTAX.markdown#numeric-literals-in-mindustry-logic) might occur. 

### Common subexpressions optimization

The Data flow optimizer keeps track of expressions that have been evaluated. When the same expression is encountered 
for a second (third, fourth, ...) time, the result of the last computation is reused instead of evaluating the 
expression again. In the following code:    

```
a = rand(10)
b = a + 1
c = 1 + a + 1
d = 1 + a + 2
print(a, b, c, d)
```

the optimizer notices that the value `a + 1` was assigned to `b` after it was computed for the first time
and reuses it in the subsequent instructions: 

```
op rand a 10 0
op add b a 1
op add c 1 b
op add d 2 b
print a
print b
print c
print d
end
```

Again, not every possible opportunity is used. Instructions are not rearranged, for example, even if doing so would 
allow more evaluations to be reused.

On the other hand, entire complex expressions are reused if they're identical. In the following code

```
a = rand(10)
b = rand(10)
x = 1 + sqrt(a * a + b * b)
y = 2 + sqrt(a * a + b * b)
print(x, y)
```

the entire square root is evaluated only once: 

```
op rand a 10 0
op rand b 10 0
op mul __tmp2 a a
op mul __tmp3 b b
op add __tmp4 __tmp2 __tmp3
op sqrt __tmp5 __tmp4 0
op add x 1 __tmp5
op add y 2 __tmp5
print x
print y
end
```

### Function call optimizations

Variables and expressions passed as arguments to inline functions, as well as return values of inline functions, are 
processed in the same way as other local variables. Using an inlined function therefore doesn't incur any overhead 
at all in Mindcode.

Data flow analysis, with some restrictions, is also applied to stackless and recursive function calls. Assignments 
to global variables inside stackless and recursive functions are tracked and properly handled. Optimizations are 
applied to function arguments and return values. This optimization has completely replaced earlier _Function call 
optimization_ and _Return value optimization_ - all optimizations that could be performed by those optimizations 
(and some that couldn't) are performed by Data Flow optimization now.

## Jump Threading

If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected
to the target of the second jump, repeated until the end of jump chain is reached. Moreover:

* on `aggressive` level, `end` instruction is handled identically to `jump 0 always`,
* conditional jumps in the jump chain are followed if
  * their condition is identical to the condition the first jump, and
  * the condition arguments do not contain a volatile variable (`@time`, `@tick`, `@counter` etc.).

No instructions are removed or added, but the execution of the code is faster.

## Unreachable Code Elimination

This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might appear:

1. Jump Threading can create unreachable jumps that are no longer targeted.
2. User-created unreachable regions, such as `while false ... end`.
3. User defined functions which are called from an unreachable region.

Instruction removal is done in loops until no instructions are removed. This way entire branches
of unreachable code (i.e. all code inside the `while false ... end` statement) should be eliminated,
assuming the unconditional jump normalization optimizer was also active.

The `end` instruction, even when not accessible, is not removed unless the optimization level is `aggressive`.
Main body program and function definitions are each terminated by the `end` instruction and removing it
might make the produced code somewhat less readable.

## Stack Optimization

Optimizes the stack usage -- eliminates `push`/`pop` instruction pairs determined to be unnecessary. The following 
optimizations are performed:

* `push`/`pop` instruction elimination for function variables that are not used anywhere apart from the push/pop 
  instructions. This happens when variables are eliminated by other optimizations. The optimization is done globally,
  in a single pass across the entire program.
* `push`/`pop` instruction elimination for function variables that are read neither by any instruction between the 
  call instruction and the end of the function, nor by any instruction which is part of the same loop as the call 
  instruction. Implicit reads by recursive calls to the same function with the value of the parameter unchanged are 
  also detected.
* `push`/`pop` instruction elimination for function parameters/variables that are never modified within the function.

## Print Merging

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
