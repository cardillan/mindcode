// This file contains the parser grammar for Mindcode language.

parser grammar MindcodeParser;

options {
    tokenVocab = 'MindcodeLexer';
}

module
    : statementList? EOF ;

// List of statements separated by semicolons
// Multiple consecutive semicolons are allowed, no expression need to be present.
statementList
    : (statement? SEMICOLON)+
    ;

// List of expressions separated by commas
// At least one expression needs to be present, multiple consecutive commas aren't allowed.
// Used just in a few places - for each loop and C-style iteration loop
expressionList
    : (expression COMMA)* expression
    ;

// A statement is an expression, which provides a value, or an executable statement, which is executable, but doesn't
// provide a value, or a declaration. Using a statement/declaration where an expression is expected is an error,
// recognized by the grammar.
//
// Note: not sure it is a good idea to separate statements and expressions in the grammar. Sometimes we could
// produce a better error message in the AST builder/code generator than we get from the grammar.
statement
    : expression                                                                        # expExpression
    | directive                                                                         # expDirective
    | REQUIRE library = IDENTIFIER                                                      # expRequireLibrary
    | REQUIRE file = STRING                                                             # expRequireFile
    | ALLOCATE allocations                                                              # expAllocations
    | inline = (INLINE | NOINLINE)? type = (VOID | DEF) name = IDENTIFIER
        params = parameterList body = statementList? END                                # expDeclareFunction
    | PARAM name = IDENTIFIER ASSIGN value = expression                                 # expParameter
    | CONST name = IDENTIFIER ASSIGN value = expression                                 # expConstant
    | BEGIN exp = statementList? END                                                    # expCodeBlock
    | (label = IDENTIFIER COLON)? FOR iterators = iteratorList IN
        values = expressionList DO body = statementList? END                            # expForEachLoop
    | (label = IDENTIFIER COLON)? FOR init = expressionList?
        SEMICOLON condition = expression? SEMICOLON update = expressionList?
        DO body = statementList? END                                                    # expForIteratedLoop
    | (label = IDENTIFIER COLON)? FOR control = IDENTIFIER IN range = rangeExpression
        DO body = statementList? END                                                    # expForRangeLoop
    | (label = IDENTIFIER COLON)?
        WHILE condition = expression DO body = statementList? END                       # expWhileLoop
    | (label = IDENTIFIER COLON)?
        DO body = statementList? loop = LOOP? WHILE condition = expression              # expDoWhileLoop
    | BREAK label = IDENTIFIER?                                                         # expBreak
    | CONTINUE label = IDENTIFIER?                                                      # expContinue
    | RETURN                                                                            # expReturn
    | RETURN value = expression                                                         # expReturn
    ;

// lvalue can be a target of an assignment - prefix/postfix increment/decrement and compound assignment.
// In this grammar, lvalue can always be read (and is therefore an rvalue and an expression too).
// For simple assignment, a generic expression can be a target, to support constructs like
// getlink(index).enabled = true
lvalue
    : id = IDENTIFIER                                                                   # expIdentifier
    | id = EXTIDENTIFIER                                                                # expIdentifierExt
    | builtin = BUILTINIDENTIFIER                                                       # expBuiltInIdentifier
    | array = IDENTIFIER LBRACKET index = expression RBRACKET                           # expArrayAccess
    ;

// The expresion rule is large. Unfortunately it can't be broken down to smaller rules for two reasons:
//
// 1. When a child rule start with an expression (e.g. the assignment rules), factoring it out to a standalone
//    rule makes expression and the new rule mutually recursice. ANTLR can't handle that.
// 2. When a child rule doesn't start with an expression, but contains one, factoring it out to a standalone
//    rule introduces ambiguities into the grammar. We wan't to prevent them if at all possible.
expression
    : END LPAREN RPAREN                                                                 # expCallEnd
    | function = IDENTIFIER args = argumentList                                         # expCallFunction
    | object = expression DOT function = IDENTIFIER args = argumentList                 # expCallMethod
    | object = expression DOT member = IDENTIFIER                                       # expMemberAccess
    | object = expression DOT property = BUILTINIDENTIFIER                              # expPropertyAccess
    | CASE exp = expression
        alternatives = caseAlternatives? (ELSE elseBranch = statementList)? END         # expCaseExpression
    | IF condition = expression THEN trueBranch = statementList?
        elsif = elsifBranches?
        (ELSE falseBranch = statementList?)? END                                        # expIfExpression
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
    | <assoc = right> target = expression
        operation = (ASSIGN | ASSIGN_POW |
                     ASSIGN_MUL | ASSIGN_DIV | ASSIGN_IDIV | ASSIGN_MOD |
                     ASSIGN_PLUS | ASSIGN_MINUS | ASSIGN_SHIFT_LEFT | ASSIGN_SHIFT_RIGHT |
                     ASSIGN_BITWISE_AND | ASSIGN_BITWISE_OR | ASSIGN_BITWISE_XOR |
                     ASSIGN_BOOLEAN_AND | ASSIGN_BOOLEAN_OR)
        value = expression                                                              # expCompoundAssignment
    | LPAREN exp = expression RPAREN                                                    # expParentheses
    ;

// Formattables

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

// Directives

directive
    : HASHSET option=directiveValue (DIRECTIVEASSIGN value = directiveValues)?          # stmtDirectiveSet
    ;

directiveValues
    : (directiveValue DIRECTIVECOMMA)* directiveValue
    ;

directiveValue
    : DIRECTIVEVALUE
    ;

// Allocations

allocations
    : (allocation COMMA)* allocation
    ;

allocation
    : HEAP IN id = IDENTIFIER (LBRACKET range = rangeExpression RBRACKET)?              # stmtHeapAllocation
    | STACK IN id = IDENTIFIER (LBRACKET range = rangeExpression RBRACKET)?             # stmtStackAllocation
    ;

// Function declarations

parameter
    : modifier_in = IN?  modifier_out = OUT? name = IDENTIFIER varargs = DOT3?
    | modifier_out = OUT modifier_in = IN    name = IDENTIFIER varargs = DOT3?
    ;

parameterList
    : LPAREN RPAREN
    | LPAREN (parameter COMMA)* parameter RPAREN
    ;

// Function calls

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

// Case expressions

caseAlternatives
    : caseAlternative+
    ;

caseAlternative
    : WHEN values = whenValueList THEN body = statementList?
    ;

whenValueList
    : (whenValue COMMA)* whenValue
    ;

whenValue
    : expression                                                                        # whenValueExpression
    | rangeExpression                                                                   # whenValueRangeExpression
    ;

rangeExpression
    : firstValue = expression operator = DOT2 lastValue = expression
    | firstValue = expression operator = DOT3 lastValue = expression
    ;

// If expressions

elsifBranches
    : elsifBranch+
    ;

elsifBranch
    : ELSIF condition = expression THEN body = statementList?
    ;

// Loops

iteratorList
    : (iterator COMMA)* iterator
    ;

iterator
    : modifier = OUT? variable = IDENTIFIER
    ;
