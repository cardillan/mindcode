lexer grammar SchemacodeLexer;

AS              : 'as';
AT              : 'at';
BLOCK           : 'block';
CONNECTED       : 'connected';
COMMAND         : 'command';
COLOR           : 'color';
DESCRIPTION     : 'description';
DIMENSIONS      : 'dimensions';
DISABLED        : 'disabled';
ENABLED         : 'enabled';
END             : 'end';
FACING          : 'facing';
FILE            : 'file';
FILENAME        : 'filename';
HORIZONTAL      : 'horizontal';
ITEM            : 'item';
LINKS           : 'links';
LIQUID          : 'liquid';
LOGIC           : 'logic';
MINDCODE        : 'mindcode';
MLOG            : 'mlog';
NAME            : 'name';
PARAM           : 'param' -> pushMode(InParam);
PROCESSOR       : 'processor';
RGBA            : 'rgba';
SCHEMATIC       : 'schematic';
TAG             : 'tag';
TARGET          : 'target';
TEXT            : 'text';
TO              : 'to';
UNIT            : 'unit';
VERTICAL        : 'vertical';
VIRTUAL         : 'virtual';

ASSIGN          : '=';
COLON           : ':';
COMMA           : ',';
DOT             : '.';
DOT2            : '..';
DOT3            : '...';
MINUS           : '-';
MUL             : '*';
PLUS            : '+';

NORTH           : 'north';
SOUTH           : 'south';
EAST            : 'east';
WEST            : 'west';

LEFTPAREN       : '(';
RIGHTPAREN      : ')';

TEXTBLOCK1      : '"""' [ \t]* [\r\n] .*? '"""';
TEXTBLOCK2      : '\'\'\'' [ \t]* [\r\n] .*? '\'\'\'';
TEXTLINE        : '"' ( ~('\n'|'\r') )*? '"';

INT             : [0-9]+;
SIGNEDINT       : ( '+' | '-' ) [0-9]+;
ID              : [_a-zA-Z] [-a-zA-Z_0-9]*;
BLOCKID         : [_a-zA-Z] [-a-zA-Z_0-9]* '#';
REF             : '@' [_a-zA-Z] [-a-zA-Z_0-9]*;
PATTERN         : [_a-zA-Z*] [-a-zA-Z_0-9*]*;
VERSION         : [0-9]+ '.' [0-9]+;

COMMENT         : '/*' .*? '*/' -> skip;
SLCOMMENT       : ('//' ~('\r' | '\n')* '\r'? '\n') -> skip;

WS              : (' ' | '\t' | '\r' | '\n')+ -> skip;

// Must be at the very end
ANY: . ;

// MODES

mode InParam;

PARAMEND        : 'end' -> popMode;
PARAMASSIGN     : '=';

PARAMCOMMENT    : '/*' .*? '*/' -> skip;
PARAMSLCOMMENT  : ('//' ~('\r' | '\n')* '\r'? '\n') -> skip;
PARAMWHITESPACE : [ \t\n\r]+ -> skip;

PARAMSTRING     : '"' ~[\r\n"]* '"' ;

// Anything whitespace separated is a token
PARAMTOKEN      : ~[ \t\r\n]+ ;
