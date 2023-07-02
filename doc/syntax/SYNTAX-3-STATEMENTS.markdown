# Control flow statements

Control flow statements are statements that alter the flow of code.
They either branch, i.e. execute one of several possible alternatives,
or they loop, i.e. repeat part of the code (zero,) one or more times.

# Loops

There are several types of loops:
* [while loops](#while-loops)
* [do-while loops](#do-while-loops)
* [range iteration loops](#range-iteration-loops)
* [list iteration loops](#list-iteration-loops)
* [C-style loops](#c-style-loops)

## While Loops

The `while` loop gets repeated as long as the condition remains `true`:

```
while @unit == null
    ubind(@poly)
end
```

## Do-While Loops

Similar to while loops, except the condition is placed at the end of the loop. Do-while loops therefore always execute at least once:

```
do
    ubind(@poly)
loop while @unit == null
```

## Range Iteration Loops

Loop over a range of values, in an inclusive or exclusive fashion. The `..` range operator indicates an
inclusive range:

```
for n in 14 .. 18
  println(n)
end
printflush(message1)

// prints 14, 15, 16, 17 and 18 on separate lines
```

The `...` range operator indicates an exclusive range:

```
sum = 0
for addr in 0 ... 64
  sum += cell1[addr]
end
avg = sum / 64
```

This loop will calculate the average of all 64 cells (0-based index) of `cell1`.

It is also possible to use expressions to specify the ranges:

```
for n in firstIndex + 1 .. lastIndex - 1
  sum += cell1[n]
end
```

The range is evaluated before the loop begins. If the value of the upper bound changes while the loop executes, it 
isn't reflected while the loop executes. To have the condition fully evaluated on each iteration, use
a [C-style loop](#c-style-loops) or a [while loop](#while-loops).   

> **Note**: currently, range iteration loops can only increment the value by 1, and only support increasing values.
> If the start value is greater than the end value, the loop body won't get executed at all.

## List Iteration Loops

Loop over a fixed collection of values or expressions:

```
for u in (@mono, @poly, @mega)
    ubind(u)
    if @unit != null
        break
    end
end
print(u)
printflush(message1)
```

Tries to bind a mono, poly or mega, in this order, ending the loop when successfully binding one.

The list of values is fixed -- it cannot be stored in a variable, for example, as Mindustry Logic doesn't support arrays or collections. It is possible to specify an expression in the list of values, though, and each expression is evaluated right before the loop utilizing the expression is executed. This loop

```
a = 0
for a in (a + 1, a + 1, a + 1)
    print(a, "\n")
end
printflush(message1)
```

prints values 1, 2, 3 (at the beginning of each iteration the loop variable -- `a` in this case -- is set to the value of the next expression in the list).

## C-Style Loops

The syntax is similar to C's, except for the absence of parenthesis:

```
// Visit every block in a given region, one at a time, starting from the bottom
// left and ending at the upper right
dx = 1
for x = SW_X, y = SW_Y; x < NE_X && j < NE_Y ; x += dx
    // do something with this block
    if x == NE_X
        dx *= -1
        y += dy
    end
end
```

## Break and continue

You can use a `break` or `continue` statement inside a loop in the usual sense (`break` exits the loop,
`continue` skips the rest of the current iteration):

```
while not within(x, y, 6)
    approach(x, y, 4)
    if @unit.dead == 1
        break
    end
    ...
end
```

### Using labels with break or continue

An unlabeled `break` statement exits the innermost `for` or `while` statement, however a labeled `break` can exit from an outer statement.
It is necessary to mark the outer statement with a label, and then use the `break <label>` syntax, as shown here:

```
MainLoop:
for i in 1 .. 10
    for j in 5 .. 20
        if i > j
            break MainLoop
        end
    end
end
```
Similarly, `continue MainLoop` skips the rest of the current iteration of both the inner loop and the main loop.
Every loop in Mindcode can be marked with a label,
and the break or continue statements can use those labels to specify which of the currently active loops they operate on.


> **Note**: usually, a `break` or `continue` statement will be the last statements in a block of code
> (typically in an `if` or `case` statement). It doesn't make sense to put additional statements or expressions
> after a `break` or `continue`, since that code would never get executed and will be removed by the optimizer.

If you do put additional statements after a `break` or `continue`, the compiler will mistake them for a label:

```
while true
    break
    print("This never gets printed")
end
```

The compiler will say:
> 
> Undefined label print
> 

If you insist on putting additional statement after a `break` or `continue`, use semicolon to separate the two statements:

```
while true
    break;
    print("This never gets printed")
end
```


# Conditionals

Mindcode offers 3 types of conditionals: if/else expressions, the ternary operator and case/when expressions.

## If/Else Expressions

In Mindcode, `if` is an expression, meaning it returns a value. The returned value is the last value of the branch. For
example:

```
result = if n == 0
    "ready"
else
    "pending"
end
```

Depending on the value of `n`, `result` will contain the one of `ready` or `pending`.

To handle more than two alternatives, you can use `elsif` as an alternative to nested `if` statements:

```
text = if n > 0
    "positive"
elsif n < 0
    "negative"
else
    "zero"
end
```
is equivalent to

```
text = if n > 0
    "positive"
else
    if n < 0
        "negative"
    else
        "zero"
    end
end
```

## Ternary Operator

The ternary operator (`?:`) is exactly like an if/else expression, except it is more succinct.
Use it when you need a conditional but want to save some space:

```
result = n == 0 ? "ready" : "pending"
```

This is the exact same conditional expression as the first `if` statement above, written on one line.

## Case Expressions

Case expression is another way of writing conditionals. Use case expression when you need to test a value against multiple different alternatives:

```
next_action = case num_enemies
    when 0
        "chill"
    when 1, 2
        "alert"
    when 3, 4, 5
        "vigilant"
    else
        "nuke-the-place"
end
```

Multiple comma-separated expressions can be listed after each `when` keyword.
It is also possible to use range expressions, and even mix them with normal expression like this:

```
text = case number
    when 0, 1, 2**3 .. 2**5, 42, -115 then
        "A number I like"
    when 10**5 .. 10**9 then
        "A very big number"
    else
        "An ugly number"
end
```

**Then keyword**

The `then` keyword at the end of the list of when expressions is optional.
Using it helps to avoid a bug that can happen when you put a superfluous comma at the end of the `when` expression list:

```
case block.type
    when @conduit, @pulse-conduit, @plated-conduit,
        block.enabled = intake
    when @overdrive-projector, @overdrive-dome
        block.enabled = boost
end
```

The comma after the `@plated-conduit` is not meant to be there.
Because of it, the compiler treats the next expression as if it was another of the `when` values.
Rearranging the code might help to understand the meaning of this code snippet:

```
case block.type
    when @conduit, @pulse-conduit, @plated-conduit, block.enabled = intake
        // Do nothing
    when @overdrive-projector, @overdrive-dome
        block.enabled = boost
end
```

The resulting effect is that when the block is some kind of conduit, nothing happens.
When it is something different, the `block.enabled = intake` expression is evaluated,
changing wrong block's state.

If the keyword `then` is used, the compiler will complain about the superfluous comma:

```
case block.type
    when @conduit, @pulse-conduit, @plated-conduit, then
        block.enabled = intake
    when @overdrive-projector, @overdrive-dome then
        block.enabled = boost
end
```

**Additional considerations:**

* Some expressions after the `when` keyword might or might not get evaluated, depending on the value of the case 
  expression. Do not use expressions with side effects (such as a function call that would modify some global variable). 
* Avoid having several `when` branches matching the same value -- currently the first matching branch gets executed, 
  but the behavior might change in the future.

# `end()` function

The `end()` function maps to the `end` instruction, and as such has a special meaning - it resets the execution of 
the program and starts at the beginning again. In this sense, the `end()` function is one of control flow statements.
The function may be called from anywhere, even from a recursive function. The following rules apply when the 
function is invoked: 

* the processor starts executing the program from the beginning,
* values of existing variables are preserved (the last value written to any uninitialized[^1] global or main variable 
  before `end()` is called is preserved),
* the call stack is reset - calling recursive functions starts from the topmost level again.

[^1] Only uninitialized variables are handled this way. Any value assigned to an initialized variable before 
calling `end()` would get overwritten with whatever value the variable is initialized to when the program execution is 
restarted.  

---

[« Previous: Expressions](SYNTAX-2-EXPRESSIONS.markdown) &nbsp; | &nbsp; [Next: Functions »](SYNTAX-4-FUNCTIONS.markdown)
