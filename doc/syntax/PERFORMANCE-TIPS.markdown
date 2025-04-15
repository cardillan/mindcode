# Performance tips

This document contains some tips for writing better performing code in Mindcode.

## Vector length calculation

Mindustry Logic provides an instruction which directly computes length of a vector. The standard formula for vector length is

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

## Conditions

At this moment, Mindcode produces suboptimal code for conditions employing boolean operators. For example, this code

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

For the next version of Mindcode, an update to significantly improve conditions is planned. it is advised not to rewrite your code if the suboptimal performance is not causing you problems, and wait for improvements in Mindcode. In cases when the code generated for the usual conditions is unacceptable,  the following tricks can be used:

### Using nested `if` conditions

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

### Using `case` expressions

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

### Avoiding boolean operators in loop conditions

If the condition is part of a loop, it might be possible to avoid the boolean expresion in it:

```Mindcode
while x > 0 do
    if x >= 10 then break; end;
    print("in loop");
    x = vault1.@coal;  // Jusst changing the value of x in some way       
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

## Loop unrolling

One of Mindcode's strongest optimizations tools is loop unrolling. When a loop is unrolled, instructions manipulating the loop control variable are eliminated, as well as jumps, potentially saving a lot of execution time. For a loop to be unrolled, the following conditions must be met:

* Mindcode must be able to determine the number of iterations.
* Only one loop control variable is used.
* Only simple updates of the loop control variable are used.

Furthermore, Mindcode is able to unroll more complex loops on [advanced optimization level](SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling-preconditions).

### Determining number of iterations

Mindcode determines the number of iterations by analyzing the loop condition. It doesn't take `break` statements into account. The following loop won't get unrolled, even though the maximal number of iterations it apparent:

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

### Using just one loop control variable

Sometimes a loop needs to be rewritten so that a single loop control variable is used. Consider this code for reversing an array stored in a memory cell:

```Mindcode
const SIZE = 10;
for var i = 0, j = SIZE - 1; i < j; i++, j-- do
    var t = cell1[i];
    cell1[i] = cell1[j];
    cell1[j] = t;
end;
```

This code can't be unrolled, because there are two control variables. A better approach is to use just one loop control variable and derive the other variable from it:

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

### Using simple updates of control variables

On `advanced` level, loop unrolling is capable to unroll almost any deterministic loop, assuming the updates to the control variable are "simple", meaning they do not depend on any other variable than the loop control variable. More complex expressions might need to be rewritten to multiple simple updates. This loop cannot be unrolled:

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

---

[« Previous: Troubleshooting Mindcode](TROUBLESHOOTING.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry Tips and Tricks »](MINDUSTRY-TIPS-N-TRICKS.markdown)
