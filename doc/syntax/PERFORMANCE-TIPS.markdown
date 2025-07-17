# Performance tips

This document contains some tips for writing better-performing code in Mindcode.

# Vector length calculation

Mindustry Logic provides an instruction which directly computes the length of a vector. The standard formula for vector length is

```Mindcode
length = sqrt(x * x + y * y);
print(length);
```

When expressed by the above formula, the resulting computation takes four instructions:

```mlog
op mul *tmp0 :x :x
op mul *tmp1 :y :y
op add *tmp2 *tmp0 *tmp1
op sqrt :length *tmp2 0
print :length
```

A better calculation uses just one instruction, `op len`, which maps to the `len()` function:

```Mindcode
length = len(x, y);
print(length);
```

which compiles to

```mlog
op len :length :x :y
print :length
```

Note: an optimization which would replace the sequence of the four instructions with `op len` is planned, but hasn't been implemented yet.

# Conditions

At this moment, Mindcode produces suboptimal code for conditions using boolean operators. For example, this code

```Mindcode
if x > 0 and x < 10 then
    print("yes");
end;
printflush(message1);
```

compiles to

```mlog
op greaterThan *tmp0 :x 0
op lessThan *tmp1 :x 10
op land *tmp2 *tmp0 *tmp1
jump 5 equal *tmp2 false
print "yes"
printflush message1
```

A better mlog code can be written like this:

```
jump skip lessThanEq :x 0
jump skip greaterThanEq :x 10
print "yes"
skip:
printflush message1
```

The problem is not as pronounced when the condition doesn't contain relational operators:

```Mindcode
if switch1.enabled and switch2.enabled then
    print("yes");
end;
printflush(message1);
```

compiles to

```mlog
sensor *tmp0 switch1 @enabled
sensor *tmp1 switch2 @enabled
op land *tmp2 *tmp0 *tmp1
jump 5 equal *tmp2 false
print "yes"
printflush message1
```

A slightly better mlog code can be written like this:

```
sensor *tmp0 switch1 @enabled
jump 5 equal *tmp0 false
sensor *tmp1 switch2 @enabled
jump 5 equal *tmp1 false
print "yes"
printflush message1
```

For the next version of Mindcode, an update to significantly improve conditions is planned. It is advised not to rewrite your code if the suboptimal performance is not causing you problems and wait for improvements in Mindcode. In cases when the code generated for the usual conditions is unacceptable, the following tricks can be used:

## Using nested `if` conditions

```
if x > 0 then
    if x < 10 then
        print("yes");
    end;
end;
printflush(message1);
```

compiles into:

```mlog
jump 3 lessThanEq :x 0
jump 3 greaterThanEq :x 10
print "yes"
printflush message1
```

## Using `case` expressions

Case expressions evaluate conditions differently and may produce better code, if it is possible to express the condition as a `when` expression:

```Mindcode
case x
    when 1 ... 10 then print("yes");
end;
printflush(message1);
```

compiles into:

```mlog
jump 3 lessThan :x 1
jump 3 greaterThanEq :x 10
print "yes"
printflush message1
```

Mindcode applies a very powerful optimization to case expressions, especially to larger case expressions. A case expression typically produces much faster code than a series of `if` statements. 

## Avoiding boolean operators in loop conditions

If the condition is part of a loop, it might be possible to avoid the boolean expression in it:

```Mindcode
// while x > 0 and x < 10 do
while x > 0 do
    if x >= 10 then break; end;
    print("in loop");
    x = vault1.@coal;  // Just changing the value of x in some way       
end;
printflush(message1);
```

which compiles into practically optimal code:

```mlog
jump 5 lessThanEq :x 0
jump 5 greaterThanEq :x 10
print "in loop"
sensor :x vault1 @coal
jump 1 greaterThan :x 0
printflush message1
```

# Loop unrolling

One of Mindcode's strongest optimization tools is loop unrolling. When a loop is unrolled, instructions manipulating the loop control variable are eliminated, as well as jumps, potentially saving a lot of execution time. For a loop to be unrolled, the following conditions must be met:

* Mindcode must be able to determine the number of iterations.
* Only one loop control variable is used.
* Only simple updates of the loop control variable are used.

Furthermore, Mindcode is able to unroll more complex loops on [advanced optimization level](SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling-preconditions).

## Determining the number of iterations

Mindcode determines the number of iterations by analyzing the loop condition. It doesn't take `break` statements into account. The following loop won't get unrolled, even though the maximal number of iterations is obvious:

```Mindcode
i = 0;
while switch1.enabled do
    print(++i);
    if i > 10 then break; end;
end;
```

You can rewrite the loop to allow unrolling by switching the conditions:

