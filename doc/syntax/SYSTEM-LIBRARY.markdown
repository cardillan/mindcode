# System library

Mindcode comes equipped with a system library. System library is stored in several files. To use a particular file from the system library, the `require` statement needs to be used:

```
require blocks;   // Use the 'blocks' system library
```

System libraries contain functions and sometimes constants that can be used by a Mindcode program. The following system libraries are provided:

* `graphics`: additional graphics functions. Requires the Mindustry Logic 8 instruction set.
* `printing`: functions for outputting formatted numbers. Some functions require the Mindustry Logic 8 instruction set.
* `blocks`: block-related functions (just the `findLinkedBlocks` function at this moment).
* `units`: functions for searching and binding available units of required type.
* `math`: mathematical functions.

The system library is an experimental feature. The functions provided by the library and the mechanism for its inclusion in your program may change in future releases.

> [!IMPORTANT]
> Using a program parameter or constant with a name matching a name of one of the library variables causes a compilation error. To avoid this problem, all function parameters in system libraries start with an underscore. Do not declare program parameters or constants starting with an underscore.
>
> This limitation of the system library will be removed in one of the future releases.

## Blocks library

To use the Blocks library, use the `require blocks;` statement.

### findLinkedBlocks

**Definition:** `inline void findLinkedBlocks(title, message, linkMap...)`

Searches blocks linked to the processor for blocks of requested types, and assigns them to given variables if found.
The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types, instead of using the predefined link name.
By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name,
such as `message1` or `switch1`. The function cannot handle situations when two blocks of the same type are needed
(e.g. two switches), but can handle situations where a single variable can accept multiple block types
(e.g. either memory cell or memory bank).

Function outputs status information while it is running to the block passed in the `message` parameter.
When a `@message` block is among the required types and is found, it is used instead of the `message` parameter.

Inputs and outputs:

- `title`: title to be used as part of the status information.
- `message`: initial block to use to output status information. Typically `message1`.
- `linkMap`: definition of the required blocks. Each blocks needs four variables:
  - `requested`: type of the requested block, e.g. `@switch`.
  - `name`: name of the block to use as part of the status information.
  - `out variable`: variable to receive the block
  - `required`: if `true`, the function will wait until a block of given type is linked to the processor. If `false`, the function doesn't wait.

Example of a call to this function:

```
require blocks;

findLinkedBlocks("Example program.\nTrying to locate linked blocks", message1,
    @large-logic-display,   "Display",  out display,    true,
    @message,               "Message",  out message,    false,
    @switch,                "Switch",   out switch,     false,
    @memory-bank,           "Memory",   out memory,     true,
    @memory-cell,           "Memory",   out memory,     true
);
```

When the function call ends, the `display` and `memory` variables are set to a large display or memory cell/memory
bank respectively. `message` and `switch` are set if corresponding blocks are linked to the processor,
otherwise they're `null`.

## Graphics library

To use the Graphics library, use the `require graphics;` statement. The Graphics library uses Mindustry Logic 8
instructions and therefore also requires `#set target = ML8A;` statement.

The provided library functions use transformations to rotate, invert or scale graphics output as needed
for each display (large or small). Transformations are additive, so it is suggested to call `reset()`
at the beginning of each program that uses transformations to clean up possible transformations from earlier runs.

The parameters used for transformations must be adapted to the size of the output display being drawn.
The transformation functions therefore exist in versions for both logic displays and large logic displays,
plus a version which automatically detects the display type from a block passed in as a parameter.

### displaySize

**Definition:** `def displaySize(display)`

Returns the actual display size based on the type of display passed in as an argument.
When the passed-in argument is not a display, the processor is stopped.

### rotateLeftSmall

**Definition:** `void rotateLeftSmall()`

Rotates the output to the left (counterclockwise) by 90 degrees for a small display.

### rotateRightSmall

**Definition:** `void rotateRightSmall()`

Rotates the output to the right (clockwise) by 90 degrees for a small display.

### upsideDownSmall

**Definition:** `void upsideDownSmall()`

Rotates the output by 180 degrees (upside down) by 90 degrees for a small display.

### flipVerticalSmall

**Definition:** `void flipVerticalSmall()`

Flips the output vertically (along the Y axis) for a small display.

### flipHorizontalSmall

