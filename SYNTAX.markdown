

# Functions

You may call any Mindustry-provided functions:

* Unit Locate => `ulocate(...)`
* Unit Bind => `ubind(...)`
* Unit Radar => `uradar(...)`
* log, tan, sin, etc. => `log(...)`, `tan(...)`, `sin(...)`
* Unit Control, move => `move(...)`
* Unit Control, approach => `approach(...)`
* Unit Control, shoot => `shoot(...)`
* Control, shoot => `building.shoot(x, y, shoot)`
* Control, color => `building.color(r, g, b)`
* End => `end()`
* and so on

There is a special case for Control with a single parameter. Mindcode accepts the following shortcut:

```
building.enabled = boolean
```
which is equivalent to 
```
building.enabled(boolean)
```

Mindcode currently supports targeting Mindustry versions 6 and 7. Web application always targets version 7 processors,
while command-line vesion of the compiler allows to select version 6 for better backwards compatibility.
The differences are minuscule, though, most of the code generated for version 7 will run on version 6 as well.

All supported functions and their respective Mindustry instruction counterparts can be found in the function reference.
Please note that the reference serves just to document all existing functions and the way they are compiled to Mindustry Logic,
but it does not aim to describe the behavior of the functions/instructions.

* [Function reference for Mindustry Logic 6](FUNCTIONS_V6.markdown)
* [Function reference for Mindustry Logic 7](FUNCTIONS_V7.markdown)

## Custom Functions

You may declare your own functions using the `def` keyword:

```
def update(message, status)
    print("Status: ", status, "\n")
    print("Game time: ", floor(@time) / 1000, " sec")
    printflush(message)
end
```

This function will print the status and flush it to given message block.
Calling your own functions is done in the same way as any other function:

```
update(message1, "The core is on fire!!!")
```

You can pass any variable or value assignable to a variable as an argument to the function.

A function may call other function(s), and nothing prevents recursive functions:

```
allocate stack in bank1[0...512]

def fib(n)
    if n <= 0
        0
    else
        case n
            when 1
                1
            else
                fib(n - 1) + fib(n - 2)
        end
    end
end

fib(1) // 0
fib(2) // 1
fib(3) // 1
fib(4) // 2
fib(5) // 3
fib(6) // 5
fib(7) // 8
fib(8) // 13
// and so on...
```

As you may have noticed, allocating a stack in a Memory Cell or Memory Bank is required in order to handle recursive functions.
When no range is specified for stack allocation, the entire capacity of linked memory block is used.
Additional constraint is that only numbers and numerical values can be passed as arguments or stored in local variables
of recursive functions - this is because function parameters and local variables need to be stored on stack,
and Memory Cell or Memory Bank only support string numerical values in them.

### Function parameters and local variables

Function parameters and variables used in functions are local to the function.
Therefore a variable `n` used in a function is different from a variable `n` used in another function
and also from a variable `n` used in main program body.
Changes to local variables are not visible outside of the function containing the variable.
The only exception are variables with upper-case names, such as `VARIABLE`. These variables are common to all functions
and the main program body, and modifications of these variables are visible everywhere in the program:

```
def foo(x)
    y = x + 10
    Z = 10
end

def bar(x)
    y = x + 20
    Z = 20
end

x = 1
y = 2
Z = 3
foo(x)
bar(x)
print(x, ":", y, ":", Z)
printflush(message1)
```

The output of this program is `1:2:20`.

Do not use upper-case names for parameters -- if you do, the parameters won't be local to their function,
possibly causing unpredictable side-effects when calling functions.

### Return values

Function ends (returns to the caller) when the end of function definition is reached, or when a `return` statement is executed.
In the first case, the return value of a function is given by the last expression evaluated by the function;
in the second case, the return value is equal to the expresion following the `return` keyword, or `null` if the return
statement doesn't specify a return value:

```
def foo(n)
    case n
        when 1      return
        when 2      return "Two"
    end
    n
end

print(foo(1), ":", foo(2), ":", foo(3))
printflush(message1)
```

The output of this program is `null:Two:3`.

### Inline functions

Normal function are compiled once and called from other places in the program.
Inline functions are compiled every time they're called, so there can be several copies of them in the compiled code.
This leads to shorter code per call and faster execution.
To create an inline function, use `inline` keyword:

```
inline def printtext(name, value, min, max)
    if value < min
        print(name, " too low")
    elsif value > max
        print(name, " too high")
    end
end

printtext("Health", health, minHealth, maxHealth)
printtext("Speed", speed, minSpeed, maxSpeed)
```

Declaring a recursive function inline leads to compilation error.

