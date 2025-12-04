# Logic functions

Mindcode allows calling/executing most Mindustry Logic instructions via functions. The available instruction set depends on the Mindustry version and processor type (edition). It is possible to choose the target Mindustry version using the [`#set target` directive](SYNTAX-5-OTHER.markdown#option-target), or a [command-line argument](TOOLS-CMDLINE.markdown).

A specific `7.1` target was added to Mindcode, where the `getBlock` and `ulocate` functions return the building that was found at given coordinates or located using the criteria. This update makes the most occurring use case, where the located building is the only used output of the function, a natural way to use the function.

The `8.0` and `8.1` targets represent the pre-released and development [Mindustry 8 versions](MINDUSTRY-8.markdown).

At this point, `7.1` is still the default target for both command line tool and web application. `8.1` will become the default target when Mindustry 8 is released.

All supported functions and their respective Mindustry Logic instruction counterparts can be found in the function reference: 

* [Function reference for target 6.0](FUNCTIONS-60.markdown)
* [Function reference for target 7.0](FUNCTIONS-70.markdown)
* [Function reference for target 7.1](FUNCTIONS-71.markdown)
* [Function reference for target 8.0](FUNCTIONS-80.markdown)
* [Function reference for target 8.1](FUNCTIONS-81.markdown)

> [!TIP]
> The function reference serves just to document all existing functions and the way they are compiled to Mindustry Logic, but it does not aim to describe the behavior of the functions/instructions. To understand what effect a particular Mindcode function has, please refer to the [Yruei's Mlog Documentation](https://yrueii.github.io/MlogDocs/).

## Instruction mapping rules

Some functions return a value representing the most significant output of the instruction. Additional output values are provided through output function parameters. Many of the instructions do not have any output value. There are no instructions having an input/output parameter, except the `sync` instruction.

Finding a Mindcode function corresponding to a specific instruction should be relatively easy most of the time, as the functions are mapped to instructions using simple rules:

* If the instruction has just one form (such as `getlink`), the function name corresponds to the instruction name. All instruction names are lowercase.
* If the instruction takes several forms depending on the exact operation the instruction performs (such as `draw` or `ucontrol`), the function name corresponds to the operation. In this case, the function name can be camelCase (such as `itemTake`).

The disparity between those two kinds of functions is a consequence of keeping Mindcode nomenclature as close to Mindustry Logic as possible.

There are a few exceptions to these rules:
* In Mindustry Logic 8, both `print` and `draw print` would map to the `print()` function. The `draw print` instruction is instead mapped to the `drawPrint()` function.
* Both `ucontrol stop` and `stop` would map to the `stop()` function. The `stop` instruction is instead mapped to the `stopProcessor()` function.
* `ucontrol getBlock` is similar to the new `getblock` World Processor instruction. The resulting functions only differ in case.
* The `status` World Processor instruction distinguishes clearing and applying the status by an enumerated parameter (`true` or `false`), which is not very readable. Mindcode instead creates separate functions, `applyStatus()` and `clearStatus()`.

## Methods

Some instructions perform an operation on an object (a linked block), which is passed as an argument to one of the instruction parameters. In these cases, the instruction can be mapped to a method called on the given block, e.g., `block.shoot(x, y, doShoot);` translates to `control shoot :block :x :y :doShoot 0`.

In some cases, the instruction can be invoked either as a function or as a method (`printflush(message1)` or `message1.printflush()`). All existing mappings are shown in the function reference above.

## Alternative `control` syntax

There is a special case for `control` instruction setting a single value on a linked block, for whose Mindcode accepts the following syntax:

- `block.enabled = boolean`, which is equivalent to `block.enabled(boolean)`
- `block.config = value`, which is equivalent to `block.config(value)`
- `block.color = value`, which is equivalent to `block.color(value)`

The `block` in the examples can be a regular variable or a linked variable.

Currently, the property access syntax cannot be used with the new [`setprop`](FUNCTIONS-70.markdown#instruction-set-prop) instruction.

## Alternative `sensor` syntax

The `sensor` method accepts an expression, not just constant property name, as an argument:

```Mindcode
var amount = vault1.sensor(useTitanium ? @titanium : @copper);
```

If the property you're trying to obtain is hard-coded, you can again use an alternate syntax: `amount = storage.@thorium` instead of the longer equivalent `amount = storage.sensor(@thorium)`. Either form can be used for any property, not just item types, such as `@unit.@dead` instead of `@unit.sensor(@dead)`.

Again, the `vault1` or `storage` in the examples can be a variable or a linked block object.

## The `min()` and `max()` functions

The `min()` and `max()` functions in Mindcode can take two or more arguments:

```Mindcode
print(min(a, b, c));
var total = max(i, j, k, l);
```

Unlike the Mindustry `op min`/`op max` operations, the `min()` and `max()` functions may return `null` if all of their arguments are `null` as well. Otherwise, `null` is converted to zero as is usual in Mindustry Logic.

## The `message()` function

The `message()` function corresponds to the `message` World Processor instruction. In Mindustry Logic 8, this instruction has an output parameter `success`, which receives an indication of whether the function succeeded. It is possible to pass in a special Mindustry identifier `@wait` as an argument to this parameter, in which case the function waits until it can successfully complete, and no output value is provided (to ensure backwards compatibility with earlier Mindustry versions).

Mindcode therefore allows passing in the `@wait` built-in value as an argument to this function, even though the parameter is an output one. When the `@wait` value is used as an argument, an `out` modifier must not be used.

## The `print()` function

The print function, which corresponds to the `print` instruction, is described in more detail in the section related to the [text output functions](SYNTAX-4-FUNCTIONS.markdown#text-output).

## The `sync()` function

A `sync` instruction (available in Mindustry Logic since version 7.0 build 146) is mapped to a `sync()` function. The function has one parameter—a variable to be synchronized across the network (namely, from the server to all clients). A [volatile variable](SYNTAX-1-VARIABLES.markdown#regular-variables) must be passed as an argument to this function, otherwise a compilation error occurs.

```Mindcode
#set target = 7w;
volatile var synced;
sync(synced);
var before = synced;
wait(1000);
var after = synced;
print(after - before);
```

This snippet of code is meant to compute a difference in the value of `synced` caused by external synchronization, and produces this mlog code:

```mlog
sync .synced
set .before .synced
wait 1000
set .after .synced
op sub *tmp0 .after .before
print *tmp0
```

As can be seen, the code actually does compute the difference between the value of the variable from two different points in time.

---

[&#xAB; Previous: Functions](SYNTAX-4-FUNCTIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: System library &#xBB;](SYSTEM-LIBRARY.markdown)
