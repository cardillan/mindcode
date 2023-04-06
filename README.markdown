# Mindcode

Welcome to Mindcode, a high-level language for [Mindustry](https://github.com/anuke/mindustry). Mindustry is a game in
the tower-defense genre. Mindustry added Logic in late 2020. Logic is a programming language, closer to assembly than a
high-level language. Mindcode aims to make programming easier for everyone.

Recently a lot of enhancements were made to Mindcode, see this [description of March 2023 changes](UPDATE-2023-03.markdown).

## Using Mindcode

Either compile and run Mindcode yourself (see the [Development](#development) section), or visit
http://mindcode.herokuapp.com/

Write some Mindcode in the "Source Code" text area, then press the "Compile" button. The "Compiled Code" section will
contain the Logic version of your Mindcode. Copy the compiled version. Back in Mindustry, edit your processor, then use
the "Edit" button in the Logic UI. Select "Import from Clipboard". Mindustry is now ready to execute your code. Be on
the lookout for "invalid" nodes in the Logic, and if you can, please report them so that Mindcode can be improved for
everyone.

## Mindcode Syntax

Please read the [syntax](SYNTAX.markdown) document for the high-level
information. The samples in the `src/main/resources/samples` directory are compiled on every test run and are thus
always up-to-date with the most-recent version of Mindcode.  If you programmed in any high-level language, you should
feel right at home.

[@schittli](https://github.com/schittli) kindly contributed a VS Code syntax highlighter.

![screenshot of Visual Studio Code, with Mindcode syntax highlighting](https://user-images.githubusercontent.com/8282673/112750180-43947a00-8fc7-11eb-8a22-83be7624753e.png)

Download the extension from the [Visual Studio marketplace](https://marketplace.visualstudio.com/items?itemName=TomSchi.mindcode).

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

1. Install Java 11+, Maven 3.6, and PostgreSQL
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

The compiler was written in a Test-Driven Development fashion. If you can, please look in `src/main/test` and attempt to
emulate an existing test, so that we can prove that your proposal works as intended.

### Roadmap

Things I'd like to add to Mindcode-the-language:

* [x] loops
    * [x] while
    * [x] for
* [x] expressions!
    * `move(center_x + radius * sin(@tick), center_y + radius * cos(@tick))` moves a unit in a circle around a specific point
* [x] if expressions (ternary operator)
* [x] auto-allocated global variables
* [x] switch / case expression
* [x] add support for drawing primitives
* [x] add support for `uradar`
* [x] add support for `ulocate`
* [x] add support for `end`
* [x] functions / reusable procedures
* [x] further optimize the generated Logic
* [x] optimize getlink / set
* [x] optimize sensor / set
* [x] skip comments when determining the return value of expressions (case/when, if/else, etc.)
* [x] #16 indirect sensor access. We can't do `resource = @silicon ; CONTAINER.resource`. This tries to call `sensor
  result CONTAINER @resource`, which doesn't make any sense.
* [x] #17 `break` and `continue`, to better control iteration
* [x] #19 inline functions, meaning functions that are inlined at the call-site
* [x] add support for passing non-numerics into/out of non-recursive functions
* [x] display compiler output in the webapp
* [x] evaluation of constant expressions at compile time
* [x] constant declaration: constants would be evaluated at compile time and wouldn't use a variable
* [ ] compiler directives (`#set`) to parametrize code compilation and optimization
* [ ] optimizer strength setting - per optimizer (off, on/normal, aggressive)
* [ ] compatibility warnings:
  * warn when `configurce` main variable is used in V7 -- ML changes it to `config`
  * warn about alloy-smelter --> surge-smelter V6 --> V7 name change
* [ ] memory jump table for case expressions where possible
* [ ] `break` and `continue` in `case` expressions: `break` to exit the expression, `continue` to skip to the next `when` branch
* [ ] varargs inline functions: the vararg can be processed using list iteration loop, or passed to another vararg function (not so sure about this)
* [ ] `in` boolean operator: tests number is in range
* [ ] short-circuit boolean evaluation in some form
  * Ruby-like (`and`, `or` for short-circuit, `&&`, `||` for full evaluation), or
  * "best-effort" basis (no guarantees either way)
* [ ] improve compiler error messages
* [ ] code generation/optimization objective (speed for faster but larger code, size for smaller but slower code) - most of the time smaller is also faster in Mindustry Logic, but there might be a few opportunities for this distinction.

Compiled code analysis

* [ ] warn developers when the generated code goes over 1000 Mindustry instructions
* [ ] warn developers when potentially non-numeric value is being pushed to stack
* [ ] warn developers when variable is read before being written to
* [ ] determine effective variable types to assist with optimizations

Optimization improvements 

* [ ] multiple optimization passes
* [ ] additional automatic inlining of nonrecursive functions 
* [ ] multiple-use temporary variables optimization
* [ ] eliminate retval variables/assignments where not needed
* [ ] elimination of useless statements, e.g. `op add x x 0`, `op mul x x 1` or `set x x`
* [ ] parameters that are only passed to inner calls and never modified won't be stored on stack
* [ ] boolean expressions
  * ternary operator assignment: instead of conditional jump/set/always jump/set, do set/conditional jump/set.
  * and/or boolean improvements: conditional jump on individual operands instead of evaluating the and/or and doing conditional jump on that (overlaps with short-circuit boolean eval)
  * and/or boolean improvements: encode `a and not b` as `a > b`, `a or not b` as `a >= b`.

More advanced optimizations 

* [ ] common subexpressions optimizations (maybe including repeated array access)
* [ ] loop unrolling / other loop optimizations
* [ ] jump threading / crossjumping
* [ ] forward store for external variables
* [ ] tail recursion

Additional tooling

* [ ] schematics-creation tool
  * tool for creating schematics files from layout description
  * creating layouts including code for processor(s) from Mindcode sources in one go

Ideas on hold

* [ ] support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* [ ] #17 `if` operator: `break if some_cond` is equivalent to `if some_cond break end`. It's just a less verbose way of doing it.
* [ ] improved data types: 2d vector
* [ ] integrate a better code editor in the webapp, rather than a plain old `<textarea>`

# License

MIT. See LICENSE for the full text of the license.
