# New functionality in Mindustry 8

Mindustry version 8 will hopefully be released soon. The expected new functionality of Mindustry Logic is already supported by Mindcode when setting the language target to 8 either by command-line argument, or by the `#set target = 8;` directive.

The Mindustry Logic v8 instruction set and corresponding Mindcode functions are described in [Function reference for Mindustry Logic 8.0](FUNCTIONS-80.markdown).

To run the code produced by Mindcode Logic 8, you need to use one of the development versions of Mindustry (a "bleeding-edge" version).

## Running development versions of Mindustry

> [!CAUTION]
> If you save a game or a campaign in a development version of Mindustry, you will no longer be able to open this game/campaign in older versions of the game, particularly in the official Mindustry 7 version. Additionally, there exists a possibility that a future version of Mindustry, including an official Mindustry release, won't be compatible with a particular development version you're using, meaning there would be no official release which would be able to read your game state.
> 
> It is strongly recommended to back up the state of your current game (Settings/Game Data/Export data) before running any development version of Mindustry.

How to run a development versions of Mindustry:

1. Download a Java installation package from https://github.com/Anuken/MindustryJreBuilds/releases/tag/v1.
2. Extract the package into a directory on your computer.
3. Obtain a development version of Mindustry from https://github.com/Anuken/MindustryBuilds/releases.
   - This link leads to all the versions of mindustry ever released. Use the latest version if you don't know which to use, or version [25368](https://github.com/Anuken/MindustryBuilds/releases/tag/25368).
   - Download the `Mindustry-BE-Desktop-<build number>.jar` file
4. Run the following command:

```
java.exe -jar Mindustry-BE-Desktop-<build number>.jar
```

- Use the full path to java.exe from the directory into which you've placed the files in step 2.
- Use the full path including a correct name of the file you've downloaded in step 3.

## Numeric literals

Mindustry now handles the numeric literals with double precision. Loss of precision, and inability to encode some numeric constants into mlog, no longer happen.  

## `format` instruction

Allows dynamic text formatting. It is possible to output special placeholders `{0}` to `{9}` into the text buffer. The `format` instruction then replaces a placeholder with actual value. Example:

```
print `Test: {1}{0}`
format `A`
format `B`
printflush message1
```

will output `Test: BA` to message1.

The `printf` function is designed to use this mechanism: `printf(fmt, a, b, c)` gets translated to

```
print fmt
format a
format b
format c
```

The upside is that `fmt` can be a variable and the formatting still works. The downside is that it generally isn't possible to optimize the `format` instructions, even if their parameters get resolved to a constant value (this would mean manipulating the placeholders in instructions that produced the text buffer, which is not universally possible with statical analysis). The existing compile time formatting (e.g. `println($"Position: $x, $y");`) will optimize to better code better if some or all of the parameters resolve down to constant values.

Apart from the `printf()`, Mindcode supports new `format()` function, which just outputs the `format` instruction for each of its arguments.

The `format` instruction searches the text buffer, looking for a placeholder with the lowest number. The first occurrence of this placeholder is then replaced by the value supplied to the `format`. This means that each format only replaces one placeholder: `printf("{0}{0}{1}", "A", "B")` followed by `printflush` therefore outputs `AB{1}` and not `AAB`. On the other hand, `printf("A{0}B", "1{0}2", "X")` outputs `A1X2B` - the placeholder inserted into the text buffer by the `format` instruction is used by the subsequent `format`. That opens up a lot of possibilities for building outputs dynamically; for example to print numbers with thousands separators:

```
#set target = 8;

// Formats a number into the text buffer, without external memory.
// The text buffer must not contain placeholders {0} and {1}. It must contain at least one other placeholder ({2} or higher).
def myFormatNumber(n)
    n = floor(n);
    if n < 0 then
        format("-{2}");     // Prepend the minus sign
        n = abs(n);
    end;
    while n > 999 do
        mod = n % 1000;
        // Insert placeholder for the next group, thousands separator, leading zeroes (if any) and a placeholder for this group.
        format(mod < 10 ? "{2},00{1}" : mod < 100 ? "{2},0{1}" : "{2},{1}");
        format(mod);
        n \= 1000;
    end;

    // Put the rest of the number into the remaining placeholder
    format(n);
end;

// Prints the number straight away
// The text buffer must not contain any placeholders lower than {3}.
def myPrintNumber(n)
    print("{2}");
    myFormatNumber(n);
end;

println("The numbers are {2} and {3}.");
myFormatNumber(floor(rand(1000000000)));
myFormatNumber(-floor(rand(1000000000)));
myPrintNumber(floor(rand(100000)));
```

