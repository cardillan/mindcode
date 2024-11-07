lexer grammar MindcodeLexer;

@members {
    boolean newLines = true;
    boolean inFormat = false;
}

// fragments
fragment BinDigit       : ( '0'..'1' ) ;
fragment HexDigit       : ( '0'..'9' | 'a'..'f' | 'A'..'F' ) ;
fragment DecDigit       : ( '0'..'9' ) ;
fragment DecExponent    : ( 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' ) DecDigit+ ;

fragment IdentifierBeg  : [a-zA-Z_] ;
fragment IdentifierMid  : [a-zA-Z0-9_] ;

fragment MindustryBeg  : [a-zA-Z_] ;
fragment MindustryMid  : [-a-zA-Z0-9_] ;
fragment MindustryEnd  : [a-zA-Z0-9_] ;

// Characters

Assign                  : '=' ;
At                      : '@' ;
Comma                   : ',' ;
Dot                     : '.' ;
DoubleDot               : '..' ;
TripleDot               : '...' ;
DoubleQuote             : '"' ;
Semicolon               : ';' ;

// Identifiers

Identifier              : IdentifierBeg IdentifierMid* ;
MindustryIdentifier     : At MindustryBeg
                        | At MindustryBeg MindustryMid* MindustryEnd ;

// Literals

String                  : '"' ~[\r\n"]* '"' ;
Binary                  : '0b' BinDigit+ ;
Hexadecimal             : '0x' HexDigit+ ;
Decimal                 : DecDigit+ ;
Float                   : DecDigit+ DecExponent
                        | DecDigit* Dot DecDigit+ DecExponent?
                        ;

// MODES

// Directives
HashSet                 : '#set' -> pushMode(InDirective) ;

// Formattable literals
FormattableLiteral      : {!inFormat}? '$"' {inFormat = true;}  -> pushMode(InFormattable) ;
RBrace                  : {inFormat}?  '}'  {inFormat = false;} -> popMode ;

// Commented line comment, to distinguish from Enhanced comment.
CommentedComment        : '////' ~[\r\n]* -> skip ;

// Enhanced comments
EnhancedComment         : {!inFormat}? '///' {inFormat = true; newLines=false;} -> pushMode(InComment);

// Whitespace + comments
Comment                 : '/*' .*? '*/'      -> skip;
EmptyComment            : '//' [\r\n]        -> skip;
LineComment             : '//' ~'/' ~[\r\n]* -> skip;
NewLine                 : {newLines}? [\r\n] -> skip;
WhiteSpace              : [ \t]+             -> skip;

mode InDirective;

DirectiveValue          : [-a-zA-Z0-9_]+ ;
DirectiveAssign         : '=' ;
DirectiveComma          : ',' ;

DirectiveSemicolon      : ';' -> type(Semicolon), popMode;

DirectiveComment        : '/*' .*? '*/' -> skip;
DirectiveLineComment    : '//' ~[\r\n]* -> skip;
DirectiveWhiteSpace     : [ \t\r\n]+    -> skip;

mode InFormattable;

Text                    : ~[\r\n\\$"]+ ;

// We would want to allow escaping only '\' and '$', but can't get it to work. Escapes will be universal and handled later.
EscapeSequence          : '\\' ~[\r\n"] ;
EmptyPlaceholder        : '${'   ' '*  '}' ;
Interpolation           : '${' -> pushMode(DEFAULT_MODE) ;
VariablePlaceholder     : '$'  -> pushMode(InFmtIdentifier);
ClosingDoubleQuote      : '"' {inFormat = false;} -> type(DoubleQuote), popMode;
EndOfLine               : [\r\n] -> popMode;                    // Pop out of InFormattable on error

mode InComment;

// Map Enhanced comment lexer tokens to Formattable lexer tokens
// Refuse double quotes
CommentText             : ~[\r\n\\$"]+      -> type(Text);

// We would want to allow escaping only '\' and '$', but can't get it to work. Escapes will be universal and handled later.
CommentEscapeSequence   : '\\' ~[\r\n"]     -> type(EscapeSequence);

// We don't want empty placeholders in enhanced comments, but the check will be done later
CommentEmptyPlaceholder : '${'   ' '*  '}'  -> type(EmptyPlaceholder);
CommentInterpolation    : '${'              -> type(Interpolation), pushMode(DEFAULT_MODE);
CommentVariablePHolder  : '$'               -> type(VariablePlaceholder), pushMode(InCommentIdentifier);
CommentEndOfLine        : [\r\n] {inFormat=false; newLines=true;} -> type(Semicolon), popMode;

mode InFmtIdentifier;

Variable                : IdentifierBeg IdentifierMid* ;
FmtEndOfLine            : [\r\n] {inFormat = false;} -> popMode, popMode;      // Pop out of InFormattable on error
FmtClosingDoubleQuote   : '"'    {inFormat = false;} -> type(DoubleQuote), popMode, popMode;
EndOfIdentifier         :  .  -> type(Text), popMode;

mode InCommentIdentifier;

// Map Enhanced comment lexer tokens to Formattable lexer tokens
InCmtVariable           : IdentifierBeg IdentifierMid* -> type(Variable);
InCmtEndOfLine          : [\r\n] {inFormat=false; newLines=true;} -> popMode, popMode;
InCmtEndOfIdentifier    : ~["] -> type(Text), popMode;    // Don't allow double quotes in enhanced comments at all
