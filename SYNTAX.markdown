# Loops

You have access to 3 styles of loops: [while loops](#while-loops), [range iteration loops](#range-iteration-loops), and
[C-style loops](#c-style-loops).

## While Loops

Loop until a condition becomes true:

```
while @unit === null
  ubind(@poly)
end
```

## Range Iteration Loops

Loop over a defined collection of values, in an inclusive or exclusive fashion. The `..` range operator indicates an
inclusive range:

```
for n in 14 .. 18
  print("\n", n)
end
printflush(message1)

// prints 14, 15, 16, 17 and 18 on separate lines
```

The `...` range operator indicates an exclusive range:

```
sum = 0
for addr in 0 ... 64
  sum += cell1[addr]
end
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
for x = SW_X, y = SW_Y; x < NE_X && j < NE_Y ; x += dx
  // do something with this block
  if x == NE_X
    dx *= -1
    y += dy
  end
end
```

# Conditionals

Mindcode offers 3 types of conditionals: if/else expressions, the ternary operator and case/when expressions.

## If/Else Expressions

In Mindcode, `if` is an expression, meaning it returns a value. The returned value is the last value of the branch. For example:

```
result = if n == 0
  "ready"
else
  "pending"
end
```

Depending on the value of `n`, `result` will contain the one of `ready` or `pending`.

## Ternary Operator

The ternary operator (`?:`) is exactly like an if/else expression, except it is more succint. Use it when you need a
conditional but want to save some space:

```
result = n == 0 ? "ready " : "pending"
```

This is the exact same conditional expression as above, except it is written on one line.

## Case/When Expressions

Case/when is another way of writing conditionals. Use case/when when you need to test a value against multiple different
alternatives:

```
next_action = case num_enemies
when 0
  "chill"
when 1
  "alert"
when 2
  "vigilant"
else
  "nuke-the-place"
end
```

# Global variables

In order to use global variables, you need to allocate a heap within a Memory Cell or a Memory Bank. This allocation
tells the Mindcode compiler where to store its global variables. A heap is simply a region of memory. The heap is
allocated using the following Mindcode:

```
allocate heap in cell4[50 ... 64]
```

This statement allocates a heap, stored in `cell4`, and uses memory locations 50, 51, 52, ..., 62, and 63 (note the
exclusive range). If you declare more global variables than you have allocated, compilation will fail with an
`OutOfHeapSpaceException`. In that case, allocate more space for the heap in your cell, or switch to a Memory Bank and
allocate more space to your heap.

Once your heap is allocated, you can use global variables:

```
allocate heap in cell4[32 ... 64]

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
allocate heap in cell2[61 .. 63]

$flag = rand(10000)
$targetx = 80
$targety = 80
```

and changed it to:

```
allocate heap in cell2[61 .. 63]

$targetx = 80
$targety = 80
$flag = rand(10000)
```


then all addresses in the heap would be reallocated.
`$targetx` would be stored in memory cell 61, rather than `$flag`. To fix this issue, you can either always
reset your heap to all zeroes on startup, or destroy and re-create your Memory Cell/Memory Bank.
