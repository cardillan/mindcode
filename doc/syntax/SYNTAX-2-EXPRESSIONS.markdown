# Expressions

Most Mindcode expressions utilize operators described below. Other important kinds of expressions are:

* `if` expressions,
* `case` expressions,
* function calls.

Those will be discussed later on.

Expression evaluation in Mindcode is largely based on functionalities provided by Mindustry Logic, with a few small differences. To help understanding the way Mindcode evaluates expressions, the important aspects of Mindustry Logic processing are described first.

# Mindustry Logic

## Error handling

Mindustry Logic deals with errors while executing instructions by ignoring them and going on to the next instruction. Possible errors are:

* trying to assign a value to a numeric literal, object, linked block or a read-only built-in variable,
* performing an operation on invalid or nonexistent block (e.g. writing to a nonexistent memory block, or invoking printflush on a block different from `@message`),
* trying to write to an invalid slot in a memory block,
* etc.

If an instruction generates value to be written to a variable, and there's an error generating the value, the variable is set to `null` instead. Examples of possible errors while generating values include:

* invalid arithmetic operations, such as
  * division by zero,
  * computing square root from a negative number,
  * taking logarithm of a non-positive value,
  * etc.
* reading values from a nonexistent memory block or from an invalid index,
* reading properties of invalid objects (using the `sensor` instruction)
* etc.

## Equality operations

Mindustry Logic provides two equality operations: `equal` and `notEqual`.

When both operands to an equality operation are non-numeric, they are compared as objects. Two objects are equal when they point to the same object (e.g. the same building or the same unit), or if their contents are equal (e.g. two string values containing the same text, or two identical item, such as `@coal`).

When at least one of the values is numeric, the numeric values of both operands are compared:

