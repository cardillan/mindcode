// This file contains the lexer grammar for Mindcode language.
//
// The lexer grammar is straightfoward except these features:
//
// The DOC_COMMENT definition contains the code documentation (inspired by JavaDoc and similar mechanisms, except
// the contents of the comments should be in Markdown). These comments are output onto the HIDDEN channel.
// The AST tree builder accesses these comments on the hidden channel and embeds them into the relevant AST nodes
// (constants, parameters, function declarations).
//
// The are several lexer modes:
// * InMlogBlock: a mode for parsing mlog blocks. An mlog block contains mlog instructions in syntax derived
//   from the Mindustry one. An mlog block is enclosed in braces; the mode ends when `}` is encountered.
// * InDirective: a mode for parsing the contents of the #set  and #setlocal directives. These directives allow using
//   various forms of option and value names (e.g. #set target = 7.1;). These names can't be matched by a normal
//    Mindcode identifier, hence the separate mode. The mode ends when a semicolon is encountered.
// * InFormattable: mode used for parsing the contents of the formattable string literal. The mode ends when
//   encountering the closing double quote. Default mode is entered when encountering an interpolated expresson
//   (${expression}). InFmtIdentifier mode is entered when encountering embedded variable ($variable).
//   InFormattable mode is not reentrant - second invocation is ignored.
// * InComment: analogous to InFormattable, but used for enhanced comments. Differences:
//   * the mode is exited upon encountering a newline,
//   * InCommentIdentifier mode is used to parse embedded variables instead of InFmtIdentifier mode.
// * InFmtIdentifier, InCommentIdentifier: for parsing embedded variables within formattable strings/comments.

lexer grammar MindcodeLexer;

@members {
    boolean newLines = true;
    boolean inFormat = false;
    boolean inMlogBlock = false;
}

// Keywords
ALLOCATE                : 'allocate' ;
ATOMIC                  : 'atomic' ;
BEGIN                   : 'begin' ;
BREAK                   : 'break' ;
CACHED                  : 'cached' ;
CASE                    : 'case' ;
CONST                   : 'const' ;
CONTINUE                : 'continue' ;
DEBUG                   : 'debug' ;
DESCENDING              : 'descending' ;
DEF                     : 'def' ;
DO                      : 'do' ;
ELIF                    : 'elif' ;
ELSE                    : 'else' ;
ELSEIF                  : 'elseif' ;
ELSIF                   : 'elsif' ;
END                     : 'end' ;
EXPORT                  : 'export' ;
EXTERNAL                : 'external' ;
FALSE                   : 'false' ;
GUARDED                 : 'guarded' ;
FOR                     : 'for' ;
HEAP                    : 'heap' ;
IF                      : 'if' ;
IN                      : 'in' ;
INLINE                  : 'inline' ;
LINKED                  : 'linked' ;
MODULE                  : 'module' ;
MLOG                    : 'mlog' ;
NOINIT                  : 'noinit' ;
NOINLINE                : 'noinline' ;
NULL                    : 'null' ;
OUT                     : 'out' ;
PARAM                   : 'param' ;
PRIVATE                 : 'private' ;
PUBLIC                  : 'public' ;
REF                     : 'ref' ;
REMOTE                  : 'remote' ;
REQUIRE                 : 'require' ;
RETURN                  : 'return' ;
STACK                   : 'stack' ;
THEN                    : 'then' ;
TRUE                    : 'true' ;
VAR                     : 'var' ;
VOID                    : 'void' ;
VOLATILE                : 'volatile' ;
WHEN                    : 'when' ;
WHILE                   : 'while' ;

// Relational operators
EQUAL                   : '==' ;
GREATER_THAN            : '>' ;
GREATER_THAN_EQUAL      : '>=' ;
LESS_THAN               : '<' ;
LESS_THAN_EQUAL         : '<=' ;
NOT_EQUAL               : '!=' ;
STRICT_EQUAL            : '===' ;
STRICT_NOT_EQUAL        : '!==' ;

// Bitwise, boolean and logical operators
BITWISE_AND             : '&' ;
BITWISE_NOT             : '~' ;
BITWISE_OR              : '|' ;
BITWISE_XOR             : '^' ;
BOOLEAN_AND             : '&&' ;
BOOLEAN_NOT             : '!' ;
BOOLEAN_OR              : '||' ;
LOGICAL_AND             : 'and' ;
LOGICAL_NOT             : 'not' ;
LOGICAL_OR              : 'or';
SHIFT_LEFT              : '<<' ;
SHIFT_RIGHT             : '>>' ;
USHIFT_RIGHT            : '>>>' ;

