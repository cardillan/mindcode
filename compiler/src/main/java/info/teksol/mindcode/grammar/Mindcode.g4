grammar Mindcode;

@parser::members {
// This variable will control if semicolons are mandatory or optional
public boolean strictSyntax = true;
}

program : expression_list? EOF;

expression_list : expression_or_rem_comment
                | expression_list expression_or_rem_comment
                ;

// The REM_COMMENT isn't followed by a semicolon, needs to be handled separately
expression_or_rem_comment
    : exp_strict_or_relaxed             # single_exp
    | REM_COMMENT                       # rem_comment
    ;

exp_strict_or_relaxed
    : {strictSyntax}? expression_strict
    | {strictSyntax == false}? expression_relaxed
    ;

expression_strict
    : expression SEMICOLON
    | expression { notifyErrorListeners(_input.LT(-1), "missing ';'", new MissingSemicolonException(_input.LT(1))); }
    ;

expression_relaxed : expression SEMICOLON? ;

optional_do
    : {strictSyntax}? DO
    | {strictSyntax == false}? DO?
    ;

optional_then
    : {strictSyntax}? THEN
    | {strictSyntax == false}? THEN?
    ;

expression : directive                                                                          # compiler_directive
           | MINUS numeric_t                                                                    # literal_minus
           | indirectpropaccess                                                                 # indirect_prop_access
           | case_expr                                                                          # case_expression
           | if_expr                                                                            # if_expression
           | funcall                                                                            # function_call
           | propaccess                                                                         # property_access
           | fundecl                                                                            # function_declaration
           | alloc                                                                              # allocation
           | lvalue                                                                             # value
           | while_expression                                                                   # while_loop
           | do_while_expression                                                                # do_while_loop
           | for_expression                                                                     # for_loop
           | NOT expression                                                                     # not_expr
           | BITWISE_NOT expression                                                             # bitwise_not_expr
           | left=expression op=EXP right=expression                                            # binop_exp
           | MINUS expression                                                                   # unary_minus
           | left=expression op=( MUL | DIV | IDIV | MOD ) right=expression                     # binop_mul_div_mod
           | left=expression op=( PLUS | MINUS ) right=expression                               # binop_plus_minus
           | left=expression op=( SHIFT_LEFT | SHIFT_RIGHT ) right=expression                   # binop_shift
           | left=expression BITWISE_AND right=expression                                       # binop_bitwise_and
           | left=expression op=( BITWISE_OR | BITWISE_XOR ) right=expression                   # binop_bitwise_or
           | left=expression op=( LESS_THAN | LESS_THAN_EQUAL
                                | GREATER_THAN_EQUAL | GREATER_THAN )
                                right=expression                                                # binop_inequality_comparison
           | left=expression op=( NOT_EQUAL | EQUAL 
                                | STRICT_NOT_EQUAL | STRICT_EQUAL ) right=expression            # binop_equality_comparison
           | left=expression op=AND right=expression                                            # binop_and
           | left=expression op=OR right=expression                                             # binop_or
           | <assoc=right> cond=expression QUESTION_MARK
                                (true_branch=expression COLON false_branch=expression)          # ternary_op
           | assign                                                                             # assignment
           | const_decl                                                                         # constant
           | param_decl                                                                         # parameter
           | literal_t                                                                          # literal_string
           | formattable_t                                                                      # literal_formattable
           | numeric_t                                                                          # literal_numeric
           | bool_t                                                                             # literal_bool
           | null_t                                                                             # literal_null
           | LEFT_RBRACKET expression RIGHT_RBRACKET                                            # parenthesized_expression
           | break_st                                                                           # break_exp
           | continue_st                                                                        # continue_exp
           | return_st                                                                          # return_exp
           ;

directive : HASHSET option=ID ASSIGN value=INT                      # numeric_directive
          | HASHSET option=ID ASSIGN value=ID                       # string_directive
          | HASHSET option=ID ( ASSIGN values=directive_list )?     # list_directive
          | HASHSTRICT   {strictSyntax = true;}                     # strict_directive
          | HASHRELAXED  {strictSyntax = false;}                    # relaxed_directive
          ;

directive_list
    : id ( COMMA id )*
    ;

indirectpropaccess : target=var_ref DOT SENSOR LEFT_RBRACKET expr=expression RIGHT_RBRACKET;

methodaccess
    : var_ref DOT prop=id
    | unit_ref DOT prop=id
    ;

propaccess
    : var_ref DOT prop=laccess
    | unit_ref DOT prop=laccess
    ;

laccess
    : unit_ref      # laccess_strict
    | var_ref       # laccess_relaxed
    ;

numeric_t : float_t
          | int_t
          ;

alloc : ALLOCATE alloc_list;

alloc_list : type=(HEAP | STACK) IN id alloc_range?
           | alloc_list COMMA type=(HEAP | STACK) IN id alloc_range?
           ;

