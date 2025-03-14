# IDE Integration

Thanks to the command-line compiler, it is possible to use an integrated development environment (IDE) for writing and compiling Mindcode. This documents has some tips on setting up the shop.

## Setting up the command-line compiler

The [command line tool](TOOLS-CMDLINE.markdown) allows you to compile your files, including the option to copy the compiled code into the clipboard automatically, or even to send it to Mindustry through the Mlog Watcher mod. The command line compiler can be set up in the following way:

1. Download and install [Eclipse Temurin version 22](https://adoptium.net/temurin/releases/?version=22).
2. Download `mindcode.jar` from the [releases page](https://github.com/cardillan/mindcode/releases) and place it in a directory on your computer.
3. To run the command line compiler, use `java.exe -jar mindcode.jar <arguments>`. Provide full paths to the `java.exe` file from the Eclipse Temurin installation created in the first step, and to the `mindcode.jar` file downloaded in the second step. `<arguments>` are the command line arguments passed to the Mindcode compiler.

For example, the following command compiles `program.mnd` into `program.mlog`, copies the resulting mlog code into the clipboard and attempts to send it directly to Mindustry (the [Mlog Watcher](TOOLS-MLOG-WATCHER.markdown) mod must be installed for that):

```
java -jar mindcode.jar cm program.mnd program.mlog -c -w
```

For easier integration with IDEs, create a script file/batch file containing this command, using full paths to both `java.exe` and `mindcode.jar`, and put this file onto executable path.   

## IntelliJ IDEA

Intellij IDEA can be downloaded here: https://www.jetbrains.com/idea/download/. The Community edition is free to use.

After installing and running IDEA, you can import the settings file containing Mindcode file types and external tool configurations.

- File types: [settings.zip](/support/idea/settings-filetypes.zip)
- External tools (Windows): [settings.zip](/support/idea/settings-tools-windows.zip)
- External tools (Linux): [settings.zip](/support/idea/settings-tools-linux.zip) (The settings for Linux were adapted from Windows environment, but weren't tested - they may need some tweaking to work.)

To import these settings, choose _File/Manage IDE settings/Import settings..._ and select the appropriate settings file.

The file types settings contain Mindcode and Schemacode file type definitions configured for syntax highlighting of Mindcode and Mindustry Logic keywords. The file containing these settings is automatically kept up-to-date with each release of Mindcode. When a new version adds or removes keywords, importing the settings again will update your IDE to the latest language definition.    

The external tools settings contain two external tools: `Mindcode compiler` and `Schemacode compiler`. You may want to assign a shortcut key to them in _Settings/Keymap/External tools_, and to  review the command line arguments configured for either tool in _Settings/Tools/External tools_.

Example of syntax highlighting with the given settings:

![image](https://github.com/user-attachments/assets/d0969248-7357-4e89-8f51-8fdc04a8427c)

Unfortunately the syntax highlighting doesn't correctly recognize some literals, but otherwise is reasonably good.

## Visual Studio Code

[@schittli](https://github.com/schittli) kindly contributed a VS Code syntax highlighter.

![screenshot of Visual Studio Code, with Mindcode syntax highlighting](https://user-images.githubusercontent.com/8282673/112750180-43947a00-8fc7-11eb-8a22-83be7624753e.png)

Download the extension from the [Visual Studio marketplace](https://marketplace.visualstudio.com/items?itemName=TomSchi.mindcode).

Unfortunately, recent additions to Mindcode syntax aren't supported by this extension.

[« Previous: Schemacode](SCHEMACODE.markdown) &nbsp; | &nbsp; [Up: Contents](SYNTAX.markdown) &nbsp; | &nbsp; [Next: Command line tool »](TOOLS-CMDLINE.markdown)
