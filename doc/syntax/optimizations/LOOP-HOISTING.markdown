# Loop Hoisting

Loop hoisting is an optimization that tries to identify loop invariant code (i.e., code inside loops which executes identically in each loop iteration) and moves it in front of the loop. This way the code is executed only once, instead of on each loop iteration.

For example, in the following code

```Mindcode
param MAX = 10;
for i in 0 ... MAX do
    print(2 * MAX);
end;
```

the evaluation of `2 * MAX` is moved in front of the loop body in the compiled code:

```mlog
set MAX 10
set :i 0
jump 0 greaterThanEq 0 MAX
op mul *tmp0 2 MAX
print *tmp0
op add :i :i 1
jump 4 lessThan :i MAX
```

The optimization affects even instructions setting up parameters or return addresses for stackless function calls and array access:

```Mindcode
param cnt = 5;

noinline def foo(x)
    println(x);
end;

var i = cnt;
do
    foo("Huzzah!");
while --i > 0;
printflush(message1);
```

compiles into

```mlog
set cnt 5
set .i cnt
set :foo:x "Huzzah!"
set :foo*retaddr 5
jump 9 always 0 0
op sub .i .i 1
jump 9 greaterThan .i 0
printflush message1
end
print :foo:x
print "\n"
set @counter :foo*retaddr
```

Hoisting the instructions setting up return addresses is not possible when [`symbolic-labels`](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) is set to `true`.

When the `select` optimization is available, Loop Hoisting is capable of handling some conditional expressions as well:

```Mindcode
#set target = 8m;
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
    jump label_9 greaterThanEq 1 MAX
    op mod *tmp0 MAX 2
    select *tmp2 equal *tmp0 0 "Even" "Odd"
label_5:
        print :i
        print *tmp2
    op add :i :i 1
    jump label_5 lessThan :i MAX
    label_9:
    print "end"
```

Some details of loop hoisting depend on the exact structure of the loop.

## Loops with a condition at the end

Loops with a condition at the end are either the product of a `do while` loop, or can be created by the [Loop Rotation](LOOP-ROTATION.markdown) optimization. In any case, it is known that such loops always execute at least once, and any loop invariant code can therefore be safely hoisted in front of the loop, where it is executed just once:

```Mindcode
#set symbolic-labels;
param A = 2;

i = 0;
do
    println(i, "**2 = ", i**2);
while ++i < 2 * A;
```

compiles to:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set A 2
    set :i 0
    op mul *tmp1 2 A
    label_3:
        op pow *tmp0 :i 2
        print :i
        print "**2 = "
        print *tmp0
        print "\n"
    op add :i :i 1
    jump label_3 lessThan :i *tmp1
```

## Fully rotated loops

Fully rotated loops have two conditions: the front condition is executed only once and determines whether the loop will be executed at all, while the end condition is executed at the end of each loop and determines when the loop terminates. In this case, the loop invariant code can be hoisted between the front loop condition and the loop body. This code is then executed only once when the loop is entered:

```Mindcode
#set symbolic-labels;
param A = 2;

status = "Not entered";
for i in 0 ... A do
    status = "Entered";
    print(i);
end;
print(status);
```

compiles to:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set A 2
    set :status "Not entered"
    set :i 0
    jump label_8 greaterThanEq 0 A
    set :status "Entered"
label_5:
        print :i
    op add :i :i 1
    jump label_5 lessThan :i A
    label_8:
    print :status
```

## Nested fully rotated loops

When a fully rotated loop is directly nested within another loop, loop invariant code which has no effect outside the loop is hoisted in front of the inner loop. This allows such code to be hoisted again within the outer loop, recursively. If the inner loop is executed at least once in most cases, the resulting code is more efficient. Example:

```Mindcode
#set symbolic-labels;

param A = 2;
param B = 3;
param C = 0;

status = "Not entered";
i = 0;
while i < 2 * A do
    for j in 0 ... 2 * B do
        for k in 0 ... 2 * C do
            status = "Entered";
            print(3 * A);
        end;
    end;
    i++;
end;
print(status);
```

