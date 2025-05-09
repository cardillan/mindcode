// This file contains the parser grammar for Mindcode language.

parser grammar MindcodeParser;

options {
    tokenVocab = 'MindcodeLexer';
}

astModule
    : astStatementList? EOF ;

// List of statements separated by semicolons
// Multiple consecutive semicolons are allowed, no expression need to be present.
astStatementList
    : (statement? SEMICOLON)+
    ;

// List of expressions separated by commas
// At least one expression needs to be present, multiple consecutive commas aren't allowed.
// Used just in a few places - for each loop and C-style iteration loop
expressionList
    : (expression COMMA)* expression
    ;

// List of identifiers separated by commas
// At least one identifier needs to be present, multiple consecutive commas aren't allowed.
// Used by the require directive.
identifierList
    : (IDENTIFIER COMMA)* IDENTIFIER
    ;

// A statement is an expression, which provides a value, or an executable statement, which is executable, but doesn't
// provide a value, or a declaration. Using a statement/declaration where an expression is expected is an error,
// recognized by the grammar.
//
// Note: not sure it is a good idea to separate statements and expressions in the grammar. Sometimes we could
// produce a better error message in the AST builder/code generator than we get from the grammar.
statement
    : expression                                                                        # astExpression
    | directive                                                                         # astDirective
    | variableDeclaration                                                               # astVariableDeclaration
    | MODULE name = IDENTIFIER                                                          # astModuleDeclaration
    | ENHANCEDCOMMENT formattableContents*                                              # astEnhancedComment
    | ALLOCATE allocations                                                              # astAllocations
    | CONST name = IDENTIFIER ASSIGN value = expression                                 # astConstant
    | PARAM name = IDENTIFIER ASSIGN value = expression                                 # astParameter
    | REQUIRE file = STRING (REMOTE processors = identifierList)?                       # astRequireFile
    | REQUIRE library = IDENTIFIER (REMOTE processors = identifierList)?                # astRequireLibrary
    | callType = (INLINE | NOINLINE | REMOTE)? type = (VOID | DEF) name = IDENTIFIER
        params = parameterList body = astStatementList? END                             # astFunctionDeclaration
    | (label = IDENTIFIER COLON)? FOR iterators = iteratorsValuesGroups
        DO body = astStatementList? END                                                 # astForEachLoopStatement
    | (label = IDENTIFIER COLON)? FOR init = declarationOrExpressionList?
        SEMICOLON condition = expression? SEMICOLON update = expressionList?
        DO body = astStatementList? END                                                 # astIteratedForLoopStatement
    | (label = IDENTIFIER COLON)? FOR type = typeSpec? control = lvalue
        IN range = astRange DESCENDING? DO body = astStatementList? END                 # astRangedForLoopStatement
    | (label = IDENTIFIER COLON)?
        WHILE condition = expression DO body = astStatementList? END                    # astWhileLoopStatement
    | (label = IDENTIFIER COLON)?
        DO body = astStatementList? loop = LOOP? WHILE condition = expression           # astDoWhileLoopStatement
    | BREAK label = IDENTIFIER?                                                         # astBreakStatement
    | CONTINUE label = IDENTIFIER?                                                      # astContinueStatement
    | RETURN value = expression?                                                        # astReturnStatement
    | BEGIN exp = astStatementList? END                                                 # astCodeBlock
    ;

// For use with iterated for loops to unambiguosly distinguish between declaration and expression list
// Mixing both isn't allowed.
// We don't want modifiers here, but we'll filter them out in AST build/code generation phase.
declarationOrExpressionList
    : decl = variableDeclaration
    | expList = expressionList
    ;

// Universal variable declaration for all contexts
variableDeclaration
    : modifiers = declModifier* type = typeSpec variables = variableSpecList
    | modifiers = declModifier+ variables = variableSpecList
    ;

declModifier
    : modifier = CACHED
    | modifier = EXTERNAL (memory = IDENTIFIER)?
    | modifier = EXTERNAL memory = IDENTIFIER LBRACKET index = expression RBRACKET
    | modifier = EXTERNAL memory = IDENTIFIER LBRACKET range = astRange RBRACKET
    | modifier = LINKED
    | modifier = NOINIT
    | modifier = REMOTE
    | modifier = VOLATILE
    ;

// To be extended in the future with more types
typeSpec
    : VAR
    ;

variableSpecList
    : (variableSpecification COMMA)* variableSpecification
    ;

variableSpecification
    : id = (IDENTIFIER | EXTIDENTIFIER) (ASSIGN exp = expression)?
    | id = (IDENTIFIER | EXTIDENTIFIER) LBRACKET length = expression? RBRACKET (ASSIGN values = valueList)?
    ;

