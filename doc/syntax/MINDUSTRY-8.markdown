# New functionality in Mindustry 8

Mindustry version 8 will hopefully be released in November 2024. The expected new functionality of Mindustry Logic is already supported by Mindcode when setting the language target to 8A either by command-line argument, or by the `#set target = ML8A;` directive.

The instruction set and corresponding functions for Mindustry Logic v8 is available here: [Function reference for Mindustry Logic 8A](FUNCTIONS_V8A.markdown).

## Numeric literals

Mindustry now handles the numeric literals with double precision, eliminating possible loss of precision when encoding some values into mlog, and allowing a wider range of numeric values to be represented as a numeric literal.

Loss of precision, and inability to encode some numerical constants into mlog, no longer happen.  

## `format` instruction

Allows dynamic text formatting. It is possible to output special placeholders `{0}` to `{9}` into the text buffer. The `format` instruction will then replace a placeholder with actual value. Example:

```
print `Test: {1}{0}`
format `A`
format `B`
printflush message1
```

will output `Test: BA` to message1.

The `printf` function will be probably repurposed to use this mechanism: `printf(fmt, a, b, c)` will get translated to

```
print fmt
format a
format b
format c
```

Apart from the `printf`, Mindcode will support new `format()` function, which will just output the `format` instruction for each of its arguments.

The upside is that `fmt` can be a variable and the formatting still works. The downside is that it generally won't be possible to optimize the `format` instructions, even if their parameters get resolved to a constant value (this would mean manipulating the placeholders in instructions that produced the text buffer, which is either impossible, or way too complicated). The existing compile time formatting (e.g. `println($"Position: $x$y");`) will optimize better if the parameters resolve down to constant values.

The format instruction searches the text buffer, looking for a placeholder with the lowest number. The first occurrence of this placeholder is then replaced by the value supplied to the `format`. This means that each format only replaces one placeholder: `printf("{0}{0}{1}", "A", "B")` will therefore output `AB{1}` and not `AAB`, which might be expected. On the other hand, `printf("A{0}B", "1{0}2", "X")` will output `A1X2B` - the placeholder inserted into the text buffer by the `format` instruction is used by the subsequent `format`. That opens up a lot of possibilities for building outputs dynamically; for example to print numbers with thousands separators:

```
#set target = ML8A;

// Formats a number into the text buffer, without external memory.
// The text buffer must not contain placeholders {0} and {1}; it needs to start from {2}.
inline def formatNumber(n)
    if n < 0 then
        format("-{2}");
        n = -n;
    end;
    while n > 1000
        mod = n % 1000;
        // Insert placeholder for the next group, separator, leading zeroes (if any) and a placeholder for this group.
        format(mod < 10 ? "{2},00{1}" : mod < 100 ? "{2},0{1}" : "{2},{1}");
        format(mod);
        n \= 1000;
    end;

    // Put the reaining number into the remaining placeholder 
    format(n);
end;

// Prints the number straight away
// The text buffer must not contain any placeholders lower than {3}. 
inline def printNumber(n)
    print("{2}");
    formatNumber(n);
end;

println("The numbers are {2} and {3}.");
formatNumber(floor(rand(1000000000)));
formatNumber(-floor(rand(1000000000)));
printNumber(floor(rand(100000)));
```

Existing print merger will be enhanced to use the new formatting mechanism where possible. For example, `println($"Minimum: $min, middle: $mid, maximum: $max")` today compiles into

```
print `Minimum: `
print min
print `, middle: `
print mid
print `, maximum: `
print max
print `\n`
```

New print merger optimization utilizing `format` will save three instructions by producing

```
print `Minimum: {0}, middle: {0}, maximum: {0}\n`
format min
format mid
format max
```

To prevent the new print merger optimization interfering with custom uses of the format instruction, it won't be used if a string constant containing a `{0}` substring, or some other specific substrings that might lead to the code creating `{0}` in the text buffer, will be detected in the program. This leaves the placeholders `{1}` to `{9}` to be used freely by the user. It even allows interleaving the old-fashioned prints with the new `format`:

```
#set target = ML8A;
#set optimization = experimental;
param a = 10;               // prevent a from being propagated as a constant
println(`{1} {2}`);         // try `{0} {1}` instead - different optimization will happen 
format(`Before`);
println($`Value: $a`);
format(`After`);
```

This program will compile to

```
set a 10
print `{1} {2}\n`
format `Before`
print `Value: {0}\n`
format a
format `After`
```

and will output

```
Before After
Value: 10
```

The new print merging functionality is available on the experimental optimization level. The `format` instruction is also supported by the emulator, allowing to experiment with the new print support right away in the web app.

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

Allows to determine whether given weather type is active, or acivate/deactivate it. Supported weathers are `@snowing`, `@rain`, `@sandstorm`, `@sporestorm`, `@fog` and `@suspend-particles`

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

[« Previous: Function reference for Mindustry Logic 8A](FUNCTIONS_V8A.markdown) &nbsp; | &nbsp; [Next: System Library »](SYSTEM-LIBRARY.markdown)
