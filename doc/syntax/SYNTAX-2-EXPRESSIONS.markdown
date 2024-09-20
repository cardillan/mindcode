# Expressions

Most expressions utilize some operators described below. Other important types of expressions are

* `if` expressions,
* `case` expressions,
* function calls.

Those will be discussed later on.

# Operators

Most operators do the expected: `+`, `-`, `*`, `/`, and they respect precedence of operation, meaning we multiply and
divide, then add and subtract. Add to this operator `\`, which stands for integer division. For example:

```
3 / 2; // returns 1.5
3 \ 2; // returns 1
```

Almost every operator maps directly to Mindustry Logic ones, but several are Mindcode-specific enhancements. These 
enhancements include boolean negation (`!` and `not`) operators, unary minus (`-`), the strict inequality (`!==`) 
operator, or compound assignment operators.

The full list of Mindcode operators in the order of precedence is as follows:

1. `!`, `not`: boolean negation,  `~`: binary negation
2. `**`: exponentiation (`2**4` is `2 * 2 * 2 * 2`, or 16)
3. `-`: unary minus (negates the value of the following expression)
4. `*`: multiplication,  `/`: floating point division,  `\`: integer division, `%`: modulo
5. `+`: addition,  `-`: subtraction
6. `<<`: left-shift, `>>`: Right-shift (`1 << 2` is easier to read in binary: `0b0001 << 2` becomes `0b0100`)
7. `&`: binary and (useful for flags)
8. `|`: binary or (useful for flags),  `^`: binary [xor (exclusive-or)](https://en.wikipedia.org/wiki/Exclusive_or)
9. `<`: less than, `<=`: less than or equal,  `>=`: greater than or equal,  `>`:  greater than
10. `==`: equality, `!=`: inequality (does not equal), `===`, `!==`: strict equality and non-equality -- see below
11. `&&`, `and`: boolean and (`reactor1.thorium > 0 and reactor1.cryofluid <= 10`)
12. `||`, `or`: boolean or
13. `? :`: ternary operator (`value <= limit ? "ok" : "too high"`)
14. `=`, `**=`, `*=`, `/=`, `\=`, `%=`, `+=`, `-=`, `<<=`, `>>=`, `&=`, `|=`, `^=`, `&&=`, `||=`: assignments

Non-alphanumeric operators generally don't require spaces around them -- `a+b` is a valid expression, for example.
Only the `-` sign needs to be separated by spaces, as `a-b` is a valid identifier and would be interpreted as variable name instead of an expression.

## Compound assignment operators

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

## Boolean operators

Boolean operators are:
* `<`, `<=`,  `>=`,  `>`
* `==`, `!=`, `===`, `!==`
* `&&`, `and`

These operators always produce either `true` or `false` - even if their operands themselves aren't boolean values.
Note that `true` is identical to `1` and `false` is identical to `0` in Mindustry Logic.

The `||` and `or` operators, although they are meant to provide boolean or, may not produce boolean values
when their operands aren't boolean themselves. They're identical with the bitwise or (`|`).
It works as expected with control statements, e.g.

```
items = vault1.totalItems;
while items or alive do
    ...
end;
```

Conditional expressions in control statements are considered true if they evaluate to a nonzero value.

## Equality operators

There are two kinds of equality operators: strict ones (`===`, `!==`) and non-strict ones (`==`, `!=`).

The strict operators will only consider two values to be equal when they're both numerical and having the same value,
or they both point to the same object, or they're both `null`.

The non-strict equality operators behave differently when one of the values being compared is numerical and the other is not.
In that scenario, the values are considered equal in these cases:
* one of the values is `null` and the other is `0` (zero), or
* one of the values is an object and the other is `1` (one).

|   `a`   |   `b`   | `a == b` | `a != b` | `a === b` | `a !== b` |
|:-------:|:-------:|:--------:|:--------:|:---------:|:---------:|
| `null`  |   `0`   |  `true`  | `false`  |  `false`  |  `true`   |
| `null`  |   `1`   | `false`  |  `true`  |  `false`  |  `true`   |
| `null`  |   `2`   | `false`  |  `true`  |  `false`  |  `true`   |
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
    printf("|$a|$b|$|$|$|$|\n", eval(a == b), eval(a != b), eval(a === b), eval(a !== b));
end;

compare(null, 0);
compare(null, 1);
compare(null, 2);
compare(@coal, 0);
compare(@coal, 1);
compare(@coal, 2);
compare(@coal, @lead);
compare("A", 0);
compare("A", 1);
compare("A", 2);
compare("A", "B");
compare("A", "A");
printflush(message1);
```

</details>