**Definition:** `void flipHorizontalSmall()`

Flips the output horizontally (along the X axis) for a small display.

### rotateLeftLarge

**Definition:** `void rotateLeftLarge()`

Rotates the output to the left (counterclockwise) by 90 degrees for a large display.

### rotateRightLarge

**Definition:** `void rotateRightLarge()`

Rotates the output to the right (clockwise) by 90 degrees for a large display.

### upsideDownLarge

**Definition:** `void upsideDownLarge()`

Rotates the output by 180 degrees (upside down) by 90 degrees for a large display.

### flipVerticalLarge

**Definition:** `void flipVerticalLarge()`

Flips the output vertically (along the Y axis) for a large display.

### flipHorizontalLarge

**Definition:** `void flipHorizontalLarge()`

Flips the output horizontally (along the X axis) for a large display.

### rotateLeft

**Definition:** `void rotateLeft(display)`

Rotates the output to the left (counterclockwise) by 90 degrees for the given display.

### rotateRight

**Definition:** `void rotateRight(display)`

Rotates the output to the right (clockwise) by 90 degrees for the given display.

### upsideDown

**Definition:** `void upsideDown(display)`

Rotates the output by 180 degrees (upside down) by 90 degrees for the given display.

### flipVertical

**Definition:** `void flipVertical(display)`

Flips the output vertically (along the Y axis) for the given display.

### flipHorizontal

**Definition:** `void flipHorizontal(display)`

Flips the output horizontally (along the X axis) for the given display.

### scaleSmallToLarge

**Definition:** `void scaleSmallToLarge()`

Scales the graphics output so that an output that targets a small display gets displayed
over the entire area of a large display.

### scaleLargeToSmall

**Definition:** `void scaleLargeToSmall()`

Scales the graphics output so that an output that targets a large display gets displayed
over the entire area of a small display.

## Math library

To use the Math library, use the `require math;` statement.

### distance

**Definition:** `def distance(x1, y1, x2, y2)`

Computes the distance between points (`x1`, `y1`) and (`x2`, `y2`).
Uses the `len` instruction for efficient hypotenuse calculation.

### round

**Definition:** `def round(x)`

Rounds the number to the closest integer. Halves are rounded up: `round(1.5)' gives '2` and `round(-1.5)` gives `1`.

### frac

**Definition:** `def frac(x)`

Returns the fractional part of the number. `frac(1.5)` gives `0.5`.

### sign

**Definition:** `def sign(x)`

Returns the sign of the number. The return value is `0` precisely when `x == 0`
(using the Mindustry Logic native comparison precision).

### signExact

**Definition:** `def signExact(x)`

Returns the sign of the number. The return value is `0` when the value of `x` is exactly zero.

### isZero

**Definition:** `def isZero(x)`

Returns `true` when `x` is precisely zero.

### isZero

**Definition:** `def isZero(x, precision)`

Returns `true` when the absolute value of x is less than `precision`.

### isEqual

**Definition:** `def isEqual(a, b, precision)`

Returns `true` if the two values differ by less than `precision`.

> [!NOTE]
> The equality operators in Mindustry (and, by extension, Mindcode), i.e. `==`, `!=`. `===` and `!==`
> consider numeric values equal when they differ by less than `0.000001`. The `isEqual` function allows
> to compare values using different precision.

### nullToZero

**Definition:** `def nullToZero(x)`

Converts the value of `x` to zero if it was `null`. Uses single instruction for the conversion,
and makes sure it won't be removed by the optimizer.

### sum

**Definition:** `inline def sum(x)`

Returns `x`. The function is a fallback case for the generic `sum` function taking a variable number of arguments.

### sum

**Definition:** `inline def sum(x1, x2, x...)`

Returns the sum of all given arguments.

### avg

**Definition:** `inline def avg(x)`

Returns `x`. The function is a fallback case for the generic `avg` function taking a variable number of arguments.

### avg

**Definition:** `inline def avg(x1, x2, x...)`

Returns the average of all given arguments.

### median

**Definition:** `def median(x)`

Returns `x`. The function is a fallback case for the generic `median` function taking a variable number of arguments.

### median

**Definition:** `def median(x1, x2)`

Returns the median of two values.

### median

**Definition:** `def median(x1, x2, x3)`

Returns the median of three values, using an optimized algorithm .