// Opening/closing symbols
// Braces - '{', '}' - aren't defined here
// They only appear as part of interpolated strings/mlog blocks and are handled specially there
LPAREN                  : '(' ;
RPAREN                  : ')' ;
LBRACKET                : '[' ;
RBRACKET                : ']' ;

// Other operators
DECREMENT               : '--' ;
DIV                     : '/' ;
IDIV                    : '\\' ;
INCREMENT               : '++' ;
MINUS                   : '-' ;
MOD                     : '%' ;
EMOD                    : '%%' ;
MUL                     : '*' ;
PLUS                    : '+' ;
POW                     : '**' ;

// Assignments
ASSIGN                  : '=' ;
ASSIGN_BITWISE_AND      : '&=' ;
ASSIGN_BITWISE_OR       : '|=' ;
ASSIGN_BITWISE_XOR      : '^=' ;
ASSIGN_BOOLEAN_AND      : '&&=' ;
ASSIGN_BOOLEAN_OR       : '||=' ;
ASSIGN_DIV              : '/=' ;
ASSIGN_IDIV             : '\\=' ;
ASSIGN_MINUS            : '-=' ;
ASSIGN_MOD              : '%=' ;
ASSIGN_EMOD             : '%%=' ;
ASSIGN_MUL              : '*=' ;
ASSIGN_PLUS             : '+=' ;
ASSIGN_POW              : '**=' ;
ASSIGN_SHIFT_LEFT       : '<<=' ;
ASSIGN_SHIFT_RIGHT      : '>>=' ;
ASSIGN_USHIFT_RIGHT     : '>>>=' ;

// Symbols
AT                      : '@' ;
COLON                   : ':' ;
COMMA                   : ',' ;
DOLLAR                  : '$' ;
DOT                     : '.' ;
DOT2                    : '..' ;  
DOT3                    : '...' ;  
DOUBLEQUOTE             : '"' ;
QUESTION                : '?' ;
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

IDENTIFIER              : (AT AT)? Letter LetterOrDigit* ;
EXTIDENTIFIER           : DOLLAR Letter LetterOrDigit* ;
BUILTINIDENTIFIER       : AT Letter
                        | AT Letter LetterDigitDash* LetterOrDigit ;
KEYWORD                 : COLON Letter
                        | COLON Letter LetterDigitDash* LetterOrDigit ;

// Literals

