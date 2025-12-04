# Case Switching

Case Switching is a [dynamic optimization](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations) and is only applied when it is compatible with the optimization goal.

[Case expressions](../SYNTAX-3-STATEMENTS.markdown#case-expressions) are normally compiled to a sequence of conditional jumps: for each `when` branch the entry condition(s) of that clause is evaluated; when it is `false`, the control is transferred to the next `when` branch, and eventually to the `else` branch or end of the expression. This means the case expression evaluates—on average—half of all existing conditions, assuming even distribution of the case expression input values. (If some input values of the case expressions are more frequent, it is possible to achieve better average execution times by placing those values first.)

The Case Switching optimization improves case expressions which branch on integer values of the expression, or on Mindustry content objects.

**Preconditions:**

The following conditions must be met for a case expression to be processed by this optimization:

* All values used in `when` clauses must be effectively constant.
* All values used in `when` clauses must be integers, or must be convertible to integers (see [Mindustry content conversion](#mindustry-content-conversion)).
* Values used in `when` clauses must be unique; when ranges are used, they must not overlap with other ranges or standalone values.

> [!WARNING]
> It is assumed that a case expression branching exclusively on integer values always gets an integer value on input as well. Similarly, a case expression branching exclusively on a Mindustry content of a given type is expected to always get a Mindustry content of the same type on input. If an unexpected input value may appear on input (e.g., a non-integer, or a Mindustry content of a different type), this optimization will produce incorrect results. At this moment Mindcode isn't able to recognize such a situation; if this is the case, you need to disable the Case Switching optimization manually (possibly for just the problematic case expression using `#setlocal`).

Depending on the target version and other compiler options, three different optimization techniques are available:

* [Value translation](#value-translation),
* [Fast dispatch](#fast-dispatch),
* [Jump tables](#jump-tables).

## Null values

All optimization techniques fully support [`null` values in the `when` clauses](../SYNTAX-3-STATEMENTS.markdown#null-values). When the `null` value is explicitly handled (i.e., there is a `when null` branch present), the corresponding branch is executed for `null` input values. In case the `when null` branch is missing, the behavior depends on the type of the case expression:

* Integers: `null` input values are handled by the `0` branch, or skipped altogether if there is no `0` branch.
* Mindustry content: `null` input values are handled by the `else` branch, or skipped altogether if there is no else branch.

This behavior is identical to the behavior of an unoptimized case expression.

Mindcode arranges the code to only perform checks distinguishing between `null` and the zero value where both of these values can occur. When a code path is known to not possibly handle both `null` and `0`, these checks are eliminated. As a result, an optimized `case` expressions checking for `null` in `when` branches is typically more efficient than letting the `null` value go into the `else` branch and checking it explicitly there, or checking for them prior to the case expression itself.

## Mindustry content conversion

When all `when` branches in the case expression contain built-in constants representing Mindustry content of the same type (items, liquids, unit types, or block types) and the optimization level is set to `advanced`, this optimization converts these built-in constants to logic IDs, adds an instruction to convert the input value to a logic ID (using the `sensor` instruction with the `@id` property) and attempts to build a jump table over the resulting numeric values.

The following preconditions need to be met to apply content conversion:

* The optimization level is set to `advanced`.
* The `builtin-evaluation` option is set to `compatible` or `full`.
* All values in `when` branches are either `null`, or built-in variables referencing Mindustry content of the same type (items, blocks, and so on).
* Values used in `when` clauses are unique.
* The logic ID is known to Mindcode for all `when` values.
* All logic IDs corresponding to the `when` values are stable, or `builtin-evaluation` mode is set to `full`.

## Range check elimination

When all possible input values in case expression are known to be handled by one of the `when` branches, it is not necessary to handle other values, which may save some instructions. Mindcode is generally incapable of determining this is the case and keeps these jumps in place by default. By setting the [`unsafe-case-optimization`](../SYNTAX-5-OTHER.markdown#option-unsafe-case-optimization) compiler directive to `true`, you inform Mindcode that all input values are handled by case expressions. This prevents the out-of-range handling instructions from being generated, making the optimized case expression faster by two instructions per execution, and leads to the optimization being considered for case expressions with four branches or more.

Putting an `else` branch into a case expression indicates not all input values are handled, and doing so disables the unsafe case optimization: the basic optimization may still happen, but the out-of-range checks will remain.

If you activate the `unsafe-case-optimization` option, and an unhandled input value is encountered, the behavior of the generated code is undefined.

> [!NOTE]
> When the input value originates in the game (e.g., item selected in a sorter), keep in mind the value obtained this way might be `null`, and do include the `when null` branch in the case expression.

The range check is also partially or fully removed when the following conditions are met:

* There is a `when` branch corresponding to the Mindustry content with a zero ID: in this case, Mindcode knows when the minimum possible numerical value of the ID (that is, zero) is handled by the case expression and may omit the check for values less than zero.
* There is a `when` branch corresponding to the Mindustry content with a maximum ID, and `builtin-evaluation` is set to `full`: in this case, Mindcode knows when the maximum possible numerical value of the ID (that is, zero) is handled by the case expression and may omit the check for values greater than that.

Mindcode may use [Jump table padding](#jump-table-padding) to be able to remove the range checks in the above two cases.

## Value translation

In Mindustry 8, it is possible to [read character values from a string](../MINDUSTRY-8.markdown#reading-characters-from-strings) at a given index in a single operation. When the case expression assigns a single new value to a variable in each branch, and the values can be represented as integers in a reasonable range, the entire expression can be encoded as a `read` from a string containing the encoded values. In the basic case, the entire case expression can be replaced by a single `read` instruction. In more complex case expressions, additional instructions may be needed to handle some more specific cases.

Assuming the case expression conforms to the requirements described above, the following prerequisites need to be met for this optimization to be applied:

* The [target](../SYNTAX-5-OTHER.markdown#option-target) must be set to version `8.1` or higher.
* The [use-text-translations](../SYNTAX-5-OTHER.markdown#option-use-text-translations) option must be active.

The basic case is:

```Mindcode
set target = 8;
volatile output = case input
    when 0 then 'A';
    when 1 then 'B';
    when 2 then 'C';
end;
```

which produces

```mlog
read .output "ABC" :input
```

Note that input values other than `0`, `1`, or `2` result in a `null`, which is what the original case expression does too.

Further on, we call the input values explicitly handled by the case expression "keys" and the possible values assigned to `output` "output values."

Assuming the case statement can be implemented using value translation, several factors may complicate things:

1. Input different from an integer range starting at 0:
    1. `input` is a Mindustry content: additional mapping fom content to logic ID is needed,
    2. `input` is an integer, and `min(keys)` is negative: `input` is offset by `-min(keys)`,
    3. `input` is an integer, and `min(keys)` is above zero:
        1. the translation table is padded by `else` values up to `min(keys)`, if possible, or
        2. `input` is offset by `- min(keys)`.
2. Output values cannot be mapped to characters valid for a string literal: an offset is added to output values which must then be subtracted. (Unfortunately, this subtraction damages null values naturally produced by the `read` instruction, may need to be compensated for later on.)
3. Keys contain `null` and the `null` key maps to a different value than the zero key (meaning a null value of `input` needs to be specifically handled):
    1. `null` maps to a non-null value: `null`-handling `select` will be added after translation.
    2. `null` maps to a branch which doesn't assign a new value: a `select` is used to restore the original value of the variable.
4. Some output values need special handling:
    1. some keys produce `null`: the null-producing keys are mapped to an unused output value (a null placeholder), and a `select` converts it into a `null`.
    2. some keys map to branches which don't assign a new value: the corresponding key is mapped to an unused output value (a void placeholder), and a `select` is later used to convert it back to the original variable.
5. Handling of values outside the mapping string (these values are always handed by the `else` branch):
    1. the `else` branch doesn't output `null`, or it does output `null`, but this value is damaged by subtracting offset: a `select` instruction replaces the original `null` value produced by translation to the actual output value,
    2. the `else` branch doesn't change the variable value: a `select` instruction handles the `null` produced by translation to restore the original variable.
6. When the minimal output value in the translation map is greater than `1` (either naturally or because of the applied offset), it is possible to map the `else` values within the map to a value lower than all other output values. The `else` branch then produces values lower than the minimal output value (as `null` equals to `0` in non-strict comparisons), and just a single `select` instruction is needed to convert them to the actual output values. Two possible cases are handled this way:
    1. The else branch produces `null` (case 6.1), and an additional branch produces `null` or there is a position in the translation table mapping the key to the else branch (case 4.2): the `null` value is represented by a placeholder lower than all other values in the map, and case 4.2 and 6.1 are replaced by a single instruction.
    2. The else branch doesn't modify the output variable (case 6.2), and there are positions in the map that also do not modify the output variable (case 5): the original variable value is represented by a placeholder lower than all other values in the map, and case 5 and 6.2 are replaced by a single instruction.
7. Output values are a Mindustry content: additional mapping from logic ID to content is added.

As has been mentioned in case 1.iii.a, the translation table can be padded on the lower end to avoid the need to offset the input value. Similarly, when the Mindustry content conversion is applied and the `builtin-evaluation` option is set to `full`, the translation table may be padded on the high end up to the largest ID of the respective Mindustry content, possibly avoiding the need to handle case 6.

Instructions corresponding to the above cases (in the order in which they're generated):

| Case | Instruction                                                                    | Note                 |
|:----:|:-------------------------------------------------------------------------------|:---------------------|
|  1   | `sensor tmp input @id` or `op sub tmp input minKeys`                           |                      |
|  -   | `read origOutput "translation" input`                                          |                      |
|  2   | `op sub output origOutput offset`                                              |                      |
| 3.1  | `select output strictEqual input null nullOutput prevOutput`                   |                      |
| 4.1  | `select output equal origOutput nullPlaceholder null prevOutput`               |                      | 
| 5.1  | `select output strictEqual origOutput null elseValue prevOutput`               |                      | 
| 6.1  | `select output lessThanEq origOutput nullPlaceholder elseValue prevOutput`     | Combines 4.1 and 5.1 | 
|  7   | `lookup <contentType> output prevOutput`                                       |                      |
| 3.2  | `select output strictEqual input null initialOutput prevOutput`                |                      |
| 4.2  | `select output equal origOutput voidPlaceholder initialOutput prevOutput`      |                      | 
| 5.2  | `select output strictEqual origOutput null initialOutput prevOutput`           |                      | 
| 6.2  | `select output lessThanEq origOutput voidPlaceholder initialOutput prevOutput` | Combines 4.2 and 5.2 | 

Description of the variable names used in the table above:
* `initialOutput`: the original value of the output variable,
* `prevOutput`: holds the output value from the previous step,
* `origOutput` or `output` is the output from the current step.

The last output value produced by the code is the resulting value of the translation. Instructions which restore the initial value of the output variable are placed after the `lookup` instruction to avoid the need to convert the input value to logic id first.

When [`unsafe-case-optimization`](../SYNTAX-5-OTHER.markdown#option-unsafe-case-optimization) is set to `true`, the handling of case 6 and sometimes case 4 may be omitted, as it is supposed not to occur.

The most complex value translations consist of up to eight instructions. When the optimization goal is speed, another solution will probably perform better than the more complex cases. When optimizing for size, though, even the largest value translations may be smaller than any of the alternatives:

```Mindcode
set target = 8;
set goal = size;
volatile var value;
case value
    when @copper      then value = @silicon;
    when null         then value = @lead;                   // causes case 2
    when @coal        then value = @copper;                 // causes case 3.1
    when @lead        then value = null;                    // causes case 4.1
    when @silicon     then null;                            // causes case 4.2
    else value = @scrap;                                    // causes case 5.1
end;
```

produces

```mlog
sensor *tmp2 .value @id
read *tmp3 "9:8880888;" *tmp2
op sub *tmp4 *tmp3 48
select *tmp5 strictEqual *tmp2 null 1 *tmp4
select *tmp6 equal *tmp3 58 null *tmp5
select *tmp7 strictEqual *tmp3 null 8 *tmp6
lookup item *tmp8 *tmp7
select .value equal *tmp3 59 .value *tmp8
```

**Multiple value translations**

When a single case expression assigns new values to several distinct variables, the optimization may be applied to each of them separately. Handling of the input value (e.g., translating it to logic id or applying an offset) is performed just once and shared by all translated variables. Example:

```Mindcode
set target = 8;
set goal = size;
a = '0';
while true do
    case a
        when '0' then a = '1'; b = 'a';
        when '1' then a = '2'; b = 'b';
        when '2' then a = '0'; b = 'c';
    end;
    printchar(a);
    printchar(b);
end;
```
compiles to:
```mlog
set :a 48
read *tmp2 "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!abc" :a
select :b lessThanEq *tmp2 33 :b *tmp2
read *tmp4 "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!120" :a
select :a lessThanEq *tmp4 33 :a *tmp4
printchar :a
printchar :b
jump 1 always 0 0
```

> [!NOTE]
> The text translation optimization works best when using readable characters as values produced by the case expression. This can be easily achieved by using character literals Mindcode provides, as demonstrated in the last example.

## Fast dispatch

In Mindustry 8, [character values can be read from a string](../MINDUSTRY-8.markdown#reading-characters-from-strings) at a given index in a single operation directly into the `@counter` variable. This allows encoding target instruction addresses into strings, similarly to encoding the actual values in [value translations](#value-translation). Furthermore, since assigning `@null` to `@counter` is ignored by the processor, values outside the range covered by the string jump table cause the next instruction to be executed. This can be leveraged by placing the `else` branch just after the `read` instruction, ensuring that the control is transferred to the proper branch by just this one instruction.

Additionally, the branch handling the highest number of input values is moved to the end of the case expression. As all branches except the last one need to execute additional jump to the end of the case expression; this ensures that the most frequented branch will use this benefit.

The following prerequisites need to be met for this optimization to be applied:

* The [target](../SYNTAX-5-OTHER.markdown#option-target) must be set to version `8` or higher.
* The [symbolic labels](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) option must be inactive.
* The [null-counter-is-noop](../SYNTAX-5-OTHER.markdown#option-null-counter-is-noop) option must be active.
* The [use-text-jump-tables](../SYNTAX-5-OTHER.markdown#option-use-text-jump-tables) option must be active.

Since the out-of-range values are fully handled by fast dispatch, the [`case-optimization-strength` compiler directive](../SYNTAX-5-OTHER.markdown#option-case-optimization-strength) is ignored when this optimization is performed. Even unspecified unhandled values are correctly handled by the single-jump dispatch.

## Jump tables

This optimization typically only gets performed when neither value translation nor fast dispatch is possible, and in some rare cases where value translation produces less optimal (typically slower) code according to the optimization goal. This optimization is available in all targets.

A jump table replaces the sequence of conditional jumps by direct jumps to the corresponding `when` branch. The actual instructions used to build a jump table are

```
jump <else branch address> lessThan value minimal_when_value
jump <else branch address> greaterThan value maximal_when_value
op add @counter <start_of_jump_table - minimal_when_value> value
start_of_jump_table:
jump <when branch for minimal when value address>
jump <when branch for minimal when value + 1 address>
jump <when branch for minimal when value + 2 address>
...
jump <when branch for maximal when value address>
```

The jump table is put in front of the `when` branches. Original conditions in front of each processed `when` branch are removed. Each `when` branch jumps to the end of the case expression as usual. The bodies of `when` branches are moved into correct places inside the case expression when possible, to avoid unnecessary jumps. On `experimental` level, the bodies of `when` branches may be duplicated to several suitable places to avoid even more jumps at the cost of additional code size increase. This optimization usually only kicks in for small branch bodies, since for larger code increases, a better performing solution can be achieved by a different segment arrangement.

To build the jump table, the minimum and maximum value of existing `when` branches are determined first. Values outside this range are handled by the `else` branch (if there isn't an explicit `else` branch in the case statement, the `else` branch just jumps to the end of the case expression). Values inside this range are mapped to a particular `when` branch, or, if the value doesn't correspond to any of the `when` branches, to the `else` branch.

The first two instructions in the example above (`jump lessThan`, `jump greaterThan`) handle the cases where the input value lies outside the range supported by the jump table. The `op add @counter` instruction then transfers the control to the corresponding specific jump in the jump table and consequently to the proper `when` branch.

A basic jump table executes at most four instructions on each case expression execution (less if the input value lies outside the supported range). We've mentioned above that the original case statement executes half of the conditional jumps on average. This means that converting the case expression to a jump table only makes sense when there are at least eight conditional jumps in the case expression.

Notes:

* When evaluating execution speed, the optimizer computes and averages execution costs of each value present in a `when` clause. All of these values are deemed equally probable to occur, and values leading to an `else` branch are not considered at all. In an unoptimized `case` expression, values handled by the `else` branch take the longest time to handle, while in the optimized case expression, values completely outside the range of `when` values are executed faster than any other values. This is a side effect of the optimization.
* As a consequence, if you put the more frequent values first in the case expression, and the value distribution is very skewed, converting the case expression to the jump table might actually worsen the average execution time. Mindcode has no way to figure this on its own; if you encounter this situation, you might need to disable the Case Switching optimization for your program.
* For smaller case expressions, a full jump table might provide worse average performance than the original case expression. Mindcode might still optimize the case expression by applying the bisection search used in [Jump table compression](#jump-table-compression), providing both better average execution time of the entire case expression and more balanced execution time of individual branches.

### Jump table compression

Building a single jump table for the entire case expression often produces the fastest code, but the jump table might become huge. The optimizer therefore tries to break the table into smaller segments, handling these segments specifically. The resulting code uses a bisection search to locate the segment handling a particular input value. Some segments might contain a single value, or a single value with a few exceptions, and can be handled by only a few jump instructions. More diverse segments may be encoded as separate, smaller jump tables. The optimizer considers a number of such arrangements and selects the best one according to the current [optimization goal](../SYNTAX-6-OPTIMIZATIONS.markdown#static-and-dynamic-optimizations).

The total number of possible segment arrangements can be quite large. The more arrangements are considered, the better code may be generated. However, generating and evaluating these arrangements can take a long time. The [`case-optimization-strength` compiler directive](../SYNTAX-5-OTHER.markdown#option-case-optimization-strength) can be used to control the number of considered arrangements. Setting this option to `0` disables jump table compression entirely.

Typically, compressing the jump table produces smaller, but slightly slower code. For more complex `case` expressions, it is possible that the optimized code will be both smaller and significantly faster than the unoptimized `case` expression.

Jump table compression is, for example, particularly useful when using blocks in case expressions: given the large dispersion of block type IDs, full jump tables tend to get quite large.

Notes:

* Jump table compression is not performed when range checks for the given case expression are eliminated via the `unsafe-case-optimization` option, or when the [`case-optimization-strength` compiler directive](../SYNTAX-5-OTHER.markdown#option-case-optimization-strength) option has been set to 0.
* The optimizer will sometimes choose a smaller implementation version over a larger one, even when the optimization goal is speed and there is plenty of instruction space. This happens when the smaller implementation is actually faster than the larger one, considering the performance of the values ending up at the `else` branch.
* Since the bisection search provides better execution time than a linear search, it may be applied even to case expressions too small for a full jump table optimization.

### Jump table padding

When the jump table starts at zero value, it is possible to generate a faster code due to these effects:

* When the Mindustry content conversion is applied, the optimizer knows the logic IDs cannot be less than zero. A jump instruction handling values smaller than the start of the jump table can therefore be omitted.
* When the [`symbolic-labels` directive](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) is set to `true`, an additional operation handling the non-zero offset can be omitted.

Similarly, when the Mindustry content conversion is applied, the `builtin-evaluation` option is set to `full` and the jump table ends at the largest ID of the respective Mindustry content, a jump instruction handling values larger than the end of the table can be omitted, as the optimizer knows no larger values may occur.

When the jump table doesn't start or end at these values naturally, Mindcode may pad the table at either end with additional jumps to the `else` branch. The optimizer considers the possibility of padding the table at the low end, high end, or both, and chooses the option that gives the best performance given the instruction space limit.

### Text-based jump tables

The ability to read character values from a string at a given index into the `@counter` variable, as described in [Fast dispatch](#fast-dispatch), can be also used to replace the actual jumps in a jump table with the `read` instruction. In most cases, when this is possible, fast dispatch will be used instead, but if that optimization is not possible for some reason, text-based jump tables may be used in this optimization too. The following prerequisites need to be met for text-based jump tables to be used:

* The [target](../SYNTAX-5-OTHER.markdown#option-target) must be set to version `8` or higher.
* The [symbolic labels](../SYNTAX-5-OTHER.markdown#option-symbolic-labels) option must be inactive.
* The [use-text-jump-tables](../SYNTAX-5-OTHER.markdown#option-use-text-jump-tables) option must be active.

The text-based jump table may be also used for individual segments produced during jump table compression.

### Example

The example illustrates the following optimization aspects:

* Case switching optimization in general
* Mindustry content conversion
* Handling of `null` values
* Jump table compression
* Jump table padding
* Moving bodies of `when` branches

The sample has been artificially constructed to demonstrate the above effects.

```Mindcode
set target = 7;
set builtin-evaluation = full;
set symbolic-labels = true;
set instruction-limit = 150;
set case-optimization-strength = 4;

text = case getlink(0).@type
    when null then "none";
    when @kiln, @phase-weaver, @pyratite-mixer, @melter, @disassembler then "A";
    when @plastanium-compressor, @cryofluid-mixer, @blast-mixer, @separator, @spore-press then "B";
    when @unit-repair-tower, @prime-refabricator, @mech-refabricator, @slag-heater, @scathe then "C";
    when @diffuse, @tank-refabricator, @ship-refabricator, @lustre then "D";
    else "E";
end;

print(text);
```

The above case expression is transformed to this:

```mlog
 Mlog code compiled with support for symbolic labels
 You can safely add/remove instructions, in most parts of the program
 Pay closer attention to sections of the program manipulating @counter
    getlink *tmp1 0
    sensor *tmp2 *tmp1 @type
    sensor *tmp4 *tmp2 @id
        jump label_21 greaterThanEq *tmp4 230
        jump label_45 greaterThanEq *tmp4 14
        op add @counter @counter *tmp4
        jump label_44 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
        jump label_19 always 0 0
        jump label_42 always 0 0
    label_19:
        set *tmp0 "B"
        jump label_46 always 0 0
    label_21:
        jump label_45 greaterThanEq *tmp4 243
        jump label_26 greaterThanEq *tmp4 232
        jump label_38 lessThan *tmp4 231
    label_24:
        set *tmp0 "D"
        jump label_46 always 0 0
    label_26:
        op sub *tmp5 *tmp4 232
        op add @counter @counter *tmp5
        jump label_38 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_24 always 0 0
        jump label_38 always 0 0
        jump label_24 always 0 0
        jump label_38 always 0 0
        jump label_45 always 0 0
        jump label_45 always 0 0
        jump label_24 always 0 0
    label_38:
        set *tmp0 "C"
        jump label_46 always 0 0
    label_40:
        set *tmp0 "none"
        jump label_46 always 0 0
    label_42:
        set *tmp0 "A"
        jump label_46 always 0 0
label_44:
    jump label_40 strictEqual *tmp4 null
label_45:
    set *tmp0 "E"
    label_46:
    print *tmp0
```

---

[&#xAB; Previous: Case Expression Optimization](CASE-EXPRESSION-OPTIMIZATION.markdown) &nbsp; | &nbsp; [Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Next: Condition Optimization &#xBB;](CONDITION-OPTIMIZATION.markdown)
