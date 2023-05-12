package info.teksol.schemacode.schema;

import info.teksol.schemacode.AbstractSchematicsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecompilerTest extends AbstractSchematicsTest {

    private String recompile(String definition, boolean relativePositions, boolean relativeConnections, boolean relativeLinks) {
        Schematics schematics = buildSchematics(definition);
        Decompiler decompiler = new Decompiler(schematics);
        decompiler.setRelativePositions(relativePositions);
        decompiler.setRelativeConnections(relativeConnections);
        decompiler.setRelativeLinks(relativeLinks);
        return decompiler.buildCode();
    }

    @Test
    public void decompilesAttributes() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''
                    tag = "label1"
                    tag = "label2"
                    tag = BLOCK-SWITCH

                    @switch              at ( 0,  0) facing east  enabled
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesAbsolutePositions() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @switch              at ( 0,  0) facing east  enabled
                    @switch              at ( 1,  0) facing east  enabled
                    @switch              at ( 2,  0) facing east  enabled
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesRelativePositions() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @switch              at ( 0,  0) facing east  enabled
                    @switch              at +(1, 0) facing east  enabled
                    @switch              at +(1, 0) facing east  enabled
                    @switch              at +(1, 0) facing east  enabled
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, true, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesAbsoluteConnections() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @power-node          at ( 0,  0) facing east  connected to ( 1,  0), ( 2,  0)
                    @power-node          at ( 1,  0) facing east  connected to ( 0,  0), ( 2,  0)
                    @power-node          at ( 2,  0) facing east  connected to ( 0,  0), ( 1,  0)
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesRelativeConnections() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @power-node          at ( 0,  0) facing east  connected to +(1, 0), +(2, 0)
                    @power-node          at ( 1,  0) facing east  connected to -(1, 0), +(1, 0)
                    @power-node          at ( 2,  0) facing east  connected to -(2, 0), -(1, 0)
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, true, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesAbsoluteLinks() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @micro-processor     at ( 0,  0) facing east  processor
                        links
                            ( 1,  1) as switch1 virtual
                            ( 2,  1) as switch2 virtual
                            ( 3,  1) as switch3 virtual
                        end
                        mlog = mlog-0
                    end
                end

                mlog-0 = '''
                    '''
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesRelativeLinks() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @micro-processor     at ( 0,  0) facing east  processor
                        links
                            +(1, 1) as switch1 virtual
                            +(2, 1) as switch2 virtual
                            +(3, 1) as switch3 virtual
                        end
                        mlog = mlog-0
                    end
                end

                mlog-0 = '''
                    '''
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, true);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesBlockNames() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                switch1:
                    @switch              at ( 0,  0) facing east  enabled
                switch2:
                    @switch              at ( 1,  0) facing east  enabled
                switch3:
                    @switch              at ( 2,  0) facing east  enabled
                switch4:
                    @switch              at ( 3,  0) facing east  enabled
                    @micro-processor     at ( 0,  1) facing east  processor
                        links
                            switch1 as switch1
                            switch2 as switch2
                            switch3 as switch3
                            switch4 as switch4
                        end
                        mlog = mlog-0
                    end
                end

                mlog-0 = '''
                    '''
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesTwoProcessorBlockNames() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                p0-switch1:
                    @switch              at ( 0,  0) facing east  enabled
                p0-switch2:
                    @switch              at ( 1,  0) facing east  enabled
                p1-switch1:
                    @switch              at ( 2,  0) facing east  enabled
                p1-switch2:
                    @switch              at ( 3,  0) facing east  enabled
                    @micro-processor     at ( 0,  1) facing east  processor
                        links
                            p0-switch1 as switch1
                            p0-switch2 as switch2
                        end
                        mlog = mlog-0
                    end
                    @micro-processor     at ( 0,  2) facing east  processor
                        links
                            p1-switch1 as switch1
                            p1-switch2 as switch2
                        end
                        mlog = mlog-1
                    end
                end

                mlog-0 = '''
                    '''
                
                mlog-1 = '''
                    '''
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesItemConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @unloader            at ( 0,  0) facing east  item @coal
                    @unloader            at ( 0,  1) facing east  item @pyratite
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesLiquidConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @liquid-source       at ( 0,  0) facing east  liquid @water
                    @liquid-source       at ( 0,  1) facing east  liquid @cryofluid
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesPositionConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @bridge-conveyor     at ( 0,  0) facing east  connected to ( 3,  0)
                    @bridge-conduit      at ( 0,  1) facing east  connected to ( 3,  1)
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesTextConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @message             at ( 0,  0) facing east  text "Single line"
                    @message             at ( 0,  1) facing east  text '''
                        Multiline 1
                        Multiline 2
                        '''
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }
}