alloc_range : LEFT_SBRACKET range_expression RIGHT_SBRACKET;

const_decl : CONST name=id ASSIGN value=expression;

param_decl : PARAM name=id ASSIGN value=expression;

fundecl : (inline=(INLINE|NOINLINE))? def=(VOID|DEF) name=id LEFT_RBRACKET args=arg_decl_list RIGHT_RBRACKET body=expression_list END
        | (inline=(INLINE|NOINLINE))? def=(VOID|DEF) name=id LEFT_RBRACKET RIGHT_RBRACKET body=expression_list END
        | {strictSyntax == false}? (inline=(INLINE|NOINLINE))? def=(VOID|DEF) name=id body=expression_list END
        ;

arg_decl
    : ( modifier_in = IN )? ( modifier_out = OUT )? name = var_ref ( ellipsis = DOT DOT DOT )?
    |   modifier_out = OUT    modifier_in = IN      name = var_ref ( ellipsis = DOT DOT DOT )?
    ;

arg_decl_list
    : arg_decl ( COMMA arg_decl )*
    ;

while_expression : ( label=loop_label COLON )? WHILE cond=expression optional_do loop_body END;

do_while_expression : ( label=loop_label COLON )? DO loop_body LOOP WHILE cond=expression;

for_expression : ( label=loop_label COLON )? FOR lvalue IN range_expression optional_do loop_body END   # ranged_for
               | ( label=loop_label COLON )? FOR init=init_list SEMICOLON cond=expression
                            SEMICOLON increment=incr_list optional_do loop_body END                     # iterated_for
               | ( label=loop_label COLON )? FOR iterators=iterator_list IN
                            LEFT_RBRACKET values=value_list RIGHT_RBRACKET optional_do loop_body END    # for_each_1
               | ( label=loop_label COLON )? FOR iterators=iterator_list IN
                            values=value_list DO loop_body END                                          # for_each_2
               ;

iterator
    : ( modifier = OUT )? lvalue
    ;

iterator_list
    : iterator ( COMMA iterator )*
    ;

value_list
    : expression ( COMMA expression )*
    ;

loop_body : expression_list ;

continue_st : CONTINUE ( label=loop_label )? ;

break_st : BREAK ( label=loop_label )? ;

return_st : RETURN ( retval=expression )? ;

range_expression : start=expression DOT DOT end=expression     # inclusive_range_exp
                 | start=expression DOT DOT DOT end=expression # exclusive_range_exp
                 ;

init_list : expression
          | init_list COMMA expression
          ;

incr_list : expression
          | incr_list COMMA expression
          ;

funcall : END LEFT_RBRACKET RIGHT_RBRACKET
        | name=id LEFT_RBRACKET RIGHT_RBRACKET
        | name=id LEFT_RBRACKET params=arg_list RIGHT_RBRACKET
        | obj=methodaccess LEFT_RBRACKET params=arg_list RIGHT_RBRACKET
        ;

arg
    : ( modifier_in = IN )? ( modifier_out = OUT )? ( argument = expression )?
    |   modifier_out = OUT    modifier_in = IN        argument = expression
    ;

arg_list
    : arg ( COMMA arg )*
    ;

if_expr : IF cond=expression optional_then true_branch=expression_list? if_trailer? END;

if_trailer : ELSE false_branch=expression_list?
           | ELSIF cond=expression optional_then true_branch=expression_list? if_trailer?
           ;

case_expr : CASE cond=expression alternative_list? ( ELSE else_branch=expression_list )? END;

alternative_list : alternative
                 | alternative_list alternative
                 ;

alternative : WHEN values=when_value_list optional_then body=expression_list?;

when_expression : expression
                | range_expression
                ;

when_value_list : when_expression
                | when_value_list COMMA when_expression
                ;

assign : <assoc=right> target=lvalue ASSIGN value=expression                             # simple_assign
       | <assoc=right> target=lvalue op=( EXP_ASSIGN | 
                                          MUL_ASSIGN | DIV_ASSIGN | IDIV_ASSIGN | MOD_ASSIGN |
                                          PLUS_ASSIGN | MINUS_ASSIGN |
                                          SHIFT_LEFT_ASSIGN | SHIFT_RIGHT_ASSIGN |
                                          BITWISE_AND_ASSIGN | BITWISE_OR_ASSIGN | BITWISE_XOR_ASSIGN |
                                          AND_ASSIGN | OR_ASSIGN ) value=expression      # compound_assign
       ;

lvalue : unit_ref
       | global_ref
       | heap_ref
       | var_ref
       | methodaccess
       ;

loop_label : ID;

heap_ref : name=id LEFT_SBRACKET address=expression RIGHT_SBRACKET;
global_ref : DOLLAR name=id;
unit_ref : AT ref;
var_ref : id;

