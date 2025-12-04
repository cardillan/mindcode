# If Expression Optimization

This optimization consists of several types of modifications performed on blocks of code created by if/ternary expressions. All possible optimizations are performed independently.

## Value propagation

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

## Instruction propagation

> [!TIP]
> This optimization only applies when the [`select` optimization](BOOLEAN-OPTIMIZATION.markdown) is not available, or when the expression is too complex for the `select` optimization.

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

## Forward assignment

> [!TIP]
> This optimization only applies when the [`select` optimization](BOOLEAN-OPTIMIZATION.markdown) is not available, or when the expression is too complex for the `select` optimization.

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

## Compound condition elimination

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

## Chained if-else statements

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

---

[&#xAB; Previous: Function Inlining](FUNCTION-INLINING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Jump Normalization &#xBB;](JUMP-NORMALIZATION.markdown)
