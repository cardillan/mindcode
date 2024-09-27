package info.teksol.schemacode.schematics;

import info.teksol.schemacode.AbstractSchematicsTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecompilerTest extends AbstractSchematicsTest {

    private String recompile(String definition, boolean relativePositions, boolean relativeConnections, boolean relativeLinks) {
        Schematic schematic = buildSchematics(definition);
        Decompiler decompiler = new Decompiler(schematic);
        decompiler.setRelativePositions(relativePositions);
        decompiler.setRelativeConnections(relativeConnections);
        decompiler.setRelativeLinks(relativeLinks);
        decompiler.setDirectionLevel(DirectionLevel.ROTATABLE);
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

                    @switch              at ( 0,  0) enabled
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

                    @switch              at ( 0,  0) enabled
                    @switch              at ( 1,  0) enabled
                    @switch              at ( 2,  0) enabled
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

                    @switch              at ( 0,  0) enabled
                    @switch              at +(1, 0) enabled
                    @switch              at +(1, 0) enabled
                    @switch              at +(1, 0) enabled
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, true, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesDirections() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''

                    @conveyor            at ( 0,  0) facing north
                    @conveyor            at +(1, 0) facing south
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

                    @power-node          at ( 0,  0) connected to ( 1,  0), ( 2,  0)
                    @power-node          at ( 1,  0) connected to ( 0,  0), ( 2,  0)
                    @power-node          at ( 2,  0) connected to ( 0,  0), ( 1,  0)
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

                    @power-node          at ( 0,  0) connected to +(1, 0), +(2, 0)
                    @power-node          at ( 1,  0) connected to -(1, 0), +(1, 0)
                    @power-node          at ( 2,  0) connected to -(2, 0), -(1, 0)
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

                    @micro-processor     at ( 0,  0) processor
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

                    @micro-processor     at ( 0,  0) processor
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
                    @switch              at ( 0,  0) enabled
                switch2:
                    @switch              at ( 1,  0) enabled
                switch3:
                    @switch              at ( 2,  0) enabled
                switch4:
                    @switch              at ( 3,  0) enabled
                    @micro-processor     at ( 0,  1) processor
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
                    @switch              at ( 0,  0) enabled
                p0-switch2:
                    @switch              at ( 1,  0) enabled
                p1-switch1:
                    @switch              at ( 2,  0) enabled
                p1-switch2:
                    @switch              at ( 3,  0) enabled
                    @micro-processor     at ( 0,  1) processor
                        links
                            p0-switch1 as switch1
                            p0-switch2 as switch2
                        end
                        mlog = mlog-0
                    end
                    @micro-processor     at ( 0,  2) processor
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

                    @unloader            at ( 0,  0) item @coal
                    @unloader            at ( 0,  1) item @pyratite
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

                    @liquid-source       at ( 0,  0) liquid @water
                    @liquid-source       at ( 0,  1) liquid @cryofluid
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

                    @bridge-conveyor     at ( 0,  0) connected to ( 3,  0)
                    @bridge-conduit      at ( 0,  1) connected to ( 3,  1)
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

                    @message             at ( 0,  0) text "Single line"
                    @message             at ( 0,  1) text '''
                        Multiline 1
                        Multiline 2
                        '''
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesUnitConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''
                
                    @air-factory         at ( 0,  0) facing west  unit @mono
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }

    @Test
    public void decompilesColorConfiguration() {
        String expected = """
                schematic
                    name = "Name"
                    description = '''
                        Description'''
                
                    @illuminator         at ( 0,  0) color rgba(255, 255, 0, 255)
                end
                """.replaceAll("'''", "\"\"\"");

        String actual = recompile(expected, false, false, false);
        assertEquals(expected, actual);
    }
}