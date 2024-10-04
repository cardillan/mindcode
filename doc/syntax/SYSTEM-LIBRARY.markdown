# System library

Mindcode comes equipped with a system library. At this moment, the library is just a single source file, `sys.mnd`, which is automatically compiled together with each source file, both in the command line compiler and in the web app. The library is only included if the language target is 8A or higher, as it contains functions that build upon some of the instructions that will be added in the upcoming Mindustry 8 version.

Individual functions in the library are documented here.

# Graphics transformations

The provided library functions use transformations to rotate, invert or scale graphics output as needed for each display (large or small). Transformations are additive, so it is suggested to call `reset()` at the beginning of each program that uses transformations to clean up possible transformations from earlier runs.

The parameters used for transformations must be adapted to the size of the output display being drawn. The transformation functions therefore exist in versions for both logic displays and large logic displays, plus a version which automatically detects the display type from a block passed in as a parameter.   

## Rotating graphics output

The following functions use graphics transformation to rotate the graphics output in multiples of 90 degrees:

### `rotateRightSmall()`, `rotateRightLarge()`, `rotateRight(display)`

Rotates the output to the right (clockwise) by 90 degrees for a small or a large display, or for a display given as a parameter.

### `rotateLeftSmall()`, `rotateLeftLarge()`, `rotateLeft(display)`

Rotates the output to the left (counterclockwise) by 90 degrees for a small or a large display, or for a display given as a parameter.

### `upsideDownSmall()`, `upsideDownLarge()`, `upsideDown(display)`

Rotates the output by 180 degrees (upside down) for a small or a large display, or for a display given as a parameter.

### `flipVerticalSmall()`, `flipVerticalLarge()`, `flipVertical(display)`

Flips the output vertically (along the Y axis) for a small or a large display, or for a display given as a parameter.

### `flipHorizontalSmall()`, `flipHorizontalLarge()`, `flipHorizontal(display)`

Flips the output horizontally (along the X axis) for a small or a large display, or for a display given as a parameter.

### `scaleSmallToLarge()`

Scales the graphics output so that an output that targets a small display gets displayed over the entire area of a large display. 

### `scaleLargeToSmall()`

Scales the graphics output so that an output that targets a large display gets displayed over the entire area of a small display. 

# Units

## `findFreeUnit(unit_type, initial_flag)`

Finds and binds a free unit of given type. When such a unit is found, it is flagged by the given initial flag.

The function doesn't use units that are controlled by a player or a different processor. The function doesn't return until required unit is found.

The status of the search is output to `message1`. 

# Text output

## `formatNumber(n)`

Formats the number passed in as a parameter into the text buffer, using comma as thousands separator. Fractional parts of the number to be printed is ignored. 

To use the function, a placeholder must be inserted into the text buffer prior to the call of this function. The placeholder must be `{2}` or higher, placeholders `{0}` and `{1}` are reserved.

The number will be rendered at the place of the lowest formatting placeholder.

> [!NOTE]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing them using the `print()` function.

## `printNumber(n)`

Prints the number passed in as a parameter, using comma as thousands separator. Fractional parts of the number to be printed is ignored.

To use the function, the text buffer must not contain placeholders `{0}`, `{1}` or `{2}`. The number will be printed at the end of the print buffer.

> [!NOTE]
> While the functions is optimized for performance, formatting numbers is many times slower than just printing them using the `print()` function.

# Additional resources

The `sys.mnd` is integrated into the compiler and as such is available both to the command-line compiler and to the web application. The current version of teh file can be found [here](/mindcode/src/main/resources/library/sys.mnd).

---

[« Previous: Mindustry 8](MINDUSTRY-8.markdown) &nbsp; | &nbsp; [Next: Schemacode »](SCHEMACODE.markdown)
