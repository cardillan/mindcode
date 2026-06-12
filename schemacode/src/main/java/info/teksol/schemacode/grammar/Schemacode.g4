grammar Schemacode;

definitions
    : definition+ EOF
    ;

definition
    : schematic
    | stringValue
    ;

schematic : (name=Id Colon)? Schematic items=schematicItem+ End ;

schematicItem
    : attribute
    | block
    ;

attribute
    : Name Assign name=textDef                      # name
    | Description Assign description=textDef        # description
    | Dimensions Assign coordinates                 # dimensions
    | Tag Assign tag=textDef                        # schemaTag
    | Filename Assign filename=simpleStringLiteral  # filename
    | Target Assign version=versionNumber           # target
    ;

number : (Int | SignedInt);

versionNumber : ( Int | Version );

block
    : labels=labelList? type=Ref At pos=blockPosition dir=direction? cfg=configuration?
    ;

blockId : (Id | BlockId);

labelList
    : blockId (Comma blockId)* Colon
    ;

blockPosition
    : start=position                                                                # simplePosition
    | start=position Dot2 end=coordinates ( orientation=(Horizontal | Vertical) )?  # inclusiveRangePosition
    | start=position Dot3 end=coordinates ( orientation=(Horizontal | Vertical) )?  # exclusiveRangePosition
    | start=position Mul size=coordinates ( orientation=(Horizontal | Vertical) )?  # areaPosition
    ;

position
    : coordinates
    | relativeCoordinates
    | coordinatesRelativeTo
    ;

coordinates
    : LeftParen x=number Comma y=number RightParen
    ;

relativeCoordinates
    : op=( Plus | Minus ) coord=coordinates
    ;

coordinatesRelativeTo
    : label=Id relCoord=relativeCoordinates
    ;

direction
    : Facing dir=( North | South | East | West )
    ;

configuration
    : Virtual                       # virtual
    | Color colorDef                # color
    | Connected To connectionList   # connections
    | Block Ref                     # blocktype
    | Command Ref                   # unitcommand
    | Item Ref                      # item
    | Liquid Ref                    # liquid
    | Unit Ref                      # unit
    | Text text=textDef             # text
    | status=( Enabled | Disabled ) # boolean
    | def=processor                 # logic
    ;

colorDef
    : Rgba LeftParen red=number Comma green=number Comma blue=number Comma alpha=number RightParen
    ;

connectionList
    : connection (Comma connection)*
    ;

connection
    : coordinates                   # connAbs
    | relativeCoordinates           # connRel
    | Id                            # connName
    ;

processor
    : Processor links=processorLinks? ( Mindcode Assign mindcode=program | Mlog Assign mlog=program) ? End
    ;

processorLinks
    : Links linkDef* End
    ;

linkDef
    : linkPattern=(Mul | Pattern)                           # linkPattern
    | linkPos=connection ( As alias=Id virtual=Virtual? )?  # linkPos
    ;

program
    : programSnippet (Plus programSnippet)*
    ;

programSnippet
    : text=textDef           # programString
    | File file=textDef      # programFile
    ;

textDef
    : reference=stringLiteral       # textLiteral
    | name=Id                       # textId
    ;

stringValue
    : name=Id Assign string=stringLiteral
    ;

stringLiteral
    : TextLine                      # textLine
    | ( TextBlock1 | TextBlock2 )   # textBlock
    ;

simpleStringLiteral
    : TextLine                      # simpleTextLine
    ;

As              : 'as';
At              : 'at';
Block           : 'block';
Connected       : 'connected';
Command         : 'command';
Color           : 'color';
Description     : 'description';
Dimensions      : 'dimensions';
Disabled        : 'disabled';
Enabled         : 'enabled';
End             : 'end';
Facing          : 'facing';
File            : 'file';
Filename        : 'filename';
Horizontal      : 'horizontal';
Item            : 'item';
Links           : 'links';
Liquid          : 'liquid';
Logic           : 'logic';
Mindcode        : 'mindcode';
Mlog            : 'mlog';
Name            : 'name';
Processor       : 'processor';
Rgba            : 'rgba';
Schematic       : 'schematic';
Tag             : 'tag';
Target          : 'target';
Text            : 'text';
To              : 'to';
Unit            : 'unit';
Vertical        : 'vertical';
Virtual         : 'virtual';

Assign          : '=';
Colon           : ':';
Comma           : ',';
Dot             : '.';
Dot2            : '..';
Dot3            : '...';
Minus           : '-';
Mul             : '*';
Plus            : '+';

North           : 'north';
South           : 'south';
East            : 'east';
West            : 'west';

LeftParen       : '(';
RightParen      : ')';

TextBlock1      : '"""' [ \t]* [\r\n] .*? '"""';
TextBlock2      : '\'\'\'' [ \t]* [\r\n] .*? '\'\'\'';
TextLine        : '"' ( ~('\n'|'\r') )*? '"';

Int             : [0-9]+;
SignedInt       : ( '+' | '-' ) [0-9]+;
Id              : [_a-zA-Z] [-a-zA-Z_0-9]*;
BlockId         : [_a-zA-Z] [-a-zA-Z_0-9]* '#';
Ref             : '@' [_a-zA-Z] [-a-zA-Z_0-9]*;
Pattern         : [_a-zA-Z*] [-a-zA-Z_0-9*]*;
Version         : [0-9]+ '.' [0-9]+;

Comment         : '/*' .*? '*/' -> skip;
SLComment       : ('//' ~('\r' | '\n')* '\r'? '\n') -> skip;

Ws              : (' ' | '\t' | '\r' | '\n')+ -> skip;

// Must be at the very end
Any: . ;
