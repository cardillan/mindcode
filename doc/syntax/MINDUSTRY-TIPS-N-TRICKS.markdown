# Mindustry Tips and Tricks

## Formatting text

The only way to print text in Mindustry Logic is to use the `print` instruction (made available to Mindcode through 
various kinds of `print()` function) one or more times to construct the resulting string in a text buffer.
Each `print` instruction adds its argument, converted to string if necessary, to the end of the text buffer.
Every `printflush` instruction, even unsuccessful, clears the text buffer.

When a text is being printed into a message by using the `printflush` instruction, the entire string from the text buffer
is displayed in the message block. At this moment, the string is inspected for special character sequences that can
alter the appearance of the text in the message block. Two kinds of formatting are supported:
* Line breaks
* Setting text color

### Line breaks

When the `\n` sequence of characters is found in the string being printed, the `\n` sequence is replaced by a line break.
For example:

```
print("One\nTwo")
printflush(message1)
```
produces the following output:
```
One
Two
```

Note that the backslash character is only recognized as part of the `\n` sequence, it is not otherwise specially handled.
Specifically, it is not possible to encode it as `\\`, unlike many other programming languages. Therefore, the following
code snippet

```
print("One\\Two\\nThree")
printflush(message1)
```
produces the following output:
```
One\\Two\
Three
```

If you really want to output `\n` in the message block for whatever reason, you can use this trick:

```
print("One\[red][]nTwo")
printflush(message1)
```
which finally produces
```
One\nTwo
```

This is because the square brackets are used co encode color (see the next paragraph). The `[]` cancels `[red]`,
and together they split apart `\` and `n` in such a way the message block doesn't recognize them anymore.

### Setting text color

The color of the text can be set by entering the color in square brackets. Colors can be specified using
predefined color names (in uppercase or lowercase, but not mixed-case), or by entering the color in `#RRGGBB`
or `#RRGGBBAA` form. The `[]` sequence then restores the previous color. It is therefore possible to use

```
println("[red]This is red")
println("[green]This is green")
println("[]This is red again")
printflush(message1)
```

to print colored texts in the colors indicated in the example.

<details><summary>Show full list of predefined color names.</summary>

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

When a color name or color code is not recognized in square brackets, the text including the brackets is left as is.
A sequence of two left square brackets, i.e. `[[`, is always printed as `[` e.g.:

```
println("The color is [[red]")
println("The state is [[alarm]")
printflush(message1)
```

produces

```
The color is [red]
The state is [alarm]
```

In the second case, the doubling of square bracket is not strictly necessary, because `alarm` isn't recognized as a color name.

### Displaying icons 

It is also possible to use built-in Mindustry icons in the `print` instruction, for example

```mindcode
println(ITEM-COAL, ": ", vault1.coal)
println(ITEM-LEAD, ": ", vault1.lead)
println(ITEM-SAND, ": ", vault1.sand)

printf("Using $UNIT-MEGA to transport items...\n") 
```

