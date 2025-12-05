# Data Flow Optimization

This optimization inspects the actual data flow in the program and removes instructions and variables (both user-defined and temporary) that are dispensable or have no effect on the program execution. Each optimization performed is described separately below.

Data Flow Optimizations can have a profound effect on the resulting code. User-defined variables can get eliminated, and variables in expressions can get replaced by various other variables that were determined to hold the same value. The goal of these replacements is to eliminate some instructions, making the resulting code both smaller and faster. The optimizer doesn't try to avoid variable replacements that do not lead to instruction elimination—this would make the resulting code more understandable, but the optimizer would have to be more complex and therefore more prone to errors.

## Handling of uninitialized variables

The data flow analysis reveals cases where variables might not be properly initialized, i.e., situations where a value of a variable is read before it is known that some value has been written to the variable. Warnings are generated for each uninitialized variable found.

Since Mindustry Logic executes the code repeatedly while preserving variable values, not initializing a variable might be a valid choice, relying on the fact that all variables are assigned a value of `null` by Mindustry at the beginning. If you intentionally leave a variable uninitialized, declare the variable using a `noinit` modifier, which suppresses the warning:

```Mindcode
noinit var count;
count++;
print(count);
printflush(message1);
```

Data Flow Optimization assumes that values assigned to variables detected as uninitialized might be reused on the next program execution. Assignments to uninitialized variables before calling the `end()` function are therefore protected, while assignments to initialized variables aren't—they will be overwritten on the next program execution anyway:

```Mindcode
noinit var initialized;
var foo = rand(10);
if initialized == 0 then
    print("Initializing...");
    // Do some initialization
    initialized = 1;
    foo = 1;
    end();
end;
print("Doing actual work");
print(initialized);
print(foo);
```

produces this code:

```mlog
op rand .foo 10 0
jump 5 notEqual .initialized 0
print "Initializing..."
set .initialized 1
end
print "Doing actual work"
print .initialized
print .foo
```

Notice the `initialized = 1` statement is preserved, while `foo = 1` is not.

This protection is also applied to assignment to uninitialized variables made before calling a user function which, directly or indirectly, calls or may call the `end()` function:

```Mindcode
set unreachable-code-elimination = none;
print(foo);
foo = 5;
bar();
foo = 6;
bar();

def bar()
    end();
end;
```

preserves both assignments to `foo`:

```mlog
print :foo
set :foo 5
end
set :foo 6
```