# Expressions without valid evaluation

When an expression cannot be mathematically evaluated (e.g. `1 / 0`, `sqrt(-1)`, `log(0)`, ...), the resulting value 
of the expression is `null`. Additionally, `null` values are treated as zero (`0`). Therefore, `15 + 6 / 0` 
evaluates to `15`.

Objects used in numerical expressions always have a value of `1`. `n = @unit * 10` will therefore assign `10` to `n` 
if a unit is bound, `0` otherwise.

# Constant expressions

Expressions or parts of expressions that are constant are evaluated at compile time. For example, `print(60 / 1000)` 
compiles to `print 0.06`, without an `op` instruction that would compute the value at runtime. Most of Mindcode 
operators and deterministic built-in functions can be used in constant expressions. 

If the result of a constant expression is a value which
[cannot be encoded into an mlog literal](SYNTAX.markdown#numeric-literals-in-mindustry-logic), the expression isn't 
evaluated at compile time, but rather computed at runtime. Expressions are evaluated to the maximum extent possible,
and it isn't required that all the intermediate values can also be encoded to mlog, just the final ones:

```
print(10 ** 50);            // Cannot be evaluated
print(10 ** (2 * 25));      // Multiplication can be evaluated, exponentiation cannot
print(sqrt(10 ** 50));      // Can be evaluated even though 10 ** 50 cannot
```

produces

```
op pow __tmp0 10 50
print __tmp0
op pow __tmp1 10 50
print __tmp1
print 1E25
end
```

If the value of the constant expression can be only encoded to mlog with loss of precision, a warning is issued.

Constant expressions are evaluated during code generation. Furthermore, expressions that contain some constant 
subexpressions (e.g. `ticks * 60 / 1000` contains a constant subexpression `60 / 1000`) may be partially evaluated 
by the [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#constant-folding).

# String expressions

Mindustry Logic doesn't evaluate string expressions at runtime, or better, doesn't evaluate them correctly. When a 
mathematical operation is performed on string variables or values, the string value is converted to `1` for the 
purpose of the evaluation. This is also true for the `+` operation, which in some other languages performs string 
concatenation when used on string values. 

Because string operations do not work anyway, the Mindcode compiler produces a compilation error when it detects a 
string-based runtime expression:

```
x = "A";
y = x + "B";
print(y);
```

Not all such situations are recognized, for example

```
x = "A";
y = "B";
print(x + y);
```

compiles, but produces `2`, as Mindustry Logic converts all string values to a numerical `1` for all operations.

The only supported operation is concatenation of string constants and literals using the `+` operator, such as: 

```
const NAME = "John";
message = formalGreeting ? "Hey " + NAME + "!" : "Good day, " + NAME;
print(message);
```

It is most useful to embed icon string constants into larger strings, which would otherwise be impossible:

```
def displayLevel(container, title, item)
    println(title, container.sensor(item));
end ;

displayLevel(vault1, ITEM_COAL + " level: ", @coal);
displayLevel(vault1, ITEM_LEAD + " level: ", @lead);
displayLevel(vault1, ITEM_SAND + " level: ", @sand);
```

Additionally, it is possible to use compile-time concatenation of a string and a non-string value, if both are 
constant:

```
const TOTAL = 10;
const MESSAGE = " out of " + TOTAL;
for i 1 .. TOTAL do
    println("Step ", i, MESSAGE);
end;
```

# Range expressions

A special class of expressions are **range expressions**.
Range expressions define a numerical interval with a lower bound and an upper bound.
There are two kinds of ranges:

* inclusive ranges: `lower .. upper`
* exclusive ranges: `lower ... upper`

For inclusive ranges, the upper bound belongs to the range.
For exclusive ranges, the upper bound doesn't belong to the range.
Ranges are constant when both lower and upper bounds are constant expressions.

Ranges cannot be assigned to variables and can only be used in several contexts:

* Specifying memory ranges in heap and stack allocation (requires constant ranges),
* Range iteration loops,
* Matching against a range in `case` expressions.

# Statement evaluation

Statements, as opposed to expressions, do not produce a value.
Mindcode syntax, somewhat incorrectly, still allows them to be used in assignments.
In these cases the statements get evaluated to `null`:

```
value = def foo()
    return 5;
end;
print(value);    // Outputs "null"
```

Perhaps somewhat surprisingly, constant declarations are statements too:

```
x = const VALUE = 5;
print(x);        // "null" again
```

---

[« Previous: Variables and constants](SYNTAX-1-VARIABLES.markdown) &nbsp; | &nbsp; [Next: Control flow statements »](SYNTAX-3-STATEMENTS.markdown)
