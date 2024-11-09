parser grammar MindcodeParser;

options {
    tokenVocab = 'MindcodeLexer';
}

program
    : expressionList? EOF ;

expressionList
    : ( expression SEMICOLON )+
    ;

expression
    : directive                                                                         # expDirective
    | IDENTIFIER                                                                        # expIdentifier
    | BUILTINIDENTIFIER                                                                 # expBuiltInIdentifier
    | ENHANCEDCOMMENT formattableContents*                                              # expEnhancedComment
    | FORMATTABLELITERAL formattableContents* DOUBLEQUOTE                               # expFormattableLiteral
    | STRING                                                                            # expStringLiteral
    | BINARY                                                                            # expBinaryLiteral
    | HEXADECIMAL                                                                       # expHexadecimalLiteral
    | DECIMAL                                                                           # expDecimalLiteral
    | FLOAT                                                                             # expFLoatLiteral
    | NULL                                                                              # expNullLiteral
    | TRUE                                                                              # expBooleanLiteralTrue
    | FALSE                                                                             # expBooleanLiteralFalse
    ;

directive
    : HASHSET option=directiveValue ( DIRECTIVEASSIGN value=directiveValues )?          # directiveSet
    ;

directiveValues
    : directiveValue ( DIRECTIVECOMMA directiveValue )*                                 # directiveValueList
    ;

directiveValue
    : DIRECTIVEVALUE
    ;

formattableContents
    : TEXT                                                                              # fmtText
    | ESCAPESEQUENCE                                                                    # fmtEscaped
    | INTERPOLATION expression RBRACE                                                   # fmtInterpolation
    | formattablePlaceholder                                                            # fmtPlaceholder
    ;

formattablePlaceholder
    : EMPTYPLACEHOLDER                                                                  # fmtPlaceholderEmpty
    | VARIABLEPLACEHOLDER ( id=VARIABLE )?                                              # fmtPlaceholderVariable
    ;
