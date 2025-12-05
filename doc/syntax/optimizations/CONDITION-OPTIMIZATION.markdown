# Condition Optimization

The Condition Optimization handles conditional instructions—instructions which evaluate conditions (`jump`, `select` and `op` with a comparison operator). It locates pairs of instructions, where the first one is an `op` instruction producing a value, and the other is a conditional instruction evaluating said value in the condition. When possible, the `op` operation is merged into the conditional instruction. If the result of the `op` instruction isn't used anywhere else, it gets completely removed.

There are two specific kinds of optimizations:

* constant folding,
* condition streamlining.

The following preconditions need to be met for the optimization to take place:

* Both the condition and the operation each have one constant, numerical argument.
* The condition does not involve strict equality (the `===` or `!==` operators).
* The `op` instruction doesn't update the variable (e.g., `op add a a 1`).

## Constant Folding

Constant folding only takes place when the optimization level is set to `advanced`.

When both the condition and the operation each have one constant argument, these two constants can be folded. Folding is supported for addition, subtraction, multiplication and division. After folding, the non-constant argument in the condition is replaced with the non-constant argument of the operation, and the constant argument in the condition is updated.

The optimization doesn't take place when a precision loss may occur:
* when the condition uses normal equality operators (`equal` or `notEqual`), a precision loss up to 10<sup>-7</sup> is allowed,
* in other cases, no precision loss is allowed.

Precision loss is detected by reversing the folding operation and retrieving back the term with a smaller absolute value. If this term is equal to its original value (with a difference smaller than the aforementioned limit in case of simple equality), no significant precision loss is deemed to occur.

Example:

```Mindcode
volatile x, y; 
x = y + 1 >= 2;
x = y - 1 >= 2;
x = 1 - y >= 2;
x = 2 <= y + 1;
x = 2 <= y - 1;
x = 2 <= 1 - y;
x = y + 1 == 2;
x = y - 1 == 2;
x = 1 - y == 2;
x = y + 1 != 2;
x = y - 1 != 2;
x = 1 - y != 2;
```

compiles to:

```mlog
op greaterThanEq .x .y 1
op greaterThanEq .x .y 3
op lessThanEq .x .y -1
op greaterThanEq .x .y 1
op greaterThanEq .x .y 3
op lessThanEq .x .y -1
op equal .x .y 1
op equal .x .y 3
op equal .x .y -1
op notEqual .x .y 1
op notEqual .x .y 3
op notEqual .x .y -1
```

Without the optimization, there would be additional `op` instructions:

```Mindcode
#set condition-optimization = none;
volatile x, y;
x = y + 1 >= 2;
x = y - 1 >= 2;
x = 1 - y >= 2;
```

compiles to:

```mlog
op add *tmp0 .y 1
op greaterThanEq .x *tmp0 2
op sub *tmp2 .y 1
op greaterThanEq .x *tmp2 2
op sub *tmp4 1 .y
op greaterThanEq .x *tmp4 2
```

The following example demonstrates constant folding applied to multiplication and division, as well as the detection of a precision loss (which typically occurs when handing decimal numbers):

```Mindcode
#set remarks = comments;
volatile x, y;
/// Multiplication and division
x = y * 2 >= 4;
x = y / 2 >= 4;
x = 2 / y >= 4;
/// Precision error
x = y - 0.1 >= 0.2;
```

compiles to:

```mlog
# Multiplication and division
op greaterThanEq .x .y 2
op greaterThanEq .x .y 8
op lessThanEq .x .y 0.5
# Precision error
op sub *tmp6 .y 0.1
op greaterThanEq .x *tmp6 0.2
```

## Condition Streamlining

Condition streamlining is performed when the condition compares a variable to `false` using an `equal`/`notEqual` comparison (this effectively determines whether the value stored in the variable represents `true` or `false`), and the `op` instruction has a relational operator. In these cases, which may be produced directly by the compiler, or are a result of boolean operators in expressions, the condition can be evaluated directly by the conditional instruction.

The optimization doesn't happen when the resulting condition requires strict inequality and emulating the strict inequality with a `select` is not possible.

A typical example of a streamlined condition is:

```Mindcode
#set condition-optimization = none;
while !(n < 0) do
    n++;
end;
```

compiles to:

```mlog
jump 0 lessThan :n 0
op add :n :n 1
jump 1 greaterThanEq :n 0
```

The unoptimized version of the same code is:

```Mindcode
#set condition-optimization = none;
while !(n < 0) do
    n++;
end;
```

compiles to:

```mlog
op lessThan *tmp0 :n 0
op equal *tmp1 *tmp0 false
jump 0 equal *tmp1 false
op add :n :n 1
op lessThan *tmp0 :n 0
jump 3 equal *tmp0 false
```

---

[&#xAB; Previous: Case Switching](CASE-SWITCHING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Data Flow Optimization &#xBB;](DATA-FLOW-OPTIMIZATION.markdown)