See also [`end()` function](../SYNTAX-3-STATEMENTS.markdown#the-end-function).

## Unnecessary assignment elimination

All assignments to variables (except global variables) are inspected, and unnecessary assignments are removed. The assignment is unnecessary if the variable is not read after being assigned, or if it is not read before another assignment to the variable is made:

```Mindcode
a = rand(10);
a = rand(20);
print(a);
a = rand(30);
```

compiles to:

```mlog
op rand :a 20 0
print :a
```

The first assignment to `:a` is removed, because `:a` is not read before another value is assigned to it. The last assignment to `:a` is removed, because `:a` is not read after that assignment at all.

An assignment can also become unnecessary due to other optimizations.

## Constant propagation

When a variable is used in an instruction and the value of the variable is known to be a constant value, Mindcode replaces the variable with the constant value. This can in turn make the original assignment unnecessary. See, for example:

```Mindcode
a = 10;
b = 20;
c = @tick + b;
print($"$a, $b, $c.");
```

produces

```mlog
op add :c @tick 20
print "10, 20, "
print :c
print "."
```

## Constant folding

Constant propagation described above ensures that constant values are used instead of variables where possible. When a deterministic operation is performed on constant values (such as addition by the `op add` instruction), constant folding evaluates the expression and replaces the operation with the resulting value, eliminating an instruction.
For example:

```Mindcode
a = 10;
b = 20;
c = a + b;
print($"$a, $b, $c.");
```

produces

```mlog
print "10, 20, 30."
```

Looks quite spectacular, doesn't it? Here's what happened:

* The optimizer figured out that variables `a` and `b` are not needed, because they only hold a constant value.
* Then it found out the `c = a + b` expression has a constant value too.
* What was left was a sequence of print statements, each printing a constant value. [Print Merging optimization](PRINT-MERGING.markdown) then merged them.

Not every opportunity for constant folding is detected at this moment. While `x = 1 + y + 2;` is optimized to `op add :x :y 3`, `x = 1 + y + z + 2;` it too complex to process as this moment and the constant values of `1` and `2` won't be folded at compile time.

If the result of a constant expression doesn't have a valid mlog representation, or if the mlog representation of the result incurs a precision loss, the optimization is not performed.

## Common subexpressions optimization

The Data Flow Optimizer keeps track of expressions that have been evaluated. When the same expression is encountered for a second (third, fourth, ...) time, the result of the last computation is reused instead of evaluating the expression again. In the following code:

```Mindcode
a = rand(10);
b = a + 1;
c = 2 * (a + 1);
d = 3 * (a + 1);
print(a, b, c, d);
```

the optimizer notices that the value `a + 1` was assigned to `b` after it was computed for the first time and reuses it in the following instructions:

```mlog
op rand :a 10 0
op add :b :a 1
op mul :c 2 :b
op mul :d 3 :b
print :a
print :b
print :c
print :d
```

Again, not every possible opportunity is used. Instructions are not rearranged, for example, even if doing so allows more evaluations to be reused.

On the other hand, entire complex expressions are reused if they're identical. In the following code

```Mindcode
a = rand(10);
b = rand(10);
x = 1 + sqrt(a * a + b * b);
y = 2 + sqrt(a * a + b * b);
print(x, y);
```

the entire square root is evaluated only once:

```mlog
op rand :a 10 0
op rand :b 10 0
op mul *tmp2 :a :a
op mul *tmp3 :b :b
op add *tmp4 *tmp2 *tmp3
op sqrt *tmp5 *tmp4 0
op add :x 1 *tmp5
op add :y 2 *tmp5
print :x
print :y
```

## Backpropagation

Backpropagation is a separate optimization that allows modifying instructions prior to the one being inspected: when a set instruction assigning a source variable to a target variable is encountered, the original instruction producing ("defining") the source variable is found. If the target variable's value is not needed between the assignments, and the source variable is not needed elsewhere, the set is dropped and the source variable is replaced with the target variable in the defining instruction.

This example demonstrates the effects of the backpropagation optimization:

```Mindcode
set optimization = experimental;
i = rand(10);
a = i;
i = rand(20);
b = i;
print(a, b);
```

which compiles to:

```mlog
op rand :a 10 0
op rand :i 20 0
print :a
print :i
```

Of course, the source code typically doesn't contain such constructs, but this essentially is what some other optimizations, such as inlining function calls or unrolling a list iteration loop with modification, may produce.

The backpropagation optimization is available on the `experimental` level.

## Function call optimizations

Variables and expressions passed as arguments to inline functions, as well as return values of inline functions, are processed in the same way as other local variables. Using an inlined function, therefore, doesn't incur any overhead at all in Mindcode.

The data flow analysis, with some restrictions, is also applied to stackless and recursive function calls. Assignments to global variables inside stackless and recursive functions are tracked and properly handled. Optimizations are applied to function arguments and return values.

## Support for other optimizations

Data Flow Optimization gathers information about variables and the values they get assigned by the program. Apart from using this information for its own optimizations, it also provides it to the other optimizers. The other optimizers can then improve their own optimizations further (e.g., to determine that a conditional jump is always true or always false and act accordingly). Data Flow Optimization is therefore crucial for the best performance of some other optimizers as well. Some optimizations, such as [Loop Unrolling](LOOP-UNROLLING.markdown), might outright require the Data Flow Optimization to be active for their own work.

---

[&#xAB; Previous: Condition Optimization](CONDITION-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Dead Code Elimination &#xBB;](DEAD-CODE-ELIMINATION.markdown)
