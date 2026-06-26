parser grammar SchemacodeParser;

options {
    tokenVocab = 'SchemacodeLexer';
}

definitions
    : definition+ EOF
    ;

definition
    : schematic
    | stringValue
    ;

schematic : (name=ID COLON)? SCHEMATIC items=schematicItem+ END ;

schematicItem
    : attribute
    | region
    | block
    ;

attribute
    : NAME ASSIGN name=textDef                      # name
    | DESCRIPTION ASSIGN description=textDef        # description
    | DIMENSIONS ASSIGN coordinates                 # dimensions
    | TAG ASSIGN tag=textDef                        # schemaTag
    | FILENAME ASSIGN filename=simpleStringLiteral  # filename
    | TARGET ASSIGN version=versionNumber           # target
    | MINDCODE ASSIGN tag=textDef                   # mindcodePrologue
    | MLOG ASSIGN tag=textDef                       # mlogPrologue
    ;

number : (INT | SIGNEDINT);

versionNumber : ( INT | VERSION );

region
    : name=ID ASSIGN REGION blocks=block* END
    ;

block
    : labels=labelList? elementType=REF AT pos=blockPosition dir=direction? cfg=configuration?
    | labels=labelList? elementId=ID AT pos=blockPosition dir=direction? cfg=configuration?
    | labels=labelList? REGION blocks=block* END AT pos=blockPosition dir=direction? cfg=configuration?
    ;

blockId : (ID | BLOCKID);

labelList
    : blockId (COMMA blockId)* COLON
    ;

blockPosition
    : start=position                                                                # simplePosition
    | start=position DOT2 end=coordinates ( orientation=(HORIZONTAL | VERTICAL) )?  # inclusiveRangePosition
    | start=position DOT3 end=coordinates ( orientation=(HORIZONTAL | VERTICAL) )?  # exclusiveRangePosition
    | start=position MUL size=coordinates ( orientation=(HORIZONTAL | VERTICAL) )?  # areaPosition
    ;

position
    : coordinates
    | relativeCoordinates
    | coordinatesRelativeTo
    ;

coordinates
    : LEFTPAREN x=number COMMA y=number RIGHTPAREN
    ;

relativeCoordinates
    : op=( PLUS | MINUS ) coord=coordinates
    ;

coordinatesRelativeTo
    : label=ID relCoord=relativeCoordinates
    ;

direction
    : FACING dir=( NORTH | SOUTH | EAST | WEST )
    ;

configuration
    : VIRTUAL                       # virtual
    | COLOR colorDef                # color
    | CONNECTED TO connectionList   # connections
    | BLOCK REF                     # blocktype
    | COMMAND REF                   # unitcommand
    | ITEM REF                      # item
    | LIQUID REF                    # liquid
    | UNIT REF                      # unit
    | TEXT text=textDef             # text
    | status=( ENABLED | DISABLED ) # boolean
    | def=processor                 # logic
    ;

colorDef
    : RGBA LEFTPAREN red=number COMMA green=number COMMA blue=number COMMA alpha=number RIGHTPAREN
    ;

connectionList
    : connection (COMMA connection)*
    ;

connection
    : coordinates                   # connAbs
    | relativeCoordinates           # connRel
    | ID                            # connName
    ;

processor
    : PROCESSOR links=processorLinks? ( MINDCODE ASSIGN mindcode=program | MLOG ASSIGN mlog=program) ? parameters=parametrization? END
    ;

processorLinks
    : LINKS linkDef* END
    ;

linkDef
    : linkPattern=(MUL | PATTERN)                           # linkPattern
    | linkPos=connection ( AS alias=ID virtual=VIRTUAL? )?  # linkPos
    ;

program
    : programSnippet (PLUS programSnippet)*
    ;

programSnippet
    : text=textDef           # programString
    | FILE file=textDef      # programFile
    ;

textDef
    : reference=stringLiteral       # textLiteral
    | name=ID                       # textId
    ;

parametrization
    : PARAM parameter* PARAMEND
    ;

parameter
    : variable=PARAMTOKEN PARAMASSIGN strValue=PARAMSTRING
    | variable=PARAMTOKEN PARAMASSIGN value=PARAMTOKEN
    ;

stringValue
    : name=ID ASSIGN string=stringLiteral
    ;

stringLiteral
    : TEXTLINE                      # textLine
    | ( TEXTBLOCK1 | TEXTBLOCK2 )   # textBlock
    ;

simpleStringLiteral
    : TEXTLINE                      # simpleTextLine
    ;
