lexer grammar MindcodeLexer;

@members {
    boolean newLines = true;
    boolean inFormat = false;
}

// Keywords
ALLOCATE                : 'allocate' ;
BEGIN                   : 'begin' ;
BREAK                   : 'break' ;
CASE                    : 'case' ;
CONST                   : 'const' ;
CONTINUE                : 'continue' ;
DEF                     : 'def' ;
DO                      : 'do' ;
ELIF                    : 'elif' ;
ELSE                    : 'else' ;
ELSEIF                  : 'elseif' ;
ELSIF                   : 'elsif' ;
END                     : 'end' ;
FALSE                   : 'false' ;
FOR                     : 'for' ;
HEAP                    : 'heap' ;
IF                      : 'if' ;
IN                      : 'in' ;
INLINE                  : 'inline' ;
LOOP                    : 'loop' ;
NOINLINE                : 'noinline' ;
NULL                    : 'null' ;
OUT                     : 'out' ;
PARAM                   : 'param' ;
RETURN                  : 'return' ;
STACK                   : 'stack' ;
THEN                    : 'then' ;
TRUE                    : 'true' ;
VAR                     : 'var' ;
VOID                    : 'void' ;
WHEN                    : 'when' ;
WHILE                   : 'while' ;

ASSIGN                  : '=' ;
AT                      : '@' ;
COMMA                   : ',' ;
DOT                     : '.' ;
DOUBLEDOT               : '..' ;
TRIPLEDOT               : '...' ;
DOUBLEQUOTE             : '"' ;
SEMICOLON               : ';' ;

// fragments
fragment BinDigit       : [01] ;
fragment HexDigit       : [0-9a-fA-F] ;
fragment DecDigit       : [0-9] ;
fragment DecExponent    : [eE][+-]? DecDigit+ ;

fragment Letter         : [a-zA-Z_] ;
fragment LetterOrDigit  : [a-zA-Z0-9_] ;
fragment LetterDigitDash: [-a-zA-Z0-9_] ;

// Identifiers

IDENTIFIER              : Letter LetterOrDigit* ;
BUILTINIDENTIFIER       : AT Letter
                        | AT Letter LetterDigitDash* LetterOrDigit ;

// Literals

STRING                  : '"' ~[\r\n"]* '"' ;
BINARY                  : '0b' BinDigit+ ;
HEXADECIMAL             : '0x' HexDigit+ ;
DECIMAL                 : DecDigit+ ;
FLOAT                   : DecDigit+ DecExponent
                        | DecDigit* DOT DecDigit+ DecExponent?
                        ;

// Directives
HASHSET                 : '#set' -> pushMode(InDirective) ;

// Formattable literals
FORMATTABLELITERAL      : {!inFormat}? '$"' {inFormat = true;}  -> pushMode(InFormattable) ;
RBRACE                  : {inFormat}?  '}'  {inFormat = false;} -> popMode ;

// Commented line comment, to distinguish from Enhanced comment.
COMMENTEDCOMMENT        : '////' ~[\r\n]* -> skip ;

// Enhanced comments
ENHANCEDCOMMENT         : {!inFormat}? '///' {inFormat = true; newLines=false;} -> pushMode(InComment);

// Whitespace + comments
COMMENT                 : '/*' .*? '*/'      -> skip;
EMPTYCOMMENT            : '//' [\r\n]        -> skip;
LINECOMMENT             : '//' ~'/' ~[\r\n]* -> skip;
NEWLINE                 : {newLines}? [\r\n] -> skip;
WHITESPACE              : [ \t]+             -> skip;

// MODES

mode InDirective;

// Identifiers/values can start with numbers and contain the dash in Directive mode
DIRECTIVEVALUE          : [-a-zA-Z0-9_]+ ;
DIRECTIVEASSIGN         : '=' ;
DIRECTIVECOMMA          : ',' ;

DIRECTIVESEMICOLON      : ';' -> type(SEMICOLON), popMode;

DIRECTIVECOMMENT        : '/*' .*? '*/' -> skip;
DIRECTIVELINECOMMENT    : '//' ~[\r\n]* -> skip;
DIRECTIVEWHITESPACE     : [ \t\r\n]+    -> skip;

mode InFormattable;

TEXT                    : ~[\r\n\\$"]+ ;

// We would want to allow escaping only '\' and '$', but can't get it to work. Escapes will be universal and handled later.
ESCAPESEQUENCE          : '\\' ~[\r\n"] ;
EMPTYPLACEHOLDER        : '${'   ' '*  '}' ;
INTERPOLATION           : '${' -> pushMode(DEFAULT_MODE) ;
VARIABLEPLACEHOLDER     : '$'  -> pushMode(InFmtIdentifier);
CLOSINGDOUBLEQUOTE      : '"' {inFormat = false;} -> type(DOUBLEQUOTE), popMode;
ENDOFLINE               : [\r\n] -> popMode;                    // Pop out of InFormattable on error

mode InComment;

// Map Enhanced comment lexer tokens to Formattable lexer tokens
// Refuse double quotes
COMMENTTEXT             : ~[\r\n\\$"]+      -> type(TEXT);

// We would want to allow escaping only '\' and '$', but can't get it to work. Escapes will be universal and handled later.
COMMENTESCAPESEQUENCE   : '\\' ~[\r\n"]     -> type(ESCAPESEQUENCE);

// We don't want empty placeholders in enhanced comments, but the check will be done later
COMMENTEMPTYPLACEHOLDER : '${'   ' '*  '}'  -> type(EMPTYPLACEHOLDER);
COMMENTINTERPOLATION    : '${'              -> type(INTERPOLATION), pushMode(DEFAULT_MODE);
COMMENTVARIABLEPHOLDER  : '$'               -> type(VARIABLEPLACEHOLDER), pushMode(InCommentIdentifier);
COMMENTENDOFLINE        : [\r\n] {inFormat=false; newLines=true;} -> type(SEMICOLON), popMode;

mode InFmtIdentifier;

VARIABLE                : Letter LetterOrDigit* ;
FMTENDOFLINE            : [\r\n] {inFormat = false;} -> popMode, popMode;      // Pop out of InFormattable on error
FMTCLOSINGDOUBLEQUOTE   : '"'    {inFormat = false;} -> type(DOUBLEQUOTE), popMode, popMode;
ENDOFIDENTIFIER         :  .  -> type(TEXT), popMode;

mode InCommentIdentifier;

// Map Enhanced comment lexer tokens to Formattable lexer tokens
INCMTVARIABLE           : Letter LetterOrDigit* -> type(VARIABLE);
INCMTENDOFLINE          : [\r\n] {inFormat=false; newLines=true;} -> popMode, popMode;
INCMTENDOFIDENTIFIER    : ~["] -> type(TEXT), popMode;    // Don't allow double quotes in enhanced comments at all
