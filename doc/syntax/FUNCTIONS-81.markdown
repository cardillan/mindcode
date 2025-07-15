# Function reference for Mindustry Logic 8.1

This document contains function reference for all built-in Mindcode functions. Functions are grouped by the
instruction they encapsulate so that functions with similar logic are listed together. The Mindcode source
listed in the **Function call** column is compiled to the instruction in the **Generated instruction**
column.

In some cases, a single instruction can be generated in more than one way (e.g., the `radar` instruction,
which can be written as a `turret.radar` function, or as a `radar` function which takes `turret` as a parameter).
Both ways are identical. Additionally, some functions have output parameters, which are marked by the 'out' modifier.
Output parameters are optional, and you may omit them if you don't need the value they return. Mindcode allows
you to omit all optional arguments, but in this case the entire instruction will be considered useless
and may be removed by the optimizer.

Instruction names in this documentation are present as they appear in the Mindustry user interface. Examples of
generated code use mlog opcodes.

# Index

* Micro Processor, Logic Processor and Hyper Processor
  * [Instruction `Read`](#instruction-read)
  * [Instruction `Write`](#instruction-write)
  * [Instruction `Draw`](#instruction-draw)
  * [Instruction `Print`](#instruction-print)
  * [Instruction `Print Char`](#instruction-print-char)
  * [Instruction `Format`](#instruction-format)
  * [Instruction `Draw Flush`](#instruction-draw-flush)
  * [Instruction `Print Flush`](#instruction-print-flush)
  * [Instruction `Get Link`](#instruction-get-link)
  * [Instruction `Control`](#instruction-control)
  * [Instruction `Radar`](#instruction-radar)
  * [Instruction `Sensor`](#instruction-sensor)
  * [Instruction `Operation`](#instruction-operation)
  * [Instruction `Lookup`](#instruction-lookup)
  * [Instruction `Pack Color`](#instruction-pack-color)
  * [Instruction `Unpack Color`](#instruction-unpack-color)
  * [Instruction `Wait`](#instruction-wait)
  * [Instruction `Stop`](#instruction-stop)
  * [Instruction `End`](#instruction-end)
  * [Instruction `Unit Bind`](#instruction-unit-bind)
  * [Instruction `Unit Control`](#instruction-unit-control)
  * [Instruction `Unit Radar`](#instruction-unit-radar)
  * [Instruction `Unit Locate`](#instruction-unit-locate)
* World processor
  * [Instruction `Get Block`](#instruction-get-block)
  * [Instruction `Set Block`](#instruction-set-block)
  * [Instruction `Spawn Unit`](#instruction-spawn-unit)
  * [Instruction `Apply Status`](#instruction-apply-status)
  * [Instruction `Weather Sense`](#instruction-weather-sense)
  * [Instruction `Weather Set`](#instruction-weather-set)
  * [Instruction `Spawn Wave`](#instruction-spawn-wave)
  * [Instruction `Set Rule`](#instruction-set-rule)
  * [Instruction `Flush Message`](#instruction-flush-message)
  * [Instruction `Cutscene`](#instruction-cutscene)
  * [Instruction `Effect`](#instruction-effect)
  * [Instruction `Explosion`](#instruction-explosion)
  * [Instruction `Set Rate`](#instruction-set-rate)
  * [Instruction `Fetch`](#instruction-fetch)
  * [Instruction `Sync`](#instruction-sync)
  * [Instruction `Get Flag`](#instruction-get-flag)
  * [Instruction `Set Flag`](#instruction-set-flag)
  * [Instruction `Set Prop`](#instruction-set-prop)
  * [Instruction `Play Sound`](#instruction-play-sound)
  * [Instruction `Set Marker`](#instruction-set-marker)
  * [Instruction `Make Marker`](#instruction-make-marker)
  * [Instruction `Locale Print`](#instruction-locale-print)

# Micro Processor, Logic Processor and Hyper Processor


## Instruction `Read`

Read a variable identified by a name from a linked processor.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#read)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = processor1.read(name)`|`read result processor1 name`|

## Instruction `Write`

Write a number to a variable identified by a name in a linked processor.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#write)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`processor1.write(value, name)`|`write value processor1 name`|

## Instruction `Draw`

Add an operation to the drawing buffer. Does not display anything until `drawflush` is used.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#draw)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`clear(r, g, b)`|`draw clear r g b 0 0 0`|
|`color(r, g, b, a)`|`draw color r g b a 0 0`|
|`col(color)`|`draw col color 0 0 0 0 0`|
|`stroke(width)`|`draw stroke width 0 0 0 0 0`|
|`line(x, y, x2, y2)`|`draw line x y x2 y2 0 0`|
|`rect(x, y, width, height)`|`draw rect x y width height 0 0`|
|`lineRect(x, y, width, height)`|`draw lineRect x y width height 0 0`|
|`poly(x, y, sides, radius, rotation)`|`draw poly x y sides radius rotation 0`|
|`linePoly(x, y, sides, radius, rotation)`|`draw linePoly x y sides radius rotation 0`|
|`triangle(x, y, x2, y2, x3, y3)`|`draw triangle x y x2 y2 x3 y3`|
|`image(x, y, image, size, rotation)`|`draw image x y image size rotation 0`|
|`drawPrint(x, y, align)`<br/>`align` - one of `:center`, `:top`, `:bottom`, `:left`, `:right`, `:topLeft`, `:topRight`, `:bottomLeft`, `:bottomRight`.|`draw print x y align 0 0 0`|
|`translate(x, y)`|`draw translate x y 0 0 0 0`|
|`scale(x, y)`|`draw scale x y 0 0 0 0`|
|`rotate(degrees)`|`draw rotate 0 0 degrees 0 0 0`|
|`reset()`|`draw reset 0 0 0 0 0 0`|

## Instruction `Print`

Add text to the print buffer. Does not display anything until printflush is used.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#print)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`print(what)`|`print what`|

## Instruction `Print Char`

Add a UTF-16 character or content icon to the print buffer.
Does not display anything until Print Flush is used.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`printchar(value)`|`printchar value`|

## Instruction `Format`

Replace next placeholder in text buffer with a value. Does not do anything if placeholder pattern is invalid. Placeholder pattern: "{number 0-9}" Example: print "test {0}"; format "example"

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`format(value)`|`format value`|

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
|`block.configure(value)`<br/>Deprecated. Use `config` instead.|`control config block value 0 0 0`|
|`block.config(value)`|`control config block value 0 0 0`|
|`block.configure = value`<br/>Deprecated. Use `config` instead.|`control config block value 0 0 0`|
|`block.config = value`|`control config block value 0 0 0`|
|`block.color(packedColor)`|`control color block packedColor 0 0 0`|
|`block.color = packedColor`|`control color block packedColor 0 0 0`|

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
|`result = object.sensor(property)`<br/>`property` - accepts `@totalItems`, `@firstItem`, `@totalLiquids`, `@totalPower`, `@itemCapacity`, `@liquidCapacity`, `@powerCapacity`, `@powerNetStored`, `@powerNetCapacity`, `@powerNetIn`, `@powerNetOut`, `@ammo`, `@ammoCapacity`, `@currentAmmoType`, `@memoryCapacity`, `@health`, `@maxHealth`, `@heat`, `@shield`, `@armor`, `@efficiency`, `@progress`, `@timescale`, `@rotation`, `@x`, `@y`, `@velocityX`, `@velocityY`, `@shootX`, `@shootY`, `@cameraX`, `@cameraY`, `@cameraWidth`, `@cameraHeight`, `@displayWidth`, `@displayHeight`, `@bufferUsage`, `@operations`, `@size`, `@solid`, `@dead`, `@range`, `@shooting`, `@boosting`, `@mineX`, `@mineY`, `@mining`, `@speed`, `@team`, `@type`, `@flag`, `@controlled`, `@controller`, `@name`, `@payloadCount`, `@payloadType`, `@totalPayload`, `@payloadCapacity`, `@id`, `@enabled`, `@config`, `@color`.|`sensor result object property`|

## Instruction `Operation`

Perform an operation on one or two variables.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#operation)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = max(a, b)`|`op max result a b`|
|`result = min(a, b)`|`op min result a b`|
|`result = angle(a, b)`|`op angle result a b`|
|`result = angleDiff(a, b)`|`op angleDiff result a b`|
|`result = len(a, b)`|`op len result a b`|
|`result = noise(a, b)`|`op noise result a b`|
|`result = abs(a)`|`op abs result a 0`|
|`result = sign(a)`|`op sign result a 0`|
|`result = log(a)`|`op log result a 0`|
|`result = logn(a, b)`|`op logn result a b`|
|`result = log10(a)`|`op log10 result a 0`|
|`result = floor(a)`|`op floor result a 0`|
|`result = ceil(a)`|`op ceil result a 0`|
|`result = round(a)`|`op round result a 0`|
|`result = sqrt(a)`|`op sqrt result a 0`|
|`result = rand(a)`|`op rand result a 0`|
|`result = sin(a)`|`op sin result a 0`|
|`result = cos(a)`|`op cos result a 0`|
|`result = tan(a)`|`op tan result a 0`|
|`result = asin(a)`|`op asin result a 0`|
|`result = acos(a)`|`op acos result a 0`|
|`result = atan(a)`|`op atan result a 0`|

## Instruction `Lookup`

Look up an item/liquid/unit/block type by ID. Total counts of each type can be accessed with @unitCount, @itemCount, @liquidCount, @blockCount.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#lookup)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = lookup(type, index)`<br/>`type` - one of `:item`, `:block`, `:liquid`, `:unit`, `:team`.|`lookup type result index`|

## Instruction `Pack Color`

Pack [0, 1] RGBA components into a single number for drawing or rule-setting.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#pack-color)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = packcolor(r, g, b, a)`|`packcolor result r g b a`|

## Instruction `Unpack Color`

Unpack RGBA components from a color that was packed using Pack Color.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`unpackcolor(out r, out g, out b, out a, color)`|`unpackcolor r g b a color`|

## Instruction `Wait`

Wait a certain number of seconds.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#wait)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`wait(sec)`|`wait sec`|

## Instruction `Stop`

Halt execution of this processor.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#stop)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`stopProcessor()`|`stop`|

## Instruction `End`

Jump to the top of the instruction stack.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#end)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`end()`|`end`|

## Instruction `Unit Bind`

Bind to the next unit of a type and store it in @unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-bind)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`unit = ubind(type)`<br/>`type` - accepts `@dagger`, `@mace`, `@fortress`, `@scepter`, `@reign`, `@nova`, `@pulsar`, `@quasar`, `@vela`, `@corvus`, `@crawler`, `@atrax`, `@spiroct`, `@arkyid`, `@toxopid`, `@flare`, `@horizon`, `@zenith`, `@antumbra`, `@eclipse`, `@mono`, `@poly`, `@mega`, `@quad`, `@oct`, `@risso`, `@minke`, `@bryde`, `@sei`, `@omura`, `@retusa`, `@oxynoe`, `@cyerce`, `@aegires`, `@navanax`, `@alpha`, `@beta`, `@gamma`, `@stell`, `@locus`, `@precept`, `@vanquish`, `@conquer`, `@merui`, `@cleroi`, `@anthicus`, `@anthicus-missile`, `@tecta`, `@collaris`, `@elude`, `@avert`, `@obviate`, `@quell`, `@quell-missile`, `@disrupt`, `@disrupt-missile`, `@renale`, `@latum`, `@evoke`, `@incite`, `@emanate`, `@block`, `@manifold`, `@assembly-drone`, `@scathe-missile`, `@scathe-missile-phase`, `@scathe-missile-surge`, `@scathe-missile-surge-split`, `@turret-unit-build-tower`.|`ubind type`|

## Instruction `Unit Control`

Control the currently bound unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#unit-control)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`idle()`|`ucontrol idle 0 0 0 0 0`|
|`stop()`|`ucontrol stop 0 0 0 0 0`|
|`move(x, y)`|`ucontrol move x y 0 0 0`|
|`approach(x, y, radius)`|`ucontrol approach x y radius 0 0`|
|`autoPathfind()`|`ucontrol autoPathfind 0 0 0 0 0`|
|`pathfind(x, y)`|`ucontrol pathfind x y 0 0 0`|
|`boost(enable)`|`ucontrol boost enable 0 0 0 0`|
|`target(x, y, shoot)`|`ucontrol target x y shoot 0 0`|
|`targetp(unit, shoot)`|`ucontrol targetp unit shoot 0 0 0`|
|`itemDrop(to, amount)`|`ucontrol itemDrop to amount 0 0 0`|
|`itemTake(from, item, amount)`<br/>`item` - accepts `@copper`, `@lead`, `@metaglass`, `@graphite`, `@sand`, `@coal`, `@titanium`, `@thorium`, `@scrap`, `@silicon`, `@plastanium`, `@phase-fabric`, `@surge-alloy`, `@spore-pod`, `@blast-compound`, `@pyratite`, `@beryllium`, `@tungsten`, `@oxide`, `@carbide`, `@fissile-matter`, `@dormant-cyst`.|`ucontrol itemTake from item amount 0 0`|
|`payDrop()`|`ucontrol payDrop 0 0 0 0 0`|
|`payTake(takeUnits)`|`ucontrol payTake takeUnits 0 0 0 0`|
|`payEnter()`|`ucontrol payEnter 0 0 0 0 0`|
|`mine(x, y)`|`ucontrol mine x y 0 0 0`|
|`flag(value)`|`ucontrol flag value 0 0 0 0`|
|`build(x, y, block, rotation, config)`|`ucontrol build x y block rotation config`|
|`building = getBlock(x, y, out type, out floor)`|`ucontrol getBlock x y type building floor`|
|`result = within(x, y, radius)`|`ucontrol within x y radius result 0`|
|`unbind()`|`ucontrol unbind 0 0 0 0 0`|

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
|`found = ulocate(:ore, oreType, out outx, out outy)`<br/>`oreType` - accepts `@copper`, `@lead`, `@metaglass`, `@graphite`, `@sand`, `@coal`, `@titanium`, `@thorium`, `@scrap`, `@silicon`, `@plastanium`, `@phase-fabric`, `@surge-alloy`, `@spore-pod`, `@blast-compound`, `@pyratite`, `@beryllium`, `@tungsten`, `@oxide`, `@carbide`, `@fissile-matter`, `@dormant-cyst`.|`ulocate ore core true oreType outx outy found 0`|
|`building = ulocate(:building, group, enemy, out outx, out outy, out found)`<br/>`group` - one of `:core`, `:storage`, `:generator`, `:turret`, `:factory`, `:repair`, `:battery`, `:reactor`, `:drill`, `:shield`.|`ulocate building group enemy @copper outx outy found building`|
|`building = ulocate(:spawn, out outx, out outy, out found)`|`ulocate spawn core true @copper outx outy found building`|
|`building = ulocate(:damaged, out outx, out outy, out found)`|`ulocate damaged core true @copper outx outy found building`|

# World processor

These instructions are only available to the World Processor,
which can be placed in custom-created levels in Mindustry 7 or higher.


## Instruction `Get Block`

Get tile data at any location.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#get-block)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = getblock(layer, x, y)`<br/>`layer` - one of `:floor`, `:ore`, `:block`, `:building`.|`getblock layer result x y`|

## Instruction `Set Block`

Set tile data at any location.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#set-block)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`setblock(:floor, to, x, y)`|`setblock floor to x y 0 0`|
|`setblock(:ore, to, x, y)`<br/>`to` - accepts `@copper`, `@lead`, `@metaglass`, `@graphite`, `@sand`, `@coal`, `@titanium`, `@thorium`, `@scrap`, `@silicon`, `@plastanium`, `@phase-fabric`, `@surge-alloy`, `@spore-pod`, `@blast-compound`, `@pyratite`, `@beryllium`, `@tungsten`, `@oxide`, `@carbide`, `@fissile-matter`, `@dormant-cyst`.|`setblock ore to x y 0 0`|
|`setblock(:block, to, x, y, team, rotation)`|`setblock block to x y team rotation`|

## Instruction `Spawn Unit`

Spawn unit at a location.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#spawn-unit)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = spawn(unit, x, y, rotation, team)`<br/>`unit` - accepts `@dagger`, `@mace`, `@fortress`, `@scepter`, `@reign`, `@nova`, `@pulsar`, `@quasar`, `@vela`, `@corvus`, `@crawler`, `@atrax`, `@spiroct`, `@arkyid`, `@toxopid`, `@flare`, `@horizon`, `@zenith`, `@antumbra`, `@eclipse`, `@mono`, `@poly`, `@mega`, `@quad`, `@oct`, `@risso`, `@minke`, `@bryde`, `@sei`, `@omura`, `@retusa`, `@oxynoe`, `@cyerce`, `@aegires`, `@navanax`, `@alpha`, `@beta`, `@gamma`, `@stell`, `@locus`, `@precept`, `@vanquish`, `@conquer`, `@merui`, `@cleroi`, `@anthicus`, `@anthicus-missile`, `@tecta`, `@collaris`, `@elude`, `@avert`, `@obviate`, `@quell`, `@quell-missile`, `@disrupt`, `@disrupt-missile`, `@renale`, `@latum`, `@evoke`, `@incite`, `@emanate`, `@block`, `@manifold`, `@assembly-drone`, `@scathe-missile`, `@scathe-missile-phase`, `@scathe-missile-surge`, `@scathe-missile-surge-split`, `@turret-unit-build-tower`.|`spawn unit x y rotation team result`|

## Instruction `Apply Status`

Apply or clear a status effect from a unit.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#apply-status)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`applyStatus(status, unit, duration)`<br/>`status` - one of `:burning`, `:freezing`, `:unmoving`, `:wet`, `:melting`, `:sapped`, `:electrified`, `:spore-slowed`, `:tarred`, `:overdrive`, `:overclock`, `:boss`, `:shocked`, `:blasted`.|`status false status unit duration`|
|`clearStatus(status, unit)`<br/>`status` - one of `:burning`, `:freezing`, `:unmoving`, `:wet`, `:melting`, `:sapped`, `:electrified`, `:spore-slowed`, `:tarred`, `:overdrive`, `:overclock`, `:boss`, `:shocked`, `:blasted`.|`status true status unit 0`|

## Instruction `Weather Sense`

Check if a type of weather is active.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = weathersense(weather)`<br/>`weather` - accepts `@snowing`, `@rain`, `@sandstorm`, `@sporestorm`, `@fog`, `@suspend-particles`.|`weathersense result weather`|

## Instruction `Weather Set`

Set the current state of a type of weather.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`weatherset(weather, active)`<br/>`weather` - accepts `@snowing`, `@rain`, `@sandstorm`, `@sporestorm`, `@fog`, `@suspend-particles`.|`weatherset weather active`|

## Instruction `Spawn Wave`

Spawn a wave.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#spawn-wave)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`spawnwave(x, y, natural)`|`spawnwave x y natural`|

## Instruction `Set Rule`

Set a game rule.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#set-rule)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`setrule(:currentWaveTime, value)`|`setrule currentWaveTime value 0 0 0 0`|
|`setrule(:waveTimer, value)`|`setrule waveTimer value 0 0 0 0`|
|`setrule(:waves, value)`|`setrule waves value 0 0 0 0`|
|`setrule(:wave, value)`|`setrule wave value 0 0 0 0`|
|`setrule(:waveSpacing, value)`|`setrule waveSpacing value 0 0 0 0`|
|`setrule(:waveSending, value)`|`setrule waveSending value 0 0 0 0`|
|`setrule(:attackMode, value)`|`setrule attackMode value 0 0 0 0`|
|`setrule(:enemyCoreBuildRadius, value)`|`setrule enemyCoreBuildRadius value 0 0 0 0`|
|`setrule(:dropZoneRadius, value)`|`setrule dropZoneRadius value 0 0 0 0`|
|`setrule(:unitCap, value)`|`setrule unitCap value 0 0 0 0`|
|`setrule(:mapArea, x, y, width, height)`|`setrule mapArea 0 x y width height`|
|`setrule(:lighting, value)`|`setrule lighting value 0 0 0 0`|
|`setrule(:canGameOver, value)`|`setrule canGameOver value 0 0 0 0`|
|`setrule(:ambientLight, value)`|`setrule ambientLight value 0 0 0 0`|
|`setrule(:solarMultiplier, value)`|`setrule solarMultiplier value 0 0 0 0`|
|`setrule(:dragMultiplier, value)`|`setrule dragMultiplier value 0 0 0 0`|
|`setrule(:ban, value)`|`setrule ban value 0 0 0 0`|
|`setrule(:unban, value)`|`setrule unban value 0 0 0 0`|
|`setrule(:buildSpeed, value, team)`|`setrule buildSpeed value team 0 0 0`|
|`setrule(:unitHealth, value, team)`|`setrule unitHealth value team 0 0 0`|
|`setrule(:unitBuildSpeed, value, team)`|`setrule unitBuildSpeed value team 0 0 0`|
|`setrule(:unitMineSpeed, value, team)`|`setrule unitMineSpeed value team 0 0 0`|
|`setrule(:unitCost, value, team)`|`setrule unitCost value team 0 0 0`|
|`setrule(:unitDamage, value, team)`|`setrule unitDamage value team 0 0 0`|
|`setrule(:blockHealth, value, team)`|`setrule blockHealth value team 0 0 0`|
|`setrule(:blockDamage, value, team)`|`setrule blockDamage value team 0 0 0`|
|`setrule(:rtsMinWeight, value, team)`|`setrule rtsMinWeight value team 0 0 0`|
|`setrule(:rtsMinSquad, value, team)`|`setrule rtsMinSquad value team 0 0 0`|

## Instruction `Flush Message`

Display a message on the screen from the text buffer. If the success result variable is @wait, will wait until the previous message finishes. Otherwise, outputs whether displaying the message succeeded.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#flush-message)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`message(:notify, out success)`|`message notify 0 success`|
|`message(:mission, out success)`|`message mission 0 success`|
|`message(:announce, duration, out success)`|`message announce duration success`|
|`message(:toast, duration, out success)`|`message toast duration success`|

## Instruction `Cutscene`

Manipulate the player camera.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#cutscene)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`cutscene(:pan, x, y, speed)`|`cutscene pan x y speed 0`|
|`cutscene(:zoom, level)`|`cutscene zoom level 0 0 0`|
|`cutscene(:stop)`|`cutscene stop 0 0 0 0`|

## Instruction `Effect`

Create a particle effect.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#effect)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`effect(:warn, x, y)`|`effect warn x y 0 0 0`|
|`effect(:cross, x, y)`|`effect cross x y 0 0 0`|
|`effect(:blockFall, x, y, blocktype)`|`effect blockFall x y 0 0 blocktype`|
|`effect(:placeBlock, x, y, size)`|`effect placeBlock x y size 0 0`|
|`effect(:placeBlockSpark, x, y, size)`|`effect placeBlockSpark x y size 0 0`|
|`effect(:breakBlock, x, y, size)`|`effect breakBlock x y size 0 0`|
|`effect(:spawn, x, y)`|`effect spawn x y 0 0 0`|
|`effect(:trail, x, y, size, color)`|`effect trail x y size color 0`|
|`effect(:breakProp, x, y, size, color)`|`effect breakProp x y size color 0`|
|`effect(:smokeCloud, x, y, color)`|`effect smokeCloud x y 0 color 0`|
|`effect(:vapor, x, y, color)`|`effect vapor x y 0 color 0`|
|`effect(:hit, x, y, color)`|`effect hit x y 0 color 0`|
|`effect(:hitSquare, x, y, color)`|`effect hitSquare x y 0 color 0`|
|`effect(:shootSmall, x, y, rotation, color)`|`effect shootSmall x y rotation color 0`|
|`effect(:shootBig, x, y, rotation, color)`|`effect shootBig x y rotation color 0`|
|`effect(:smokeSmall, x, y, color)`|`effect smokeSmall x y 0 color 0`|
|`effect(:smokeBig, x, y, color)`|`effect smokeBig x y 0 color 0`|
|`effect(:smokeColor, x, y, rotation, color)`|`effect smokeColor x y rotation color 0`|
|`effect(:smokeSquare, x, y, rotation, color)`|`effect smokeSquare x y rotation color 0`|
|`effect(:smokeSquareBig, x, y, rotation, color)`|`effect smokeSquareBig x y rotation color 0`|
|`effect(:spark, x, y, color)`|`effect spark x y 0 color 0`|
|`effect(:sparkBig, x, y, color)`|`effect sparkBig x y 0 color 0`|
|`effect(:sparkShoot, x, y, rotation, color)`|`effect sparkShoot x y rotation color 0`|
|`effect(:sparkShootBig, x, y, rotation, color)`|`effect sparkShootBig x y rotation color 0`|
|`effect(:drill, x, y, color)`|`effect drill x y 0 color 0`|
|`effect(:drillBig, x, y, color)`|`effect drillBig x y 0 color 0`|
|`effect(:lightBlock, x, y, size, color)`|`effect lightBlock x y size color 0`|
|`effect(:explosion, x, y, size)`|`effect explosion x y size 0 0`|
|`effect(:smokePuff, x, y, color)`|`effect smokePuff x y 0 color 0`|
|`effect(:sparkExplosion, x, y, color)`|`effect sparkExplosion x y 0 color 0`|
|`effect(:crossExplosion, x, y, size, color)`|`effect crossExplosion x y size color 0`|
|`effect(:wave, x, y, size, color)`|`effect wave x y size color 0`|
|`effect(:bubble, x, y)`|`effect bubble x y 0 0 0`|

## Instruction `Explosion`

Create an explosion at a location.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#explosion)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`explosion(team, x, y, radius, damage, air, ground, pierce, effect)`|`explosion team x y radius damage air ground pierce effect`|

## Instruction `Set Rate`

Set processor execution speed in instructions/tick.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#set-rate)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`setrate(ipt)`|`setrate ipt`|

## Instruction `Fetch`

Lookup units, cores, players or buildings by index. Indices start at 0 and end at their returned count.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#fetch)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = fetch(:unitCount, team, type)`|`fetch unitCount result team 0 type`|
|`result = fetch(:playerCount, team)`|`fetch playerCount result team 0 0`|
|`result = fetch(:coreCount, team)`|`fetch coreCount result team 0 0`|
|`result = fetch(:buildCount, team, type)`|`fetch buildCount result team 0 type`|
|`result = fetch(:unit, team, index, type)`|`fetch unit result team index type`|
|`result = fetch(:player, team, index)`|`fetch player result team index 0`|
|`result = fetch(:core, team, index)`|`fetch core result team index 0`|
|`result = fetch(:build, team, index, type)`|`fetch build result team index type`|

## Instruction `Sync`

Sync a variable across the network. Limited to 20 times a second per variable.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#sync)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`sync(out var)`|`sync var`|

## Instruction `Get Flag`

Check if a global flag is set.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#get-flag)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`result = getflag(flag)`|`getflag result flag`|

## Instruction `Set Flag`

Set a global flag that can be read by all processors.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#set-flag)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`setflag(flag, value)`|`setflag flag value`|

## Instruction `Set Prop`

Sets a property of a unit or building.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#set-prop)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`object.setprop(property, value)`<br/>`property` - accepts `@totalPower`, `@health`, `@shield`, `@armor`, `@rotation`, `@x`, `@y`, `@velocityX`, `@velocityY`, `@speed`, `@team`, `@flag`, `@payloadType`.|`setprop property object value`|

## Instruction `Play Sound`

Plays a sound. Volume and pan can be a global value, or calculated based on position.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`playsound(:true, sound, volume, pitch, x, y, limit)`<br/>`sound` - accepts `@sfx-artillery`, `@sfx-back`, `@sfx-bang`, `@sfx-beam`, `@sfx-bigshot`, `@sfx-bioLoop`, `@sfx-blaster`, `@sfx-bolt`, `@sfx-boom`, `@sfx-break`, `@sfx-build`, `@sfx-buttonClick`, `@sfx-cannon`, `@sfx-chatMessage`, `@sfx-click`, `@sfx-combustion`, `@sfx-conveyor`, `@sfx-corexplode`, `@sfx-cutter`, `@sfx-door`, `@sfx-drill`, `@sfx-drillCharge`, `@sfx-drillImpact`, `@sfx-dullExplosion`, `@sfx-electricHum`, `@sfx-explosion`, `@sfx-explosionbig`, `@sfx-extractLoop`, `@sfx-fire`, `@sfx-flame`, `@sfx-flame2`, `@sfx-flux`, `@sfx-glow`, `@sfx-grinding`, `@sfx-hum`, `@sfx-largeCannon`, `@sfx-largeExplosion`, `@sfx-laser`, `@sfx-laserbeam`, `@sfx-laserbig`, `@sfx-laserblast`, `@sfx-lasercharge`, `@sfx-lasercharge2`, `@sfx-lasershoot`, `@sfx-machine`, `@sfx-malignShoot`, `@sfx-mediumCannon`, `@sfx-message`, `@sfx-mineDeploy`, `@sfx-minebeam`, `@sfx-missile`, `@sfx-missileLarge`, `@sfx-missileLaunch`, `@sfx-missileSmall`, `@sfx-missileTrail`, `@sfx-mud`, `@sfx-noammo`, `@sfx-pew`, `@sfx-place`, `@sfx-plantBreak`, `@sfx-plasmaboom`, `@sfx-plasmadrop`, `@sfx-press`, `@sfx-pulse`, `@sfx-pulseBlast`, `@sfx-railgun`, `@sfx-rain`, `@sfx-release`, `@sfx-respawn`, `@sfx-respawning`, `@sfx-rockBreak`, `@sfx-sap`, `@sfx-shield`, `@sfx-shockBlast`, `@sfx-shoot`, `@sfx-shootAlt`, `@sfx-shootAltLong`, `@sfx-shootBig`, `@sfx-shootSmite`, `@sfx-shootSnap`, `@sfx-shotgun`, `@sfx-smelter`, `@sfx-spark`, `@sfx-spellLoop`, `@sfx-splash`, `@sfx-spray`, `@sfx-steam`, `@sfx-techloop`, `@sfx-thruster`, `@sfx-titanExplosion`, `@sfx-torch`, `@sfx-tractorbeam`, `@sfx-unlock`, `@sfx-wave`, `@sfx-wind`, `@sfx-wind2`, `@sfx-wind3`, `@sfx-windhowl`.|`playsound true sound volume pitch 0 x y limit`|
|`playsound(:false, sound, volume, pitch, pan, limit)`<br/>`sound` - accepts `@sfx-artillery`, `@sfx-back`, `@sfx-bang`, `@sfx-beam`, `@sfx-bigshot`, `@sfx-bioLoop`, `@sfx-blaster`, `@sfx-bolt`, `@sfx-boom`, `@sfx-break`, `@sfx-build`, `@sfx-buttonClick`, `@sfx-cannon`, `@sfx-chatMessage`, `@sfx-click`, `@sfx-combustion`, `@sfx-conveyor`, `@sfx-corexplode`, `@sfx-cutter`, `@sfx-door`, `@sfx-drill`, `@sfx-drillCharge`, `@sfx-drillImpact`, `@sfx-dullExplosion`, `@sfx-electricHum`, `@sfx-explosion`, `@sfx-explosionbig`, `@sfx-extractLoop`, `@sfx-fire`, `@sfx-flame`, `@sfx-flame2`, `@sfx-flux`, `@sfx-glow`, `@sfx-grinding`, `@sfx-hum`, `@sfx-largeCannon`, `@sfx-largeExplosion`, `@sfx-laser`, `@sfx-laserbeam`, `@sfx-laserbig`, `@sfx-laserblast`, `@sfx-lasercharge`, `@sfx-lasercharge2`, `@sfx-lasershoot`, `@sfx-machine`, `@sfx-malignShoot`, `@sfx-mediumCannon`, `@sfx-message`, `@sfx-mineDeploy`, `@sfx-minebeam`, `@sfx-missile`, `@sfx-missileLarge`, `@sfx-missileLaunch`, `@sfx-missileSmall`, `@sfx-missileTrail`, `@sfx-mud`, `@sfx-noammo`, `@sfx-pew`, `@sfx-place`, `@sfx-plantBreak`, `@sfx-plasmaboom`, `@sfx-plasmadrop`, `@sfx-press`, `@sfx-pulse`, `@sfx-pulseBlast`, `@sfx-railgun`, `@sfx-rain`, `@sfx-release`, `@sfx-respawn`, `@sfx-respawning`, `@sfx-rockBreak`, `@sfx-sap`, `@sfx-shield`, `@sfx-shockBlast`, `@sfx-shoot`, `@sfx-shootAlt`, `@sfx-shootAltLong`, `@sfx-shootBig`, `@sfx-shootSmite`, `@sfx-shootSnap`, `@sfx-shotgun`, `@sfx-smelter`, `@sfx-spark`, `@sfx-spellLoop`, `@sfx-splash`, `@sfx-spray`, `@sfx-steam`, `@sfx-techloop`, `@sfx-thruster`, `@sfx-titanExplosion`, `@sfx-torch`, `@sfx-tractorbeam`, `@sfx-unlock`, `@sfx-wave`, `@sfx-wind`, `@sfx-wind2`, `@sfx-wind3`, `@sfx-windhowl`.|`playsound false sound volume pitch pan 0 0 limit`|

## Instruction `Set Marker`

Set a property for a marker. The ID used must be the same as in the Make Marker instruction. null values are ignored.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`setmarker(:remove, id)`|`setmarker remove id 0 0 0`|
|`setmarker(:world, id, boolean)`|`setmarker world id boolean 0 0`|
|`setmarker(:minimap, id, boolean)`|`setmarker minimap id boolean 0 0`|
|`setmarker(:autoscale, id, boolean)`|`setmarker autoscale id boolean 0 0`|
|`setmarker(:pos, id, x, y)`|`setmarker pos id x y 0`|
|`setmarker(:endPos, id, x, y)`|`setmarker endPos id x y 0`|
|`setmarker(:drawLayer, id, layer)`|`setmarker drawLayer id layer 0 0`|
|`setmarker(:color, id, color)`|`setmarker color id color 0 0`|
|`setmarker(:radius, id, radius)`|`setmarker radius id radius 0 0`|
|`setmarker(:stroke, id, stroke)`|`setmarker stroke id stroke 0 0`|
|`setmarker(:outline, id, outline)`|`setmarker outline id outline 0 0`|
|`setmarker(:rotation, id, rotation)`|`setmarker rotation id rotation 0 0`|
|`setmarker(:shape, id, sides, fill, outline)`|`setmarker shape id sides fill outline`|
|`setmarker(:arc, id, from, to)`|`setmarker arc id from to 0`|
|`setmarker(:flushText, id, fetch)`|`setmarker flushText id fetch 0 0`|
|`setmarker(:fontSize, id, size)`|`setmarker fontSize id size 0 0`|
|`setmarker(:textHeight, id, height)`|`setmarker textHeight id height 0 0`|
|`setmarker(:labelFlags, id, background, outline)`|`setmarker labelFlags id background outline 0`|
|`setmarker(:texture, id, printFlush, name)`|`setmarker texture id printFlush name 0`|
|`setmarker(:textureSize, id, width, height)`|`setmarker textureSize id width height 0`|
|`setmarker(:posi, id, index, x, y)`|`setmarker posi id index x y`|
|`setmarker(:uvi, id, index, x, y)`|`setmarker uvi id index x y`|
|`setmarker(:colori, id, index, color)`|`setmarker colori id index color 0`|

## Instruction `Make Marker`

Create a new logic marker in the world. An ID to identify this marker must be provided. Markers currently limited to 20,000 per world.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`makemarker(marker, id, x, y, replace)`<br/>`marker` - one of `:shapeText`, `:point`, `:shape`, `:text`, `:line`, `:texture`, `:quad`.|`makemarker marker id x y replace`|

## Instruction `Locale Print`

Add map locale property value to the text buffer. To set map locale bundles in map editor, check Map Info > Locale Bundles. If client is a mobile device, tries to print a property ending in ".mobile" first.

[Yruei's documentation](https://yrueii.github.io/MlogDocs/#bleeding-edge)

|Function&nbsp;call&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|Generated&nbsp;instruction&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
|-------------|---------------------|
|`localeprint(property)`|`localeprint property`|

---

[« Previous: Function reference for Mindustry Logic 8.0](FUNCTIONS-80.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: System Library »](SYSTEM-LIBRARY.markdown)
