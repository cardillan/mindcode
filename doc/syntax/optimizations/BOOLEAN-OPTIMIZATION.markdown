# Boolean Optimization

This optimization handles boolean expressions in conditional expressions or standalone, both fully evaluated and short-circuited. The basis of the optimization lies in replacing the conditional expression with a sequence of `op` or `select` (in target `8.1` or higher) instructions. In the following text only the `select` instructions are mentioned. However, wherever possible, the `op` instructions are used instead.

> [!TIP]
> Since the `op` instruction can only generate boolean values, the optimization possibilities are very limited for targets where `select` is not available.

Available optimizations depend on the expression evaluation (full or short-circuited).

## Fully evaluated expressions

In fully evaluated expressions, the value of the condition is fully computed before the conditional statement is executed. Such expressions can be optimized by replacing the true and false branches with a sequence of `select` instructions based on the final value of the condition; one `select` instruction is needed for each output variable of the conditional expressions. When additional computations are needed to produce a final value of any output variable in either branch, these computations are kept in the optimized code and are executed regardless of the actual value of the condition.    

When this optimization is applied, it always decreases the code size, as it avoids any jumps, and it replaces two separate assignments to the same variable in each branch with a single `select` instruction.  

Whether the optimization is applied depends on the optimization goal:

* `speed` or `neutral`: the optimization is applied if the average execution time is improved by the optimization. When computing the average execution time, it is assumed both branches get selected with the same probability.
* `size`: the optimization is applied if the number of instructions required needed for the additional computations (see above) is at most five.

Example:

```Mindcode
#set target = 8.1m;
print(rand(10) < 5 ? "low" : "high");
```

compiles to:

```mlog
op rand *tmp0 10 0
select *tmp2 lessThan *tmp0 5 "low" "high"
print *tmp2
```

The optimization can handle nested/chained if expressions as well as expressions assigning values to different variables in each branch.

```Mindcode
#set target = 8m;

if switch1.enabled then
    a = "on";
    b = @coal;
    c = 50;
else
    a = "off";
end;

if switch2.enabled then
  a = "paused";
end;

print(a, b, c);
```

compiles to:

```mlog
sensor *tmp0 switch1 @enabled
select :a notEqual *tmp0 false "on" "off"
select :b notEqual *tmp0 false @coal :b
select :c notEqual *tmp0 false 50 :c
sensor *tmp2 switch2 @enabled
select :a notEqual *tmp2 false "paused" :a
print :a
print :b
print :c
```

Even when the assignments modify one or both variables used in the branch condition, or a simple single expression is used in one of the branches, the `select` optimization can compensate for that:

```Mindcode
#set target = 8m;

noinit var a, b;

if a > b then
    a = 10; b = 20;
else
    a = 20; b = 10;
end;

col(a > 0 ? packcolor(0, b, b, 1) : %[red]);
```
compiles to:

```mlog
op lessThanEq *tmp5 .a .b
select .a equal *tmp5 false 10 20
select .b equal *tmp5 false 20 10
packcolor *tmp6 0 .b .b 1
select *tmp3 greaterThan .a 0 *tmp6 %[red]
draw col *tmp3 0 0 0 0 0
```

## Short-circuit expressions

Three different optimizations are available for short-circuited expressions. Unless specified otherwise, all of these optimizations are only supported when the condition can be expressed as a sequence of `and` or `or` operators (not both). 

### Pure boolean expressions

Pure boolean expressions are expressions over variables using only the `and` and `or` operators. When such an expression has at most three terms (variables), it can be fully evaluated using just two `op` instructions. As this solution is always smaller and faster than the fully evaluated solution, the optimization is always applied for these cases. All six possible forms of a three-variable pure boolean expression, and the resulting optimizations, are demonstrated here:

```Mindcode
volatile a, b, c;
print(a and b and c);
print(a and (b or c));
print(a or (b and c));
print(a or b or c);
print((a or b) and c);
print((a and b) or c);
```

compiles to:

```mlog
op land *tmp6 .a .b
op land *tmp0 *tmp6 .c
print *tmp0
op or *tmp7 .b .c
op land *tmp1 *tmp7 .a
print *tmp1
op land *tmp8 .b .c
op or *tmp2 *tmp8 .a
print *tmp2
op or *tmp9 .a .b
op or *tmp3 *tmp9 .c
print *tmp3
op or *tmp10 .a .b
op land *tmp4 *tmp10 .c
print *tmp4
op land *tmp11 .a .b
op or *tmp5 *tmp11 .c
print *tmp5
```

