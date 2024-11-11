# Expressions

Most expressions utilize some operators described below. Other important types of expressions are

* `if` expressions,
* `case` expressions,
* function calls.

Those will be discussed later on.

Mindcode evaluates expressions and mathematical functions using the corresponding Mindustry Logic instructions. Most of the Mindcode expression evaluation rules therefore follow Mindustry Logic rules, with only a few exceptions documented here.

Sometimes the expression evaluation in Mindustry Logic itself might be slightly unexpected, and these cases are also described in this document.     

## Null values in expression

Handling `null` values in Mindustry Logic is governed by these rules:

1. When an invalid operation is attempted, such as `1 / 0`, `sqrt(-1)`, or `log(0)`, the resulting value is `null`.
2. When a `null` value is used in a mathematical operation, it is converted to zero.

However, a code produced by Mindcode may handle operators differently from Mindustry Logic when one of the operands is `null`. In this case, the result may also be `null`, if the corresponding operation in Mindustry Logic would produce a zero value. The only exceptions are operators producing boolean values, which are guaranteed to evaluate to `true` or `false` and never produce any other value.   

Note: this difference in handling `null` values allows Mindcode to optimize code when one of the operands has a known value which is idempotent with respect to the operator. When Mindcode encounters `a + 0`, for example, it can replace the operation with `a` directly. Similarly, `b * 1` can be replaced with `b` (such expressions typically aren't present directly in the user code, but may arise as a result of optimizations Mindcode performs). Of course, when `a` or `b` happens to be `null`, not executing the operation prevents the conversion from `null` to `0` which would otherwise happen.

The impact of the difference in `null` handling by Mindcode is minimal - as has been mentioned above, the `null` value is handled the same as `0` in most cases. The difference between `null` and `0` is meaningful only in these operations:

- `strictEqual` comparison, represented by `===` and `!==` in Mindcode.
- Printing the value using the `print` or `format` instructions: `null` is output for nulls and `0` for zero values.

# Operators

Most operators do the expected: `+`, `-`, `*`, `/`, and they respect precedence of operation, meaning we multiply and divide, then add and subtract. Add to this operator `\`, which stands for integer division. For example:

```
3 / 2; // returns 1.5
3 \ 2; // returns 1
```

Almost every operator maps directly to Mindustry Logic ones, but several are Mindcode-specific enhancements. These enhancements include boolean negation (`!` and `not`) operators, unary minus (`-`), the strict inequality (`!==`) operator, the boolean or operator (`||`), or compound assignment operators.

Non-alphanumeric operators generally don't require spaces around them: `a+b` is a valid expression, for example.
Only the `-` sign needs to be separated by spaces, as `a-b` is an identifier and would be interpreted as variable name instead of an expression.

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
11. `&&`, `and`: boolean/logical and (`reactor1.thorium > 0 and reactor1.cryofluid <= 10`)
12. `||`, `or`: boolean/logical or
13. `? :`: ternary operator (`value <= limit ? "ok" : "too high"`)
14. `=`, `**=`, `*=`, `/=`, `\=`, `%=`, `+=`, `-=`, `<<=`, `>>=`, `&=`, `|=`, `^=`, `&&=`, `||=`: assignments

## Boolean operators

For the following operators, their resulting value is guaranteed to be either `false` or `true` (`0` or `1` respectively) for all possible values of their operands, including `null` values:
- equality operators: `==`, `!=`, `===`, `!==`
- relational operators: `<`, `<=`, `>`, `>=`
- boolean operators: `&&`, `||`

> [!TIP]
> The "boolean or" operator (`||`) doesn't have a corresponding Mindustry Logic instruction, and is encoded using two mlog instructions. Similarly, while the "boolean and" operator (`&&`) does have a corresponding mlog instruction, the guarantee to always produce a boolean value precludes using some optimizations on it. 
>
> For evaluating conditions, using the logical operators (`and`/`or`) is preferred. Only use the boolean operators when you need to limit the output values to `0` or `1`, e.g. `count += is_active || reactor.heat > 0.5`. Using boolean operators where logical operators would be adequate, for example in `if` conditions, may result in less optimal mlog code.

## Logical operators

The logical operators (`and`, `or`) behave slightly differently than boolean operators (`&&`, `||`) - specifically, they may produce a `null` value (equivalent to `false`) or any non-zero value (equivalent to `true`). They are suitable to be used in control statements, where conditional expressions are considered true if they evaluate to a nonzero value:

```
items = vault1.totalItems;
while items and !@unit.@dead do
    ...
end;
```

## Equality operators

There are two kinds of equality operators: strict ones (`===`, `!==`) and non-strict ones (`==`, `!=`).

The strict operators will only consider two values to be equal when they're both numerical and having the same value, or they both point to the same object, or they're both `null`.

The non-strict equality operators behave differently when one of the values being compared is numerical and the other is not. In that scenario, the values are considered equal in these cases:

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
    println($"|$a|$b|$|$|$|$|", eval(a == b), eval(a != b), eval(a === b), eval(a !== b));
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

[Run it](https://mindcode.herokuapp.com/?run=true&mindcode=inline%20def%20eval%28b%29%0A%20%20%20%20b%20%3F%20%22true%22%20%3A%20%22false%22%3B%0Aend%3B%0A%0Ainline%20def%20compare%28a%2C%20b%29%0A%20%20%20%20println%28%24%22%7C%24a%7C%24b%7C%24%7C%24%7C%24%7C%24%7C%22%2C%20eval%28a%20%3D%3D%20b%29%2C%20eval%28a%20%21%3D%20b%29%2C%20eval%28a%20%3D%3D%3D%20b%29%2C%20eval%28a%20%21%3D%3D%20b%29%29%3B%0Aend%3B%0A%0Acompare%28null%2C%200%29%3B%0Acompare%28null%2C%201%29%3B%0Acompare%28null%2C%202%29%3B%0Acompare%28%40coal%2C%200%29%3B%0Acompare%28%40coal%2C%201%29%3B%0Acompare%28%40coal%2C%202%29%3B%0Acompare%28%40coal%2C%20%40lead%29%3B%0Acompare%28%22A%22%2C%200%29%3B%0Acompare%28%22A%22%2C%201%29%3B%0Acompare%28%22A%22%2C%202%29%3B%0Acompare%28%22A%22%2C%20%22B%22%29%3B%0Acompare%28%22A%22%2C%20%22A%22%29%3B%0Aprintflush%28message1%29%3B%0A)

</details>

### Comparison precision

Numeric values are stored in Mindustry Logic variables as floating-point numbers. Floating-point numbers have limited precision, and during mathematical operations might accumulate numerical errors. Therefore, comparing the results of floating-point mathematical operations for equality needs to take possible imprecision into account.

For example, the following code

```
param a = 0.1;

b = 1;
for i in 1 .. 10 do
    b -= a;
end;

println(b > 0 ? "greater than zero" : "not greater then zero");
printflush(message1);
```

prints `greater than zero`.

After ten iterations, we'd expect the value to be equal to `0` precisely, but due to the numerical imprecision this is not the case. This makes comparing results of floating-point operations to exact values problematic. The standard way to handle this issue is to consider two numbers equal if their difference is smaller than some value.

The equality operators in Mindustry Logic implicitly use , namely `0.000001` (i.e. `1e-6`): if two values differ by less than `1e-6`, they are considered equal by `==`, `!=`, `===` and `!==` operators. Therefore, updating the print call in the above code to  

```
println(b == 0 ? "equal to zero" : "not equal to zero");
```

prints `equal to zero`.

> [!TIP]
> To compare numbers using different precision, you can use the `isZero` or `isEqual` functions from the [`math` system library](SYSTEM-LIBRARY.markdown#math-library): `println(isZero(b) ? "equal to zero" : "not equal to zero");` outputs `not equal to zero`.

> [!IMPORTANT] 
> As has been shown above, the Mindustry Logic precision handling isn't applied to the inequality operators `<`, `<=`, `>` and `>=`. Therefore, `x <= 0` may evaluate to `false`, even though `x == 0` evaluates to `true`. This is especially important if you use these operators in loop conditions where the loop control variable is updated using floating point operations: the number of iterations of the loop may be different from what would be expected if all numerical operations were absolutely precise.        

### Output precision

Mindustry Logic applies the precision handling described above also when displaying values of variables using the `print` and `format` instruction and also on the `Vars` screen. Values that are close to an integer value are displayed as the integer value instead.

Therefore, when a variable value is displayed as an integer by Mindustry, it is necessary to keep in mind that the actual value of the variable might be different, and that the relational operators (`<`, `<=`, `>`, `>=`) operate over the actual value, not the displayed value of the variable.

To display a value of a variable without rounding, it is possible to use the `printExact()` function from the [`math` system library](SYSTEM-LIBRARY.markdown#utils-library): `printExact(1.2345e-15);` outputs `1.2345000000000002e-15`. Note that it takes several instructions to print the exact value, and also that the manipulations used to produce the output may introduce some numerical error into the value being outputted.   

**Note:** at this moment, Mindcode doesn't display values that are slightly smaller than an integer value rounded. `print(0.99999999);` therefore outputs `0.99999999` and not `1`, as could be expected. This is probably a bug that might eventually get fixed.

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

# Objects in expressions

Behavior of equality operators on objects has been described above.

For all other expressions, object values are treated as if they have a numeric value of  `1`: `n = @unit * 10` will assign `10` to `n` if a unit is bound, `0` otherwise.

## Strings in expressions

Strings are objects in Mindustry Logic, and therefore are evaluated in expressions just like any other object.

> [!NOTE]
> Since strings are objects, they can be compared for equality. However, relational operators on strings do not work as expected: `"a" < "b"` evaluates to `false`, because both `"a"` and `"b"` are converted to `1` for the operation. The same is true for all other relational operators `>`, `<=` and `>=`.
> 
> This also means that `"a" < "b"`, `"a" > "b"` and `"a" == "b"` all evaluate to `false`.

Because string operations are generally unavailable in Mindustry Logic, the Mindcode compiler produces a compilation error when it detects a string-based runtime expression:

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
message = formalGreeting ? "Good day, " + NAME : "Hey " + NAME + "!";
print(message);
```

It is especially useful to embed icon string constants into larger strings, which would otherwise be impossible:

```
def displayLevel(container, title, item)
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

# Constant expressions

Expressions or parts of expressions that are constant are evaluated at compile time. For example, `print(60 / 1000)` 
compiles to `print 0.06`, without an `op` instruction that would compute the value at runtime. Most of Mindcode 
operators and deterministic built-in functions can be used in constant expressions.

Constant expressions are evaluated during code generation. Furthermore, expressions that contain some constant
subexpressions (e.g. `ticks * 60 / 1000` contains a constant subexpression `60 / 1000`) may be partially evaluated
by the [Data Flow Optimization](SYNTAX-6-OPTIMIZATIONS.markdown#constant-folding).

## Constant expressions in Mindustry Logic 7 and earlier

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
print __tmp0
print 1E25
end
```

If the value of the constant expression can be only encoded to mlog with loss of precision, a warning is issued.

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

[« Previous: Variables and constants](SYNTAX-1-VARIABLES.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Control flow statements »](SYNTAX-3-STATEMENTS.markdown)
