# Boolean Optimization

This optimization replaces conditional expressions, which assign a value to variables depending on a condition, by the `select` instruction, if the target is `8.1` or higher. Example:

```Mindcode
set target = 8.1;
print(rand(10) < 5 ? "low" : "high");
```

compiles to

```mlog
op rand *tmp0 10 0
select *tmp2 lessThan *tmp0 5 "low" "high"
print *tmp2
```

The optimization can handle nested/chained if expressions as well as expressions assigning values to different variables in each branch. It is applied if the average execution time is improved by the optimization, unless the optimization goal is set to `size`, in which case the optimization is always applied. (The code size is always reduced thanks to avoiding any jumps.)

```Mindcode
set target = 8;

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

Even when the assignments modify both variables used in the branch condition, or a simple single expression is used in one of the branches, the `select` optimization can compensate for that:

```Mindcode
set target = 8;

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

---

[&#xAB; Previous: Array Optimization](ARRAY-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Case Expression Optimization &#xBB;](CASE-EXPRESSION-OPTIMIZATION.markdown)
