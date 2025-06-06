# System library

Mindcode comes equipped with a system library. System library is stored in several files. The `require` statement imports the library functions into your program:

```
require blocks;   // Use the 'blocks' system library
require units;    // and the 'units' library too
```

The order in which system libraries are imported doesn't matter.

System libraries contain functions and sometimes constants that can be used by a Mindcode program. The following system libraries are provided:

* `graphics`: additional graphics functions. Requires the Mindustry Logic 8 instruction set.
* `printing`: functions for outputting formatted numbers. Some functions require the Mindustry Logic 8 instruction set.
* `blocks`: block-related functions (just the `findLinkedBlocks` function at this moment).
* `units`: functions for searching and binding available units of a required type.
* `math`: mathematical functions.

## Compiled function sizes

As Mindustry Logic limits the program size to 1,000 instructions, the compiled size of the library functions may present an important consideration. The documentation of each function contains a table specifying the size of the resulting code under various conditions. The size is measured using these rules:

- all input arguments are variables,
- all output values provided by the function are used,
- the optimization level is set to `advanced`.

If a function just returns its input parameter as the return value and does nothing else, it doesn't produce any instruction—its size is zero. The system libraries sometimes use these functions for consistency, for example, as a special case for a vararg function.  

The following types of function calls are measured:

- **Inlined function**: the size of the function when called inline. Valid for functions that are declared inline or inlined later either because they're called just once, or by the Function Inlining optimization.
- **Function body**: the size of the function when not called inline. It is typically just one instruction larger than the inline version, because an instruction to return from the function to the caller needs to be added. Not shown for functions that are declared `inline`.
- **Function call**: the number of instructions used to call the function from another place of the program. Not shown for functions that are declared `inline`.
- Vararg functions can only be declared `inline`. For these functions, the size of the function depends heavily on the number of arguments passed in. Therefore, for vararg functions a few examples of function calls and their corresponding sizes are included with each function.

There are several factors that might cause the size of a function used in an actual program to differ from the measurement above:

- Optimizations might remove instructions setting up input parameters or receiving output values from the function.
- When constant values are used as input parameters in inlined functions, optimizations might make the resulting code significantly smaller, as constants in expressions can sometimes be optimized away. 
- If the output value of an output parameter isn't used in the entire program, the output parameter might get removed from the function body.

> [!NOTE]
> The function sizes are measured separately for the `speed` and `size` optimization goals. In some cases, optimizing for speed may produce smaller code than optimizing for size. The reason for this primarily is that optimization for speed may unroll some loops resulting in linear code, which is much better suited for further optimizations.


# Arrays library

To use the Arrays library, use the `require arrays;` statement.

Functions in this library are designed for loop unrolling for the best performance.
When instruction space doesn't allow loop unrolling, random array access is used,
possibly leading to general (un-inlined) array jump tables being generated for space conservation.

## Functions

### fill

**Definition:** `inline void fill(array, value)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five elements in the array     |                   5 |                 19 |
| Ten elements in the array      |                  10 |                 34 |
| Twenty elements in the array   |                  20 |                 64 |

Fills the array with a given value.


### reverse

**Definition:** `inline void reverse(array)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five elements in the array     |                  11 |                 43 |
| Ten elements in the array      |                  25 |                 68 |
| Twenty elements in the array   |                  50 |                118 |

Reverses the values in the array.


### bubblesort

**Definition:** `inline void bubblesort(array, in maxToMin)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five elements in the array     |                  74 |                 72 |
| Ten elements in the array      |                 164 |                 97 |
| Twenty elements in the array   |                 344 |                147 |

Sorts the array.


### bubblesort

**Definition:** `inline void bubblesort(sortBy, values, in maxToMin)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five elements in the array     |                  98 |                114 |
| Ten elements in the array      |                 218 |                159 |
| Twenty elements in the array   |                 458 |                249 |

Sorts elements of two arrays in parallel. The `sortBy` array contains the sort keys, while the
`values` array gets reordered to the same relative order as the `sortBy` array.


# Blocks library

To use the Blocks library, use the `require blocks;` statement.

## Functions

### findLinkedBlocks

