# Mindcode

Mindcode is a high-level language for [Mindustry](https://github.com/anuke/mindustry). The language design was inspired mostly by Ruby, but there are quite a few differences. Mindcode aims to provide programmatic access to full Mindustry Logic functionality. Mindustry Logic instructions interacting with Mindustry World are available through functions (see [Function reference for Mindustry Logic 8.0](FUNCTIONS-80.markdown)). Other instructions are used by expressions, control structures and other statements in Mindcode.

This documentation covers the basics of Mindcode syntax:

* [Mindcode basics](#mindcode-basics)
* [Variables](SYNTAX-1-VARIABLES.markdown)
* [Expressions](SYNTAX-2-EXPRESSIONS.markdown)
* [Control flow statements](SYNTAX-3-STATEMENTS.markdown)
* [Functions](SYNTAX-4-FUNCTIONS.markdown)
* [Advanced features](SYNTAX-5-OTHER.markdown)
* [Code optimization](SYNTAX-6-OPTIMIZATIONS.markdown)

Function reference for individual compiler versions:

* [Function reference for Mindustry Logic 6.0](FUNCTIONS-60.markdown)
* [Function reference for Mindustry Logic 7.0](FUNCTIONS-70.markdown)
* [Function reference for Mindustry Logic 7.1](FUNCTIONS-71.markdown)
* [Function reference for Mindustry Logic 8.0](FUNCTIONS-80.markdown)

For Mindustry Logic 8 and later, new mlog instructions and a Mindcode system library of functions are supported:

* [Mindustry Logic 8](MINDUSTRY-8.markdown)
* [System Library](SYSTEM-LIBRARY.markdown)

Schemacode, a schematic definition language, is covered here:

* [Schemacode](SCHEMACODE.markdown)

Supporting tools: 

* [IDE Integration](TOOLS-IDE-INTEGRATION.markdown)
* [Command line tool](TOOLS-CMDLINE.markdown)
* [Processor emulator](TOOLS-PROCESSOR-EMULATOR.markdown)
* [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown)
* [Schematics Refresher](TOOLS-REFRESHER.markdown)
* [Mlog Decompiler](TOOLS-MLOG-DECOMPILER.markdown)
* [Testing Framework](TOOLS-TESTING-TOOL.markdown)

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

> [!NOTE]
> Enhanced comments can only be used at places where a statement can start, it is not possible to insert them in the middle of a statement. The following code demonstrates it:
> 
> ```
> for i = 0;            /// Cannot put an enhanced commment here
>     i < 10;           /// Neither here
>     i++               /// Nope
> do
>     /// But it can be here
>     println(i);       /// Or here
> end;
> ```

### Libraries and external files

Mindcode language contains several [system libraries](SYSTEM-LIBRARY.markdown). To add a system library or an external file to compiled code, use the `require` statement:

```
require units;
require "library\drawings.mnd";
```

The `require` statement can appear anywhere in the compiled code, although it is recommended to use it at the top of the source code. The code from the required file is compiled separately and combined with the code from the original file. Syntax errors are reported in the file where they occurred.

A file added though a `require` statement can also use a `require` statement. Circular dependencies between files are resolved and each file is compiled only once.

In the web application, the `require` statement can only be used to import system libraries. Using the statement with external files is not supported.

In the command-line tool, using the `require` statement with external files is analogous to the [`--append` command-line argument](TOOLS-CMDLINE.markdown#additional-input-files).

> [!IMPORTANT]
> At this moment, all included files share the same variable namespace. Functions, variables, constants and program parameters defined in one source file may conflict with identically named function, variable, constant or program parameter in another included file. 

## Compilation and optimization

Mindcode provides the web application and the command-line compiler for compiling Mindcode into mlog code. There are several [optimization levels](SYNTAX-5-OTHER.markdown#option-optimization) available:

* `none`: completely switches off optimization. The only practical use is when you have a suspicion that there's a bug in Mindcode - switching off optimizations and running comparing the results might help pinpoint the problem. (In any case, if you suspect a bug in Mindcode, please don't hesitate to [open an issue](https://github.com/cardillan/mindcode/issues/new) - we'll have a look into it.)     
* `basic`: at this level, most optimizations are performed, but some are avoided so that the structure of the compiled program is more understandable. Some optimizations (such as loop unrolling) will still modify the resulting mlog code a lot.  
* `advanced`: all standard optimizations are performed.
* `experimental`: experimental optimizations are kept in a separate category either because they're new and can be easily switched off if there's a bug in them, or because they break backwards compatibility and need to be deactivated until older code is updated to new standard. 

The web application uses `basic` by default, while the command-line compiler uses `advanced`. In both it is possible to easily select a different optimization level.

All examples in this documentation are run on the `basic` level, unless specified otherwise.

## Syntax modes

Mindcode supports multiple syntax modes tailored for different needs:

* Relaxed syntax: useful for shorter scripts, as it requires less boilerplate code.
* Strict syntax: useful for larger projects, as it enforces additional rules designed to make source code more maintainable.
* Mixed syntax: designed to help with a transition of relaxed syntax code to the strict standard. In this mode, code is compiled using the relaxed syntax rules, but all violations of the strict syntax rules are reported as warnings.
 
Relaxed syntax is the default one. Syntax mode can be changed by the command-line switch or through the `#set syntax = mode;` compiler directive, where `mode` is one of `strict`, `relaxed` or `mixed`.

Code adhering to the strict syntax produces the same output regardless of the syntax mode used to compile it. 

### Relaxed syntax

* **Code in global scope.** It is permissible to place executable code outside a code block (e.g. `print("Hello!"); printflush(message1);` is a valid source code).
* **Optional variable declarations.** While variables can be explicitly declared, these declarations are optional. When variables aren't declared, they can be used freely in the source code and their scope is determined by naming convention.

> [!TIP]
> Using the naming convention to determine variable scopes leads to some constrains on allowed variable names: names of linked blocks generally cannot be used to name any other variable, and upper-case variable names (which are global by convention) cannot be used as function parameters. 

### Strict syntax

> [!NOTE]
> Strict syntax is currently an experimental feature and its rules might still change.

* **No code in global scope.** Only declarations are allowed in the global scope. All code must be enclosed either in a code block (i.e. between the `begin` and `end` keywords), or in a function.
* **Explicit variable declarations.** All variables must be declared before being used, even variables representing linked blocks. The variables are valid in the code block in which they were declared.

> [!TIP]
> At this moment, several code blocks can exist in the global scope. They are compiled in the order in which they're encountered in the program.

The requirement to declare variables explicitly helps to identify misspelled variable names, as unknown variable names cause compilation errors, and cases when a single variable is used for multiple different purposes, as repeated variable declarations also cause compilation errors.

> [!TIP]
> In strict mode, the additional constraints on variable names are not enforced. Variables can be named after linked blocks and upper-case identifiers can be used for local variables, including function parameters.

Example of a code adhering to the strict syntax:

```
#set syntax = strict;

// These are declarations and need to be present in the global scope:
allocate heap in cell1;
const pi = 3.1415926535898;

// Enhanced comment is also allowed in global scope
param UNIT_TYPE = @flare;       /// Type of unit to use:

// A global, external variable
external total = 0;
var message = findMessage();   // Initialization through function call is possible

// All code must be enclosed in a code block or in a function
begin
    var firstUnit = ubind(UNIT_TYPE);
    if firstUnit == null then
        println($"No unit of type $UNIT_TYPE found.");
    else
        for var unit = ubind(UNIT_TYPE); unit != firstUnit; unit = ubind(UNIT_TYPE) do
            if firstUnit.@dead then
                println("A unit was killed!");
                printflush(message);
                end();
            end;
            total++;
        end;

        println($"$total unit(s) of type $UNIT_TYPE found.");
    end;

    printflush(message);
end;

def findMessage()
    for var i in 0 ... @links do
        var block = getlink(i);
        if block.@type == @message then return block; end;
    end;

    return null;
end;
```

## Keywords

This is a list of Mindcode keywords:

* `allocate`
* `begin`
* `break`
* `cached`
* `case`
* `const`
* `continue`
* `def`
* `do`
* `else`
* `elsif`
* `end`
* `external`
* `false`
* `for`
* `heap`
* `if`
* `in`
* `inline`
* `linked`
* `loop`
* `noinit`
* `noinline`
* `null`
* `out`
* `param`
* `return`
* `require`
* `stack`
* `then`
* `true`
* `var`
* `void`
* `volatile`
* `when`
* `while`

The following keywords do not have any function in Mindcode, but are reserved for compatibility reason or for future use: 

* `elif`
* `elseif`

Keywords cannot be used as function or variable names.

## Identifiers

Identifiers are names of variables, constants, and functions. Identifiers in Mindcode are case-sensitive. They consist of basic alphanumeric characters (i.e. letters a-z, A-Z and digits 0-9, accented characters aren't allowed) and underscores. The first character must be an underscore or a letter. `_foo` and `bar_baz9` are valid identifiers, `9to5` and `français` are not.

Variables created by the Mindcode compiler never interfere with user variables or functions. 

## Built-in variables and constants

Mindustry Logic provides variables and constants that start with `@`. They can be read-only variables (such as `@unit` or `@time`) or effectively constant values (such as `@coal` or `@this`). Multi-word built-in variable names in mlog use the kebab-case convention (e.g. `@blast-compound`).           

Built-in variables and constants can be used as-is in Mindcode, e.g. `@time` or `@titanium-conveyor`. The leading `@` is always present.

> [!TIP]
> Mindcode has a list of built-in variables and handles some of them specifically, but any unknown built-in values in the source code are compiled in as-is. When Mindustry Logic introduces a new built-in variable, it can be used right away without waiting for a new version of Mindcode to be released, as long as the new built-in variable doesn't need some special handling by the compiler.    

## Literals

### Null literal

`null` is a literal representing the mlog value of `null`. Null is the value of variables that haven't been initialized and a result of any calculation that is undefined or produced an error in Mindustry.

### Boolean literals

Additionally, boolean literals are available:

* `false`
* `true`

Boolean values are represented in mlog as (and indistinguishable from) numerical values `0` and `1`, respectively.

### Decimal literals

Mindcode supports the classic decimal notation for integers and floats, including scientific notation:

* `1`
* `3.14159`
* `-35`
* `-9381.355`
* `1e10`
* `1.5e-5`
* `-6.45e+8`

### Binary and hexadecimal literals

Mindustry also supports hexadecimal and binary representation for integers:

* `0x35`
* `0xf1`
* `0b010001101`

These literals are written into the mlog code exactly as they appear in Mindcode. If the value of the literal exceeds the maximum allowed range for a 64-bit signed integer, a compilation error occurs.    

### Color literals

Mindustry Logic recognizes special literals designed for encoding colors. These literals use `%` (a percent character) as a prefix, followed by six or eight hexadecimal digits. The first six digits encode red, green and blue channels respectively, the (optional) 7th-8th digits encode the alpha channel (0 = transparent, 255 = opaque), when not specified, the alpha channel defaults to opaque:

* `%FF0000`: bright red
* `%007F00`: dark green
* `%ffffff7f`: white at 50% opacity.

These literals are written into the mlog code exactly as they appear in Mindcode. If the value of the literal doesn't fit the supported format, a compilation error occurs.

If the result of a compile-time evaluated expression lies within the range of color literals, it is encoded into mlog as a color literal.

### Character literals

Mindcode supports character literals. These consists of a single character enclosed in single quotes: `'a'`. It is possible to use both single- and double quotes in a character literal, i.e. `'\''` and `'"'` respectively.

Character literals have a numeric value equal to the ASCII value of the enclosed character. When converting the literal to mlog, the numeric value is used, as Mindustry Logic doesn't support character literals - `'A'` is equivalent to `65`, for example.

Character literals are most useful with the Mindustry Logic 8 `printchar` instruction, for example:

```
#set target = 8;

// Prints ABCDEFGHIJKLMNOPQRSTUVWXYZ
for ch in 'A' .. 'Z' do
   printchar(ch);
end;
```

It is also possible to output double quotes to the text buffer using this `printchar('"');` (or `printchar(34);`, which is equivalent, but less readable). There's no other way to print double quotes in Mindustry Logic. 

### String literals

Finally, there are string literals, a sequence of characters enclosed in double quotes:

`"A string literal."`

> [!NOTE]
> It is not possible to include a double quote inside string literals, because Mindustry Logic itself doesn't support them. Trying to include a double quote in a string literal using backslash escape, as is usual in other languages (e.g. `"This is an embedded \"quote\""`) leads to syntax error.   

### Formattable string literals

Formattable string literals are a special case of string literals which can only be used with [`print`, `println`, and `remark` functions](SYNTAX-4-FUNCTIONS.markdown#compile-time-formatting). They are prepended by the `$` character: 

`$"A formattable string literal."`

Concatenation of strings with the formattable string literal is supported through the string interpolation:

```
const FORMAT = $"$ITEM_COAL coal: ${vault1.@coal}";
println(FORMAT); 
```

## Specifics of numeric literals in Mindustry Logic

When Mindustry processes the mlog code in Mindustry Logic, it handles numeric literals with some limitations:

* Decimal numeric literal can be an integer, a decimal number or a number in exponential notation where the mantissa doesn't contain a decimal separator, and may include a leading minus. `15`, `-187.5786` and `1e10` are valid numbers, while `1.4e10` is not, as it contains both `.` and `e` characters.
* Binary and hexadecimal literals always start with `0b` or `0x` prefix. Negative values aren't supported.   
* Integers and decimal numbers are ultimately stored with `double` precision - this representation supports up to about 16 valid digits. Decimal numbers may specify a lot more digits after the decimal separator, but superfluous ones will be ignored.
* All integers up to 9,007,199,254,740,992 (2<sup>53</sup>) can be represented precisely in `double` precision.
* Integers between 2<sup>53</sup> and 2<sup>63</sup>-1 may lose precision when converted to `double`.
* The maximum value of integers and decimal numbers is 9,223,372,036,854,775,807 (2<sup>63</sup>-1). The results of parsing integer literals larger than this value are not consistent; apparently, sometimes an arithmetic overflow happens, otherwise the result is `null`.
* **Mindustry Logic version 7 and earlier**: The range of values that is recognized when using exponential notation is about 10<sup>-38</sup> to 10<sup>38</sup>. However, numbers in exponential notation are converted with `float` precision, only preserving 6 to 7 valid digits.

To find a way around these constraints, Mindcode always reads the value of the numeric literal and then converts it to Mindustry Logic compatible literal using these rules (first applicable rule is used):

1. Floating point literals (i.e. decimal literals containing either decimal point or exponent):
   1. If the value is negative, the absolute value is converted according to these rules, and a minus sign is prepended to the result. 
   2. If the value is zero, it is encoded as `0`.
   3. If the value is between 10<sup>-20</sup> and 2<sup>63</sup>-1, the number is converted to decimal notation using 20 digits precision and a decimal separator when needed.
   4. **Mindustry Logic version 7 and earlier**: If the value is between 10<sup>-38</sup> and 10<sup>38</sup>, the number is converted to exponential notation
      without using a decimal separator, using `float` precision (which will be used by Mindustry processor when
      reading the literal as well). If the conversion to float causes loss of precision, a warning is produced.
   5. **Mindustry Logic version 8 and later**: If the value is roughly between 10<sup>-308</sup> and 10<sup>308</sup>, the number is converted to exponential notation
      without using a decimal separator. Loss of precision doesn't happen.
   6. If none of the above rules is applicable, the conversion isn't possible and a compilation error is produced.
2. Binary, decimal or hexadecimal integer literals:
   1. If the literal is a positive binary, hexadecimal or decimal integer lower than 2<sup>63</sup>, the literal is used exactly as specified in the source code.
   2. If the literal is negative, it is converted to decimal representation prepended by a unary minus.
   3. If the value of the literal exceeds the maximum supported value, a compilation error is produced.
   4. If the value of the literal is larger than 2<sup>52</sup>, Mindcode emits a 'Literal exceeds safe range for integer operations' warning (the warning threshold is set to 2<sup>52</sup> and not 2<sup>53</sup>, because values above 2<sup>52</sup> are potentially unsafe for binary complement operations).

This processing ensures that numbers within a reasonable range are encoded to use maximal available precision, without producing mlog representations that would be unreasonably long.

Examples of Mindcode literals and their conversion into mlog:

| Mindcode literal | mlog 7 representation  | mlog 8 representation  |
|:-----------------|:-----------------------|:-----------------------|
| `1`              | `1`                    | `1`                    |
| `-008`           | `-008`                 | `-008`                 |
| `0b10101`        | `0b10101`              | `0b10101`              |
| `-0xFF`          | `-255`                 | `-255`                 |
| `3.0`            | `3`                    | `3`                    |
| `1e10`           | `10000000000`          | `10000000000`          |
| `-1e-10`         | `-0.0000000001`        | `-0.0000000001`        |
| `1.23456789e10`  | `12345678900`          | `12345678900`          |
| `1.23456789e-10` | `0.000000000123456789` | `0.000000000123456789` |
| `1.23456789e25`  | `1234568E19`           | `123456789E17`         |
| `1.23456789e-25` | `12345679E-32`         | `123456789E-33`        |
| `1.23456789e100` | _Cannot be encoded_    | `123456789E92`         |

The last three examples show the loss of precision when the number needs to be encoded using exponential notation into Mindustry Logic version 7, and an inability to represent a literal value in mlog 7 at all.

---

[Next: Variables and constants »](SYNTAX-1-VARIABLES.markdown)
