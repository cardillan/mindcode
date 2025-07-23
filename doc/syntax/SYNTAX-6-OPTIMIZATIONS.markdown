# Dynamic optimizations

Most of the optimizations Mindcode performs are fairly simple: they're relatively small changes to the code that are known to improve the code size, the (average) execution time, or both, while also not making either of these two metrics worse. We call these optimizations _static optimizations_. A few of the optimizers are capable of performing more complex optimizations that may increase the code size to achieve a better performance or sacrifice some performance to decrease code size. These optimizations are called _dynamic optimizations_.

Mindcode provides the [`goal` option](SYNTAX-5-OTHER.markdown#option-goal) to specify which dynamic optimizations to apply:

* `speed` (the default value): Mindcode applies dynamic optimizations that make the resulting code larger, but faster, while adhering to the current [instruction limit](SYNTAX-5-OTHER.markdown#option-instruction-limit). When several possible optimizations of this kind are available, the ones having the best effect (the highest speedup per additional instruction generated) are applied first until the instruction limit is reached.
* `neutral`: Mindcode applies dynamic optimizations making the code either smaller or faster (or both) than the original code.
* `size`: Mindcode applies dynamic optimizations leading to the smallest code possible, even at the expense of execution speed.

## Optimization efficiency

To decide which dynamic optimizations to apply given the optimization goal, three metrics are computed for each possible optimization:

* **Size**: the number of additional instructions that will be created by the optimization. When the optimization reduces the number of instructions, the **Size** is negative.
* **Benefit**: a measure of execution improvement achieved by the optimization. The **Benefit** is negative when the optimization results in a slower code execution.
* **Efficiency**: this measure gives the overall value of the optimization, which allows it to be compared with other optimizations. The **Efficiency** computation depends on the optimization goal:
  * `speed`: the **Efficiency** of the optimization is computed by dividing the **Benefit** by the **Size**, providing a measure of execution speed improvement per additional instruction of code. When the **Size** is zero or negative, the **Efficiency** is infinite.
  * `neutral`: the **Efficiency** of the optimization is computed as a negative product of the **Size** and the **Benefit**.
  * `size`: in this case, the optimizer always chooses the smallest code, so the **Efficiency** equals to the negative **Size** (i.e., code size savings) of the optimization.  

## Optimization benefit

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

## Optimization for speed

When the optimization goal is `speed`, there is a constraint on the total code size (1000 instructions by default). The optimizations are applied in the order of effectiveness until the total code size reaches the constraint, or all optimization opportunities are exhausted.

Some optimizers (e.g., [Case Switching](#case-switching)) can produce several different ways to optimize the same portion of code, of which one needs to be selected. For the `speed` optimization goal, the entire group of mutually exclusive optimizations is evaluated against other possible optimizations, choosing the most effective optimization while respecting the total code size constraint. 

Oftentimes, after an optimization is applied, opportunities for further optimizations crop up. The additional static optimizations might even reduce the code size again, providing space for further dynamic optimizations. Mindcode is unable to include the effects of these static optimizations when computing dynamic optimization effects, but uses the additional instruction space when applying remaining optimizations.     

Mindcode writes the possible optimizations it considers to the log file. For better understanding, this includes optimizations exceeding the total code size constraint by a small margin. It is possible to let Mindcode perform additional optimizations by changing the value of the total code size constraint, using the  [`instruction-limit` option](SYNTAX-5-OTHER.markdown#option-instruction-limit). Sometimes, increasing the instruction limit a bit can produce a code which still fits into the total code size constraint. Here's a [demonstration](https://github.com/cardillan/mindcode/discussions/106) of this approach being applied to a real-life code example.  

## Optimization for size and neutral optimization

These two optimization goals do not increase code size and therefore aren't subject to the total code size constraint. In these cases, all possible optimizations are eventually applied. If there's a group of mutually exclusive optimizations, the best one according to the optimization goal is always selected. 

# Individual Mindcode optimization

Code optimization runs on compiled (mlog) code. The compiled code is inspected for sequences of instructions which can be removed or replaced by a functionally equivalent, but shorter and/or faster sequence of instructions. The new sequence might even be longer than the original one if it is executing faster (see the [`goal` option](SYNTAX-5-OTHER.markdown#option-goal)).

The information on compiler optimizations is a bit technical. It might be useful if you're trying to better understand how Mindcode generates the mlog code.

## Temporary Variables Elimination

The compiler sometimes creates temporary variables whose only function is to carry some value to another instruction. This optimization removes such temporary variables that only carry the value to an adjacent instruction. The `set` instruction is removed, while the adjacent instruction is updated to replace the temporary variable with the other variable used in the `set` instruction.

The optimization is performed only when the following conditions are met:

* The `set` instruction assigns/reads a temporary variable.
* The temporary variable is used in exactly one other instruction, adjacent to the `set` instruction.
* All arguments of the other instruction referencing the temporary variable are either input ones (the `set` instruction precedes the other instruction) or output ones (the `set` instruction follows the other instruction).

An additional optimization is performed when an instruction has a temporary output variable which isn't read by any other instruction. In this case, the unused output variable is replaced by `0` (literal zero value). Such an instruction will be executed correctly by Mindustry Logic, but a new variable will be allocated for the replaced argument.   

`push` and `pop` instructions are ignored by the above algorithm. `push`/`pop` instructions of any eliminated variables are removed by the [Stack Optimization](#stack-optimization) down the line.

## Case Expression Optimization

Case expressions allocate a temporary variable to hold the value of the input expression, even if the input expression is actually a user-defined variable. This optimization detects these instances, removes the temporary variable, and replaces it with the original variable containing the value of the input expression. The set instruction is removed, while the other instructions are updated to replace the temporary variable with the one used in the set statement.

The optimization is performed only when the following conditions are met:

* The set instruction assigns to a case-expression temporary variable.
* The set instruction is the first of all those using the temporary variable (the check is based on the absolute instruction sequence in the program, not on the actual program flow).
* Each following instruction using the temporary variable conforms to the code generated by the compiler (i.e., has the form of `jump target <condition> *tmpX testValue`)

## Dead Code Elimination

This optimization inspects the entire code and removes all instructions that write to non-volatile variables if none of the variables written to are actually read anywhere in the code.

Dead Code Elimination also inspects your code and prints out suspicious variables:
* _Unused variables_: those are the variables that are unused and possibly were eliminated.
* _Uninitialized variables_: those are global variables that are read by the program but never written to. (The [Data Flow Optimization](#data-flow-optimization) detects uninitialized local and function variables.)

Both cases deserve a closer inspection, as they might indicate a problem with your program.

## Jump Normalization

This optimization handles conditional jumps whose condition can be fully evaluated:

* constantly false conditional jumps are removed,
* constantly true conditional jumps are converted to unconditional ones.

A condition can be fully evaluated if both of its operands are literals or if they're variables whose values were determined to be constant by the [Data Flow Optimization](#data-flow-optimization). 

The first case reduces the code size and speeds up execution. The second one in itself improves neither size nor speed but allows those jumps to be handled by other optimizations aimed at unconditional jumps.

## Jump Optimization

Conditional jumps are sometimes compiled into an `op` instruction evaluating a boolean expression, and a conditional jump acting on the value of the expression.

This optimization turns the following sequence of instructions:

```
op <comparison> var A B
jump label equal/notEqual var false
```

into

```
jump label <inverse of comparison>/<comparison> A B
```

Prerequisites:

* `jump` is an `equal`/`notEqual` comparison to `false`,
* `var` is a temporary variable,
* `var` is not used anywhere in the program except these two instructions,
* `<comparison>` has an inverse/`<comparison>` exists as a condition.

## Single Step Elimination

This optimizer simplifies the following sequences of jumps that are a result of the code generation and various optimizations:

* A conditional or unconditional jump targeting the next instruction.
* A conditional or unconditional jump is removed if there is an identical jump immediately following it. The second jump may be a target of another jump. 
* The `end` and `jump 0 always` instructions at the very end of the program are removed, as the processor will jump to the first instruction of the program upon reaching the end of the instruction list anyway, saving execution of one instruction.
* A jump is removed if there is an identical jump preceding it, and these conditions are met:
  * the jump doesn't contain volatile variables,
  * there are no instructions affecting control flow between the jumps, including landing points of other jumps,
  * there are no instructions modifying the jump condition variables between the jumps. 

The rationale behind the optimization described in the last point is that if any of the removed conditional jumps conditions were evaluated to `true`, so would be the condition of the first jump in the sequence, so the other jumps cannot fire, even though the value of the condition isn't known at the compile time. These sequences of jumps may appear as a result of unrolled loops.    

Note: this optimization does not affect jumps that are part of a larger structure (e.g., jump tables).

## Expression Optimization

This optimization looks for certain expressions that can be performed more efficiently. Currently, the following optimizations are available:

* `floor` instruction applied to a result of a multiplication by a constant or a division. Combines the two operations into one integer division (`idiv`) operation. In the case of multiplication, the constant operand is inverted to become the divisor in the `idiv` operation.
* `select` instruction with constant condition is replaced by a `set` instruction directly.
* `sensor var @this @x` and `sensor var @this @y` are replaced by `set var @thisx` and `set var @thisy` respectively. Data Flow Optimization may then apply [constant propagation](#constant-propagation) to the `@thisx`/`@thisy` built-in constants.
* All set instructions assigning a variable to itself (e.g., `set x x`) are removed.
* When both operands of the instruction are known to have the same value, some operations always produce a fixed value. If this is the case, the operation is replaced by a `set` instruction setting the target variable to the fixed value:
  * `equal`, `lessThanEq`, `greaterThanEq`, `strictEqual`: sets the result to `1` (true) 
  * `notEqual`, `lessThan`, `greaterThan`: sets the result to `0` (false)
  * `sub`, `xor`: sets the result to `0`
  * `or`, `land`: sets the result directly to the first operand, if the instruction doesn't represent the boolean version of the operation (`||` or `&&`),
  * `and`, `min` and `max`: sets the result directly to the first operand.
* The result of some operations may be determined by a known value of one of its operands. In some cases, the result doesn't depend on the other operand (a multiplication by zero is always zero regardless of the other operand). In other cases, the result is equal to the other operand (a multiplication by one or a subtraction of zero). All the performed replacements are listed in this table:

| Operation      | Operator     | First operand | Second operand | Result |  Level   | Note        |
|----------------|:-------------|:-------------:|:--------------:|:------:|:--------:|-------------|
| mul            |              |      var      |       1        |  var   |  Basic   | Commutative |
| mul            |              |      var      |       0        |   0    |  Basic   | Commutative |
| div            |              |      var      |       1        |  var   |  Basic   |             |
| div, idiv, mod |              |      var      |       0        |  null  |  Basic   |             |
| div, idiv, mod |              |       0       |      var       |   0    | Advanced |             |
| add, xor       |              |      var      |       0        |  var   |  Basic   | Commutative |
| sub            |              |      var      |       0        |  var   |  Basic   |             |
| shl, shr       |              |      var      |       0        |  var   | Advanced |             |
| shl, shr       |              |       0       |      var       |   0    |  Basic   |             |
| or             | `\|`, `or`   |      var      |       0        |  var   | Advanced | Commutative |
| or             | `\|\|`, `or` |      var      |    nonzero     |   1    |  Basic   | Commutative |
| and, land      |              |      var      |       0        |   0    |  Basic   | Commutative |
| land           | `and`        |      var      |    nonzero     |  var   | Advanced | Commutative |

In the case of commutative operations, the result is the same if the first and second operands are swapped. `var` represents a variable with an arbitrary, unknown value.

Some mlog operations are produced by different Mindcode operators. When the optimizations are only applied to operations corresponding to specific Mindcode operators, these operators are listed in the **Operator** column.  

> [!IMPORTANT]
> Some optimizations applied to `or`, `and`, `land`, `xor`, `shl` and `shr` operations applied to non-integer or non-boolean values may produce different results from unoptimized code: unoptimized code would result into an integer value (or boolean value in case of `land`), while the optimized code may produce a non-integer value. Passing a non-integer/non-boolean value into these operators is unusual, but not impossible. These optimizations are therefore only performed on `advanced` level and can be turned off by setting the level to `basic`.

When the `builtin-evaluation` option is set to `compatible` or `full`, the following additional expressions are handled:

* If the `@constant` in a `sensor var @constant @id` instruction is a known item, liquid, block or unit constant, the Mindustry's ID of the objects is looked up and the instruction is replaced by `set var <id>`, where `<id>` is a numeric literal.
* If the `id` in a `lookup <type> id` instruction is a constant, Mindcode searches for the appropriate item, liquid, block, or unit with given ID and if it finds one, the instruction is replaced by `set var <built-in>`, where `<built-in>` is an item, liquid, block, or unit literal.

Some Mindustry content objects may have different logic IDs in different Mindustry versions (these objects are called "unstable"). For these objects, the above optimizations only happen when the [`builtin-evaluation` option](SYNTAX-5-OTHER.markdown#option-builtin-evaluation) is set to `full`:

```Mindcode
#set target = 7;
#set builtin-evaluation = full;
println(lookup(:item, 18));
println(@tungsten.@id);
printflush(message1);
```

compiles to 

```mlog
print "dormant-cyst\n19\n"
printflush message1
```

When compiled with `builtin-evaluation` set to `compatible`, the code is 

```mlog
lookup item *tmp0 18
print *tmp0
print "\n"
sensor *tmp1 @tungsten @id
print *tmp1
print "\n"
printflush message1
```

## If Expression Optimization

This optimization consists of three types of modifications performed on blocks of code created by if/ternary expressions. All possible optimizations are done independently.

### `select` optimization

If expressions which assign a fixed value to a variable depending on a condition are replaced by the `select` instruction, if the level is set to `experimental` and the target is `8.1` or higher. Example:

```
#set target = 8.1;
print(rand(10) > 5 ? "low" : "high");
```

compiles to

```
op rand *tmp0 10 0
select *tmp2 greaterThan *tmp0 5 "low" "high"
print *tmp2
```

### Value propagation

The value of ternary expressions and if expressions is sometimes assigned to a user-defined variable. In these situations, the true and false branches of the if/ternary expression assign the value to a temporary variable, which is then assigned to the user variable. This optimization detects these situations and when possible, assigns the final value to the user variable directly in the true/false branches:

```Mindcode
abs = if x < 0 then
    negative += 1;
    -x;
else
    positive += 1;
    x;
end;
print(abs);
```

produces this code:

```mlog
jump 4 greaterThanEq :x 0
op add :negative :negative 1
op sub *tmp1 0 :x
jump 6 always 0 0
op add :positive :positive 1
set *tmp1 :x
print *tmp1
```

As the example demonstrates, value propagation works on more than just the `set` instruction. All instructions having exactly one output parameter (based on instruction metadata) are handled.

### Instruction propagation

> [!TIP]
> This optimization only applies when the [`select` optimization](#select-optimization) is not available, or when the expression is too complex for the `select` optimization.  

If the instruction immediately following the `if` expression isn't a `set` instruction, but another instruction taking the resulting value of the `if` expression, and the resulting value is stored using a `set` instruction, the `set` instruction will be replaced by the instruction actually consuming the value. The optimization targets these conditional expressions passed as a parameter into a function, for example `print(a > 0 ? "positive" : "negative");`, which produces:

```
jump 3 lessThanEq :a 0
print "positive"
jump 0 always 0 0
print "negative"
```

This saves the execution of one instruction storing the resulting value (`positive` or `negative`, in this case) before passing it into the `print` instruction.

All kinds of instructions are supported, for example `approach(@thisx, @thisy, @unit.@type == @mega ? 8 : 12);` produces

```
sensor *tmp0 @unit @type
jump 4 notEqual *tmp0 @mega
ucontrol approach @thisx @thisy 8 0 0
jump 5 always 0 0
ucontrol approach @thisx @thisy 12 0 0
```

The optimization handles even nested `if` expressions, such as

```Mindcode
print(a < 100 ? a < 10 ? "units" : "tens" : a < 1000 ? "hundreds" : "thousands");
```

which produces 

```mlog
jump 6 greaterThanEq :a 100
jump 4 greaterThanEq :a 10
print "units"
jump 0 always 0 0
print "tens"
jump 0 always 0 0
jump 9 greaterThanEq :a 1000
print "hundreds"
jump 0 always 0 0
print "thousands"
```

### Forward assignment

> [!TIP]
> This optimization only applies when the [`select` optimization](#select-optimization) is not available, or when the expression is too complex for the `select` optimization.  

Some conditional expressions can be rearranged to save instructions while keeping execution time unchanged:

```Mindcode
x = rand(10) - 5;
text = x < 0 ? "negative" : "positive";
print("Value is ", text);
```

Without If Expression Optimization, the produced code is

```
op rand *tmp0 10 0
op sub :x *tmp0 5
jump 5 greaterThanEq :x 0
set *tmp3 "negative"
jump 6 always 0 0
set *tmp3 "positive"
print "Value is "
print *tmp3
```

Execution speed:
* x is negative: four instructions (2, 3, 4) are executed,
* x is positive: three instructions (2, 5) are executed.

The If Expression Optimization turns the code into this:

```
op rand *tmp0 10 0
op sub :x *tmp0 5
set *tmp3 "positive"
jump 5 greaterThanEq :x 0
set *tmp3 "negative"
print "Value is "
print *tmp3
```

Execution speed:
* x is negative: four instructions (2, 3, 4) are executed,
* x is positive: three instructions (2, 3) are executed.

The execution time is the same. However, one less instruction is generated.

The forward assignment optimization is performed when these conditions are met:
* both branches assign a value to the same variable (resulting variable) as the last statement,
* the resulting variable is not global (global variables can be modified or used in function calls),
* the resulting variable is not used in the condition in any way,
* at least one branch consists of just one instruction which sets the resulting variable (this is the branch to be moved),
* the other branch doesn't use the resulting variable anywhere except the last statement,
* the last statement of the other branch doesn't depend on the resulting variable.

Depending on the type of condition and the branch sizes, either the true branch or the false branch can get eliminated this way. The average execution time remains the same, although in some cases the number of executed instructions per branch may change by one.

### Compound condition elimination

The instruction generator always generates the true branch first. In some cases, the jump condition cannot be expressed as a single jump and requires additional instruction (this only happens with the strict equality operator `===`, which doesn't have an opposite operator in Mindustry Logic).

The additional instruction can be avoided when the true and false branches in the code are swapped. When this
optimizer detects such a situation, it does exactly that:

```Mindcode
if @unit.@dead === 0 then
    print("alive");
else
    print("dead");
end;
```

Notice the `print "dead"` occurs before `print "alive"` now:

```mlog
sensor *tmp0 @unit @dead
jump 4 strictEqual *tmp0 0
print "dead"
jump 0 always 0 0
print "alive"
```

### Chained if-else statements

> [!TIP]
> This optimization only applies when the [`select` optimization](#select-optimization) is not available, or when the expression is too complex for the `select` optimization.

The `elsif` statements are equivalent to nesting the elsif part in the `else` branch of the outer expression. Optimizations of these nested statements work as expected:

```Mindcode
y = if x < 0 then
    "negative";
elsif x > 0 then
    "positive";
else
    "zero";
end;
print("value is ", y);
```

produces

```mlog
set *tmp1 "negative"
jump 5 lessThan :x 0
set *tmp1 "zero"
jump 5 lessThanEq :x 0
set *tmp1 "positive"
print "value is "
print *tmp1
```

saving three instructions over the code without if statement optimization:

```
jump 3 greaterThanEq :x 0
set *tmp1 "negative"
jump 8 always 0 0
jump 6 lessThanEq :x 0
set *tmp3 "positive"
jump 7 always 0 0
set *tmp3 "zero"
set *tmp1 *tmp3
print "value is "
print *tmp1
```

## Data Flow Optimization

This optimization inspects the actual data flow in the program and removes instructions and variables (both user-defined and temporary) that are dispensable or have no effect on the program execution. Each optimization performed is described separately below.

Data Flow Optimizations can have a profound effect on the resulting code. User-defined variables can get eliminated, and variables in expressions can get replaced by various other variables that were determined to hold the same value. The goal of these replacements is to eliminate some instructions, making the resulting code both smaller and faster. The optimizer doesn't try to avoid variable replacements that do not lead to instruction elimination—this would make the resulting code more understandable, but the optimizer would have to be more complex and therefore more prone to errors.

### Handling of uninitialized variables

The data flow analysis reveals cases where variables might not be properly initialized, i.e., situations where a value of a variable is read before it is known that some value has been written to the variable. Warnings are generated for each uninitialized variable found.

Since Mindustry Logic executes the code repeatedly while preserving variable values, not initializing a variable might be a valid choice, relying on the fact that all variables are assigned a value of `null` by Mindustry at the beginning. If you intentionally leave a variable uninitialized, declare the variable using a `noinit` modifier, which suppresses the warning:

```Mindcode
noinit var count;
count++;
print(count);
printflush(message1);
```

Data Flow Optimization assumes that values assigned to variables detected as uninitialized might be reused on the next program execution. Assignments to uninitialized variables before calling the `end()` function are therefore protected, while assignments to initialized variables aren't—they will be overwritten on the next program execution anyway:

```Mindcode
noinit var initialized;
var foo = rand(10);
if initialized == 0 then
    print("Initializing...");
    // Do some initialization
    initialized = 1;
    foo = 1;
    end();
end;
print("Doing actual work");
print(initialized);
print(foo);
```

produces this code:

```mlog
op rand .foo 10 0
jump 5 notEqual .initialized 0
print "Initializing..."
set .initialized 1
end
print "Doing actual work"
print .initialized
print .foo
```

Notice the `initialized = 1` statement is preserved, while `foo = 1` is not.

This protection is also applied to assignment to uninitialized variables made before calling a user function which, directly or indirectly, calls or may call the `end()` function:

```Mindcode
#set unreachable-code-elimination = none;
print(foo);
foo = 5;
bar();
foo = 6;
bar();

def bar()
    end();
end;
```

preserves both assignments to `foo`:

```mlog
print :foo
set :foo 5
end
set :foo 6
```

See also [`end()` function](SYNTAX-3-STATEMENTS.markdown#the-end-function).

### Unnecessary assignment elimination

All assignments to variables (except global variables) are inspected, and unnecessary assignments are removed. The assignment is unnecessary if the variable is not read after being assigned, or if it is not read before another assignment to the variable is made:

```Mindcode
a = rand(10);
a = rand(20);
print(a);
a = rand(30);
```

compiles to:

```mlog
op rand :a 20 0
print :a
```

The first assignment to `:a` is removed, because `:a` is not read before another value is assigned to it. The last assignment to `:a` is removed, because `:a` is not read after that assignment at all.

An assignment can also become unnecessary due to other optimizations.

### Constant propagation

When a variable is used in an instruction and the value of the variable is known to be a constant value, Mindcode replaces the variable with the constant value. This can in turn make the original assignment unnecessary. See, for example:

```Mindcode
a = 10;
b = 20;
c = @tick + b;
print($"$a, $b, $c.");
```

produces

```mlog
op add :c @tick 20
print "10, 20, "
print :c
print "."
```

### Constant folding

Constant propagation described above ensures that constant values are used instead of variables where possible. When a deterministic operation is performed on constant values (such as addition by the `op add` instruction), constant folding evaluates the expression and replaces the operation with the resulting value, eliminating an instruction.
For example:

```Mindcode
a = 10;
b = 20;
c = a + b;
print($"$a, $b, $c.");
```

produces

```mlog
print "10, 20, 30."
```

Looks quite spectacular, doesn't it? Here's what happened:

* The optimizer figured out that variables `a` and `b` are not needed, because they only hold a constant value.
* Then it found out the `c = a + b` expression has a constant value too.
* What was left was a sequence of print statements, each printing a constant value. [Print Merging optimization](#print-merging) then merged them.

Not every opportunity for constant folding is detected at this moment. While `x = 1 + y + 2;` is optimized to `op add :x :y 3`, `x = 1 + y + z + 2;` it too complex to process as this moment and the constant values of `1` and `2` won't be folded at compile time.

If the result of a constant expression doesn't have a valid mlog representation, or if the mlog representation of the result incurs a precision loss, the optimization is not performed.

### Common subexpressions optimization

The Data Flow Optimizer keeps track of expressions that have been evaluated. When the same expression is encountered for a second (third, fourth, ...) time, the result of the last computation is reused instead of evaluating the expression again. In the following code:

```Mindcode
a = rand(10);
b = a + 1;
c = 2 * (a + 1);
d = 3 * (a + 1);
print(a, b, c, d);
```

the optimizer notices that the value `a + 1` was assigned to `b` after it was computed for the first time and reuses it in the following instructions:

```mlog
op rand :a 10 0
op add :b :a 1
op mul :c 2 :b
op mul :d 3 :b
print :a
print :b
print :c
print :d
```

Again, not every possible opportunity is used. Instructions are not rearranged, for example, even if doing so allows more evaluations to be reused.

On the other hand, entire complex expressions are reused if they're identical. In the following code

```Mindcode
a = rand(10);
b = rand(10);
x = 1 + sqrt(a * a + b * b);
y = 2 + sqrt(a * a + b * b);
print(x, y);
```

the entire square root is evaluated only once:

```mlog
op rand :a 10 0
op rand :b 10 0
op mul *tmp2 :a :a
op mul *tmp3 :b :b
op add *tmp4 *tmp2 *tmp3
op sqrt *tmp5 *tmp4 0
op add :x 1 *tmp5
op add :y 2 *tmp5
print :x
print :y
```

### Backpropagation

Backpropagation is a separate optimization that allows modifying instructions prior to the one being inspected: when a set instruction assigning a source variable to a target variable is encountered, the original instruction producing ("defining") the source variable is found. If the target variable's value is not needed between the assignments, and the source variable is not needed elsewhere, the set is dropped and the source variable is replaced with the target variable in the defining instruction.

This example demonstrates the effects of the backpropagation optimization:

```Mindcode
#set optimization = experimental;
i = rand(10);
a = i;
i = rand(20);
b = i;
print(a, b);
```

which compiles to

```mlog
op rand :a 10 0
op rand :i 20 0
print :a
print :i
```

Of course, the source code typically doesn't contain such constructs, but this essentially is what some other optimizations, such as inlining function calls or unrolling a list iteration loop with modification, may produce.

The backpropagation optimization is available on the `experimental` level.

### Function call optimizations

Variables and expressions passed as arguments to inline functions, as well as return values of inline functions, are processed in the same way as other local variables. Using an inlined function, therefore, doesn't incur any overhead at all in Mindcode.

The data flow analysis, with some restrictions, is also applied to stackless and recursive function calls. Assignments to global variables inside stackless and recursive functions are tracked and properly handled. Optimizations are applied to function arguments and return values.

### Support for other optimizations

Data Flow Optimization gathers information about variables and the values they get assigned by the program. Apart from using this information for its own optimizations, it also provides it to the other optimizers. The other optimizers can then improve their own optimizations further (e.g., to determine that a conditional jump is always true or always false and act accordingly). Data Flow Optimization is therefore crucial for the best performance of some other optimizers as well. Some optimizations, such as [Loop Unrolling](#loop-unrolling), might outright require the Data Flow Optimization to be active for their own work.

## Loop Hoisting

Loop hoisting is an optimization that tries to identify loop invariant code (i.e., code inside loops which executes identically in each loop iteration) and moves it in front of the loop. This way the code is executed only once, instead of on each loop iteration.

For example, in the following code

```Mindcode
param MAX = 10;
for i in 0 ... MAX do
    print(2 * MAX);
end;
```

the evaluation of `2 * MAX` is moved in front of the loop in the compiled code:

```mlog
set MAX 10
set :i 0
op mul *tmp0 2 MAX
jump 0 greaterThanEq 0 MAX
print *tmp0
op add :i :i 1
jump 4 lessThan :i MAX
```

A loop condition is processed as well as a loop body, and invariant code in nested loops is hoisted all the way to the top when possible: 

```Mindcode
param MAX = 10;
for j in 0 ... MAX do
    i = 0;
    while i < MAX + 10 do
        i = i + 1;
        print(i);
    end;
end;
```

compiles into

```mlog
set MAX 10
set :j 0
op add *tmp0 MAX 10
jump 0 greaterThanEq 0 MAX
set :i 0
jump 9 greaterThanEq 0 *tmp0
op add :i :i 1
print :i
jump 6 lessThan :i *tmp0
op add :j :j 1
jump 4 lessThan :j MAX
```

The optimization affects even instructions setting up parameters or return addresses for stackless function calls and array access:   

```Mindcode
noinline def foo(x)
    print(x);
end;

for i in 0 ... @links do
    foo("Huzzah!");
end;
```

compiles into

```mlog
set :i 0
set :foo:x "Huzzah!"
set :foo*retaddr 5
jump 0 greaterThanEq 0 @links
jump 8 always 0 0
op add :i :i 1
jump 8 lessThan :i @links
end
print :foo:x
set @counter :foo*retaddr
```

Hoisting the instructions setting up return addresses is not possible when [`symbolic-labels`](SYNTAX-5-OTHER.markdown#option-symbolic-labels) is set to `true`.

When the `select` optimization is available, Loop Hoisting is capable of handling some conditional expressions as well:

```Mindcode
#set target = 8;
#set symbolic-labels = true;
param MAX = 10;
for i in 1 ... MAX do
    print(i, (MAX % 2 == 0) ? "Even" : "Odd");
end;
print("end");
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set MAX 10
    set :i 1
    op mod *tmp0 MAX 2
    select *tmp2 equal *tmp0 0 "Even" "Odd"
    jump label_9 greaterThanEq 1 MAX
    label_5:
        print :i
        print *tmp2
    op add :i :i 1
    jump label_5 lessThan :i MAX
    label_9:
    print "end"
```

At this moment, the following limitations apply:

* If the loop contains a stackless or recursive function call, global variables that might be modified by that function call are marked as loop dependent and expressions based on them aren't hoisted. The compiler must assume the value of the global variable may change inside these functions.
* `if` expressions are hoisted only when part of simple expressions. Specifically, when the `if` expression is nested in a function call (such as `print(x < 0 ? "positive" : "negative");`), it won't be optimized.   

## Loop Optimization

The loop optimization improves loops with the condition at the beginning by performing these modifications:

* If the loop jump condition is invertible, the unconditional jump at the end of the loop to the loop condition is replaced by a conditional jump with an inverted loop condition targeting the first instruction of the loop body. This doesn't affect the number of instructions but executes one less instruction per loop.
  * If the loop condition isn't invertible (that is, the jump condition is `===`), the optimization isn't done, since the saved jump would be spent on inverting the condition, and the code size would increase for no benefit at all.  
* If the previous optimization was done and the loop condition is known to be true before the first iteration of the loop, the optimizer removes the jump at the front of the loop. The Loop Optimizer uses information gathered by Data Flow Optimization to evaluate the initial loop condition.  
* Loop conditions that are complex expressions spanning several instructions can still be replicated at the end of the loop, if the code generation goal is set to `speed` (the default setting at the moment). As a result, the code size might actually increase after performing this kind of optimization. See [Dynamic optimizations](#dynamic-optimizations) for details on performing these optimizations.

The result of the first two optimizations in the list can be seen here:

```Mindcode
param LIMIT = 10;
for i in 0 ... LIMIT do
    cell1[i] = 1;
end;
print("Done.");
```

produces 

```mlog
set LIMIT 10
set :i 0
jump 6 greaterThanEq 0 LIMIT
write 1 cell1 :i
op add :i :i 1
jump 3 lessThan :i LIMIT
print "Done."
```

Executing the entire loop (including the `i` variable initialization) takes 32 steps. Without optimization, the loop would require 43 steps. That's quite a significant difference, especially for tight loops.

The third modification is demonstrated here:

```Mindcode
while switch1.enabled and switch2.enabled do
    print("Doing something.");
end;
print("A switch has been reset.");
```

which produces:

```mlog
sensor *tmp0 switch1 @enabled
sensor *tmp1 switch2 @enabled
op land *tmp2 *tmp0 *tmp1
jump 9 equal *tmp2 false
print "Doing something."
sensor *tmp0 switch1 @enabled
sensor *tmp1 switch2 @enabled
op land *tmp2 *tmp0 *tmp1
jump 4 notEqual *tmp2 false
print "A switch has been reset."
```

## Loop Unrolling

Loop unrolling is a [dynamic optimization](#dynamic-optimizations) and is only applied when it is compatible with the optimization goal. Furthermore, loop unrolling depends on the [Data Flow optimization](#data-flow-optimization) and isn't functional when the Data Flow optimization is not active.

### The fundamentals of loop unrolling

The Loop Unrolling optimization works by replacing loops whose number of iterations can be determined by the compiler with a linear sequence of instructions. This results in a significant speedup of program execution: the jump instruction representing an exit condition, and oftentimes also the instruction(s) updating the loop control variable, can be removed from the unrolled loop, so that only instructions actually performing the intended work of the loop remain. The optimization is most efficient on loops that are very "tight"—contain very few instructions apart from the loop itself. The most dramatic practical example is probably something like this (let's see it first without the loop unrolling):

```Mindcode
#set loop-unrolling = none;
for i in 0 ... 10 do
    cell1[i] = 0;
end;
```

This code clears the first ten slots of a memory cell. Without a loop unrolling, the code would look like this:

```mlog
set :i 0
write 0 cell1 :i
op add :i :i 1
jump 1 lessThan :i 10
```

It takes 31 instruction executions to perform this code. With loop unrolling, though, the code is changed to this:

```Mindcode
for i in 0 ... 10 do
    cell1[i] = 0;
end;
```

```mlog
write 0 cell1 0
write 0 cell1 1
write 0 cell1 2
write 0 cell1 3
write 0 cell1 4
write 0 cell1 5
write 0 cell1 6
write 0 cell1 7
write 0 cell1 8
write 0 cell1 9
```

The size of the loop is now 10 instructions instead of 4, but it takes just these 10 instructions to execute, instead of the 31 in the previous case, executing three times as fast!

The price for this speedup is the increased number of instructions themselves. Since there's a hard limit of 1000 instructions in a Mindustry Logic program, loops with a large number of iterations cannot be unrolled. See [Dynamic optimizations](#dynamic-optimizations) for an explanation of how Mindcode decides whether to unroll a loop.

Apart from removing the superfluous instructions, loop unrolling also replaces variables with constant values. This can make further optimization opportunities arise, especially for a Data Flow Optimizer and possibly for others. A not particularly practical, but nonetheless striking example is this program that computes the sum of numbers from 0 to 100:

```Mindcode
sum = 0;
for i in 0 .. 100 do
    sum += i;
end;
print(sum);
```

which compiles to:

```mlog
print 5050
```

What happened here is this: the loop was unrolled to individual instructions in this basic form:

```
set :sum 0
set :i 0
op add :sum :sum :i
op add :i :i 1 
op add :sum :sum :i
op add :i :i 1 
...
```

Data Flow Optimization then evaluated all those expressions using the combination of [constant propagation](#constant-propagation) and [constant folding](#constant-folding), all the way to the final sum of `5050`, which is then directly printed.

### Loop unrolling preconditions

A list iteration loop can always be unrolled if there's enough instruction space left. 

For other loops, unrolling can generally be performed when Mindcode can determine the loop has a certain fixed number of iterations and can infer other properties of the loop, such as a variable that controls the loop iterations. A loop should be eligible for the unrolling when the following conditions are met:

* The loop is controlled by a single, local, or main variable: the loop condition must consist of a variable which is modified inside a loop, and a constant or an effectively constant variable. Loops based on global variables cannot be unrolled. 
* The loop control variable is modified inside the loop only by `op` instructions which have the loop control variable as a first argument and a constant or an effectively constant variable as a second argument; the op instructions must be deterministic. Any other instruction that sets the value of a loop control variable precludes loop unrolling.
* All modifications of the loop control variable happen directly in the loop body: the variable must not be modified in a nested loop or in an if statement, for example.
* The loop has a nonzero number of iterations. The upper limit of the iteration count depends on available instruction space, but generally can never exceed 1000 iterations.

Furthermore:

* If the optimization level is `basic`, the loop control variable can only be modified by `add` and `sub` operations. Every value the loop control variable attains in individual iterations must be an integer (it means that the starting value, ending value, and every change of the iteration variable must be an integer as well). The loop condition must be expressed using one of these operators: `>`, `<`, `>=` or `<=`. In this mode, the total number of iterations is computed using the starting and ending value of the variable and the change in each iteration (resulting in a fast computation).
* If the optimization level is `advanced`, every deterministic update of loop control variable by a constant value and every form of loop condition is allowed. In this case, Mindcode determines the total number of iterations by emulating the entire execution of the loop. The emulation stops when the loop exits or the maximum possible number of iterations allowed by available instruction space is reached (meaning the loop cannot be unrolled).
* `break` and `continue` statements are supported. However, when using a `break` statement, the entire loop is still unrolled. Other optimizers might remove unreachable code after a break statement.

Examples of loops that can be unrolled on `basic` optimization level:

```Mindcode
// Basic case
for i in 0 .. 10 do
    cell1[i] = cell1[i + 1];
end;

// Two separate increments of the loop control variable
j = 0;
while j < 20 do
    println(j);
    j += 1;
    println(j);
    j += 2;
end;

// The loop control variable can be used in further expressions
for k in 0 ... 10 do
    cell1[k] = cell1[2 * k];
end;

for l in 0 ... 10 do
    println(l % 2 ? "Odd" : "Even");
end;

// Loop inside an inline function: can be unrolled if a constant value is passed into the size parameter
inline def copy(src, dst, size)
    for i in 0 ... size do
        dst[i] = src[i];
    end;
end;

// This will produce a loop that can be unrolled
copy(cell1, cell2, 10);

// This produces a loop that CANNOT be unrolled: SIZE is not a constant value
param SIZE = 10;
copy(cell1, cell2, SIZE);

// Some loops containing expressions in the condition can still be unrolled,
// but it strongly depends on the structure of the expression
i = 0;
while (i += 1) < 10 do
    print(i);
end;
```

Examples of loops that can be unrolled on `advanced` optimization level:

```Mindcode
// An operation different from add and sub is supported
for i = 1; i < 100000; i <<= 1 do
    println(i);
end;

// This loop can be unrolled, because it terminates
// (if the condition was j > 0, it would never terminate)
j = 10;
while j > 1 do
    j += 1;
    println(j);
    j \= 2;
    println(j);
end;

// This loop is unrolled, but the number of iterations is 11!
// The code produces the same output as if it wasn't unrolled.
// This is because of rounding errors when evaluating floating-point expressions 
for k = 0; k < 1; k += 0.1 do
    println(k);
end;
```

Examples of loops that **cannot** be unrolled:

```Mindcode
// LIMIT is a program parameter and as such the value assigned to it isn't considered constant
param LIMIT = 10;
for i in 0 ... LIMIT do
    cell1[i] = 0;
end;

// The loop control variable is changed inside an if
i = 0;
while i < 10 do
    i += 1;
    print(i);
    if i % 5 == 0 then
        i += 1;
        print("Five");
    end;
end;

// There isn't a single loop control variable - loop condition depens on both i and j
for i = 0, j = 10; i < j; i += 1, j -= 1 do
    print(i);
end;

// The expression changing the loop control variable is too complex.
// (Rewriting the assignment to i *= 2; i += 1; would allow unrolling) 
i = 0;
while i < 1000 do
  i = 2 * i + 1;
  print(i);
end;

// This loop won't be unrolled. We know it ends after 5 iterations due to the break statement,
// but Mindcode assumes 2000 iterations, always reaching the instruction limit.  
for i in 0 ... 2000 do
    if i > 5 then break; end;
    print(i);
end;
```

### Unrolling nested loops

Nested loops can also be unrolled, and the optimizer prefers unrolling the inner loop:

```Mindcode
k = 0;
for i in 0 ... 100 do
    for j in 0 ... 100 do
        k = k + rand(j); // Prevents collapsing the loop by Data Flow Optimization 
    end;
end;
```

Both loops are eligible for unrolling at the beginning, and the inner one is chosen. After that, the outer loop can no longer be unrolled, because the instruction limit would be hit.

Sometimes unrolling an outer loop can make the inner loop eligible for unrolling too. In this case, the inner loop cannot be unrolled first, as it is not constant:

```Mindcode
#set optimization = advanced;
first = true;
for i in 1 .. 5 do
    for j in i .. 5 do
        if first then
            first = false;
        else
            print(" ");
        end;
        print(10 * i + j);
    end;
end;
```

In this example, the outer loop is unrolled first, after which each copy of the inner loop can be unrolled 
independently. Further optimizations (including Print Merging) then compact the entire computation into a single 
print instruction:

```mlog
print "11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"
```

### List iteration loop with modifications

For [list iteration loops with modifications](SYNTAX-3-STATEMENTS.markdown#modifications-of-variables-in-the-list), the output loop control variable is completely replaced with the variable assigned to it for the iteration. This helps in some more complicated cases where the Data Flow Optimization alone wasn't able to do the substitution on its own.

## Function Inlining

Function Inlining is a [dynamic optimization](#dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

### The fundamentals of function inlining

Function inlining converts out-of-line function calls into inline function calls. This conversion alone saves a few execution steps: storing the return address, jumping to the function body, and jumping back at the original address. However, additional optimizations might be available once a function is inlined, especially if the inlined function call is using constant argument values. In such a situation, many other powerful optimizations, such as constant folding or loop unrolling, may become available.

User-defined, non-recursive function which is called just once in the entire program, is automatically inlined, and this cannot be prevented except by the `noinline` keyword: such code is always both faster and smaller. It is also possible to declare individual functions using the `inline` keyword, forcing all calls of such functions to be inlined.

### Automatic function inlining

This optimization can inline additional functions that aren't recursive and also aren't declared `inline` or `noinline` in the source code. If there's enough instruction space, all function calls may be inlined and the original function body removed from the program. 

When there isn't enough instruction space, only a single one or several specific function calls may be inlined; in this case, the original function body remains in the program and is used by the function calls that weren't inlined. If there are only the last two function calls remaining, either both of them or none of them will be inlined.    

It is therefore no longer necessary to use the `inline` keyword, except in cases when Mindcode's automatic inlining chooses function different from the one(s) you prefer to be inlined, or when using functions with variable number of parameters.  

## Case Switching

Case Switching is a [dynamic optimization](#dynamic-optimizations) and is only applied when it is compatible with the optimization goal. Case Switching optimization is, at this moment, unique in its ability to generate optimizations applicable to all three optimization goals. 

Case expressions are normally compiled to a sequence of conditional jumps: for each `when` branch the entry condition(s) of that clause is evaluated; when it is `false`, the control is transferred to the next `when` branch, and eventually to the `else` branch or end of the expression. This means the case expression evaluates—on average—half of all existing conditions, assuming even distribution of the case expression input values. (If some input values of the case expressions are more frequent, it is possible to achieve better average execution times by placing those values first.)

The Case Switching optimization improves case expressions which branch on integer values of the expression, or on Mindustry content objects.

> [!WARNING]
> It is assumed that a case statement branching exclusively on integer values always gets an integer value on input as well. If the input value of the case expression may take on non-integer values, this optimization will produce wrong code. At this moment Mindcode isn't able to recognize such a situation; if this is the case, you need to disable the Case Switching optimization manually.

The basic structure used by this optimization is a _jump table_.

### Jump tables

A jump table replaces the sequence of conditional jumps by direct jumps to the corresponding `when` branch. The actual instructions used to build a jump table are

```
jump <else branch address> lessThan value minimal_when_value
jump <else branch address> greaterThan value maximal_when_value
op add @counter <start_of_jump_table - minimal_when_value> value
start_of_jump_table:
jump <when branch for minimal when value address>
jump <when branch for minimal when value + 1 address>
jump <when branch for minimal when value + 2 address>
...
jump <when branch for maximal when value address>
```

The jump table is put in front of the `when` branches. Original conditions in front of each processed `when` branch are removed. Each `when` branch jumps to the end of the case expression as usual. The bodies of `when` branches are moved into correct places inside the case expression when possible, to avoid unnecessary jumps. On `experimental` level, the bodies of `when` branches may be duplicated to several suitable places to avoid even more jumps at the cost of additional code size increase. This optimization usually only kicks in for small branch bodies, since for larger code increases, a better performing solution can be achieved by a different segment arrangement.

To build the jump table, the minimum and maximum value of existing `when` branches are determined first. Values outside this range are handled by the `else` branch (if there isn't an explicit `else` branch in the case statement, the `else` branch just jumps to the end of the case expression). Values inside this range are mapped to a particular `when` branch, or, if the value doesn't correspond to any of the `when` branches, to the `else` branch.

The first two instructions in the example above (`jump lessThan`, `jump greaterThan`) handle the cases where the input value lies outside the range supported by the jump table. The `op add @counter` instruction then transfers the control to the corresponding specific jump in the jump table and consequently to the proper `when` branch.

A basic jump table executes at most four instructions on each case expression execution (less if the input value lies outside the supported range). We've mentioned above that the original case statement executes half of the conditional jumps on average. This means that converting the case expression to a jump table only makes sense when there are at least eight conditional jumps in the case expression.

Notes:

* When evaluating execution speed, the optimizer computes and averages execution costs of each value present in a `when` clause. All of these values are deemed equally probable to occur, and values leading to an `else` branch are not considered at all. In an unoptimized `case` expression, values handled by the `else` branch take the longest time to handle, while in the optimized case expression, values completely outside the range of `when` values are executed faster than any other values. This is a side effect of the optimization.  
* As a consequence, if you put the more frequent values first in the case expression, and the value distribution is very skewed, converting the case expression to the jump table might actually worsen the average execution time. Mindcode has no way to figure this on its own; if you encounter this situation, you might need to disable the Case Switching optimization for your program.
* For smaller case expressions, a full jump table might provide worse average performance than the original case expression. Mindcode might still optimize the case expression by applying the bisection search used in [Jump table compression](#jump-table-compression), providing both better average execution time of the entire case expression and more balanced execution time of individual branches.
 
**Preconditions:**

The following conditions must be met for a case expression to be processed by this optimization:

* All values used in `when` clauses must be effectively constant.
* All values used in `when` clauses must be integers, or must be convertible to integers (see [Mindustry content conversion](#mindustry-content-conversion)). Specifically, no `null` values may be used.
* Values used in `when` clauses must be unique; when ranges are used, they must not overlap with other ranges or standalone values. 

### Range check elimination

When all possible input values in case expression are handled by one of the `when` branches, it is not necessary to use the two jumps in front of the jump table to handle out-of-range values. Mindcode is currently incapable of determining this is the case and keeps these jumps in place by default. By setting the `unsafe-case-optimization` compiler directive to `true`, you inform Mindcode that all input values are handled by case expressions. This prevents the out-of-range handling instructions from being generated, making the optimized case expression faster by two instructions per execution, and leads to the optimization being considered for case expressions with four branches or more.

Putting an `else` branch into a case expression indicates not all input values are handled, and doing so disables the unsafe case optimization: the basic optimization may still happen, but the out-of-range checks will remain.

If you activate the `unsafe-case-optimization` directive, and an unhandled input value is encountered, the behavior of the generated code is undefined.

### Mindustry content conversion

When all `when` branches in the case expression contain built-in constants representing Mindustry content of the same type (items, liquids, unit types, or block types) and the optimization level is set to `advanced`, this optimization converts these built-in constants to logic IDs, adds an instruction to convert the input value to a logic ID (using the `sensor` instruction with the `@id` property) and attempts to build a jump table over the resulting numeric values.

The following preconditions need to be met to apply content conversion:

* The optimization level is set to `advanced`.
* The `builtin-evaluation` option is set to `compatible` or `full`.
* All values in `when` branches are either `null`, or built-in variables referencing Mindustry content of the same type (items, building types and so on).
* Values used in `when` clauses are unique.
* The logic ID is known to Mindcode for all `when` values. 
* All logic IDs are stable, or `builtin-evaluation` mode is set to `full`.

> [!NOTE]
> When `unsafe-case-optimization` is set to `true` and there's no `else` branch, the optimizer creates a jump table omitting the out-of-range checks. Make sure that all possible input values are handled before removing the `else` branch or applying the `unsafe-case-optimization` directive. When the input value originates in the game (e.g., item selected in a sorter), keep in mind the value obtained this way might be `null`.  

The range check is also partially or fully removed when the following conditions are met:

* There is a `when` branch corresponding to the Mindustry content with a zero ID: in this case, Mindcode knows the minimum possible numerical value of the ID (that is, zero) is handled by the case expression and doesn't check for IDs less than zero.
* There is a `when` branch corresponding to the Mindustry content with a maximum ID, and `builtin-evaluation` is set to `full`: in this case, Mindcode knows the maximum possible numerical value of the ID is handled by the case expression and doesn't check for IDs greater than the maximum value.

#### Null values

When Mindustry content conversion occurs, `null` values in `when` clauses are supported. When the `null` value is explicitly handled (i.e., there is a `when null` branch present), the corresponding branch is executed for `null` input values. In case the `when null` branch is missing, `null` input values are handled by the `else` branch, or skipped altogether if there is no else branch.

Mindcode arranges the code to only perform checks distinguishing between `null` and the zero value where both of these values can occur. When a code path is known not to possibly handle both `null` and `0`, these checks are eliminated. As a result, an optimized `case` expressions checking for `null` in `when` branches is typically more efficient than handling the `null` values in the `else` branch, or checking for them prior to the case expression itself.

### Text-based jump tables

In Mindustry 8, it is possible to [read character values from a string](MINDUSTRY-8.markdown#reading-characters-from-strings) at a given index in a single operation. This allows encoding instruction addresses into strings, instead of building actual jump tables out of jump instruction. The following prerequisites need to be met for this optimization to be applied:

* The [target](SYNTAX-5-OTHER.markdown#option-target) must be set to version `8` or higher,
* The [symbolic labels](SYNTAX-5-OTHER.markdown#option-symbolic-labels) option must be inactive,
* The [text-jump-tables](SYNTAX-5-OTHER.markdown#option-text-jump-tables) option must be active.

When all these conditions are met, the case expression is always 

### Jump table compression

Building a single jump table for the entire case expression often produces the fastest code, but the jump table might become huge. The optimizer therefore tries to break the table into smaller segments, handling these segments specifically. Some segments might contain a single value, or a single value with a few exceptions, and can be handled by only a few jump instructions. More diverse segments may be encoded as separate, smaller jump tables. The optimizer considers a number of such arrangements and selects those that give the best performance for a given code size, taking other possible optimizations described here into account as well. To locate the segment handling a particular input value, a bisection search is used. 

The total number of possible segment arrangements can be quite large. The more arrangements are considered, the better code may be generated. However, generating and evaluating these arrangements can take a long time. The [`case-optimization-strength` compiler directive](SYNTAX-5-OTHER.markdown#option-case-optimization-strength) can be used to control the number of considered arrangements. Setting this option to `0` disables jump table compression entirely.

Typically, compressing the jump table produces smaller, but slightly slower code. For more complex `case` expressions, it is possible that the optimized code will be both smaller and significantly faster than the unoptimized `case` expression.   

Jump table compression is particularly useful when using block types in case expressions, as, given the large dispersion of block type IDs, full jump tables tend to get quite large.

Notes:

* Jump table compression is not performed when range checks for the given case expression are eliminated via the `unsafe-case-optimization` option, or when the [`case-optimization-strength` compiler directive](SYNTAX-5-OTHER.markdown#option-case-optimization-strength) option has been set to 0.
* When a compressed jump table is smaller, but slower than a full, or a less compressed jump table, it will only be selected when there isn't enough instruction space for the larger jump table.
* Compressing a jump table may, under some circumstances, produce a code which is on average faster than a full jump table, while still being smaller. When this is the case, the optimizer will select the smaller version over the faster version, even when there is plenty of instruction space.
* Since the bisection search provides better execution time than a linear search, it may be applied even to case expressions too small for a full jump table optimization.

### Jump table padding

When the jump table starts at zero value, it is possible to generate a faster code due to these effects:

* When the Mindustry content conversion is applied, the optimizer knows the logic IDs cannot be less than zero. A jump instruction handling values smaller than the start of the jump table can therefore be omitted.
* When the [`symbolic-labels` directive](SYNTAX-5-OTHER.markdown#option-symbolic-labels) is set to `true`, an additional operation handling the non-zero offset can be omitted.

Similarly, when the Mindustry content conversion is applied, the `builtin-evaluation` option is set to `full` and the jump table ends at the largest ID of the respective Mindustry content, a jump instruction handling values larger than the end of the table can be omitted, as the optimizer knows no larger values may occur. 

When the jump table doesn't start or end at these values naturally, Mindcode may pad the table at either end with additional jumps to the `else` branch. The optimizer considers the possibility of padding the table at the low end, high end, or both, and chooses the option that gives the best performance given the instruction space limit.

### Example

The example illustrates the following optimization aspects:

* Case switching optimization in general
* Mindustry content conversion
* Handling of `null` values
* Jump table compression
* Jump table padding
* Moving bodies of `when` branches

The sample has been artificially constructed to demonstrate the above effects. 

```Mindcode
#set target = 7;
#set builtin-evaluation = full;
#set symbolic-labels = true;
#set instruction-limit = 150;
#set case-optimization-strength = 4;

text = case getlink(0).@type
    when null then "none";
    when @kiln, @phase-weaver, @pyratite-mixer, @melter, @disassembler then "A";
    when @plastanium-compressor, @cryofluid-mixer, @blast-mixer, @separator, @spore-press then "B";
    when @unit-repair-tower, @prime-refabricator, @mech-refabricator, @slag-heater, @scathe then "C";
    when @diffuse, @tank-refabricator, @ship-refabricator, @lustre then "D";
    else "E";
end;

print(text);
```

The above case expression is transformed to this:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    getlink *tmp1 0
    sensor *tmp2 *tmp1 @type
    sensor *tmp4 *tmp2 @id
        jump label_21 greaterThanEq *tmp4 230
        jump label_45 greaterThanEq *tmp4 14
        op add @counter @counter *tmp4
        jump label_44 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
    label_19:
        set *tmp0 "B"
        jump label_46 always 0 0
    label_21:
        jump label_45 greaterThanEq *tmp4 243
        jump label_26 greaterThanEq *tmp4 232
        jump label_38 lessThan *tmp4 231
    label_24:
        set *tmp0 "D"
        jump label_46 always 0 0
    label_26:
        op sub *tmp5 *tmp4 232
        op add @counter @counter *tmp5
        jump label_38 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_24 always 0 0
        jump label_38 always 0 0
        jump label_24 always 0 0
        jump label_38 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_24 always 0 0
    label_38:
        set *tmp0 "C"
        jump label_46 always 0 0
    label_40:
        set *tmp0 "none"
        jump label_46 always 0 0
    label_42:
        set *tmp0 "A"
        jump label_46 always 0 0
label_44:
    jump label_40 strictEqual *tmp4 null
label_45:
    set *tmp0 "E"
    label_46:
    print *tmp0
```

## Array Optimization

The array optimization improves the performance of array operations in several ways. At this moment, all optimizations are only available on the `experimental` level.

Loop unrolling may replace random access to array elements with sequential code accessing individual elements directly, in which case no array optimization happens. 

### Array access inlining

Array access inlining is a [dynamic optimization](#dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

To facilitate random array access, shared jump tables for read and write access are generated for each array. These jump tables are shared by all read/write random access of an individual array. This requires using a dedicated _array access variable_ for each access, and setting up return addresses for resuming the control flow after the array element has been processed. 

Array access inlining builds a dedicated jump table at each place an array access operation is performed, eliminating the need for array access variables and return addresses.

Inlining a jump table in a general case reduces the number of steps required per element access from 6 to 4. When accessing an element of the array in a loop at most once for reading and once for writing, the usage of array access variables can be streamlined and return addresses setup can be hoisted out of the loop. This reduces a lot of the overhead of shared jump tables.  

### Short array optimizations

This optimization is performed for arrays of up to three elements.

Short array optimizations cause out-of-bounds indexes to always resolve to the last element. Using an out-of-bounds index can't derail the program execution. Nevertheless, runtime checks still get generated when prescribed by a compiler directive.

#### Arrays of length 1

Each element access gets converted to direct access of the single element.

#### Arrays of length 2 and 3

The jump table is replaced by a sequence of `select`s or if/else statements. Arrays of length 2 are always optimized, while arrays of length 3 are only optimized if the `select` optimization can be used, or the jump table for the array access has been selected for inlining.

**The `select` optimization**

The `select` optimization is applied when the optimization level is set to `experimental` and the `select` instruction is available.

For arrays of length 2, the optimization effectively replaces the jump table with these constructs:

* `a[x] = b` gets converted to

```
select a[0] x equal 0 b a[0]
select a[1] x equal 1 b a[1]
```
 
* `b = a[x]` gets converted to

```
select b x equal 0 a[0] a[1]
```

For arrays of length 3, the optimization can be described like this:

* `a[x] = b` gets converted to

```
select a[0] x equal 0 b a[0]
select a[1] x equal 1 b a[1]
select a[2] x equal 2 b a[2]
```

* `b = a[x]` gets converted to

```
select b x equal 0 a[0] a[1]
select b x equal 2 a[2] b
```

**The if/else optimization**

For arrays of length 2, the optimization effectively replaces the jump table with these constructs:

* `a[x] = b;` gets converted to `if x == 0 then a[0] = b; else a[1] = b; end;`.
* `b = a[x]` gets converted to  `if x == 0 then b = a[0]; else b = a[1]; end;`.

For arrays of length 3, the optimization can be described like this:

* `a[x] = b;` gets converted to `if x == 0 then a[0] = b; elsif x == 1 then a[1] = b; else a[2] = b end;`.
* `b = a[x]` gets converted to  `if x == 0 then b = a[0]; elsif x == 1 then b = a[1]; else b = a[2] end;`.

This optimization allows additional [If Expression optimizations](#if-expression-optimization) to take place.  

## Return Optimization

Return Optimization is a [dynamic optimization](#dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

The Return Optimization is simple: whenever there's an unconditional jump to the final sequence of instructions representing a return from the call (which is always three instructions long), the jump is replaced by the entire return sequence. The jump execution is avoided at the price of two additional instructions.        

The impact of this optimization is probably marginal. Recursive functions are of limited use by themselves, and this optimization only applies in a rather specific context.

## Jump Straightening

This optimization detects situations where a conditional jump skips a following, unconditional one and replaces it with a single conditional jump with a reversed condition and a target of the second jump. Example:

```
jump *label0 equal *tmp9 false
jump *label1
label *label0
```

will be turned to

```
jump *label1 notEqual *tmp9 false
```

Optimization won't be done if the condition does not have an inverse condition (`strictEqual`).

These sequences of instructions may arise when using the `break` or `continue` statements:

```
while true do
    ...
    if not_alive then
        break;
    end;
end;
```

## Jump Threading

If a jump (conditional or unconditional) targets an unconditional jump, the target of the first jump is redirected to the target of the second jump, repeated until the end of a jump chain is reached. Moreover:

* `end` instruction is handled identically to `jump 0 always`,
* conditional jumps in the jump chain are followed if:
  * their condition is identical to the condition of the first jump in the chain, and
  * the condition arguments do not contain a volatile variable (`@time`, `@tick`, `@counter` etc.),
* unconditional jumps targeting an indirect jump (i.e., an instruction assigning value to `@counter`) are replaced with the indirect jump itself, 
* on the `experimental` level, when symbolic labels aren't used, the following function-call-related optimizations are also available:
  * the return address of a function call is redirected to the target of the following unconditional jump,
  * a conditional or unconditional jump to a function call is redirected directly to the function. 

No instructions are directly removed or added, but the execution of the code is faster; furthermore, some jumps in the jump chain may be removed later by the [Unreachable Code Elimination](#unreachable-code-elimination).

## Unreachable Code Elimination

This optimizer removes instructions that are unreachable. There are several ways unreachable instructions might appear:

1. Jump Threading can create unreachable jumps that are no longer targeted.
2. User-created unreachable regions, such as `while false ... end`, or code following a `while true` loop.
3. User-defined functions which are called from an unreachable region.

Instruction removal is done by analyzing the control flow of the program and removing instructions that are never executed. When [Jump Normalization](#jump-normalization) is not active, some section of unreachable code may not be recognized.

## Stack Optimization

Optimizes the stack usage -- eliminates `push`/`pop` instruction pairs determined to be unnecessary. The following optimizations are performed:

* `push`/`pop` instruction elimination for function variables that are not used anywhere apart from the push/pop instructions. This happens when variables are eliminated by other optimizations. The optimization is done globally, in a single pass across the entire program.
* `push`/`pop` instruction elimination for function variables that are read neither by any instruction between the call instruction and the end of the function nor by any instruction that is part of the same loop as the call instruction. Implicit reads by recursive calls to the same function with the value of the parameter unchanged are 
  also detected.
* `push`/`pop` instruction elimination for function parameters/variables that are never modified within the function.

## Print Merging

This optimization merges two or more print instructions taking a string literal as an argument. The print instructions will get merged even if they aren't consecutive, assuming there aren't instructions that could break the print sequence (`jump`, `label` or `print <variable>`).

Effectively, this optimization eliminates a `print` instruction by turning this

```Mindcode
println("Items: ", items);
println("Time: ", @time);
```
into this:

```Mindcode
print("Items: ", items);
print("\nTime: ", @time, "\n");
```

All constant values - not just string constants - are merged. For example:

```Mindcode
const MAX_VALUE = 10;
println($"Step $i of $MAX_VALUE");
```

produces

```mlog
print "Step "
print :i
print " of 10\n"
```

On the basic optimization level, the output would be:

```
print "Step "
print i
print " of "
print 10
print "\n"
```

### The `format` instruction

The `format` instruction included in Mindustry Logic 8 allows more efficient optimizations to be done. For example, `println($"Minimum: $min, middle: $mid, maximum: $max");` without using the `format` instruction gets compiled to this:

```
print "Minimum: "
print :min
print ", middle: "
print :mid
print ", maximum: "
print :max
print "\n"
```

The optimization utilizing `format` saves three instructions by producing

```
print "Minimum: {0}, middle: {0}, maximum: {0}\n"
format :min
format :mid
format :max
```

The format instruction is used in optimization when these conditions are met:

- The language target supports the format instruction (`8.0` or later)
- The compiled code entering this optimization contains neither a string literal containing a `{0}` placeholder, nor any other substrings that could produce the `{0}` in a text buffer (for example, `print("{{1}}"); format("0");` produces `{0}` in the text buffer and disables this optimization).

If the `{0}` placeholder is avoided, the formatting mechanism can be used freely in the code without any limitations and the print merging optimization with the format instruction can still happen. To avoid the `{0}` placeholder, use placeholders starting at `{1}`:

```Mindcode
#set target = 8;
param a = 10;               // prevent a from being propagated as a constant
println("{2} {1}");         // if you use "{0} {1}" instead - different optimization will happen 
format("Before");
println($"Value: $a");
format("After");
```

This program will compile to

```mlog
set a 10
print "{2} {1}\n"
format "Before"
print "Value: {0}\n"
format a
format "After"
```

and will output

```
After Before
Value: 10
```


### String length limit

On `basic` level, the optimization won't merge print instructions if the merge produces a string longer than 34 characters (36 when counting the double quotes). On `advanced` level, such instructions will be merged regardless. This can create long string constants, but according to our tests, these can be pasted into Mindustry processors even if they're longer than what the Mindustry GUI allows to enter.

### Remarks

The print merging optimization will also merge `print` instructions generated by the `remark()` function. Instructions generated by remarks are merged differently to standard `print` instructions:

* `print` instructions generated from different `remark()` function calls are never merged. Only instructions generated from a single `remark()` are merged.
* All constant values are merged regardless of the resulting string length, even on the `basic` optimization level.

If the print merging optimization is not active, instructions from `remark()` functions aren't merged.

---

[« Previous: Compiler directives](SYNTAX-5-OTHER.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Extending Mindcode »](SYNTAX-EXTENSIONS.markdown)
