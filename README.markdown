# Mindcode

Welcome to Mindcode, a high-level language for [Mindustry](https://github.com/anuke/mindustry). Mindustry is a game in
the tower-defense genre. Mindustry added Logic in late 2020. Logic is a programming language, closer to assembly than a
high-level language. Mindcode aims to make Mindustry programming easier for everyone.

The main focus of Mindcode design is two-fold:
* Keeping terminology and naming convention predictable and close to Mindustry Logic.
* Provide language constructs that are not prohibitively expensive given the astonishingly slow speeds
  and limited instruction space of Mindustry processors.

A recent addition to this project is [Schemacode](doc/syntax/SCHEMACODE.markdown), a schematic definition language, and 
Schematics Builder/Decompiler. Using these tools it is possible to convert existing schematics into a readable, 
easily updatable text representation which can be compiled back into Mindustry schematics after modifications. The 
flagship feature of the Schematics Builder is its ability to compile Mindcode and assign it to processors in the 
schematic on the fly, saving a lot of hassle with maintaining schematics based on Mindcode programs.     

A [changelog](CHANGELOG.markdown) is now maintained for releases.  

## Using Mindcode

Mindcode is available at http://mindcode.herokuapp.com/. Write some Mindcode in the _Mindcode Source Code_ text area,
then press the **Compile** button. The _Mindustry Logic_ text area will contain the Logic version of your Mindcode. 
Copy the compiled version. Back in Mindustry, edit your processor, then use the **Edit** button in the Logic UI. 
Select **Import from Clipboard**". Mindustry is now ready to execute your code. 

It is also possible to build Mindcode locally (see the [Development](#development) section), and use the
[command line tool](doc/syntax/TOOLS-CMDLINE.markdown) to compile your files, even copying the compiled code into 
the clipboard automatically if desired.

### Mindcode Syntax

Please read the [syntax](doc/syntax/SYNTAX.markdown) document for the high-level information.
The samples in the `src/main/resources/samples` directory are compiled on every test run and are thus
always up-to-date with the most-recent version of Mindcode. If you programmed in any high-level language, you should
feel right at home.

### VS Code syntax highlighting

[@schittli](https://github.com/schittli) kindly contributed a VS Code syntax highlighter.

![screenshot of Visual Studio Code, with Mindcode syntax highlighting](https://user-images.githubusercontent.com/8282673/112750180-43947a00-8fc7-11eb-8a22-83be7624753e.png)

Download the extension from the [Visual Studio marketplace](https://marketplace.visualstudio.com/items?itemName=TomSchi.mindcode).
I'm not sure how well the extension supports latest additions to Mindcode.

### IntelliJ IDEA syntax highlighting

IntelliJ IDEA (even the Community edition) can be easily configured for basic Mindcode syntax highlighting.

* Go to _File / Settings_, or press Ctrl-Alt-S
* Navigate to _Editor / File types_
* Create new file type
  * _Name_: Mindcode
  * _Description_: Mindcode source file
  * _Line comment_: `//` (leave _Only at line start_ unchecked)
  * _Block comment start/end_: leave empty
  * _Hex prefix_: `0x`
  * _Number postfixes_: leave empty
  * _Keywords_: paste Mindcode keywords to the first list. Optionally, paste Mindustry Logic object names to the second list. 
  * _Ignore case_: leave unchecked.
* Assign a file extension `*.mnd`

<details><summary>Show full list of Mindcode keywords.</summary>

```
allocate
and
break
case
const
continue
def
do
else
elsif
end
false
for
heap
if
in
inline
loop
not
null
or
return
sensor
stack
then
true
when
while
```

</details>

<details><summary>Show full list of Mindustry Logic object names.</summary>

```
@additive-reconstructor
@aegires
@afflict
@air
@air-factory
@alpha
@ammo
@ammoCapacity
@anthicus
@antumbra
@arc
@arkycite
@arkycite-floor
@arkyic-boulder
@arkyic-stone
@arkyic-vent
@arkyic-wall
@arkyid
@armored-conveyor
@armored-duct
@atmospheric-concentrator
@atrax
@avert
@barrier-projector
@basalt
@basalt-boulder
@basic-assembler-module
@battery
@battery-large
@beam-link
@beam-node
@beam-tower
@beryllic-boulder
@beryllic-stone
@beryllic-stone-wall
@beryllium
@beryllium-wall
@beryllium-wall-large
@beta
@blast-compound
@blast-door
@blast-drill
@blast-mixer
@bluemat
@boosting
@boulder
@breach
@bridge-conduit
@bridge-conveyor
@bryde
@build-tower
@canvas
@carbide
@carbide-crucible
@carbide-wall
@carbide-wall-large
@carbon-boulder
@carbon-stone
@carbon-vent
@carbon-wall
@char
@chemical-combustion-chamber
@cleroi
@cliff
@cliff-crusher
@coal
@coal-centrifuge
@collaris
@color
@combustion-generator
@command-center
@commanded
@conduit
@config
@configure
@conquer
@constructor
@container
@controlled
@controller
@conveyor
@copper
@copper-wall
@copper-wall-large
@core-acropolis
@core-bastion
@core-citadel
@core-foundation
@core-nucleus
@core-shard
@core-zone
@corvus
@counter
@crater-stone
@crawler
@cryofluid
@cryofluid-mixer
@crystal-blocks
@crystal-cluster
@crystal-floor
@crystal-orbs
@crystalline-boulder
@crystalline-stone
@crystalline-stone-wall
@crystalline-vent
@cultivator
@cyanogen
@cyanogen-synthesizer
@cyclone
@cyerce
@dacite
@dacite-boulder
@dacite-wall
@dagger
@dark-metal
@darksand
@darksand-tainted-water
@darksand-water
@dead
@deconstructor
@deep-tainted-water
@deep-water
@dense-red-stone
@differential-generator
@diffuse
@diode
@dirt
@dirt-wall
@disassembler
@disperse
@disrupt
@distributor
@door
@door-large
@dormant
@duct
@duct-bridge
@duct-router
@duct-unloader
@dune-wall
@duo
@eclipse
@efficiency
@electric-heater
@electrolyzer
@elude
@emanate
@empty
@enabled
@eruption-drill
@evoke
@exponential-reconstructor
@ferric-boulder
@ferric-craters
@ferric-stone
@ferric-stone-wall
@firstItem
@fissile
@flag
@flare
@flux-reactor
@force-projector
@foreshadow
@fortress
@fuse
@gamma
@graphite
@graphite-press
@graphitic-wall
@grass
@ground-factory
@hail
@health
@heat
@heat-reactor
@heat-redirector
@heat-router
@heat-source
@horizon
@hotrock
@hydrogen
@hyper-processor
@ice
@ice-snow
@ice-wall
@illuminator
@impact-drill
@impact-reactor
@impulse-pump
@incinerator
@incite
@interplanetary-accelerator
@inverted-sorter
@item-source
@item-void
@itemCapacity
@junction
@kiln
@lancer
@large-constructor
@large-logic-display
@large-payload-mass-driver
@large-plasma-bore
@large-shield-projector
@laser-drill
@launch-pad
@lead
@legacy-mech-pad
@legacy-unit-factory
@legacy-unit-factory-air
@legacy-unit-factory-ground
@liquid-container
@liquid-junction
@liquid-router
@liquid-source
@liquid-tank
@liquid-void
@liquidCapacity
@locus
@logic-display
@logic-processor
@lustre
@mace
@magmarock
@malign
@mass-driver
@maxHealth
@mech-assembler
@mech-fabricator
@mech-refabricator
@mechanical-drill
@mechanical-pump
@mega
@meltdown
@melter
@memory-bank
@memory-cell
@mend-projector
@mender
@merui
@message
@metaglass
@metal-floor
@metal-floor-damaged
@micro-processor
@mineX
@mineY
@mining
@minke
@minute
@molten-slag
@mono
@moss
@mud
@multi-press
@multiplicative-reconstructor
@name
@naval-factory
@navanax
@neoplasia-reactor
@neoplasm
@nitrogen
@nova
@obviate
@oct
@oil
@oil-extractor
@omura
@ore-crystal-thorium
@ore-wall-beryllium
@ore-wall-thorium
@ore-wall-tungsten
@overdrive-dome
@overdrive-projector
@overflow-duct
@overflow-gate
@oxidation-chamber
@oxide
@oxynoe
@ozone
@parallax
@payload-conveyor
@payload-loader
@payload-mass-driver
@payload-router
@payload-source
@payload-unloader
@payload-void
@payloadCount
@payloadType
@pebbles
@phase-conduit
@phase-conveyor
@phase-fabric
@phase-heater
@phase-synthesizer
@phase-wall
@phase-wall-large
@phase-weaver
@pine
@plasma-bore
@plastanium
@plastanium-compressor
@plastanium-conveyor
@plastanium-wall
@plastanium-wall-large
@plated-conduit
@pneumatic-drill
@poly
@pooled-cryofluid
@power-node
@power-node-large
@power-source
@power-void
@powerCapacity
@powerNetCapacity
@powerNetIn
@powerNetOut
@powerNetStored
@precept
@prime-refabricator
@progress
@pulsar
@pulse-conduit
@pulverizer
@pur-bush
@pyratite
@pyratite-mixer
@pyrolysis-generator
@quad
@quasar
@quell
@radar
@range
@red-diamond-wall
@red-ice
@red-ice-boulder
@red-ice-wall
@red-stone
@red-stone-boulder
@red-stone-vent
@red-stone-wall
@redmat
@redweed
@regen-projector
@regolith
@regolith-wall
@reign
@reinforced-bridge-conduit
@reinforced-conduit
@reinforced-container
@reinforced-liquid-container
@reinforced-liquid-junction
@reinforced-liquid-router
@reinforced-liquid-tank
@reinforced-message
@reinforced-payload-conveyor
@reinforced-payload-router
@reinforced-pump
@reinforced-surge-wall
@reinforced-surge-wall-large
@reinforced-vault
@repair-point
@repair-turret
@retusa
@rhyolite
@rhyolite-boulder
@rhyolite-crater
@rhyolite-vent
@rhyolite-wall
@ripple
@risso
@rotary-pump
@rotation
@rough-rhyolite
@router
@rtg-generator
@salt
@salt-wall
@salvo
@sand
@sand-boulder
@sand-floor
@sand-wall
@sand-water
@scathe
@scatter
@scepter
@scorch
@scrap
@scrap-wall
@scrap-wall-gigantic
@scrap-wall-huge
@scrap-wall-large
@second
@segment
@sei
@separator
@shale
@shale-boulder
@shale-wall
@shallow-water
@shield-projector
@shielded-wall
@ship-assembler
@ship-fabricator
@ship-refabricator
@shock-mine
@shockwave-tower
@shootX
@shootY
@shooting
@shrubs
@silicon
@silicon-arc-furnace
@silicon-crucible
@silicon-smelter
@size
@slag
@slag-centrifuge
@slag-heater
@slag-incinerator
@small-deconstructor
@smite
@snow
@snow-boulder
@snow-pine
@snow-wall
@solar-panel
@solar-panel-large
@sorter
@space
@spawn
@spectre
@speed
@spiroct
@spore
@spore-cluster
@spore-moss
@spore-pine
@spore-pod
@spore-press
@spore-wall
@steam-generator
@stell
@stone
@stone-wall
@sublimate
@surge
@surge-alloy
@surge-conveyor
@surge-crucible
@surge-router
@surge-smelter
@surge-tower
@surge-wall
@surge-wall-large
@swarmer
@switch
@tainted-water
@tank-assembler
@tank-fabricator
@tank-refabricator
@tar
@team
@tecta
@tendrils
@tetrative-reconstructor
@thermal-generator
@thorium
@thorium-reactor
@thorium-wall
@thorium-wall-large
@thruster
@tick
@time
@timescale
@titan
@titanium
@titanium-conveyor
@titanium-wall
@titanium-wall-large
@totalItems
@totalLiquids
@totalPower
@toxopid
@tsunami
@tungsten
@tungsten-wall
@tungsten-wall-large
@turbine-condenser
@type
@underflow-duct
@underflow-gate
@unit
@unit-cargo-loader
@unit-cargo-unload-point
@unit-repair-tower
@unloader
@vanquish
@vault
@vela
@vent-condenser
@vibrant-crystal-cluster
@water
@water-extractor
@wave
@white-tree
@white-tree-dead
@world-cell
@world-message
@world-processor
@x
@y
@yellow-stone
@yellow-stone-boulder
@yellow-stone-plates
@yellow-stone-vent
@yellow-stone-wall
@yellowcoral
@zenith
```

</details>

## Mindustry Logic References

If you don't know much about Mindustry Logic, you can read more information about them here:

* [Logic in 6.0](https://www.reddit.com/r/Mindustry/comments/ic9wrm/logic_in_60/) <small>Aug 2020</small>
* [How To Use Processors in 6.0](https://steamcommunity.com/sharedfiles/filedetails/?id=2268059244) <small>Nov 2020</small>
* [An Overly In-Depth Logic Guide](https://www.reddit.com/r/Mindustry/comments/kfea1e/an_overly_indepth_logic_guide/) <small>Dec 2020</small>

There also exists a [VSCode syntax highlighter for Mindustry Logic](https://marketplace.visualstudio.com/items?itemName=Antyos.vscode-mlog).

## Development

There are two options for getting Mindcode up and running on your own machine. Using Docker, or running it natively:

### With Docker & Docker Compose

```sh
docker-compose up --build
```
It can take a few minutes to download and compile all the required parts the first time you run this, but subsequent
runs will be a lot faster.

The Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

### Native installation

1. Install Java 17+, Maven 3.6, and PostgreSQL
2. Create a database in PostgreSQL named `mindcode_development`
3. Set environment variables with the PostgreSQL connection parameters:
```sh
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost/mindcode_development
export SPRING_DATASOURCE_USERNAME=postgres_username
export SPRING_DATASOURCE_PASSWORD=postgres_password
```

Then run:

```sh
bin/run-local
```

The Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

> **Note**: If you run PostgreSQL locally, you won't be able to run Mindcode in Docker due to port clash.

### Contributing 

The compiler was written in a Test-Driven Development fashion. If you can, please look in `src/main/test` and attempt to
emulate an existing test, so that we can prove that your proposal works as intended.

Tests in the `info.teksol.mindcode.processor` package are particularly useful. They compile a script and run it
on an emulated processor, comparing values produced by `print` instructions with expected ones. The processor emulator
cannot execute instructions interfacing with the Mindustry world - except Memory Bank and MemoryCell - but it can
process all kinds of Mindcode control elements. Implementing some more complicated algorithms using loops, 
conditional statements and/or function helps tremendously. (The emulated processor runs much faster than Mindcode
processors, so even more complicated algorithms are feasible to run.)

## Roadmap

Or perhaps a wish-list, can be found [here](ROADMAP.markdown).  

# License

MIT. See LICENSE for the full text of the license.
