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
FILL            : 'fill';
GLOBAL          : 'global';
HORIZONTAL      : 'horizontal';
ITEM            : 'item';
LINKS           : 'links';
LIQUID          : 'liquid';
LOCAL           : 'local';
LOGIC           : 'logic';
MINDCODE        : 'mindcode';
MLOG            : 'mlog';
NAME            : 'name';
PARAM           : 'param' -> pushMode(InParam);
PARENT          : 'parent';
PROCESSOR       : 'processor';
RANDOM          : 'random';
REPLACE         : 'replace';
REGION          : 'region';
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

fragment Id     : [_a-zA-Z] [-a-zA-Z_0-9]*;

INT             : [0-9]+;
SIGNEDINT       : ( '+' | '-' ) [0-9]+;
ID              : Id;
ID_ARRAY        : Id '#';
TYPE            : '@' Id;
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
