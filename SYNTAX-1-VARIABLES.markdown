# Variables and constants

Mindcode uses Mindustry Logic processor variables for its own variables.
These variables can contain a number (integer or real), an object or a value `null`. An object can be a Mindustry object,
such as a block or a unit, a Mindustry constant such as `@coal`, or a string such as `"Text"`.

Variables aren't declared in Mindcode, they're just used. 
`count = 10` creates a variable named `count` and assigns a value of `10` to it.
Variables that weren't previously written to have a value of `0` in Mindustry Logic.
Once set, processor variables are preserved (even across game saves/loads)
until the processor code is modified or the processor is destroyed.

Mindustry Logic stores numbers as `double`, a 64-bit floating point value.
To perform bitwise operations, such as `&` or `<<`, the value is converted to a 64bit integer (a Java `long`),
the operation is performed and the result is assigned to a `double` again.
As a consequence, for bitwise operations the variables are able to hold only about 51 bits or so.

# Main variables

Main variables are variables used in the main body of the program. The name of the variable in Mindcode
is used as-is in Mindustry code. It might be therefore useful to put assignments to important variables 
at the front of the program, so that it can be easily modified in compiled code without the need of recompilation:

```
COUNT = 10
WARNING = false
ITEM = @coal
print(COUNT, WARNING, ITEM)
```

produces 

```
set COUNT 10
set WARNING false
set ITEM @coal
print COUNT
print WARNING
print ITEM
```

Mindcode optimizations won't remove used main variables or code depending on them. In the following code

```
DEBUG = false
...
if DEBUG
    println("State: ", state)
    println("Item count: ", @unit.totalItems)
end
```

both the `DEBUG` variable and the debugging code is compiled as-is, even if the `DEBUG` variable is not changed anywhere else in the code.
You can therefore assign `true` to the variable and activate the debugging code after pasting it to Mindustry processor.

(Note: unused main variables, ie. variables that are never read, can get removed from the compiled code.
This is why we had to include `print(COUNT, WARNING, ITEM)` in the example above.)

# Local variables

Local variables are function parameters or variables used in a user-defined function. For example, in a function

```
def foo(x)
    y = x ** 2
end
```

both `x` and `y` are local variables, not accessible outside of the function `foo`.

# Global variables

Global variables are common to all user functions and the main program body.
Use names that don't contain any lowercase letters, such as `MAIN` or `_9` (this is not a good name for a variable, btw.),
to create global variables. Variables whose name contains at least one lowercase letter are local.
For example, the following code

```
def foo(x)
    MAIN = x + 10
    local = 10
end
MAIN = 5
local = 5
x = 0
foo(10)
print(MAIN, ", ", local)
printflush(message1)
```