**Definition:** `inline void findLinkedBlocks(title, message, linkMap...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Linking two blocks             |                  27 |                 27 |
| Linking four blocks            |                  40 |                 40 |
| Linking six blocks             |                  53 |                 53 |

Searches blocks linked to the processor for blocks of requested types and assigns them to given variables if found.
The function tries to locate blocks repeatedly until all required blocks are found.

This function is useful to dynamically locate blocks of given types instead of using the predefined link name.
By locating the blocks dynamically, it is not necessary to link a block to the processor under a particular name,
such as `message1` or `switch1`. The function cannot handle situations when two blocks of the same type are needed
(e.g., two switches), but can handle situations where a single variable can accept multiple block types
(e.g., either memory cell or memory bank).

Function outputs status information while it is running to the block passed in the `message` parameter.
When a `@message` block is among the required types and is found, it is used instead of the `message` parameter.

**Inputs and outputs:**

- `title`: title to be used as part of the status information.
- `message`: initial block to use to output status information. Typically `message1`.
- `linkMap`: definition of the required blocks. Each block needs four variables:
  - `requested`: type of the requested block, e.g. `@switch`.
  - `name`: name of the block to use as part of the status information.
  - `out variable`: variable to receive the block
  - `required`: if `true`, the function will wait until a block of a given type is linked to the processor. If `false`, the function doesn't wait.

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


# Graphics library

To use the Graphics library, use the `require graphics;` statement. The Graphics library uses Mindustry Logic 8
instructions and therefore also requires `#set target = 8;` statement.

The provided library functions use transformations to rotate, invert or scale graphics output as needed
for each display (large or small). Transformations are additive, so it is suggested to call `reset()`
at the beginning of each program that uses transformations to clean up possible transformations from earlier runs.

The parameters used for transformations must be adapted to the size of the output display being drawn.
The transformation functions therefore exist in versions for both logic displays and large logic displays,
plus a version which automatically detects the display type from a block passed in as a parameter.

## Constants

### DISPLAY_SIZE_SMALL

**Definition:** `const DISPLAY_SIZE_SMALL = 80;`

Length of the side of the drawing area of the `logic-display` block.

### DISPLAY_SIZE_LARGE

**Definition:** `const DISPLAY_SIZE_LARGE = 176;`

Length of the side of the drawing area of the `large-logic-display` block.

## Functions

### unpackcolor

**Definition:** `void unpackcolor(out r, out g, out b, out a, in packedColor)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   9 |                  9 |
| Function body                  |                  10 |                 10 |
| Function call                  |                   8 |                  8 |

Unpacks numeric value created by the `packcolor` instruction (or a corresponding color literal) into
individual color channel components. The function produces real numbers between 0 and 1 (inclusive) for
all four color channels.

**Note:** When compiling for Mindustry 8, the built-in `unpackcolor` function is used instead of this one.

**Inputs and outputs:**

- `r`: variable to receive the value corresponding to the red channel
- `g`: variable to receive the value corresponding to the green channel
- `b`: variable to receive the value corresponding to the blue channel
- `a`: variable to receive the value corresponding to the alpha channel
- `packedColor`: color value to unpack

### setAlpha

**Definition:** `def setAlpha(color, alpha)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   4 |                  4 |
| Function body                  |                   5 |                  5 |
| Function call                  |                   5 |                  5 |

Allows redefining the alpha channel of an existing color. When used with constant value of alpha and a built-in
color literal (e.g. `@colorSalmon`), the value will be compile-time evaluated. When a named color literal is used
(e.g. `%[salmon]`), compile time evaluation is not possible.

This function is significantly more efficient than unpacking the color into individual color channels
and repackaging them back.

**Inputs and outputs:**

- `color`: the color to modify, in packed color format
- `alpha`: new value of the alpha channel, 0 to 255
- return the packed color representation of the altered color.

### packHsv

**Definition:** `def packHsv(hue, saturation, value, alpha)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  26 |                 26 |
| Function body                  |                  27 |                 27 |
| Function call                  |                   7 |                  7 |

Creates a packed color using the HSV color model. The individual components (hue, saturation, value, alpha)
must be real numbers between 0 and 1.

**Inputs and outputs:**

- `hue`: desired hue of the color
- `saturation`: desired saturation of the color
- `value`: desired value of the color
- `alpha`: desired alpha channel of the color
- returns the desired color in the packed color representation

### drawflush

**Definition:** `inline void drawflush()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Clears the processor's graphics buffer without outputting the contents into any display. Equivalent to
`drawflush(null);`.