Large inline functions called multiple times can generate lots of instructions and make the compiled code too long.
If this happens, remove the `inline` keyword to generate less code.

The compiler will automatically make a function inline when it is called just once in the entire program.
This is safe, as in this case the program will always be both smaller and faster.
Other non-recursive functions might also be compiled inline, if the instruction limit isnt't reached.

# Sensors

You may sense any property using the following syntax:

```
conveyor1.enabled = vault1.sensor(@thorium) < vault1.sensor(@itemCapacity)
reactor1.enabled = reactor1.sensor(@cryofluid) < reactor1.sensor(@liquidCapacity)
```

This syntax allows us to sense values using an "indirect" reference, where the property we want to query is stored in a
variable:

```
resource = @silicon
conveyor1.enabled = vault1.sensor(resource) < vault1.sensor(@itemCapacity)
```

This will compile down to:

```
set resource @silicon
sensor __tmp0 vault1 resource
sensor __tmp1 vault1 @itemCapacity
op lessThan __tmp2 __tmp0 __tmp1
control enabled conveyor1 __tmp2
```

Mindcode also offers a shortcut syntax for the regular case where the sensor directly references the property it wants:

```
leader = cyclone3

// regular sensor
lx = leader.sensor(@shootX)
ly = leader.sensor(@shootY)
ls = leader.sensor(@shooting)

// shortcut sensor
lx = leader.shootX
ly = leader.shootY
ls = leader.shooting

cyclone1.shoot(lx, ly, ls)
cyclone2.shoot(lx, ly, ls)
```

# Loops

