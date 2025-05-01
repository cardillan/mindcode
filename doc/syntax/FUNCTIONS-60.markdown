# Function reference for Mindustry Logic 6.0

This document contains function reference for all built-in Mindcode functions. Functions are grouped by the
instruction they encapsulate, so that functions with similar logic are listed together. The Mindcode source
listed in the **Function call** column is compiled to the instruction in the **Generated instruction**
column.

In some cases, a single instruction can be generated in more than one way (e.g. the `radar` instruction,
which can be written as a `turret.radar` function, or as a `radar` function which takes `turret` as a parameter).
Both ways are identical. Additionally, some functions have output parameters, which are marked by the 'out' modifier.
Output parameters are optional, and you may omit them if you don't need the value they return. Mindcode allows
you to omit all optional argument, but in this case the entire instruction will be considered useless
and may be removed by the optimizer.

Instruction names in this documentation are present as they appear in Mindustry user interface. Examples of
generated code use mlog opcodes.

# Index

* Micro Processor, Logic Processor and Hyper Processor
  * [Instruction `Read`](#instruction-read)
  * [Instruction `Write`](#instruction-write)
  * [Instruction `Draw`](#instruction-draw)
  * [Instruction `Print`](#instruction-print)
  * [Instruction `Draw Flush`](#instruction-draw-flush)
  * [Instruction `Print Flush`](#instruction-print-flush)
  * [Instruction `Get Link`](#instruction-get-link)
  * [Instruction `Control`](#instruction-control)
  * [Instruction `Radar`](#instruction-radar)
  * [Instruction `Sensor`](#instruction-sensor)
  * [Instruction `Set`](#instruction-set)
  * [Instruction `Operation`](#instruction-operation)
  * [Instruction `End`](#instruction-end)
  * [Instruction `Jump`](#instruction-jump)
  * [Instruction `Unit Bind`](#instruction-unit-bind)
  * [Instruction `Unit Control`](#instruction-unit-control)
  * [Instruction `Unit Radar`](#instruction-unit-radar)
  * [Instruction `Unit Locate`](#instruction-unit-locate)
* World processor

# Micro Processor, Logic Processor and Hyper Processor


## Instruction `Draw`

Add an operation to the drawing buffer. Does not display anything until drawflush is used.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#draw)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`clear(r, g, b)`|`draw clear r g b 0 0 0`|
|`color(r, g, b, a)`|`draw color r g b a 0 0`|
|`stroke(width)`|`draw stroke width 0 0 0 0 0`|
|`line(x, y, x2, y2)`|`draw line x y x2 y2 0 0`|
|`rect(x, y, width, height)`|`draw rect x y width height 0 0`|
|`lineRect(x, y, width, height)`|`draw lineRect x y width height 0 0`|
|`poly(x, y, sides, radius, rotation)`|`draw poly x y sides radius rotation 0`|
|`linePoly(x, y, sides, radius, rotation)`|`draw linePoly x y sides radius rotation 0`|
|`triangle(x, y, x2, y2, x3, y3)`|`draw triangle x y x2 y2 x3 y3`|
|`image(x, y, image, size, rotation)`|`draw image x y image size rotation 0`|

## Instruction `Print`

Add text to the print buffer. Does not display anything until printflush is used.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#print)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`print(what)`|`print what`|

## Instruction `Draw Flush`

Flush queued Draw operations to a display.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#draw-flush)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`display1.drawflush()`|`drawflush display1`|
|`drawflush(display1)`|`drawflush display1`|

## Instruction `Print Flush`

Flush queued Print operations to a message block.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#print-flush)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`message1.printflush()`|`printflush message1`|
|`printflush(message1)`|`printflush message1`|

## Instruction `Get Link`

Get a processor link by index. Starts at 0.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#get-link)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`block = getlink(linkNum)`|`getlink block linkNum`|

## Instruction `Control`

Control a building.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#control)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`block.enabled(value)`|`control enabled block value 0 0 0`|
|`block.enabled = value`|`control enabled block value 0 0 0`|
|`block.shoot(x, y, shoot)`|`control shoot block x y shoot 0`|
|`block.shootp(unit, shoot)`|`control shootp block unit shoot 0 0`|
|`block.configure(value)`|`control configure block value 0 0 0`|
|`block.configure = value`|`control configure block value 0 0 0`|
|`block.color(r, g, b)`|`control color block r g b 0`|

## Instruction `Radar`

Locate units around a building with range.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#radar)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = turret.radar(attr1, attr2, attr3, sort, order)`<br/>`attr1` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr2` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr3` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`sort` - one of `:distance`, `:health`, `:shield`, `:armor`, `:maxHealth`.|`radar attr1 attr2 attr3 sort turret order result`|
|`result = radar(attr1, attr2, attr3, sort, turret, order)`<br/>`attr1` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr2` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr3` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`sort` - one of `:distance`, `:health`, `:shield`, `:armor`, `:maxHealth`.|`radar attr1 attr2 attr3 sort turret order result`|

## Instruction `Sensor`

Get data from a building or unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#sensor)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = object.sensor(property)`|`sensor result object property`|

## Instruction `Operation`

Perform an operation on 1-2 variables.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#operation)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = max(a, b)`|`op max result a b`|
|`result = min(a, b)`|`op min result a b`|
|`result = angle(a, b)`|`op angle result a b`|
|`result = len(a, b)`|`op len result a b`|
|`result = noise(a, b)`|`op noise result a b`|
|`result = abs(a)`|`op abs result a 0`|
|`result = log(a)`|`op log result a 0`|
|`result = log10(a)`|`op log10 result a 0`|
|`result = floor(a)`|`op floor result a 0`|
|`result = ceil(a)`|`op ceil result a 0`|
|`result = sqrt(a)`|`op sqrt result a 0`|
|`result = rand(a)`|`op rand result a 0`|
|`result = sin(a)`|`op sin result a 0`|
|`result = cos(a)`|`op cos result a 0`|
|`result = tan(a)`|`op tan result a 0`|

## Instruction `End`

Jump to the top of the instruction stack.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#end)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`end()`|`end`|

## Instruction `Unit Bind`

Bind to the next unit of a type, and store it in @unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-bind)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`unit = ubind(type)`|`ubind type`|

## Instruction `Unit Control`

Control the currently bound unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-control)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`idle()`|`ucontrol idle 0 0 0 0 0`|
|`stop()`|`ucontrol stop 0 0 0 0 0`|
|`move(x, y)`|`ucontrol move x y 0 0 0`|
|`approach(x, y, radius)`|`ucontrol approach x y radius 0 0`|
|`boost(enable)`|`ucontrol boost enable 0 0 0 0`|
|`pathfind()`|`ucontrol pathfind 0 0 0 0 0`|
|`target(x, y, shoot)`|`ucontrol target x y shoot 0 0`|
|`targetp(unit, shoot)`|`ucontrol targetp unit shoot 0 0 0`|
|`itemDrop(to, amount)`|`ucontrol itemDrop to amount 0 0 0`|
|`itemTake(from, item, amount)`|`ucontrol itemTake from item amount 0 0`|
|`payDrop()`|`ucontrol payDrop 0 0 0 0 0`|
|`payTake(takeUnits)`|`ucontrol payTake takeUnits 0 0 0 0`|
|`mine(x, y)`|`ucontrol mine x y 0 0 0`|
|`flag(value)`|`ucontrol flag value 0 0 0 0`|
|`build(x, y, block, rotation, config)`|`ucontrol build x y block rotation config`|
|`getBlock(x, y, out type, out building)`|`ucontrol getBlock x y type building 0`|
|`result = within(x, y, radius)`|`ucontrol within x y radius result 0`|

## Instruction `Unit Radar`

Locate units around the currently bound unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-radar)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = uradar(attr1, attr2, attr3, sort, order)`<br/>`attr1` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr2` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`attr3` - one of `:any`, `:enemy`, `:ally`, `:player`, `:attacker`, `:flying`, `:boss`, `:ground`.<br/>`sort` - one of `:distance`, `:health`, `:shield`, `:armor`, `:maxHealth`.|`uradar attr1 attr2 attr3 sort 0 order result`|

## Instruction `Unit Locate`

Locate a specific type of position/building anywhere on the map. Requires a bound unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-locate)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`found = ulocate(:ore, oreType, out outx, out outy)`|`ulocate ore core true oreType outx outy found 0`|
|`found = ulocate(:building, group, enemy, out outx, out outy, out building)`<br/>`group` - one of `:core`, `:storage`, `:generator`, `:turret`, `:factory`, `:repair`, `:battery`, `:reactor`, `:rally`, `:resupply`.|`ulocate building group enemy @copper outx outy found building`|
|`found = ulocate(:spawn, out outx, out outy, out building)`|`ulocate spawn core true @copper outx outy found building`|
|`found = ulocate(:damaged, out outx, out outy, out building)`|`ulocate damaged core true @copper outx outy found building`|

---

[« Previous: Code optimization](SYNTAX-6-OPTIMIZATIONS.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Function reference for Mindustry Logic 7.0 »](FUNCTIONS-70.markdown)
