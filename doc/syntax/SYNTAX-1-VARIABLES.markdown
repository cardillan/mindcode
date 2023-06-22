# Variables and constants

Mindcode uses Mindustry Logic processor variables for its own variables. These variables can contain a number 
(integer or real), an object or a value `null`. An object can be a Mindustry object, such as a block or a unit, a 
Mindustry constant such as `@coal`, or a string such as `"Text"`.

Variables aren't declared in Mindcode, they're created with first use. `count = 10` creates a variable named 
`count` and assigns a value of `10` to it. Variables that weren't previously written to have a value of `0` in 
Mindustry Logic. Once set, processor variables are preserved (even across game saves/loads) until the processor code 
is modified or the processor is destroyed.

> **Note**: Mindustry Logic stores numbers as `double`, a 64-bit floating point value. To perform bitwise operations,
> such as `&` or `<<`, the value is converted to a 64-bit integer (a Java `long`), the operation is performed and the 
> result is assigned to a `double` again. As a consequence, for bitwise operations the variables are able to hold 
> only about 52 bits or so.

# Main variables

Main variables are variables used in the main body of the program. The name of the variable in Mindcode
is used as-is in Mindustry code. It might be therefore useful to put assignments to important variables 
at the front of the program, so that it can be easily modified in compiled code without the need of recompilation:

```
count = 10
warning = false
item = @coal
print(count, warning, item)
```

produces 

```
set count 10
set warning false
set item @coal
print count
print warning
print item
```

When you compile a program using Mindcode, it is obviously possible to manually modify the compiled code. Mindcode 
anticipates this and to better support this option, keeps variable names identical in the compiled code as in 
the source code as much as possible.

Mindcode is also aware of the actual values you assign to variables. In some cases, knowing the actual value allows 
Mindcode to perform specific code optimizations that are only valid for the value you've assigned to the variable in 
the source code. This, however, can interfere with the possibility to modify the compiled code directly.

