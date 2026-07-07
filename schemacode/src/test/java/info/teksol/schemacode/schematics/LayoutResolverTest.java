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
        void buildHorizontalArray() {
            String original = """
                    schematic
                        name = "name"
                    
                        @micro-processor processor links * end end
                        message#: @message at (1, 0) * (2, 2) text "foo"
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message4 as message4
                                message3 as message3
                                message2 as message2
                                message1 as message1
                            end
                            mlog = mlog-0
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
                    
                    mlog-0 = ""\"
                        ""\"
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildVerticalArray() {
            String original = """
                    schematic
                        name = "name"
                    
                        @micro-processor processor links * end end
                        message#: @message at (1, 0) * (2, 2) vertical text "foo"
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message4 as message4
                                message3 as message3
                                message2 as message2
                                message1 as message1
                            end
                            mlog = mlog-0
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
                    
                    mlog-0 = ""\"
                        ""\"
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildArrayInclusiveRange() {
            String original = """
                    schematic
                        name = "name"
                        @memory-bank at (0, 0) .. (2, 3)
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
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
        void buildArrayExclusiveRange() {
            String original = """
                    schematic
                        name = "name"
                        @memory-bank at (0, 0) ... (4, 5)
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @memory-bank         at ( 0,  0)
                        @memory-bank         at ( 2,  0)
                        @memory-bank         at ( 0,  2)
                        @memory-bank         at ( 2,  2)
                    end
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class ArraysOfRegions {
        @Test
        void buildHorizontalArray() {
            String original = """
                    schematic
                        name = "name"
                    
                        @micro-processor processor links ** end end
                        region
                            message#: @message text "foo"
                        end at (1, 0) * (2, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message1 as message1
                                message2 as message2
                                message3 as message3
                                message4 as message4
                            end
                            mlog = mlog-0
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
                    
                    mlog-0 = ""\"
                        ""\"
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildVerticalArray() {
            String original = """
                    schematic
                        name = "name"
                    
                        @micro-processor processor links ** end end
                        region
                            message#: @message text "foo"
                        end at (1, 0) * (2, 2) vertical
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                message1 as message1
                                message2 as message2
                                message3 as message3
                                message4 as message4
                            end
                            mlog = mlog-0
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
                    
                    mlog-0 = ""\"
                        ""\"
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }

        @Test
        void buildArrayInArray() {
            String original = """
                    schematic
                        name = "name"
                        @micro-processor processor links ** end end
                        region
                            bank#: @memory-bank at (0, 0) * (2, 1)
                        end at (1, 0) * (1, 2)
                    end
                    """;

            String expected = """
                    schematic
                        name = "name"
                    
                        @micro-processor     at ( 0,  0) processor
                            links
                                bank1 as bank1
                                bank2 as bank2
                                bank3 as bank3
                                bank4 as bank4
                            end
                            mlog = mlog-0
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
                    
                    mlog-0 = ""\"
                        ""\"
                    """;

            String actual = recompile(original);
            assertEquals(expected, actual);
        }
    }

    @Nested
    class Regions {

    }

    @Nested
    class SimpleRotations {
    }

}
