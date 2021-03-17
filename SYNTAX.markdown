# Loops

You have access to 3 styles of loops: [while loops](#while-loops), [range iteration loops](#range-iteration-loops), and
[C-style loops](#c-style-loops).

## While Loops

Loop until a condition becomes true:

```
while @unit === null {
  ubind(@poly)
}
```

## Range Iteration Loops

Loop over a defined collection of values, in an inclusive or exclusive fashion. The `..` range operator indicates an
inclusive range:

```
for n in 14 .. 18 {
  print("\n", n)
}
printflush(message1)

// prints 14, 15, 16, 17 and 18 on separate lines
```

The `...` range operator indicates an exclusive range:

```
sum = 0
for addr in 0 ... 64 {
  sum += cell1[addr]
}
avg = sum / 64
```

This loop will calculate the average of all 64 cells (0-based index) of `cell1`.

Please note that currently, range iteration loops can only increment the value
by 1, and only support increasing values. A range of `60 .. 50` is invalid, but
a range of `-1 .. 1` is valid.

## C-Style Loops

The syntax is similar to C's, except for the absence of parenthesis:

```
// Visit every block in a given region, one at a time, starting from the bottom
// left and ending at the upper right
dx = 1
for x = SW_X, y = SW_Y; x < NE_X && j < NE_Y ; x += dx {
  // do something with this block
  if x == NE_X {
    dx *= -1
    y += dy
  }
}
```

# Global variables

In order to use global variables, you need to allocate a heap within a Memory Cell or a Memory Bank. This allocation
tells the Mindcode compiler where to store its global variables. A heap is simply a region of memory. The heap is
allocated using the following Mindcode:

```
allocate heap in cell4 50 ... 64
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more global variables than you have allocated, compilation will fail with an
`OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a Memory Bank and
allocate more space to your heap.

Once your heap is allocated, you can use global variables:

```
allocate heap in cell4 32 ... 64

$dx = 1
$dy = 1
$ne_x = 90
$ne_y = 90
$target_y = $sw_x = 50
$target_x = $sw_y = 50
```

The above will compile to:

```
cell4[32] = 1
cell4[33] = 1
cell4[34] = 90
cell4[35] = 90
cell4[36] = cell4[37] = 50
cell4[38] = cell4[39] = 50
```

Note that global variables are allocated on a first-come, first-served basis. If you had the following code:

```
allocate heap in cell2 61 .. 63

$flag = rand(10000)
$targetx = 80
$targety = 80
```

And you rotated lines around such that `$flag` was the last line, then all addresses in the heap would be reallocated
such that `$targetx` would be stored in memory cell 61, rather than `$flag`. To fix this issue, you can either always
reset your heap to all zeroes on startup, or destroy and re-create your Memory Cell/Memory Bank.