The [Data Flow optimization](SYNTAX-5-OTHER.markdown#data-flow-optimization) performs the optimizations described 
above. When its optimization level is set to `basic`, the optimizer leaves main variables alone and produces code 
which fully supports making changes to them. When the optimization level is set to `aggressive`, assignments of 
constants to main variables may be completely removed from code and parts of the code specific to other values of 
the variable can get completely removed:

```
debug = false

if debug
    println("State: ", state)
    println("Item count: ", @unit.totalItems)
end
```

When compiled with Data Flow optimization level set to `agressive`, all of the above code will be eliminated, as the 
debugging information isn't printed out.

[Global variables](#global-variables) are never optimized in his fashion regardless of optimization level and are 
safe to use for parametrization of the compiled code. 

# Local variables

Local variables are function parameters or variables used in a user-defined function. For example, in a function

```
def foo(x)
    y = x ** 2
end
```

both `x` and `y` are local variables, not accessible outside the function `foo`.

In compiled code, names of these variables are appended to a unique function prefix. Recognizing names of local 
variables in compiled code might be a bit cumbersome.  

# Global variables

Global variables are common to all user functions and the main program body. Use names that don't contain any
lowercase letters, such as `MAIN` or `_9` (this is actually not a particularly good name for a variable), to create 
global variables. Variables whose name contains at least one lowercase letter are local. For example, the following code

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

displays `20, 5` on the `message1` block in Mindustry world (from now on, we'll omit the `printflush(message1)` in 
examples), since both `x` and `local` in the `foo` function are local variables and therefore distinct from `x` and 
`local` in the main program body.

Using global variables as function parameters (e.g. `def foo(Z) ... end`) is not allowed.

Unlike main variables, global variables are never optimized away from the code. This also means that their use makes 
the code a bit less friendly towards optimization, and you should limit their use to situations where they're really 
needed (i.e. for variables accessed from different functions, or for compile-in parameters).  

# External memory

Mindcode supports storing variables in external memory - memory cells or memory banks linked to the processor. These 
variables are stored independently of the processor and can be used to share values between processors, or to keep 
values even when the processor is destroyed or its code altered.

On the other hand, only numeric values can be stored in external memory. It is therefore not possible to use it to 
store strings, buildings or item types there. This -- quite restrictive -- limitation is unfortunately imposed by 
Mindustry Logic itself.

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

You can also have Mindcode assign identifiers to external variables. For that, you need to allocate a heap within a 
Memory Cell or a Memory Bank. This allocation tells the Mindcode compiler where to store the external variables. A 
heap is simply a region of external memory. The heap is allocated using the following Mindcode:

```
allocate heap in cell4[50 ... 64]
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more external variables than you have allocated space for, compilation will fail 
with an `OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a 
Memory Bank and allocate more space to your heap.

Once the heap is allocated, you can use external variables. External variables are identified by the `$` 
(dollar-sign) prefix: 

```
allocate heap in cell4[32 ... 64]

$dx = 1 // this is an external variable assignment
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

**Note**: external variables are allocated on a first-come, first-served basis. If you had the following code:

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
To fix this issue, you can either reset your heap to all zeroes on startup, or destroy and re-create your memory
cell/memory bank.

## Stack

When using recursive functions, some of their local variables and parameters may have to be stored on a stack.
As Mindustry Logic doesn't provide a built-in stack, external memory is used instead. This places the same limitations
on local variables and parameters of recursive functions as on arrays and external variables (that is, only numeric 
values are supported).

Stack needs to be allocated similarly to heap:

```
allocate stack in bank1[256...512]
```

When a function is not recursive, it won't store anything on a stack, even when it is called from or it itself calls 
a recursive function. If your code contains a recursive function, it won't compile unless the stack is allocated. 
Therefore, if your code compiles without the `allocate stack` statement, you don't need to worry about your 
functions not supporting non-numeric variables or parameters.

## Heap and stack indirection

When you build your Mindcode scripts, the actual Memory Cell and Memory Bank that you use may be different from the 
ones you use when playing the game. To that end, you also have the option of referencing your heap and stack through 
the use of a variable, like this:

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

Since the very first instruction of the compiled code will be the global variable assignment, you can easily change the
actual cell or bank your will use, without having to do a global search & replace within the compiled code. This 
introduces more avenues for code sharing.

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
* `@unitCount`, `@itemCount`, `@liquidCount`, `@blockCount`: counts of elements of given type, can be used with 
  `lookup` function.

Mindcode allows you to read these variables, but it is not possible to assign a value to them.
Some of them are constant during the lifetime of the processor, but others do - or at least may - change (`@time`, 
`@counter` or `@links`).

`@unit` is a very special variable - it always contains the unit currently controlled by the processor. The only way
to assign a new unit to this variable is to use the `ubind()` function. All unit control commands are sent to this
unit. See also [Using units](/doc/syntax/MINDUSTRY-TIPS-N-TRICKS.markdown#using-units).

My experience shows that the value of time variables (`@tick`, `@time` and so on) can actually decrease when loading a 
game from a save file. Take it into account especially when programming loops that should terminate at some 
predetermined time.   

# Linked blocks

When a block/building (e.g. a factory, a turret or a memory cell) is linked to a processor, an object representing
the linked blocks is created. This object is named using the short name of the linked block and an integer index,
e.g. `cell1`, `switch3` or `smelter16`. These objects allow the code to directly control or query linked blocks.
Names of linked blocks are reserved and cannot be used by any variable. If a variable with the same name existed
earlier, it is removed from the processor. Assignments to a variable named after a linked block are silently ignored
by the processor.

The compiler doesn't know which blocks will be linked to the processor when compiling the code. To avoid generating 
code that might stop working when blocks are linked to the processor, all possible names of linked blocks are 
specially handled: 

* it is not possible to assign values to variables with these names,
* these variables are implicitly global.

The second point means that in the following code

```
def foo()
    print("In function foo")
    printflush(message1)
end
```

the `message1` variable represents a message block linked to the processor, even when used inside a function.

The list of possible block names is quite exhaustive.

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

Constants are global, even if their names contain lower-case characters. The value assigned to them must be either a 
numeric, boolean or text literal, or an expression whose value can be computed at compile time. Compile-time 
evaluation uses the same rules as Mindustry Logic, i.e. `const ERROR = 1 / 0` is a valid constant declaration which 
creates a constant `ERROR` with a value of `null`.

If a numeric value is assigned to a constant, and it isn't possible to
[encode the value into an mlog literal](SYNTAX.markdown#numeric-literals-in-mindustry-logic), a compilation error 
occurs.  

A name used for a constant cannot be used for a main, global or local variable.

Unlike variables, constants aren't kept in processor variables, and their values therefore cannot be changed in 
compiled code. It also means that in the following code

```
const DEBUG = false
...
if DEBUG
    println("State: ", state)
    println("Item count: ", @unit.totalItems)
end
```

the entire `if DEBUG ... end` statement will be skipped and not included in the compiled code.

## Built-in icons

Mindustry has a set of built-in icons that are represented by specific Unicode characters and are properly rendered 
in all the user interface elements. While it would be possible to enter a corresponding Unicode character into a 
String literal directly, it would be probably a bit cumbersome. To provide access to these icons, Mindcode comes 
with a predefined set of constants that correspond to individual icons. The constants map the symbolic icon names 
onto a String literal containing their corresponding Unicode character.

Printing an icon is as easy as this:

```
print(ITEM-LEAD, " ", vault1.lead, "\n")
print(ITEM-COAL, " ", vault1.coal, "\n")
print(ITEM-BLAST-COMPOUND, " ", vault1.blast-compound)
printflush(message1)
```

As has already been mentioned, names of the icons are constants and cannot therefore be used for user-defined 
constants or variables. 

The list of all existing icons is quite huge:

<details><summary>Show full list of Mindustry block icons.</summary>

* `BLOCK-ADDITIVE-RECONSTRUCTOR`
* `BLOCK-AFFLICT`
* `BLOCK-AIR-FACTORY`
* `BLOCK-ALPHA-MECH-PAD`
* `BLOCK-ARC`
* `BLOCK-ARKYCITE-FLOOR`
* `BLOCK-ARKYIC-BOULDER`
* `BLOCK-ARKYIC-STONE`
* `BLOCK-ARKYIC-VENT`
* `BLOCK-ARKYIC-WALL`
* `BLOCK-ARMORED-CONVEYOR`
* `BLOCK-ARMORED-DUCT`
* `BLOCK-ATMOSPHERIC-CONCENTRATOR`
* `BLOCK-BALLISTIC-SILO`
* `BLOCK-BARRIER-PROJECTOR`
* `BLOCK-BASALT`
* `BLOCK-BASALT-BOULDER`
* `BLOCK-BASIC-ASSEMBLER-MODULE`
* `BLOCK-BASIC-RECONSTRUCTOR`
* `BLOCK-BATTERY`
* `BLOCK-BATTERY-LARGE`
* `BLOCK-BEAM-DRILL`
* `BLOCK-BEAM-LINK`
* `BLOCK-BEAM-NODE`
* `BLOCK-BEAM-TOWER`
* `BLOCK-BERYLLIC-BOULDER`
* `BLOCK-BERYLLIC-STONE`
* `BLOCK-BERYLLIC-STONE-WALL`
* `BLOCK-BERYLLIUM-WALL`
* `BLOCK-BERYLLIUM-WALL-LARGE`
* `BLOCK-BLAST-DOOR`
* `BLOCK-BLAST-DRILL`
* `BLOCK-BLAST-MIXER`
* `BLOCK-BLOCK-FORGE`
* `BLOCK-BLOCK-LAUNCHER`
* `BLOCK-BLOCK-LOADER`
* `BLOCK-BLOCK-UNLOADER`
* `BLOCK-BLUEMAT`
* `BLOCK-BOULDER`
* `BLOCK-BREACH`
* `BLOCK-BRIDGE-CONDUIT`
* `BLOCK-BRIDGE-CONVEYOR`
* `BLOCK-BUILD-TOWER`
* `BLOCK-CANVAS`
* `BLOCK-CARBIDE-CRUCIBLE`
* `BLOCK-CARBIDE-WALL`
* `BLOCK-CARBIDE-WALL-LARGE`
* `BLOCK-CARBON-BOULDER`
* `BLOCK-CARBON-STONE`
* `BLOCK-CARBON-VENT`
* `BLOCK-CARBON-WALL`
* `BLOCK-CELL-SYNTHESIS-CHAMBER`
* `BLOCK-CHAR`
* `BLOCK-CHEMICAL-COMBUSTION-CHAMBER`
* `BLOCK-CLIFF`
* `BLOCK-CLIFF-CRUSHER`
* `BLOCK-CLIFFS`
* `BLOCK-COAL-CENTRIFUGE`
* `BLOCK-COMBUSTION-GENERATOR`
* `BLOCK-COMMAND-CENTER`
* `BLOCK-CONDUIT`
* `BLOCK-CONSTRUCTOR`
* `BLOCK-CONTAINER`
* `BLOCK-CONVEYOR`
* `BLOCK-COPPER-WALL`
* `BLOCK-COPPER-WALL-LARGE`
* `BLOCK-CORE-ACROPOLIS`
* `BLOCK-CORE-BASTION`
* `BLOCK-CORE-CITADEL`
* `BLOCK-CORE-FOUNDATION`
* `BLOCK-CORE-NUCLEUS`
* `BLOCK-CORE-SHARD`
* `BLOCK-CORE-SILO`
* `BLOCK-CORE-ZONE`
* `BLOCK-CRATER-STONE`
* `BLOCK-CRATERS`
* `BLOCK-CRAWLER-FACTORY`
* `BLOCK-CRYOFLUID-MIXER`
* `BLOCK-CRYSTAL-BLOCKS`
* `BLOCK-CRYSTAL-CLUSTER`
* `BLOCK-CRYSTAL-FLOOR`
* `BLOCK-CRYSTAL-ORBS`
* `BLOCK-CRYSTALLINE-BOULDER`
* `BLOCK-CRYSTALLINE-STONE`
* `BLOCK-CRYSTALLINE-STONE-WALL`
* `BLOCK-CRYSTALLINE-VENT`
* `BLOCK-CULTIVATOR`
* `BLOCK-CYANOGEN-SYNTHESIZER`
* `BLOCK-CYCLONE`
* `BLOCK-DACITE`
* `BLOCK-DACITE-BOULDER`
* `BLOCK-DACITE-WALL`
* `BLOCK-DAGGER-FACTORY`
* `BLOCK-DARK-METAL`
* `BLOCK-DARK-PANEL-1`
* `BLOCK-DARK-PANEL-2`
* `BLOCK-DARK-PANEL-3`
* `BLOCK-DARK-PANEL-4`
* `BLOCK-DARK-PANEL-5`
* `BLOCK-DARK-PANEL-6`
* `BLOCK-DARKSAND`
* `BLOCK-DARKSAND-TAINTED-WATER`
* `BLOCK-DARKSAND-WATER`
* `BLOCK-DART-MECH-PAD`
* `BLOCK-DART-SHIP-PAD`
* `BLOCK-DATA-PROCESSOR`
* `BLOCK-DECONSTRUCTOR`
* `BLOCK-DEEP-TAINTED-WATER`
* `BLOCK-DEEP-WATER`
* `BLOCK-DEEPWATER`
* `BLOCK-DELTA-MECH-PAD`
* `BLOCK-DENSE-RED-STONE`
* `BLOCK-DIFFERENTIAL-GENERATOR`
* `BLOCK-DIFFUSE`
* `BLOCK-DIODE`
* `BLOCK-DIRT`
* `BLOCK-DIRT-WALL`
* `BLOCK-DISASSEMBLER`
* `BLOCK-DISPERSE`
* `BLOCK-DISTRIBUTOR`
* `BLOCK-DOOR`
* `BLOCK-DOOR-LARGE`
* `BLOCK-DRAUG-FACTORY`
* `BLOCK-DUCT`
* `BLOCK-DUCT-BRIDGE`
* `BLOCK-DUCT-ROUTER`
* `BLOCK-DUCT-UNLOADER`
* `BLOCK-DUNE-WALL`
* `BLOCK-DUO`
* `BLOCK-ELECTRIC-HEATER`
* `BLOCK-ELECTROLYZER`
* `BLOCK-EMPTY`
* `BLOCK-ERUPTION-DRILL`
* `BLOCK-EXPONENTIAL-RECONSTRUCTOR`
* `BLOCK-FABRICATOR`
* `BLOCK-FERRIC-BOULDER`
* `BLOCK-FERRIC-CRATERS`
* `BLOCK-FERRIC-STONE`
* `BLOCK-FERRIC-STONE-WALL`
* `BLOCK-FLUX-REACTOR`
* `BLOCK-FORCE-PROJECTOR`
* `BLOCK-FORESHADOW`
* `BLOCK-FORTRESS-FACTORY`
* `BLOCK-FRACTURE`
* `BLOCK-FUSE`
* `BLOCK-GHOUL-FACTORY`
* `BLOCK-GLAIVE-SHIP-PAD`
* `BLOCK-GRAPHITE-PRESS`
* `BLOCK-GRAPHITIC-WALL`
* `BLOCK-GRASS`
* `BLOCK-GROUND-FACTORY`
* `BLOCK-HAIL`
* `BLOCK-HEAT-REACTOR`
* `BLOCK-HEAT-REDIRECTOR`
* `BLOCK-HEAT-ROUTER`
* `BLOCK-HEAT-SOURCE`
* `BLOCK-HOTROCK`
* `BLOCK-HYPER-PROCESSOR`
* `BLOCK-ICE`
* `BLOCK-ICE-SNOW`
* `BLOCK-ICE-WALL`
* `BLOCK-IGNAROCK`
* `BLOCK-ILLUMINATOR`
* `BLOCK-IMPACT-DRILL`
* `BLOCK-IMPACT-REACTOR`
* `BLOCK-IMPULSE-PUMP`
* `BLOCK-INCINERATOR`
* `BLOCK-INTERPLANETARY-ACCELERATOR`
* `BLOCK-INVERTED-SORTER`
* `BLOCK-ITEM-SOURCE`
* `BLOCK-ITEM-VOID`
* `BLOCK-JAVELIN-SHIP-PAD`
* `BLOCK-JUNCTION`
* `BLOCK-KILN`
* `BLOCK-LANCER`
* `BLOCK-LARGE-CONSTRUCTOR`
* `BLOCK-LARGE-LOGIC-DISPLAY`
* `BLOCK-LARGE-OVERDRIVE-PROJECTOR`
* `BLOCK-LARGE-PAYLOAD-MASS-DRIVER`
* `BLOCK-LARGE-PLASMA-BORE`
* `BLOCK-LARGE-SHIELD-PROJECTOR`
* `BLOCK-LASER-DRILL`
* `BLOCK-LAUNCH-PAD`
* `BLOCK-LAUNCH-PAD-LARGE`
* `BLOCK-LEGACY-COMMAND-CENTER`
* `BLOCK-LEGACY-MECH-PAD`
* `BLOCK-LEGACY-UNIT-FACTORY`
* `BLOCK-LEGACY-UNIT-FACTORY-AIR`
* `BLOCK-LEGACY-UNIT-FACTORY-GROUND`
* `BLOCK-LIQUID-CONTAINER`
* `BLOCK-LIQUID-JUNCTION`
* `BLOCK-LIQUID-ROUTER`
* `BLOCK-LIQUID-SOURCE`
* `BLOCK-LIQUID-TANK`
* `BLOCK-LIQUID-VOID`
* `BLOCK-LOGIC-DISPLAY`
* `BLOCK-LOGIC-PROCESSOR`
* `BLOCK-LUSTRE`
* `BLOCK-MAGMAROCK`
* `BLOCK-MALIGN`
* `BLOCK-MASS-CONVEYOR`
* `BLOCK-MASS-DRIVER`
* `BLOCK-MECH-ASSEMBLER`
* `BLOCK-MECH-FABRICATOR`
* `BLOCK-MECH-RECONSTRUCTOR`
* `BLOCK-MECH-REFABRICATOR`
* `BLOCK-MECHANICAL-DRILL`
* `BLOCK-MECHANICAL-PUMP`
* `BLOCK-MELTDOWN`
* `BLOCK-MELTER`
* `BLOCK-MEMORY-BANK`
* `BLOCK-MEMORY-CELL`
* `BLOCK-MEND-PROJECTOR`
* `BLOCK-MENDER`
* `BLOCK-MESSAGE`
* `BLOCK-METAL-FLOOR`
* `BLOCK-METAL-FLOOR-2`
* `BLOCK-METAL-FLOOR-3`
* `BLOCK-METAL-FLOOR-4`
* `BLOCK-METAL-FLOOR-5`
* `BLOCK-METAL-FLOOR-DAMAGED`
* `BLOCK-MICRO-PROCESSOR`
* `BLOCK-MOLTEN-SLAG`
* `BLOCK-MOSS`
* `BLOCK-MUD`
* `BLOCK-MULTI-PRESS`
* `BLOCK-MULTIPLICATIVE-RECONSTRUCTOR`
* `BLOCK-NAVAL-FACTORY`
* `BLOCK-NEOPLASIA-REACTOR`
* `BLOCK-NUCLEAR-WARHEAD`
* `BLOCK-OIL-EXTRACTOR`
* `BLOCK-OMEGA-MECH-PAD`
* `BLOCK-ORE-BERYLLIUM`
* `BLOCK-ORE-COAL`
* `BLOCK-ORE-COPPER`
* `BLOCK-ORE-CRYSTAL-THORIUM`
* `BLOCK-ORE-LEAD`
* `BLOCK-ORE-SCRAP`
* `BLOCK-ORE-THORIUM`
* `BLOCK-ORE-TITANIUM`
* `BLOCK-ORE-TUNGSTEN`
* `BLOCK-ORE-WALL-BERYLLIUM`
* `BLOCK-ORE-WALL-THORIUM`
* `BLOCK-ORE-WALL-TUNGSTEN`
* `BLOCK-OVERDRIVE-DOME`
* `BLOCK-OVERDRIVE-PROJECTOR`
* `BLOCK-OVERFLOW-DUCT`
* `BLOCK-OVERFLOW-GATE`
* `BLOCK-OXIDATION-CHAMBER`
* `BLOCK-OXIDIZER`
* `BLOCK-PARALLAX`
* `BLOCK-PAYLOAD-CONVEYOR`
* `BLOCK-PAYLOAD-INCINERATOR`
* `BLOCK-PAYLOAD-LOADER`
* `BLOCK-PAYLOAD-MASS-DRIVER`
* `BLOCK-PAYLOAD-ROUTER`
* `BLOCK-PAYLOAD-SOURCE`
* `BLOCK-PAYLOAD-UNLOADER`
* `BLOCK-PAYLOAD-VOID`
* `BLOCK-PEBBLES`
* `BLOCK-PHANTOM-FACTORY`
* `BLOCK-PHASE-CONDUIT`
* `BLOCK-PHASE-CONVEYOR`
* `BLOCK-PHASE-HEATER`
* `BLOCK-PHASE-SYNTHESIZER`
* `BLOCK-PHASE-WALL`
* `BLOCK-PHASE-WALL-LARGE`
* `BLOCK-PHASE-WEAVER`
* `BLOCK-PINE`
* `BLOCK-PLASMA-BORE`
* `BLOCK-PLASTANIUM-COMPRESSOR`
* `BLOCK-PLASTANIUM-CONVEYOR`
* `BLOCK-PLASTANIUM-WALL`
* `BLOCK-PLASTANIUM-WALL-LARGE`
* `BLOCK-PLATED-CONDUIT`
* `BLOCK-PNEUMATIC-DRILL`
* `BLOCK-POOLED-CRYOFLUID`
* `BLOCK-POWER-NODE`
* `BLOCK-POWER-NODE-LARGE`
* `BLOCK-POWER-SOURCE`
* `BLOCK-POWER-VOID`
* `BLOCK-PRESSURE-TURBINE`
* `BLOCK-PRIME-REFABRICATOR`
* `BLOCK-PULSE-CONDUIT`
* `BLOCK-PULVERIZER`
* `BLOCK-PUR-BUSH`
* `BLOCK-PYRATITE-MIXER`
* `BLOCK-PYROLYSIS-GENERATOR`
* `BLOCK-RADAR`
* `BLOCK-RAVAGE`
* `BLOCK-RED-DIAMOND-WALL`
* `BLOCK-RED-ICE`
* `BLOCK-RED-ICE-BOULDER`
* `BLOCK-RED-ICE-WALL`
* `BLOCK-RED-STONE`
* `BLOCK-RED-STONE-BOULDER`
* `BLOCK-RED-STONE-VENT`
* `BLOCK-RED-STONE-WALL`
* `BLOCK-REDMAT`
* `BLOCK-REDWEED`
* `BLOCK-REFABRICATOR`
* `BLOCK-REGEN-PROJECTOR`
* `BLOCK-REGOLITH`
* `BLOCK-REGOLITH-WALL`
* `BLOCK-REINFORCED-BRIDGE-CONDUIT`
* `BLOCK-REINFORCED-CONDUIT`
* `BLOCK-REINFORCED-CONTAINER`
* `BLOCK-REINFORCED-LIQUID-CONTAINER`
* `BLOCK-REINFORCED-LIQUID-JUNCTION`
* `BLOCK-REINFORCED-LIQUID-ROUTER`
* `BLOCK-REINFORCED-LIQUID-TANK`
* `BLOCK-REINFORCED-MESSAGE`
* `BLOCK-REINFORCED-PAYLOAD-CONVEYOR`
* `BLOCK-REINFORCED-PAYLOAD-ROUTER`
* `BLOCK-REINFORCED-PUMP`
* `BLOCK-REINFORCED-SURGE-WALL`
* `BLOCK-REINFORCED-SURGE-WALL-LARGE`
* `BLOCK-REINFORCED-VAULT`
* `BLOCK-REPAIR-POINT`
* `BLOCK-REPAIR-TURRET`
* `BLOCK-RESUPPLY-POINT`
* `BLOCK-REVENANT-FACTORY`
* `BLOCK-RHYOLITE`
* `BLOCK-RHYOLITE-BOULDER`
* `BLOCK-RHYOLITE-CRATER`
* `BLOCK-RHYOLITE-VENT`
* `BLOCK-RHYOLITE-WALL`
* `BLOCK-RIPPLE`
* `BLOCK-ROCK`
* `BLOCK-ROTARY-PUMP`
* `BLOCK-ROUGH-RHYOLITE`
* `BLOCK-ROUTER`
* `BLOCK-RTG-GENERATOR`
* `BLOCK-SALT`
* `BLOCK-SALT-WALL`
* `BLOCK-SALVO`
* `BLOCK-SAND`
* `BLOCK-SAND-BOULDER`
* `BLOCK-SAND-FLOOR`
* `BLOCK-SAND-WALL`
* `BLOCK-SAND-WATER`
* `BLOCK-SCATHE`
* `BLOCK-SCATTER`
* `BLOCK-SCORCH`
* `BLOCK-SCRAP-WALL`
* `BLOCK-SCRAP-WALL-GIGANTIC`
* `BLOCK-SCRAP-WALL-HUGE`
* `BLOCK-SCRAP-WALL-LARGE`
* `BLOCK-SEGMENT`
* `BLOCK-SEPARATOR`
* `BLOCK-SHALE`
* `BLOCK-SHALE-BOULDER`
* `BLOCK-SHALE-WALL`
* `BLOCK-SHALLOW-WATER`
* `BLOCK-SHIELD-PROJECTOR`
* `BLOCK-SHIELDED-WALL`
* `BLOCK-SHIP-ASSEMBLER`
* `BLOCK-SHIP-FABRICATOR`
* `BLOCK-SHIP-RECONSTRUCTOR`
* `BLOCK-SHIP-REFABRICATOR`
* `BLOCK-SHOCK-MINE`
* `BLOCK-SHOCKWAVE-TOWER`
* `BLOCK-SHRUBS`
* `BLOCK-SILICON-ARC-FURNACE`
* `BLOCK-SILICON-CRUCIBLE`
* `BLOCK-SILICON-SMELTER`
* `BLOCK-SLAG-CENTRIFUGE`
* `BLOCK-SLAG-HEATER`
* `BLOCK-SLAG-INCINERATOR`
* `BLOCK-SMALL-DECONSTRUCTOR`
* `BLOCK-SMITE`
* `BLOCK-SNOW`
* `BLOCK-SNOW-BOULDER`
* `BLOCK-SNOW-PINE`
* `BLOCK-SNOW-WALL`
* `BLOCK-SNOWROCK`
* `BLOCK-SOLAR-PANEL`
* `BLOCK-SOLAR-PANEL-LARGE`
* `BLOCK-SORTER`
* `BLOCK-SPACE`
* `BLOCK-SPAWN`
* `BLOCK-SPECTRE`
* `BLOCK-SPIRIT-FACTORY`
* `BLOCK-SPORE-CLUSTER`
* `BLOCK-SPORE-MOSS`
* `BLOCK-SPORE-PINE`
* `BLOCK-SPORE-PRESS`
* `BLOCK-SPORE-WALL`
* `BLOCK-STEAM-GENERATOR`
* `BLOCK-STEAM-VENT`
* `BLOCK-STONE`
* `BLOCK-STONE-WALL`
* `BLOCK-SUBLIMATE`
* `BLOCK-SURGE-CONVEYOR`
* `BLOCK-SURGE-CRUCIBLE`
* `BLOCK-SURGE-DUCT`
* `BLOCK-SURGE-ROUTER`
* `BLOCK-SURGE-SMELTER`
* `BLOCK-SURGE-TOWER`
* `BLOCK-SURGE-WALL`
* `BLOCK-SURGE-WALL-LARGE`
* `BLOCK-SWARMER`
* `BLOCK-SWITCH`
* `BLOCK-TAINTED-WATER`
* `BLOCK-TANK-ASSEMBLER`
* `BLOCK-TANK-FABRICATOR`
* `BLOCK-TANK-RECONSTRUCTOR`
* `BLOCK-TANK-REFABRICATOR`
* `BLOCK-TAR`
* `BLOCK-TAU-MECH-PAD`
* `BLOCK-TENDRILS`
* `BLOCK-TETRATIVE-RECONSTRUCTOR`
* `BLOCK-THERMAL-GENERATOR`
* `BLOCK-THORIUM-REACTOR`
* `BLOCK-THORIUM-WALL`
* `BLOCK-THORIUM-WALL-LARGE`
* `BLOCK-THRUSTER`
* `BLOCK-TITAN`
* `BLOCK-TITAN-FACTORY`
* `BLOCK-TITANIUM-CONVEYOR`
* `BLOCK-TITANIUM-WALL`
* `BLOCK-TITANIUM-WALL-LARGE`
* `BLOCK-TRIDENT-SHIP-PAD`
* `BLOCK-TSUNAMI`
* `BLOCK-TUNGSTEN-WALL`
* `BLOCK-TUNGSTEN-WALL-LARGE`
* `BLOCK-TURBINE-CONDENSER`
* `BLOCK-UNDERFLOW-DUCT`
* `BLOCK-UNDERFLOW-GATE`
* `BLOCK-UNIT-CARGO-LOADER`
* `BLOCK-UNIT-CARGO-UNLOAD-POINT`
* `BLOCK-UNIT-REPAIR-TOWER`
* `BLOCK-UNLOADER`
* `BLOCK-VAULT`
* `BLOCK-VENT-CONDENSER`
* `BLOCK-VIBRANT-CRYSTAL-CLUSTER`
* `BLOCK-WALL-ORE-BERYLLIUM`
* `BLOCK-WALL-ORE-TUNGSTEN`
* `BLOCK-WARHEAD-ASSEMBLER`
* `BLOCK-WATER-EXTRACTOR`
* `BLOCK-WAVE`
* `BLOCK-WHITE-TREE`
* `BLOCK-WHITE-TREE-DEAD`
* `BLOCK-WORLD-CELL`
* `BLOCK-WORLD-MESSAGE`
* `BLOCK-WORLD-PROCESSOR`
* `BLOCK-WRAITH-FACTORY`
* `BLOCK-YELLOW-STONE`
* `BLOCK-YELLOW-STONE-BOULDER`
* `BLOCK-YELLOW-STONE-PLATES`
* `BLOCK-YELLOW-STONE-VENT`
* `BLOCK-YELLOW-STONE-WALL`
* `BLOCK-YELLOWCORAL`

</details>

<details><summary>Show full list of Mindustry item/liquid icons.</summary>

* `ITEM-BERYLLIUM`
* `ITEM-BLAST-COMPOUND`
* `ITEM-CARBIDE`
* `ITEM-COAL`
* `ITEM-COPPER`
* `ITEM-DORMANT-CYST`
* `ITEM-FISSILE-MATTER`
* `ITEM-GRAPHITE`
* `ITEM-LEAD`
* `ITEM-METAGLASS`
* `ITEM-OXIDE`
* `ITEM-PHASE-FABRIC`
* `ITEM-PLASTANIUM`
* `ITEM-PYRATITE`
* `ITEM-SAND`
* `ITEM-SCRAP`
* `ITEM-SILICON`
* `ITEM-SPORE-POD`
* `ITEM-SURGE-ALLOY`
* `ITEM-THORIUM`
* `ITEM-TITANIUM`
* `ITEM-TUNGSTEN`
* `LIQUID-ARKYCITE`
* `LIQUID-CRYOFLUID`
* `LIQUID-CYANOGEN`
* `LIQUID-GALLIUM`
* `LIQUID-HYDROGEN`
* `LIQUID-NEOPLASM`
* `LIQUID-NITROGEN`
* `LIQUID-OIL`
* `LIQUID-OXYGEN`
* `LIQUID-OZONE`
* `LIQUID-SLAG`
* `LIQUID-WATER`

</details>

<details><summary>Show full list of Mindustry status and team icons.</summary>

* `STATUS-BLASTED`
* `STATUS-BOSS`
* `STATUS-BURNING`
* `STATUS-CORRODED`
* `STATUS-DISARMED`
* `STATUS-ELECTRIFIED`
* `STATUS-FREEZING`
* `STATUS-INVINCIBLE`
* `STATUS-MELTING`
* `STATUS-MUDDY`
* `STATUS-NONE`
* `STATUS-OVERCLOCK`
* `STATUS-OVERDRIVE`
* `STATUS-SAPPED`
* `STATUS-SHIELDED`
* `STATUS-SHOCKED`
* `STATUS-SLOW`
* `STATUS-SPORE-SLOWED`
* `STATUS-TARRED`
* `STATUS-UNMOVING`
* `STATUS-WET`
* `TEAM-CRUX`
* `TEAM-DERELICT`
* `TEAM-MALIS`
* `TEAM-SHARDED`

</details>

<details><summary>Show full list of Mindustry unit icons.</summary>

* `UNIT-AEGIRES`
* `UNIT-ALPHA`
* `UNIT-ANTHICUS`
* `UNIT-ANTHICUS-MISSILE`
* `UNIT-ANTUMBRA`
* `UNIT-ARKYID`
* `UNIT-ASSEMBLY-DRONE`
* `UNIT-ATRAX`
* `UNIT-AVERT`
* `UNIT-BETA`
* `UNIT-BLOCK`
* `UNIT-BRYDE`
* `UNIT-CATACLYST`
* `UNIT-CLEROI`
* `UNIT-COLLARIS`
* `UNIT-CONQUER`
* `UNIT-CORVUS`
* `UNIT-CRAWLER`
* `UNIT-CYERCE`
* `UNIT-DAGGER`
* `UNIT-DISRUPT`
* `UNIT-DISRUPT-MISSILE`
* `UNIT-ECLIPSE`
* `UNIT-EFFECT-DRONE`
* `UNIT-ELUDE`
* `UNIT-EMANATE`
* `UNIT-EVOKE`
* `UNIT-FLARE`
* `UNIT-FORTRESS`
* `UNIT-GAMMA`
* `UNIT-HORIZON`
* `UNIT-INCITE`
* `UNIT-LATUM`
* `UNIT-LOCUS`
* `UNIT-MACE`
* `UNIT-MANIFOLD`
* `UNIT-MEGA`
* `UNIT-MERUI`
* `UNIT-MINKE`
* `UNIT-MONO`
* `UNIT-NAVANAX`
* `UNIT-NOVA`
* `UNIT-OBVIATE`
* `UNIT-OCT`
* `UNIT-OMURA`
* `UNIT-OSC`
* `UNIT-OXYNOE`
* `UNIT-POLY`
* `UNIT-PRECEPT`
* `UNIT-PULSAR`
* `UNIT-QUAD`
* `UNIT-QUASAR`
* `UNIT-QUELL`
* `UNIT-QUELL-MISSILE`
* `UNIT-REIGN`
* `UNIT-RENALE`
* `UNIT-RETUSA`
* `UNIT-RISSE`
* `UNIT-RISSO`
* `UNIT-SCATHE-MISSILE`
* `UNIT-SCEPTER`
* `UNIT-SEI`
* `UNIT-SPIROCT`
* `UNIT-STELL`
* `UNIT-TECTA`
* `UNIT-TOXOPID`
* `UNIT-TURRET-UNIT-BUILD-TOWER`
* `UNIT-VANQUISH`
* `UNIT-VELA`
* `UNIT-VESTIGE`
* `UNIT-ZENITH`

</details>

<details><summary>Show full list of Mindustry unclassified icons.</summary>

* `ALPHAAAA`
* `CRATER`

</details>


---

[« Previous: Mindcode basics](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Expressions »](SYNTAX-2-EXPRESSIONS.markdown)
