package info.teksol.schemacode.schematics;

import info.teksol.schemacode.AbstractSchematicsTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// To make sure the schematic is built correctly, the original source code is compiled and then decompiled
// This converts the original into a standardized representation that can be compared with the expected output
class LayoutResolverTest extends AbstractSchematicsTest {

    private String recompile(String definition) {
        Schematic schematic = buildSchematics(definition);
        Decompiler decompiler = new Decompiler(schematic);
        decompiler.setRelativePositions(false);
        decompiler.setRelativeConnections(false);
        decompiler.setRelativeLinks(false);
        decompiler.setDirectionLevel(DirectionLevel.ROTATABLE);
        return decompiler.buildCode();
    }

    @Nested
    class Arrays {
        @Test
        void buildsHorizontalArray() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor processor links * end end
                        message#: @message at (1, 0) * (2, 2) text "foo"
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message4 as message4
                                message3 as message3
                                message2 as message2
                                message1 as message1
                            end
                        end
                    message1:
                        @message             at ( 1,  0) text "foo"
                    message2:
                        @message             at ( 2,  0) text "foo"
                    message3:
                        @message             at ( 1,  1) text "foo"
                    message4:
                        @message             at ( 2,  1) text "foo"
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsVerticalArray() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor processor links * end end
                        message#: @message at (1, 0) * (2, 2) vertical text "foo"
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message4 as message4
                                message3 as message3
                                message2 as message2
                                message1 as message1
                            end
                        end
                    message1:
                        @message             at ( 1,  0) text "foo"
                    message2:
                        @message             at ( 1,  1) text "foo"
                    message3:
                        @message             at ( 2,  0) text "foo"
                    message4:
                        @message             at ( 2,  1) text "foo"
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsArrayInclusiveRange() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @memory-bank at (0, 0) .. (2, 3)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @memory-bank         at ( 0,  0)
                        @memory-bank         at ( 2,  0)
                        @memory-bank         at ( 0,  2)
                        @memory-bank         at ( 2,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsArrayExclusiveRange() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @memory-bank at (0, 0) ... (4, 5)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @memory-bank         at ( 0,  0)
                        @memory-bank         at ( 2,  0)
                        @memory-bank         at ( 0,  2)
                        @memory-bank         at ( 2,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsArrayInclusiveRangeBackwards() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @micro-processor processor links * end end
                        bank#: @memory-bank at (3, 2) .. (1, 0)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                bank2 as bank2
                                bank3 as bank3
                                bank1 as bank1
                                bank4 as bank4
                            end
                        end
                    bank1:
                        @memory-bank         at ( 3,  2)
                    bank2:
                        @memory-bank         at ( 1,  2)
                    bank3:
                        @memory-bank         at ( 3,  0)
                    bank4:
                        @memory-bank         at ( 1,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsArrayExclusiveRangeBackwards() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @micro-processor processor links * end end
                        bank#: @memory-bank at (3, 2) ... (-1, -1) vertical
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                bank2 as bank2
                                bank3 as bank3
                                bank1 as bank1
                                bank4 as bank4
                            end
                        end
                    bank1:
                        @memory-bank         at ( 3,  2)
                    bank2:
                        @memory-bank         at ( 3,  0)
                    bank3:
                        @memory-bank         at ( 1,  2)
                    bank4:
                        @memory-bank         at ( 1,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Regions {
        @Test
        void buildsHorizontalArray() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor processor links ** end end
                        region
                            message#: @message text "foo"
                        end at (1, 0) * (2, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message1 as message1
                                message2 as message2
                                message3 as message3
                                message4 as message4
                            end
                        end
                    message1:
                        @message             at ( 1,  0) text "foo"
                    message2:
                        @message             at ( 2,  0) text "foo"
                    message3:
                        @message             at ( 1,  1) text "foo"
                    message4:
                        @message             at ( 2,  1) text "foo"
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsVerticalArray() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor processor links ** end end
                        region
                            message#: @message text "foo"
                        end at (1, 0) * (2, 2) vertical
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message1 as message1
                                message2 as message2
                                message3 as message3
                                message4 as message4
                            end
                        end
                    message1:
                        @message             at ( 1,  0) text "foo"
                    message2:
                        @message             at ( 1,  1) text "foo"
                    message3:
                        @message             at ( 2,  0) text "foo"
                    message4:
                        @message             at ( 2,  1) text "foo"
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsArrayInArray() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @micro-processor processor links ** end end
                        region
                            bank#: @memory-bank at (0, 0) * (2, 1)
                        end at (1, 0) * (1, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                bank1 as bank1
                                bank2 as bank2
                                bank3 as bank3
                                bank4 as bank4
                            end
                        end
                    bank1:
                        @memory-bank         at ( 1,  0)
                    bank2:
                        @memory-bank         at ( 3,  0)
                    bank3:
                        @memory-bank         at ( 1,  2)
                    bank4:
                        @memory-bank         at ( 3,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsIrregularRegions() {
            String original = """
                    schematic
                        name = "Dev Test"
                        region(1, 3)
                            @micro-processor processor links * end end
                            cell1: @memory-cell at (1, 1)
                            switch1: @switch at (2, 2)
                        end at (0, 0) * (2, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                p0-switch1 as switch1
                                p0-cell1 as cell1
                            end
                        end
                    p0-cell1:
                        @memory-cell         at ( 1,  1)
                    p0-switch1:
                        @switch              at ( 2,  2) disabled
                        @micro-processor     at ( 1,  0) processor
                            links
                                p1-switch1 as switch1
                                p1-cell1 as cell1
                            end
                        end
                    p1-cell1:
                        @memory-cell         at ( 2,  1)
                    p1-switch1:
                        @switch              at ( 3,  2) disabled
                        @micro-processor     at ( 0,  3) processor
                            links
                                p2-switch1 as switch1
                                p2-cell1 as cell1
                            end
                        end
                    p2-cell1:
                        @memory-cell         at ( 1,  4)
                    p2-switch1:
                        @switch              at ( 2,  5) disabled
                        @micro-processor     at ( 1,  3) processor
                            links
                                p3-switch1 as switch1
                                p3-cell1 as cell1
                            end
                        end
                    p3-cell1:
                        @memory-cell         at ( 2,  4)
                    p3-switch1:
                        @switch              at ( 3,  5) disabled
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsNestedRegions() {
            String original = """
                    schematic
                        name = "Dev Test"
                        @copper-wall
                        region
                            region
                                @copper-wall at (1, 1)
                            end at (1, 1)
                        end at (1, 1)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall         at ( 0,  0)
                        @copper-wall         at ( 3,  3)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Rotations {

        @Test
        void buildsRotatedRegions() {
            String original = """
                    schematic
                        name = "Dev Test"
                        A = region
                            @copper-wall at (0, 0)
                            @phase-wall at (0, 1)
                            @scrap-wall at (1, 0)
                        end
                        A at (0, 0)
                        A at (2, 0) facing north
                        A at (2, 2) facing west
                        A at (0, 2) facing south
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall         at ( 0,  0)
                        @phase-wall          at ( 0,  1)
                        @scrap-wall          at ( 1,  0)
                        @copper-wall         at ( 3,  0)
                        @phase-wall          at ( 2,  0)
                        @scrap-wall          at ( 3,  1)
                        @copper-wall         at ( 3,  3)
                        @phase-wall          at ( 3,  2)
                        @scrap-wall          at ( 2,  3)
                        @copper-wall         at ( 0,  3)
                        @phase-wall          at ( 1,  3)
                        @scrap-wall          at ( 0,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsRotatedRegionsLarge() {
            String original = """
                    schematic
                        name = "Dev Test"
                        A = region
                            @copper-wall-large at (0, 0)
                            @phase-wall-large at (0, 2)
                            @scrap-wall-large at (2, 0)
                        end
                    
                        A at (0, 0)
                        A at (4, 0) facing north
                        A at (4, 4) facing west
                        A at (0, 4) facing south
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall-large   at ( 0,  0)
                        @phase-wall-large    at ( 0,  2)
                        @scrap-wall-large    at ( 2,  0)
                        @copper-wall-large   at ( 6,  0)
                        @phase-wall-large    at ( 4,  0)
                        @scrap-wall-large    at ( 6,  2)
                        @copper-wall-large   at ( 6,  6)
                        @phase-wall-large    at ( 6,  4)
                        @scrap-wall-large    at ( 4,  6)
                        @copper-wall-large   at ( 0,  6)
                        @phase-wall-large    at ( 2,  6)
                        @scrap-wall-large    at ( 0,  4)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsDoubleRotatedRegionsLargeNorthSouth() {
            String original = """
                    schematic
                        name = "Dev Test"
                        A = region
                            @copper-wall-large at (0, 0)
                            @phase-wall-large at (0, 2)
                            @scrap-wall-large at (2, 0)
                        end
                    
                        B = region
                            A at (0, 0)
                            A at (5, 0) facing north
                            A at (10, 0) facing west
                            A at (15, 0) facing south
                        end
                    
                        B facing north
                        B at (5, 0) facing south
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall-large   at ( 2,  0)
                        @phase-wall-large    at ( 0,  0)
                        @scrap-wall-large    at ( 2,  2)
                        @copper-wall-large   at ( 2,  7)
                        @phase-wall-large    at ( 2,  5)
                        @scrap-wall-large    at ( 0,  7)
                        @copper-wall-large   at ( 0, 12)
                        @phase-wall-large    at ( 2, 12)
                        @scrap-wall-large    at ( 0, 10)
                        @copper-wall-large   at ( 0, 15)
                        @phase-wall-large    at ( 0, 17)
                        @scrap-wall-large    at ( 2, 15)
                        @copper-wall-large   at ( 5, 17)
                        @phase-wall-large    at ( 7, 17)
                        @scrap-wall-large    at ( 5, 15)
                        @copper-wall-large   at ( 5, 10)
                        @phase-wall-large    at ( 5, 12)
                        @scrap-wall-large    at ( 7, 10)
                        @copper-wall-large   at ( 7,  5)
                        @phase-wall-large    at ( 5,  5)
                        @scrap-wall-large    at ( 7,  7)
                        @copper-wall-large   at ( 7,  2)
                        @phase-wall-large    at ( 7,  0)
                        @scrap-wall-large    at ( 5,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsDoubleRotatedRegionsLargeEastWest() {
            String original = """
                    schematic
                        name = "Dev Test"
                        A = region
                            @copper-wall-large at (0, 0)
                            @phase-wall-large at (0, 2)
                            @scrap-wall-large at (2, 0)
                        end
                    
                        B = region
                            A at (0, 0)
                            A at (5, 0) facing north
                            A at (10, 0) facing west
                            A at (15, 0) facing south
                        end
                    
                        B facing east
                        B at (0, 5) facing west
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall-large   at ( 0,  0)
                        @phase-wall-large    at ( 0,  2)
                        @scrap-wall-large    at ( 2,  0)
                        @copper-wall-large   at ( 7,  0)
                        @phase-wall-large    at ( 5,  0)
                        @scrap-wall-large    at ( 7,  2)
                        @copper-wall-large   at (12,  2)
                        @phase-wall-large    at (12,  0)
                        @scrap-wall-large    at (10,  2)
                        @copper-wall-large   at (15,  2)
                        @phase-wall-large    at (17,  2)
                        @scrap-wall-large    at (15,  0)
                        @copper-wall-large   at (17,  7)
                        @phase-wall-large    at (17,  5)
                        @scrap-wall-large    at (15,  7)
                        @copper-wall-large   at (10,  7)
                        @phase-wall-large    at (12,  7)
                        @scrap-wall-large    at (10,  5)
                        @copper-wall-large   at ( 5,  5)
                        @phase-wall-large    at ( 5,  7)
                        @scrap-wall-large    at ( 7,  5)
                        @copper-wall-large   at ( 2,  5)
                        @phase-wall-large    at ( 0,  5)
                        @scrap-wall-large    at ( 2,  7)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsMultipleRotations() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        region
                            region
                                region
                                    @conveyor at (1, 0)
                                end at (1, 0) facing north
                            end at (1, 0) facing north
                        end at (1, 0) facing north
                        @copper-wall at (0, 0)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @conveyor            at ( 1,  1) facing south
                        @copper-wall         at ( 0,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Transformations {

        @Test
        void buildsHorizontalFlip() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        region
                            @copper-wall at (0, 0)
                            @phase-wall at (0, 1)
                            @scrap-wall-large at (1, 0)
                        end flip horizontal
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall         at ( 2,  0)
                        @phase-wall          at ( 2,  1)
                        @scrap-wall-large    at ( 0,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsVerticalFlip() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        region
                            @copper-wall at (0, 0)
                            @phase-wall at (1, 0)
                            @scrap-wall-large at (0, 1)
                        end flip vertical
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall         at ( 0,  2)
                        @phase-wall          at ( 1,  2)
                        @scrap-wall-large    at ( 0,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsBothFlips() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        region
                            region
                                @copper-wall at (0, 0)
                                @phase-wall at (1, 0)
                                @scrap-wall-large at (0, 1)
                            end flip vertical
                        end flip horizontal
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @copper-wall         at ( 1,  2)
                        @phase-wall          at ( 0,  2)
                        @scrap-wall-large    at ( 0,  0)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Placements {

        @Test
        void buildsReplacement() {
            String original = """
                    schematic
                        name = "Dev Test"
                        w$: @water-extractor at (0, 0) * (3, 3)
                        @power-node-large replace w5 connected to w*
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @water-extractor     at ( 0,  0)
                        @water-extractor     at ( 2,  0)
                        @water-extractor     at ( 4,  0)
                        @water-extractor     at ( 0,  2)
                        @water-extractor     at ( 4,  2)
                        @water-extractor     at ( 0,  4)
                        @water-extractor     at ( 2,  4)
                        @water-extractor     at ( 4,  4)
                        @power-node-large    at ( 2,  2) connected to ( 0,  0), ( 0,  2), ( 0,  4), ( 2,  0), ( 2,  4), ( 4,  0), ( 4,  2), ( 4,  4)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsFillings() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @titanium-wall
                        @copper-wall fill (0, 0) * (2, 2)
                        @phase-wall fill (0, 0) * (2, 3)
                        @scrap-wall fill (0, 0) * (3, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @titanium-wall       at ( 0,  0)
                        @copper-wall         at ( 1,  0)
                        @copper-wall         at ( 0,  1)
                        @copper-wall         at ( 1,  1)
                        @phase-wall          at ( 0,  2)
                        @phase-wall          at ( 1,  2)
                        @scrap-wall          at ( 2,  0)
                        @scrap-wall          at ( 2,  1)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsAirReplacement() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @titanium-wall at (0, 0) * (3, 3)
                        @air replace (1, 1) * (2, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @titanium-wall       at ( 0,  0)
                        @titanium-wall       at ( 1,  0)
                        @titanium-wall       at ( 2,  0)
                        @titanium-wall       at ( 0,  1)
                        @titanium-wall       at ( 0,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildsOverlapReplaceAndFill() {
            String original = """
                    schematic
                        name = "Dev Test"
                    
                        @surge-wall-large at (0, 0) * (3, 3)
                        @copper-wall replace (1, 1) * (2, 2)
                        @titanium-wall fill (0, 0) * (6, 6)
                    end
                    """;

            String expected = """
                    schematic
                        name = "Dev Test"
                    
                        @surge-wall-large    at ( 4,  0)
                        @surge-wall-large    at ( 4,  2)
                        @surge-wall-large    at ( 0,  4)
                        @surge-wall-large    at ( 2,  4)
                        @surge-wall-large    at ( 4,  4)
                        @copper-wall         at ( 1,  1)
                        @copper-wall         at ( 2,  1)
                        @copper-wall         at ( 1,  2)
                        @copper-wall         at ( 2,  2)
                        @titanium-wall       at ( 0,  0)
                        @titanium-wall       at ( 1,  0)
                        @titanium-wall       at ( 2,  0)
                        @titanium-wall       at ( 3,  0)
                        @titanium-wall       at ( 0,  1)
                        @titanium-wall       at ( 3,  1)
                        @titanium-wall       at ( 0,  2)
                        @titanium-wall       at ( 3,  2)
                        @titanium-wall       at ( 0,  3)
                        @titanium-wall       at ( 1,  3)
                        @titanium-wall       at ( 2,  3)
                        @titanium-wall       at ( 3,  3)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }
}
