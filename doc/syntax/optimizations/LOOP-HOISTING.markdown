# Loop Hoisting

Loop hoisting is an optimization that tries to identify loop invariant code (i.e., code inside loops which executes identically in each loop iteration) and moves it in front of the loop. This way the code is executed only once, instead of on each loop iteration.

For example, in the following code

```Mindcode
param MAX = 10;
for i in 0 ... MAX do
    print(2 * MAX);
end;
```

the evaluation of `2 * MAX` is moved in front of the loop in the compiled code:

```mlog
set MAX 10
set :i 0
op mul *tmp0 2 MAX
jump 0 greaterThanEq 0 MAX
print *tmp0
op add :i :i 1
jump 4 lessThan :i MAX
```

A loop condition is processed as well as a loop body, and invariant code in nested loops is hoisted all the way to the top when possible:

```Mindcode
param MAX = 10;
for j in 0 ... MAX do
    i = 0;
    while i < MAX + 10 do
        i = i + 1;
        print(i);
    end;
end;
```

compiles into

```mlog
set MAX 10
set :j 0
op add *tmp0 MAX 10
jump 0 greaterThanEq 0 MAX
set :i 0
jump 9 lessThanEq MAX -10
op add :i :i 1
print :i
jump 6 lessThan :i *tmp0
op add :j :j 1
jump 4 lessThan :j MAX
```

The optimization affects even instructions setting up parameters or return addresses for stackless function calls and array access:

```Mindcode
noinline def foo(x)
    print(x);
end;

for i in 0 ... @links do
    foo("Huzzah!");
end;
```

compiles into

```mlog
set :i 0
set :foo:x "Huzzah!"
set :foo*retaddr 5
jump 0 greaterThanEq 0 @links
jump 6 always 0 0
op add :i :i 1
print :foo:x
set @counter :foo*retaddr
```

Hoisting the instructions setting up return addresses is not possible when [`symbolic-labels`](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) is set to `true`.

When the `select` optimization is available, Loop Hoisting is capable of handling some conditional expressions as well:

```Mindcode
#set target = 8;
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
    op mod *tmp0 MAX 2
    select *tmp2 equal *tmp0 0 "Even" "Odd"
    jump label_9 greaterThanEq 1 MAX
    label_5:
        print :i
        print *tmp2
    op add :i :i 1
    jump label_5 lessThan :i MAX
    label_9:
    print "end"
```

At this moment, the following limitations apply:

* If the loop contains a stackless or recursive function call, global variables that might be modified by that function call are marked as loop dependent and expressions based on them aren't hoisted. The compiler must assume the value of the global variable may change inside these functions.
* `if` expressions are hoisted only when part of simple expressions. Specifically, when the `if` expression is nested in a function call (such as `print(x < 0 ? "positive" : "negative");`), it won't be optimized.

---

[&#xAB; Previous: Jump Threading](JUMP-THREADING.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Loop Optimization &#xBB;](LOOP-OPTIMIZATION.markdown)
