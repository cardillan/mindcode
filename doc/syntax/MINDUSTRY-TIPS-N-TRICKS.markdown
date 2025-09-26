# Mindustry Tips and Tricks

## Formatting text

The only way to print text in Mindustry Logic is to use the `print` instruction (made available to Mindcode through various kinds of `print()` function) one or more times to construct the resulting string in a text buffer. Each `print` instruction adds its argument, converted to string if necessary, to the end of the text buffer. Every `printflush` instruction, even unsuccessful, clears the text buffer.

When a text is being printed into a message by using the `printflush` instruction, the entire string from the text buffer is displayed in the message block. At this moment, the string is inspected for special character sequences that can alter the appearance of the text in the message block. Two kinds of formatting are supported:

* Line breaks
* Setting text color

### Line breaks

When the `\n` sequence of characters is found in the string being printed, the `\n` sequence is replaced by a line break. For example:

```Mindcode
print("One\nTwo");
printflush(message1);
```

produces the following output:

```
One
Two
```

Note that the backslash character is only recognized as part of the `\n` sequence, it is not otherwise specially handled. Specifically, it is not possible to encode it as `\\`, unlike many other programming languages. Therefore, the following code snippet

```Mindcode
print("One\\Two\\nThree");
printflush(message1);
```

produces the following output:

```
One\\Two\
Three
```

If you really want to output `\n` in the message block for whatever reason, you can use this trick:

```Mindcode
print("One\[red][]nTwo");
printflush(message1);
```

which finally produces

```
One\nTwo
```

This is because the square brackets are used co encode color (see the next paragraph). The `[]` cancels `[red]`, and together they split apart `\` and `n` in such a way the message block doesn't recognize them anymore.

### Setting text color

The color of the text can be set by entering the color in square brackets. Colors can be specified using predefined color names (in uppercase or lowercase, but not mixed-case), or by entering the color in `#RRGGBB` or `#RRGGBBAA` form. The `[]` sequence then restores the previous color. It is therefore possible to use

```Mindcode
println("[red]This is red");
println("[green]This is green");
println("[]This is red again");
printflush(message1);
```

to print colored texts in the colors indicated in the example.

<details><summary>Show a full list of predefined color names.</summary>

```
clear
black
white
light_gray
gray
dark_gray
light_grey
grey
dark_grey
blue
navy
royal
slate
sky
cyan
teal
green
acid
lime
forest
olive
yellow
gold
goldenrod
orange
brown
tan
brick
red
scarlet
crimson
coral
salmon
pink
magenta
purple
violet
maroon
```

</details>

When a color name or color code is not recognized in square brackets, the text including the brackets is left as is. A sequence of two left square brackets, i.e., `[[`, is always printed as `[` e.g.:

```Mindcode
println("The color is [[red]");
println("The state is [[alarm]");
printflush(message1);
```

produces

```
The color is [red]
The state is [alarm]
```

In the second case, the doubling of square bracket is not strictly necessary, because `alarm` isn't recognized as a color name.

### Displaying icons 

It is also possible to use built-in Mindustry icons in the `print` instruction, for example

```Mindcode
println(ITEM_COAL, ": ", vault1.@coal);
println(ITEM_LEAD, ": ", vault1.@lead);
println(ITEM_SAND, ": ", vault1.@sand);

print($"Using $UNIT_MEGA to transport items...\n"); 
```

