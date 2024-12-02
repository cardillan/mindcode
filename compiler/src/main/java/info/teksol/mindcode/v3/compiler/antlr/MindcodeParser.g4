parser grammar MindcodeParser;

options {
    tokenVocab = 'MindcodeLexer';
}

program
    : expressionList? EOF ;

expressionList
    : (expression? SEMICOLON)+
    ;

// The expresion rule is very large. Unfortunately it can't be broken down to smaller rules for two reasons:
//
// 1. When a child rule start with an expression (e.g. the assignment rules), factoring it out to a standalone
//    rule makes expression and the new rule mutually recursice. ANTLR can't handle that.
// 2. When a child rule doesn't start with an expression, but contains one, factoring it out to a standalone
//    rule introduces ambiguities into the grammar. We wan't to prevent them if at all possible.
expression
    : directive                                                                         # expDirective
    | REQUIRE library = IDENTIFIER                                                      # expRequireLibrary
    | REQUIRE file = STRING                                                             # expRequireFile
    | BEGIN exp = expressionList? END                                                   # expCodeBlock
    | END LPAREN RPAREN                                                                 # expCallEnd
    | function = IDENTIFIER args = argumentList                                         # expCallFunction
    | object = expression DOT function = IDENTIFIER args = argumentList                 # expCallMethod
    | object = expression DOT member = IDENTIFIER                                       # expMemeberAccess
    | object = expression DOT property = BUILTINIDENTIFIER                              # expPropertyAccess
    | lvalue                                                                            # expLvalue
    | ENHANCEDCOMMENT formattableContents*                                              # expEnhancedComment
    | FORMATTABLELITERAL formattableContents* DOUBLEQUOTE                               # expFormattableLiteral
    | STRING                                                                            # expStringLiteral
    | BINARY                                                                            # expBinaryLiteral
    | HEXADECIMAL                                                                       # expHexadecimalLiteral
    | DECIMAL                                                                           # expDecimalLiteral
    | FLOAT                                                                             # expFloatLiteral
    | NULL                                                                              # expNullLiteral
    | TRUE                                                                              # expBooleanLiteralTrue
    | FALSE                                                                             # expBooleanLiteralFalse
    | (label = IDENTIFIER COLON)?
        WHILE condition = expression DO body = expressionList? END                      # expWhileLoop
    | (label = IDENTIFIER COLON)?
        DO body = expressionList? LOOP? WHILE condition = expression                    # expDoWhileLoop
    | exp = lvalue postfix = (INCREMENT | DECREMENT)                                    # expPostfix
    | prefix = (INCREMENT | DECREMENT) exp = lvalue                                     # expPrefix
    | op = (BITWISE_NOT | BOOLEAN_NOT | LOGICAL_NOT | PLUS | MINUS) exp = expression    # expUnary
    | left = expression op = POW right = expression                                     # expExponentiation
    | left = expression op = (MUL | DIV | IDIV | MOD) right = expression                # expMultiplication
    | left = expression op = (PLUS | MINUS) right = expression                          # expAddition
    | left = expression op = (SHIFT_LEFT | SHIFT_RIGHT) right = expression              # expBitShift
    | left = expression op = BITWISE_AND right=expression                               # expBitwiseAnd
    | left = expression op = (BITWISE_OR | BITWISE_XOR) right = expression              # expBitwiseOr
    | left = expression
        op = (LESS_THAN | LESS_THAN_EQUAL | GREATER_THAN_EQUAL | GREATER_THAN)
        right = expression                                                              # expInequalityRelation
    | left = expression
        op = (NOT_EQUAL | EQUAL | STRICT_NOT_EQUAL | STRICT_EQUAL)
        right = expression                                                              # expEqualityRelation
    | left = expression op = (BOOLEAN_AND | LOGICAL_AND) right = expression             # expLogicalAnd
    | left = expression op = (BOOLEAN_OR | LOGICAL_OR) right = expression               # expLogicalOr
    | <assoc = right> condition = expression
         QUESTION trueBranch = expression
         COLON falseBranch = expression                                                 # expTernary
    | <assoc = right> target = expression ASSIGN value = expression                     # expAssignment
    | <assoc = right> target = lvalue
        operation = (ASSIGN_POW |
                     ASSIGN_MUL | ASSIGN_DIV | ASSIGN_IDIV | ASSIGN_MOD |
                     ASSIGN_PLUS | ASSIGN_MINUS | ASSIGN_SHIFT_LEFT | ASSIGN_SHIFT_RIGHT |
                     ASSIGN_BITWISE_AND | ASSIGN_BITWISE_OR | ASSIGN_BITWISE_XOR |
                     ASSIGN_BOOLEAN_AND | ASSIGN_BOOLEAN_OR)
        value = expression                                                              # expCompoundAssignment
    | LPAREN expression RPAREN                                                          # expParentheses
    ;

// Directives

directive
    : HASHSET option=directiveValue (DIRECTIVEASSIGN value = directiveValues)?          # directiveSet
    ;

directiveValues
    : directiveValue (DIRECTIVECOMMA directiveValue)*                                   # directiveValueList
    ;

directiveValue
    : DIRECTIVEVALUE
    ;

// Function elements

argument
    : modifier_in = IN?  modifier_out = OUT? arg = expression
    | modifier_out = OUT modifier_in = IN    arg = expression
    ;

// Optional argument can match an empty string
// So far it doesn't seem to be a problem, and fixing it doesn't seem easy
optionalArgument
    : argument?
    ;

argumentList
    : LPAREN RPAREN
    | LPAREN argument RPAREN
    | LPAREN (optionalArgument COMMA)+ optionalArgument RPAREN
    ;

// Expression fragments

// In this grammar, lvalue can always be read (and is therefore an rvalue too)
lvalue
    : id = IDENTIFIER                                                                   # expIdentifier
    | id = EXTIDENTIFIER                                                                # expIdentifierExt
    | builtin = BUILTINIDENTIFIER                                                       # expBuiltInIdentifier
    | array = IDENTIFIER LBRACKET index = expression RBRACKET                           # expArrayAccess
    ;

formattableContents
    : TEXT                                                                              # fmtText
    | ESCAPESEQUENCE                                                                    # fmtEscaped
    | INTERPOLATION expression RBRACE                                                   # fmtInterpolation
    | formattablePlaceholder                                                            # fmtPlaceholder
    ;

formattablePlaceholder
    : EMPTYPLACEHOLDER                                                                  # fmtPlaceholderEmpty
    | VARIABLEPLACEHOLDER (id = VARIABLE)?                                              # fmtPlaceholderVariable
    ;
