# Graphics library

Additional graphics functions. Functions related to display transformations require the Mindustry Logic 8 instruction set.

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

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   9 |                  9 |
| Function body                            |                  10 |                 10 |
| Function call                            |                   8 |                  8 |

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

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   4 |                  4 |
| Function body                            |                   5 |                  5 |
| Function call                            |                   5 |                  5 |

Allows redefining the alpha channel of an existing color.

When compiled for target 7, this function is significantly more efficient than unpacking the color into individual
color channels and repackaging them back. When compiled for target 8, the unpack/pack route is more efficient.

**Inputs and outputs:**

- `color`: the color to modify, in packed color format
- `alpha`: new value of the alpha channel, 0 to 255
- return the packed color representation of the altered color.

### packHsv

**Definition:** `def packHsv(hue, saturation, value, alpha)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  22 |                 22 |
| Function body                            |                  23 |                 23 |
| Function call                            |                   7 |                  7 |

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

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Clears the processor's graphics buffer without outputting the contents into any display. Equivalent to
`drawflush(null);`.

### displaySize

**Definition:** `def displaySize(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |
| Function body                            |                   4 |                  4 |
| Function call                            |                   4 |                  4 |

Returns the actual display size based on the type of display passed in as an argument.
When the passed-in argument is not a display, the processor is stopped.

### rotateLeftSmall

**Definition:** `inline void rotateLeftSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output to the left (counterclockwise) by 90 degrees for a small display.

### rotateRightSmall

**Definition:** `inline void rotateRightSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output to the right (clockwise) by 90 degrees for a small display.

### upsideDownSmall

**Definition:** `inline void upsideDownSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output by 180 degrees (upside down) by 90 degrees for a small display.

### flipVerticalSmall

**Definition:** `inline void flipVerticalSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Flips the output vertically (along the Y axis) for a small display.

### flipHorizontalSmall

**Definition:** `inline void flipHorizontalSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Flips the output horizontally (along the X axis) for a small display.

### rotateLeftLarge

**Definition:** `inline void rotateLeftLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output to the left (counterclockwise) by 90 degrees for a large display.

### rotateRightLarge

**Definition:** `inline void rotateRightLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output to the right (clockwise) by 90 degrees for a large display.

### upsideDownLarge

**Definition:** `inline void upsideDownLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rotates the output by 180 degrees (upside down) by 90 degrees for a large display.

### flipVerticalLarge

**Definition:** `inline void flipVerticalLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Flips the output vertically (along the Y axis) for a large display.

### flipHorizontalLarge

**Definition:** `inline void flipHorizontalLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Flips the output horizontally (along the X axis) for a large display.

### rotateLeft

**Definition:** `void rotateLeft(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  10 |                 10 |
| Function body                            |                  11 |                 11 |
| Function call                            |                   4 |                  4 |

Rotates the output to the left (counterclockwise) by 90 degrees for the given display.

### rotateRight

**Definition:** `void rotateRight(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  10 |                 10 |
| Function body                            |                  11 |                 11 |
| Function call                            |                   4 |                  4 |

Rotates the output to the right (clockwise) by 90 degrees for the given display.

### upsideDown

**Definition:** `void upsideDown(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  10 |                 10 |
| Function body                            |                  11 |                 11 |
| Function call                            |                   4 |                  4 |

Rotates the output by 180 degrees (upside down) by 90 degrees for the given display.

### flipVertical

**Definition:** `void flipVertical(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  10 |                 10 |
| Function body                            |                  11 |                 11 |
| Function call                            |                   4 |                  4 |

Flips the output vertically (along the Y axis) for the given display.

### flipHorizontal

**Definition:** `void flipHorizontal(display)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  10 |                 10 |
| Function body                            |                  11 |                 11 |
| Function call                            |                   4 |                  4 |

Flips the output horizontally (along the X axis) for the given display.

### scaleSmallToLarge

**Definition:** `inline void scaleSmallToLarge()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Scales the graphics output so that an output that targets a small display gets displayed
over the entire area of a large display.

### scaleLargeToSmall

**Definition:** `inline void scaleLargeToSmall()`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Scales the graphics output so that an output that targets a large display gets displayed
over the entire area of a small display.

### scaleDisplay

**Definition:** `inline void scaleDisplay(x, y)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |

Applies scaling to the graphic output, trying to minimize rounding error caused by Mindustry Logic
storing the scale parameters with a precision of 10 bits and a scale of 0.05.

Both `x` and `y` are increased by `1 / 40` (`0.025`), to compensate for the processor truncating the
value of either parameter after dividing it by `1 / 20` (`0.05`).

---

[« Previous: Compatibility](SYSTEM-LIBRARY-COMPATIBILITY.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Math »](SYSTEM-LIBRARY-MATH.markdown)
