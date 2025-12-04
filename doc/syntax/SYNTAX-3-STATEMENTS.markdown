# Control Flow Statements

Control flow statements are statements that alter the flow of code. They either branch, i.e., execute one of several possible alternatives, or they loop, i.e., repeat part of the code one or more times.

# Loops

There are several types of loops:
* [while loops](#while-loops)
* [do-while loops](#do-while-loops)
* [range iteration loops](#range-iteration-loops)
* [list iteration loops](#list-iteration-loops)
* [C-style loops](#c-style-loops)

## While Loops

The `while` loop gets repeated as long as the condition remains true:

```Mindcode
while @unit == null do
    ubind(@poly);
end;
```

## Do-While Loops

Similar to while loops, except the condition is placed at the end of the loop. Do-while loops therefore always execute at least once:

```Mindcode
do
    ubind(@poly);
while @unit == null;
```

> [!NOTE]
> In version 3.0.0, the `loop` keyword became optional. The keyword will be deprecated and then removed in a future release. 

## Range Iteration Loops

Loop over a range of values, in an inclusive or exclusive fashion. The `..` range operator indicates an inclusive range:

```Mindcode
for var n in 14 .. 18 do
    println(n);
end;
printflush(message1);

// prints 14, 15, 16, 17 and 18 on separate lines
```

The `...` range operator indicates an exclusive range:

```Mindcode
var sum = 0;
for var addr in 0 ... 64 do
    sum += cell1[addr];
end;
var avg = sum / 64;
```

This loop will calculate the average of all 64 cells (0-based index) of `cell1`.

It is also possible to use expressions to specify the ranges:

```Mindcode
var sum = 0;
for var n in firstIndex + 1 .. lastIndex - 1 do
    sum += cell1[n];
end;
```

> [!NOTE] 
> The range is evaluated before the loop begins. If the value of the upper bound changes while the loop executes, it isn't reflected while the loop executes. To have the condition fully evaluated on each iteration, use a [C-style loop](#c-style-loops) or a [while loop](#while-loops).
> 
> However, if a volatile built-in is used as a bound (such as `@links`), no defensive copy will be created and the variable will be used in the condition directly. This doesn't apply to expressions involving volatile built-ins: `for n in 0 .. @links - 1` causes `@links - 1` to be evaluated and stored in a temporary variable.
> 
> The suggested way to loop over all linked blocks is
> 
> ```
> for n = 0 ... @links do
>     block = getlink(n);
>     /* do something with block */
> end;
> ```

It is possible to iterate the range in descending order by specifying `descending`:

```Mindcode
for var n in 14 ... 18 descending do
    println(n);
end;
printflush(message1);

// prints 17, 16, 15 and 14 on separate lines
```

The `descending` keyword just reverses the order of loop iterations. The range still needs to specify the lower bound first and the upper bound second. If the range is exclusive (as in the example above), the iteration starts at the upper bound value decreased by one.

> [!IMPORTANT]
> Currently, range iteration loops can only increment/decrement the value by 1. If the start value is greater than the end value, the loop body won't get executed at all, both in ascending and descending iteration order. 

## List Iteration Loops

Loop over a fixed collection of values or expressions:

```Mindcode
for u in @mono, @poly, @mega do
    ubind(u);
    if @unit != null then
        break;
    end;
end;
print(u);
printflush(message1);
```

Tries to bind a mono, poly, or mega, in this order, ending the loop when successfully binding one.

The list of values is fixed—it cannot be stored in a variable, for example, as Mindustry Logic itself doesn't support dynamic arrays or collections. It is possible to specify an expression in the list of values, though, and each expression is evaluated at the beginning of the iteration that uses it. This loop

```Mindcode
var n = 0;
for var a in foo(), foo(), foo() do
    print(a, "\n");
end;
def foo()
    ++n;
end;
printflush(message1);
```

prints values 1, 2, 3, as the `foo()` function call is evaluated at the beginning of each iteration.

The list iterator loop can use more loop variables to process several items from the list at once:

```Mindcode
for var unit, count in
    @mono, 5, 
    @poly, 4,
    @mega, 2
do
    print($"$unit: $count\n");
end;
printflush(message1);
```

This code will print out `mono: 5`, `poly: 4` and `mega: 2` on separate lines.

The values in the list aren't organized into tuples. You can put them on separate lines, as shown in the example, to keep them organized. The list length must be divisible by the number of loop control variables.

If you use expressions based on the values of the loop control variables in the list, the results are generally undefined. Example:

```Mindcode
var a = 1;
var b = 2;
for a, b in b, a do
    print(a, b);
end;
```

This code prints out `22` and not `21`, as might be expected.

### Arrays

When an array or subarray is used in the value list (external or internal), the array elements are concatenated with the rest of the list:

```Mindcode
var a[] = (1, 2, 3, 4, 5);
external(cell1) b[] = (6, 7, 8, 9);

// Prints 1234506789
for var i in a, 0, b do
    print(i);
end;
println();

// Prints 12342345
for var i in a[0 .. 3], a[1 .. 4] do
    print(i);
end;  
println();

// Prints 3 7 5 13 17
for var i, j in a, 0, b do
    println(i + j);
end;  
```

> [!TIP]
> It is generally more efficient to use list iteration loop rather than other forms of loops (e.g., range iteration loop combined with index access) for internal arrays. For external arrays, index access is about as effective as list iteration loop, but produces smaller code.
> 
> When loop unrolling optimization is applied, the resulting code is identical regardless of the type of loop used.  

### Modifications of variables in the list

If the elements of the list being iterated over are variables or arrays, it is possible to change their values by declaring the loop control variable with the `out` modifier:

```Mindcode
var a = 1, b = 2, c = 3, d = 4;
for var out i in a, b, c, d do
    print(i);
    i = i * 2;
end;

println();
print(a, b, c, d);
```

This code will print "1234" on one line, followed by "2468" on the second line.

It is possible to declare more than one variable in the loop as output:

```Mindcode
var a = 1, b = 2, c = 3, d = 4;
for var out i, out j in a, b, c, d do
    tmp = i;
    i = j;
    j = tmp;
end;

print(a, b, c, d);
```

This code swaps values of `a` and `c` with `b` and `d`, producing "2143" on output.

It is also possible to use a list iteration loop to initialize variables:

```Mindcode
var index = 0;
for var out i in a, b, c, d do
    i = ++index;
end;

print(a, b, c, d);
```

This code initializes values `a`, `b`, `c` and `d` to `1`, `2`, `3` and `4` respectively. No warning about these variables not being initialized is made because their initial values aren't used inside the loop body.

If some elements in the list cannot be modified, it is an error if it is assigned to an `out` loop control variable:

```
// Error - 'c + 1' is not a variable
for var out i in a, b, c + 1, d do
    i = rand(10);
end;
```

### Parallel iteration

It is possible to specify multiple iterators and their values in the loop. In each iteration, all iterators are assigned values from their respective lists. Iterators/values groups are separated using a semicolon. Iterators in each group may be declared `out`, and each group can have different number of iterators. The only requirement is that all iterator groups must be provided with data for the same number of iterations. 

```Mindcode
var a[20], b[10];

// Ten iterations in total: a has 20 elements, but feeds 2 iterators
for var i, j in a; var out k in b do
    k = i + j;
end;
```

By combining subarrays and parallel iteration, arrays can be processed by list iteration loops in many ways. For example, it is possible to iterate over all adjacent pairs of elements in the array:

```Mindcode
var a[10];

var index = 0;
for var out i in a do
    i = index++;
end;

for var e1 in a[0 ... 9]; e2 in a[1 ... 10] do
    print(e1, e2, " ");
end;

printflush(message1);
```

This code outputs `01 12 23 34 45 56 67 78 89 `.

This can be used in, for example, a bubble sort algorithm:

```Mindcode
const SIZE = 10;
var a[SIZE];

for var out i in a do
    i = floor(rand(1000));
end;

for var i in a do
    println(i);
end;

// Bubblesort!
do
    var swapped = false;
    for var out i in a[0 ... 9]; var out j in a[1 ... 10] do
        if i > j then
            var x = i; i = j; j = x;
            swapped = true; 
        end;
    end;
while swapped;

println();

for var i in a do
    println(i);
end;
```

### Descending iteration order

It is possible to execute the loop in descending order by specifying `descending` keyword.

```Mindcode
// This code prints "4321"
for var i in 1, 2, 3, 4 descending do
    print(i);
end;
```

In the case of parallel iterations, each group of iterators can be processed in ascending or descending order separately:

```Mindcode
var a[10], b[10];

for var i in a descending; var out k in b do
    k = 2 * i;
end;
```

In case of multiple iterators, the `descending` keyword reverses the order of iterations. Individual iterators get assigned the same values, just in descending order:

```Mindcode
// Prints "12345678"
for var i, j in 1, 2, 3, 4, 5, 6, 7, 8 do
    print(i, j);
end;
printflush(message1);

// Prints "78563412"
for var i, j in 1, 2, 3, 4, 5, 6, 7, 8 descending do
    print(i, j);
end;
printflush(message1);
```

Descending iteration order is especially useful with varargs, where it provides the only means to access arrays in reverse order:

```Mindcode
const SIZE = 10;

var array[SIZE];

begin
    for var i in 0 ... SIZE do
        array[i] = i;
    end;

    reverse(out array);
    
    print(array);
    printflush(message1);
end;

inline void reverse(array...)
    // We need to stop in the middle, otherwise the elements would get swapped twice
    var count = length(array) \ 2;
    for var out i in array; var out j in array descending do
        if --count >= 0 then
            var t = i; i = j; j = t;
        end;
    end;
end;
```

## C-Style Loops

The syntax is similar to C's, except for the absence of parenthesis and the `do` keyword:

```Mindcode
// Visit every block in a given region, one at a time, starting from the bottom
// left and ending at the upper right
var dx = 1;
for var x = SW_X, y = SW_Y; x < NE_X && j < NE_Y ; x += dx do
    // do something with this block
    if x == NE_X then
        dx *= -1;
        y += dy;
    end;
end;
```

## Break and continue

You can use a `break` or `continue` statement inside a loop in the usual sense (`break` exits the loop, `continue` skips the rest of the current iteration):

```Mindcode
while not within(x, y, 6) do
    approach(x, y, 4);
    if @unit.@dead == 1 then
        break;
    end;
    // ...
end;
```

### Using labels with break or continue

An unlabeled `break` statement exits the innermost `for` or `while` statement, however a labeled `break` can exit from an outer statement. It is necessary to mark the outer statement with a label, and then use the `break <label>` syntax, as shown here:

```Mindcode
MainLoop:
for var i in 1 .. 10 do
    for var j in 5 .. 20 do
        if i > j then
            break MainLoop;
        end;
        print(j);
    end;
end;
```

Similarly, `continue MainLoop;` skips the rest of the current iteration of both the inner loop and the main loop. Every loop in Mindcode can be marked with a label, and the break or continue statements can use those labels to specify which of the currently active loops they operate on.

> [!TIP]
> Usually, a `break` or `continue` statement will be the last statements in a block of code (typically in an `if` or `case` statement). It doesn't make sense to put additional statements or expressions after a `break` or `continue`, since that code would never get executed and will be removed by the optimizer.

# Conditionals

Mindcode offers three types of conditionals: if/else expressions, the ternary operator, and case/when expressions. Ternary operator was described in the [previous chapter](SYNTAX-2-EXPRESSIONS.markdown#ternary-operator).

## If/Else Expressions

In Mindcode, `if` is an expression, meaning it returns a value. The returned value is the last value of the branch. For example:

```Mindcode
var result = if n == 0 then
    "ready";
else
    "pending";
end;
```

Depending on the value of `n`, `result` will contain the one of `ready` or `pending`.

To handle more than two alternatives, you can use `elsif` as an alternative to nested `if` statements:

```Mindcode
var text = if n > 0 then
    "positive";
elsif n < 0 then
    "negative";
else
    "zero";
end;
```

is equivalent to

```Mindcode
var text = if n > 0 then
    "positive";
else
    if n < 0 then
        "negative";
    else
        "zero";
    end;
end;
```

## Case Expressions

Case expression is another way of writing conditionals. Use case expression when you need to test a value against multiple different alternatives:

```Mindcode
var status = case num_enemies
    when 0 then
        "chill";
    when 1, 2 then
        "alert";
    when 3, 4, 5 then
        "vigilant";
    else
        "nuke-the-place";
end;
```

Multiple comma-separated expressions can be listed after each `when` keyword. It is also possible to use range expressions and even mix them with normal expression like this:

```Mindcode
var text = case number
    when 0, 1, 2**3 .. 2**5, 42, -115 then
        "A number I like";
    when 10**5 .. 10**9 then
        "A very big number";
    else
        "An ugly number";
end;
```

### `null` values

When a `null` literal is used as a value in the `when` clause, Mindcode generates a strict comparison to that literal. If both a zero literal and a `null` literal are present in your `when` values, both `0` and `null` are matched using strict comparison:

```Mindcode
var text = case number
    when 0, 1, 2**3 .. 2**5, 42, -115 then
        "A number I like";
    when 10**5 .. 10**9 then
        "A very big number";
    when null then
        "A null";
    else
        "An ugly number";
end;
```

If the `when null` clause is not used, or the `when` clause contains an expression that evaluates to `null` or zero (as opposed to a `null` or zero literal), `null` and zero are not distinguished by the case statement.

While the [Case Switching optimization](optimizations/CASE-SWITCHING.markdown) can alter case expressions heavily, the original behavior described here is preserved. 

### Additional considerations

* Some expressions after the `when` keyword might or might not get evaluated, depending on the value of the case expression. Do not use expressions with side effects (such as a function call that would modify some global variable). 
* Avoid having several `when` branches matching the same value -- currently the first matching branch gets executed, but the behavior might change in the future.

# The `end()` function

The `end()` function maps to the `end` instruction, and as such has a special meaning - it resets the execution of the program and starts it from the beginning again. In this sense, the `end()` function is one of control flow statements. The function may be called from anywhere, even from a recursive function. The following rules apply when the function is invoked: 

* the processor starts executing the program from the beginning,
* values of existing variables are preserved (the last value written to any uninitialized[^1] global or main variable before `end()` is called is preserved),
* the call stack is reset - calling recursive functions starts from the topmost level again.

[^1]: Only uninitialized variables are handled this way. Any value assigned to an initialized variable before calling `end()` would get overwritten with whatever value the variable is initialized to when the program execution is restarted.  

---

[&#xAB; Previous: Expressions](SYNTAX-2-EXPRESSIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Functions &#xBB;](SYNTAX-4-FUNCTIONS.markdown)
