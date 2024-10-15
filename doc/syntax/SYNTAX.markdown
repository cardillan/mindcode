# Mindcode syntax

Mindcode is a high-level language for [Mindustry](https://github.com/anuke/mindustry). The language design was inspired mostly by Ruby, but there are quite a few differences. Mindcode aims to provide programmatic access to full Mindustry Logic functionality. Mindustry Logic instructions interacting with Mindustry World are available through functions (see [Function reference for Mindustry Logic 8A](FUNCTIONS_V8A.markdown)). Other instructions are used by expressions, control structures and other statements in Mindcode.

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
* [Function reference for Mindustry Logic 8A](FUNCTIONS_V8A.markdown)

For Mindustry Logic 8 and higher, new mlog instructions and a Mindcode system library of functions are supported:

* [Mindustry Logic 8](MINDUSTRY-8.markdown)
* [System Library](SYSTEM-LIBRARY.markdown)

Schemacode, a schematic definition language, is covered here:

* [Schemacode](SCHEMACODE.markdown)

Supporting tools: 

* [IDE Integration](TOOLS-IDE-INTEGRATION.markdown)
* [Command line tool](TOOLS-CMDLINE.markdown)
* [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown)
* [Schematics Refresher](TOOLS-REFRESHER.markdown)

Additional information on Mindcode and Mindustry Logic:

* [Troubleshooting Mindcode](TROUBLESHOOTING.markdown)
* [Mindustry Tips and Tricks](MINDUSTRY-TIPS-N-TRICKS.markdown)

---

# Mindcode basics

## Program structure

Mindcode program is a sequence of expressions or statements. Statements and expressions are separated by semicolons, as in `a = 5; b = 10;`. Expressions have a value: `1 + 2` is an expression whose value is `3`. Whitespace (spaces, tabs, new-line characters) serves to separate individual _tokens_ - keywords, identifiers, operators and so on, but is otherwise ignored. Indentation, while recommended, is ignored by the compiler.

Mindcode identifiers and keywords are case-sensitive -- `if` is different from `If` (first is a keyword, the second is not and could - but shouldn't - be used as a variable or function name).

A text enclosed between `/*` and `*/` is a comment that can span several lines. Additionally, anything following a `//` is a comment till the end of the line. Comments are completely ignored by Mindcode.

You can use the [remark() function](SYNTAX-4-FUNCTIONS.markdown#remarks) to place comments or notes directly to the compiled code. An enhanced comment is a comment which starts with `///` (three slashes instead of two). This is an [alternative way to enter remarks](SYNTAX-4-FUNCTIONS.markdown#enhanced-comments) into the compiled code. 

## Compilation and optimization

Mindcode provides the web application and the command-line compiler for compiling Mindcode into mlog code. There are several [optimization levels](SYNTAX-5-OTHER.markdown#option-optimization) available:

* `none`: completely switches off optimization. The only practical use is when you have a suspicion that there's a bug in Mindcode - switching off optimizations and running comparing the results might help pinpoint the problem. (In any case, if you suspect a bug in Mindcode, please don't hesitate to [open an issue](https://github.com/cardillan/mindcode/issues/new) - we'll have a look into it.)     
* `basic`: at this level, most optimizations are performed, but some are avoided so that the structure of the compiled program is more understandable. Some optimizations (such as loop unrolling) will still modify the resulting mlog code a lot.  
* `advanced`: all standard optimizations are performed.
* `experimental`: experimental optimizations are kept in a separate category either because they're new and can be easily switched off if there's a bug in them, or because they break backwards compatibility and need to be deactivated until older code is updated to new standard. 

The web application uses `basic` by default, while the command-line compiler uses `advanced`. In both it is possible to easily select a different optimization level.

All examples in this documentation are run on the `basic` level, unless specified otherwise.

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
* `out`
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

Identifiers are names of variables, constants, and functions. Identifiers in Mindcode are case-sensitive. They consist of basic alphanumeric characters (i.e. letters a-z, A-Z and digits 0-9, accented characters aren't allowed) and underscores. The first character must be an underscore or a letter. `_foo` and `bar_baz9` are valid identifiers, `9to5` and `français` are not.

> [!IMPORTANT]
> For backwards compatibility, Mindcode allows to use "kebab-case" identifiers, e.g. `selected-item`. Support for these identifiers will be removed in the future. At this moment, Mindcode produces a warning when it encounters such an identifier in source code.

Mindcode compiler creates variables starting with two underscores, such as `__tmp10` or `__fn0retval`, when generating code. To avoid interference with the compiler, do not use any identifiers starting with two underscores for your variable or function names.

## Built-in variables and constants

Mindustry Logic provides variables and constants that start with `@`. They can be read-only variables (such as `@unit` or `@time`) or effectively constant values (such as `@coal` or `@this`). Built-in variables in mlog use the kebab-case convention (e.g. `@blast-compound`), which is supported in Mindcode.           

Built-in variables and constants can be used as-is in Mindcode, e.g. `@time` or `@titanium-conveyor`. In some situations the leading `@` is dropped (more on this later).

> [!TIP]
> Mindcode has a list of built-in variables and handles some of them specifically, but any unknown built-in values in the source code are compiled in as-is. When Mindustry Logic introduces a new built-in variable, it can be used right away without waiting for a new version of Mindcode to be released, as long as the new built-in variable doesn't need some special handling by the compiler.    

## Literals

### Null literal

`null` is a literal representing the value of null. Null is the value of variables that haven't been initialized and a result of any calculation that is undefined or produced an error in Mindustry.

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
* The maximum value of integers and decimal numbers is  `9223372036854775807` (2<sup>63</sup>-1). Larger numbers 
  wouldn't be recognized and would be read as zero by Mindustry processors. To specify a larger number, an 
  exponential notation must be used. 
* **Mindustry Logic version 7 and earlier**: The range of values that is recognized when using exponential notation is about 10<sup>-38</sup> to 
  10<sup>38</sup>. However, numbers in exponential notation are converted with `float` precision, only keeping 6 
  to 7 valid digits.

To find a way around these constraints, Mindcode always reads the value of the numeric literal and then converts it 
to Mindustry Logic compatible literal using these rules (first applicable rule is used): 

1. If the value is zero, it is encoded as `0`.
2. If the value is between 10<sup>-20</sup> and 2<sup>63</sup>-1, the number is converted to standard decimal 
   notation using 20 digits precision.
3. **Mindustry Logic version 7 and earlier**: If the value is between 10<sup>-38</sup> and 10<sup>38</sup>, the number is converted to exponential notation 
   without using a decimal separator, using `float` precision (which will be used by Mindustry processor when 
   reading the literal as well). If the conversion to float causes loss of precision, a warning is produced.
4. **Mindustry Logic version 8 and later**: If the value is roughly between 10<sup>-308</sup> and 10<sup>308</sup>, the number is converted to exponential notation
   without using a decimal separator. Loss of precision doesn't happen.
5. If none of the above rules is applicable, the conversion isn't possible and a compilation error is produced. 

Note: these rules are applied to the absolute value on the number being encoded. When the number is negative, a minus
sign is then prepended to the created mlog representation.

This processing ensures that numbers within a reasonable range are encoded to use maximal available precision, 
without producing mlog representations that would be unreasonably long. 

Examples of Mindcode literals and their conversion into mlog: 

| Mindcode literal | mlog 7 representation  | mlog 8 representation  |
|:-----------------|:-----------------------|:-----------------------|
| `1`              | `1`                    | `1`                    |
| `3.0`            | `3`                    | `3`                    |
| `1e10`           | `10000000000`          | `10000000000`          |
| `1e-10`          | `0.0000000001`         | `0.0000000001`         |
| `1.23456789e10`  | `12345678900`          | `12345678900`          |
| `1.23456789e-10` | `0.000000000123456789` | `0.000000000123456789` |
| `1.23456789e25`  | `1234568E19`           | `123456789E17`         |
| `1.23456789e-25` | `12345679E-32`         | `123456789E-33`        |
| `1.23456789e100` | _Cannot be encoded_    | `123456789E92`         |

The last three examples show the loss of precision when the number needs to be encoded using exponential notation into Mindustry Logic version 7, and an inability to represent a literal value in mlog 7 at all.

### Binary and hexadecimal literals

Mindustry also supports hexadecimal and binary representation for integers:

* `0x35`
* `0xf1`
* `0b010001101`

These literals are written into the mlog code exactly as they appear in Mindcode. If the value of the literal exceeds the maximum allowed range for a 64-bit signed integer, a compilation error occurs.    

### Boolean literals

Additionally, boolean literals are available:

* `false`
* `true`

Boolean values are represented in mlog as (and indistinguishable from) numerical values `0` and `1`, respectively.

### String literals

Finally, there are string literals, a sequence of characters enclosed in double quotes:

`"A string literal."`

Double quotes in string literals aren't supported in Mindustry Logic at all. Mindcode recognizes them in string literals in source code if they are properly escaped, i.e. `\"`, but generates a warning and replaces them by single quotes in the compiled code (`Hello, "friend"!`, which would be encoded as `"Hello, \"friend\"!"`in the source code, becomes `Hello, 'friend'!`). Embedding double quotes in string literals is deprecated and will be removed in a future release - such literals, when encountered, will cause a syntax error. 

#### Formattable string literals

Formattable string literals are a special case of string literals which can only be used with [`print`, `println`, and `remark` functions](SYNTAX-4-FUNCTIONS.markdown#compile-time-formatting). They are prepended by the `$` character: 

`$"A formattable string literal."`

Double quotes aren't supported in formattable string literal, even if they're escaped. Concatenation of strings with the formattable string literal is supported through the string interpolation:

```
const FORMAT = $"$ITEM_COAL coal: $";
println(FORMAT, vault1.coal); 
```

---

[Next: Variables and constants »](SYNTAX-1-VARIABLES.markdown)
