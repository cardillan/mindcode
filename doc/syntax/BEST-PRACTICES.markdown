# Best Practices

This document contains some tips on writing a better-performing code in Mindcode.

As Mindcode undergoes development, the beast practices may change as new versions are released. Significant changes to the best practices are therefore described here.

## Mindcode 3.11

The most important change in the 3.11 release comes with the ability to short-circuit boolean expressions and some other related changes to the handling of boolean expressions.

* It is no longer necessary or advisable to avoid boolean operators in conditions. When using the `and` and `or` operators, which are short-circuiting, the conditions are often compiled using fewer instructions than a fully evaluated condition right off the bat. Furthermore, the evaluation of the condition is terminated as soon as the value of the condition is known. This may result in significant performance improvements.
* The `&&` and `||` operators are analogous to the `and` and `or` operators, except they still perform full evaluations. In cases where full evaluation of an expression is for some reason preferred, these operators may be used. The `||` operator also ensures the resulting value is always normalized (either `1` or `0`), which requires an additional instruction to perform the normalization. However, a new optimization removes these additional instructions where possible. Furthermore, it is possible to use the `|` operator instead of the `||` one, as it also performs full evaluation, but doesn't normalize the result.
* The `in range` operator is also short-circuited (the lower bound is always tested first) and can be efficiently used in boolean expressions involving `and` or `or` operators. The value is guaranteed to be evaluated only once. When testing the upper bound first is more desirable for some reason, it is necessary to express the condition as two separate tests, putting the upper bound first.   
* The `in (list)` operator is implemented as a `case` expression and benefits from the same optimizations. Its most effective when used as a sole condition in an `if` expression.

> [!NOTE] 
> Short-circuited boolean expressions cannot be reused. In previous versions, the following code
>
> ```Mindcode
> x = a > 0 or b > 0 ? "positive": "negative";
> y = a > 0 or b > 0 ? 1: -1;
> ```
>
> would evaluate the condition `a > 0 or b > 0` only once, reusing it in the second statement. This is no longer the case with short-circuited conditions; in similar situations, use a single `if` statement instead:
>
> ```Mindcode
> if a > 0 or b > 0 then
>     x = "positive";
>     y = 1;
> else
>     x = "negative";
>     y = -1;
> end;
> ```

# Vector length calculation

Mindustry Logic provides an instruction which directly computes the length of a vector. The standard formula for vector length is

```Mindcode
length = sqrt(x * x + y * y);
print(length);
```

When expressed by the above formula, the resulting computation takes four instructions:

```mlog
op mul *tmp0 :x :x
op mul *tmp1 :y :y
op add *tmp2 *tmp0 *tmp1
op sqrt :length *tmp2 0
print :length
```

A better calculation uses just one instruction, `op len`, which maps to the `len()` function:

```Mindcode
length = len(x, y);
print(length);
```

which compiles to:

```mlog
op len :length :x :y
print :length
```

Note: an optimization which would replace the sequence of the four instructions with `op len` is planned, but hasn't been implemented yet.

# Absolute addressing

