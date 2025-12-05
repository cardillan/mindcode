# Expression Optimization

This optimization looks for certain expressions that can be performed more efficiently. Many optimizations for different instructions are available.

## The `lookup` instruction

If the logic ID used in the `lookup` instruction is an integer constant, Mindcode searches for the appropriate item, liquid, block, or unit type with given ID and if it finds one, the instruction is replaced by a `set` instruction setting the output variable directly to the item, liquid, block, or unit type.

Whether the optimization will be applied or not depends on the value of the [`builtin-evaluation` option](../SYNTAX-5-OTHER.markdown#option-builtin-evaluation):

* `none`: the optimization is never applied,
* `compatible`: the optimization is only applied when the ID maps to a stable Mindustry content object,
* `full`: the optimization is always applied.

## The `op` instruction

The result of some operations may be determined by inspecting the operation's operands, even when one or both of them are not known at compile time.

### Known value of an operand 

In some cases, the operation result doesn't depend on the other operand (a multiplication by zero is always zero regardless of the other operand). In other cases, the result is equal to the other operand (a multiplication by one or a subtraction of zero). All the performed replacements are listed in this table:

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

### Binary operation on identical operands

When both operands of the `op` instruction are known to have the same value, some operations always produce a fixed value. If this is the case, the operation is replaced by a `set` instruction setting the target variable to the fixed value:

* `equal`, `lessThanEq`, `greaterThanEq`, `strictEqual`: sets the result to `true`
* `notEqual`, `lessThan`, `greaterThan`: sets the result to `false`
* `sub`, `xor`: sets the result to `0`
* `or`, `land`: sets the result directly to the first operand, if the instruction doesn't represent the boolean version of the operation (`||` or `&&`),
* `and`, `min` and `max`: sets the result directly to the first operand.

### Simplifying complex expressions

In some cases, an expression consisting of several instructions can be simplified to use fewer instructions, while producing identical results.

* `floor` instruction applied to a result of a multiplication or a division by a constant. Combines the two operations into one integer division (`idiv`) operation. In the case of multiplication, the constant operand is inverted to become the divisor in the `idiv` operation.

## The `packcolor` instruction

The `packcolor` instruction is replaced with the resulting value when all its arguments are numeric constant. The result is encoded as a color literal.

## The `readarr`/`writearr` instructions

The `readarr`/`writearr` instruction is a virtual instruction which represents internal array element access. When the index is a numeric constant, the instruction is replaced by a `set` instruction accessing the array element directly. When the index is out of bounds, a compile-time error is generated.

## The `read`/`write` instructions

When the `read` instruction accesses a character in a constant string at a constant index, the instruction is replaced by a `set` instruction accessing the character directly. 

The `read` and `write` instructions accessing variables in current processor using a constant name are replaced with a `set` instruction accessing the processor variable directly.

> [!IMPORTANT]
> This optimization ensures the creation of the corresponding processor variable, as it includes its name in the `set` instruction. Since the `read` and `write` instructions' behavior depends on whether the target variable exists in the processor or not, this optimization may alter the behavior of the program. For this reason, this optimization is only performed on `advanced` level.       

Example:

```Mindcode
#set target = 8;

param a = "x";
param b = "y";
@this.write(1, a);
@this.write(1, b);
print(@this.read("x"));
print(@this.read(b));
```

compiles to:

```mlog
set a "x"
set b "y"
write 1 @this a
write 1 @this b
print x
read *tmp1 @this b
print *tmp1
```

This program outputs `1null`. Since the name of variable `y` is not a compile-time constant, the optimization is not applied for it, and the value read from `y` is `null`. `x`, on the other hand, is a compile-time constant, so the optimization is applied, and the value is actually written to the variable.

## The `select` instruction

Several optimizations are applied to the `select` instruction:

* `select` instruction with a constant condition is replaced by a `set` instruction assigning the resulting value determined by the condition to the target variable. Note that in cases where the condition has identical operands, its value is also constant (see [above](#binary-operation-on-identical-operands)).
* `select` instruction with identical true and false values is replaced by a `set` instruction assigning the true value to the target variable, as the resulting value doesn't depend on the condition. 
* `select` instruction with true/false values of `1`/`0` or `0`/`1` is replaced by an `op` instruction with the same condition (this is done not just for better readability/shorter code, but also to allow further optimizations involving the created `op` instruction). If the true value is equal to zero, the condition is inverted to produce the correct result. The optimization doesn't happen for the `strictEqual` conditions in that case, as it can't be inverted. 

## The `sensor` instruction

`sensor var @this @x` and `sensor var @this @y` are replaced by `set var @thisx` and `set var @thisy` respectively.

When the [`builtin-evaluation` option](../SYNTAX-5-OTHER.markdown#option-builtin-evaluation) is set to `compatible` or `full`, the following additional expressions are handled:

* If the `@constant` in a `sensor var @constant @id` instruction is a known item, liquid, block, or unit type constant with a defined logic ID, the Mindustry's ID of the object is looked up and the instruction is replaced by `set var <ID>`, where `<ID>` is a numeric literal.
* If the `@constant` in a `sensor var @constant @name` instruction is a known item, liquid, block, or unit type constant, the Mindustry's content name of the object is looked up and the instruction is replaced by `set var <name>`, where `<name>` is a string literal. **This optimization takes place even when `builtin-evaluation` is set to `none`**.

Some Mindustry content objects may have different logic IDs in different Mindustry versions (these objects are called _unstable_). For these objects, the above optimizations only happen when the `builtin-evaluation` option is set to `full`:

```Mindcode
set target = 7;
set builtin-evaluation = full;
println(lookup(:item, 18));
println(@tungsten.@id);
printflush(message1);
```

compiles to:

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

## The `set` instruction

* All `set` instructions assigning a variable to itself (e.g., `set x x`) are removed.

---

[&#xAB; Previous: Dead Code Elimination](DEAD-CODE-ELIMINATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Function Inlining &#xBB;](FUNCTION-INLINING.markdown)
