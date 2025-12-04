# Expression Optimization

This optimization looks for certain expressions that can be performed more efficiently. Many optimizations for different instructions are available.

* `floor` instruction applied to a result of a multiplication by a constant or a division. Combines the two operations into one integer division (`idiv`) operation. In the case of multiplication, the constant operand is inverted to become the divisor in the `idiv` operation.
* `select` instruction with constant condition is replaced by a `set` instruction directly.
* `packcolor` instruction is replaced with the resulting value when all parameters are constant.
* `sensor var @this @x` and `sensor var @this @y` are replaced by `set var @thisx` and `set var @thisy` respectively. Data Flow Optimization may then apply [constant propagation](DATA-FLOW-OPTIMIZATION.markdown#constant-propagation) to the `@thisx`/`@thisy` built-in constants.
* `read` and `write` instructions accessing variables in current processor using a constant name are replaced with a `set` instruction accessing the processor variable directly.
* `readarr` and `writearr` instructions using a constant index are replaced with a `set` instruction accessing the array element directly. If the index happens to fall out of the array's range, a compile-time error is generated.
* All set instructions assigning a variable to itself (e.g., `set x x`) are removed.
* When both operands of the `op` instruction are known to have the same value, some operations always produce a fixed value. If this is the case, the operation is replaced by a `set` instruction setting the target variable to the fixed value:
    * `equal`, `lessThanEq`, `greaterThanEq`, `strictEqual`: sets the result to `1` (true)
    * `notEqual`, `lessThan`, `greaterThan`: sets the result to `0` (false)
    * `sub`, `xor`: sets the result to `0`
    * `or`, `land`: sets the result directly to the first operand, if the instruction doesn't represent the boolean version of the operation (`||` or `&&`),
    * `and`, `min` and `max`: sets the result directly to the first operand.
* The result of some operations may be determined by a known value of one of its operands. In some cases, the result doesn't depend on the other operand (a multiplication by zero is always zero regardless of the other operand). In other cases, the result is equal to the other operand (a multiplication by one or a subtraction of zero). All the performed replacements are listed in this table:

| Operation      | Operator     | First operand | Second operand | Result |  Level   | Note        |
|----------------|:-------------|:-------------:|:--------------:|:------:|:--------:|-------------|
| mul            |              |      var      |       1        |  var   |  Basic   | Commutative |
| mul            |              |      var      |       0        |   0    |  Basic   | Commutative |
| div            |              |      var      |       1        |  var   |  Basic   |             |
| div, idiv, mod |              |      var      |       0        |  null  |  Basic   |             |
| div, idiv, mod |              |       0       |      var       |   0    | Advanced |             |
| add, xor       |              |      var      |       0        |  var   |  Basic   | Commutative |
| sub            |              |      var      |       0        |  var   |  Basic   |             |
| shl, shr       |              |      var      |       0        |  var   | Advanced |             |
| shl, shr       |              |       0       |      var       |   0    |  Basic   |             |
| or             | `\|`, `or`   |      var      |       0        |  var   | Advanced | Commutative |
| or             | `\|\|`, `or` |      var      |    nonzero     |   1    |  Basic   | Commutative |
| and, land      |              |      var      |       0        |   0    |  Basic   | Commutative |
| land           | `and`        |      var      |    nonzero     |  var   | Advanced | Commutative |

`var` represents a variable with an arbitrary, unknown value. In the case of commutative operations, the optimization is applied also when the first and second operands, as given in the table, are swapped.

Some mlog operations are produced by different Mindcode operators. When the optimizations are only applied to operations corresponding to specific Mindcode operators, these operators are listed in the **Operator** column.

> [!IMPORTANT]
> Some optimizations applied to `or`, `and`, `land`, `xor`, `shl` and `shr` operations applied to non-integer or non-boolean values may produce different results from unoptimized code: unoptimized code would result into an integer value (or boolean value in case of `land`), while the optimized code may produce a non-integer value. Passing a non-integer/non-boolean value into these operators is unusual, but not impossible. These optimizations are therefore only performed on `advanced` level and can be turned off by setting the level to `basic`.

When the `builtin-evaluation` option is set to `compatible` or `full`, the following additional expressions are handled:

* If the `@constant` in a `sensor var @constant @id` instruction is a known item, liquid, block or unit constant with a defined logic ID, the Mindustry's ID of the object is looked up and the instruction is replaced by `set var <id>`, where `<id>` is a numeric literal.
* If the `id` in a `lookup <type> id` instruction is a constant, Mindcode searches for the appropriate item, liquid, block, or unit with given ID and if it finds one, the instruction is replaced by `set var <built-in>`, where `<built-in>` is an item, liquid, block, or unit literal.
* If the `@constant` in a `sensor var @constant @name` instruction is a known item, liquid, block or unit constant, the Mindustry's content name of the object is looked up and the instruction is replaced by `set var <name>`, where `<name>` is a string literal. This optimization takes place even when `builtin-evaluation` is set to `none`.

Some Mindustry content objects may have different logic IDs in different Mindustry versions (these objects are called "unstable"). For these objects, the above optimizations only happen when the [`builtin-evaluation` option](../SYNTAX-5-OTHER.markdown#option-builtin-evaluation) is set to `full`:

```Mindcode
set target = 7;
set builtin-evaluation = full;
println(lookup(:item, 18));
println(@tungsten.@id);
printflush(message1);
```

compiles to

```mlog
print "dormant-cyst\n19\n"
printflush message1
```

When compiled with `builtin-evaluation` set to `compatible`, the code is

```mlog
lookup item *tmp0 18
print *tmp0
print "\n"
sensor *tmp1 @tungsten @id
print *tmp1
print "\n"
printflush message1
```

---

[&#xAB; Previous: Dead Code Elimination](DEAD-CODE-ELIMINATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Function Inlining &#xBB;](FUNCTION-INLINING.markdown)
