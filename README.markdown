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

Please look into the `src/main/resources/samples` directory to examine multiple examples of Mindcode. They are currently
the best way for you to learn how to write Mindcode. If you programmed in any high-level language, you should feel right
at home.

At the moment, Mindcode is sensitive to newlines. For example, this will be rejected:

```
if a == 1 { print("a was 1") }
```

The "correct" way to write the above is to space everything out on multiple lines:

```
if a == 1 {
  print("a was 1")
}
```

This is due to my inexperience in writing ANTLR. This will certainly be fixed at some point.

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
* [ ] auto-allocated global variables
* [ ] functions / reusable procedures
* [ ] support multi-value return functions (`getBlock` comes to mind, but also Unit Locate)
* [ ] improved data types: 2d vector
* [ ] switch / case expression
* [ ] further optimize the generated Logic

Things I'd like to add to Mindcode-the-webapp:

* [ ] integrate a better code editor, rather than a plain old `<textarea>`
* [ ] ability to expose functions to the public
* [ ] import functions from fellow Mindcode users
    * this will probably be implemented as a preprocessor, instead of being directly integrated in Mindcode-the-language

# License

MIT. See LICENSE for the full text of the license.