Mindcode provides an [option](SYNTAX-5-OTHER.markdown#option-symbolic-labels) for generating the mlog code with symbolic labels. Even though this option ensures the resulting code is more readable and can also be modified manually, it also precludes the compiler from using absolute addressing in the code and (in taget `8.0` or higher) text-encoded jump tables. This causes the compiler to generate a slower code, a larger code or both. Especially case expressions can be seriously affected, and to a lesser extent internal arrays.        

# The `case` expressions

Mindcode provides a very powerful optimization for `case` expressions, in case where all the `when` values are either integer constants, or constant Mindustry objects of the same type (blocks, items, etc.).

As an extreme example, in target `8.0` or higher Mindcode may be capable of converting the entire case expression into a single instruction:

```Mindcode
#set target = 8;
volatile output = case input
    when 0 then 'A';
    when 1 then 'B';
    when 2 then 'C';
end;
```

compiles to:

```mlog
read .output "ABC" :input
```

The following measures may help produce the most efficient code:

* When using Mindustry objects as `when` values, set the [`builtin-evaluation` option](SYNTAX-5-OTHER.markdown#option-builtin-evaluation) to `full`. This means the compiler only considers the Mindustry objects which exist in the given target and doesn't need to produce code for handling unknown objects. On the other hand, the compiled code is only guaranteed to run correctly on Mindustry versions compatible with the chosen target. Consider using the [`target-guard` option](SYNTAX-5-OTHER.markdown#option-target-guard) to ensure the program won't run on incompatible Mindustry versions.
* If possible, use printable characters as `when` values. For example, if your case expression produces a few categories, consider assigning each category a value using character literals (e.g. `'A'`, `'B'` or `'0'`, `'1'` and so on). If you need to perform additional computations on the resulting values, though, use the natural values in the `when` clause and let Mindcode perform the necessary conversions.  

An `in` operator applied to a list of values uses `case`expression internally, so it benefits from the same optimizations.

# Conditional expressions

The `and` and `or` operators in Mindcode provide short-circuited evaluation. This provides two benefits:

* The evaluation of a condition ends up as soon as its final value is known, avoiding the need to evaluate the remaining condition terms.
* Relational operators can be evaluated using just one instruction (`jump` or `select`). Compare short-circuited and full evaluation of the same condition: 

```Mindcode
#set remarks = comments;

/// Full evaluation 
if a > 0 || b > 0 then
    print("Positive");
end;

/// Short-circuit evaluation 
if a > 0 or b > 0 then
    print("Positive");
end;

printflush(message1);
```

compiles to:

```mlog
# Full evaluation 
op greaterThan *tmp0 :a 0
op greaterThan *tmp1 :b 0
op or *tmp2 *tmp0 *tmp1
jump 5 equal *tmp2 false
print "Positive"
# Short-circuit evaluation 
jump 7 greaterThan :a 0
jump 8 lessThanEq :b 0
print "Positive"
printflush message1
```

Mindcode performs extensive optimization of conditional expressions and may even turn a short-circuited expression into a fully evaluated one when it is beneficial.

# Variable-based lookups

Mindustry 8 allows reading a variable from a processor (including the current processor identified by `@this`) using a variable name. While a general string manipulation is not supported by mlog, a specific string can be obtained by querying the `@name` property of a Mindustry object. It is therefore possible to get a string name of a Mindustry object and use that name to read or write a variable, replacing a more complex case statement.

Mindcode supports specifying mlog variable names when declaring variables, using constant expressions (not just string literals). A variable to be accessed indirectly by its name must be declared `volatile`, for example:

```Mindcode
#set target = 8;
volatile mlog("foo") var foo = 10;
param variable = "foo";
@this.write(20, variable);
print(@this.read("foo"));       // Mindcode resolves this to variable 'foo' 
```

compiles to:

```mlog
set foo 10
set variable "foo"
write 20 @this variable
print foo
```

This mechanism can be used to build custom lookups, where the named variable holds the lookup value. At the same time, the `@name` property is compile-time evaluated and thus can be used to define the variable name:

```Mindcode
#set target = 8;
#set remarks = comments;

/// Initialization:
volatile mlog(@copper.@name)    var oreCopper = @ore-copper;
volatile mlog(@lead.@name)      var oreLead = @ore-lead;
volatile mlog(@scrap.@name)     var oreScrap = @ore-scrap;
volatile mlog(@coal.@name)      var oreCoal = @ore-coal;
volatile mlog(@titanium.@name)  var oreTitanium = @ore-titanium;
volatile mlog(@thorium.@name)   var oreThorium = @ore-thorium;
volatile mlog(@sand.@name)      var oreSand = @sand-floor;

/// Lookup:
var ore = sorter1.@config;
var floorOre = @this.read(ore.@name);
print(floorOre);
```

compiles to:

```mlog
# Initialization:
set copper @ore-copper
set lead @ore-lead
set scrap @ore-scrap
set coal @ore-coal
set titanium @ore-titanium
set thorium @ore-thorium
set sand @sand-floor
# Lookup:
sensor .ore sorter1 @config
sensor *tmp1 .ore @name
read .floorOre @this *tmp1
print .floorOre
```

There's a potential problem, though: sand can be mined on both `@sand-floor` and `@darksand`, so further processing might be needed to handle that case.

Since the `@name` property is defined even for Mindustry content which doesn't have a logic ID assigned, a reverse lookup would also be possible:

```Mindcode
#set target = 8;
#set remarks = comments;

/// Initialization:
volatile mlog(@ore-copper.@name)    var floorCopper = @copper;
volatile mlog(@ore-lead.@name)      var floorLead = @lead;
volatile mlog(@ore-scrap.@name)     var floorScrap = @scrap;
volatile mlog(@ore-coal.@name)      var floorCoal = @coal;
volatile mlog(@ore-titanium.@name)  var floorTitanium = @titanium;
volatile mlog(@ore-thorium.@name)   var floorThorium = @thorium;
volatile mlog(@sand-floor.@name)    var floorSand = @sand;
volatile mlog(@darksand.@name)      var floorDarksand = @sand;

/// Lookup:
getBlock(10, 20, , out floor);
var ore = @this.read(floor.@name);
print(ore);
```

compiles to:

```mlog
# Initialization:
set ore-copper @copper
set ore-lead @lead
set ore-scrap @scrap
set ore-coal @coal
set ore-titanium @titanium
set ore-thorium @thorium
set sand-floor @sand
set darksand @sand
# Lookup:
ucontrol getBlock 10 20 0 0 :floor
sensor *tmp2 :floor @name
read .ore @this *tmp2
print .ore
```

`ore` will be set to null if the floor type is not included among the lookup variables. 

> [!TIP]
> This technique is especially useful for Mindcode objects which don't have a logic ID assigned, as it is not possible to create efficient `case` expressions for them.

As this technique depends on the values of variables with static names, each content type can be handled by at most one lookup table. It is, however, possible to create the variables in another processor. The remote processor needs to have the variables defined. A Mindcode remote module could be used for that:

```Mindcode
#set target = 8;

module lookup;

volatile mlog(@ore-copper.@name)    var floorCopper = @copper;
volatile mlog(@ore-lead.@name)      var floorLead = @lead;
volatile mlog(@ore-scrap.@name)     var floorScrap = @scrap;
volatile mlog(@ore-coal.@name)      var floorCoal = @coal;
volatile mlog(@ore-titanium.@name)  var floorTitanium = @titanium;
volatile mlog(@ore-thorium.@name)   var floorThorium = @thorium;
volatile mlog(@sand-floor.@name)    var floorSand = @sand;
volatile mlog(@darksand.@name)      var floorDarksand = @sand;
```

compiles to:

```mlog
set ore-copper @copper
set ore-lead @lead
set ore-scrap @scrap
set ore-coal @coal
set ore-titanium @titanium
set ore-thorium @thorium
set sand-floor @sand
set darksand @sand
set *signature "0:v1"
wait 1e12
jump 9 always 0 0
```

This module could be used in a program like this:

```Mindcode
#set target = 8;

linked lookup = processor1;

getBlock(10, 20, , out floor);
var ore = lookup.read(floor.@name);
print(ore);
```

compiles to:

```mlog
ucontrol getBlock 10 20 0 0 :floor
sensor *tmp2 :floor @name
read .ore processor1 *tmp2
print .ore
```

# Efficient static data representation

Mindustry 8 allows reading individual characters from a string, resulting in a UTF-16 value of the given character. This can be used to pack several different integer values into a string, which can then be passed around the program, and the information can be decoded when needed. Mindcode provides the [`encode()` function](SYNTAX-4-FUNCTIONS.markdown#the-encode-function) for this purpose.

> [!NOTE]
> Not every integer value can be encoded into a string. See the documentation of the `encode()` function for more details.

To illustrate this technique, consider this excerpt from the [Base Builder project](https://github.com/cardillan/golem/tree/main/base-builder):

```Mindcode
#set target = 8;
#set symbolic-labels = true;

// The encoded values will be shifted by this offset to avoid unsupported characters in the resulting string
const Common_offset = 74;

def packCfg(type, x, y, rotation, ind)
    // The `A` - Common_offset ensures the resulting string starts with an "A".
    encode(Common_offset, 'A' - Common_offset, type.@id, round(2 * x), round(2 * y), rotation, ind) + "-" + type.@name + "-" + ind;
end;

void unpackCfg(cfg, out type, out x, out y, out rotation, out ind)
    var index = 1;
    type = lookup(:block, char(cfg, index++) - Common_offset);
    x = (char(cfg, index++) - Common_offset) / 2;
    y = (char(cfg, index++) - Common_offset) / 2;
    rotation = char(cfg, index++) - Common_offset;
    ind = char(cfg, index++) - Common_offset;
end;


const PRESS     = packCfg(@graphite-press,  -2.5, +1.5, 0, 0);
const BATTERY1  = packCfg(@battery,          0.0, +5.0, 0, 1);
const BATTERY2  = packCfg(@battery,         +1.0, +5.0, 0, 2);

// Since each encoded string is unique (this is also ensured by using a unique index),
// it is possible to create variables whose mlog name is the encoded string
volatile noinit mlog(PRESS) var press;
volatile noinit mlog(BATTERY1) var battery1;
volatile noinit mlog(BATTERY2) var battery2;

// The encoded value can then be passed into a function and decoded there
noinline void build(in cfg)
    var type, x, y, rotation, ind;
    unpackCfg(cfg, out type, out x, out y, out rotation);
    // Do something with the decoded values...
    var block = buildBlock(type, @thisx + x, @thisy + y, rotation);
    @this.write(block, cfg);
end;

noinline def buildBlock(type, x, y, rotation)
    // Builds and returns the block
    print(type, x, y, rotation);
    return null;
end;

// Process the configuration
build(PRESS);
build(BATTERY1);
build(BATTERY2);
```

The compiled code is

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set :build:cfg "AJEMJJ-graphite-press-0"
    op add :build*retaddr @counter 1
    jump label_10 always 0 0
    set :build:cfg "AJTJK-battery-1"
    op add :build*retaddr @counter 1
    jump label_10 always 0 0
    set :build:cfg "ALTJL-battery-2"
    op add :build*retaddr @counter 1
    jump label_10 always 0 0
end
        # Function: noinline void build(in cfg)
        # Function: void unpackCfg(in cfg, out type, out x, out y, out rotation, out ind)
label_10:
            read *tmp1 :build:cfg 1
            op sub *tmp2 *tmp1 74
            lookup block :buildBlock:type *tmp2
            read *tmp5 :build:cfg 2
            op sub *tmp6 *tmp5 74
            op div :unpackCfg:x *tmp6 2
            read *tmp9 :build:cfg 3
            op sub *tmp10 *tmp9 74
            op div :unpackCfg:y *tmp10 2
            read *tmp13 :build:cfg 4
            op sub :buildBlock:rotation *tmp13 74
        op add :buildBlock:x @thisx :unpackCfg:x
        op add :buildBlock:y @thisy :unpackCfg:y
        op add :buildBlock*retaddr @counter 1
        jump label_27 always 0 0
        write :buildBlock*retval @this :build:cfg
    set @counter :build*retaddr
        # Function: noinline def buildBlock(in type, in x, in y, in rotation)
label_27:
        print :buildBlock:type
        print :buildBlock:x
        print :buildBlock:y
        print :buildBlock:rotation
        set :buildBlock*retval null
    set @counter :buildBlock*retaddr
draw triangle AJEMJJ-graphite-press-0 AJTJK-battery-1 ALTJL-battery-2 0 0 0
```

The constants contain block types and positions and are used as instructions to the block builder to build individual blocks. The block configuration is decoded from the string, and when the block is actually built it is stored in the corresponding variable using `@this.write(block, cfg)` (in the actual project it is actually written to several different processors this way). When the block is built, it is therefore immediately accessible via the corresponding variable.

The type name and index are appended to the encoded configuration, which isn't used when decoding the configuration but allows identifying the variable in the processor **Vars** screen. The actual variables look like this:

```
AJEMJJ-graphite-press-0
AJTJK-battery-1
ALTJL-battery-2
```

# Loop unrolling

One of Mindcode's strongest optimization tools is loop unrolling. When a loop is unrolled, instructions manipulating the loop control variable are eliminated, as well as jumps, potentially saving a lot of execution time. For a loop to be unrolled, the following conditions must be met:

* Mindcode must be able to determine the number of iterations.
* Only one loop control variable is used.
* Only simple updates of the loop control variable are used.

Furthermore, Mindcode is able to unroll more complex loops on [advanced optimization level](optimizations/LOOP-UNROLLING.markdown#loop-unrolling-preconditions).

## Determining the number of iterations

Mindcode determines the number of iterations by analyzing the loop condition. It doesn't take `break` statements into account. The following loop won't get unrolled, even though the maximal number of iterations is obvious:

```Mindcode
i = 0;
while switch1.enabled do
    print(++i);
    if i > 10 then break; end;
end;
```

You can rewrite the loop to allow unrolling by switching the conditions:

```Mindcode
i = 0;
do
    if not switch1.enabled then break; end;
    print(i++);
while i <= 10; 
```

## Using just one loop control variable

Sometimes a loop needs to be rewritten so that a single loop control variable is used. Consider this code for reversing an array stored in a memory cell:

```Mindcode
const SIZE = 10;
for var i = 0, j = SIZE - 1; i < j; i++, j-- do
    var t = cell1[i];
    cell1[i] = cell1[j];
    cell1[j] = t;
end;
```

This code can't be unrolled because there are two control variables. A better approach is to use just one loop control variable and derive the other variable from it:

```Mindcode
const SIZE = 10;
for var i in 0 ... SIZE \ 2 do
    var j = SIZE - 1 - i;
    var t = cell1[i];
    cell1[i] = cell1[j];
    cell1[j] = t;
end;
```

Since `j` isn't used in the loop condition, the loop can be unrolled.

## Using simple updates of control variables

On `advanced` level, loop unrolling is capable to unroll almost any deterministic loop, assuming the updates to the control variable are "simple," meaning they do not depend on any other variable than the loop control variable. More complex expressions might need to be rewritten to multiple simple updates. This loop cannot be unrolled:

```Mindcode
i = 0;
while i < 1000 do
    i = 2 * i + 1;
    println(i);
end;
```

but this can:

```Mindcode
i = 0;
while i < 1000 do
    i = 2 * i;
    i++;
    println(i);
end;
```

```mlog
print "1\n3\n7\n15\n31\n63\n127\n255\n511\n1023\n"
```

# Arrays in loops

Mindcode offers two mechanisms to iterate over arrays: list iteration loops, and loops accessing array elements using the loop control variable as an index. Oftentimes, it is possible to express the same algorithm using either of these two approaches, but they may offer different performance in Mindcode due to various factors.

## Arrays in unrolled loops

If the loop over an array or arrays gets unrolled, the produced code is effectively the same in most cases. However, in cases where the list iteration loop updates the array, Mindcode is unable to properly resolve the self-referential update and produces suboptimal code. Compare these two approaches:

```Mindcode
param p = 0;
var array[3];

for var out a in array do
    a++;
end;

print(array);
```

produces

```mlog
op add :a .array*0 1
set .array*0 :a
op add :a .array*1 1
set .array*1 :a
op add :a .array*2 1
set .array*2 :a
print .array*0
print .array*1
print :a
```

Index-based approach, on the other hand:

```Mindcode
var array[3];

for i in 0 ... length(array) do
    array[i]++;
end;

print(array);
```

produces

```mlog
op add .array*0 .array*0 1
op add .array*1 .array*1 1
op add *tmp1 .array*2 1
op add .array*2 .array*2 1
print .array*0
print .array*1
print *tmp1
```

The long-term goal is to produce identical, optimal code in both of these cases. At this moment, there's room for improvement in both approaches, but index-based loops may produce better code in some circumstances.

## Arrays in non-unrolled loops

When the loop cannot get unrolled for some reason, list iteration loops are generally faster than loops using index-based array access. When more than a single variable is used, or when the array is modified in the loop, list iteration loops provide much better performance than index-based loops. Index-based access may be preferable when the arrays are huge, as a single jump table can be generated for the array to be accessed from multiple places of the program, saving a considerable amount of instruction space.  

Example of simple array access:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var array[4];

var x = 0;
/// List iteration loop (5 instructions per iteration)
for var a in array do
    x += a;
end;

var y = 0;
/// Index-based access (7 instructions per iteration)
for var i in 0 ... length(array) do
    y += array[i];
end;

print(x, y);
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    set .x 0
    # List iteration loop (5 instructions per iteration)
    set :a .array*0
    op add *tmp0 @counter 1
    jump label_12 always 0 0
    set :a .array*1
    op add *tmp0 @counter 1
    jump label_12 always 0 0
    set :a .array*2
    op add *tmp0 @counter 1
    jump label_12 always 0 0
    set :a .array*3
    set *tmp0 null
    label_12:
        op add .x .x :a
        set @counter *tmp0
    set .y 0
    # Index-based access (7 instructions per iteration)
    set :i 0
label_16:
        op mul *tmp3 :i 2
        op add @counter @counter *tmp3
        set *tmp2 .array*0
        jump label_25 always 0 0
        set *tmp2 .array*1
        jump label_25 always 0 0
        set *tmp2 .array*2
        jump label_25 always 0 0
        set *tmp2 .array*3
    label_25:
        op add .y .y *tmp2
    op add :i :i 1
    jump label_16 lessThan :i 4
    print .x
    print .y
```

The difference increases with each additional array access within the loop:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var a[4], b[4], c[4];

/// List iteration loop (7 instructions per iteration)
for var out x in a; var y in b; var z in c do
    x = y + z;
end;

/// Index-based access (15 instructions per iteration)
for var i in 0 ... length(a) do
    a[i] = b[i] + c[i];
end;

print(a);
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    # List iteration loop (7 instructions per iteration)
    set :y .b*0
    set :z .c*0
    op add *tmp0 @counter 1
    jump label_18 always 0 0
    set .a*0 :x
    set :y .b*1
    set :z .c*1
    op add *tmp0 @counter 1
    jump label_18 always 0 0
    set .a*1 :x
    set :y .b*2
    set :z .c*2
    op add *tmp0 @counter 1
    jump label_18 always 0 0
    set .a*2 :x
    set :y .b*3
    set :z .c*3
    set *tmp0 null
    label_18:
        op add :x :y :z
        set @counter *tmp0
    set .a*3 :x
    # Index-based access (15 instructions per iteration)
    set :i 0
label_22:
        op mul *tmp9 :i 2
        op add @counter @counter *tmp9
        set *tmp5 .b*0
        jump label_31 always 0 0
        set *tmp5 .b*1
        jump label_31 always 0 0
        set *tmp5 .b*2
        jump label_31 always 0 0
        set *tmp5 .b*3
    label_31:
        op add @counter @counter *tmp9
        set *tmp7 .c*0
        jump label_39 always 0 0
        set *tmp7 .c*1
        jump label_39 always 0 0
        set *tmp7 .c*2
        jump label_39 always 0 0
        set *tmp7 .c*3
    label_39:
        op add *tmp8 *tmp5 *tmp7
        op add @counter @counter *tmp9
        set .a*0 *tmp8
        jump label_48 always 0 0
        set .a*1 *tmp8
        jump label_48 always 0 0
        set .a*2 *tmp8
        jump label_48 always 0 0
        set .a*3 *tmp8
    label_48:
    op add :i :i 1
    jump label_22 lessThan :i 4
    print .a*0
    print .a*1
    print .a*2
    print .a*3
```

The reason is that in the list iteration loop, all array accesses are performed using direct access to elements, while in index-based access, each array element is accessed separately using a jump table.

Optimizations aimed at merging multiple array accesses are planned but aren't yet available.

> [!NOTE]
> Many different array access patterns can be encoded using parallel list-iteration syntax, subarrays and/or the `descending` keyword. If your algorithm accesses the arrays linearly, there's probably a way to encode it using list-iteration loops.

For example, a list-iteration loop reversing an array can be encoded like this:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;

var array[10];

for
    var out a in array[0 ... length(array) \ 2];
    var out b in array[length(array) - length(array) \ 2 ... length(array)] descending
do
    var x = a;
    a = b;
    b = x;
end;

print(array);
```

### Breaking out of list iteration loops

List-iteration loops are always generated for the entire array. If you want to iterate over a part of the array only, use subarrays instead of the `break` statement to limit the number of iterations, preventing both generation of unwanted iterations and code for computing/testing iterations:

```Mindcode
#set symbolic-labels = true;
#set loop-unrolling = none;
#set remarks = comments;

var array[10];

/// Looping through half of the array: break 
var i = 0;
for var a in array do
    print(a);
    if ++i >= length(array) \ 2 then
        break;
    end;
end; 

/// Looping through half of the array: subarray
for var a in array[0 ... length(array) \ 2] do
    print(a);
end; 
```

produces

```mlog
# Mlog code compiled with support for symbolic labels
# You can safely add/remove instructions, in most parts of the program
# Pay closer attention to sections of the program manipulating @counter
    # Looping through half of the array: break 
    set .i 0
    set :a .array*0
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*1
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*2
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*3
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*4
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*5
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*6
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*7
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*8
    op add *tmp0 @counter 1
    jump label_30 always 0 0
    set :a .array*9
    set *tmp0 null
    label_30:
        print :a
        op add .i .i 1
        jump label_34 greaterThanEq .i 5
        set @counter *tmp0
    # Looping through half of the array: subarray
label_34:
    set :a.1 .array*0
    op add *tmp3 @counter 1
    jump label_48 always 0 0
    set :a.1 .array*1
    op add *tmp3 @counter 1
    jump label_48 always 0 0
    set :a.1 .array*2
    op add *tmp3 @counter 1
    jump label_48 always 0 0
    set :a.1 .array*3
    op add *tmp3 @counter 1
    jump label_48 always 0 0
    set :a.1 .array*4
    set *tmp3 null
    label_48:
        print :a.1
        set @counter *tmp3
```

---

[&#xAB; Previous: Extending Mindcode](SYNTAX-EXTENSIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Schemacode &#xBB;](SCHEMACODE.markdown)