STRING                  : '"' ~[\r\n"]* '"' ;
COLOR                   : '%'  HexDigit+ ;
NAMEDCOLOR              : '%[' Letter+ ']';
BINARY                  : '0b' BinDigit+ ;
HEXADECIMAL             : '0x' HexDigit+ ;
DECIMAL                 : DecDigit+ ;
FLOAT                   : DecDigit+ DecExponent
                        | DecDigit* DOT DecDigit+ DecExponent?
                        ;

// A single character. Converted to its ASCII value
CHAR                    : '\'' ~[\r\n'] '\''
                        | '\'\\\'\''
                        | '\'\\n\''
                        ;

// Mlog blocks
//LBRACE                  : {!inMlogBlock}? '{' {inMlogBlock = true; newLines=false;} -> pushMode(InMlogBlock);
LBRACE                  : '{' {inMlogBlock = true; newLines=false;} -> pushMode(InMlogBlock);

// Directives
HASHDECLARE             : '#declare';
HASHSET                 : '#set' -> pushMode(InDirective) ;
HASHSETLOCAL            : '#setlocal' -> pushMode(InDirective) ;

// Formattable literals
FORMATTABLELITERAL      : {!inFormat}? '$"' {inFormat = true;}  -> pushMode(InFormattable) ;
RBRACE                  : {inFormat}?  '}' -> popMode ;

// Commented line comment, to distinguish from Enhanced comment.
COMMENTEDCOMMENT        : '////' ~[\r\n]* -> skip ;

// Enhanced comments
ENHANCEDCOMMENT         : {!inFormat}? '///' {inFormat = true; newLines=false;} -> pushMode(InComment);

// Whitespace + comments
DOC_COMMENT             : '/**' .*? '*/'            -> channel(HIDDEN);
COMMENT                 : '/*' .*? '*/'             -> skip;
EMPTYCOMMENT            : '//' [\r\n]               -> skip;
LINECOMMENT             : '//' ~[/\r\n] ~[\r\n]*    -> skip;
NEWLINE                 : {newLines}? [\r\n]        -> skip;
WHITESPACE              : [ \t]+                    -> skip;

UNKNOWN_CHAR : . ;

// MODES

mode InMlogBlock;

//MLOGCLOSE               : {inMlogBlock}? '}' {inMlogBlock=false; newLines=true;} -> type(SEMICOLON), popMode ;
MLOGCLOSE               : '}' {inMlogBlock=false; newLines=true;} -> type(SEMICOLON), popMode ;

MLOGCOMMENT             : '#'  ~[\r\n]* ;
MLOGSILENTCOMMENT       : '//' ~[\r\n]* -> skip ;

fragment PlusOrMinus    : [+-] ;

// Literals - need to be defined again in mlog mode
// We also need the plus and minus signs explicitly here
MLOGSTRING              : '"' ~[\r\n"]* '"' ;
MLOGCOLOR               : '%' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit ( HexDigit HexDigit )? ;
MLOGNAMEDCOLOR          : '%[' Letter+ ']';
MLOGBINARY              : PlusOrMinus? '0b' BinDigit+ ;
MLOGHEXADECIMAL         : PlusOrMinus? '0x' HexDigit+ ;
MLOGDECIMAL             : PlusOrMinus? DecDigit+ ;
MLOGFLOAT               : PlusOrMinus? DecDigit+ DecExponent
                        | PlusOrMinus? DecDigit* DOT DecDigit+ DecExponent?
                        ;
MLOGCHAR                : '\'' ~[\r\n'] '\''
                        | '\'\\\'\''
                        | '\'\\n\''
                        ;
MLOGBUILTIN             : AT Letter
                        | AT Letter LetterDigitDash* LetterOrDigit ;

MLOGLABEL               : Letter LetterOrDigit* ':' ;
MLOGTOKEN               : ':' ~["#; \t\r\n] ~[#; \t\r\n]*
                        | ~["'#; \t\r\n]* ~["'#;} \t\r\n]
                        | ':"' ~[\r\n"]* '"'
                        ;

MLOGSEPARATOR           : [;\n\r]+ ;
MLOGWHITESPACE          : [ \t]+;

MLOG_UNKNOWN            : . ;

mode InDirective;

// Identifiers/values can start with numbers and contain the dash in Directive mode
DIRECTIVEVALUE          : [-a-zA-Z0-9_.]+
                        | '"' ~[\r\n"]* '"' ;
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
EMPTYPLACEHOLDER        : '${' ' '* '}' ;
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
COMMENTEMPTYPLACEHOLDER : '${' ' '* '}'  -> type(EMPTYPLACEHOLDER);
COMMENTINTERPOLATION    : '${'           -> type(INTERPOLATION), pushMode(DEFAULT_MODE);
COMMENTVARIABLEPHOLDER  : '$'            -> type(VARIABLEPLACEHOLDER), pushMode(InCommentIdentifier);
COMMENTENDOFLINE        : [\r\n] {inFormat=false; newLines=true;} -> type(SEMICOLON), popMode;

mode InFmtIdentifier;

VARIABLE                : Letter LetterOrDigit* ;
NEXTVARIABLE            : '$' -> type(VARIABLEPLACEHOLDER);
FMTENDOFLINE            : [\r\n] {inFormat = false;} -> popMode, popMode;      // Pop out of InFormattable on error
FMTCLOSINGDOUBLEQUOTE   : '"'    {inFormat = false;} -> type(DOUBLEQUOTE), popMode, popMode;
ENDOFIDENTIFIER         :  .  -> type(TEXT), popMode;

mode InCommentIdentifier;

// Map Enhanced comment lexer tokens to Formattable lexer tokens
INCMTVARIABLE           : Letter LetterOrDigit* -> type(VARIABLE);
INCMTNEXTVARIABLE       : '$' -> type(VARIABLEPLACEHOLDER);
INCMTENDOFLINE          : [\r\n] {inFormat=false; newLines=true;} -> type(SEMICOLON), popMode, popMode;
INCMTENDOFIDENTIFIER    : ~["] -> type(TEXT), popMode;    // Don't allow double quotes in enhanced comments at all