Note: `formatNumber` and `printNumber` functions, identical to those above, are part of the [system library](SYSTEM-LIBRARY.markdown).

Existing Print Merging optimization is enhanced to use the new formatting mechanism where possible. For example, `println($"Minimum: $min, middle: $mid, maximum: $max")` in language target earlier than 8 compiles into

```
print `Minimum: `
print min
print `, middle: `
print mid
print `, maximum: `
print max
print `\n`
```

The new Print Merging optimization utilizing `format` saves three instructions by producing

```
print `Minimum: {0}, middle: {0}, maximum: {0}\n`
format min
format mid
format max
```

To prevent the new Print Merging optimization interfering with custom uses of the format instruction, it isn't used if a string constant containing a `{0}` substring, or some other specific substrings that might lead to the code creating `{0}` in the text buffer, are detected in the program. This leaves the placeholders `{1}` to `{9}` to be used freely by the user. It even allows interleaving the old-fashioned prints with the new `format` with no restrictions:

```
#set target = 8;
param a = 10;               // prevent a from being propagated as a constant
println("{2} {1}");         // if you use "{1} {0}" instead, the output will be the same, but different optimization will happen 
format("Before");
println($"Value: $a");
format("After");
```

This program will compile to

```
set a 10
print `{2} {1}\n`
format `Before`
print `Value: {0}\n`
format a
format `After`
```

and will output

```
After Before
Value: 10
```
The `format` instruction is also supported by the emulator, allowing you to experiment with the new print support right away in the web app.

## `printchar` instruction

The `printchar` instruction allows to output individual characters to the text buffer. The character is identified by its ASCII code. The ASCII code can be specified directly (`printchar(65);`), using Mindcode's character literal (`printchar('!');`) or using the `ascii()` function (`printchar(ascii("Today"));`).

When an object is used as instruction argument, the corresponding icon is output instead: `printchar(@mega);`.

```
#set target = 8;
for i in 0 ... 4 do
    itemType = lookup(item, i);
    printchar(itemType);
    amount = vault1.sensor(itemType);
    println(" ", amount);
end;
printflush(message1);
```

produces

```
print " {0}\n {0}\n {0}\n {0}\n"
sensor :amount vault1 @copper
format :amount
sensor :amount vault1 @lead
format :amount
sensor :amount vault1 @metaglass
format :amount
sensor :amount vault1 @graphite
format :amount
printflush message1
```

## Reading and writing linked processor variables

Mindustry 8 allows reading and writing variables of another processor, addressing them by name. This functionality is provided as an extension to the `read` and `write` instructions, which can take processors instead of memory cells/banks, and string values representing variable names instead of numerical index.

This functionality is accessible in Mindcode via the new `read()` and `write()` methods:

```
#set target = 8;
x = processor1.read("x");
y = processor1.read("y");
processor1.write(x + y, "z");
```

compiles into

```
read :x processor1 "x"
read :y processor1 "y"
op add *tmp2 :x :y
write *tmp2 processor1 "z"
```

Unlike external variables, access to other processor's variables is not limited to numeric values. All possible variable values are correctly transferred using these new instructions.    

### Remote functions and variables

Thanks to the ability to access other processors' variables, Mindcode now supports remote functions and variables, described [in this file](REMOTE-CALLS.markdown).   

## New drawing commands

### `print draw`

Prints the contents of the text buffer onto the display. So, instead of `printflush(message1);` you'll use `drawPrint(x, y, alignment); drawflush(display1);` to output the text on the display instead of a message block.

The new `draw print` instruction is represented by the `drawPrint()` function, as `print()` is already taken. 

The text is drawn using monospace font. Each character being printed represents one graphical operation. The graphics buffer has a capacity of 256 operations, after which it doesn't accept new ones and must be output using `drawflush`.

### Output transformations

New `draw rotate`, `draw translate` and `draw scale` can be used to rotate, translate or scale the graphical output. `draw reset` resets the transformations.

These instructions are represented by `rotate()`, `translate()`, `scale()` and `reset()` functions. By issuing `reset(); rotate(90); translate(0, -176);` it is possible to rotate the output by 90 degrees counterclockwise when drawing to the large display.

Figuring out the correct transformations isn't always easy. Issuing incorrect ones may result into all the output being drawn off-screen, which is then difficult to diagnose. For this reason, the following system functions are defined:

