# System library

Mindcode comes equipped with a system library. System library is organized in several parts. To use a particular file from the system library, the `require` statement must be used:

```
require blocks;   // Use the 'blocks' system library
```

System libraries contain functions and sometimes constants that can be used by a Mindcode program. The following system libraries are provided:

* `graphics`: additional graphics functions. Requires the Mindustry Logic 8 instruction set.
* `printing`: functions for outputting formatted numbers. Requires the Mindustry Logic 8 instruction set.
* `blocks`: block-related functions (just the `findLinkedBlocks` function at this moment).
* `units`: functions for searching and binding available units of required type.
* `math`: mathematical functions.

The system library is an experimental feature. The functions provided by the library and the mechanism for its inclusion in your program may change in future releases. 

> [!IMPORTANT]
> Using a program parameter or constant with a name matching a name of one of the library variables causes a compilation error. To avoid this problem, all function parameters in system libraries start with an underscore. Do not declare program parameters or constants starting with an underscore.
> 
> This limitation of the system library will be removed in one of the future releases.
 
# Graphics library

To use the Graphics library, use the `require graphics;` statement. The Graphics library uses Mindustry Logic 8 instructions and therefore also requires `#set target = ML8A;` statement.  

The provided library functions use transformations to rotate, invert or scale graphics output as needed for each display (large or small). Transformations are additive, so it is suggested to call `reset()` at the beginning of each program that uses transformations to clean up possible transformations from earlier runs.

The parameters used for transformations must be adapted to the size of the output display being drawn. The transformation functions therefore exist in versions for both logic displays and large logic displays, plus a version which automatically detects the display type from a block passed in as a parameter.

## Display sizes

### Constants

Constants are defined for the small and large display sizes:  
 
* `const DISPLAY_SIZE_SMALL =  80;`
* `const DISPLAY_SIZE_LARGE = 176;`

### displaySize

**Definition:** `def displaySize(display)`

Returns the actual display size based on the type of display passed in as an argument. When the passed-in argument is not a display, the processor is stopped.

## Rotating graphics output

The following functions use graphics transformation to rotate the graphics output in multiples of 90 degrees. Functions that take a display as a parameter choose the transformation parameters to match the display size. When the passed-in argument is not a display, the processor is stopped.

### rotateRight

**Definitions:**

* `void rotateRightSmall()`
* `void rotateRightLarge()`
* `void rotateRight(display)`

Rotates the output to the right (clockwise) by 90 degrees for a small or a large display, or for a display given as an argument.

### rotateLeft 

**Definitions:**

* `void rotateLeftSmall()`
* `void rotateLeftLarge()`
* `void rotateLeft(display)`

Rotates the output to the left (counterclockwise) by 90 degrees for a small or a large display, or for a display given as an argument.

### upsideDown

**Definitions:**

* `void upsideDownSmall()`
* `void upsideDownLarge()`
* `void upsideDown(display)`

Rotates the output by 180 degrees (upside down) for a small or a large display, or for a display given as an argument.

## Flipping the output along the X or Y axis

### flipVertical

**Definitions:**

* `void flipVerticalSmall()`
* `void flipVerticalLarge()`
* `void flipVertical(display)`

Flips the output vertically (along the Y axis) for a small or a large display, or for a display given as an argument.

### flipHorizontal

**Definitions:**

* `void flipHorizontalSmall()`
* `void flipHorizontalLarge()`
* `void flipHorizontal(display)`

Flips the output horizontally (along the X axis) for a small or a large display, or for a display given as an argument.

## Scaling the output

### scaleSmallToLarge

**Definition:** `def scaleSmallToLarge()`

Scales the graphics output so that an output that targets a small display gets displayed over the entire area of a large display. 

### scaleLargeToSmall

**Definition:** `def scaleLargeToSmall()`

Scales the graphics output so that an output that targets a large display gets displayed over the entire area of a small display. 

# Blocks library

To use the Blocks library, use the `require blocks;`

## findLinkedBlocks

**Definition:** `inline void findLinkedBlocks(title, message, linkMap...)`

Searches blocks linked to the processor for blocks of requested types, and assigns them to given variables if found. The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types, instead of using the predefined link name. By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name, such as `message1` or `switch1`. The function cannot handle situations when two blocks of the same type are needed (e.g. two switches), but can handle situations where a single variable can accept multiple block types (e.g. either memory cell or memory bank).

Function outputs status information while it is running to the block passed in the `message` parameter. When a `@message` block is among the required types and is found, it is used instead of the `message` parameter.

Inputs and outputs:

* `title`: title to be used as part of the status information.
* `message`: initial block to use to output status information. Typically `message1`.
* `linkMap`: definition of the required blocks. Each blocks needs four variables:
  * `requested`: type of the requested block, e.g. `@switch`.
  * `name`: name of the block to use as part of the status information.
  * `out variable`: variable to receive the block
  * `required`: if `true`, the function will wait until a block of given type is linked to the processor. If `false`, the function doesn't wait.

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

When the function call ends, the `display` and `memory` variables are set to a large display or memory cell/memory bank respectively. `message` and `switch` are set if corresponding blocks are linked to the processor, otherwise they're `null`. 

# Units library

To use the Units library, use the `require units;`

## findFreeUnit

**Definition:** `def findFreeUnit(unit_type, initial_flag)`

Finds and binds a free unit of given type. When such a unit is found, it is flagged by the given initial flag. If no free unit of given type can be found (either because none exists, or because all existing units are occupied), returns immediately.

Inputs and outputs:

