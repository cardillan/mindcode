# Mindcode

Welcome to Mindcode, a high-level language for [Mindustry](https://github.com/anuke/mindustry). Mindustry is a game in
the tower-defense genre. Mindustry added Logic in late 2020. Logic is a programming language, closer to assembly than a
high-level language. Mindcode aims to make programming easier for everyone.

## Using Mindcode

Either compile and run Mindcode yourself (see the [Development](#development) section), or visit
http://mindcode.herokuapp.com/

Write some Mindcode in the "Source Code" text area, then press the "Compile" button. The "Compiled Code" section will
contain the Logic version of your Mindcode. Copy the compiled version. Back in Mindustry, edit your processor, then use
the "Edit" button in the Logic UI. Select "Import from Clipboard". Mindustry is now ready to execute your code. Be on
the lookout for "invalid" nodes in the Logic, and if you can, please report them so that Mindcode can be improved for
everyone.

## Mindcode Syntax

Please read the [syntax](https://github.com/francois/mindcode/blob/main/SYNTAX.markdown) document for the high-level
information. The samples in the `src/main/resources/samples` directory are compiled on every test run and are thus
always up-to-date with the most-recent version of Mindcode.  If you programmed in any high-level language, you should
feel right at home.

[@schittli](https://github.com/schittli) kindly contributed a VS Code syntax highlighter.

![screenshot of Visual Studio Code, with Mindcode syntax highlighting](https://user-images.githubusercontent.com/8282673/112750180-43947a00-8fc7-11eb-8a22-83be7624753e.png)

Download the extension from the [Visual Studio marketplace](https://marketplace.visualstudio.com/items?itemName=TomSchi.mindcode).

## Development

Install Java 11+, Maven 3.6, and then run:

```sh
mvn clean compile exec:java
```

This will start the Mindcode UI on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

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
* [ ] warn developers when trying to pass in/return values that are non-numeric to/from functions
* [ ] skip comments when determining the return value of expressions (case/when, if/else, etc.)
* [ ] support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* [ ] improved data types: 2d vector
* [ ] add support for passing non-numerics into/out of functions

Things I'd like to add to Mindcode-the-webapp:

* [ ] integrate a better code editor, rather than a plain old `<textarea>`
* [ ] ability to expose functions to the public
* [ ] import functions from fellow Mindcode users
    * this will probably be implemented as a preprocessor, instead of being directly integrated in Mindcode-the-language

# License

MIT. See LICENSE for the full text of the license.