### displaySize

**Definition:** `def displaySize(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   7 |                  7 |
| Function body                  |                   8 |                  8 |
| Function call                  |                   4 |                  4 |

Returns the actual display size based on the type of display passed in as an argument.
When the passed-in argument is not a display, the processor is stopped.

### rotateLeftSmall

**Definition:** `inline void rotateLeftSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output to the left (counterclockwise) by 90 degrees for a small display.

### rotateRightSmall

**Definition:** `inline void rotateRightSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output to the right (clockwise) by 90 degrees for a small display.

### upsideDownSmall

**Definition:** `inline void upsideDownSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output by 180 degrees (upside down) by 90 degrees for a small display.

### flipVerticalSmall

**Definition:** `inline void flipVerticalSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Flips the output vertically (along the Y axis) for a small display.

### flipHorizontalSmall

**Definition:** `inline void flipHorizontalSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Flips the output horizontally (along the X axis) for a small display.

### rotateLeftLarge

**Definition:** `inline void rotateLeftLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output to the left (counterclockwise) by 90 degrees for a large display.

### rotateRightLarge

**Definition:** `inline void rotateRightLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output to the right (clockwise) by 90 degrees for a large display.

### upsideDownLarge

**Definition:** `inline void upsideDownLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rotates the output by 180 degrees (upside down) by 90 degrees for a large display.

### flipVerticalLarge

**Definition:** `inline void flipVerticalLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Flips the output vertically (along the Y axis) for a large display.

### flipHorizontalLarge

