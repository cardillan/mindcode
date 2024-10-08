# Variables and constants

Mindcode uses Mindustry Logic processor variables for its own variables. These variables can contain a number 
(integer or real), an object or a `null` value. An object can be a Mindustry object, such as a block or a unit, a 
Mindustry constant such as `@coal`, or a string such as `"Text"`.

Variables aren't declared in Mindcode, they're created with first use. `count = 10` creates a variable named 
`count` and assigns a value of `10` to it. Variables that weren't previously written to have a value of `null` in 
Mindustry Logic. Once set, processor variables are preserved (even across game saves/loads) until the processor code 
is modified or the processor is destroyed.

> [!IMPORTANT]
> Mindustry Logic stores numbers as `double`, a 64-bit floating point value. To perform bitwise operations, such as 
> `&` or `<<`, the value is converted to a 64-bit integer (a Java `long`), the operation is performed and the result 
> is assigned to a `double` again. As a consequence, for bitwise operations the variables are able to hold only 
> about 52 bits or so.

Mindcode is aware of the actual values you assign to variables. In some cases, knowing the actual value allows 
specific code optimizations to be performed, which are only valid for the value you've assigned to the variable in
the source code (see [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#data-flow-optimization)). Therefore, 
changing a value assigned to a variable in the compiled code might not produce the same results as changing the 
assigned value in the source code. For example, the following code:

```
min = 0;
max = 10;

func(min, max);

while min < max do
    println(min);
    min += 1;
    max -= 1;
end;

def func(min, max)
    println(max - min > 5 ? "High" : "Low");
end;
```

compiles into 

```
set min 0
set max 10
print "High\n"
print min
print "\n"
op add min min 1
op sub max max 1
jump 3 lessThan min max
end
```

Setting min to 10 in the source code would cause the function to output "Low" instead of "High". However, as is obvious 
from the compiled code, the function has been optimized in such a way that it always prints "High", based on the 
values of the `min` and `max` variables present in the source code.    

Mindcode has a specific mechanism for allowing _program parametrization_, that is changing some values assigned to 
variables in the compiled code. Such values must be declared as [program parameters](#program-parameters), as 
described below.

# Main variables

Main variables are variables used in the main body of the program. The name of the variable in Mindcode is used as-is 
in the compiled mlog code.

# Local variables

Local variables are function parameters or variables used in a user-defined function. For example, in a function

```
def foo(x)
    y = x ** 2;
end;
```

both `x` and `y` are local variables, not accessible outside the function `foo`.

In compiled code, names of these variables are appended to a unique function prefix. Recognizing names of local 
variables in compiled code might be a bit cumbersome.  

# Global variables

Global variables are common to all user functions and the main program body. Use names that don't contain any
lowercase letters, such as `MAIN` or `_9` (this is actually not a particularly good name for a variable), to create 
global variables. Variables whose name contains at least one lowercase letter are not global.

For example, the following code

```
def foo(x)
    MAIN = x + 10;
    local = 10;
end;
MAIN = 5;
local = 5;
x = 0;
foo(10);
print(MAIN, ", ", local);
printflush(message1);
```

displays `20, 5` on the `message1` block in Mindustry world (from now on, we'll omit the `printflush(message1)` in 
examples), since both `x` and `local` in the `foo` function are local variables and therefore distinct from `x` and 
`local` in the main program body.

Using global variables as function parameters (e.g. `def foo(Z) ... end`) is not allowed.

> [!IMPORTANT]
> In previous versions of Mindcode, global variables also served as program parameters described below. This usage 
> of global variables will cease to be supported be removed in a future release. Please modify your programs to use 
> program parameters instead.

# Program parameters

One of Mindcode goals is to facilitate making small changes to the compiled code, allowing users to change crucial 
parameters without having to recompile entire program. To this end, it is possible to declare _program parameters_,
a special type of variable, using the `param` keyword: 

```
param UNIT_TYPE = @flare;
param MAX_UNITS = 10;
param MEMORY = cell1;
param USER_NAME = "Pete"; 
```

This code, when compiled, produces the following instructions:

```
set UNIT_TYPE @flare
set MAX_UNITS 10
set MEMORY cell1
set USER_NAME "Pete"
```

Names of the mlog variables representing the program parameters are always the same as the names used in the source 
code. Values assigned through these instructions can be changed in the compiled code. When modifying values assigned 
to program parameters in the compiled code, the program behaves as if these values were specified in the source code 
itself. This is not guaranteed when changing values assigned to any other variables in the compiled mlog code.

The following values may be used when declaring program parameters:

* String or numeric literals, such as `5` or `"some text"`.
* Names of linked blocks, such as `message1` or `switch5`.
* Mindustry logic values and constant (or effectively constant) variables, such as `@flare`, `@coal` or `@mapw`.

Other values, including compile-time constant expressions (e.g. `5 * 3`) and non-constant mlog values (e.g. `@links`)
are not allowed.

In Mindcode, program parameters are global (can be accessed from main code and all functions) even when their names 
contain lower case letters, and once declared, are read-only. It is not allowed to assign a new value to them:

```
param parameter = 10; // lower-case names are ok
parameter = 20;       // error, cannot assign another value to parameter.
```

This means there's always exactly one `set` instruction assigning a value to the program parameter, making the 
purpose of the mlog variable in the compiled code clearer.

Unlike other types of variables, program parameters are never optimized away from the code. Only when the program
parameter is not used at all in the program, it may be removed by the Dead Code optimization.

> [!IMPORTANT]
> Correct execution of the program is only guaranteed if the value assigned to the program parameter in the compiled code is constant. When assigning a non-constant value (for example `@links`) to a program parameter, or when the value of the parameter may be read before being initialized, the behavior of the resulting code is generally undefined.

# External memory

Mindcode supports storing variables in external memory - memory cells or memory banks linked to the processor. These 
variables are stored independently of the processor and can be used to share values between processors, or to keep 
values even when the processor is destroyed or its code altered.

On the other hand, only numeric values can be stored in external memory. It is therefore not possible to use it to 
store strings, units, buildings or item types there. This -- quite restrictive -- limitation is unfortunately 
imposed by Mindustry Logic itself.

## Arrays

You can use external variables using the array access syntax:

```
for i in 0 .. 63 do
    cell2[i] = cell1[i];
end;
```

This code copies the entire contents of memory cell `cell1` to `cell2`.

It is also possible to reference external memory indirectly:

```
def copy(source, target, size)
    for i in 0 ... size do
        target[i] = source[i];
    end;
end;
copy(bank1, bank2, 512);
```

## External variables

You can also have Mindcode assign identifiers to external variables. For that, you need to allocate a heap within a 
Memory Cell or a Memory Bank. This allocation tells the Mindcode compiler where to store the external variables. A 
heap is simply a region of external memory. The heap is allocated using the following Mindcode:

```
allocate heap in cell4[50 ... 64];
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more external variables than you have allocated space for, compilation will fail 
with an `OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a 
Memory Bank and allocate more space to your heap.

Once the heap is allocated, you can use external variables. External variables are identified by the `$` 
(dollar-sign) prefix: 

```
allocate heap in cell4[32 ... 64];

$dx = 1; // this is an external variable assignment
$dy = 1;
$ne_x = 90;
$ne_y = 90;
$target_y = $sw_x = 50;
$target_x = $sw_y = 50;
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

> [!NOTE]
> External variables are allocated on a first-come, first-served basis.

If you had the following code:

```
allocate heap in cell2[61 .. 63];

$flag = rand(10000);
$targetx = 80;
$targety = 80;
```

and changed it to:

```
allocate heap in cell2[61 .. 63];

$targetx = 80;
$targety = 80;
$flag = rand(10000);
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
allocate stack in bank1[256...512];
```

When a function is not recursive, it won't store anything on a stack, even when it is called from or it itself calls 
a recursive function. If your code contains a recursive function, it won't compile unless the stack is allocated. 
Therefore, if your code compiles without the `allocate stack` statement, you don't need to worry about your 
functions not supporting non-numeric variables or parameters.

## Heap and stack indirection

When you build your Mindcode scripts, the actual Memory Cell and Memory Bank that you use may be different from the 
ones you use when playing the game. To that end, you also have the option of referencing your heap and stack through 
the use of a program parameter, like this:

```
param HEAPPTR = cell3;
allocate heap in HEAPPTR;
$dx = 0;
```

This will translate to:

```
set HEAPPTR cell3
write 0 HEAPPTR 0
```

Since the very first instruction of the compiled code will be the program parameter assignment, you can easily 
change the actual cell or bank your will use, without having to do a global search & replace within the compiled 
code. This introduces more avenues for code sharing.

It is possible to allocate stack and heap in the same memory block, and/or in one statement:

```
param MEMORY = bank1;
allocate stack in MEMORY[0 ... 480], heap in MEMORY[480 ... 512];
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
    print("In function foo");
    printflush(message1);
end
```

the `message1` variable represents a message block linked to the processor, even when used inside a function.

The list of possible block names is quite exhaustive.

<details><summary>Show full list of Mindustry block names.</summary>

* `acropolis`
* `afflict`
* `arc`
* `assembler`
* `bank`
* `bastion`
* `battery`
* `bore`
* `breach`
* `bridge`
* `canvas`
* `cell`
* `centrifuge`
* `chamber`
* `citadel`
* `compressor`
* `concentrator`
* `condenser`
* `conduit`
* `constructor`
* `container`
* `conveyor`
* `crucible`
* `crusher`
* `cultivator`
* `cyclone`
* `deconstructor`
* `diffuse`
* `diode`
* `disassembler`
* `disperse`
* `display`
* `distributor`
* `dome`
* `door`
* `drill`
* `driver`
* `duct`
* `duo`
* `electrolyzer`
* `extractor`
* `fabricator`
* `factory`
* `foreshadow`
* `foundation`
* `furnace`
* `fuse`
* `gate`
* `generator`
* `gigantic`
* `hail`
* `heater`
* `huge`
* `illuminator`
* `incinerator`
* `junction`
* `kiln`
* `lancer`
* `link`
* `loader`
* `lustre`
* `malign`
* `meltdown`
* `melter`
* `mender`
* `message`
* `mine`
* `mixer`
* `module`
* `node`
* `nucleus`
* `pad`
* `panel`
* `parallax`
* `point`
* `press`
* `processor`
* `projector`
* `pulverizer`
* `pump`
* `radar`
* `reactor`
* `reconstructor`
* `redirector`
* `refabricator`
* `ripple`
* `router`
* `salvo`
* `scathe`
* `scatter`
* `scorch`
* `segment`
* `separator`
* `shard`
* `smelter`
* `smite`
* `sorter`
* `source`
* `spectre`
* `sublimate`
* `swarmer`
* `switch`
* `synthesizer`
* `tank`
* `thruster`
* `titan`
* `tower`
* `tsunami`
* `turret`
* `unloader`
* `vault`
* `void`
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
const DEBUG = true;
const HIGH_SPEED = 50;
const LOW_SPEED = HIGH_SPEED / 2;
const RATIO = sqrt(2);
const message = DEBUG ? "Debug" : "Release";
const FORMAT = $"Position: $, $"; 
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
const DEBUG = false;
...
if DEBUG
    println("State: ", state);
    println("Item count: ", @unit.totalItems);
end;
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
print(ITEM_LEAD, " ", vault1.lead, "\n");
print(ITEM_COAL, " ", vault1.coal, "\n");
print(ITEM_BLAST_COMPOUND, " ", vault1.blast-compound);
printflush(message1);
```

As has already been mentioned, names of the icons are constants and cannot therefore be used for user-defined 
constants or variables. 

The list of all existing icons is quite huge:

<details><summary>Show full list of Mindustry block icons.</summary>

* `BLOCK_ADDITIVE_RECONSTRUCTOR`
* `BLOCK_AFFLICT`
* `BLOCK_AIR_FACTORY`
* `BLOCK_ALPHA_MECH_PAD`
* `BLOCK_ARC`
* `BLOCK_ARKYCITE_FLOOR`
* `BLOCK_ARKYIC_BOULDER`
* `BLOCK_ARKYIC_STONE`
* `BLOCK_ARKYIC_VENT`
* `BLOCK_ARKYIC_WALL`
* `BLOCK_ARMORED_CONVEYOR`
* `BLOCK_ARMORED_DUCT`
* `BLOCK_ATMOSPHERIC_CONCENTRATOR`
* `BLOCK_BALLISTIC_SILO`
* `BLOCK_BARRIER_PROJECTOR`
* `BLOCK_BASALT`
* `BLOCK_BASALT_BOULDER`
* `BLOCK_BASIC_ASSEMBLER_MODULE`
* `BLOCK_BASIC_RECONSTRUCTOR`
* `BLOCK_BATTERY`
* `BLOCK_BATTERY_LARGE`
* `BLOCK_BEAM_DRILL`
* `BLOCK_BEAM_LINK`
* `BLOCK_BEAM_NODE`
* `BLOCK_BEAM_TOWER`
* `BLOCK_BERYLLIC_BOULDER`
* `BLOCK_BERYLLIC_STONE`
* `BLOCK_BERYLLIC_STONE_WALL`
* `BLOCK_BERYLLIUM_WALL`
* `BLOCK_BERYLLIUM_WALL_LARGE`
* `BLOCK_BLAST_DOOR`
* `BLOCK_BLAST_DRILL`
* `BLOCK_BLAST_MIXER`
* `BLOCK_BLOCK_FORGE`
* `BLOCK_BLOCK_LAUNCHER`
* `BLOCK_BLOCK_LOADER`
* `BLOCK_BLOCK_UNLOADER`
* `BLOCK_BLUEMAT`
* `BLOCK_BOULDER`
* `BLOCK_BREACH`
* `BLOCK_BRIDGE_CONDUIT`
* `BLOCK_BRIDGE_CONVEYOR`
* `BLOCK_BUILD_TOWER`
* `BLOCK_CANVAS`
* `BLOCK_CARBIDE_CRUCIBLE`
* `BLOCK_CARBIDE_WALL`
* `BLOCK_CARBIDE_WALL_LARGE`
* `BLOCK_CARBON_BOULDER`
* `BLOCK_CARBON_STONE`
* `BLOCK_CARBON_VENT`
* `BLOCK_CARBON_WALL`
* `BLOCK_CELL_SYNTHESIS_CHAMBER`
* `BLOCK_CHAR`
* `BLOCK_CHEMICAL_COMBUSTION_CHAMBER`
* `BLOCK_CLIFF`
* `BLOCK_CLIFF_CRUSHER`
* `BLOCK_CLIFFS`
* `BLOCK_COAL_CENTRIFUGE`
* `BLOCK_COMBUSTION_GENERATOR`
* `BLOCK_COMMAND_CENTER`
* `BLOCK_CONDUIT`
* `BLOCK_CONSTRUCTOR`
* `BLOCK_CONTAINER`
* `BLOCK_CONVEYOR`
* `BLOCK_COPPER_WALL`
* `BLOCK_COPPER_WALL_LARGE`
* `BLOCK_CORE_ACROPOLIS`
* `BLOCK_CORE_BASTION`
* `BLOCK_CORE_CITADEL`
* `BLOCK_CORE_FOUNDATION`
* `BLOCK_CORE_NUCLEUS`
* `BLOCK_CORE_SHARD`
* `BLOCK_CORE_SILO`
* `BLOCK_CORE_ZONE`
* `BLOCK_CRATER_STONE`
* `BLOCK_CRATERS`
* `BLOCK_CRAWLER_FACTORY`
* `BLOCK_CRYOFLUID_MIXER`
* `BLOCK_CRYSTAL_BLOCKS`
* `BLOCK_CRYSTAL_CLUSTER`
* `BLOCK_CRYSTAL_FLOOR`
* `BLOCK_CRYSTAL_ORBS`
* `BLOCK_CRYSTALLINE_BOULDER`
* `BLOCK_CRYSTALLINE_STONE`
* `BLOCK_CRYSTALLINE_STONE_WALL`
* `BLOCK_CRYSTALLINE_VENT`
* `BLOCK_CULTIVATOR`
* `BLOCK_CYANOGEN_SYNTHESIZER`
* `BLOCK_CYCLONE`
* `BLOCK_DACITE`
* `BLOCK_DACITE_BOULDER`
* `BLOCK_DACITE_WALL`
* `BLOCK_DAGGER_FACTORY`
* `BLOCK_DARK_METAL`
* `BLOCK_DARK_PANEL_1`
* `BLOCK_DARK_PANEL_2`
* `BLOCK_DARK_PANEL_3`
* `BLOCK_DARK_PANEL_4`
* `BLOCK_DARK_PANEL_5`
* `BLOCK_DARK_PANEL_6`
* `BLOCK_DARKSAND`
* `BLOCK_DARKSAND_TAINTED_WATER`
* `BLOCK_DARKSAND_WATER`
* `BLOCK_DART_MECH_PAD`
* `BLOCK_DART_SHIP_PAD`
* `BLOCK_DATA_PROCESSOR`
* `BLOCK_DECONSTRUCTOR`
* `BLOCK_DEEP_TAINTED_WATER`
* `BLOCK_DEEP_WATER`
* `BLOCK_DEEPWATER`
* `BLOCK_DELTA_MECH_PAD`
* `BLOCK_DENSE_RED_STONE`
* `BLOCK_DIFFERENTIAL_GENERATOR`
* `BLOCK_DIFFUSE`
* `BLOCK_DIODE`
* `BLOCK_DIRT`
* `BLOCK_DIRT_WALL`
* `BLOCK_DISASSEMBLER`
* `BLOCK_DISPERSE`
* `BLOCK_DISTRIBUTOR`
* `BLOCK_DOOR`
* `BLOCK_DOOR_LARGE`
* `BLOCK_DRAUG_FACTORY`
* `BLOCK_DUCT`
* `BLOCK_DUCT_BRIDGE`
* `BLOCK_DUCT_ROUTER`
* `BLOCK_DUCT_UNLOADER`
* `BLOCK_DUNE_WALL`
* `BLOCK_DUO`
* `BLOCK_ELECTRIC_HEATER`
* `BLOCK_ELECTROLYZER`
* `BLOCK_EMPTY`
* `BLOCK_ERUPTION_DRILL`
* `BLOCK_EXPONENTIAL_RECONSTRUCTOR`
* `BLOCK_FABRICATOR`
* `BLOCK_FERRIC_BOULDER`
* `BLOCK_FERRIC_CRATERS`
* `BLOCK_FERRIC_STONE`
* `BLOCK_FERRIC_STONE_WALL`
* `BLOCK_FLUX_REACTOR`
* `BLOCK_FORCE_PROJECTOR`
* `BLOCK_FORESHADOW`
* `BLOCK_FORTRESS_FACTORY`
* `BLOCK_FRACTURE`
* `BLOCK_FUSE`
* `BLOCK_GHOUL_FACTORY`
* `BLOCK_GLAIVE_SHIP_PAD`
* `BLOCK_GRAPHITE_PRESS`
* `BLOCK_GRAPHITIC_WALL`
* `BLOCK_GRASS`
* `BLOCK_GROUND_FACTORY`
* `BLOCK_HAIL`
* `BLOCK_HEAT_REACTOR`
* `BLOCK_HEAT_REDIRECTOR`
* `BLOCK_HEAT_ROUTER`
* `BLOCK_HEAT_SOURCE`
* `BLOCK_HOTROCK`
* `BLOCK_HYPER_PROCESSOR`
* `BLOCK_ICE`
* `BLOCK_ICE_SNOW`
* `BLOCK_ICE_WALL`
* `BLOCK_IGNAROCK`
* `BLOCK_ILLUMINATOR`
* `BLOCK_IMPACT_DRILL`
* `BLOCK_IMPACT_REACTOR`
* `BLOCK_IMPULSE_PUMP`
* `BLOCK_INCINERATOR`
* `BLOCK_INTERPLANETARY_ACCELERATOR`
* `BLOCK_INVERTED_SORTER`
* `BLOCK_ITEM_SOURCE`
* `BLOCK_ITEM_VOID`
* `BLOCK_JAVELIN_SHIP_PAD`
* `BLOCK_JUNCTION`
* `BLOCK_KILN`
* `BLOCK_LANCER`
* `BLOCK_LARGE_CONSTRUCTOR`
* `BLOCK_LARGE_LOGIC_DISPLAY`
* `BLOCK_LARGE_OVERDRIVE_PROJECTOR`
* `BLOCK_LARGE_PAYLOAD_MASS_DRIVER`
* `BLOCK_LARGE_PLASMA_BORE`
* `BLOCK_LARGE_SHIELD_PROJECTOR`
* `BLOCK_LASER_DRILL`
* `BLOCK_LAUNCH_PAD`
* `BLOCK_LAUNCH_PAD_LARGE`
* `BLOCK_LEGACY_COMMAND_CENTER`
* `BLOCK_LEGACY_MECH_PAD`
* `BLOCK_LEGACY_UNIT_FACTORY`
* `BLOCK_LEGACY_UNIT_FACTORY_AIR`
* `BLOCK_LEGACY_UNIT_FACTORY_GROUND`
* `BLOCK_LIQUID_CONTAINER`
* `BLOCK_LIQUID_JUNCTION`
* `BLOCK_LIQUID_ROUTER`
* `BLOCK_LIQUID_SOURCE`
* `BLOCK_LIQUID_TANK`
* `BLOCK_LIQUID_VOID`
* `BLOCK_LOGIC_DISPLAY`
* `BLOCK_LOGIC_PROCESSOR`
* `BLOCK_LUSTRE`
* `BLOCK_MAGMAROCK`
* `BLOCK_MALIGN`
* `BLOCK_MASS_CONVEYOR`
* `BLOCK_MASS_DRIVER`
* `BLOCK_MECH_ASSEMBLER`
* `BLOCK_MECH_FABRICATOR`
* `BLOCK_MECH_RECONSTRUCTOR`
* `BLOCK_MECH_REFABRICATOR`
* `BLOCK_MECHANICAL_DRILL`
* `BLOCK_MECHANICAL_PUMP`
* `BLOCK_MELTDOWN`
* `BLOCK_MELTER`
* `BLOCK_MEMORY_BANK`
* `BLOCK_MEMORY_CELL`
* `BLOCK_MEND_PROJECTOR`
* `BLOCK_MENDER`
* `BLOCK_MESSAGE`
* `BLOCK_METAL_FLOOR`
* `BLOCK_METAL_FLOOR_2`
* `BLOCK_METAL_FLOOR_3`
* `BLOCK_METAL_FLOOR_4`
* `BLOCK_METAL_FLOOR_5`
* `BLOCK_METAL_FLOOR_DAMAGED`
* `BLOCK_MICRO_PROCESSOR`
* `BLOCK_MOLTEN_SLAG`
* `BLOCK_MOSS`
* `BLOCK_MUD`
* `BLOCK_MULTI_PRESS`
* `BLOCK_MULTIPLICATIVE_RECONSTRUCTOR`
* `BLOCK_NAVAL_FACTORY`
* `BLOCK_NEOPLASIA_REACTOR`
* `BLOCK_NUCLEAR_WARHEAD`
* `BLOCK_OIL_EXTRACTOR`
* `BLOCK_OMEGA_MECH_PAD`
* `BLOCK_ORE_BERYLLIUM`
* `BLOCK_ORE_COAL`
* `BLOCK_ORE_COPPER`
* `BLOCK_ORE_CRYSTAL_THORIUM`
* `BLOCK_ORE_LEAD`
* `BLOCK_ORE_SCRAP`
* `BLOCK_ORE_THORIUM`
* `BLOCK_ORE_TITANIUM`
* `BLOCK_ORE_TUNGSTEN`
* `BLOCK_ORE_WALL_BERYLLIUM`
* `BLOCK_ORE_WALL_THORIUM`
* `BLOCK_ORE_WALL_TUNGSTEN`
* `BLOCK_OVERDRIVE_DOME`
* `BLOCK_OVERDRIVE_PROJECTOR`
* `BLOCK_OVERFLOW_DUCT`
* `BLOCK_OVERFLOW_GATE`
* `BLOCK_OXIDATION_CHAMBER`
* `BLOCK_OXIDIZER`
* `BLOCK_PARALLAX`
* `BLOCK_PAYLOAD_CONVEYOR`
* `BLOCK_PAYLOAD_INCINERATOR`
* `BLOCK_PAYLOAD_LOADER`
* `BLOCK_PAYLOAD_MASS_DRIVER`
* `BLOCK_PAYLOAD_ROUTER`
* `BLOCK_PAYLOAD_SOURCE`
* `BLOCK_PAYLOAD_UNLOADER`
* `BLOCK_PAYLOAD_VOID`
* `BLOCK_PEBBLES`
* `BLOCK_PHANTOM_FACTORY`
* `BLOCK_PHASE_CONDUIT`
* `BLOCK_PHASE_CONVEYOR`
* `BLOCK_PHASE_HEATER`
* `BLOCK_PHASE_SYNTHESIZER`
* `BLOCK_PHASE_WALL`
* `BLOCK_PHASE_WALL_LARGE`
* `BLOCK_PHASE_WEAVER`
* `BLOCK_PINE`
* `BLOCK_PLASMA_BORE`
* `BLOCK_PLASTANIUM_COMPRESSOR`
* `BLOCK_PLASTANIUM_CONVEYOR`
* `BLOCK_PLASTANIUM_WALL`
* `BLOCK_PLASTANIUM_WALL_LARGE`
* `BLOCK_PLATED_CONDUIT`
* `BLOCK_PNEUMATIC_DRILL`
* `BLOCK_POOLED_CRYOFLUID`
* `BLOCK_POWER_NODE`
* `BLOCK_POWER_NODE_LARGE`
* `BLOCK_POWER_SOURCE`
* `BLOCK_POWER_VOID`
* `BLOCK_PRESSURE_TURBINE`
* `BLOCK_PRIME_REFABRICATOR`
* `BLOCK_PULSE_CONDUIT`
* `BLOCK_PULVERIZER`
* `BLOCK_PUR_BUSH`
* `BLOCK_PYRATITE_MIXER`
* `BLOCK_PYROLYSIS_GENERATOR`
* `BLOCK_RADAR`
* `BLOCK_RAVAGE`
* `BLOCK_RED_DIAMOND_WALL`
* `BLOCK_RED_ICE`
* `BLOCK_RED_ICE_BOULDER`
* `BLOCK_RED_ICE_WALL`
* `BLOCK_RED_STONE`
* `BLOCK_RED_STONE_BOULDER`
* `BLOCK_RED_STONE_VENT`
* `BLOCK_RED_STONE_WALL`
* `BLOCK_REDMAT`
* `BLOCK_REDWEED`
* `BLOCK_REFABRICATOR`
* `BLOCK_REGEN_PROJECTOR`
* `BLOCK_REGOLITH`
* `BLOCK_REGOLITH_WALL`
* `BLOCK_REINFORCED_BRIDGE_CONDUIT`
* `BLOCK_REINFORCED_CONDUIT`
* `BLOCK_REINFORCED_CONTAINER`
* `BLOCK_REINFORCED_LIQUID_CONTAINER`
* `BLOCK_REINFORCED_LIQUID_JUNCTION`
* `BLOCK_REINFORCED_LIQUID_ROUTER`
* `BLOCK_REINFORCED_LIQUID_TANK`
* `BLOCK_REINFORCED_MESSAGE`
* `BLOCK_REINFORCED_PAYLOAD_CONVEYOR`
* `BLOCK_REINFORCED_PAYLOAD_ROUTER`
* `BLOCK_REINFORCED_PUMP`
* `BLOCK_REINFORCED_SURGE_WALL`
* `BLOCK_REINFORCED_SURGE_WALL_LARGE`
* `BLOCK_REINFORCED_VAULT`
* `BLOCK_REPAIR_POINT`
* `BLOCK_REPAIR_TURRET`
* `BLOCK_RESUPPLY_POINT`
* `BLOCK_REVENANT_FACTORY`
* `BLOCK_RHYOLITE`
* `BLOCK_RHYOLITE_BOULDER`
* `BLOCK_RHYOLITE_CRATER`
* `BLOCK_RHYOLITE_VENT`
* `BLOCK_RHYOLITE_WALL`
* `BLOCK_RIPPLE`
* `BLOCK_ROCK`
* `BLOCK_ROTARY_PUMP`
* `BLOCK_ROUGH_RHYOLITE`
* `BLOCK_ROUTER`
* `BLOCK_RTG_GENERATOR`
* `BLOCK_SALT`
* `BLOCK_SALT_WALL`
* `BLOCK_SALVO`
* `BLOCK_SAND`
* `BLOCK_SAND_BOULDER`
* `BLOCK_SAND_FLOOR`
* `BLOCK_SAND_WALL`
* `BLOCK_SAND_WATER`
* `BLOCK_SCATHE`
* `BLOCK_SCATTER`
* `BLOCK_SCORCH`
* `BLOCK_SCRAP_WALL`
* `BLOCK_SCRAP_WALL_GIGANTIC`
* `BLOCK_SCRAP_WALL_HUGE`
* `BLOCK_SCRAP_WALL_LARGE`
* `BLOCK_SEGMENT`
* `BLOCK_SEPARATOR`
* `BLOCK_SHALE`
* `BLOCK_SHALE_BOULDER`
* `BLOCK_SHALE_WALL`
* `BLOCK_SHALLOW_WATER`
* `BLOCK_SHIELD_PROJECTOR`
* `BLOCK_SHIELDED_WALL`
* `BLOCK_SHIP_ASSEMBLER`
* `BLOCK_SHIP_FABRICATOR`
* `BLOCK_SHIP_RECONSTRUCTOR`
* `BLOCK_SHIP_REFABRICATOR`
* `BLOCK_SHOCK_MINE`
* `BLOCK_SHOCKWAVE_TOWER`
* `BLOCK_SHRUBS`
* `BLOCK_SILICON_ARC_FURNACE`
* `BLOCK_SILICON_CRUCIBLE`
* `BLOCK_SILICON_SMELTER`
* `BLOCK_SLAG_CENTRIFUGE`
* `BLOCK_SLAG_HEATER`
* `BLOCK_SLAG_INCINERATOR`
* `BLOCK_SMALL_DECONSTRUCTOR`
* `BLOCK_SMITE`
* `BLOCK_SNOW`
* `BLOCK_SNOW_BOULDER`
* `BLOCK_SNOW_PINE`
* `BLOCK_SNOW_WALL`
* `BLOCK_SNOWROCK`
* `BLOCK_SOLAR_PANEL`
* `BLOCK_SOLAR_PANEL_LARGE`
* `BLOCK_SORTER`
* `BLOCK_SPACE`
* `BLOCK_SPAWN`
* `BLOCK_SPECTRE`
* `BLOCK_SPIRIT_FACTORY`
* `BLOCK_SPORE_CLUSTER`
* `BLOCK_SPORE_MOSS`
* `BLOCK_SPORE_PINE`
* `BLOCK_SPORE_PRESS`
* `BLOCK_SPORE_WALL`
* `BLOCK_STEAM_GENERATOR`
* `BLOCK_STEAM_VENT`
* `BLOCK_STONE`
* `BLOCK_STONE_WALL`
* `BLOCK_SUBLIMATE`
* `BLOCK_SURGE_CONVEYOR`
* `BLOCK_SURGE_CRUCIBLE`
* `BLOCK_SURGE_DUCT`
* `BLOCK_SURGE_ROUTER`
* `BLOCK_SURGE_SMELTER`
* `BLOCK_SURGE_TOWER`
* `BLOCK_SURGE_WALL`
* `BLOCK_SURGE_WALL_LARGE`
* `BLOCK_SWARMER`
* `BLOCK_SWITCH`
* `BLOCK_TAINTED_WATER`
* `BLOCK_TANK_ASSEMBLER`
* `BLOCK_TANK_FABRICATOR`
* `BLOCK_TANK_RECONSTRUCTOR`
* `BLOCK_TANK_REFABRICATOR`
* `BLOCK_TAR`
* `BLOCK_TAU_MECH_PAD`
* `BLOCK_TENDRILS`
* `BLOCK_TETRATIVE_RECONSTRUCTOR`
* `BLOCK_THERMAL_GENERATOR`
* `BLOCK_THORIUM_REACTOR`
* `BLOCK_THORIUM_WALL`
* `BLOCK_THORIUM_WALL_LARGE`
* `BLOCK_THRUSTER`
* `BLOCK_TITAN`
* `BLOCK_TITAN_FACTORY`
* `BLOCK_TITANIUM_CONVEYOR`
* `BLOCK_TITANIUM_WALL`
* `BLOCK_TITANIUM_WALL_LARGE`
* `BLOCK_TRIDENT_SHIP_PAD`
* `BLOCK_TSUNAMI`
* `BLOCK_TUNGSTEN_WALL`
* `BLOCK_TUNGSTEN_WALL_LARGE`
* `BLOCK_TURBINE_CONDENSER`
* `BLOCK_UNDERFLOW_DUCT`
* `BLOCK_UNDERFLOW_GATE`
* `BLOCK_UNIT_CARGO_LOADER`
* `BLOCK_UNIT_CARGO_UNLOAD_POINT`
* `BLOCK_UNIT_REPAIR_TOWER`
* `BLOCK_UNLOADER`
* `BLOCK_VAULT`
* `BLOCK_VENT_CONDENSER`
* `BLOCK_VIBRANT_CRYSTAL_CLUSTER`
* `BLOCK_WALL_ORE_BERYLLIUM`
* `BLOCK_WALL_ORE_TUNGSTEN`
* `BLOCK_WARHEAD_ASSEMBLER`
* `BLOCK_WATER_EXTRACTOR`
* `BLOCK_WAVE`
* `BLOCK_WHITE_TREE`
* `BLOCK_WHITE_TREE_DEAD`
* `BLOCK_WORLD_CELL`
* `BLOCK_WORLD_MESSAGE`
* `BLOCK_WORLD_PROCESSOR`
* `BLOCK_WRAITH_FACTORY`
* `BLOCK_YELLOW_STONE`
* `BLOCK_YELLOW_STONE_BOULDER`
* `BLOCK_YELLOW_STONE_PLATES`
* `BLOCK_YELLOW_STONE_VENT`
* `BLOCK_YELLOW_STONE_WALL`
* `BLOCK_YELLOWCORAL`

</details>

<details><summary>Show full list of Mindustry item/liquid icons.</summary>

* `ITEM_BERYLLIUM`
* `ITEM_BLAST_COMPOUND`
* `ITEM_CARBIDE`
* `ITEM_COAL`
* `ITEM_COPPER`
* `ITEM_DORMANT_CYST`
* `ITEM_FISSILE_MATTER`
* `ITEM_GRAPHITE`
* `ITEM_LEAD`
* `ITEM_METAGLASS`
* `ITEM_OXIDE`
* `ITEM_PHASE_FABRIC`
* `ITEM_PLASTANIUM`
* `ITEM_PYRATITE`
* `ITEM_SAND`
* `ITEM_SCRAP`
* `ITEM_SILICON`
* `ITEM_SPORE_POD`
* `ITEM_SURGE_ALLOY`
* `ITEM_THORIUM`
* `ITEM_TITANIUM`
* `ITEM_TUNGSTEN`
* `LIQUID_ARKYCITE`
* `LIQUID_CRYOFLUID`
* `LIQUID_CYANOGEN`
* `LIQUID_GALLIUM`
* `LIQUID_HYDROGEN`
* `LIQUID_NEOPLASM`
* `LIQUID_NITROGEN`
* `LIQUID_OIL`
* `LIQUID_OXYGEN`
* `LIQUID_OZONE`
* `LIQUID_SLAG`
* `LIQUID_WATER`

</details>

<details><summary>Show full list of Mindustry status and team icons.</summary>

* `STATUS_BLASTED`
* `STATUS_BOSS`
* `STATUS_BURNING`
* `STATUS_CORRODED`
* `STATUS_DISARMED`
* `STATUS_ELECTRIFIED`
* `STATUS_FREEZING`
* `STATUS_INVINCIBLE`
* `STATUS_MELTING`
* `STATUS_MUDDY`
* `STATUS_NONE`
* `STATUS_OVERCLOCK`
* `STATUS_OVERDRIVE`
* `STATUS_SAPPED`
* `STATUS_SHIELDED`
* `STATUS_SHOCKED`
* `STATUS_SLOW`
* `STATUS_SPORE_SLOWED`
* `STATUS_TARRED`
* `STATUS_UNMOVING`
* `STATUS_WET`
* `TEAM_CRUX`
* `TEAM_DERELICT`
* `TEAM_MALIS`
* `TEAM_SHARDED`

</details>

<details><summary>Show full list of Mindustry unit icons.</summary>

* `UNIT_AEGIRES`
* `UNIT_ALPHA`
* `UNIT_ANTHICUS`
* `UNIT_ANTHICUS_MISSILE`
* `UNIT_ANTUMBRA`
* `UNIT_ARKYID`
* `UNIT_ASSEMBLY_DRONE`
* `UNIT_ATRAX`
* `UNIT_AVERT`
* `UNIT_BETA`
* `UNIT_BLOCK`
* `UNIT_BRYDE`
* `UNIT_CATACLYST`
* `UNIT_CLEROI`
* `UNIT_COLLARIS`
* `UNIT_CONQUER`
* `UNIT_CORVUS`
* `UNIT_CRAWLER`
* `UNIT_CYERCE`
* `UNIT_DAGGER`
* `UNIT_DISRUPT`
* `UNIT_DISRUPT_MISSILE`
* `UNIT_ECLIPSE`
* `UNIT_EFFECT_DRONE`
* `UNIT_ELUDE`
* `UNIT_EMANATE`
* `UNIT_EVOKE`
* `UNIT_FLARE`
* `UNIT_FORTRESS`
* `UNIT_GAMMA`
* `UNIT_HORIZON`
* `UNIT_INCITE`
* `UNIT_LATUM`
* `UNIT_LOCUS`
* `UNIT_MACE`
* `UNIT_MANIFOLD`
* `UNIT_MEGA`
* `UNIT_MERUI`
* `UNIT_MINKE`
* `UNIT_MONO`
* `UNIT_NAVANAX`
* `UNIT_NOVA`
* `UNIT_OBVIATE`
* `UNIT_OCT`
* `UNIT_OMURA`
* `UNIT_OSC`
* `UNIT_OXYNOE`
* `UNIT_POLY`
* `UNIT_PRECEPT`
* `UNIT_PULSAR`
* `UNIT_QUAD`
* `UNIT_QUASAR`
* `UNIT_QUELL`
* `UNIT_QUELL_MISSILE`
* `UNIT_REIGN`
* `UNIT_RENALE`
* `UNIT_RETUSA`
* `UNIT_RISSE`
* `UNIT_RISSO`
* `UNIT_SCATHE_MISSILE`
* `UNIT_SCEPTER`
* `UNIT_SEI`
* `UNIT_SPIROCT`
* `UNIT_STELL`
* `UNIT_TECTA`
* `UNIT_TOXOPID`
* `UNIT_TURRET_UNIT_BUILD_TOWER`
* `UNIT_VANQUISH`
* `UNIT_VELA`
* `UNIT_VESTIGE`
* `UNIT_ZENITH`

</details>

<details><summary>Show full list of Mindustry unclassified icons.</summary>

* `ALPHAAAA`
* `CRATER`

</details>


---

[« Previous: Mindcode basics](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Expressions »](SYNTAX-2-EXPRESSIONS.markdown)
