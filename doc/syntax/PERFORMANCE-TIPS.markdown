# Performance tips

This document contains some tips for writing better-performing code in Mindcode.

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

which compiles to

```mlog
op len :length :x :y
print :length
```

Note: an optimization which would replace the sequence of the four instructions with `op len` is planned, but hasn't been implemented yet.

# Conditions

At this moment, Mindcode produces suboptimal code for conditions using boolean operators. For example, this code

```Mindcode
if x > 0 and x < 10 then
    print("yes");
end;
printflush(message1);
```

compiles to

```mlog
op greaterThan *tmp0 :x 0
op lessThan *tmp1 :x 10
op land *tmp2 *tmp0 *tmp1
jump 5 equal *tmp2 false
print "yes"
printflush message1
```

A better mlog code can be written like this:

```
jump skip lessThanEq :x 0
jump skip greaterThanEq :x 10
print "yes"
skip:
printflush message1
```

The problem is not as pronounced when the condition doesn't contain relational operators:

```Mindcode
if switch1.enabled and switch2.enabled then
    print("yes");
end;
printflush(message1);
```

compiles to

```mlog
sensor *tmp0 switch1 @enabled
sensor *tmp1 switch2 @enabled
op land *tmp2 *tmp0 *tmp1
jump 5 equal *tmp2 false
print "yes"
printflush message1
```

A slightly better mlog code can be written like this:

```
sensor *tmp0 switch1 @enabled
jump 5 equal *tmp0 false
sensor *tmp1 switch2 @enabled
jump 5 equal *tmp1 false
print "yes"
printflush message1
```

For the next version of Mindcode, an update to significantly improve conditions is planned. It is advised not to rewrite your code if the suboptimal performance is not causing you problems and wait for improvements in Mindcode. In cases when the code generated for the usual conditions is unacceptable, the following tricks can be used:

## Using nested `if` conditions

```
if x > 0 then
    if x < 10 then
        print("yes");
    end;
end;
printflush(message1);
```

compiles into:

```mlog
jump 3 lessThanEq :x 0
jump 3 greaterThanEq :x 10
print "yes"
printflush message1
```

## Using `case` expressions

Case expressions evaluate conditions differently and may produce better code, if it is possible to express the condition as a `when` expression:

```Mindcode
case x
    when 1 ... 10 then print("yes");
end;
printflush(message1);
```

compiles into:

```mlog
jump 3 lessThan :x 1
jump 3 greaterThanEq :x 10
print "yes"
printflush message1
```

Mindcode applies a very powerful optimization to case expressions, especially to larger case expressions. A case expression typically produces much faster code than a series of `if` statements. 

## Avoiding boolean operators in loop conditions

If the condition is part of a loop, it might be possible to avoid the boolean expression in it:

```Mindcode
// while x > 0 and x < 10 do
while x > 0 do
    if x >= 10 then break; end;
    print("in loop");
    x = vault1.@coal;  // Just changing the value of x in some way       
end;
printflush(message1);
```

which compiles into practically optimal code:

```mlog
jump 5 lessThanEq :x 0
jump 5 greaterThanEq :x 10
print "in loop"
sensor :x vault1 @coal
jump 1 greaterThan :x 0
printflush message1
```

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

compiles to

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

compiles to

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
    // The `A` - Common_offset ensures the resulting string start with "A".
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

Furthermore, Mindcode is able to unroll more complex loops on [advanced optimization level](SYNTAX-6-OPTIMIZATIONS.markdown#loop-unrolling-preconditions).

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

println(array);
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
print "\n"
```

Index-based approach, on the other hand:

```Mindcode
param p = 0;
var array[3];

for i in 0 ... length(array) do
    array[i]++;
end;

println(array);
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
print "\n"
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

[« Previous: Troubleshooting Mindcode](TROUBLESHOOTING.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Mindustry Tips and Tricks »](MINDUSTRY-TIPS-N-TRICKS.markdown)