### median

**Definition:** `def median(x1, x2, x3, x4)`

Returns the median of four values, using an optimized algorithm .

### median

**Definition:** `def median(x1, x2, x3, x4, x5)`

Returns the median of five values, using an optimized algorithm .

### median

**Definition:** `inline def median(x...)`

Computes the median of the given arguments using a generic algorithm. The algorithm generates quite a large code
and is fairly slow, because Mindcode doesn't support internal memory arrays yet.

## Printing library

To use the Printing library, use the `require printing;` statement. Some of the Printing library functions use
Mindustry Logic 8 instructions and therefore require the `#set target = ML8A;` statement.

### formatNumber

**Definition:** `void formatNumber(number)`

**Note:** Function requires target `ML8A` or later.

Formats the number passed in as a parameter into the text buffer, using comma as thousands separator.
Fractional part of the number to be printed is ignored.

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function.
The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved. The number will be rendered
at the place of the lowest formatting placeholder.

Nulls are printed as 0.

> [!TIP]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing
> them using the `print()` function.

### printNumber

**Definition:** `void printNumber(number)`

**Note:** Function requires target `ML8A` or later.

Prints the number passed in as a parameter into the text buffer, using comma as thousands separator.
Fractional part of the number to be printed is ignored.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`.
The number will be printed at the end of the print buffer.

Nulls are printed as 0.

> [!TIP]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing
> them using the `print()` function.

See also [`formatNumber`](#formatnumber)

### printExact

**Definition:** `void printExact(n)`

Prints the value into the text buffer without rounding to the nearest integer value.
The function is primarily useful for debugging purposes to determine the actual value of variables.

**Note:** when printing the exact representation of numbers that are close to integer value, the Mindustry Logic
`print` instruction cannot be used, as it would print the rounded value. In such case a custom printing routine
is used, which can take around 50 steps to output the entire number.

> [!TIP]
> The `print` and `format` instructions applied to numerical values round the value to the nearest integer
> when they differ from the integer value by less than `1e-6`. This makes it impossible to directly print
> (with sufficient precision) numerical values close to integer values in general, and values close to zero
> in particular. For example, `print(10 ** -50)` prints `0`. On the other hand, `printExact(10 ** -50)`
> outputs `1E-50`.

> [!IMPORTANT]
> The mathematical operations used by `printExact` to output the value may introduce small additional numerical
> errors to the output value; for example `printExact(3.00000003)` outputs `3.000000029999999`.

## Units library

To use the Units library, use the `require units;`

### findFreeUnit

**Definition:** `def findFreeUnit(unit_type, initial_flag)`

Finds and binds a free unit of given type. When such a unit is found, it is flagged by the given initial flag.
If no free unit of given type can be found (either because none exists, or because all existing units are occupied),
returns immediately.

Inputs and outputs:

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to he freshly bound unit.
- returns the freshly bound unit, or `null` if no free unit of given type exists

The function doesn't use units that are controlled by a player or a different processor.

### findClosestUnit

**Definition:** `def findClosestUnit(x, y, unit_type, initial_flag)`

Searches for and binds a free unit of given type closest to the coordinates on the map given.
If no free unit of given type can be found (either because none exists, or because all existing units
are occupied), returns immediately.

Inputs and outputs:

- `x`, `y`: position of the map to compute unit distance relative to
- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to he freshly bound unit.
- returns the freshly bound unit closest to given coordinates, or `null` if no free unit of given type exists

The function doesn't use units that are controlled by a player or a different processor.

### waitForFreeUnit

**Definition:** `def waitForFreeUnit(unit_type, initial_flag)`

Finds and binds a free unit of given type. When such a unit is found, it is flagged by the given initial flag.
The function doesn't return until a free unit of the given type can be found,

Inputs and outputs:

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to he freshly bound unit.
- returns the freshly bound unit

The function doesn't use units that are controlled by a player or a different processor.

The status of the search is output to `SYS_MESSAGE`. Either set the message to an existing memory block to receive
the updates, or set `SYS_MESSAGE` to `null` to disable status updates (`const SYS_MESSAGE = null;` is also supported).
# Additional resources

The system library is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the library can be found [here](https://github.com/cardillan/mindcode/tree/main/compiler/src/main/resources/library).

---

[« Previous: Mindustry 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
