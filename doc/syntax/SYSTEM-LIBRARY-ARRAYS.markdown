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
| Five elements in the array     |                   5 |                  5 |
| Ten elements in the array      |                  10 |                 10 |
| Twenty elements in the array   |                  20 |                 20 |

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
| Five elements in the array     |                  78 |                 73 |
| Ten elements in the array      |                 173 |                 98 |
| Twenty elements in the array   |                 363 |                148 |

Sorts the array.


### bubblesort

**Definition:** `inline void bubblesort(sortBy, values, in maxToMin)`

| Compiled code size when...     | optimized for speed | optimized for size |
|--------------------------------|--------------------:|-------------------:|
| Five elements in the array     |                 102 |                115 |
| Ten elements in the array      |                 227 |                160 |
| Twenty elements in the array   |                 477 |                250 |

Sorts elements of two arrays in parallel. The `sortBy` array contains the sort keys, while the
`values` array gets reordered to the same relative order as the `sortBy` array.

