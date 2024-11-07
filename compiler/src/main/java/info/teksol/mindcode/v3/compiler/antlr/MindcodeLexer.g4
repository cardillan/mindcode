lexer grammar MindcodeLexer;

@members {
    boolean newLines = true;
    boolean inFormat = false;
}

Identifier: [a-zA-Z_][a-zA-Z0-9_]* ;

StringLiteral           : '"' ~[\r\n"]* '"' ;


// Characters

Assign                  : '=' ;
Comma                   : ',' ;
Dot                     : '.' ;
DoubleDot               : '..' ;
TripleDot               : '...' ;
DoubleQuote             : '"' ;
Semicolon               : ';' ;

// Numeric literal fragments

// fragments
fragment BinDigits      : ( '0'..'1' )+ ;
fragment HexDigits      : ( '0'..'9' | 'a'..'f' | 'A'..'F' )+ ;
fragment DecDigits      : ( '0'..'9' )+ ;
fragment DecExponent    : ( 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' ) DecDigits ;

Binary                  : '0b' BinDigits+ ;
Hexadecimal             : '0x' HexDigits ;
Decimal                 : DecDigits ;
Float                   : DecDigits DecExponent
                        | DecDigits Dot DecDigits DecExponent?
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
CommentEscapeSequence   : '\\' ~[\r\n"]     -> type(EscapeSequence);
CommentEmptyPlaceholder : '${'   ' '*  '}'  -> type(EmptyPlaceholder);
CommentInterpolation    : '${'              -> type(Interpolation), pushMode(DEFAULT_MODE);
CommentVariablePHolder  : '$'               -> type(VariablePlaceholder), pushMode(InCommentIdentifier);
CommentEndOfLine        : [\r\n] {inFormat=false; newLines=true;} -> type(Semicolon), popMode;

mode InFmtIdentifier;

Variable                : [a-zA-Z_][a-zA-Z0-9_]* ;
FmtEndOfLine            : [\r\n] {inFormat = false;} -> popMode, popMode;      // Pop out of InFormattable on error
FmtClosingDoubleQuote   : '"'    {inFormat = false;} -> type(DoubleQuote), popMode, popMode;
EndOfIdentifier         :  .  -> type(Text), popMode;

mode InCommentIdentifier;

InCmtVariable           : [a-zA-Z_][a-zA-Z0-9_]* -> type(Variable);
InCmtEndOfLine          : [\r\n] {inFormat=false; newLines=true;} -> popMode, popMode;   // Pop out of InFormattable on error
InCmtEndOfIdentifier    : ~["] -> type(Text), popMode;
