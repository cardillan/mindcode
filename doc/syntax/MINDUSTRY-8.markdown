# Using unreleased versions of Mindustry

[Mindustry 8 pre-release](https://github.com/Anuken/Mindustry/releases) is now available. The new functionality of Mindustry Logic is already supported by Mindcode when setting the language target to `8` either by command-line argument, or by the `#set target = 8;` directive.

The Mindustry Logic v8 instruction set and corresponding Mindcode functions are described in [Function reference for Mindustry Logic 8.1](FUNCTIONS-81.markdown).

To run the code produced by Mindcode Logic 8, you need to use one of the pre-release versions, or one of the development versions of Mindustry (a "bleeding-edge" version).

> [!CAUTION]
> If you save a game or a campaign in a pre-release or development version of Mindustry, you will no longer be able to open this game/campaign in older versions of the game. Additionally, there exists a possibility that a future version of Mindustry, including an official Mindustry release, won't be compatible with a particular development version you're using. There thus might be no official release that would be able to read your game state.
>
> It is strongly recommended to back up the state of your current game (Settings/Game Data/Export data) before running any pre-release or development version of Mindustry.

## Running a pre-release version

You can download and install the pre-release version in the same way as an official release, for example, from [Itch](https://anuke.itch.io/mindustry). 

## Running development versions of Mindustry

How to run a development version of Mindustry:

1. Download a Java installation package from https://github.com/Anuken/MindustryJreBuilds/releases/tag/v1.
2. Extract the package into a directory on your computer.
3. Download a development version of Mindustry from https://github.com/Anuken/MindustryBuilds/releases.
   - This link leads to all the versions of mindustry ever released. Use the latest version if you don't know which to use, or version [25368](https://github.com/Anuken/MindustryBuilds/releases/tag/25368).
   - Download the `Mindustry-BE-Desktop-<build number>.jar` file
4. Run the following command:

```
java.exe -jar Mindustry-BE-Desktop-<build number>.jar
```

- Use the full path to `java.exe` from the directory into which you've placed the files in step 2.
- Use the full path including the correct name of the file you've downloaded in step 3.

# New functionality in Mindustry 8

The changes that affect Mindcode the most are described here. For a comprehensive list of changes to Mindustry Logic, see [Changes to Mindustry Logic](https://github.com/cardillan/mindcode/wiki/Changes-to-Mindustry-Logic).  

## Literals and properties

### Numeric literals

Mindustry now handles the numeric literals with double precision. Loss of precision, and the inability to encode some numeric constants into mlog, no longer happen.  

### Named color literals

Color literals for named colors (e.g., `%[red]`), are also supported by Mindcode.

### Properties

A number of properties to be used with the `sensor` instructions were added. Mindcode supports using new properties out of the box, so no special support for these was needed.  

## Text output

### `format` instruction

The `format` instruction allows dynamic text formatting. It is possible to output special placeholders `{0}` to `{9}` into the text buffer. The `format` instruction then replaces a placeholder with actual value. Example:

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

The upside is that `fmt` can be a variable and the formatting still works. The downside is that it generally isn't possible to optimize the `format` instructions, even when their parameters get resolved to a constant value. (This would mean manipulating the placeholders in instructions that produced the text buffer, which is not universally possible with a static analysis.) The existing compile time formatting (e.g., `println($"Position: $x, $y");`) will optimize to better code better if some or all of the parameters resolve down to constant values.

Apart from the `printf()`, Mindcode supports new `format()` function, which just outputs the `format` instruction for each of its arguments.

The `format` instruction searches the text buffer, looking for a placeholder with the lowest number. The first occurrence of this placeholder is then replaced by the value supplied to the `format`. This means that each format only replaces one placeholder: `printf("{0}{0}{1}", "A", "B")` followed by `printflush` therefore outputs `AB{1}` and not `AAB`. On the other hand, `printf("A{0}B", "1{0}2", "X")` outputs `A1X2B` - the placeholder inserted into the text buffer by the `format` instruction is used by the subsequent `format`. That opens up a lot of possibilities for building outputs dynamically; for example, to print numbers with thousands separators:

```Mindcode
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

#### Print merging optimization

The [Print Merging optimization](SYNTAX-6-OPTIMIZATIONS.markdown#print-merging) was enhanced to use the new formatting mechanism where possible. For example, `println($"Minimum: $min, middle: $mid, maximum: $max")` in language targets earlier than `8` compiles into

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

```Mindcode
#set target = 8;
param a = 10;               // prevent a from being propagated as a constant
println("{2} {1}");         // if you use "{1} {0}" instead, the output will be the same, but different optimization will happen 
format("Before");
println($"Value: $a");
format("After");
```

This program will compile to

```mlog
set a 10
print "{2} {1}\n"
format "Before"
print "Value: {0}\n"
format a
format "After"
```

and will output

```
After Before
Value: 10
```

The `format` instruction is also supported by the emulator, allowing you to experiment with the new print support right away in the web app.

### `printchar` instruction

The `printchar` instruction allows outputting individual characters to the text buffer. The character is identified by its ASCII code. The ASCII code can be specified directly (`printchar(65);`), using Mindcode's character literal (`printchar('!');`) or using the `ascii()` function (`printchar(ascii("Today"));`).

When an object is used as instruction argument, the corresponding icon is output instead: `printchar(@mega);`.

The Print Merging optimization handles the `printchar` instructions when possible:

```Mindcode
#set target = 8;
for i in 0 ... 4 do
    itemType = lookup(:item, i);
    printchar(itemType);
    amount = vault1.sensor(itemType);
    println(" ", amount);
end;
printflush(message1);
```

produces

```mlog
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

To make using the `printchar()` function easier, [character literals](SYNTAX.markdown#character-literals) were added to Mindcode. 

## `select` instruction

Since the **v8 Build 150 Beta** pre-release, Mindustry supports the `select` instruction facilitating conditional assignments.

> [!NOTE]
> The Mindcode target which supports the `select` instruction is `8.1`.

Mindcode doesn't provide direct access to the select instruction, but optimizes conditional expressions [to use the `select` instruction](SYNTAX-6-OPTIMIZATIONS.markdown#select-optimization) when possible. Using the ternary operator with simple values (`variable = condition ? trueValue : falseValue`) in Mindcode typically results in the `select` instruction being used.

### Implementing strict nonequality using `select`

The `strictNotEqual` condition is supported neither in the `op`, nor in the `jump` instructions, and prior to the `select` instruction, this condition needs to be evaluated in two instructions. The `select` instruction allows to simplify it:

* `op strictNotEqual resul a b` can be expressed as `select result strictEqual a b false true`.
* `jump target strictNotEqual resul a b` can be expressed as `select @counter strictEqual a b @counter target`.

The `jump strictNotEqual` instruction can't be emulated using a `select` instruction when [symbolic labels](SYNTAX-5-OTHER.markdown#option-symbolic-labels) are used, as symbolic label can't be used with the `select` instruction. Furthermore, it is possible to deactivate this mechanism using the [`emulate-strict-not-equal` option](SYNTAX-5-OTHER.markdown#option-emulate-strict-not-equal).

## `read` and `write` enhancements

The `read` and `write` instructions were updated to support reading and writing data to other blocks and objects apart from memory blocks.

### Reading and writing processor variables

Mindustry 8 allows reading and writing variables of another processor, addressing them by name. This functionality is provided as an extension to the `read` and `write` instructions, which can take processors instead of memory cells/banks, and string values representing variable names instead of numerical index.

This functionality is accessible in Mindcode via the new `read()` and `write()` methods:

```Mindcode
#set target = 8;
x = processor1.read("x");
y = processor1.read("y");
processor1.write(x + y, "z");
```

compiles into

```mlog
read :x processor1 "x"
read :y processor1 "y"
op add *tmp2 :x :y
write *tmp2 processor1 "z"
```

Unlike external variables, access to another processor's variables is not limited to numeric values. All possible variable values are correctly transferred using these new instructions.    

#### Remote functions and variables

Thanks to the ability to access other processors' variables, Mindcode now supports remote functions and variables, as described [here](REMOTE-CALLS.markdown).   

### Reading characters from strings

Mindustry 8 supports accessing individual characters of string values as UTF-16 numeric values using the `read` instruction. Characters are indexed starting at `0`. The instruction returns `null` when the index is out of bounds.

This functionality is accessible in Mindcode via the [`char()` function](SYNTAX-4-FUNCTIONS.markdown#the-char-function).

#### Obtaining string length

A length of a string can be obtained by sensing it's `@size` property. This functionality is accessible in Mindcode either using the `@size` property, `sensor` function or the specialized [`strlen()` function](SYNTAX-4-FUNCTIONS.markdown#the-strlen-function).

#### Reading characters from message boxes

Similarly to strings, it is possible to read characters from message blocks using the `read` instruction. The mechanics is the same as in the case of strings: both `char()` and `strlen()` functions can be used with message blocks:

```Mindcode
#set target = 8;

print("ABCD");
printflush(message1);
println(strlen(message1));
println(char(message1, 0));
printflush(message2);
```

#### Data storage using strings 

The ability to access individual characters of string values is a convenient way to store data. Mindcode provides the [`encode()` function](SYNTAX-4-FUNCTIONS.markdown#the-encode-function) to easily encode data into strings, and also uses the functionality internally to implement [very space-efficient jump tables](SYNTAX-6-OPTIMIZATIONS.markdown#text-based-jump-tables).

### Reading and writing canvas pixels

Mindustry 8 allows reading and writing individual canvas pixels using `read` and `write`. Canvas is an Erekir-specific block, so this is only relevant when working with mixed tech or when creating world processors for Erekir maps. The indexes need to be in the `0 ... 64` range, and individual pixel values are in the `0 ... 8` range (there are only eight different colors supported by canvases).

## Graphic output

### `draw print`

This instruction prints the contents of the text buffer onto the display. So, instead of `printflush(message1);` you'll use `drawPrint(x, y, alignment); drawflush(display1);` to output the text on the display instead of a message block.

The new `draw print` instruction is represented by the `drawPrint()` function, as `print()` is already taken. 

The text is drawn using a monospace font. Each character being printed represents one graphical operation. The graphics buffer has a capacity of 256 operations, after which it doesn't accept new ones and must be applied using `drawflush`.

### Output transformations

New `draw rotate`, `draw translate` and `draw scale` can be used to rotate, translate or scale the graphical output. `draw reset` resets the transformations.

These instructions are represented by `rotate()`, `translate()`, `scale()` and `reset()` functions. By issuing `reset(); rotate(90); translate(0, -176);` it is possible to rotate the output by 90 degrees counterclockwise when drawing to the large display.

Figuring out the correct transformations isn't always easy. Issuing incorrect ones may result in all the output being drawn off-screen, which might be challenging to diagnose. For this reason, the following system functions are defined:

* `rotateLeftLarge()`, `rotateRightLarge()`, `upsideDownLarge()`: ensures the output to the large display will be rotated left, right or upside down by issuing `reset()`, `rotate()` and `translate()` as needed.
* `flipVerticalLarge()`, `flipHorizontalLarge()`: flips output to the large display vertically or horizontally.   
* `rotateLeftSmall()` and so on: the same, but intended for drawing on a small display.
* `rotateLeft(display)`: will inspect the display and apply the correct transformation for small/large ones.
* `smallToLarge()`, `largeToSmall()`: will set up scaling so that output to a small display will be mapped completely to the large one or vice versa. Especially the `largeToSmall()` transformation results into a graphics which is still nicely readable on a smaller display.

## Other standard processor instructions

Note: these new instructions map either to functions or to operators. When compiling for Mindustry 7, the new operators are available by default, and the new functions are available through system libraries `graphics` and `math`, implemented in a backwards compatible way. When the function is provided by the target processor, it is used instead of the library implementation. This way you can start using these operators and functions when compiling for Mindustry 7 and seamlessly transition to Mindustry 8 without having to update your code. 

### `unpackcolor`

Reverses the `packcolor` instruction. Prior to version 8, the same operation may be performed by the `unpackcolor()` function from the `graphics` library.  

### `op emod`

Positive modulo: like modulo, except when the divisor is positive and the dividend is negative, still returns a positive number. The instruction is mapped to the [`%%` operator](SYNTAX-2-EXPRESSIONS.markdown#multiplicative-operators). Prior to version 8, the operator is emulated using available instructions.

### `op ushr`

Unsigned right-shift operator. The difference between the signed (`>>`) and unsigned (`>>>`) right shift operators concerns negative numbers: the signed operator copies the value of the leftmost bit (the sign bit) to lower bits when shifting, while the unsigned operator shifts in zeroes.

The instruction is mapped to the [`>>>` operator](SYNTAX-2-EXPRESSIONS.markdown#shift-operators). Prior to version 8, the operator is emulated using available instructions.

### `op logn`

The instruction `op logn result a b` computes the logarithm of `a` in base `b`. Prior to version 8, the same operation may be performed by the `logn()` function from the `math` library.

### `op sign`

Provides the signum of the argument (`-1`, `0` or `1` for a negative, zero or positive argument). Prior to version 8, the same operation may be performed by the `sign()` function from the `math` library.

### `op round`

Rounds the argument to the closest integer. Prior to version 8, the same operation may be performed by the `round()` function from the `math` library.

## World processor instructions

### `weathersense`, `weatherset`

Allows determining whether a given weather type is active or activate/deactivate it. Supported weathers are `@snowing`, `@rain`, `@sandstorm`, `@sporestorm`, `@fog` and `@suspend-particles`

### `message`

The instruction has a new output parameter that says whether displaying the message succeeded (it doesn't if another message is still being shown). Passing in special value `@wait` makes the instruction wait for the previous message to disappear.

### `explosion`

The instruction has a new parameter to specify whether the explosion triggers additional effects.

### `playsound`

Plays a sound, either in a specific position on the world map or with a global volume and pan.

<details><summary>Show the full list of available sounds.</summary>

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

These instructions serve for displaying markers on the map or minimap, as part of creating interactive map objectives.  

### `localeprint`

Adds a map locale property value to the text buffer.

---

[« Previous: System Library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Remote functions and variables »](REMOTE-CALLS.markdown)
