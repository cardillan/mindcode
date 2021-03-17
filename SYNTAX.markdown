# Loops

You have access to 3 styles of loops: [while loops](#while-loops), [range iteration loops](#range-iteration-loops), and [C-style loops](#c-style-loops).

## While Loops

Loop until a condition becomes true:

```
while @unit === null {
  ubind(@poly)
}
```

## Range Iteration Loops

Loop over a defined collection of values, in an inclusive or exclusive fashion. The `..` range operator indicates an inclusive range:

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