valueList
    : LPAREN values = expressionList RPAREN
    ;

// lvalue can be a target of an implicit assignment - prefix/postfix increment/decrement and loop iterator.
// In this grammar, lvalue can always be read (and is therefore an rvalue and an expression too).
// For assignments, a generic expression can be a target, to support constructs like
// getlink(index).enabled = true
lvalue
    : id = IDENTIFIER                                                                       # astIdentifier
    | id = EXTIDENTIFIER                                                                    # astIdentifierExt
    | builtin = BUILTINIDENTIFIER                                                           # astBuiltInIdentifier
    | array = (IDENTIFIER | EXTIDENTIFIER) LBRACKET index = expression RBRACKET             # astArrayAccess
    | array = (IDENTIFIER | EXTIDENTIFIER) LBRACKET range = astRange RBRACKET               # astSubarray
    | processor = IDENTIFIER DOT array = IDENTIFIER LBRACKET index = expression RBRACKET    # astRemoteArray
    | processor = IDENTIFIER DOT array = IDENTIFIER LBRACKET range = astRange RBRACKET      # astRemoteSubarray
    ;


// The expresion rule is large. Unfortunately it can't be broken down to smaller rules for two reasons:
//
// 1. When a child rule start with an expression (e.g. the assignment rules), factoring it out to a standalone
//    rule makes expression and the new rule mutually recursice. ANTLR can't handle that.
// 2. When a child rule doesn't start with an expression, but contains one, factoring it out to a standalone
//    rule introduces ambiguities into the grammar. We wan't to prevent them at all cost.
expression
    : lvalue                                                                            # expLvalue
    | KEYWORD                                                                           # astKeyword
    | END LPAREN RPAREN                                                                 # astFunctionCallEnd
    | function = IDENTIFIER args = argumentList                                         # astFunctionCall
    | object = expression DOT function = IDENTIFIER args = argumentList                 # astMethodCall
    | object = expression DOT member = IDENTIFIER                                       # astMemberAccess
    | object = expression DOT property = BUILTINIDENTIFIER                              # astPropertyAccess
    | CASE exp = expression
        alternatives = caseAlternatives? (ELSE elseBranch = astStatementList)? END      # astCaseExpression
    | IF condition = expression THEN trueBranch = astStatementList?
        elsif = elsifBranches?
        (ELSE falseBranch = astStatementList?)? END                                     # astIfExpression
    | FORMATTABLELITERAL formattableContents* DOUBLEQUOTE                               # astFormattableLiteral
    | STRING                                                                            # astLiteralString
    | COLOR                                                                             # astLiteralColor
    | NAMEDCOLOR                                                                        # astLiteralNamedColor
    | BINARY                                                                            # astLiteralBinary
    | HEXADECIMAL                                                                       # astLiteralHexadecimal
    | DECIMAL                                                                           # astLiteralDecimal
    | FLOAT                                                                             # astLiteralFloat
    | CHAR                                                                              # astLiteralChar
    | NULL                                                                              # astLiteralNull
    | value = (TRUE | FALSE)                                                            # astLiteralBoolean
    | exp = lvalue postfix = (INCREMENT | DECREMENT)                                    # astOperatorIncDecPostfix
    | prefix = (INCREMENT | DECREMENT) exp = lvalue                                     # astOperatorIncDecPrefix
    | op = (BITWISE_NOT | BOOLEAN_NOT | LOGICAL_NOT | PLUS | MINUS) exp = expression    # astOperatorUnary
    | left = expression op = POW right = expression                                     # astOperatorBinaryExp
    | left = expression op = (MUL | DIV | IDIV | MOD) right = expression                # astOperatorBinaryMul
    | left = expression op = (PLUS | MINUS) right = expression                          # astOperatorBinaryAdd
    | left = expression op = (SHIFT_LEFT | SHIFT_RIGHT) right = expression              # astOperatorBinaryShift
    | left = expression op = BITWISE_AND right=expression                               # astOperatorBinaryBitwiseAnd
    | left = expression op = (BITWISE_OR | BITWISE_XOR) right = expression              # astOperatorBinaryBitwiseOr
    | left = expression
        op = (LESS_THAN | LESS_THAN_EQUAL | GREATER_THAN_EQUAL | GREATER_THAN)
        right = expression                                                              # astOperatorBinaryInequality
    | left = expression
        op = (NOT_EQUAL | EQUAL | STRICT_NOT_EQUAL | STRICT_EQUAL)
        right = expression                                                              # astOperatorBinaryEquality
    | left = expression op = (BOOLEAN_AND | LOGICAL_AND) right = expression             # astOperatorBinaryLogicalAnd
    | left = expression op = (BOOLEAN_OR | LOGICAL_OR) right = expression               # astOperatorBinaryLogicalOr