displays `20, 5` on the `message1` block in Mindustry world (from now on, we'll omit the `printflush(message1)` in examples),
since both `x` and `local` in the `foo` function are local variables and therefore distinct from `x` and `local` in the main program body.

Using global variables as function parameters (eg. `def foo(Z) ... end`) is not allowed.

# External memory

Mindcode supports storing variables in external memory (memory cells or memory banks linked to the processor).
These variables are stored independently from the processor and can be used to share values between processors,
or to keep values even when the processor is destroyed or its code recompiled.

On the other hand, only numeric values can be stored in external memory.
It is therefore not possible to use it to store strings, buildings or item types there.
This -- quite restrictive -- limitation is unfortunately imposed by Mindustry Logic itself.

## Arrays

You can use external variables using the array access syntax:

```
for i in 0 .. 63
    cell2[i] = cell1[i]
end
```

This code copies the entire contents of memory cell `cell1` to `cell2`.

It is also possible to reference external memory indirectly:

```
def copy(source, target, size)
    for i in 0 ... size
        target[i] = source[i]
    end
end
copy(bank1, bank2, 512)
```

## External variables

You can also have Mindcode assign identifiers to external variables. 
For that, you need to allocate a heap within a Memory Cell or a Memory Bank.
This allocation tells the Mindcode compiler where to store the external variables.
A heap is simply a region of external memory.
The heap is allocated using the following Mindcode:

```
allocate heap in cell4[50 ... 64]
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more external variables than you have allocated space for, compilation will fail with an
`OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a Memory Bank and
allocate more space to your heap.

Once the heap is allocated, you can use external variables. External variables are identified by the `$` (dollar-sign) prefix:

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
To fix this issue, you can either reset your heap to all zeroes on startup, or destroy and re-create your Memory
Cell/Memory Bank.

## Stack

When using recursive functions, some of the local variables and parameters may have to be stored on a stack.
As Mindustry Logic doesn't provide a built-in stack, external memory is used instead. This places the same limitations
on local variables and parameters of recursive functions as on arrays and external variables (that is, only numeric values are supported).

Stack needs to be allocated similarly to heap:

```
allocate stack in bank1[256...512]
```

When a function is not recursive, it won't store anything on a stack,
even when it is called from or it itself calls a recursive function.
If your code contains a recursive function, it won't compile unless the stack is allocated.
Therefore, if your code compiles without the `allocate stack` statement,
you don't need to worry about your functions not supporting non-numeric variables or parameters.

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

It is possible to allocate stack and heap in the same memory block, and/or in one statement:

```
MEMORY = bank1
allocate stack in MEMORY[0 .. 480], heap in MEMORY[481 ... 512]
```

# Built-in variables

Mindustry Logic processors have several built-in variables.
Their names start with the `@` sign. The most common ones are:
* `@counter`: address of the next instruction to be executed
* `@this`, `@thisx`, `@thisy`: processor executing current code and its coordinates
* `@mapw`, `@maph`: dimensions of the map
* `@time`, `@tick`, `@second`, `@minute`: current game time expressed in various units
* `@links`: number of blocks linked to the processor
* `@ipt`: instructions per tick executed by this processor
* `@unit`: currently bound unit
* `@unitCount`, `@itemCount`, `@liquidCount`, `@blockCount`: counts of elements of given type, can be used with `lookup` function.

Mindcode allows you to read these variables, but it is not possible to assign a value to them.
Some of them are constant during the lifetime of the processor, but others do - or at least may - change (`@time`, `@counter` or `@links`).

`@unit` is a very special variable - it always contains the unit currently controlled by the processor.
The only way to assign a new unit to this variable is to use the `ubind()` function.
All unit control commands are sent to this unit.

It is possible to store the value of `@unit` in another variable, but the unit cannot be controlled through that variable.
Just properties of the unit (eg. `dead`) can be read using another variable, and it can be compared for identity:

```
ubind(@poly)
bound = @unit
... 
if bound != @unit or bound.dead !== 0
    ...
end
```

# Linked blocks

When a block/building (eg. a factory, a turret or a memory cell) is linked to a processor,
an object representing the linked blocks is created.
This object is named using the short name of the linked block and an integer index, 
eg. `cell1`, `switch3` or `smelter16`.
These objects allow the code to directly control or query linked blocks.
Names of linked blocks are reserved and cannot be used by any variable.
If a variable with the same name existed earlier, it is removed from the processor.
Assignments to a variable named after a linked block are silently ignored by the processor.

The compiler doesn't know which blocks will be linked to the processor when compiling the code.
To avoid generating code that wouldn't work when blocks are linked to the processor,
all possible names of linked blocks are specially handled:
* it is not possible to assign values to variables with these names,
* these variables are implicitly global.

The second point means that in the following code

```
def foo(msg)
    print("In function foo")
    printflush(message1)
end
```

the `message1` variable represents a message block linked to the processor, even when used inside a function.

The list of possible block names is quite exhaustive

<details><summary>Show full list of Mindustry block names.</summary>

* `arc`
* `bank`
* `battery`
* `cell`
* `center`
* `centrifuge`
* `compressor`
* `conduit`
* `container`
* `conveyor`
* `crucible`
* `cultivator`
* `cyclone`
* `diode`
* `disassembler`
* `display`
* `distributor`
* `dome`
* `door`
* `drill`
* `driver`
* `duo`
* `extractor`
* `factory`
* `foreshadow`
* `foundation`
* `fuse`
* `gate`
* `generator`
* `hail`
* `incinerator`
* `junction`
* `kiln`
* `lancer`
* `meltdown`
* `melter`
* `mender`
* `message`
* `mine`
* `mixer`
* `node`
* `nucleus`
* `panel`
* `parallax`
* `point`
* `press`
* `processor`
* `projector`
* `pulverizer`
* `reactor`
* `reconstructor`
* `ripple`
* `router`
* `salvo`
* `scatter`
* `scorch`
* `segment`
* `separator`
* `shard`
* `smelter`
* `sorter`
* `spectre`
* `swarmer`
* `switch`
* `tank`
* `tower`
* `tsunami`
* `unloader`
* `vault`
* `wall`
* `wave`
* `weaver`

</details>

Any variable name consisting of one of these prefixes and a positive integer is a reserved for linked blocks.
* These are block names: `point3`, `arc7`, `tank999999`.
* These aren't: `switch` (no numeric index at all), `cell05` (leading zero), `Router15` (upper-case letter),
`wave_1` or `reactor-5` (the index doesn't immediately follow the block name).

# Constants

Mindcode supports declaring and using constants throughout your code. Constants are declared using the `const` keyword:

```
const DEBUG = true
const HIGH_SPEED = 50
const LOW_SPEED = HIGH_SPEED / 2
const RATIO = sqrt(2)
const message = DEBUG ? "Debug" : "Release"
```

Constants are global, even if their names contain lower-case characters.
The value assigned to them must be either a numeric, boolean or text literal,
or an expression whose value can be computed at compile time (basically, you can't use a variable there).
Compile-time evaluation uses the same rules as Mindustry Logic,
ie. `const ERROR = 1 / 0` is a valid constant declaration which creates a constant `ERROR` with a value of `null`.

A name used for a constant cannot be used for a global or local variable.

Unlike variables, constants aren't kept in processor variables, and their values therefore cannot be changed in compiled code.
It also means that in the following code

```
const DEBUG = false
...
if DEBUG
    println("State: ", state)
    println("Item count: ", @unit.totalItems)
end
```

the entire `if DEBUG ... end` statement will be skipped and not included in the compiled code.

---

[« Previous: Mindcode basics](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Expressions »](SYNTAX-2-EXPRESSIONS.markdown)