compiles to:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set A 2
    set B 3
    set C 0
    set :status "Not entered"
    set :i 0
    jump label_22 lessThanEq A 0
    op mul *tmp3 2 B
    op mul *tmp5 2 C
    op mul *tmp6 3 A
    op mul *tmp0 2 A
label_10:
        set :j 0
        jump label_20 lessThanEq B 0
    label_12:
            set :k 0
            jump label_18 lessThanEq C 0
            set :status "Entered"
        label_15:
                print *tmp6
            op add :k :k 1
            jump label_15 lessThan :k *tmp5
            label_18:
        op add :j :j 1
        jump label_12 lessThan :j *tmp3
        label_20:
        op add :i :i 1
    jump label_10 lessThan :i *tmp0
    label_22:
    print :status
```

Here, `set :status "Entered"` is only hoisted in front of the loop body, because it has an effect outside the loop (and executing it while the loop wasn't actually run would change the program semantics). On the other hand, the instructions setting up `*tmp5` and `*tmp6` are hoisted due to the nested loop hoisting rule, but are never actually used, because `C` is zero. To prevent that, an explicit condition can be used:

```Mindcode
#set symbolic-labels;

param A = 2;
param B = 3;
param C = 0;

status = "Not entered";
i = 0;
while i < 2 * A do
    for j in 0 ... 2 * B do
        if C > 0 then
            for k in 0 ... 2 * C do
                status = "Entered";
                print(3 * A);
            end;
        end;
    end;
    i++;
end;
print(status);
```

compiles to:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set A 2
    set B 3
    set C 0
    set :status "Not entered"
    set :i 0
    jump label_22 lessThanEq A 0
    op mul *tmp3 2 B
    op mul *tmp0 2 A
label_8:
        set :j 0
        jump label_20 lessThanEq B 0
    label_10:
            jump label_18 lessThanEq C 0
                op mul *tmp7 2 C
                set :k 0
                set :status "Entered"
                op mul *tmp8 3 A
            label_15:
                    print *tmp8
                op add :k :k 1
                jump label_15 lessThan :k *tmp7
            label_18:
        op add :j :j 1
        jump label_10 lessThan :j *tmp3
        label_20:
        op add :i :i 1
    jump label_8 lessThan :i *tmp0
    label_22:
    print :status
```

## Partially rotated or non-rotated loops

When a loop is only partially rotated or not rotated at all, there isn't a place that gets executed upon the first iteration of the loop only, and the execution of the loop is not guaranteed. If the loop is nested in another loop, instructions not having an effect outside the loop are hoisted in front of the loop (similarly to the case of nested, fully rotated loops). Again, this may produce code which is less efficient if the loop is usually not executed:

```Mindcode
#set symbolic-labels;
#set goal = size;

param x = 1;
var a = x;
var b = !x;

for i in 0 ... x do
    while a or b do
       a--;
       b++;
       print(2 * x);
    end;
end;

print("Done!");
```

compiles to:

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set x 1
    set .a x
    op equal .b x false
    set :i 0
    jump label_14 greaterThanEq 0 x
    op mul *tmp3 2 x
label_6:
        jump label_8 notEqual .a false
        jump label_12 equal .b false
    label_8:
            op sub .a .a 1
            op add .b .b 1
            print *tmp3
            jump label_6 always 0 0
        label_12:
    op add :i :i 1
    jump label_6 lessThan :i x
    label_14:
    print "Done!"
```

# Limitations

At this moment, the following limitations apply:

* Assignments to volatile variables are never hoisted out of the loop.
* If the loop contains a stackless or recursive function call, global variables that might be modified by that function call are marked as loop dependent and expressions based on them aren't hoisted. The compiler must assume the value of the global variable may change inside these functions.

---

[&#xAB; Previous: Jump Threading](JUMP-THREADING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Loop Rotation &#xBB;](LOOP-ROTATION.markdown)
