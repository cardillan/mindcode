# Mindustry Tips and Tricks

## Formatting text

The only way to print text in Mindustry Logic is to use the `print` instruction (made available to Mindcode through 
various kinds of `print()` function) one or more times to construct the resulting string in a text buffer.
Each `print` instruction adds its argument, converted to string if necessary, to the end of the text buffer.
Every `printflush` instruction, even unsuccessful, clears the text buffer.

When a text is being printed into a message by using the `printflush` instruction, the entire string from the text buffer
is displayed in the message block. At this moment, the string is inspected for special character sequences that can
alter the appearance of the text in the message block. Two kinds of formatting are supported:
* Line breaks
* Setting text color

### Line breaks

When the `\n` sequence of characters is found in the string being printed, the `\n` sequence is replaced by a line break.
For example:

```
print("One\nTwo")
printflush(message1)
```
produces the following output:
```
One
Two
```

Note that the backslash character is only recognized as part of the `\n` sequence, it is not otherwise specially handled.
Specifically, it is not possible to encode it as `\\`, unlike many other programming languages. Therefore, the following
code snippet

```
print("One\\Two\\nThree")
printflush(message1)
```
produces the following output:
```
One\\Two\
Three
```

If you really want to output `\n` in the message block for whatever reason, you can use this trick:

```
print("One\[red][]nTwo")
printflush(message1)
```
which finally produces
```
One\nTwo
```

This is because the square brackets are used co encode color (see the next paragraph). The `[]` cancels `[red]`,
and together they split apart `\` and `n` in such a way the message block doesn't recognize them anymore.

### Setting text color

The color of the text can be set by entering the color in square brackets. Colors can be specified using
predefined color names (in uppercase or lowercase, but not mixed-case), or by entering the color in `#RRGGBB`
or `#RRGGBBAA` form. The `[]` sequence then restores the previous color. It is therefore possible to use

```
println("[red]This is red")
println("[green]This is green")
println("[]This is red again")
printflush(message1)
```

to print colored texts in the colors indicated in the example.

<details><summary>Show full list of predefined color names.</summary>

```
clear
black
white
light_gray
gray
dark_gray
light_grey
grey
dark_grey
blue
navy
royal
slate
sky
cyan
teal
green
acid
lime
forest
olive
yellow
gold
goldenrod
orange
brown
tan
brick
red
scarlet
crimson
coral
salmon
pink
magenta
purple
violet
maroon
```

</details>

When a color name or color code is not recognized in square brackets, the text including the brackets is left as is.
A sequence of two left square brackets, i.e. `[[`, is always printed as `[` e.g.:

```
println("The color is [[red]")
println("The state is [[alarm]")
printflush(message1)
```

produces

```
The color is [red]
The state is [alarm]
```

In the second case, the doubling of square bracket is not strictly necessary, because `alarm` isn't recognized as a color name.

## Printing values

When printing numbers, Mindustry always prints the full representation of a number. It might be sometimes cumbersome,
as fractions can produce a lot of digits. To avoid this, use the `floor` or `ceil` function:

```
start_time = @time
do_some_stuff()
duration = @time - start_time
println("Elapsed: ", floor(duration), " ms")
```

When a number you're printing is smaller than `0.00001` (in absolute value), Mindustry will print zero (`0`) instead.
The same formatting is used to display value of a variable in the Vars dialog in Mindustry UI. There isn't a way to output
such a small value directly. It is necessary to be aware that a number which was printed as `0` doesn't necessarily have 
a zero value.

---

[« Previous: Compiler directives](SYNTAX-5-OTHER.markdown)
