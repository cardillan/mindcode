package info.teksol.mindcode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AstPrinterTest extends AbstractAstTest {
    @Test
    void printsNicelyFormattedProgram() {
        assertEquals(
                "\nwhile false {\nwhile n < 0 {\nn = n + 1\n}\n\ndoit(2 * n, 4 ** p, r < c + l)\n\n}\n\n",
                AstPrinter.print(translateToAst("while false {\nwhile n < 0 {\nn += 1\n}\ndoit(2 * n, 4**p, r < c + l)\n}\n"))
        );
    }
}