Supported Mindustry icons are available through built-in String constants containing them. For a complete list of
available icons, see [Built-in icons](SYNTAX-1-VARIABLES.markdown#built-in-icons), for a possibility of embedding
icons into string constant see [String expressions](SYNTAX-2-EXPRESSIONS.markdown#string-expressions).

## Printing values

When printing numbers, Mindustry always prints the full representation of a number. It might be sometimes cumbersome,
as fractions can produce a lot of digits. To avoid this, use the `floor` or `ceil` function:

```
start_time = @time
do_some_stuff()
duration = @time - start_time
println("Elapsed: ", floor(duration), " ms")
```

When a number you're printing is smaller than `0.00001` (in absolute value), Mindustry will print zero (`0`) instead.
The same formatting is used to display value of a variable in the Vars dialog in Mindustry UI. There isn't a way to output
such a small value directly. It is necessary to be aware that a number which was printed as `0` doesn't necessarily have 
a zero value.

## Using units

Mindustry allows your processors to control existing units. Among other things, you can use units to mine, attack,
build, heal or move things around. Using units isn't that complicated, but it isn't always immediately apparent 
what needs to be done or what went wrong with your code, if it doesn't work as expected.

I'm laying out just the basic pointers here, you'll need to combine the techniques mentioned in your own way to 
create a truly robust solution. 

### Binding units

Unit needs to be bound to the processor to be controlled through it, using the `ubind` instruction (and a 
corresponding `ubind()` function). Only one unit can be bound at a time.
All [commands controlling the unit](FUNCTIONS_V7.markdown#instruction-ucontrol) are then sent to the bound unit.

You need to specify a unit type when binding a unit (e.g. `ubind(@poly)`). If there is at least one unit of the 
requested type, it is bound to your processor, and it is stored in a built-in variable named `@unit`. (The `ubind()` 
function also returns this value for convenience.) If no unit of the requested type exists, no unit is bound and 
`@unit` contains `null`. When you call `ubind()` again with the same unit type, a next available unit is returned. 
Once you've gone through all existing units, the first one is bound again.

The `@unit` variable is special, as it always contain the unit currently bound to the processor. You can store this 
value in another variable. One possible reason to do that is that you can use such variable to determine you've 
already encountered all existing units:

```
first_unit = ubind(@poly)
if first_unit != null
    count = 1
    while ubind(@poly) != first_unit
        count += 1
    end 
else
    count = 0
end

printf("There are $count active poly(s).")

printflush(message1)
```

This code counts the current number of active polys by binding all of them; as soon as the first one is encountered 
again, we know we've seen them all. There is a slight problem with this code: if the unit stored in `first_unit` is 
destroyed, it will never be bound again, and we'll end up in an infinite loop. More on detecting destroyed units 
later.

There's one additional, very important thing you can do with a unit stored in a variable: it can be used to bind 
that unit again. For example:

```
poly = ubind(@poly)     // We just assume the units exist
mega = ubind(@mega)

COUNT = 0
while true
    controlUnit(poly)
    controlUnit(mega)
end

def controlUnit(my_unit)
    ubind(my_unit)
    move(@this.x + 10 * sin(COUNT), @this.y + 10 * cos(COUNT))  // Do *something* with the unit
    printf("Currently bound unit is ${@unit}.")
    printflush(message1)
    wait(0.8)
    COUNT += 45
end
```

### Flagging units

Each unit has a flag, which can hold a numeric value (integer or decimal values). Initially, each unit has a zero 
flag. A lot of code that can be seen on the internet uses flags to mark units that are in use, so that other 
processors know to avoid them. Typical code might look like this:

```
def findFreeUnit(unit_type, mark_flag)
    do
        ubind(type)
    loop while @unit.flag !== 0      

    // If no unit was found, @unit would be null and so would be @unit.flag. 
    // The strict comparison (!==) ensures the loop will only end when a live unit with zero flag is found  

    flag(mark_flag)
    return @unit
end

flag = rand(10**10)
my_unit = findFreeUnit(@mono, flag)
```

Later on, you might loop through all units and use the particular value of the flag to recognize those you acquired.
Flag are typically generated randomly so that two processors running the same code do not steal each other's units.

There are two downsides to this arrangement:

* If a processor for some reason stops controlling the unit without clearing its flag, all other processors will 
  consider that unit used and won't reuse it, potentially leading to shortage of available units.
* The flag actually allows you to store various information about unit state, for example which particular task was 
  assigned to it. It is possible to encode the flag and the state into one numerical value, although it requires 
  more computations and makes the code at least a bit slower.

### Unit controllers

The alternative to using flags is querying the unit to see whether it is free or actively controlled. When a unit is 
free, the `@unit.controlled` property returns `0`. When the value is nonzero, the unit is controlled, either by
a processor, or directly by a player, or part of the player group (different values are assigned to each of these 
cases).

A wee bit enhanced `findFreeUnit()` function using the `controlled` property might look like this:

```
def findFreeUnit(my_unit, initial_flag)
    // Keep looking for unit until one is found
    while true
        ubind(my_unit)
        if @unit == null
            printf("No unit of type $my_unit found.")
        elsif @unit.controlled != 0
            printf("Looking for a free $my_unit...")
        else
            flag(initial_flag)		// Mark unit as active
            return
        end
        printflush(message1)
    end
end
```

We're still flagging the unit. First of all, it assigns the initial state to it right off the bat, and secondly, it 
will signal to other processors that might use flags to recognize free units that this one is busy. 

The other property is `@unit.controller`. This returns the processor that is actively controlling the unit, or 
`null` if no processor controls that unit. Use this property to detect that your unit was lost:

```
if @unit.controller != @this
    // We lost our unit. Immediatelly get a new one.
    findFreeUnit(@mega, STATE_INIT)
end
```

Unit can become lost if a player or another rogue processor takes over it, so it is definitely useful to guard 
yourself against this possibility.

Unit becomes controlled by the processor when it is issued a command. Most
[ucontrol instructions](FUNCTIONS_V7.markdown#instruction-ucontrol) will do so. Notably, setting a flag marks the 
unit as controlled while querying the flag or other properties of the unit won't.

If a unit is not issued commands from a processor for some time, it becomes free again and both `controlled` and 
`controller` properties are cleared. My tests show it takes about 10 seconds:

```
loops = 0
ubind(@poly)
start = @time
flag(10)
while @unit.controller == @this
    loops += 1      // while loop doesn't allow empty body as of now 
end
printf("Unit was controlled for $ ms", floor(@time - start))
printflush(message1)
```

### Detecting destroyed units

When a unit is destroyed, the variable that pointed to it keeps its original value. It is possible to detect a 
destroyed unit (as well as a destroyed building, actually) by querying the `dead` property. A value of `0` (or 
`false`) means the unit is alive, a value of `1` (or `true`) means it is, well, dead as a parrot.

When querying the `@unit.dead` property, you can possibly obtain three values
* `null` when no unit is actually bound,
* `0` when a unit is bound and alive,
* `1` when a unit is bound, but dead.

A single strict comparison (`===` or `!==`) can be used to recognize live unit from the other two alternatives. 
An improved way to detect units no longer available to processor therefore is:

```
if @unit.controller != @this or @unit.dead !== 0
    // We lost our unit. Immediatelly get a new one.
    findFreeUnit(@mega, STATE_INIT)
end
```

### Locating the core

One of the first thing you'll probably want to do is to locate your core, as it is the most important building in 
the game. Doing so without a processor placed next to the core requires a unit. As soon as you bind a unit, just 
issue this command:

```
found = ulocate(building, core, false, core_x, core_y, core)
```

Let's look at each argument here: 

* `found` is a variable that will receive the result of the operation, `true` if the code was found, `false` if it 
wasn't.  If you don't need it, use just `ulocate(building, core, false, core_x, core_y, core)`.
* `ulocate` is the name of the function we're calling.
* `building` and `core` are constant values that specify what are we looking for, They must be specified exactly 
  like this, you cannot, for example, store them in variable (e.g. `type = core; ulocate(building, type, false, 
  core_x, core_y, core)` won't work).
* `false` specifies we're looking for our own core. Put `true` if you want to locate enemy one.
* `core_x` and `core_y` are variables that will receive the position of the core on the map. Use them to send your 
  units there.
* `core` is the building itself, and it can be used to query its state:

```
findFreeUnit(@poly, 1)
ulocate(building, core, false, core_x, core_y, core)
println("Silicon status: ", core.silicon)
printflush(message1) 
```

will tell you how bad your silicone situation is.

### Discarding unwanted items

Units can carry only one type of items at a time. It might therefore be sometimes necessary to discard items that 
are no longer needed. The simple, but not-so-obvious way of doing so, is to drop the item into the air:

```
itemDrop(@air, @unit.totalItems)
```

In case of dropping things into the air, all items are always dropped, regardless of the specified amount. I'd still 
suggest specifying the correct amount, just in case something changes in the future.     

---

[« Previous: Schematics Refresher](TOOLS-REFRESHER.markdown)
