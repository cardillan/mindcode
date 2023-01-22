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

## Custom Functions

You may declare your own functions using the `def` keyword:

```
allocate stack in cell1

def delay(n, fps)
  _x = 0
  deadline = @tick + n * fps
  while @tick < deadline
    _x += 1
  end
end
```

This function will prevent the caller from proceeding until the right number of frames have passed. Calling your own
functions is done in the same way as any other function:

```
delay(1, 60) // stops execution for 1 second, if your framerate is 60 fps
```

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

As you may have noticed, allocating a stack in a Memory Cell or Memory Bank is required in order to handle custom
functions.

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

You have access to 3 styles of loops: [while loops](#while-loops), [range iteration loops](#range-iteration-loops), and
[C-style loops](#c-style-loops).

## While Loops

Loop until a condition becomes true:

```
while @unit === null
  ubind(@poly)
end
```

## Range Iteration Loops

Loop over a defined collection of values, in an inclusive or exclusive fashion. The `..` range operator indicates an
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

Please note that currently, range iteration loops can only increment the value
by 1, and only support increasing values. A range of `60 .. 50` is invalid, but
a range of `-1 .. 1` is valid.

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

You can use a `break` or `continue` statement inside a loop in the usual sense (`break` exits the loop, `continue` skips the rest of the current iteration):

```
while not within(x, y, 6)
  approach(x, y, 4)
  if @unit.dead == 1
    break
  end
  ...
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

The ternary operator (`?:`) is exactly like an if/else expression, except it is more succint. Use it when you need a
conditional but want to save some space:

```
result = n == 0 ? "ready" : "pending"
```

This is the exact same conditional expression as the first if statement above, except it is written on one line.

## Case/When Expressions

Case/when is another way of writing conditionals. Use case/when when you need to test a value against multiple different
alternatives:

```
next_action = case num_enemies
when 0
  "chill"
when 1
  "alert"
when 2
  "vigilant"
else
  "nuke-the-place"
end
```

## Comparison Operators

Mindustry Logic offers us many comparison operators, namely:

* the classic `<`, `<=`, `>=`, and `>` operators, for comparing numeric values
* `==` and `!=` for equal and not equal, respectively
* `&&`, `and`, `||`, and `or`, to implement complex conditionals: `reactor1.thorium > 0 and (reactor1.cryofluid /
  reactor1.liquidCapacity) < 0.25`
* '!' and `not` for neagting boolean expressions
* `===` for "strict equality". In Mindustry, the value `0` is equivalent to `false` and `null`. By using `===`, you
  force Mindustry Logic to check for the exact value, instead of type-casting the value before checking if the values
  are equal. This is very useful for checks where the distinction between `0` and `null` is important:

```
// Bind new units until a living one is found
while not (@unit.dead === 0)
  ubind(@poly)
end
```

`@unit.dead` can take one of three values:
* `null` if no unit is bound,
* `0` if the unit is not dead,
* `1` if the unit is dead.

Using `===` allows us to distinguish the cases when no unit is bound and when a unit is bound and not dead.

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

Most operators do the expected: `+`, `-`, `*`, `/`, and they respect priority of operation, meaning we multiply and
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
10. `==`: equality,  `!=`: inequality (does not equal),  `===`: strict equality -- use when values can be `null`
11. `&&`, `and`: boolean and (`reactor1.thorium > 0 and reactor1.cryofluid <= 10`)
12. `||`, `or`: boolean or
13. `? :`: ternary operator
14. `=`, `/=`, `**=`, `-=`, `+=`, `*=`: assignments (the compound operators combine arithemtic operation with assignment, 'x += 1' is equivalent to 'x = x + 1')

# Literals

Mindustry supports the classic decimal notation for integers and floats:

* 1
* 3.14159
* -35
* -9381.355

Mindustry also supports hexadecimal representation for integers:

* 0x35
* 0xf1