**Definition:** `inline void flipHorizontalLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Flips the output horizontally (along the X axis) for a large display.

### rotateLeft

**Definition:** `void rotateLeft(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  10 |                 10 |
| Function body                  |                  11 |                 11 |
| Function call                  |                   4 |                  4 |

Rotates the output to the left (counterclockwise) by 90 degrees for the given display.

### rotateRight

**Definition:** `void rotateRight(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  10 |                 10 |
| Function body                  |                  11 |                 11 |
| Function call                  |                   4 |                  4 |

Rotates the output to the right (clockwise) by 90 degrees for the given display.

### upsideDown

**Definition:** `void upsideDown(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  10 |                 10 |
| Function body                  |                  11 |                 11 |
| Function call                  |                   4 |                  4 |

Rotates the output by 180 degrees (upside down) by 90 degrees for the given display.

### flipVertical

**Definition:** `void flipVertical(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  10 |                 10 |
| Function body                  |                  11 |                 11 |
| Function call                  |                   4 |                  4 |

Flips the output vertically (along the Y axis) for the given display.

### flipHorizontal

**Definition:** `void flipHorizontal(display)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  10 |                 10 |
| Function body                  |                  11 |                 11 |
| Function call                  |                   4 |                  4 |

Flips the output horizontally (along the X axis) for the given display.

### scaleSmallToLarge

**Definition:** `inline void scaleSmallToLarge()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Scales the graphics output so that an output that targets a small display gets displayed
over the entire area of a large display.

### scaleLargeToSmall

**Definition:** `inline void scaleLargeToSmall()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Scales the graphics output so that an output that targets a large display gets displayed
over the entire area of a small display.

### scaleDisplay

**Definition:** `inline void scaleDisplay(x, y)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   3 |                  3 |

Applies scaling to the graphic output, trying to minimize rounding error caused by Mindustry Logic
storing the scale parameters with a precision of 10 bits, and a scale of 0.05.

Both `x` and `y` are increased by `1 / 40` (`0.025`), to compensate for the processor truncating the
value of either parameter after dividing it by `1 / 20` (`0.05`).

# Math library

To use the Math library, use the `require math;` statement.

## Constants

### PI

**Definition:** `const PI = 3.141592653589793;`

The value that is closer than any other to _pi_ (&pi;), the ratio of the circumference
of a circle to its diameter. Provides better precision than `@pi`, and is compile-time
evaluated when possible.

### DEG_TO_RAD

**Definition:** `const DEG_TO_RAD = 0.017453292519943295;`

Constant by which to multiply an angular value in degrees to obtain an  angular value in radians.

### RAD_TO_DEG

**Definition:** `const RAD_TO_DEG = 57.29577951308232;`

Constant by which to multiply an angular value in radians to obtain an angular value in degrees.

## Functions

### distance

**Definition:** `inline def distance(x1, y1, x2, y2)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   3 |                  3 |

Computes the distance between points (`x1`, `y1`) and (`x2`, `y2`).
Uses the `len` instruction for efficient hypotenuse calculation.

### round

**Definition:** `inline def round(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Rounds the number to the closest integer. Halves are rounded up: `round(1.5)' gives '2` and `round(-1.5)` gives `1`.

**Note:** When compiling for Mindustry 8, the mlog `round` function is used instead of this one.

### frac

**Definition:** `inline def frac(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Returns the fractional part of the number. `frac(1.5)` gives `0.5`.

### signInexact

**Definition:** `def signInexact(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   6 |                  6 |
| Function body                  |                   7 |                  7 |
| Function call                  |                   4 |                  4 |

Returns the sign of the number. The return value is `0` precisely when `x == 0`
(using the Mindustry Logic native comparison precision).

### sign

**Definition:** `inline def sign(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   3 |                  3 |

Returns the sign of the number. The return value is `0` when `x` is null or exactly zero.

**Note:** When compiling for Mindustry 8, the built-in `sign` function is used instead of this one.

### isZero

**Definition:** `inline def isZero(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Returns `true` when `x` is null or precisely zero.

### isZero

**Definition:** `inline def isZero(x, precision)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Returns `true` when `x` is null or its absolute value is less than `precision`.

### isEqual

**Definition:** `inline def isEqual(a, b, precision)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   3 |                  3 |

Returns `true` if the two values differ by less than `precision`.

> [!NOTE]
> The non-strict equality operators in Mindustry (and, by extension, Mindcode), i.e. `==`, `!=`,
> consider numeric values equal when they differ by less than `0.000001`. The `isEqual` function
> allows comparing values using different precision.

### nullToZero

**Definition:** `inline def nullToZero(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Converts the value of `x` to zero if it was `null`. Uses a single instruction for the conversion,
and makes sure it won't be removed by the optimizer.

### boolean

**Definition:** `inline def boolean(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Converts the value of `x` to boolean values (`0` or `1`). The returning value will be `0`
if `x` is equal to `0` using Mindustry equality operator, `1` otherwise.

### integer

**Definition:** `inline def integer(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Converts the value of `x` to an integer in the same way Mindustry Logic converts operands of bitwise operations
(`and`, `or`, `xor`, `shl`, `shr`, `not`) from real numbers to integer numbers.
Uses a single instruction for the conversion and makes sure it won't be removed by the optimizer.

### sum

**Definition:** `inline def sum(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `sum` function taking a variable number of arguments.

### sum

**Definition:** `inline def sum(x1, x2, x...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five arguments in total        |                   4 |                  4 |
| Ten arguments in total         |                   9 |                  9 |
| Twenty arguments in total      |                  19 |                 19 |

Returns the sum of all given arguments.

### avg

**Definition:** `inline def avg(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `avg` function taking a variable number of arguments.

### avg

**Definition:** `inline def avg(x1, x2, x...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five arguments in total        |                   5 |                  5 |
| Ten arguments in total         |                  10 |                 10 |
| Twenty arguments in total      |                  20 |                 20 |

Returns the average of all given arguments.

### logn

**Definition:** `inline def logn(number, base)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   3 |                  3 |

Returns the logarithm of the number in the given base.

**Note:** When compiling for Mindustry 8, the mlog `logn` function is used instead of this one.

### log2

**Definition:** `inline def log2(number)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Returns the logarithm of the number in base 2.

**Note:** When compiling for Mindustry 8, the logarithm is computed using the mlog `logn` function.

### lerp

**Definition:** `inline def lerp(from, to, ratio)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   4 |                  4 |

Perform linear interpolation between two values.

**Inputs and outputs:**

- `from`: first value
- `to`: second value
- `ratio`: value in the range of `0 .. 1`, `0` corresponds to `from`, `1` corresponds to `to`, fractional value
           corresponds to a point in between (`2/3` to a point two-thirds of the way from `from` to `to`).

### median

**Definition:** `inline def median(x)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `median` function taking a variable number of arguments.

### median

**Definition:** `inline def median(x1, x2)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   2 |                  2 |

Returns the median of two values.

### median

**Definition:** `inline def median(x1, x2, x3)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   5 |                  5 |

Returns the median of three values.

### median

**Definition:** `def median(x1, x2, x3, x4)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  16 |                 16 |
| Function body                  |                  17 |                 17 |
| Function call                  |                   7 |                  7 |

Returns the median of four values.

### median

**Definition:** `def median(x1, x2, x3, x4, x5)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  18 |                 18 |
| Function body                  |                  19 |                 19 |
| Function call                  |                   8 |                  8 |

Returns the median of five values.

### median

**Definition:** `inline def median(x...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Ten arguments in total         |                 693 |                 88 |
| Fifteen arguments in total     |                 143 |                118 |
| Twenty arguments in total      |                 183 |                148 |

Computes the median of the given arguments using a generic algorithm. The algorithm generates quite a large code
and is fairly slow as it doesn't modify the input array.


# Printing library

To use the Printing library, use the `require printing;` statement. Some of the Printing library functions use
Mindustry Logic 8 instructions and therefore require the `#set target = 8;` statement.

## Functions

### printflush

**Definition:** `inline void printflush()`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   1 |                  1 |

Clears the processor's text buffer without outputting the contents into any message block. Equivalent to
`printflush(null);`.

### printLines

**Definition:** `inline void printLines(lines...)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five arguments in total        |                   6 |                  6 |
| Ten arguments in total         |                  11 |                 11 |
| Twenty arguments in total      |                  21 |                 21 |

Prints all arguments passed to it, each on a new line.

### formatNumber

**Definition:** `void formatNumber(number)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  17 |                 17 |
| Function body                  |                  18 |                 18 |
| Function call                  |                   4 |                  4 |

**Note:** Function requires Mindustry Logic version 8 or later.

Formats the number passed in as a parameter into the text buffer, using commas as thousand separators.
The fractional part of the number to be printed is ignored.

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function.
The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved. The number will be rendered
at the place of the lowest formatting placeholder.

Nulls are printed as 0.

> [!TIP]
> While the function is optimized for performance, formatting numbers is many times slower than just printing
> them using the `print()` function.

### printNumber

**Definition:** `void printNumber(number)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  18 |                 18 |
| Function body                  |                  19 |                 19 |
| Function call                  |                   4 |                  4 |

**Note:** Function requires Mindustry Logic version 8 or later.

Prints the number passed in as a parameter into the text buffer, using commas as thousand separators.
The fractional part of the number to be printed is ignored.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`.
The number will be printed at the end of the print buffer.

Nulls are printed as 0.

> [!TIP]
> While the function is optimized for performance, formatting numbers is many times slower than just printing
> them using the `print()` function.

See also [`formatNumber`](#formatnumber)

### formatBinaryNumber

**Definition:** `void formatBinaryNumber(number, digits)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  17 |                 17 |
| Function body                  |                  18 |                 18 |
| Function call                  |                   5 |                  5 |

**Note:** Function requires Mindustry Logic version 8 or later.

Formats the number passed in as a parameter into the text buffer in binary base, outputting at least the given
number of binary digits. The fractional part of the number to be printed is ignored. Negative numbers are
formatted with a minus sign.

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function.
The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved. The number will be rendered
at the place of the lowest formatting placeholder.

Nulls are printed as zero values.

**Inputs and outputs:**

- `number`: number to be formatted
- `digits`: minimal number of digits to output (padded on the left by zeroes)

### printBinaryNumber

**Definition:** `void printBinaryNumber(number, digits)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  18 |                 18 |
| Function body                  |                  19 |                 19 |
| Function call                  |                   5 |                  5 |

**Note:** Function requires Mindustry Logic version 8 or later.

Prints the number passed in as a parameter into the text buffer in binary base, outputting at least the given
number of binary digits. The fractional part of the number to be printed is ignored. Negative numbers are printed
with a minus sign.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`.
The number will be printed at the end of the print buffer.

Nulls are printed as zero values.

See also [`formatBinaryNumber`](#formatbinarynumber)

**Inputs and outputs:**

- `number`: number to be formatted
- `digits`: minimal number of digits to output (padded on the left by zeroes)

### formatHexNumber

**Definition:** `void formatHexNumber(number, digits)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  37 |                 37 |
| Function body                  |                  38 |                 38 |
| Function call                  |                   5 |                  5 |

**Note:** Function requires Mindustry Logic version 8 or later.

Formats the number passed in as a parameter into the text buffer in hexadecimal base, outputting at least the given
number of hexadecimal digits. The fractional part of the number to be printed is ignored. Negative numbers are
formatted with a minus sign.

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function.
The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved. The number will be rendered
at the place of the lowest formatting placeholder.

Nulls are printed as zero values.

**Inputs and outputs:**

- `number`: number to be formatted
- `digits`: minimal number of digits to output (padded on the left by zeroes)

### printHexNumber

**Definition:** `void printHexNumber(number, digits)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  38 |                 38 |
| Function body                  |                  39 |                 39 |
| Function call                  |                   5 |                  5 |

**Note:** Function requires Mindustry Logic version 8 or later.

Prints the number passed in as a parameter into the text buffer in hexadecimal base, outputting at least the given
number of hexadecimal digits. The fractional part of the number to be printed is ignored. Negative numbers are
printed with a minus sign.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`.
The number will be printed at the end of the print buffer.

Nulls are printed as zero values.

See also [`formatBinaryNumber`](#formatbinarynumber)

**Inputs and outputs:**

- `number`: number to be formatted
- `digits`: minimal number of digits to output (padded on the left by zeroes)

### printExactFast

**Definition:** `void printExactFast(n)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  74 |                 47 |
| Function body                  |                  75 |                 48 |
| Function call                  |                   4 |                  4 |

Prints the value into the text buffer without rounding to the nearest integer value.
The function is primarily useful for debugging purposes to determine the actual value of variables.
This variant of the function is optimized to produce faster, larger code.

**Note:** When printing the exact representation of numbers that are close to integer value, the Mindustry Logic
`print` instruction cannot be used, as it would print the rounded value. In such a case a custom printing routine
is used, which can take around 50 steps to output the entire number.

> [!TIP]
> The `print` and `format` instructions applied to numerical values round the value to the nearest integer
> when they differ from the integer value by less than `1e-6`. This makes it impossible to directly print
> (with sufficient precision) numerical values close to integer values in general, and values close to zero
> in particular. For example, `print(10 ** -50)` prints `0`. On the other hand, `printExactFast(10 ** -50)`
> outputs `1E-50`.

> [!IMPORTANT]
> The mathematical operations used by `printExact` to output the value may introduce small additional numerical
> errors to the output value; for example `printExact(3.00000003)` outputs `3.000000029999999`.

**Inputs and outputs:**

- `n`: number to be printed

### printExactSlow

**Definition:** `void printExactSlow(n)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  79 |                 26 |
| Function body                  |                  80 |                 27 |
| Function call                  |                   4 |                  4 |

Prints the value into the text buffer without rounding to the nearest integer value.
The function is primarily useful for debugging purposes to determine the actual value of variables.
This variant of the function is optimized to produce slower, smaller code.

**Note:** This function uses exact (slow) printing for all values except zero.

See also [`printExactFast`](#printexactfast)

**Inputs and outputs:**

- `n`: number to be printed

### printExactBinary

**Definition:** `void printExactBinary(n)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                 192 |                 12 |
| Function body                  |                 193 |                 13 |
| Function call                  |                   4 |                  4 |

Prints the number passed in as a parameter into the text buffer in a binary base, always outputting 64 binary digits.
No prefix (i.e., `0b`) is printed.

The fractional part of the number to be printed is ignored. Negative values are printed with the most significant
bit (the sign bit) set to `1`.

This function is safe to use with numbers in the unsafe integer range: no precision loos occurs during the printing
itself, except the conversion of the number from a `double` to a 64-bit integer (`long`).

**Inputs and outputs:**

- `n`: number to be printed

### printExactHex

**Definition:** `void printExactHex(n)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                 112 |                 17 |
| Function body                  |                 113 |                 18 |
| Function call                  |                   4 |                  4 |

**Note:** Function can be compiled for all Mindustry Logic versions. The displayed statistics is valid for
target 8, for earlier targets the code size may be higher.

Prints the number passed in as a parameter into the text buffer in a hexadecimal base, always outputting 16
hexadecimal digits. No prefix (i.e., `0b`) is printed.

The fractional part of the number to be printed is ignored. Negative values are printed with the most significant
bit (the sign bit) set to `1`.

This function is safe to use with numbers in the unsafe integer range: no precision loos occurs during the printing
itself, except the conversion of the number from a `double` to a 64-bit integer (`long`).

**Inputs and outputs:**

- `n`: number to be printed

# Units library

To use the Units library, use the `require units;`

## Functions

### noControlWithin

**Definition:** `def noControlWithin(x, y, radius)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   6 |                  6 |
| Function body                  |                   7 |                  7 |
| Function call                  |                   6 |                  6 |

Determines whether the current unit is within a given radius from a given point.
Unlike the built-in `within()` function, this function doesn't get the control of the unit to make the test
but is slower. If you already control the current unit, use `within()` instead.

### findFreeUnit

**Definition:** `def findFreeUnit(unit_type, initial_flag)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  16 |                 16 |
| Function body                  |                  17 |                 17 |
| Function call                  |                   5 |                  5 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
If no free unit of the given type can be found (either because none exists or because all existing units are occupied),
the function returns immediately.

**Inputs and outputs:**

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit, or `null` if no free unit of the given type exists.

The function doesn't use units that are controlled by a player or a different processor.

### findClosestUnit

**Definition:** `def findClosestUnit(x, y, unit_type, initial_flag)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  31 |                 31 |
| Function body                  |                  32 |                 32 |
| Function call                  |                   7 |                  7 |

Searches for and binds a free unit of the given type closest to the coordinates on the map given.
If no free unit of the given type can be found (either because none exists or because all existing units
are occupied), the function returns immediately.

**Inputs and outputs:**

- `x`, `y`: position of the map to compute unit distance relative to
- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit closest to given coordinates, or `null` if no free unit of the given type exists.

The function doesn't use units that are controlled by a player or a different processor.

### waitForFreeUnit

**Definition:** `def waitForFreeUnit(unit_type, initial_flag)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                   6 |                  6 |
| Function body                  |                   7 |                  7 |
| Function call                  |                   5 |                  5 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
The function doesn't return until a free unit of the given type can be found.

**Inputs and outputs:**

- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit.

The function doesn't use units that are controlled by a player or a different processor.

### waitForFreeUnit

**Definition:** `def waitForFreeUnit(message, preface, unit_type, initial_flag)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Inlined function               |                  16 |                 16 |
| Function body                  |                  17 |                 17 |
| Function call                  |                   7 |                  7 |

Finds and binds a free unit of the given type. When such a unit is found, it is flagged by the given initial flag.
The function doesn't return until a free unit of the given type can be found. The function prints status
information about the search onto a message block—one of two messages:

- `No unit of type <unit type> found.` when no unit of the given type exists,
- `Looking for a free <unit type>...` when units of the given type exist, but none is free to use.

**Inputs and outputs:**

- `message`: message block to receive status information about the search.
- `preface`: additional text to output before the status message, e.g., description of the processor.
- `unit_type`: type of the unit: `@flare`, `@mono`, `@poly` etc. Can be a variable.
- `initial_flag`: initial flag to set to the freshly bound unit.
- Returns the freshly bound unit.

The function doesn't use units that are controlled by a player or a different processor.
 
# Additional resources

The system library is integrated into the compiler and as such is available to both the command-line compiler and the web application. The current version of the library can be found [here](https://github.com/cardillan/mindcode/tree/main/compiler/src/main/resources/library).

---

[« Previous: Function reference for Mindustry Logic 8A](FUNCTIONS-80.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry Logic 8 »](MINDUSTRY-8.markdown)
