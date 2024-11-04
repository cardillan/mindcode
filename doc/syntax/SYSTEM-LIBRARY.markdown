# System library

Mindcode comes equipped with a system library. At this moment, the library is just a single source file, `sys.mnd`, which is automatically compiled together with each source file, both in the command line compiler and in the web app. The library is only included if the language target is 8A or higher, as it contains functions that build upon some of the instructions that will be added in the upcoming Mindustry 8 version.

To use the functions and constants provided by the system library in your program, you need to compile for Mindustry Logic 8 by including `#set target = ML8A` in your program.

The system library is an experimental feature. The functions provided by the library and the mechanism for its inclusion in your program may change in future releases. 

Individual functions in the library are documented here.

# Graphics transformations

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

# Blocks

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
#set target = ML8A;

findLinkedBlocks("Example program.\nTrying to locate linked blocks", message1,
    @large-logic-display,   "Display",  out display,    true,
    @message,               "Message",  out message,    false,
    @switch,                "Switch",   out switch,     false,
    @memory-bank,           "Memory",   out memory,     true,
    @memory-cell,           "Memory",   out memory,     true
);
```

When the function call ends, the `display` and `memory` variables are set to a large display or memory cell/memory bank respectively. `message` and `switch` are set if corresponding blocks are linked to the processor, otherwise they're `null`. 

# Units

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

# Text output

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

# Utility functions

## distance

**Definition:** `def distance(x1, y1, x2, y2)`

Computes the distance between points (`x1`, `y1`) and (`x2`, `y2`). Uses the `len` instruction for efficient hypotenuse calculation.

# Additional resources

The `sys.mnd` is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the file can be found [here](/compiler/src/main/resources/library/sys.mnd).

---

[« Previous: Mindustry 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