* `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
* `initial_flag`: initial flag to set to he freshly bound unit.
* returns the freshly bound unit, or null if no free unit of given type exists

The function doesn't use units that are controlled by a player or a different processor.

## findClosestUnit

**Definition:** `def findClosestUnit(x, y, unit_type, initial_flag)`

Searches for and binds a free unit of given type closest to the coordinates on the map given. If no free unit of given type can be found (either because none exists, or because all existing units are occupied), returns immediately.

Inputs and outputs:

* `x`, `y`: position of the map to compute unit distance relative to
* `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
* `initial_flag`: initial flag to set to he freshly bound unit.
* returns the freshly bound unit closest to given coordinates, or null if no free unit of given type exists

The function doesn't use units that are controlled by a player or a different processor.

## waitForFreeUnit

**Definition:** `def waitForFreeUnit(unit_type, initial_flag)`

Finds and binds a free unit of given type. When such a unit is found, it is flagged by the given initial flag. The function doesn't return until a free unit of the given type can be found, 

Inputs and outputs:

* `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
* `initial_flag`: initial flag to set to he freshly bound unit.
* returns the freshly bound unit

The function doesn't use units that are controlled by a player or a different processor.

The status of the search is output to `SYS_MESSAGE`. Either set the message to an existing memory block to receive the updates, or set `SYS_MESSAGE` to `null` to disable status updates (`const SYS_MESSAGE = null;` is also supported).

# Printing library

To use the Printing library, use the `require printing;` statement. The Printing library uses Mindustry Logic 8 instructions and therefore also requires `#set target = ML8A;` statement.

## formatNumber

**Definition:** `void formatNumber(n)`

Formats the number passed in as a parameter into the text buffer, using comma as thousands separator. Fractional part of the number to be printed is ignored. 

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function. The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved.

The number will be rendered at the place of the lowest formatting placeholder.

> [!TIP]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing them using the `print()` function.

## printNumber

**Definition:** `void printNumber(n)`

Prints the number passed in as a parameter, using comma as thousands separator. Fractional parts of the number to be printed is ignored.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`. The number will be printed at the end of the print buffer.

> [!TIP]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing them using the `print()` function.

See also [`formatNumber`](#formatnumber)

# Math library

To use the Utils library, use the `require math;` statement.

The library contains various mathematical functions.

## distance

**Definition:** `def distance(x1, y1, x2, y2)`

Computes the distance between points (`x1`, `y1`) and (`x2`, `y2`). Uses the `len` instruction for efficient hypotenuse calculation.

## round

**Definition:** `def round(x)`

Rounds the number to the closest integer. Halves are rounded up: `round(1.5)' gives '2` and `round(-1.5)` gives `1`.

## frac

**Definition:** `def frac(x)`

Returns the fractional part of the number. `frac(1.5)` gives `0.5`.

## sign

**Definition:** `def sign(x)`

Returns the sign of the number. The return value is `0` precisely when `x == 0` (using the Mindustry Logic native comparison precision).

## signExact

**Definition:** `def sign(x)`

Returns the sign of the number. The return value is `0` when the value of `x` is exactly zero.

## isZero

Function has two definitions:

* `def isZero(x)`: returns `true` if `x` is precisely zero.
* `def isZero(x, precision)`: returns `true` if the absolute value of x is less than `precision`.

Note: the equality operators in Mindustry (and, by extension, Mindcode), i.e. `==`, `!=`. `===` and `!==` considers numeric values equal when they differ by less than `0.000001`. The `isZero` function allows to compare value to zero using different precision.   

## isEqual

**Definition:** `isEqual(a, b, precision)`

Returns `true` if the two values differ by less than `precision`.

Note: the equality operators in Mindustry (and, by extension, Mindcode), i.e. `==`, `!=`. `===` and `!==` considers numeric values equal when they differ by less than `0.000001`. The `isEqual` function allows to compare values using different precision.   

## printExact

**Definition:** `printExact(n)`

Prints the value into the text buffer without rounding to the nearest integer value. The function is primarily useful for debugging purposes to determine the actual value of variables.

**Note:** when printing the exact representation of numbers that are close to integer value, the Mindustry Logic `print` instruction cannot be used, as it would print the rounded value. In such case a custom printing routine is used, which can take around 50 steps to output the entire number.

> [!TIP] 
> The `print` and `format` instructions applied to numerical values round the value to the nearest integer when they differ from the integer value by less than `1e-6`. This makes it impossible to directly print (with sufficient precision) numerical values close to integer values in general, and values close to zero in particular. For example, `print(10 ** -50)` prints `0`. On the other hand, `printExact(10 ** -50)` outputs `1E-50`. 

> [!IMPORTANT]
> The mathematical operations used by `printExact` to output the value may introduce small additional numerical errors to the output value; for example `printExact(3.00000003)` outputs `3.000000029999999`.    

## nullToZero

**Definition:** `nullToZero(x)`

Converts the value of `x` to zero if it was `null`. Uses single instruction for the conversion, and makes sure it won't be removed by the optimizer.

## sum

Function has two definitions:

* `def sum(x)`: returns x (covers the degenerate case).
* `def sum(x1, x2, x...)`: returns the sum of all given arguments.

## avg

Function has two definitions:

* `def avg(x)`: returns x (covers the degenerate case).
* `def avg(x1, x2, x...)`: returns the average of all given arguments.

## median

Computes the median of given arguments. The function implements a specialized, fast code for up to five arguments. For more arguments a generic algorithm is used. The generic algorithm generates quite a large code and is fairly slow, because Mindcode doesn't support internal memory arrays (so far).  

**Definitions:**

* `def median(x)`
* `def median(x1, x2)`
* `def median(x1, x2, x3)`
* `def median(x1, x2, x3, x4)`
* `def median(x1, x2, x3, x4, x5)`
* `def median(x...)`

# Additional resources

The system library is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the library can be found [here](https://github.com/cardillan/mindcode/tree/main/compiler/src/main/resources/library).

---

[« Previous: Mindustry 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