ref : ID;
int_t : decimal_int | hex_int | binary_int;
float_t : FLOAT;
literal_t : LITERAL;
formattable_t : FORMATTABLE;
null_t : NULL;
bool_t : true_t  # true_bool_literal
       | false_t # false_bool_literal
       ;
true_t : TRUE;
false_t : FALSE;
id : ID;

decimal_int : INT;
hex_int : HEXINT;
binary_int : BININT;

ALLOCATE : 'allocate';
BEGIN : 'begin';
BREAK : 'break';
CASE : 'case';
CONST : 'const';
CONTINUE : 'continue';
DEF : 'def';
DO : 'do';
ELIF : 'elif';
ELSE : 'else';
ELSEIF : 'elseif';
ELSIF : 'elsif';
END : 'end';
FALSE : 'false';
FOR : 'for';
HEAP : 'heap';
IF : 'if';
IN : 'in';
INLINE : 'inline';
LOOP : 'loop';
NOINLINE : 'noinline';
NULL : 'null';
OUT : 'out';
PARAM : 'param';
RETURN : 'return';
SENSOR : 'sensor';
STACK : 'stack';
THEN : 'then';
TRUE : 'true';
VAR : 'var';
VOID : 'void';
WHEN : 'when';
WHILE : 'while';

ASSIGN : '=';
AT : '@';
COLON : ':';
COMMA : ',';
DIV : '/';
IDIV : '\\';
DOLLAR : '$';
DOT : '.';
EXP : '**';
MINUS : '-';
MOD : '%';
MUL : '*';
NOT : '!' | 'not';
BITWISE_NOT : '~';
PLUS : '+';
QUESTION_MARK : '?';
SEMICOLON : ';';
HASHSET : '#set';
HASHSTRICT : '#strict';
HASHRELAXED : '#relaxed';

EXP_ASSIGN : '**=';
MUL_ASSIGN : '*=';
DIV_ASSIGN : '/=';
IDIV_ASSIGN : '\\=';
MOD_ASSIGN : '%=';
PLUS_ASSIGN : '+=';
MINUS_ASSIGN : '-=';

SHIFT_LEFT_ASSIGN : '<<=';
SHIFT_RIGHT_ASSIGN : '>>=';
BITWISE_AND_ASSIGN : '&=';
BITWISE_OR_ASSIGN : '|=';
BITWISE_XOR_ASSIGN : '^=';
AND_ASSIGN : '&&=';
OR_ASSIGN : '||=';

LESS_THAN : '<';
LESS_THAN_EQUAL : '<=';
NOT_EQUAL  : '!=';
EQUAL  : '==';
STRICT_EQUAL  : '===';
STRICT_NOT_EQUAL  : '!==';
GREATER_THAN_EQUAL  : '>=';
GREATER_THAN : '>';
AND : '&&' | 'and';
OR : '||' | 'or';

SHIFT_LEFT : '<<';
SHIFT_RIGHT : '>>';
BITWISE_AND : '&';
BITWISE_OR : '|';
BITWISE_XOR : '^';

LEFT_SBRACKET : '[';
RIGHT_SBRACKET : ']';
LEFT_RBRACKET : '(';
RIGHT_RBRACKET : ')';
LEFT_CBRACKET : '{';
RIGHT_CBRACKET : '}';

FORMATTABLE : '$"' ~[\n\r"]* '"' ;

fragment ESCAPED_QUOTE : '\\"';
LITERAL : '"' ( ESCAPED_QUOTE | ~[\n\r"] )*? '"';

// fragments
fragment DecimalExponent : ( 'e' | 'E' | 'e+' | 'E+' | 'e-' | 'E-' ) DecimalDigits;
fragment DecimalDigits   : ('0'..'9')+ ;
fragment FloatLiteral    : Float ;
fragment IntegerLiteral  : Integer ;
fragment Integer         : Decimal | Binary | Hexadecimal ;
fragment Decimal         : DecimalDigits ;
fragment Binary          : '0b' ('0' | '1')+ ;
fragment Hexadecimal     : '0x' HexDigit+ ;
fragment DecimalDigit    : '0'..'9' ;
fragment HexDigit        : ('0'..'9'|'a'..'f'|'A'..'F') ;
fragment Float           : DecimalDigits DecimalExponent
                         | DecimalDigits DOT DecimalDigits DecimalExponent?
                         ;

FLOAT : Float;
INT : Decimal;
HEXINT : Hexadecimal;
BININT : Binary;

ID : [_a-zA-Z][-a-zA-Z_0-9]*;

REM_COMMENT: '///' ~[/\r\n] ~[\r\n]*;

COMMENT: '/*' .*? '*/' -> skip;
SL_COMMENT : '//' ~[\r\n]* -> skip;
WS : (' ' | '\t' | '\r' | '\n')+ -> skip;