Notes: 
* Pure boolean expression optimization doesn't depend on the optimization goal or the selected target.
* When a pure boolean expression is used as a condition in a conditional statement, this optimization is not applied.

### Conversion to full evaluation

This optimization only happens when the short-circuit condition can be converted to full evaluation without any side effects, and the result is compatible with the optimization goal:

* `speed` or `neutral`: the condition consists of a single `and` or `or` operator only, there are no additional computations neither in the true or false branch, nor in the short-circuited portion of the condition and both branches write to the same set of variables.
* `size`: the number of instructions required needed for the additional computations is at most five.

This optimization allows the condition to be processed as a [Fully evaluated expression](#fully-evaluated-expressions) described above, which provides the actual benefit.

Due to the preconditions listed above, the optimization only happens when the second term in the condition is a simple variable when the optimization goal is `speed` or `neutral`. The combined effects of both optimizations taking place can be seen here:

```Mindcode
#set target = 8m;
if switch1.enabled and a then
    x = 10; y = 20;
else
    x = 5; y = 7;
end;
print(x, y);
```

compiles to:

```mlog
sensor *tmp0 switch1 @enabled
op land *tmp2 *tmp0 :a
select :x notEqual *tmp2 false 10 5
select :y notEqual *tmp2 false 20 7
print :x
print :y
```

Just switching the terms in the condition precludes the optimization:

```Mindcode
#set target = 8m;
if a and switch1.enabled then
    x = 10; y = 20;
else
    x = 5; y = 7;
end;
print(x, y);
```

compiles to:

```mlog
jump 6 equal :a false
sensor *tmp0 switch1 @enabled
jump 6 equal *tmp0 false
set :x 10
set :y 20
jump 8 always 0 0
set :x 5
set :y 7
print :x
print :y
```

### Single variable assignment

If both the true and the false branches write to just one variable without any additional computations, it is again possible to replace the expression with a sequence of `select` instructions (the `op` instructions can't be used in this case, as they do not allow handling intermediate results). Unlike the case of the fully evaluated condition, each `select` instruction evaluates a piece of the original condition:

```Mindcode
#set target = 8m;
print(a > 0 or b > 0 ? 10 : 20);
```

compiles to:

```mlog
select *tmp3 greaterThan :a 0 10 20
select *tmp2 greaterThan :b 0 10 *tmp3
print *tmp2
```

This optimization only happens when the short-circuit condition can be converted to full evaluation without any side effects, and the result is compatible with the optimization goal:

* `speed` or `neutral`: the condition has at most three terms, and there are no additional computations in the condition.
* `size`: the condition has at most five terms, and it uses at most five additional instructions for evaluation of the condition.

An example of the optimization applied under the `size` optimization goal:

```Mindcode
#set target = 8m;
#set goal = size;
volatile x = (switch1.enabled and sorter1.config == @coal);
```

compiles to:

```mlog
sensor *tmp0 switch1 @enabled
op notEqual *tmp4 *tmp0 false
sensor *tmp1 sorter1 @config
select .x equal *tmp1 @coal *tmp4 false
```

### Last jump conversion

When none of the above optimizations can be made, the optimizer tries to replace the last jump in the sequence of jumps with a `select` instruction. This is only possible when the conditional expression produces just one variable identical in both branches, and the condition is a simple variable. This optimization also doesn't depend on the optimization goal.

Example:

```Mindcode
#set target = 8m;
volatile x = switch1.enabled and !@unit.@dead and amount > 0 ? "yes" : "no";
```

compiles to:

```mlog
sensor *tmp0 switch1 @enabled
jump 6 equal *tmp0 false
sensor *tmp1 @unit @dead
jump 6 notEqual *tmp1 false
select .x lessThanEq :amount 0 "no" "yes"
jump 0 always 0 0
set .x "no"
```

For comparison, the unoptimized code would look like this:

```Mindcode
#set target = 8m;
#set boolean-optimization = none;
volatile x = switch1.enabled and !@unit.@dead and amount > 0 ? "yes" : "no";
```

compiles to:

```mlog
sensor *tmp0 switch1 @enabled
jump 7 equal *tmp0 false
sensor *tmp1 @unit @dead
jump 7 notEqual *tmp1 false
jump 7 lessThanEq :amount 0
set .x "yes"
jump 0 always 0 0
set .x "no"
```

---

[&#xAB; Previous: Array Optimization](ARRAY-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Case Expression Optimization &#xBB;](CASE-EXPRESSION-OPTIMIZATION.markdown)
