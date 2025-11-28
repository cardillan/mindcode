# Math library

A math library.

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

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |

Computes the distance between points (`x1`, `y1`) and (`x2`, `y2`).
Uses the `len` instruction for efficient hypotenuse calculation.

### round

**Definition:** `inline def round(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Rounds the number to the closest integer. Halves are rounded up: `round(1.5)' gives '2` and `round(-1.5)` gives `1`.

**Note:** When compiling for Mindustry 8, the mlog `round` function is used instead of this one.

### frac

**Definition:** `inline def frac(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Returns the fractional part of the number. `frac(1.5)` gives `0.5`.

### signInexact

**Definition:** `def signInexact(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |
| Function body                            |                   3 |                  3 |
| Function call                            |                   4 |                  4 |

Returns the sign of the number. The return value is `0` precisely when `x == 0`
(using the Mindustry Logic native comparison precision).

### sign

**Definition:** `inline def sign(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |

Returns the sign of the number. The return value is `0` when `x` is null or exactly zero.

**Note:** When compiling for Mindustry 8, the built-in `sign` function is used instead of this one.

### isZero

**Definition:** `inline def isZero(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Returns `true` when `x` is null or precisely zero.

### isZero

**Definition:** `inline def isZero(x, precision)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Returns `true` when `x` is null or its absolute value is less than `precision`.

### isEqual

**Definition:** `inline def isEqual(a, b, precision)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |

Returns `true` if the two values differ by less than `precision`.

> [!NOTE]
> The non-strict equality operators in Mindustry (and, by extension, Mindcode), i.e. `==`, `!=`,
> consider numeric values equal when they differ by less than `0.000001`. The `isEqual` function
> allows comparing values using different precision.

### nullToZero

**Definition:** `inline def nullToZero(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Converts the value of `x` to zero if it was `null`. Uses a single instruction for the conversion,
and makes sure it won't be removed by the optimizer.

### boolean

**Definition:** `inline def boolean(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Converts the value of `x` to boolean values (`0` or `1`). The returning value will be `0`
if `x` is equal to `0` using Mindustry equality operator, `1` otherwise.

### integer

**Definition:** `inline def integer(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   1 |                  1 |

Converts the value of `x` to an integer in the same way Mindustry Logic converts operands of bitwise operations
(`and`, `or`, `xor`, `shl`, `shr`, `not`) from real numbers to integer numbers.
Uses a single instruction for the conversion and makes sure it won't be removed by the optimizer.

### sum

**Definition:** `inline def sum(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `sum` function taking a variable number of arguments.

### sum

**Definition:** `inline def sum(x1, x2, x...)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five arguments in total                  |                   4 |                  4 |
| Ten arguments in total                   |                   9 |                  9 |
| Twenty arguments in total                |                  19 |                 19 |

Returns the sum of all given arguments.

### avg

**Definition:** `inline def avg(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `avg` function taking a variable number of arguments.

### avg

**Definition:** `inline def avg(x1, x2, x...)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five arguments in total                  |                   5 |                  5 |
| Ten arguments in total                   |                  10 |                 10 |
| Twenty arguments in total                |                  20 |                 20 |

Returns the average of all given arguments.

### logn

**Definition:** `inline def logn(number, base)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   3 |                  3 |

Returns the logarithm of the number in the given base.

**Note:** When compiling for Mindustry 8, the mlog `logn` function is used instead of this one.

### log2

**Definition:** `inline def log2(number)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Returns the logarithm of the number in base 2.

**Note:** When compiling for Mindustry 8, the logarithm is computed using the mlog `logn` function.

### lerp

**Definition:** `inline def lerp(from, to, ratio)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   4 |                  4 |

Perform linear interpolation between two values.

**Inputs and outputs:**

- `from`: first value
- `to`: second value
- `ratio`: value in the range of `0 .. 1`, `0` corresponds to `from`, `1` corresponds to `to`, fractional value
           corresponds to a point in between (`2/3` to a point two-thirds of the way from `from` to `to`).

### median

**Definition:** `inline def median(x)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   0 |                  0 |

Returns `x`. The function is a fallback case for the generic `median` function taking a variable number of arguments.

### median

**Definition:** `inline def median(x1, x2)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   2 |                  2 |

Returns the median of two values.

### median

**Definition:** `inline def median(x1, x2, x3)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                   5 |                  5 |

Returns the median of three values.

### median

**Definition:** `def median(x1, x2, x3, x4)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  16 |                 16 |
| Function body                            |                  17 |                 17 |
| Function call                            |                   7 |                  7 |

Returns the median of four values.

### median

**Definition:** `def median(x1, x2, x3, x4, x5)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Inlined function                         |                  18 |                 18 |
| Function body                            |                  19 |                 19 |
| Function call                            |                   8 |                  8 |

Returns the median of five values.

### median

**Definition:** `inline def median(x...)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Ten arguments in total                   |                 673 |                 87 |
| Fifteen arguments in total               |                 141 |                117 |
| Twenty arguments in total                |                 181 |                147 |

Computes the median of the given arguments using a generic algorithm. The algorithm generates quite a large code
and is fairly slow as it doesn't modify the input array.


---

[« Previous: Graphics](SYSTEM-LIBRARY-GRAPHICS.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Printing »](SYSTEM-LIBRARY-PRINTING.markdown)