```Mindcode
i = 0;
do
    if not switch1.enabled then break; end;
    print(i++);
while i <= 10; 
```

## Using just one loop control variable

Sometimes a loop needs to be rewritten so that a single loop control variable is used. Consider this code for reversing an array stored in a memory cell:

```Mindcode
const SIZE = 10;
for var i = 0, j = SIZE - 1; i < j; i++, j-- do
    var t = cell1[i];
    cell1[i] = cell1[j];
    cell1[j] = t;
end;
```

This code can't be unrolled because there are two control variables. A better approach is to use just one loop control variable and derive the other variable from it:

```Mindcode
const SIZE = 10;
for var i in 0 ... SIZE \ 2 do
    var j = SIZE - 1 - i;
    var t = cell1[i];
    cell1[i] = cell1[j];
    cell1[j] = t;
end;
```

Since `j` isn't used in the loop condition, the loop can be unrolled.

## Using simple updates of control variables

On `advanced` level, loop unrolling is capable to unroll almost any deterministic loop, assuming the updates to the control variable are "simple," meaning they do not depend on any other variable than the loop control variable. More complex expressions might need to be rewritten to multiple simple updates. This loop cannot be unrolled:

```Mindcode
i = 0;
while i < 1000 do
    i = 2 * i + 1;
    println(i);
end;
```

but this can:

```Mindcode
i = 0;
while i < 1000 do
    i = 2 * i;
    i++;
    println(i);
end;
```

```mlog
print "1\n3\n7\n15\n31\n63\n127\n255\n511\n1023\n"
```

# Arrays in loops

Mindcode offers two mechanisms to iterate over arrays: list iteration loops, and loops accessing array elements using the loop control variable as an index. Oftentimes, it is possible to express the same algorithm using either of these two approaches, but they may offer different performance in Mindcode due to various factors.

## Arrays in unrolled loops

If the loop over an array or arrays gets unrolled, the produced code is effectively the same in most cases. However, in cases where the list iteration loop updates the array, Mindcode is unable to properly resolve the self-referential update and produces suboptimal code. Compare these two approaches:

```Mindcode
param p = 0;
var array[3];

for var out a in array do
    a++;
end;

println(array);
```

produces

```mlog
op add :a .array*0 1
set .array*0 :a
op add :a .array*1 1
set .array*1 :a
op add :a .array*2 1
set .array*2 :a
print .array*0
print .array*1
print :a
print "\n"
```

Index-based approach, on the other hand:

```Mindcode
param p = 0;
var array[3];

for i in 0 ... length(array) do
    array[i]++;
end;

println(array);
```

produces

```mlog
op add .array*0 .array*0 1
op add .array*1 .array*1 1
op add *tmp1 .array*2 1
op add .array*2 .array*2 1
print .array*0
print .array*1
print *tmp1
print "\n"
```

The long-term goal is to produce identical, optimal code in both of these cases. At this moment, there's room for improvement in both approaches, but index-based loops may produce better code in some circumstances.

## Arrays in non-unrolled loops

When the loop cannot get unrolled for some reason, list iteration loops are generally faster than loops using index-based array access. When more than a single variable is used, or when the array is modified in the loop, list iteration loops provide much better performance than index-based loops. Index-based access may be preferable when the arrays are huge, as a single jump table can be generated for the array to be accessed from multiple places of the program, saving a considerable amount of instruction space.  

Example of simple array access:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var array[4];

var x = 0;
/// List iteration loop (5 instructions per iteration)
for var a in array do
    x += a;
end;

var y = 0;
/// Index-based access (7 instructions per iteration)
for var i in 0 ... length(array) do
    y += array[i];
end;

print(x, y);
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set .x 0
    # List iteration loop (5 instructions per iteration)
    set :a .array*0
    op add *tmp0 @counter 1
    jump label_14 always 0 0
    set :a .array*1
    op add *tmp0 @counter 1
    jump label_14 always 0 0
    set :a .array*2
    op add *tmp0 @counter 1
    jump label_14 always 0 0
    set :a .array*3
    op add *tmp0 @counter 1
    jump label_14 always 0 0
    jump label_16 always 0 0
    label_14:
        op add .x .x :a
        set @counter *tmp0
label_16:
    set .y 0
    # Index-based access (7 instructions per iteration)
    set :i 0
    label_18:
        op mul *tmp3 :i 2
        op add @counter @counter *tmp3
        set *tmp2 .array*0
        jump label_27 always 0 0
        set *tmp2 .array*1
        jump label_27 always 0 0
        set *tmp2 .array*2
        jump label_27 always 0 0
        set *tmp2 .array*3
    label_27:
        op add .y .y *tmp2
    op add :i :i 1
    jump label_18 lessThan :i 4
    print .x
    print .y
