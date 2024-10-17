# Contributing

There's currently a bunch of issues marked with the `help wanted` label. You can also have a look [here](https://github.com/cardillan/mindcode/discussions/167). 

## Guidelines

Mindcode uses JDK17, Maven and Spring Boot. 

The project consists of four modules:
* `mindcode-compiler` contains several independent parts:
  * Mindcode compiler, itself consisting of the grammar definition, parser, code generator and optimizer. Work is currently under way to make these parts better separated.
  * Processor emulator, capable of running mlog code in an emulated environment with limited support for Mindustry World interaction.
* `mindcode-schemacode`: contains the grammar definition, compiler and decompiler for Schemacode.
* `mindcode-toolapp`: contains the command-line interface.
* `mindcode-webapp`: contains the web application frontend.

Unit tests are used extensively to verify that source code gets compiled, optimized and executed as expected. 

Please do not make large changes before discussing them first. Especially do not alter existing syntax without prior agreement.

Changes to the Mindcode compiler are more complicated. The grammar, parser and code generator will be completely rewritten in near horizon. The optimizers, on the other hand, do not adhere to usual CS compiler standards, are not well documented and appear to be somewhat fragile. A comprehensive refactoring of the optimizers is not currently planned.         

### System library

Mindcode now provides a [system library](doc/syntax/SYNTAX.markdown), currently as an experimental feature. Contributions to the system library are welcome: if you have some general utility functions you are willing to share, incorporating them to the system library would help all Mindcode users.

## Running Mindcode locally

There are two options for getting Mindcode up and running on your own machine. Using Docker, or running it natively.

### Docker & Docker Compose

```
docker-compose up --build
```

It can take a few minutes to download and compile all the required parts the first time you run this, but subsequent runs will be a lot faster.

The Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

Note: The Docker is set up to allow running Mindcode in Docker alongside a local PostgreSQL installation.

### Native installation

1. Install Java 17+, Maven 3.6, and PostgreSQL
2. Create a database in PostgreSQL named `mindcode_development`

#### Windows

Set environment variables with the PostgreSQL connection parameters. You can set them by running the following commands in the console:

```
SET SPRING_DATASOURCE_URL=jdbc:postgresql://localhost/mindcode_development
SET SPRING_DATASOURCE_USERNAME=postgres_username
SET SPRING_DATASOURCE_PASSWORD=postgres_password
```

You also need to set a `JAVA_HOME` variable pointing to the directory containing your Java 17 installation, for example (the exact path depends on the distribution and version of Java you've installed):

```
SET JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot
```

(You can also set all these variables permanently in the _System Properties_ dialog, in the _Advanced_ tab, after pressing the _Environment Variables..._ button.)

Then, using the same console window, run:

```
mvnw.cmd install
```

to build the app, and then

```
bin\webapp.bat
```

The Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

#### Linux

Set environment variables with the PostgreSQL connection parameters:

```
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost/mindcode_development
export SPRING_DATASOURCE_USERNAME=postgres_username
export SPRING_DATASOURCE_PASSWORD=postgres_password
```

Then run:

```bash
./mvnw install
```

from the project root to build the app, and then

```bash
bin/run-local
```

The Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.

### IDE

To run the application from your IDE, set the environment variables as described above (some IDEs allow to set them just in the IDE) and set the startup class to `info.teksol.mindcode.webapp.WebappApplication`. When you run or debug the project, the Mindcode UI will now be running on localhost, port 8080. Visit http://localhost:8080/ to interact with it.
