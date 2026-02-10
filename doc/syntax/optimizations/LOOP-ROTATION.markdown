# Loop Rotation

The loop rotation improves loops with the condition at the beginning. By default, the body of these loops contains a jump which jumps back to the beginning of the opening condition. Loop rotation can either move the condition to the end of the loop body or duplicate the entire loop condition or its part at the end of the loop body.

Loop rotation supports both fully evaluated and short-circuited conditions.

## Full loop rotation

Full loop rotation copies the loop condition to the end of the loop. The front condition is executed just once to determine whether the loop should be executed at all. Then the loop body gets executed, followed by the end condition, which jumps back to the beginning of the loop body when it evaluates as true. Furthermore, if the front condition can be shown to be always true, it gets eliminated. This provides several possible benefits:

* the condition is only evaluated at the beginning of the loop when necessary,
* parts of the front condition can be eliminated when they aren't needed to determine whether the loop should be executed,
* the unconditional jump jumping back to the beginning of the loop is replaced by the actual condition, saving one execution step.

Example:

```Mindcode
var a = 105;
var b = 21;

while a > 0 do
    var m = max(a, b);
    b = min(a, b);
    a = m % b;
end;

print($"The largest common divisor is $b.");
```

compiles to:

```mlog
set .a 105
set .b 21
op max :m .a .b
op min .b .a .b
op mod .a :m .b
jump 2 greaterThan .a 0
print "The largest common divisor is "
print .b
print "."
```

If the front condition can be eliminated, the full loop rotation typically decreases the code size and decreases execution time in simple cases. In more complex cases, the code size may actually increase (when the condition performs additional computation with effects outside the condition), which makes it a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations).

Note: performing the loop rotation often requires inverting the condition of some conditional jumps in the duplicated condition. When the condition includes a strict equal comparison (`===` or `!==`), such operations may increase the execution time of the rotated condition. This might decrease or outright remove the optimization benefit from loop executions but still saves executing the condition on the first entry to the loop:

```Mindcode
#set target = 7m;
var index = 0;
var a = 0;

while a !== null do
    a = getlink(index++);
    print(a);
end;
print("Done!");
```

compiles to:

```mlog
set .index 0
set *tmp2 .index
op add .index .index 1
getlink .a *tmp2
print .a
op strictEqual *tmp4 .a null
jump 1 equal *tmp4 false
print "Done!"
```

## Partial loop rotation

When the loop condition involves short-circuit operations, the loop rotation can be performed partially. In this case, parts of the condition in front of the loop are duplicated to the end of the loop body. The non-duplicated remaining parts of the original condition are reused (by rerouting some jumps from the duplicated condition to the original condition).

In a typical case, a partially rotated loop completely avoids the jump returning to the beginning of the loop condition. If a strict-equal comparison (`===` or `!==`) is involved, the additional jump might not be avoided entirely, but the average execution time of the loop should still be reduced.

Example:

```Mindcode
#set symbolic-labels;
while switch1.enabled and switch2.enabled do
    print("Doing something.");
end;
print("A switch has been reset.");
```

which produces:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    sensor *tmp0 switch1 @enabled
    jump label_7 equal *tmp0 false
label_2:
    sensor *tmp1 switch2 @enabled
    jump label_7 equal *tmp1 false
        print "Doing something."
    sensor *tmp0 switch1 @enabled
    jump label_2 notEqual *tmp0 false
    label_7:
    print "A switch has been reset."
```

Coupled with the other optimizations, complex conditions can sometimes be simplified a lot:

```Mindcode
#set symbolic-labels;
a = true;
c = true;

println("Before loop");
while (a or b) and (c or d) and (e or f) do
    println("In loop");
    a = rand(10) > 5;
    c = rand(10) > 5;
    e = rand(10) > 5;
end;
println("After loop");
```

which produces:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set :c true
    print "Before loop\n"
label_2:
    jump label_4 notEqual :c false
    jump label_14 equal :d false
label_4:
    jump label_6 notEqual :e false
    jump label_14 equal :f false
label_6:
        print "In loop\n"
        op rand *tmp0 10 0
        op rand *tmp2 10 0
        op greaterThan :c *tmp2 5
        op rand *tmp4 10 0
        op greaterThan :e *tmp4 5
    jump label_2 greaterThan *tmp0 5
    jump label_2 notEqual :b false
    label_14:
    print "After loop\n"
```

---

[&#xAB; Previous: Loop Hoisting](LOOP-HOISTING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Loop Unrolling &#xBB;](LOOP-UNROLLING.markdown)