```

The difference increases with each additional array access within the loop:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var a[4], b[4], c[4];

/// List iteration loop (7 instructions per iteration)
for var out x in a; var y in b; var z in c do
    x = y + z;
end;

/// Index-based access (15 instructions per iteration)
for var i in 0 ... length(a) do
    a[i] = b[i] + c[i];
end;

print(a);
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    # List iteration loop (7 instructions per iteration)
    set :y .b*0
    set :z .c*0
    op add *tmp0 @counter 1
    jump label_20 always 0 0
    set .a*0 :x
    set :y .b*1
    set :z .c*1
    op add *tmp0 @counter 1
    jump label_20 always 0 0
    set .a*1 :x
    set :y .b*2
    set :z .c*2
    op add *tmp0 @counter 1
    jump label_20 always 0 0
    set .a*2 :x
    set :y .b*3
    set :z .c*3
    op add *tmp0 @counter 1
    jump label_20 always 0 0
    jump label_22 always 0 0
    label_20:
        op add :x :y :z
        set @counter *tmp0
label_22:
    set .a*3 :x
    # Index-based access (15 instructions per iteration)
    set :i 0
    label_24:
        op mul *tmp9 :i 2
        op add @counter @counter *tmp9
        set *tmp5 .b*0
        jump label_33 always 0 0
        set *tmp5 .b*1
        jump label_33 always 0 0
        set *tmp5 .b*2
        jump label_33 always 0 0
        set *tmp5 .b*3
    label_33:
        op add @counter @counter *tmp9
        set *tmp7 .c*0
        jump label_41 always 0 0
        set *tmp7 .c*1
        jump label_41 always 0 0
        set *tmp7 .c*2
        jump label_41 always 0 0
        set *tmp7 .c*3
    label_41:
        op add *tmp8 *tmp5 *tmp7
        op add @counter @counter *tmp9
        set .a*0 *tmp8
        jump label_50 always 0 0
        set .a*1 *tmp8
        jump label_50 always 0 0
        set .a*2 *tmp8
        jump label_50 always 0 0
        set .a*3 *tmp8
    label_50:
    op add :i :i 1
    jump label_24 lessThan :i 4
    print .a*0
    print .a*1
    print .a*2
    print .a*3
```

The reason is that in the list iteration loop, all array accesses are performed using direct access to elements, while in index-based access, each array element is accessed separately using a jump table.

Optimizations aimed at merging multiple array accesses are planned but aren't yet available.

> [!NOTE]
> Many different array access patterns can be encoded using parallel list-iteration syntax, subarrays and/or the `descending` keyword. If your algorithm accesses the arrays linearly, there's probably a way to encode it using list-iteration loops.

For example, a list-iteration loop reversing an array can be encoded like this:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;

var array[10];

for
    var out a in array[0 ... length(array) \ 2];
    var out b in array[length(array) - length(array) \ 2 ... length(array)] descending
do
    var x = a;
    a = b;
    b = x;
end;

print(array);
```

### Breaking out of list iteration loops

List-iteration loops are always generated for the entire array. If you want to iterate over a part of the array only, use subarrays instead of the `break` statement to limit the number of iterations, preventing both generation of unwanted iterations and code for computing/testing iterations:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var array[10];

/// Looping through half of the array: break 
var i = 0;
for var a in array do
    print(a);
    if ++i >= length(array) \ 2 then
        break;
    end;
end; 

/// Looping through half of the array: subarray
for var a in array[0 ... length(array) \ 2] do
    print(a);
end; 
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    # Looping through half of the array: break 
label_0:
    set .i 0
    set :a .array*0
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*1
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*2
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*3
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*4
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*5
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*6
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*7
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*8
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    set :a .array*9
    op add *tmp0 @counter 1
    jump label_32 always 0 0
    jump label_36 always 0 0
    label_32:
        print :a
        op add .i .i 1
        jump label_36 greaterThanEq .i 5
        set @counter *tmp0
    # Looping through half of the array: subarray
label_36:
    set :a.1 .array*0
    op add *tmp3 @counter 1
    jump label_52 always 0 0
    set :a.1 .array*1
    op add *tmp3 @counter 1
    jump label_52 always 0 0
    set :a.1 .array*2
    op add *tmp3 @counter 1
    jump label_52 always 0 0
    set :a.1 .array*3
    op add *tmp3 @counter 1
    jump label_52 always 0 0
    set :a.1 .array*4
    op add *tmp3 @counter 1
    jump label_52 always 0 0
    jump label_0 always 0 0
    label_52:
        print :a.1
        set @counter *tmp3
```

---

[« Previous: Troubleshooting Mindcode](TROUBLESHOOTING.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry Tips and Tricks »](MINDUSTRY-TIPS-N-TRICKS.markdown)