* `rotateLeftLarge()`, `rotateRightLarge()`, `upsideDownLarge()`: ensures the output to the large display will be rotated left, right or upside down by issuing `reset()`, `rotate()` and `translate()` as needed.
* `flipVerticalLarge()`, `flipHorizontalLarge()`: flips output to the large display vertically or horizontally.   
* `rotateLeftSmall()` and so on: the same, but intended for drawing on a small display.
* `rotateLeft(display)`: will inspect the display and apply the correct transformation for small/large ones.
* `smallToLarge()`, `largeToSmall()`: will set up scaling so that output to a small display will be mapped completely to the large one or vice versa. Especially the `largeToSmall()` transformation results into a graphics which is still nicely readable on a smaller display.

## New World processor instructions

### `weathersense`, `weatherset`

Allows to determine whether given weather type is active, or activate/deactivate it. Supported weathers are `@snowing`, `@rain`, `@sandstorm`, `@sporestorm`, `@fog` and `@suspend-particles`

### `message`

The instruction has a new output parameter that says whether displaying the message succeeded (it doesn't if another message is still being shown). Passing in special value `@wait` makes the instruction wait for the previous message to disappear.

### `explosion`

The instruction has a new parameter to specify whether the explosion triggers additional effects.

### `playsound`

Plays a sound, either on a specific position, or with a global volume and pan.

<details><summary>Show full list of available sounds.</summary>

`@sfx-artillery`
`@sfx-bang`
`@sfx-beam`
`@sfx-bigshot`
`@sfx-bioLoop`
`@sfx-blaster`
`@sfx-bolt`
`@sfx-boom`
`@sfx-break`
`@sfx-build`
`@sfx-buttonClick`
`@sfx-cannon`
`@sfx-click`
`@sfx-combustion`
`@sfx-conveyor`
`@sfx-corexplode`
`@sfx-cutter`
`@sfx-door`
`@sfx-drill`
`@sfx-drillCharge`
`@sfx-drillImpact`
`@sfx-dullExplosion`
`@sfx-electricHum`
`@sfx-explosion`
`@sfx-explosionbig`
`@sfx-extractLoop`
`@sfx-fire`
`@sfx-flame`
`@sfx-flame2`
`@sfx-flux`
`@sfx-glow`
`@sfx-grinding`
`@sfx-hum`
`@sfx-largeCannon`
`@sfx-largeExplosion`
`@sfx-laser`
`@sfx-laserbeam`
`@sfx-laserbig`
`@sfx-laserblast`
`@sfx-lasercharge`
`@sfx-lasercharge2`
`@sfx-lasershoot`
`@sfx-machine`
`@sfx-malignShoot`
`@sfx-mediumCannon`
`@sfx-minebeam`
`@sfx-mineDeploy`
`@sfx-missile`
`@sfx-missileLarge`
`@sfx-missileLaunch`
`@sfx-missileSmall`
`@sfx-missileTrail`
`@sfx-mud`
`@sfx-noammo`
`@sfx-pew`
`@sfx-place`
`@sfx-plantBreak`
`@sfx-plasmaboom`
`@sfx-plasmadrop`
`@sfx-pulse`
`@sfx-pulseBlast`
`@sfx-railgun`
`@sfx-rain`
`@sfx-release`
`@sfx-respawn`
`@sfx-respawning`
`@sfx-rockBreak`
`@sfx-sap`
`@sfx-shield`
`@sfx-shockBlast`
`@sfx-shoot`
`@sfx-shootAlt`
`@sfx-shootAltLong`
`@sfx-shootBig`
`@sfx-shootSmite`
`@sfx-shootSnap`
`@sfx-shotgun`
`@sfx-smelter`
`@sfx-spark`
`@sfx-spellLoop`
`@sfx-splash`
`@sfx-spray`
`@sfx-steam`
`@sfx-swish`
`@sfx-techloop`
`@sfx-thruster`
`@sfx-titanExplosion`
`@sfx-torch`
`@sfx-tractorbeam`
`@sfx-wave`
`@sfx-wind`
`@sfx-wind2`
`@sfx-wind3`
`@sfx-windhowl`

</details>

### `setmarker`, `makemarker`

These instructions serve for displaying markers on the map or minimap, as part of creating interactive map objectives (I believe).  

### `localeprint`

Adds a map locale property value to the text buffer.

---

[« Previous: System Library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Remote functions and variables »](REMOTE-CALLS.markdown)