1. The non-numeric operand, if any, is converted to a number first (see [numeric conversion](SYNTAX-1-VARIABLES.markdown#numeric-conversion)).
2. The two numbers are considered equal if their absolute values differ by less than `0.000001` (10<sup>-6</sup>).

The non-zero precision used to compare two variables is harmless for integer operations, and is actually very useful for code that mixes integer and floating point arithmetics: floating-point numbers have limited precision, and during mathematical operations might accumulate numerical errors. Mindustry Logic compensates for these errors by ignoring small differences between operands.

Furthermore, due to [numeric conversion](SYNTAX-1-VARIABLES.markdown#numeric-conversion), `null` values are equal to zero, and any object (blocks, units, item types or strings) are equal to one.

## Strict equality operation

Mindustry Logic provides one strict equality operator: `strictEqual`. This operator compares the values according to these rules:

1. If one value is numeric and the other is non-numeric, the values are considered non-equal. This comparison distinguishes `null` from zero (unlike both `equal` and `notEqual`).
2. If both values are numeric, they are considered equal if their numerical values match exactly. This comparison recognizes two values as different even if their difference is smaller than `0.000001`  (unlike both `equal` and `notEqual`).
3. If both values are non-numeric, they are considered equal if they have the same value.

## Relational operations

All inequality operations operate on numeric values. If one or both operands are non-numeric, they're converted to a number first and then the normal comparison is performed using full precision. This has the following consequences:

1. Strings cannot be meaningfully compared using inequality operators: inequality operators convert all strings to `1` and thus consider all strings equal.
2. Every positive number is evaluated as greater than zero and every negative number is evaluated as lower than zero.

It is therefore possible for two numbers, say `1.0e-10` and `1.1e-10`, to be evaluated as equal by equality comparison, but one strictly greater than the other using inequality operations.

## Bitwise `or`

Bitwise operations, including `or`, operate on integer values, and thus are affected by [integer conversion](SYNTAX-1-VARIABLES.markdown#integerdouble-conversion), meaning that a non-zero value less than one is converted to zero.

Therefore, even values that wouldn't compare to zero using equality operations may be regarded as `false` when used in `or` operation:

```
set a 0.5
set b 0.5
op notEqual x a 0         # set x to 1 if a is nonzero (i.e. true)
op or y a b               # y = a or b
print x                   # prints 1
print y                   # prints 0
```

## Logical `and`

Logical `and` (`op land` instructions) suffers from a different problem: the operands aren't converted to integer values, but are considered `true` if their value is strictly nonzero.

Therefore,  values that would compare to zero using equality operations may be regarded as `true` when used in `land` operation:

```
set a 0.00000001
set b 0.00000001
op notEqual x a 0         # set x to 1 if a is nonzero (i.e. true)
op land y a b             # y = a and b
print x                   # prints 0
print y                   # prints 1
```

## Output precision

When a value is close to an integer value (i.e. the difference between the value and the closes integer is smaller than `0.000001` - 10<sup>-6</sup>), the integer value is displayed instead of the precise value. This is applied when displaying values of variables using the `print` and `format` instruction and also on the `Vars` screen.

Therefore, when a variable value is displayed as an integer by Mindustry, it is necessary to keep in mind that the actual value of the variable might be different, and that the relational operators (`<`, `<=`, `>`, `>=`) operate over the actual value, not the displayed value of the variable.

To display a value of a variable without rounding, it is possible to use the [`printExactFast()`](SYSTEM-LIBRARY.markdown#printexactfast) or [`printExactSlow()`](SYSTEM-LIBRARY.markdown#printexactslow) functions from the [`math` system library](SYSTEM-LIBRARY.markdown#math-library): `printExactSlow(1.2345e-15);` outputs `1.2345000000000002e-15`. Note that it takes several instructions to print the exact value, and also that the manipulations used to produce the output may introduce some numerical error into the value being outputted.

> [!NOTE]
> Mindustry 7 doesn't apply the above transformation to values that are slightly smaller than an integer value: `print(0.99999999);` outputs `0.99999999` and not `1`. In Mindustry 8 this is fixed and both slightly smaller and slightly larger values are rounded to integers for display. Mindcode compile-time evaluation and emulator match the actual behavior depending on the language target. 

# Mindcode

## Error handing

Mindcode completely adopts Mindustry Logic error handling system.

## Null values in expression

A code produced by Mindcode may handle operators differently from Mindustry Logic when one of the operands is `null`. In this case, the result may also be `null`, if the corresponding operation in Mindustry Logic would produce a zero value. The only exceptions are operators producing boolean values, which are guaranteed to evaluate to `true` or `false` and never produce any other value.

> [!TIP]
> This difference in handling `null` values allows Mindcode to optimize code when one of the operands has a known value which is idempotent with respect to the operator. When Mindcode encounters `a + 0`, for example, it can replace the operation with `a` directly. Similarly, `b * 1` can be replaced with `b` (such expressions typically aren't present directly in the user code, but may arise as a result of optimizations Mindcode performs). Of course, when `a` or `b` happens to be `null`, not executing the operation prevents the conversion from `null` to `0` which would otherwise happen.

The impact of the difference in `null` handling by Mindcode is minimal - as has been mentioned above, the `null` value is handled the same as `0` in most cases. The difference between `null` and `0` is meaningful only in these operations:

* `strictEqual` comparison, represented by `===` and `!==` in Mindcode.
* Printing the value using the `print` or `format` instructions: `null` is output for nulls and `0` for zero values.

## String expressions

Only these operations can be applied to string operands:

* equality operators,
* the `+` operator applied to string literals (resulting in [string concatenation](#string-concatenation)).

> [!IMPORTANT]
> Using string values in expressions not listed above is not supported in Mindcode and its results are undefined.

## Undefined expression

Behavior of expressions producing side effects that may affect any other value in the same expression is intentionally not defined in Mindcode. Examples of such expressions are:

```
a = rand(0);
print(++a + a++);
cell1[++a] = ++a;
print(a++, a);
```

In each of these expressions, Mindcode is free to evaluate individual subexpressions in any order.

To get consistent behavior, these expressions need to be rewritten to eliminate side effects affecting other parts of the same expression, for example:

```
index = ++a;
cell1[index] = ++a;

b = a++;
print(b, a);
```

# Operators

Mindcode operators follow conventions common to many programming languages. Almost every Mindcode operator maps directly to a Mindustry Logic `op` instruction, but several are Mindcode-specific enhancements.

Non-alphanumeric operators don't require spaces around them: `a+b` is a valid expression, for example. Only the `-` sign needs to be separated by a space if it immediately follows a built-in variable: `var duration = @time - start`, as `@time-start` would be processed as a single built-in variable.

The full list of Mindcode operators in the order of precedence is as follows:

| Category            | Operators                                                                            |
|---------------------|--------------------------------------------------------------------------------------|
| postfix             | `var++` `var--`                                                                      |
| prefix              | `++var` `--var`                                                                      |
| unary               | `+` `-` `~` `!` `not`                                                                |
| exponentiation      | `**`                                                                                 |
| multiplicative      | `*` `/` `\` `%`                                                                      |
| additive            | `+` `-`                                                                              |
| shift               | `<<` `>>`                                                                            |
| bitwise AND         | `&`                                                                                  |
| bitwise XOR/OR      | `^` `\|`                                                                             |
| relational          | `<` `<=` `>` `>=`                                                                    |
| equality            | `==` `!=` `===` `!==`                                                                |
| boolean/logical AND | `&&` `and`                                                                           |
| boolean/logical OR  | `\|\|` `or`                                                                          |
| ternary             | `? :`                                                                                |
| assignment          | `=` `**=` `*=` `/=` `\=` `%=` `+=` `-=`<br>`<<=` `>>=` `&=` `^=` `\|=` `&&=` `\|\|=` |

## Postfix and prefix operators

Postfix and prefix operators can only be used with variables. They increment (`++`) or decrement (`--`) the variable by one. The prefix form evaluates to the new value, while the postfix form evaluates to the original value of the variable. This is important when the operator is used in a larger expression:

```
begin
    var i = 5;
    i++;
    println(i);       // Prints 6
    println(++i);     // Prints 7
    println(i++);     // Prints 7
    println(i);       // Prints 8
    printflush(message1);
end;
```

## Unary operators

Unary plus and minus work as expected.

Bitwise complement (`~`) operates on integer values and flips all the bits of the expression. Due to [Mindustry Logic limitations](SYNTAX-1-VARIABLES.markdown#processor-variables), only values up to 2<sup>52</sup> are processed correctly.

> [!TIP]
> Bitwise complement operations are usable in Mindustry Logic because flipping the highest bit makes the number negative, and converting negative numbers under 2<sup>52</sup> from `double` to `long` sets all the topmost bits to ones as well.

Boolean negation (`!` and `not` - both work the same) turns nonzero values into zero and zero values into one. Standard Mindustry Logic [equality convention](#equality-operations) is used to compare the values to zero.

## Exponentiation operator

Exponentiation works as usual: `2**4` is `2 * 2 * 2 * 2`, or 16.

## Multiplicative operators

* `*`: multiplication,
* `/`: floating point division,
* `\`: integer division: `3 \ 2` gives `1`
* `%`: modulo (remainder after division): `10 % 7` gives `3`. Is defined both for integers and floating point numbers.

## Additive operators

Addition and subtraction on numeric values work as usual. 

### String concatenation

The `+` operator may be also used to concatenate string constants and/or literals:

```
const NAME = "John";
message = formalGreeting ? "Good day, " + NAME : "Hey " + NAME + "!";
println(message);
```

It is especially useful to embed icon string constants into larger strings, which would otherwise be impossible:

```
void displayLevel(container, title, item)
    println(title, container.sensor(item));
end ;

displayLevel(vault1, ITEM_COAL + " level: ", @coal);
displayLevel(vault1, ITEM_LEAD + " level: ", @lead);
displayLevel(vault1, ITEM_SAND + " level: ", @sand);
```

Additionally, it is possible to use compile-time concatenation of a string and a non-string value, if both are constant:

```
const TOTAL = 10;
const MESSAGE = " out of " + TOTAL;
for i in 1 .. TOTAL do
    println("Step ", i, MESSAGE);
end;
```

Compile-time string concatenation isn't supported for [formattable string literals](SYNTAX.markdown#formattable-string-literals).

## Shift operators

> [!IMPORTANT]
>  Shift operators are bitwise operators, i.e. manipulate individual bits in an integer value. Their operands are subject to [integer conversion](SYNTAX-1-VARIABLES.markdown#integerdouble-conversion).

These operators shift the numeric value of the first operand left or right by the amount of bits specified by the second operand. If the second operand is outside the `0 .. 63` range, it's value is taken modulo 64.

The results of the operation are easiest inspected in binary form:

* `0b001001 << 2` becomes `0b100100`,
* `0b001011 >> 2` becomes `0b000010` (the bits shifted "outside" the value are lost).

Shifting left is equivalent to an integer multiplication by given power of two, shifting right is an equivalent of an integer division by a power of two.

## Bitwise AND, OR, XOR operators

> [!IMPORTANT]
>  `&`, `^` and `|` are bitwise operators, i.e. manipulate individual bits in an integer value. Their operands are subject to [integer conversion](SYNTAX-1-VARIABLES.markdown#integerdouble-conversion).

Bitwise operations manipulate individual bits of integer representation of their arguments.

* `&` performs bitwise AND operation,
* `^` performs bitwise exclusive OR operation,
* `|` performs bitwise inclusive OR operation.

## Relational operators

Relational operators compare numeric values of their operands using Mindustry Logic [relational operations](#relational-operations).

These are boolean operators and as such are guaranteed to always return `0` or `1`.

## Equality operators

Relational operators compare values of their operands using Mindustry Logic [equality operations](#equality-operations) and [strict equality operation](#strict-equality-operation):

* `==` corresponds to `op equal`
* `!=` corresponds to `op notEqual`
* `===` corresponds to `op strictEqual`
* `!==` doesn't have a direct counterpart in Mindustry Logic, and is compiled into an `op strictEqual` operation and a boolean negation.

These are boolean operators and as such are guaranteed to always return `0` or `1`.

> [!TIP]
> When possible, prefer `==` and `!=` operators to `===` and `!==`. Depending on their exact use, both `===` and `!==` may require one or two instructions in mlog. Mindcode optimizations strive to rearrange the code to use the shorter variant when possible, but sometimes doesn't succeed.

This table lists some examples of comparing various values for equality:

|   `a`   |   `b`   | `a == b` | `a != b` | `a === b` | `a !== b` |
|:-------:|:-------:|:--------:|:--------:|:---------:|:---------:|
| `null`  |   `0`   |  `true`  | `false`  |  `false`  |  `true`   |
| `null`  |   `1`   | `false`  |  `true`  |  `false`  |  `true`   |
| `null`  |   `2`   | `false`  |  `true`  |  `false`  |  `true`   |
| `1e-8`  | `2e-8`  |  `true`  | `false`  |  `false`  |  `true`   |
| `@coal` |   `0`   | `false`  |  `true`  |  `false`  |  `true`   |
| `@coal` |   `1`   |  `true`  | `false`  |  `false`  |  `true`   |
| `@coal` |   `2`   | `false`  |  `true`  |  `false`  |  `true`   |
| `@coal` | `@lead` | `false`  |  `true`  |  `false`  |  `true`   |
|  `"A"`  |   `0`   | `false`  |  `true`  |  `false`  |  `true`   |
|  `"A"`  |   `1`   |  `true`  | `false`  |  `false`  |  `true`   |
|  `"A"`  |   `2`   | `false`  |  `true`  |  `false`  |  `true`   |
|  `"A"`  |  `"B"`  | `false`  |  `true`  |  `false`  |  `true`   |
|  `"A"`  |  `"A"`  |  `true`  | `false`  |  `true`   |  `false`  |

<details><summary>Show code used to generate data for this table.</summary>

```
inline def eval(b)
    b ? "true" : "false";
end;

inline def compare(a, b)
    println($"|`$a`|`$b`|`$`|`$`|`$`|`$`|", eval(a == b), eval(a != b), eval(a === b), eval(a !== b));
end;

inline def compare(stra, strb, a, b)
    println($"|`$stra`|`$strb`|`$`|`$`|`$`|`$`|", eval(a == b), eval(a != b), eval(a === b), eval(a !== b));
end;

println("|   `a`   |   `b`   | `a == b` | `a != b` | `a === b` | `a !== b` |");
println("|:-------:|:-------:|:--------:|:--------:|:---------:|:---------:|");

compare(null, 0);
compare(null, 1);
compare(null, 2);
compare("1e-8", "2e-8", 1e-8, 2e-8);
compare("@coal", 0, @coal, 0);
compare("@coal", 1, @coal, 1);
compare("@coal", 2, @coal, 2);
compare("@coal", "@lead", @coal, @lead);
compare("A", 0);
compare("A", 1);
compare("A", 2);
compare("A", "B");
compare("A", "A");
printflush(message1);
```

</details>

## Boolean and logical AND, OR operators

* `&&` and `and` represent boolean and logical AND operation, 
* `||` and `or` represent boolean and logical OR operation.

Boolean operators are guaranteed to always return `0` or `1`.

Logical operators may return any numeric value or `null`. `null` and zero represent `false`, any other value represents `true`. 

Boolean operators may be a little bit less effective, as additional operations might need to be performed to ensure the resulting value is always `0` or `1`.

> [!TIP]
> For evaluating conditions, using the logical operators (`and`/`or`) is preferred. Only use the boolean operators when you need to limit the output values to `0` or `1`, e.g. `count += active || reactor.heat > 0.5`. Using boolean operators where logical operators would be adequate, for example in `if` conditions, may result in less optimal compiled code.

## Ternary operator

Ternary operator is a conditional operator and is equivalent to an `if` expression having exactly one expression in both true and false branches:

```
var message = value <= limit ? "ok" : "too high";
```

The true branch is executed if the conditional expression (`value <= limit` in our case) [is not equal to zero](#equality-operations). When it is equal to zero, the false branch is executed. 

## Assignment operators

The basic assignment operator assigns a value of its right operand to its left operand. Furthermore, the new value of the left operand is also a value of the entire expression, allowing to use chain assignments: `a = b = c = 0;`

The compound assignment operators combine arithmetic operation with an assignment: `x += 1` is equivalent to `x = x + 1`.

Assignment operators are evaluated from right to left:

```
x += y *= 2;
```
is the same as
```
y = y * 2;
x = x + y;
```

# Constant expressions

Expressions or parts of expressions that are constant are evaluated at compile time. For example, `print(60 / 1000)` compiles to `print 0.06`, without an `op` instruction that would compute the value at runtime. Most of Mindcode operators and deterministic built-in functions can be used in constant expressions.

Constant expressions can be used in [constant](SYNTAX-1-VARIABLES.markdown#constants) and [program parameter](SYNTAX-1-VARIABLES.markdown#program-parameters) declarations.

Furthermore, expressions that contain some constant subexpressions (e.g. `ticks * 60 / 1000` contains a constant subexpression `60 / 1000`) may be partially evaluated by the [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#constant-folding).

> [!TIP]
> Compile-time expression evaluation gives identical results to performing the computation on an actual Mindustry Logic processor, emulating the behavior corresponding to selected language target.

## Constant expressions in Mindustry Logic 7 and earlier

If the result of a constant expression is a value which [cannot be encoded into an mlog literal](SYNTAX.markdown#specifics-of-numeric-literals-in-mindustry-logic), the expression isn't evaluated at compile time, but rather computed at runtime. If the resulting value can only be encoded to mlog with loss of precision, the expression isn't evaluated at compile time, unless the value is assigned to a constant or program parameter. In such case a warning is issued.   

When evaluating expressions, it isn't required that all the intermediate values can also be encoded to mlog, just the final ones:

```
#set target = 7;
print(10 ** 50);            // Cannot be evaluated
print(10 ** (2 * 25));      // Multiplication can be evaluated, exponentiation cannot
print(log10(10 ** 50));     // Can be evaluated even though 10 ** 50 cannot
```

produces

```
op pow *tmp0 10 50
print *tmp0
print *tmp0
print 50
```

# Range expressions

A special class of expressions are **range expressions**. Range expressions define a numerical interval with a lower bound and an upper bound. There are two kinds of ranges:

* inclusive ranges: `lower .. upper`
* exclusive ranges: `lower ... upper`

For inclusive ranges, the upper bound belongs to the range. For exclusive ranges, the upper bound doesn't belong to the range. Ranges are constant when both lower and upper bounds are constant expressions.

Ranges cannot be assigned to variables and can only be used in several contexts:

* Specifying memory ranges in heap and stack allocation (requires constant ranges),
* Range iteration loops,
* Matching against a range in `case` expressions.

# Addendum: comparison precision

To illustrate hoe equality and relational operations work in Mindustry Logic, consider this example:

```
param a = 0.1;

b = 1;
for i in 1 .. 10 do
    b -= a;
end;

println(b > 0 ? "Greater than zero" : "Not greater then zero");
println(b == 0 ? "Equal to zero" : "Not equal to zero");
println(b === 0 ? "Strictly equal to zero" : "Not strictly equal to zero");
printflush(message1);
stopProcessor();
```

After ten iterations, we'd expect the value to be equal to `0` precisely, but due to the numerical imprecision this is not the case. As the equality comparison compensates for small differences, but relational operators do not, the code produces this result:

```
Greater than zero
Equal to zero
Not strictly equal to zero
```

> [!TIP]
> To compare numbers using different precision, you can use the `isZero` or `isEqual` functions from the [`math` system library](SYSTEM-LIBRARY.markdown#math-library): `println(isZero(b) ? "equal to zero" : "not equal to zero");` outputs `not equal to zero`.

> [!IMPORTANT]
> As has been shown above, `x <= 0` may evaluate to `false`, even though `x == 0` evaluates to `true`. This is especially important to consider if you use these operators in loop conditions where the loop control variable is updated using floating point operations: the number of iterations of the loop may be different from what would be expected if all numerical operations were absolutely precise.

---

[« Previous: Variables and constants](SYNTAX-1-VARIABLES.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Control flow statements »](SYNTAX-3-STATEMENTS.markdown)
