# Arrays library

Functions for basic array manipulations.

Functions in this library are designed for loop unrolling for the best performance.
When instruction space doesn't allow loop unrolling, random array access is used,
possibly leading to general (un-inlined) array jump tables being generated for space conservation.

## Functions

### fill

**Definition:** `inline void fill(ref array, value)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five elements in an internal array       |                   5 |                  5 |
| Ten elements in an internal array        |                  10 |                 10 |
| Twenty elements in an internal array     |                  20 |                 20 |
| Five elements in an external array       |                   5 |                  4 |
| Ten elements in an external array        |                  10 |                  4 |
| Twenty elements in an external array     |                  20 |                  4 |

Fills the array with a given value.


### reverse

**Definition:** `inline void reverse(ref array)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five elements in an internal array       |                   6 |                 18 |
| Ten elements in an internal array        |                  15 |                 18 |
| Twenty elements in an internal array     |                  30 |                 18 |
| Five elements in an external array       |                   8 |                  8 |
| Ten elements in an external array        |                  20 |                  8 |
| Twenty elements in an external array     |                  40 |                  8 |

Reverses the values in the array.


### bubblesort

**Definition:** `inline void bubblesort(ref array, in maxToMin)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five elements in an internal array       |                  81 |                 48 |
| Ten elements in an internal array        |                 181 |                 48 |
| Twenty elements in an internal array     |                 381 |                 48 |
| Five elements in an external array       |                  81 |                 33 |
| Ten elements in an external array        |                 181 |                 33 |
| Twenty elements in an external array     |                 381 |                 33 |

Sorts the array.


### bubblesort

**Definition:** `inline void bubblesort(ref sortBy, ref values, in maxToMin)`

| Compiled code size when...               | optimized for speed | optimized for size |
|------------------------------------------|--------------------:|-------------------:|
| Five elements in internal arrays         |                 113 |                 70 |
| Ten elements in internal arrays          |                 253 |                 70 |
| Twenty elements in internal arrays       |                 533 |                 70 |
| Five elements in external arrays         |                 113 |                 45 |
| Ten elements in external arrays          |                 253 |                 45 |
| Twenty elements in external arrays       |                 533 |                 45 |

Sorts elements of two arrays in parallel. The `sortBy` array contains the sort keys, while the
`values` array gets reordered to the same relative order as the `sortBy` array.


---

[Up: System library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Next: Blocks »](SYSTEM-LIBRARY-BLOCKS.markdown)
