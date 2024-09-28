# Mindcode syntax

Mindcode is a high-level language for [Mindustry](https://github.com/anuke/mindustry). The language design was 
inspired mostly by Ruby, but there are quite a few differences. Mindcode aims to provide programmatic access to full 
Mindustry Logic functionality. ML instructions interacting with Mindustry World are available through functions (see 
[Function reference for Mindustry Logic 7](FUNCTIONS_V7.markdown)). Other instructions are used by expressions, 
control structures and other statements in Mindcode.

This document covers the basics of Mindcode syntax:

* [Mindcode basics](#mindcode-basics)
* [Variables and constants](SYNTAX-1-VARIABLES.markdown)
* [Expressions](SYNTAX-2-EXPRESSIONS.markdown)
* [Control flow statements](SYNTAX-3-STATEMENTS.markdown)
* [Functions](SYNTAX-4-FUNCTIONS.markdown)
* [Compiler directives](SYNTAX-5-OTHER.markdown)
* [Code optimization](SYNTAX-6-OPTIMIZATIONS.markdown)

Function reference for individual compiler versions:

* [Function reference for Mindustry Logic 6](FUNCTIONS_V6.markdown)
* [Function reference for Mindustry Logic 7](FUNCTIONS_V7.markdown)
* [Function reference for Mindustry Logic 7A](FUNCTIONS_V7A.markdown)

Schemacode, a schematic definition language, is covered here:

* [Schemacode](SCHEMACODE.markdown)

Supporting tools: 

* [Command line tool](TOOLS-CMDLINE.markdown): a command line tool containing Mindcode Compiler, Schematics Builder and 
  Schematics Decompiler
* [Schematics Refresher](TOOLS-REFRESHER.markdown): a Mindustry mod providing automatic or manual refreshing of 
  schematics created by Schematics Builder (or any other tool)

Additional information on Mindcode and Mindustry Logic:

* [Troubleshooting Mindcode](TROUBLESHOOTING.markdown))
* [Mindustry Tips and Tricks](MINDUSTRY-TIPS-N-TRICKS.markdown)

---

# Mindcode basics

## Program structure

Mindcode program is a sequence of expressions or statements. Statements and expressions are separated by spaces, 
tabs or new lines, and can be optionally separated by semicolons, as in `a = 5; b = 10`. In rare cases the semicolon 
may be required to mark an end of a statement/expression. Expressions have a value, `1 + 2` is an expression whose 
value is `3`.

Mindcode identifiers and keywords are case-sensitive -- `if` is different from `If` (first is a keyword, the second 
is not and could be used as a variable or function name).

You can use the [remark() function](SYNTAX-4-FUNCTIONS.markdown#remark) to place comments or notes directly to the compiled code.

A text enclosed between `/*` and `*/` is a comment that can span several lines. Additionally, anything following a `//` is a comment till the end of the line. Comments are completely ignored by Mindcode.

An enhanced comment is a comment which starts with `///` (three slashes instead of two). This is an [alternative way to enter remarks](SYNTAX-4-FUNCTIONS.markdown#enhanced-comments) into the compiled code. 

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
* `noinline`
* `null`
* `param`
* `return`
* `sensor`
* `stack`
* `then`
* `true`
* `when`
* `while`

Keywords cannot be used as function or variable names.

## Identifiers

Identifiers are names of variables, constants, and functions. Identifiers in Mindcode are case-sensitive. They 
consist of basic alphanumeric characters (i.e. letters a-z, A-Z and digits 0-9, accented characters aren't allowed), 
underscores and a dash characters. The first character must be an underscore or a letter. `_foo` and `bar-baz9` are 
valid identifiers, `9to5`, `-qux` and `français` are not.

Mindcode compiler creates variables starting with two underscores, such as `__tmp10` or `__fn0retval`,
when generating code. To avoid interference with the compiler, do not use any identifiers starting with two 
underscores for your variable or function names.

## Built-in variables and constants

Mindustry Logic provides variables and constants that start with `@`. They can be read-only variables (such as 
`@unit` or `@time`) or effectively constant values (such as `@coal` or `@this`).      

Built-in variables and constants can be used as-is in Mindcode, e.g. `@time` or `@titanium-conveyor`. In some 
situations the leading `@` is dropped (more on this later).

## Literals

### Null literal

`null` is a literal representing the value of null. Null is the value of variables that haven't been initialized
and a result of any calculation that is undefined or produced an error in Mindustry.

### Decimal literals

Mindcode supports the classic decimal notation for integers and floats, including scientific notation:

* `1`
* `3.14159`
* `-35`
* `-9381.355`
* `1e10`
* `1.5e-5`
* `-6.45e+8`

#### Numeric literals in Mindustry Logic

When Mindustry processes the source code in Mindustry Logic, it handles numeric literals with some limitations:

* Numeric literal can be an integer, a decimal number or a number in exponential notation where the mantissa doesn't 
  contain a decimal separator. `15`, `187.5786` and `1e10` are valid numbers, while `1.4e10` is not, as it contains 
  both `.` and `e` characters.
* Integers and decimal numbers are converted with `double` precision - the conversion uses up to about 16 valid digits. 
  Decimal numbers can specify a lot more digits after the decimal separator, but superfluous ones will be ignored.
* The maximum value of integers and decimal numbers is  9223372036854775807 (2<sup>63</sup>-1). Larger numbers 
  wouldn't be recognized and would be read as zero by Mindustry processors. To specify a larger number, an 
  exponential notation must be used. 
* The range of values that is recognized when using exponential notation is about 10<sup>-38</sup> to 
  10<sup>38</sup>. However, numbers in exponential notation are converted with `float` precision, only keeping 6 
  to 7 valid digits.

To find a way around these constraints, Mindcode always reads the value of the numeric literal and then converts it 
to Mindustry Logic compatible literal using these rules (first applicable rule is used): 

1. If the value is zero, it is encoded as `0`.
2. If the value is between 10<sup>-20</sup> and 2<sup>63</sup>-1, the number is converted to standard decimal 
   notation using 20 digits precision.
3. If the value is between 10<sup>-38</sup> and 10<sup>38</sup>, the number is converted to exponential notation 
   without using a decimal separator, using `float` precision (which will be used by Mindustry processor when 
   reading the literal as well). If the conversion to float causes loss of precision, a warning is produced.
4. If none of the above rules is applicable, the conversion isn't possible and a compilation error is produced. 

Note: these rules are applied to the absolute value on the number being encoded. When the number is negative, a minus
sign is then prepended to the created mlog representation.

This processing ensures that numbers within a reasonable range are encoded to use maximal available precision, 
without producing mlog representations that would be unreasonably long. 

Examples of Mindcode literals and their conversion into mlog: 

| Mindcode literal | mlog representation    |
|:-----------------|:-----------------------|
| `1`              | `1`                    |
| `3.0`            | `3`                    |
| `1e10`           | `10000000000`          |
| `1e-10`          | `0.0000000001`         |
| `1.23456789e10`  | `12345678900`          |
| `1.23456789e-10` | `0.000000000123456789` |
| `1.23456789e25`  | `1234568E19`           |
| `1.23456789e-25` | `12345679E-32`         |

The last two examples show the loss of precision when the number needs to be encoded using exponential notation.

### Binary and hexadecimal literals

Mindustry also supports hexadecimal and binary representation for integers:

* `0x35`
* `0xf1`
* `0b010001101`

These literals are written into the mlog code exactly as they appear in Mindcode. If the value of the literal exceeds 
the maximum allowed range for a 64-bit signed integer, a compilation error occurs.    

### Boolean literals

Additionally, boolean literals are available:

* `false`
* `true`

Boolean values are represented as (and indistinguishable from) numerical values `0` and `1`, respectively.

### String literals

Finally, there are string literals, a sequence of characters enclosed in double quotes:

`"A string literal."`

Double quotes in string literals aren't supported in Mindustry Logic at all. Mindcode only allows them if they are properly
escaped, i.e. `\"`, and even then they're replaced by single quotes in the compiled code (`"Hello, \"friend\"!"`
--> `"Hello, 'friend'!"`). Support for escaped double quotes might be completely removed in some future release.

#### Formattable string literals

Formattable string literals are a special case of string literals which can only be used with [`printf`, `print`, `println`, and `remark` functions](SYNTAX-4-FUNCTIONS.markdown#printf). They are prepended by the `$` character: 

`$"A formattable string literal."`

Double quotes aren't supported in formattable string literal, even if they're escaped. No other operations are allowed on them.

---

[Next: Variables and constants »](SYNTAX-1-VARIABLES.markdown)