You have access to several styles of loops: [while loops](#while-loops), [do-while loops](#do-while-loops),
[range iteration loops](#range-iteration-loops), [list iteration loops](#list-iteration-loops), and [C-style loops](#c-style-loops).

## While Loops

Loop until a condition becomes true:

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
  print("\n", n)
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

Please note that currently, range iteration loops can only increment the value
by 1, and only support increasing values. If the start value is greater than
end value,  the loop body won't get executed at all.

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


Note: usually, a `break` or `continue` statement will be the last statements in a block of code (typically in an `if` or `case` statement).
It doesn't make sense to put additional statements or expressions after a `break` or `continue`, since that code would never get executed
and will be removed by the optimizer. If you do put additional statements there, the compiler will mistake them for a label:

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

The ternary operator (`?:`) is exactly like an if/else expression, except it is more succinct. Use it when you need a
conditional but want to save some space:

```
result = n == 0 ? "ready" : "pending"
```

This is the exact same conditional expression as the first `if` statement above, except it is written on one line.

## Case/When Expressions

Case/when is another way of writing conditionals. Use case/when when you need to test a value against multiple different alternatives:

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
Using it helps to avoid a bug that can happen when you put a superfluous comma at the end of the when expression list:

```
case block.type
    when @conduit, @pulse-conduit, @plated-conduit,
        block.enabled = intake
    when @overdrive-projector, @overdrive-dome
        block.enabled = boost
end
```

The comma after the `@plated-conduit` is not meant to be there.
Because of that, the compiler treats the next expression as if it was another one of the `when` values.
Rearranging the code might help to understand the meaning of this code snippet:

```
case block.type
    when @conduit, @pulse-conduit, @plated-conduit, block.enabled = intake
        // Do nothing
    when @overdrive-projector, @overdrive-dome
        block.enabled = boost
end
```

The resulting effect is that when the block is some kind of a conduit, nothing happens.
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

* Some expressions after the `when` keyword might or might not get evaluated, depending on the value of the case expression. Do not use expressions with side effects (such as a function call that would modify some global variable). 
* Avoid having several `when` branches matching the same value -- currently the first matching branch gets executed, but the behavior might change in the future.


## Comparison Operators

Mindustry Logic offers us many comparison operators, namely:

* the classic `<`, `<=`, `>=`, and `>` operators, for comparing numeric values
* `==` and `!=` for equal and not equal, respectively
* `&&`, `and`, `||`, and `or`, to implement complex conditionals: `reactor1.thorium > 0 and (reactor1.cryofluid / reactor1.liquidCapacity) < 0.25`
* `!` and `not` for negating boolean expressions
* `===` for "strict equality". When using the non-strict comparison in Mindustry -- `==` or `!=`, the value `0` is equal to `false` and `null`. By using `===`, you  force Mindustry Logic to check for the exact value, instead of type-casting the value before checking if the values are equal. This is very useful for checks where the distinction between `0` and `null` is important, such as:
 
```
// Bind new units until a living one is found
while not (@unit.dead === 0)
    ubind(@poly)
end
```

`@unit.dead` can take one of these values:
* `null` if no unit is bound,
* `0` if the unit is not dead,
* `1` if the unit is dead.

Using `===` allows us to distinguish the `null` and `0` values, ie. the cases where no unit is bound and where a unit is bound and not dead.

While Mindustry Logic doesn't support a strict non-equality operator, Mindcode provides `!==`. It's realized as a negation of `===` behind the scenes.

# Global variables

In order to use global variables, you need to allocate a heap within a Memory Cell or a Memory Bank. This allocation
tells the Mindcode compiler where to store its global variables. A heap is simply a region of memory. The heap is
allocated using the following Mindcode:

```
allocate heap in cell4[50 ... 64]
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more global variables than you have allocated, compilation will fail with an
`OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a Memory Bank and
allocate more space to your heap.

Once your heap is allocated, you can use global variables. Global variables are identified by the `$` (dollar-sign)
prefix:

```
allocate heap in cell4[32 ... 64]

$dx = 1 // this is a global variable assignment
$dy = 1
$ne_x = 90
$ne_y = 90
$target_y = $sw_x = 50
$target_x = $sw_y = 50
```

The above will compile to:

```
cell4[32] = 1
cell4[33] = 1
cell4[34] = 90
cell4[35] = 90
cell4[36] = cell4[37] = 50
cell4[38] = cell4[39] = 50
```

Note that global variables are allocated on a first-come, first-served basis. If you had the following code:

```
allocate heap in cell2[61 .. 63]

$flag = rand(10000)
$targetx = 80
$targety = 80
```

and changed it to:

```
allocate heap in cell2[61 .. 63]

$targetx = 80
$targety = 80
$flag = rand(10000)
```

then all addresses in the heap would be reallocated. `$targetx` would be stored in memory cell 61, rather than `$flag`.
To fix this issue, you can either always reset your heap to all zeroes on startup, or destroy and re-create your Memory
Cell/Memory Bank.

## Heap and stack indirection

When you build your Mindcode scripts, the actual Memory Cell and Memory Bank that you use may be different than the ones
you use when playing the game. To that end, you also have the option of referencing your heap and stack through the use
of a variable, like this:

```
HEAPPTR = cell3
allocate heap in HEAPPTR
$dx = 0
```

This will translate to:

```
set HEAPPTR cell3
write 0 HEAPPTR 0
```

Since the very first instruction of the Logic code will be the global variable assignment, you can easily change the
actual cell or bank your will use, without having to do a global search & replace within the Logic code. This introduces
more avenues for code sharing.

# Unary and Binary operators

Most operators do the expected: `+`, `-`, `*`, `/`, and they respect precedence of operation, meaning we multiply and
divide, then add and substract. Add to this operator `\`, which stands for integer division. For example:

```
3 / 2 // returns 1.5
3 \ 2 // returns 1
```

Otherwise, the full list of operators in the order of precedence is as follows:

1. `!`, `not`: boolean negation,  `~`: binary negation
2. `**`: exponentiation (`2**4` is `2 * 2 * 2 * 2`, or 16)
3. `-`: unary minus (negates the value of the following expression)
4. `*`: multiplication,  `/`: floating point division,  `\`: integer division,  `%`: modulo
5. `+`: addition,  `-`: subtraction
6. `<<`: left-shift,  `>>`: Right-shift (`1 << 2` is easier to read in binary: `0b0001 << 2` becomes `0b0100`)
7. `&`: binary and (useful for flags)
8. `|`: binary or (useful for flags),  `^`: binary [xor (exclusive-or)](https://en.wikipedia.org/wiki/Exclusive_or)
9. `<`: less than,  `<=`: less than or equal,  `>=`: greater than or equal,  `>`:  greater than
10. `==`: equality,  `!=`: inequality (does not equal),  `===`, `!==`: strict equality and non-equality -- use when values can be `null`
11. `&&`, `and`: boolean and (`reactor1.thorium > 0 and reactor1.cryofluid <= 10`)
12. `||`, `or`: boolean or
13. `? :`: ternary operator
14. `=`, `**=`, `*=`, `/=`, `\=`, `%=`, `+=`, `-=`, `<<=`, `>>=`, `&=`, `|=`, `^=`, `&&=`, `||=`: assignments
(the compound operators combine arithmetic operation with assignment, `x += 1` is equivalent to `x = x + 1`)

# Literals

Mindustry supports the classic decimal notation for integers and floats:

* 1
* 3.14159
* -35
* -9381.355

Mindustry also supports hexadecimal representation for integers:

* 0x35
* 0xf1
