# Loop Unrolling

Loop unrolling is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when it is compatible with the optimization goal. Furthermore, loop unrolling depends on the [Data Flow optimization](DATA-FLOW-OPTIMIZATION.markdown) and isn't functional when the Data Flow optimization is not active.

## The fundamentals of loop unrolling

The Loop Unrolling optimization works by replacing loops whose number of iterations can be determined by the compiler with a linear sequence of instructions. This results in a significant speedup of program execution: the jump instruction representing an exit condition, and oftentimes also the instruction(s) updating the loop control variable, can be removed from the unrolled loop, so that only instructions actually performing the intended work of the loop remain. The optimization is most efficient on loops that are very "tight"—contain very few instructions apart from the loop itself. The most dramatic practical example is probably something like this (let's see it first without the loop unrolling):

```Mindcode
set loop-unrolling = none;
for i in 0 ... 10 do
    cell1[i] = 0;
end;
```

This code clears the first ten slots of a memory cell. Without a loop unrolling, the code would look like this:

```mlog
set :i 0
write 0 cell1 :i
op add :i :i 1
jump 1 lessThan :i 10
```

It takes 31 instruction executions to perform this code. With loop unrolling, though, the code is changed to this:

```Mindcode
for i in 0 ... 10 do
    cell1[i] = 0;
end;
```

```mlog
write 0 cell1 0
write 0 cell1 1
write 0 cell1 2
write 0 cell1 3
write 0 cell1 4
write 0 cell1 5
write 0 cell1 6
write 0 cell1 7
write 0 cell1 8
write 0 cell1 9
```

The size of the loop is now 10 instructions instead of 4, but it takes just these 10 instructions to execute, instead of the 31 in the previous case, executing three times as fast!

The price for this speedup is the increased number of instructions themselves. Since there's a hard limit of 1000 instructions in a Mindustry Logic program, loops with a large number of iterations cannot be unrolled. See [Dynamic optimizations](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) for an explanation of how Mindcode decides whether to unroll a loop.

Apart from removing the superfluous instructions, loop unrolling also replaces variables with constant values. This can make further optimization opportunities arise, especially for a Data Flow Optimizer and possibly for others. A not particularly practical, but nonetheless striking example is this program that computes the sum of numbers from 0 to 100:

```Mindcode
sum = 0;
for i in 0 .. 100 do
    sum += i;
end;
print(sum);
```

which compiles to:

```mlog
print 5050
```

What happened here is this: the loop was unrolled to individual instructions in this basic form:

```
set :sum 0
set :i 0
op add :sum :sum :i
op add :i :i 1 
op add :sum :sum :i
op add :i :i 1 
...
```

Data Flow Optimization then evaluated all those expressions using the combination of [constant propagation](DATA-FLOW-OPTIMIZATION.markdown#constant-propagation) and [constant folding](DATA-FLOW-OPTIMIZATION.markdown#constant-folding), all the way to the final sum of `5050`, which is then directly printed.

## Loop unrolling preconditions

A list iteration loop can always be unrolled if there's enough instruction space left.

For other loops, unrolling can generally be performed when Mindcode can determine the loop has a certain fixed number of iterations and can infer other properties of the loop, such as a variable that controls the loop iterations. A loop should be eligible for the unrolling when the following conditions are met:

* The loop is controlled by a single, local, or main variable: the loop condition must consist of a variable which is modified inside a loop, and a constant or an effectively constant variable. Loops based on global variables cannot be unrolled.
* The loop control variable is modified inside the loop only by `op` instructions which have the loop control variable as a first argument and a constant or an effectively constant variable as a second argument; the op instructions must be deterministic. Any other instruction that sets the value of a loop control variable precludes loop unrolling.
* All modifications of the loop control variable happen directly in the loop body: the variable must not be modified in a nested loop or in an if statement, for example.
* The loop has a nonzero number of iterations. The upper limit of the iteration count depends on available instruction space, but generally can never exceed 1000 iterations.

Furthermore:

* If the optimization level is `basic`, the loop control variable can only be modified by `add` and `sub` operations. Every value the loop control variable attains in individual iterations must be an integer (it means that the starting value, ending value, and every change of the iteration variable must be an integer as well). The loop condition must be expressed using one of these operators: `>`, `<`, `>=` or `<=`. In this mode, the total number of iterations is computed using the starting and ending value of the variable and the change in each iteration (resulting in a fast computation).
* If the optimization level is `advanced`, every deterministic update of loop control variable by a constant value and every form of loop condition is allowed. In this case, Mindcode determines the total number of iterations by emulating the entire execution of the loop. The emulation stops when the loop exits or the maximum possible number of iterations allowed by available instruction space is reached (meaning the loop cannot be unrolled).
* `break` and `continue` statements are supported. However, when using a `break` statement, the entire loop is still unrolled. Other optimizers might remove unreachable code after a break statement.

Examples of loops that can be unrolled on `basic` optimization level:

```Mindcode
// Basic case
for i in 0 .. 10 do
    cell1[i] = cell1[i + 1];
end;

// Two separate increments of the loop control variable
j = 0;
while j < 20 do
    println(j);
    j += 1;
    println(j);
    j += 2;
end;

// The loop control variable can be used in further expressions
for k in 0 ... 10 do
    cell1[k] = cell1[2 * k];
end;

for l in 0 ... 10 do
    println(l % 2 ? "Odd" : "Even");
end;

// Loop inside an inline function: can be unrolled if a constant value is passed into the size parameter
inline def copy(src, dst, size)
    for i in 0 ... size do
        dst[i] = src[i];
    end;
end;

// This will produce a loop that can be unrolled
copy(cell1, cell2, 10);

// This produces a loop that CANNOT be unrolled: SIZE is not a constant value
param SIZE = 10;
copy(cell1, cell2, SIZE);

// Some loops containing expressions in the condition can still be unrolled,
// but it strongly depends on the structure of the expression
i = 0;
while (i += 1) < 10 do
    print(i);
end;
```

Examples of loops that can be unrolled on `advanced` optimization level:

```Mindcode
// An operation different from add and sub is supported
for i = 1; i < 100000; i <<= 1 do
    println(i);
end;

// This loop can be unrolled, because it terminates
// (if the condition was j > 0, it would never terminate)
j = 10;
while j > 1 do
    j += 1;
    println(j);
    j \= 2;
    println(j);
end;

// This loop is unrolled, but the number of iterations is 11!
// The code produces the same output as if it wasn't unrolled.
// This is because of rounding errors when evaluating floating-point expressions 
for k = 0; k < 1; k += 0.1 do
    println(k);
end;
```

Examples of loops that **cannot** be unrolled:

```Mindcode
// LIMIT is a program parameter and as such the value assigned to it isn't considered constant
param LIMIT = 10;
for i in 0 ... LIMIT do
    cell1[i] = 0;
end;

// The loop control variable is changed inside an if
i = 0;
while i < 10 do
    i += 1;
    print(i);
    if i % 5 == 0 then
        i += 1;
        print("Five");
    end;
end;

// There isn't a single loop control variable - loop condition depens on both i and j
for i = 0, j = 10; i < j; i += 1, j -= 1 do
    print(i);
end;

// The expression changing the loop control variable is too complex.
// (Rewriting the assignment to i *= 2; i += 1; would allow unrolling) 
i = 0;
while i < 1000 do
  i = 2 * i + 1;
  print(i);
end;

// This loop won't be unrolled. We know it ends after 5 iterations due to the break statement,
// but Mindcode assumes 2000 iterations, always reaching the instruction limit.  
for i in 0 ... 2000 do
    if i > 5 then break; end;
    print(i);
end;
```

## Unrolling nested loops

Nested loops can also be unrolled, and the optimizer prefers unrolling the inner loop:

```Mindcode
k = 0;
for i in 0 ... 100 do
    for j in 0 ... 100 do
        k = k + rand(j); // Prevents collapsing the loop by Data Flow Optimization 
    end;
end;
```

Both loops are eligible for unrolling at the beginning, and the inner one is chosen. After that, the outer loop can no longer be unrolled, because the instruction limit would be hit.

Sometimes unrolling an outer loop can make the inner loop eligible for unrolling too. In this case, the inner loop cannot be unrolled first, as it is not constant:

```Mindcode
set optimization = advanced;
first = true;
for i in 1 .. 5 do
    for j in i .. 5 do
        if first then
            first = false;
        else
            print(" ");
        end;
        print(10 * i + j);
    end;
end;
```

In this example, the outer loop is unrolled first, after which each copy of the inner loop can be unrolled
independently. Further optimizations (including Print Merging) then compact the entire computation into a single
print instruction:

```mlog
print "11 12 13 14 15 22 23 24 25 33 34 35 44 45 55"
```

## List iteration loop with modifications

For [list iteration loops with modifications](../SYNTAX-3-STATEMENTS.markdown#modifications-of-variables-in-the-list), the output loop control variable is completely replaced with the variable assigned to it for the iteration. This helps in some more complicated cases where the Data Flow Optimization alone wasn't able to do the substitution on its own.

---

[&#xAB; Previous: Loop Optimization](LOOP-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Print Merging &#xBB;](PRINT-MERGING.markdown)