Supported Mindustry icons are available through built-in String constants containing them. For a complete list of available icons, see [Built-in icons](SYNTAX-1-VARIABLES.markdown#constants-representing-built-in-icons), for a possibility of embedding icons into string constant see [String concatenation](SYNTAX-2-EXPRESSIONS.markdown#string-concatenation).

## Printing values

When printing numbers, Mindustry prints the full representation of a number. It might sometimes be cumbersome, as fractions can produce a lot of digits. To avoid this, use the `floor`, `ceil`, or (in Mindustry 8) the `round` function:

```Mindcode
start_time = @time;
// do some stuff
duration = @time - start_time;
println($"Elapsed: ${floor(duration)} ms");
```

When a number you're printing is smaller than `0.00001` (in absolute value), Mindustry will print zero (`0`) instead. The same formatting is used to display the value of a variable in the _Vars_ dialog in Mindustry UI. It is necessary to be aware that a number which was printed as `0` doesn't necessarily equal to zero.

Mindcode provides the `printExactFast` and `printExactSlow` functions in the `printing` library, which can be used to output the exact numerical values of variables to the text buffer. However, these functions require Mindustry Logic 8 to compile.

## Using units

Mindustry allows your processors to control existing units. Among other things, you can use units to mine, attack, build, heal or move things around. Using units isn't that complicated, but it isn't always immediately noticeable what needs to be done or what went wrong with your code if it doesn't work as expected. Note that this chapter contains just the very basic information; you'll need to build upon these techniques in your own way to create a truly robust solution.

Here are the basic principles governing units:

* Each processor can control at most one unit at a time; this unit is acquired through `ubind()` an is available as the `@unit` variable.
* The `ubind()` function, when run repeatedly, returns units of the given type one by one; after cycling through all units, it starts again from the first one. 
* It is possible to store a bound unit in a variable and bind it again later: `savedUnit = @unit; ...; ubind(savedUnit);`. This way, several units can be stored in variables (or an [internal array](SYNTAX-1-VARIABLES.markdown#internal-arrays)).
* On the other hand, it is not possible to store units in memory cells and memory banks.
* Binding a unit doesn't make that unit controlled by the processor. This only happens after using one of unit-related instructions: [`ucontrol`](FUNCTIONS-80.markdown#instruction-unit-control), [`ulocate`](FUNCTIONS-80.markdown#instruction-unit-locate) or [`uradar`](FUNCTIONS-80.markdown#instruction-unit-radar).
  * The `within()` function which maps to `ucontrol within`, makes the unit controlled by the processor.
  * Sensing unit's properties, such as `@x`, `@y`, `@dead` or `@controller` doesn't make the unit controlled by the processor.
* Binding a unit to a processor doesn't make the unit exclusive to that processor: other processors might bind the same unit, or a player might take over the unit and control it manually, "stealing" it.
* A unit controlled by the processor (or stored in a variable) may die. To detect the unit is dead, sense its `@dead` property (e.g., `@unit.@dead`).
* When a command is issued to a unit using `ucontrol`, the processor doesn't wait for the unit to complete the command. Explicit checks to see if the unit has finished the command are sometimes necessary.

### Binding units

> [!TIP]
> The `units` system library contains functions you can use to search for and bind free units. For more information, see [System library](SYSTEM-LIBRARY-UNITS.markdown).
>
> The unit functions in the system library are based on the principles described here.

Unit needs to be bound to the processor to be controlled through it, using the `ubind` instruction (and a corresponding `ubind()` function). Only one unit can be bound at a time. All [commands controlling the unit](FUNCTIONS-80.markdown#instruction-unit-control) are then sent to the bound unit.

You need to specify a unit type when binding a unit (e.g., `ubind(@poly)`). If there is at least one unit of the requested type, it is bound to your processor, and it is stored in a built-in variable named `@unit`. (The `ubind()` function also returns this value for convenience.) If no unit of the requested type exists, no unit is bound and `@unit` contains `null`. When you call `ubind()` again with the same unit type, a next available unit is returned. Once you've gone through all existing units, the first one is bound again.

The `@unit` variable is special, as it always contains the unit currently bound to the processor. You can store this value in another variable. One possible reason to do that is that you can use such a variable to determine you've already encountered all existing units:

```Mindcode
var count;
var firstUnit = ubind(@poly);
if firstUnit != null then
    count = 1;
    while ubind(@poly) != firstUnit do
        count += 1;
    end;
else
    count = 0;
end;

print($"There are $count active poly(s).");
printflush(message1);
```

This code counts the current number of active polys by binding all of them; as soon as the first one is encountered again, we know we've seen them all. There is a slight problem with this code: if the unit stored in `firstUnit` is destroyed, it will never be bound again, and we'll end up in an infinite loop. More on detecting destroyed units 
later.

There's one additional, crucial thing you can do with a unit stored in a variable: it can be used to bind that unit again. For example:

```Mindcode
var poly = ubind(@poly);     // We just assume the units exist
var mega = ubind(@mega);

var angle = 0;
while true do
    controlUnit(poly);
    controlUnit(mega);
end;

def controlUnit(myUnit)
    ubind(myUnit);
    move(@thisx + 10 * sin(angle), @thisy + 10 * cos(angle));
    print($"Currently bound unit is ${@unit}.");
    printflush(message1);
    wait(0.8);
    angle += 45;
end;
```

### Flagging units

Each unit has a flag, which can hold a numeric value (integer or decimal values). Initially, each unit has a zero flag. A lot of code that can be seen on the internet uses flags to mark units that are in use so that other processors know to avoid them. Typical code might look like this:

```Mindcode
def findFreeUnit(unitType, markFlag)
    do
        ubind(unitType);
    while @unit.@flag !== 0;      

    // If no unit was found, @unit would be null and so would be @unit.@flag. 
    // The strict comparison (!==) ensures the loop will only end when a live unit with zero flag is found  

    flag(markFlag);
    return @unit;
end;

flag = rand(10**10);
myUnit = findFreeUnit(@mono, flag);
```

Later on, you might loop through all units and use the particular value of the flag to recognize those you acquired. Flags are typically generated randomly so that two processors running the same code do not steal each other's units.

There are two downsides to this arrangement:

* If a processor for some reason stops controlling the unit without clearing its flag, all other processors will consider that unit used and won't reuse it, potentially leading to a shortage of available units.
* The flag actually allows you to store various information about the unit state, for example, which particular task it was assigned to. It is possible to encode the flag and the state into one numeric value, although it requires more computations and makes the code at least a bit slower.
  
### Unit controllers

The alternative to using flags is querying the unit to see whether it is free or actively controlled. When a unit is free, the `@unit.@controlled` property returns `0`. When the value is nonzero, the unit is controlled, either by a processor, or directly by a player, or by being part of the units commanded indirectly by the player (different values are assigned to each of these possibilities).

A wee bit enhanced `findFreeUnit()` function using the `@controlled` property might look like this:

```Mindcode
def findFreeUnit(unitType, initialFlag)
    // Keep looking for unit until one is found
    while true do
        ubind(unitType);
        if @unit == null then
            print($"No unit of type $unit_type found.");
        elsif @unit.@controlled != 0 then
            print($"Looking for a free $unit_type...");
        else
            flag(initialFlag);		// Mark unit as active
            return @unit;
        end;
        printflush(message1);
    end;
end;
```

We're still flagging the unit. Firstly, it assigns the initial state to it right off the bat, and secondly, it will signal to other processors that might use flags to recognize free units that this one is busy.

The other property is `@unit.@controller`. This returns the processor that is actively controlling the unit, or `null` if no processor controls that unit. Use this property to detect that your unit was lost:

```
if @unit.@controller != @this then
    // We lost our unit. Immediatelly get a new one.
    findFreeUnit(@mega, STATE_INIT);
end;
```

Unit can become lost if a player or another rogue processor takes it over, so it is definitely useful to guard yourself against this possibility.

A unit becomes controlled by the processor when it is issued a command. Most [ucontrol instructions](FUNCTIONS-80.markdown#instruction-unit-control) will do so. Notably, setting a flag marks the unit as controlled while querying the flag or other properties of the unit won't.

If a unit is not issued commands from a processor for some time, it becomes free again and both `@controlled` and `@controller` properties are cleared. My tests show it takes about 10 seconds:

```Mindcode
ubind(@poly);
start = @time;
flag(10);
do while @unit.@controller == @this;
print($"Unit was controlled for ${floor(@time - start)} ms");
printflush(message1);
```

> [!NOTE]
> Unit controllers aren't stored in a map file. When a game is loaded from a save, or when a sector is changed in a campaign, the unit controllers are reset. If your logic depends on identifying owned units using `@controller`, the situation where all units are suddenly lost needs to be accounted for.

### Detecting destroyed units

When a unit is destroyed, the variable that pointed to it keeps its original value. It is possible to detect a destroyed unit (as well as a destroyed building, actually) by querying the `@dead` property. A value of `0` (or `false`) means the unit is alive, a value of `1` (or `true`) means it is, well, dead as a parrot.

> [!TIP]
> The `@dead` property always returns either `0` or `1`. Specifically, invoking `var.@dead` returns 1 when `var` happens to be `null`.

```
if @unit.@controller != @this or @unit.@dead == 1 then
    // We lost our unit. Immediatelly get a new one.
    findFreeUnit(@mega, STATE_INIT);
end;
```

### Commanding units

Once a unit is bound to the processor, it can be issued commands using the [`ucontrol` instruction](FUNCTIONS-80.markdown#instruction-unit-control). There's a concern, though: the processor doesn't wait for the unit to complete the command you've given to it but continues executing your program. If another command is issued to the unit before the previous command has been finished, the unit will abandon the previous command and will start executing the new one. For example:

```Mindcode
require units;

findFreeUnit(@mono, 123456789);
while true do
    move(10, 10);
    move(20, 20);
end;
```

It might appear that this program will bind a single unit (mono) and will make it patrol between coordinates (10, 10) and (20, 20). What will actually happen is that the mono will travel close to the coordinates given. However, then it will stay practically in one place, as the commands to move to one point and to the other point will alternate too fast.

The solution to this problem is to detect whether the unit has completed the command, and only when it did, issue another command. In case of movements, you need to verify the unit has arrived at the destination before issuing a new command:

```Mindcode
require units;

def moveTo(x, y)
    do
        move(x, y);
    while !within(x, y, 1);
end;

findFreeUnit(@mono, 123456789);

while true do
    moveTo(10, 10);
    moveTo(20, 20);
end;
```

The above program keeps issuing the `move` command, until the unit is within one tile from the intended destination. Issuing the command repeatedly, instead of just waiting is important: if you don't issue a command to a unit for ten seconds, the unit stops executing the last logic-issued action and becomes AI-controlled instead.   

### Discarding unwanted items

Units can carry only one type of items at a time. It might therefore be sometimes necessary to discard items that are no longer needed. The simple, but not-so-obvious way of doing so is to drop the item into the air:

```Mindcode
itemDrop(@air, @unit.@totalItems);
```

In case of dropping things into the air, all items are always dropped, regardless of the specified amount. I'd still suggest specifying the correct amount, just in case something will change in the future.     

## Using buildings

Mindustry allows your processors to control and receive information from any allied building only if they are linked to your processor, which can be done on a limited processor's range. Otherwise, you can only use units to get building and block information. Getting this data is not just limited to your buildings. You can also obtain enemy building information, which is not possible with linking to your processor since you can't link to an enemy building.

### Locating the core

One of the first things you'll probably want to do is to locate your core, as it is the most important building in the game. Doing so without a processor placed next to the core requires a unit. As soon as you bind a unit, issue this command:

```Mindcode
found = ulocate(:building, :core, false, out core_x, out core_y, out core);
```

Let's look at each argument here: 

* `found` is a variable that will receive the result of the operation, `true` if the code was found, `false` if it wasn't.  If you don't need it, use just `ulocate(:building, :core, false, out core_x, out core_y, out core)`.
* `ulocate` is the name of the function we're calling.
* `building` and `core` are constant values that specify what we are looking for. They must be specified exactly like this; you cannot, for example, store them in variable (e.g., `type = core; ulocate(building, type, false, out core_x, out core_y, out core)` won't work).
* `false` specifies we're looking for our own core. Put `true` if you want to locate enemy one.
* `core_x` and `core_y` are variables that will receive the position of the core on the map. Since they receive a value produced by the instruction, they are "output" arguments and need to be marked with the `out` keyword. Use them to send your units there. If you don't need them, you can omit them.
* `core`, again an output argument, receives the building itself, and it can be used to query its state:

```
findFreeUnit(@poly, 1);
ulocate(:building, :core, false, , , out core);
println("Silicon status: ", core.@silicon);
printflush(message1);
```

which will tell you how bad your silicone situation is.

### Obtaining buildings

You can collect building and block information with the `getBlock` command. Do not confuse with the `getblock` command for world processors with only lowercase letters! The `getBlock` command retrieves the building, floor type or block type at the given coordinates if the unit is within the radius of the position (unit range).

```Mindcode
building = getBlock(x, y, out type, out floor);
```

Let's look at each argument here:
* `getBlock` it’s a function that itself returns nothing, but the arguments are returned with a value 
* `building` variable which receives the building at given location. If there is no building, it will become `null`.
* `x` and `y` coordinates
* `type` variable which receives the block type. If it’s a building, it receives the proper type, such as `@vault`. If there is free space, receives `@air`. If it's a solid environmental block, receives `@solid`
* `floor` variable which receives the floor type.

If you don't need some arguments, just omit them: `getBlock(x, y, , out floor)`. If a unit is out of range, or if the block doesn’t exist, all return values are `null`.

In the example code below, the flare finds the nearest enemy turret and approaches it. If the unit is within range of the turret, checks the building variable. If it's `null`, turret has been destroyed, if not, it still exists. If `ulocate` return 0, no enemy turret was found. Another example [here](http://mindcode.herokuapp.com/?s=upgrade-conveyors).

```Mindcode
// This example adheres to the strict syntax:
// All variables are explicitly declared, code is enclosed in code block.

// A system library for manging units
require units;

// Expect a message1 block linked to this processor
linked message1;

begin
    // Function from the system library: gives us the unit
    waitForFreeUnit(@flare, 1);
    var unitRange = @unit.@range;
    var turretX, turretY;

    // Locating nearest enemy turret. If found, turret != null and (turretX, turretY) are it's coordinates
    var turret = ulocate(:building, :turret, true, out turretX, out turretY);

    // If the unit still controlled by this proc
    while @unit.@controller == @this do
        if turret == null then
            println("There are no more turrets.");
            printflush(message1);
            wait(3);
            end();
        end;
        approach(turretX, turretY, unitRange);
        if within(turretX, turretY, unitRange) == 1 then
            var block = getBlock(turretX, turretY);             // We don't need a block type or floor type, only a building
            if block == null then
                println($"Enemy turret at $turretX, $turretY has been destroyed!");
                printflush(message1);
                wait(3);
                turret = ulocate(:building, :turret, true, out turretX, out turretY);        // Finding another enemy turret
            else
                println($"Enemy turret at $turretX, $turretY still exists");
            end;
        else
            println($"Searching for enemy turret at $turretX, $turretY...");
        end;
        printflush(message1);
    end;
end;
```

---

[« Previous: Mindcode Performance Tips](PERFORMANCE-TIPS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown)