//    | left = expression op = IN firstValue = expression
//        operator = (DOT2 | DOT3) lastValue = expression                                 # astOperatorBinaryInRange
    | <assoc = right> condition = expression
         QUESTION trueBranch = expression
         COLON falseBranch = expression                                                 # astOperatorTernary
    | <assoc = right> target = expression
        operation = (ASSIGN | ASSIGN_POW |
                     ASSIGN_MUL | ASSIGN_DIV | ASSIGN_IDIV | ASSIGN_MOD |
                     ASSIGN_PLUS | ASSIGN_MINUS | ASSIGN_SHIFT_LEFT | ASSIGN_SHIFT_RIGHT |
                     ASSIGN_BITWISE_AND | ASSIGN_BITWISE_OR | ASSIGN_BITWISE_XOR |
                     ASSIGN_BOOLEAN_AND | ASSIGN_BOOLEAN_OR)
        value = expression                                                              # astAssignment
    | LPAREN exp = expression RPAREN                                                    # astParentheses
    ;

// Formattables

formattableContents
    : TEXT                                                                              # formattableText
    | ESCAPESEQUENCE                                                                    # formattableEscaped
    | INTERPOLATION expression RBRACE                                                   # formattableInterpolation
    | formattablePlaceholder                                                            # placeholder
    ;

formattablePlaceholder
    : EMPTYPLACEHOLDER                                                                  # formattablePlaceholderEmpty
    | VARIABLEPLACEHOLDER (id = VARIABLE)?                                              # formattablePlaceholderVariable
    ;

// Directives

directive
    : HASHSET option=astDirectiveValue (DIRECTIVEASSIGN value = directiveValues)?       # astDirectiveSet
    ;

directiveValues
    : (astDirectiveValue DIRECTIVECOMMA)* astDirectiveValue
    ;

astDirectiveValue
    : DIRECTIVEVALUE
    ;

// Allocations

allocations
    : (astAllocation COMMA)* astAllocation
    ;

astAllocation
    : type = (HEAP | STACK) IN id = IDENTIFIER (LBRACKET range = astRange RBRACKET)?
    ;

// Function declarations

parameterList
    : LPAREN RPAREN
    | LPAREN (astFunctionParameter COMMA)* astFunctionParameter RPAREN
    ;

astFunctionParameter
    : modifier_in = IN?  modifier_out = OUT? name = IDENTIFIER varargs = DOT3?
    | modifier_out = OUT modifier_in = IN    name = IDENTIFIER varargs = DOT3?
    | modifier_ref = REF                     name = IDENTIFIER varargs = DOT3?
    ;

// Function calls

astFunctionArgument
    : modifier_in = IN?  modifier_out = OUT? arg = expression
    | modifier_out = OUT modifier_in = IN    arg = expression
    | modifier_ref = REF                     arg = expression
    ;

// Optional argument can match an empty string
// So far it doesn't seem to be a problem, and fixing it doesn't seem easy
astOptionalFunctionArgument
    : astFunctionArgument?
    ;

argumentList
    : LPAREN RPAREN
    | LPAREN astFunctionArgument RPAREN
    | LPAREN (astOptionalFunctionArgument COMMA)+ astOptionalFunctionArgument RPAREN
    ;

// Case expressions

caseAlternatives
    : astCaseAlternative+
    ;

astCaseAlternative
    : WHEN values = whenValueList THEN body = astStatementList?
    ;

whenValueList
    : (whenValue COMMA)* whenValue
    ;

whenValue
    : expression
    | astRange
    ;

astRange
    : firstValue = expression operator = DOT2 lastValue = expression
    | firstValue = expression operator = DOT3 lastValue = expression
    ;

// If expressions

elsifBranches
    : elsifBranch+
    ;

elsifBranch
    : ELSIF condition = expression THEN body = astStatementList?
    ;

// Loops

iteratorsValuesGroups
    : (astIteratorsValuesGroup SEMICOLON)* astIteratorsValuesGroup
    ;

astIteratorsValuesGroup
    : iteratorGroup IN expressionList DESCENDING?
    ;

iteratorGroup
    : type = typeSpec? (astIterator COMMA)* astIterator
    ;

astIterator
    : modifier = OUT? variable = lvalue
    ;
