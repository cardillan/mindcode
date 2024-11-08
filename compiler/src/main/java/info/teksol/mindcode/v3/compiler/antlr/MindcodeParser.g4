parser grammar MindcodeParser;

options {
    tokenVocab = 'MindcodeLexer';
}

program
    : expressionList? EOF ;

expressionList
    : ( expression Semicolon )+
    ;

expression
    : directive                                                                         # expDirective
    | Identifier                                                                        # expIdentifier
    | BuiltInIdentifier                                                                 # expBuiltInIdentifier
    | EnhancedComment formattableContents*                                              # expEnhancedComment
    | FormattableLiteral formattableContents* DoubleQuote                               # expFormattableLiteral
    | String                                                                            # expStringLiteral
    | Binary                                                                            # expBinaryLiteral
    | Hexadecimal                                                                       # expHexadecimalLiteral
    | Decimal                                                                           # expDecimalLiteral
    | Float                                                                             # expFLoatLiteral
    ;

directive
    : HashSet option=directiveValue ( DirectiveAssign value=directiveValues )?          # directiveSet
    ;

directiveValues
    : directiveValue ( DirectiveComma directiveValue )*                                 # directiveValueList
    ;

directiveValue
    : DirectiveValue
    ;

formattableContents
    : Text                                                                              # fmtText
    | EscapeSequence                                                                    # fmtEscaped
    | Interpolation expression RBrace                                                   # fmtInterpolation
    | formattablePlaceholder                                                            # fmtPlaceholder
    ;

formattablePlaceholder
    : EmptyPlaceholder                                                                  # fmtPlaceholderEmpty
    | VariablePlaceholder ( id=Variable )?                                              # fmtPlaceholderVariable
    ;
