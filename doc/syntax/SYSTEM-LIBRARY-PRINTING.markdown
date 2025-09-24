# Printing library

Functions for printing and formatting numbers. Some functions require the Mindustry Logic 8 instruction set.

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
| Inlined function               |                  13 |                 17 |
| Function body                  |                  14 |                 18 |
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
| Inlined function               |                  14 |                 18 |
| Function body                  |                  15 |                 19 |
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
| Inlined function               |                  38 |                 32 |
| Function body                  |                  39 |                 33 |
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
| Inlined function               |                  39 |                 34 |
| Function body                  |                  40 |                 35 |
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
| Inlined function               |                  64 |                 14 |
| Function body                  |                  65 |                 15 |
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

---

[« Previous: Math](SYSTEM-LIBRARY-MATH.markdown) &nbsp; | &nbsp; [Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Units »](SYSTEM-LIBRARY-UNITS.markdown)
