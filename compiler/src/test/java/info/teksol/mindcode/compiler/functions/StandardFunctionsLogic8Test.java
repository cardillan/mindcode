package info.teksol.mindcode.compiler.functions;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.logic.ProcessorVersion;
import info.teksol.util.ExpectedMessages;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.logic.Opcode.*;

public class StandardFunctionsLogic8Test extends AbstractGeneratorTest {

    @Override
    protected ProcessorVersion getProcessorVersion() {
        return ProcessorVersion.V8A;
    }

    @Test
    void recognizesNewProperties() {
        assertGeneratesMessages(ExpectedMessages.none(),
                """
                        a = getlink(0);
                        print(a.@currentAmmoType);
                        print(a.@armor);
                        print(a.@velocityX);
                        print(a.@velocityY);
                        print(a.@cameraX);
                        print(a.@cameraY);
                        print(a.@cameraWidth);
                        print(a.@cameraHeight);
                        """);
    }


    @Test
    void generatesNewDrawInstructions() {
        assertCompilesTo("""
                        drawPrint(10, 10, topRight);
                        translate(3, 4);
                        scale(-1, 1);
                        rotate(90);
                        reset();
                        """,
                createInstruction(DRAW, "print", "10", "10", "topRight"),
                createInstruction(DRAW, "translate", "3", "4"),
                createInstruction(DRAW, "scale", "-1", "1"),
                createInstruction(DRAW, "rotate", "0", "0", "90"),
                createInstruction(DRAW, "reset"),
                createInstruction(END)
        );
    }

    @Test
    void refusesWrongAlignment() {
        assertGeneratesMessages(
                ExpectedMessages.create().add("Invalid value 'fluffyBunny' for parameter 'align': allowed values are 'center', 'top', 'bottom', 'left', 'right', 'topLeft', 'topRight', 'bottomLeft', 'bottomRight'."),
                "drawPrint(10, 10, fluffyBunny);"
        );
    }

    @Test
    void generatesWeatherInstructions() {
        assertCompilesTo(
                ExpectedMessages.create()
                        .add("Built-in variable '@rain' not recognized.")
                        .add("Built-in variable '@fluffyBunny' not recognized.")
                ,
                """
                        active = weathersense(@snow);
                        weatherset(@rain, true);
                        weatherset(@fluffyBunny, false);
                        """,
                createInstruction(WEATHERSENSE, var(0), "@snow"),
                createInstruction(SET, "active", var(0)),
                createInstruction(WEATHERSET, "@rain", "true"),
                createInstruction(WEATHERSET, "@fluffyBunny", "false"),
                createInstruction(END)
        );
    }

    @Test
    void generatesPlaysound() {
        assertCompilesTo(
                ExpectedMessages.create()
                        .add("Built-in variable '@sfx-railgun' not recognized.")
                        .add("Built-in variable '@sfx-laser' not recognized.")
                ,
                """
                        playsound(true, @sfx-railgun, 1, 1, 100, 10, true);
                        playsound(false, @sfx-laser, 1, 1, 0.5, false);
                        """,
                createInstruction(PLAYSOUND, "true", "@sfx-railgun", "1", "1", "0", "100", "10", "true"),
                createInstruction(PLAYSOUND, "false", "@sfx-laser", "1", "1", "0.5", "0", "0", "false"),
                createInstruction(END)
        );
    }

    @Test
    void generatesSetMarker() {
        assertCompilesTo("""
                        setmarker(remove, id);
                        setmarker(world, id, boolean);
                        setmarker(minimap, id, boolean);
                        setmarker(autoscale, id, boolean);
                        setmarker(pos, id, x, y);
                        setmarker(endPos, id, x, y);
                        setmarker(drawLayer, id, layer);
                        setmarker(color, id, color);
                        setmarker(radius, id, radius);
                        setmarker(stroke, id, stroke);
                        setmarker(rotation, id, rotation);
                        setmarker(shape, id, sides, fill, outline);
                        setmarker(arc, id, from, to);
                        setmarker(flushText, id, fetch);
                        setmarker(fontSize, id, size);
                        setmarker(textHeight, id, height);
                        setmarker(labelFlags, id, background, outline);
                        setmarker(texture, id, printFlush, name);
                        setmarker(textureSize, id, width, height);
                        setmarker(posi, id, index, x, y);
                        setmarker(uvi, id, index, x, y);
                        setmarker(colori, id, index, color);
                        """,
                createInstruction(SETMARKER, "remove", "id"),
                createInstruction(SETMARKER, "world", "id", "boolean"),
                createInstruction(SETMARKER, "minimap", "id", "boolean"),
                createInstruction(SETMARKER, "autoscale", "id", "boolean"),
                createInstruction(SETMARKER, "pos", "id", "x", "y"),
                createInstruction(SETMARKER, "endPos", "id", "x", "y"),
                createInstruction(SETMARKER, "drawLayer", "id", "layer"),
                createInstruction(SETMARKER, "color", "id", "color"),
                createInstruction(SETMARKER, "radius", "id", "radius"),
                createInstruction(SETMARKER, "stroke", "id", "stroke"),
                createInstruction(SETMARKER, "rotation", "id", "rotation"),
                createInstruction(SETMARKER, "shape", "id", "sides", "fill", "outline"),
                createInstruction(SETMARKER, "arc", "id", "from", "to"),
                createInstruction(SETMARKER, "flushText", "id", "fetch"),
                createInstruction(SETMARKER, "fontSize", "id", "size"),
                createInstruction(SETMARKER, "textHeight", "id", "height"),
                createInstruction(SETMARKER, "labelFlags", "id", "background", "outline"),
                createInstruction(SETMARKER, "texture", "id", "printFlush", "name"),
                createInstruction(SETMARKER, "textureSize", "id", "width", "height"),
                createInstruction(SETMARKER, "posi", "id", "index", "x", "y"),
                createInstruction(SETMARKER, "uvi", "id", "index", "x", "y"),
                createInstruction(SETMARKER, "colori", "id", "index", "color"),
                createInstruction(END)
        );
    }

    @Test
    void generatesMakeMarker() {
        assertCompilesTo("""
                        makemarker(shapeText, id, x, y, replace);
                        """,
                createInstruction(MAKEMARKER, "shapeText", "id", "x", "y", "replace"),
                createInstruction(END)
        );
    }

    @Test
    void generatesLocaleprint() {
        assertCompilesTo("""
                        localeprint(property);
                        """,
                createInstruction(LOCALEPRINT, "property"),
                createInstruction(END)
        );
    }
}
