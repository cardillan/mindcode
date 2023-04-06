# Mindcode syntax

Mindcode is a high-level language for [Mindustry](https://github.com/anuke/mindustry).
The language design was inspired mostly by Ruby, but there are quite a few differences.
Mindcode aims to provide programmatic access to full Mindustry Logic functionality.
ML instructions interacting with Mindustry World are available through functions
(see [Function reference for Mindustry Logic 7](FUNCTIONS_V7.markdown)).
Other instructions are used by expressions, control structures and other statements in Mindcode.

This document covers the basics of Mindcode syntax:

* [Mindcode basics](#mindcode-basics)
* [Variables and constants](SYNTAX-1-VARIABLES.markdown)
* [Expressions](SYNTAX-2-EXPRESSIONS.markdown)
* [Control flow statements](SYNTAX-3-STATEMENTS.markdown)
* [Functions](SYNTAX-4-FUNCTIONS.markdown)
* [Compiler directives](SYNTAX-5-OTHER.markdown)

---

# Mindcode basics

## Program structure

Mindcode program is a sequence of expressions or statements.
Statements and expressions are separated by spaces, tabs or new lines,
and can be optionally separated by semicolons, as in `a = 5; b = 10`.
In rare cases the semicolon may be required to mark an end of a statement/expression.

Expressions have a value, `1 + 2` is an expression whose value is `3`.

Mindcode identifiers and keywords are case-sensitive -- `if` is different from `If`
(first is a keyword, the second is not and could be used as a variable or function name).

Anything following a `//` is a comment till the end of the line.
Comments are completely ignored by Mindcode.

## Keywords

This is a list of Mindcode keywords:

* `allocate`
* `break`
* `case`
* `const`
* `continue`
* `def`
* `do`
* `else`
* `elsif`
* `end`
* `false`
* `for`
* `heap`
* `if`
* `in`
* `inline`
* `loop`
* `null`
* `return`
* `sensor`
* `stack`
* `then`
* `true`
* `when`
* `while`

Keywords cannot be used as function or variable names.

## Identifiers

Identifiers are names of variables, constants, and functions. 
Identifiers in Mindcode are case-sensitive. They consist of basic alphanumeric characters
(i.e. letters a-z, A-Z and digits 0-9, accented characters aren't allowed), underscores and a dash characters.
The first character must be an underscore or a letter. `_foo` and `bar-baz9` are valid identifiers,
`9to5`, `-qux` and `français` are not.

Mindustry Logic provides constants and variables that start with `@`. These can be used as-is in Mindcode,
e.g. `@time` or `@titanium-conveyor`. In some cases the `@` is dropped (more on this later).

Mindcode compiler creates variables starting with two underscores, such as `__tmp10` or `__fn0retval`,
when generating code. To avid interference with the compiler and code optimizer,
do not use any identifiers starting with two underscores for your variable or function names.

## Literals

Mindustry supports the classic decimal notation for integers and floats:

* `1`
* `3.14159`
* `-35`
* `-9381.355`

Mindustry also supports hexadecimal representation for integers:

* `0x35`
* `0xf1`

Additionally, boolean literals are available:

* `false`
* `true`

Boolean values are represented (and equivalent to) as `0` and `1`, respectively.

Finally, there are string literals, a sequence of characters enclosed in double quotes:

`"A string literal."`

It is possible use `\n` as a newline character in a string literal: `print("\n")` creates a new line in text output.
Double quotes must not be used in a string literal. THey are anly allowed if they are properly escaped, i.e. `\"`,
and even then they're replaced by single quotes in the compiled code (`"Hello, \"friend\"!"` --> `"Hello, 'friend'!"`),
as Mindustry Logic cannot handle them otherwise.

---

[Next: Variables and constants »](SYNTAX-1-VARIABLES.markdown)