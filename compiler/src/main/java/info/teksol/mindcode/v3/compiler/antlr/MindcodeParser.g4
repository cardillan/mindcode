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
    : directive                                                             # expDirective
    | Identifier                                                            # expIdentifier
    | EnhancedComment formattableContents*                                  # expEnhancedComment
    | FormattableLiteral formattableContents* DoubleQuote                   # expFormattableLiteral
    | StringLiteral                                                         # expStringLiteral
    ;

directive
    : HashSet directiveDeclaration
    ;

directiveDeclaration
    : option=DirectiveValue ( DirectiveAssign value=directiveValues )?
    ;

directiveValues
    : DirectiveValue ( DirectiveComma DirectiveValue )*
    ;

formattableContents
    : Text                                                                  # fmtText
    | EscapeSequence                                                        # fmtEscaped
    | Interpolation expression RBrace                                       # fmtInterpolation
    | formattablePlaceholder                                                # fmtPlaceholder
    ;

formattablePlaceholder
    : EmptyPlaceholder                                                      # fmtPlaceholderEmpty
    | VariablePlaceholder ( id=Variable )?                                  # fmtPlaceholderVariable
    ;
