# Schemacode

Schemacode is a specialized definition language designed to describe structure of Mindustry Schematics. Schemacode 
definitions can be compiled into Mindustry schematic, either as a binary `.msch` file, or as a text.

While simple schematics can be easily created in Schemacode from scratch, a better method for creating more 
complicated designs is to build the schematic in Mindustry, export it to a `.msch` file or copy it to the clipboard 
as a text and obtain a valid Schemacode representation by decompiling. To decompile a `.msch` file, the 
[command line tool](TOOLS-CMDLINE.markdown) has to be used; decompiling a text representation obtained via clipboard is 
possible through the [web application](http://mindcode.herokuapp.com/decompiler).

Schemacode supports almost all existing features of Mindustry schematics. Specifically, all features employed by 
Serpulo technology are fully supported. Features specific to Erekir (such as canvas pictures) are unavailable.

Most importantly, logic processors can be fully configured using Schemacode. When specifying the code to be embedded 
in a given processor, it is possible to use either the native mlog language, or Mindcode. The source code (both mlog 
and Mindcode) can also be injected into the schematic from external file when building it using the command line tool.

It might be useful to have a look at existing Schemacode samples at http://mindcode.herokuapp.com/schematics before 
going on with this documentation.

# Whitespace and comments

All tokes in Schemacode are separated by whitespace. End of line characters have no special meaning (except in text 
blocks, where they're preserved). There's no character (such as `;`) separating commands in Schemacode.

Schemacode supports line comments using the `//` characters: everything after `//` is ignored.

# Schemacode file structure

On the topmost level, the Schemacode source may contain two kinds of definitions:

* String value definition
* Schematic definition

The Schemacode source must contain exactly one schematic definition, and zero or more string value definitions, in 
any order.

# String value definition

The string value definition has the following form:

```
id = "String literal"
```

or

```
id = """
    Text
    block
    """
```

or

```
id = '''
    Text
    block
    '''
```

No escape characters are recognized in either kind of string values. To encode line endings into string values, 
the text block version must be used. It is possible to use triple double quotes in string literals when they're 
defined as text blocks marked by triple single quotes (and vice versa). 

Text blocks indents are removed when interpreting the value of the literal. 

# Schematic definition

The schematic definition code has the following form:

```
schematic
    [definition]
    [definition]
    ...
end
```

where definition is either an attribute definition, or a block definition, in any order. By convention the attribute 
definitions should come first. 

# Attribute definition

Defines various attributes of the schematic. The syntax of attribute definition is:

```
attribute = value
```

The following attributes are recognized:

* `name`: specifies the name of the schematic. The value of the attribute is String (a string text value, a string 
  literal, or a text block). Can be specified at most once.
* `description`: specifies the description of the schematic. The value of the attribute is String (a string text 
  value, a string literal, or a text block). In case of text block, single newline characters are removed; an empty
  line must be used to define a line break. Can be specified at most once.
* `dimensions`: specifies the dimensions of the schematic, given as `(width, height)`, where `width` and `height` 
  are positive numbers. Can be specified at most once. When not specified, the dimensions are calculated from 
  schematic definition. Must not be smaller than calculated dimensions when specified. (In the future, specifying 
  dimensions different from calculated ones might serve some specific purpose.)  
* `tag`: assigns a tag to the schematic. The tag can be either a String value, or a predefined icon (see
  [Icons](SYNTAX-1-VARIABLES.markdown#built-in-icons)). `tag` attribute can be specified more than once; all 
  specified tags are attached to the schematic.  

# Block definition

Block definition specifies the type and configuration of a block placed at certain coordinates within the schematic. 
The syntax of block definition is:

```
[labels] <block-type> at <block-position> [facing <direction>] [configuration]
```

## Labels

A block can be assigned one or more labels. Labels are identifiers, separated by commas and followed by a colon, e.g.

```
cell1, cell2: @memory-cell at (3, 5)
```

creates a memory cell at coordinates (3, 5) and assigns labels `cell1` and `cell2` to it.

Labels are useful for creating references to labeled blocks.

## Block type

To specify a block type, Mindustry built-in block type must be specified, including the `@` sign at the beginning,
for example `@switch`, `@micro-processor` or `@battery-large`. Only built-in block types are supported at this 
moment, blocks added by mods cannot be used.

All supported block types are listed below.

<details><summary>Show full list of block in the Turret category.</summary>

* `@duo`
* `@scatter`
* `@scorch`
* `@hail`
* `@wave`
* `@lancer`
* `@arc`
* `@parallax`
* `@swarmer`
* `@salvo`
* `@segment`
* `@tsunami`
* `@fuse`
* `@ripple`
* `@cyclone`
* `@foreshadow`
* `@spectre`
* `@meltdown`
* `@breach`
* `@diffuse`
* `@sublimate`
* `@titan`
* `@disperse`
* `@afflict`
* `@lustre`
* `@scathe`
* `@smite`
* `@malign`

</details>

<details><summary>Show full list of block in the Production category.</summary>

* `@mechanical-drill`
* `@pneumatic-drill`
* `@laser-drill`
* `@blast-drill`
* `@water-extractor`
* `@cultivator`
* `@oil-extractor`
* `@vent-condenser`
* `@cliff-crusher`
* `@plasma-bore`
* `@large-plasma-bore`
* `@impact-drill`
* `@eruption-drill`

</details>

<details><summary>Show full list of block in the Distribution category.</summary>

* `@conveyor`
* `@titanium-conveyor`
* `@plastanium-conveyor`
* `@armored-conveyor`
* `@junction`
* `@bridge-conveyor`
* `@phase-conveyor`
* `@sorter`
* `@inverted-sorter`
* `@router`
* `@distributor`
* `@overflow-gate`
* `@underflow-gate`
* `@mass-driver`
* `@duct`
* `@armored-duct`
* `@duct-router`
* `@overflow-duct`
* `@underflow-duct`
* `@duct-bridge`
* `@duct-unloader`
* `@surge-conveyor`
* `@surge-router`
* `@unit-cargo-loader`
* `@unit-cargo-unload-point`
* `@item-source`
* `@item-void`

</details>

<details><summary>Show full list of block in the Liquids category.</summary>

* `@mechanical-pump`
* `@rotary-pump`
* `@impulse-pump`
* `@conduit`
* `@pulse-conduit`
* `@plated-conduit`
* `@liquid-router`
* `@liquid-container`
* `@liquid-tank`
* `@liquid-junction`
* `@bridge-conduit`
* `@phase-conduit`
* `@reinforced-pump`
* `@reinforced-conduit`
* `@reinforced-liquid-junction`
* `@reinforced-bridge-conduit`
* `@reinforced-liquid-router`
* `@reinforced-liquid-container`
* `@reinforced-liquid-tank`
* `@liquid-source`
* `@liquid-void`

</details>

<details><summary>Show full list of block in the Power category.</summary>

* `@power-node`
* `@power-node-large`
* `@surge-tower`
* `@diode`
* `@battery`
* `@battery-large`
* `@combustion-generator`
* `@thermal-generator`
* `@steam-generator`
* `@differential-generator`
* `@rtg-generator`
* `@solar-panel`
* `@solar-panel-large`
* `@thorium-reactor`
* `@impact-reactor`
* `@beam-node`
* `@beam-tower`
* `@beam-link`
* `@turbine-condenser`
* `@chemical-combustion-chamber`
* `@pyrolysis-generator`
* `@flux-reactor`
* `@neoplasia-reactor`
* `@power-source`
* `@power-void`

</details>

<details><summary>Show full list of block in the Defense category.</summary>

* `@copper-wall`
* `@copper-wall-large`
* `@titanium-wall`
* `@titanium-wall-large`
* `@plastanium-wall`
* `@plastanium-wall-large`
* `@thorium-wall`
* `@thorium-wall-large`
* `@phase-wall`
* `@phase-wall-large`
* `@surge-wall`
* `@surge-wall-large`
* `@door`
* `@door-large`
* `@scrap-wall`
* `@scrap-wall-large`
* `@scrap-wall-huge`
* `@scrap-wall-gigantic`
* `@thruster`
* `@beryllium-wall`
* `@beryllium-wall-large`
* `@tungsten-wall`
* `@tungsten-wall-large`
* `@blast-door`
* `@reinforced-surge-wall`
* `@reinforced-surge-wall-large`
* `@carbide-wall`
* `@carbide-wall-large`
* `@shielded-wall`

</details>

<details><summary>Show full list of block in the Crafting category.</summary>

* `@graphite-press`
* `@multi-press`
* `@silicon-smelter`
* `@silicon-crucible`
* `@kiln`
* `@plastanium-compressor`
* `@phase-weaver`
* `@surge-smelter`
* `@cryofluid-mixer`
* `@pyratite-mixer`
* `@blast-mixer`
* `@melter`
* `@separator`
* `@disassembler`
* `@spore-press`
* `@pulverizer`
* `@coal-centrifuge`
* `@incinerator`
* `@silicon-arc-furnace`
* `@electrolyzer`
* `@atmospheric-concentrator`
* `@oxidation-chamber`
* `@electric-heater`
* `@slag-heater`
* `@phase-heater`
* `@heat-redirector`
* `@heat-router`
* `@slag-incinerator`
* `@carbide-crucible`
* `@surge-crucible`
* `@cyanogen-synthesizer`
* `@phase-synthesizer`
* `@heat-source`

</details>

<details><summary>Show full list of block in the Units category.</summary>

* `@ground-factory`
* `@air-factory`
* `@naval-factory`
* `@additive-reconstructor`
* `@multiplicative-reconstructor`
* `@exponential-reconstructor`
* `@tetrative-reconstructor`
* `@repair-point`
* `@repair-turret`
* `@tank-fabricator`
* `@ship-fabricator`
* `@mech-fabricator`
* `@tank-refabricator`
* `@mech-refabricator`
* `@ship-refabricator`
* `@prime-refabricator`
* `@tank-assembler`
* `@ship-assembler`
* `@mech-assembler`
* `@basic-assembler-module`
* `@unit-repair-tower`
* `@payload-conveyor`
* `@payload-router`
* `@reinforced-payload-conveyor`
* `@reinforced-payload-router`
* `@payload-mass-driver`
* `@large-payload-mass-driver`
* `@small-deconstructor`
* `@deconstructor`
* `@constructor`
* `@large-constructor`
* `@payload-loader`
* `@payload-unloader`
* `@payload-source`
* `@payload-void`

</details>

<details><summary>Show full list of block in the Effect category.</summary>

* `@mender`
* `@mend-projector`
* `@overdrive-projector`
* `@overdrive-dome`
* `@force-projector`
* `@shock-mine`
* `@build-tower`
* `@regen-projector`
* `@shockwave-tower`
* `@shield-projector`
* `@large-shield-projector`
* `@core-shard`
* `@core-foundation`
* `@core-nucleus`
* `@core-bastion`
* `@core-citadel`
* `@core-acropolis`
* `@container`
* `@vault`
* `@unloader`
* `@reinforced-container`
* `@reinforced-vault`
* `@illuminator`
* `@launch-pad`
* `@interplanetary-accelerator`

</details>

<details><summary>Show full list of block in the Logic category.</summary>

* `@message`
* `@switch`
* `@micro-processor`
* `@logic-processor`
* `@hyper-processor`
* `@memory-cell`
* `@memory-bank`
* `@logic-display`
* `@large-logic-display`
* `@canvas`
* `@reinforced-message`
* `@world-processor`
* `@world-cell`
* `@world-message`

</details>

## Block position

Block position can be specified as relative or absolute. The first block defined by the schematic must use absolute 
position, but all subsequent blocks can use absolute or relative positions. Relative position always relates to the 
previous block, as defined by the schematic.

Block position can be specified using this syntax: 

```
[+-] (x, y)
```

The `+` or `-` sign, if used, specifies relative position, in which case the `x` and `y` coordinates are added to or 
subtracted from previous block position. When no plus or minus sign is used, the coordinates specify an absolute 
position for the block.

It is also possible to specify a position relative to another block using this syntax:

```
label {+-} (x, y)
```

In this case, the position is specified as an offset against the position of a block labeled as `label`.

All three ways of specifying block position can be seen in this example:

```
schematic
  name "Example"
message1:
  @message at (1, 0)                      // Places block at (1, 0)
switch1:
  @switch at +(1, 0)                      // Places block at (2, 0)              
  @micro-processor at switch1 - (2, 0)    // Places block at (0, 0)
end
```

Blocks in the schematic must not overlap. Overlapping blocks are detected and cause compilation error.

Blocks larger than 1x1 are placed into the schematic in such a way that their lower-left corner is at the given 
coordinates. This makes it quite natural to design schematic starting in the lower left corner, i.e. from coordinates
(0, 0), and building right and up (or up and right). 

Block position may also be negative (see [Origin and dimensions calculation](#origin-and-dimensions-calculation)).

Correctly positioning blocks, especially blocks larger than 1x1, can be a bit tricky. For more complex layouts, it is 
easier to create the schematic in Mindustry, decompile to Schemacode definition and modify the resulting file.   

## Block orientation

Each block in schematic has an orientation, although specific orientation affects only some types of blocks (such as 
conveyors or unit factories). Orientation can take four values - `east`, `west`, `north` or `south` - and is 
specified using this syntax:

```
facing <orientation>
```

e.g. `@conveyor at (2, 4) facing west`.

The cardinal directions are related to the coordinate system of the schematic, i.e. conveyor facing east is moving 
items from left to right.

## Block configuration

Some blocks are stored with specific configurations: connection for bridges or power nodes, item type for unloaders,
text for messages or links and code for processors. Schemacode supports the following types of configuration:

* [Block type](#block-type-configuration): block type selected in a constructor or a payload source (a sandbox-only 
  block).
* [Boolean](#boolean-configuration): on/off or open/close, for switches and doors.
* [Color](#color-configuration): color of the illuminator block.
* [Single connection](#connection-configuration): connection to another block (e.g. a bridge or a mass driver).
* [Multiple connections](#connection-configuration): connections to several different blocks (e.g. for power nodes).
* [Item](#item-configuration): item type selected in a sorter, unloader or similar block.
* [Liquid](#liquid-configuration): liquid type selected in a liquid source (a sandbox-only block).
* [Unit](#unit-configuration): unit type selected in a unit factory or payload source.
* [Unit command](#unit-command-configuration): unit command selected in a reconstructor.
* [Text](#text-configuration): text contents for message blocks.
* [Processor](#processor-configuration): links and code for logic processors.
* Virtual: a specific configuration marking blocks that aren't part of the schematic, but can be used as link 
  targets. The keyword is accepted by Schematics Builder, but has no meaning at the moment.  

## Block type configuration

Block type configuration is specified as `block` followed by block type name (including the `@` prefix):

```
    @payload-source at (1, 0) block @large-copper-wall
```

The following block types can have block type configuration specified:

* `@payload-source`
* `@constructor`
* `@large-constructor`

## Boolean configuration

Boolean configuration is specified as `enabled` or `disabled` for the values of true/false or opened/closed:

```
@door at (1,0) enabled   // Creates an opened door
```

The following block types can have boolean configuration specified:

* `@door`
* `@door-large`
* `@switch`

## Color configuration

Color configuration is specified as `color rgba(<red>, <green>, <blue>, <alpha>)`, where `<red>`, `<green>` and 
`<blue>` are the value if individual color components, while `<alpha>` is the value of the alpha channel. all these 
values must be given as a number between `0` and `255`, inclusive. Alpha specifies the opacity, `0` is not opaque at 
all (i.e. fully transparent), `255` is fully opaque. The exact handling of the alpha channel is done by the 
illuminator block, generally speaking higher values of alpha make the illuminator lightning more prominent.

This example specifies an illuminator block emitting intense green color: 

```
    @illuminator at (0, 0) color rgba(0, 255, 0, 255)
```

The following block types can have color configuration specified:

* `@illuminator`

## Connection configuration

Connection configuration is specified as `connected to` followed by a comma separated list of absolute or relative 
positions; relative positions are related to the block being configured. It is also possible to specify a block label:

```
    @bridge-conveyor     at (0, 0) connected to (0, 2)    // Connects to the bridge at (0, 2)
    @bridge-conveyor     at (0, 2) connected to bridge3   // Connects to the bridge at (0, 4)
bridge3:
    @bridge-conveyor     at (0, 4) connected to -(0, 1)   // Connects to the bridge at (0, 3)
```

Multiple connections are separated by commas, and it is possible to mix different ways to specify a position:

```
    @power-node at (5, 5) connected to (1, 1), -(2, 3), +(1, -1), node2, reactor4
```

The node is connected to blocks at positions (1, 1), (3, 2), (6, 4) and `node2` and `reactor4`, whatever their 
positions are.

When connecting to blocks larger than 1x1 by their position, it is sufficient to specify any position occupied by 
the block, it is not necessary to target the lower left corner.

Connection specified by position may lead to a place not occupied by any block, or even to a place outside the 
schematic. Such a connection - called a "virtual connection" - is allowed and will be compiled into the schematic. 
When the schematic is built in the Mindustry map and later a compatible block is added at the position of the 
virtual connection, the connection will be automatically made.    

The following block types can be connected to at most one block:

* `@bridge-conduit`
* `@bridge-conveyor`
* `@mass-driver`
* `@payload-mass-driver`
* `@large-payload-mass-driver`
* `@phase-conduit`
* `@phase-conveyor`

The following block types can be connected to several blocks (number of available connections depends on the block 
type):

* `@power-node`
* `@power-node-large`
* `@surge-tower`
* `@beam-link`
* `@power-source`

### Connecting bridges

Both normal and phase bridge connections must conform to the following criteria, otherwise a compilation error occurs:

* At most one connection is allowed.
* The connection must not lead to the same block (no connection to itself).
* The connections must lead to a block of the same type; it is not possible to connect e.g. a `@bridge-conveyor` to 
  a `@phase-conveyor`.
* No circular connections: if a block is connected to another block, the other block must not be connected to the 
  original block.
* The connection must be either vertical or horizontal; diagonal connections of any kind are disallowed.
* The connection distance must not exceed the bridge range.

Connections to empty positions are allowed and no warnings are generated. When the schematic is build in a Mindustry 
world and later a bridge of the same type is placed at the target position, the bridge is automatically connected.

### Connecting mass drivers

Mass driver connections must conform to the following criteria, otherwise a compilation error occurs:

* At most one connection is allowed.
* The connection must not lead to the same block (no connection to itself).
* The connections must lead to a block of the same type; it is not possible to connect e.g. a `@payload-mass-driver` to
  a `@large-payload-mass-driver`.
* The connection distance must not exceed the mass driver range.

Connections to empty positions are allowed and no warnings are generated.

### Connecting power nodes

Several different blocks in Mindustry represent power nodes: `@power-node`, `@power-node-large`, `@surge-tower`,
`@beam-link` and `@power-source`.

Power node connections must conform to the following criteria, otherwise a compilation error occurs:

* The connection must not lead to the same block (no connection to itself).
* The connection must connect to a block which produces or consumes power, or to another power node. (Note: a diode 
  isn't such a block, power node cannot connect to a diode.)
* The connection distance must not exceed the power node range. When linking two power nodes, larger of the two 
  power node ranges is used.
* Number of connections (including incoming connections from other power nodes) must not exceed the maximum number 
  of connections for given type of power node.

When the following situations are detected, a warning is produced and the connection is ignored:

* The connection leads to an empty location.
* The connection to the same block has already been defined.

Connecting two power nodes `N1` and `N2` is possible in any of these ways:

* declaring connection from `N1` to `N2` only, 
* declaring connection from `N2` to `N1` only, 
* declaring connection in both directions.

In all these cases both the `N1` to `N2` and `N2` to `N1` connections are written to the compiled schematic. 

## Item configuration

Item configuration is specified as `item` followed by item name (including the `@` prefix):

```
    @unloader at (1, 0) item @coal
```

The following block types can have item configuration specified:

* `@sorter`
* `@inverted-sorter`
* `@unloader`
* `@duct-router`
* `@duct-unloader`
* `@surge-router`
* `@unit-cargo-unload-point`
* `@item-source`

## Liquid configuration

Liquid configuration is specified as `liquid` followed by liquid name (including the `@` prefix):

```
    @liquid-source at (1, 0) liquid @cryofluid
```

The following block types can have liquid configuration specified:

* `@liquid-source`

## Unit configuration

Unit configuration is specified as `unit` followed by unit name (including the `@` prefix):

```
    @air-factory at (0, 0) unit @mono
```

Available unit types depend on the type of the factory:

* `air-factory`:      `@flare` or `@mono`
* `ground-factory`:   `@dagger`, `@crawler` or `@nova`
* `naval-factory`:    `@risso` or `@retusa`
* `mech-fabricator`:  `@merui`
* `ship-fabricator`:  `@elude`
* `tank-fabricator`:  `@stell`
* `payload-source`:   all unit types are allowed

## Unit command configuration

Unit command configuration is specified as `command` followed by the command name (including the `@` prefix):

```
    @multiplicative-reconstructor at (0, 0) command @repair
```

Possible unit commands are

* `move`
* `repair`
* `rebuild`
* `assist`
* `mine`
* `boost`

The following block types can have unit command configuration specified:

* `@additive-reconstructor`
* `@multiplicative-reconstructor`
* `@exponential-reconstructor`
* `@tetrative-reconstructor`

The applicability of a command depends on the type of unit processed by the reconstructor.

## Text configuration

Text configuration assigns a text content to blocks. It is specified as `text` followed by a string literal,
text block literal or string value identifier:

```
schematic
    dimensions (3, 1)
    @message at (0, 0) text "This is a message"
    @message at (1, 0) text """
        This is
        a multiline message"""
    @message at (2, 0) text message-text
end

message-text = """
        This is also a multiline message,
        one with [green]color[] and an additional newline at the end.
        """
```

There's no support to embed built-in icons into the messages at the moment.

The following block types can have text configuration specified:

* `@message`
* `@reinforced-message`
* `@world-message`

## Processor configuration

Processor configuration is the most complex one. It can specify both the code embedded to the processor and 
links to blocks in the schematic (and even outside the schematic) to the processor. The configuration is specified 
using the `processor` syntax:

```
    @micro-processor at (0, 0) processor
        links
            <link specifications>
        end
        
        mlog = <mlog code>
        mindcode = <mindcode>    
    end 
```

The following block types can have processor configuration specified:

* `@micro-processor`
* `@logic-processor`
* `@hyper-processor`
* `@world-processor`

### Processor links

There are several ways to specify blocks linked to the processor.

#### Linking by pattern

By specifying a link pattern, it is possible to link to the processor all blocks whose labels are matching the 
given pattern. The pattern is specified using "wildcard" convention: a string where the `*` character matches any 
part of a block label (the `?` matching a single character is **not** supported). The simplest use is this

```
    links
        *
    end
```

This definition matches all labelled blocks, which will be linked to the processor using their labels as link names.
This is the easiest way that can be used when every linked block is assigned the correct label:

```
schematic
    @micro-processor at (0, 0) processor
        links * end
        mlog = ""
    end
    switch1:  @switch  at +(1, 0)
    message1: @message at +(1, 0)
end
```

The  switch and message blocks are linked to the processor as `switch1` and `message1`, respectively. 

A more complicated way uses prefixes to group block labels. It allows to link blocks to more than one processor 
using pattern matching:

```
schematic
    @micro-processor at (0, 0) processor
        links p1-* end
        mlog = ""
    end
    p1-message1:            @message at +(1, 0)
    p1-switch1, p2-switch1: @switch  at +(1, 0)
    
    @micro-processor at (0, 1) processor
        links p2-* end
        mlog = ""
    end
    p2-message1:            @message at +(1, 0)
end
```

When the block label contains a dash character (`-`), only the part of the label after the first dash character is 
used as a link name. The above example therefore links the switch to both processors as `switch1`, while each 
message block is linked to separate processor as `message1`.

#### Linking by name

Named links allow to link blocks to the processor directly using the block labels. It is possible to specify a link 
name for the label; if it isn't specified, the label is used as a link name. Any prefix is stripped away again:

```
schematic
    @micro-processor at (0, 0) processor
        links 
            switch1                   // linked as "switch1"       
            a-message1                // linked as "message1", prefix stripped
            b-message1 as message2    // linked as "message2"
        end
        mlog = ""
    end
    switch1:      @switch  at +(1, 0)
    a-message1:   @message at +(1, 0)
    b-message1:   @message at +(1, 0)
end
```

#### Linking by position

Finally, it is possible to specify linked blocks by their positions. In this case, a name must be assigned explicitly 
(Schematics Builder doesn't generate link names automatically yet):

```
schematic
    @micro-processor at (0, 0) processor
        links 
             (1, 0) as switch1
            +(2, 0) as message4 virtual
            +(3, 0) as message1    
        end
        mlog = ""
    end
    @switch  at (1, 0)
    @message at (3, 0)
end
```

Relative coordinates evaluate against the processor block.

Pay attention to the `message4` block: its coordinates are (2, 0), which is inside the schematic, but there's no 
block at these coordinates. The link is created nevertheless, and when the schematic is built in Mindustry world, any 
block placed subsequently on the tile corresponding to the position (2, 0) in the schematic will be automatically 
linked to the processor. If it is a message, it will be linked as `message4`.

Virtual links can be placed outside the schematic as well. The keyword `virtual` marks such virtual links. It is 
optional now, but it will become compulsory for virtual blocks in the future.

All the ways to specify processor links can be mixed, for example:

```
schematic
    @micro-processor at (0, 0) processor
        links
            p-*
            message1
            (4, 0) as cell1
            (2, 0) as cell2 virtual    
        end
        mlog = ""
    end
    p-switch1:  @switch  at (1, 0)
    message1:   @message at (3, 0)
    @memory-cell at (4, 0)
end
```

#### Link name requirements

Link names must meet the following conditions:

* A link name must correspond to the last part of the block type name (e.g. `drill` for `@laser-drill`, `cell` for 
  `@memory-cell` and so on; if the last part is `large`, the next-to-last is used as in `node` for 
  `@power-node-large`), followed by a number.
* Link names must be unique, no two linked blocks can share a link name in a single processor.
* Each block can be linked at most once, it is not possible to link the same block twice under different names in a 
  single processor.

### Processor code

It is possible to specify either an mlog code, or a Mindcode for the processor. To specify an mlog code, use

```
mlog = <code>
```

To specify Mindcode, use

```
mindcode = <code>
```

At most one of `mlog` or `mindcode` can be specified. If neither of these options is specified, the processor created 
will not contain any instructions.

`<code>` is one or more code snippets joined together using a `+` (plus) operator. A code snippet can be specified in 
one of these ways:

* as a string literal,
* as a text block,
* as a string value identifier,
* as a reference to an external file.

A string literal is only useful for very small snippets of code:

```
schematic
    @micro-processor at (0, 0) processor
        links * end
        mindcode = "print(switch1.enabled); printflush(message1);"
    end
    switch1:  @switch  at (1, 0)
    message1: @message at (2, 0)
end
```

A text block allows to include line breaks in the code definition:

```
schematic
    @micro-processor at (0, 0) processor
        links * end
        mlog = """
            sensor result switch1 @enabled
            print result
            printflush message1
            """
    end
    switch1:  @switch  at (1, 0)
    message1: @message at (2, 0)
end
```

A string value identifier allows to move the code away from the processor definition for better organization:

```
schematic
    @micro-processor at (0, 0) processor
        links switch1 end
        mindcode = source-code
    end
    switch1: @switch at +(1, 0)
end

source-code = """
    on = switch1.enabled;
    // Starting at 1, we want to skip the switch
    for link in 1 ... @links do
        device = getlink(link);
        device.enabled = on;
    end;
    """
```

An external file can be defined like this:

```
schematic
    @micro-processor at (0, 0) processor
        links * end
        mindcode = file "../mindcode/regulator.mnd"
    end
    switch1:  @switch  at (1, 0)
    message1: @message at (2, 0)
end
```

The relative file path is evaluated from the directory containing the file being compiled; if the compiled code is 
read from standard input, it is evaluated from the current directory.

> [!IMPORTANT]
> Only command line tool allows you to use code from an external file. The web application cannot access your local 
> files by specified path, and the `file` option is therefore disabled there.

### Processor code

A program will typically consist of just one code snippet. Using multiple code snippets is primarily used to 
parametrize a common code shared between multiple processors, for example:

```
schematic
    @micro-processor at (0, 0) processor
        mindcode = "pos_x = 0; pos_y = 0; " + file "fractal.mnd"
    end
    @micro-processor at (0, 1) processor
        mindcode = "pos_x = 100; pos_y = 100; " + file "fractal.mnd"
    end
end
```

It is assumed that the code stored in `fractal.mnd` uses the `pos_x` and `pos_y` variables, specifically the values 
assigned to them in the preceding code snippet.

This feature may be especially useful for parametrizing Mindcode. Since the code for each processor is compiled 
independently, different values assigned to each processor may lead to different mlog code due to optimizations, 
specifically constant folding and constant propagation.   

# Origin and dimensions calculation

Schematics Builder automatically calculates schematic boundaries. If the lower-left corner of the compiled schematic
isn't positioned at (0, 0), all block and connection positions of the schematic are shifted to compensate for the 
non-zero origin. Note that the adjusted position (0, 0) can be still left empty:

```
schematic
  @switch at (2, 3)
  @message at (3, 2)
end
```

The positions in this schematic will be adjusted to (0, 1) for switch and (1, 0) for message block. 

Block positions can be negative as well. It is therefore possible to easily extend an existing schematic to the left 
or down without having to manually reposition all blocks.

Similarly, the dimensions of the schematic are calculated as the dimensions of the smallest rectangle containing all 
blocks of the schematic. These dimensions are written to the compiled schematic. If dimensions smaller than computed 
dimensions are specified as an attribute, a compilation error occurs.

---

[« Previous: System Library](SYSTEM-LIBRARY.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: IDE Integration »](TOOLS-IDE-INTEGRATION.markdown)
